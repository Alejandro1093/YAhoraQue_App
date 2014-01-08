package co.yahoraque.www;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import co.ensalsaverde.apps.yahoraque.R;

public class Recomendar extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Variables
	JSONParser jsonParser = new JSONParser();
	EditText edtitulo, eddescripcion;
	TextView longitudTitulo, longitudDescripcion;
	String dianoche, sologrupo, interiorexterior;
	boolean bdianoche, bsologrupo, binteriorexterior = false;

	// url to create new recommendation
	private static String url_create_product = "http://ensalsaverdeco.domain.com/yahoraquesugerencias/create_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	public static String fb_id = null;

	// Shared Preferences
	private final String USER_NAME = "username";
	private final String FB_ID = "fb_id";

	// SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
	// MODE_WORLD_READABLE);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomendar);

		/* ACTION BAR SHERLOCK
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);*/

		edtitulo = (EditText) findViewById(R.id.titulosugerencia);
		eddescripcion = (EditText) findViewById(R.id.descripcionsugerencia);
		longitudTitulo = (TextView) findViewById(R.id.longitudTitulo);
		longitudDescripcion = (TextView) findViewById(R.id.longitudDescripcion);

		// Para detectar el numero de caracteres en edtitulo y eddescripcion
		edtitulo.addTextChangedListener(TituloTextEditorWatcher);
		eddescripcion.addTextChangedListener(DescripcionTextEditorWatcher);

		final Button sugerir = (Button) findViewById(R.id.botonenviar);
		final Button dia = (Button) findViewById(R.id.botondia);
		final Button noche = (Button) findViewById(R.id.botonnoche);
		final Button solo = (Button) findViewById(R.id.botonsolo);
		final Button grupo = (Button) findViewById(R.id.botongrupo);
		final Button interior = (Button) findViewById(R.id.botoninterior2);
		final Button exterior = (Button) findViewById(R.id.botonexterior2);


		// Al pulsar el boton Dia
		dia.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					dia.setClickable(false);
					dia.setPressed(true);
					noche.setClickable(true);
					noche.setPressed(false);
					dianoche = "dia";
					bdianoche = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton Noche
		noche.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					dia.setClickable(true);
					dia.setPressed(false);
					noche.setClickable(false);
					noche.setPressed(true);
					dianoche = "noche";
					bdianoche = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton Solo
		solo.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					solo.setClickable(false);
					solo.setPressed(true);
					grupo.setClickable(true);
					grupo.setPressed(false);
					sologrupo = "solo";
					bsologrupo = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton Grupo
		grupo.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					solo.setClickable(true);
					solo.setPressed(false);
					grupo.setClickable(false);
					grupo.setPressed(true);
					sologrupo = "grupo";
					bsologrupo = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton Interior
		interior.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					interior.setClickable(false);
					interior.setPressed(true);
					exterior.setClickable(true);
					exterior.setPressed(false);
					interiorexterior = "interior";
					binteriorexterior = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton Exterior
		exterior.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					interior.setClickable(true);
					interior.setPressed(false);
					exterior.setClickable(false);
					exterior.setPressed(true);
					interiorexterior = "exterior";
					binteriorexterior = true;

					if (bdianoche == true && binteriorexterior == true
							&& bsologrupo == true) {
						sugerir.setClickable(true);
					}
					return true;
				} else
					return false;
			}
		});

		// Al pulsar el boton de Sugerir (enviar sugerencia)
		sugerir.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == 1) {
					
					//Checar que no estén vacíos los campos
					String ContenidoTitulo = edtitulo.getText().toString();
					String ContenidoDescripcion = eddescripcion.getText().toString();
					
					if(ContenidoTitulo.matches("") || ContenidoDescripcion.matches("")){
						Toast.makeText(Recomendar.this, "Faltan datos por llenar :O", Toast.LENGTH_SHORT).show();					    
					}
					
					else {
						//Enviar Sugerencia
					new CreateNewProduct().execute();
						//Regresar a las recomendaciones
					Intent i = new Intent(getApplicationContext(),
							Recomendaciones.class);
					startActivity(i);
					Toast.makeText(Recomendar.this,
							"¡Gracias, se ha enviado tu sugerencia!",
							Toast.LENGTH_LONG).show();
					}
					
					return true;
				} else
					return false;
			}
		});
	}

	/* ActionBar Sherlock Buttons
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.soloenviar, menu);
		return true;
	}

	// OnClick Botones del ActionBar
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		// Enviar
		case R.id.soloenviar:

			new CreateNewProduct().execute();
			Intent i = new Intent(getApplicationContext(),
					Recomendaciones.class);
			startActivity(i);
			Toast.makeText(Recomendar.this,
					"¡Gracias, se ha enviado tu sugerencia!",
					Toast.LENGTH_SHORT).show();

		default:
			return super.onOptionsItemSelected(item);

		}

	}*/

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
			pDialog = new ProgressDialog(Recomendar.this);
			pDialog.setMessage("Teletransportando sugerencia...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			Spinner mySpinner = (Spinner) findViewById(R.id.spinnercategoria);
			String categoria = mySpinner.getSelectedItem().toString();
			String titulo = edtitulo.getText().toString();
			// String price = inputPrice.getText().toString();
			String descripcion = eddescripcion.getText().toString();
			// String opcion_1 = "fr�o";
			// String opcion_2 = "interior";
			// String opcion_3 = "extremo";
			// String id_usuarios = "69";
			// String fb_id= myPrefs.getString(FB_ID, null);
			loadPrefs();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("titulo", titulo));
			params.add(new BasicNameValuePair("categoria", categoria));
			params.add(new BasicNameValuePair("descripcion", descripcion));
			params.add(new BasicNameValuePair("opcion_1", dianoche));
			params.add(new BasicNameValuePair("opcion_2", sologrupo));
			params.add(new BasicNameValuePair("opcion_3", interiorexterior));
			params.add(new BasicNameValuePair("fb_id", fb_id));
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

		fb_id = sp.getString(FB_ID, null);

	}

	// Detectar el umero de caracteres en los TextEdit:

	// Titulo Watcher
	private final TextWatcher TituloTextEditorWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// This sets a textview to the current length
			longitudTitulo.setText("" + String.valueOf(s.length()) + "/30");
		}

		public void afterTextChanged(Editable s) {
		}
	};

	// Descripcion Watcher
	private final TextWatcher DescripcionTextEditorWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// This sets a textview to the current length
			longitudDescripcion
					.setText("" + String.valueOf(s.length()) + "/70");
		}

		public void afterTextChanged(Editable s) {
		}
	};

}
