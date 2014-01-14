package co.yahoraque.www.actividades;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import co.ensalsaverde.apps.yahoraque.R;
import co.yahoraque.www.JSONParser;
import co.yahoraque.www.NetworkUtil;

public class Feedback extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Variables
	JSONParser jsonParser = new JSONParser();
	EditText edfeedback;
	TextView longitudFeedback;

	// url to create new recommendation
	private static String url_create_product = "http://ensalsaverdeco.domain.com/yahoraquefeedback/create_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	public static String correo = null;

	// Shared Preferences
	private final String MAIL = "correo";

	// SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
	// MODE_WORLD_READABLE);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);

		// ACTION BAR SHERLOCK
		// getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		// getSupportActionBar().setHomeButtonEnabled(false);
		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		// getSupportActionBar().setDisplayShowTitleEnabled(false);

		edfeedback = (EditText) findViewById(R.id.editfeedback);
		longitudFeedback = (TextView) findViewById(R.id.longitudFeedback);

		// Para detectar el numero de caracteres en edtitulo y eddescripcion
		edfeedback.addTextChangedListener(FeedbackTextEditorWatcher);

		final Button sugerir = (Button) findViewById(R.id.botonenviar);

		// Al pulsar el boton de Sugerir (enviar feedback)
		sugerir.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					
					//Checar que no esté vacío
					String ContenidoFeedback = edfeedback.getText().toString();
					
					if(ContenidoFeedback.matches("")){
						Toast.makeText(Feedback.this, "Falta información por llenar :0", Toast.LENGTH_SHORT).show();					    
					}
					
					else {
						
						//Checar conexión a internet
						int status = NetworkUtil.getConnectivityStatus(getBaseContext());
						
						if(status == 0){
							Toast.makeText(Feedback.this,
									"Error de conexión (por culpa de los aliens)",
									Toast.LENGTH_LONG).show();
						}
						
						else{
						
					//Enviar Feedback
					new CreateNewProduct().execute();
					//Iniciar Actividad de Recomendaciones
					Intent i = new Intent(getApplicationContext(),
							Recomendaciones.class);
					startActivity(i);
					Toast.makeText(
							Feedback.this,
							"¡Muchas gracias! :') "
									+ "P.D. No olvides compartir la App con tus amigos.",
							Toast.LENGTH_LONG).show();
						}
					}

					return true;
				} else
					return false;
			}
		});
	}

	/*
	 * ActionBar Sherlock Buttons
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getSupportMenuInflater().inflate(R.menu.soloenviar, menu); return true; }
	 * 
	 * // OnClick Botones del ActionBar public boolean
	 * onOptionsItemSelected(MenuItem item) {
	 * 
	 * switch (item.getItemId()) { // Enviar case R.id.soloenviar:
	 * 
	 * new CreateNewProduct().execute(); Intent i = new
	 * Intent(getApplicationContext(), Recomendaciones.class); startActivity(i);
	 * Toast.makeText(Recomendar.this, "¡Gracias, se ha enviado tu sugerencia!",
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * default: return super.onOptionsItemSelected(item);
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * Background Async Task to Create new recommendation
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Feedback.this);
			pDialog.setMessage("Teletransportando retroalimentación...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String feedback = edfeedback.getText().toString();
			loadPrefs();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("feedback", feedback));
			params.add(new BasicNameValuePair("correo", correo));
			// params.add(new BasicNameValuePair("id_usuarios", id_usuarios));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					pDialog.dismiss();
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();

		}

	}

	// Get Data from SharedPreferences
	public void loadPrefs() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		correo = sp.getString(MAIL, null);

	}

	// Detectar el numero de caracteres en los TextEdit:

	// Feedback Watcher
	private final TextWatcher FeedbackTextEditorWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// This sets a textview to the current length
			longitudFeedback.setText("" + String.valueOf(s.length()) + "/300");
		}

		public void afterTextChanged(Editable s) {
		}
	};

}
