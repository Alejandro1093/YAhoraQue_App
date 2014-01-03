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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Subir extends Activity {		
	 
	  private Spinner categorias;
	  private Button btnSubmit;
	  boolean interior, exterior, dia, noche, solo, grupo = false;
	  
		// Progress Dialog
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();
		EditText edtitulo;
		EditText eddescripcion;

		// url to create new product
		private static String url_create_product = "http://ensalsaverdeco.domain.com/yahoraquesugerencias/create_product.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomendar);
		

				// Edit Text
				 edtitulo = (EditText) findViewById(R.id.titulosugerencia);
				 eddescripcion = (EditText) findViewById(R.id.descripcionsugerencia);

				// Create button
				/*Button botonsolo = (Button)findViewById(R.id.botonsolo);
		        Button botongrupo = (Button)findViewById(R.id.botongrupo);
		        Button botonnoche = (Button)findViewById(R.id.botonnoche);
		        Button botondia= (Button)findViewById(R.id.botondia);
		        Button botonexterior = (Button)findViewById(R.id.botonexterior);
		        Button botoninterior = (Button)findViewById(R.id.botoninterior);*/
				Button botonenviar = (Button) findViewById(R.id.botonenviar);

				// button click event
				botonenviar.setOnClickListener(new View.OnClickListener() {

					//@Override
					public void onClick(View view) {
						// creating new product in background thread
						new CreateNewProduct().execute();						
					}
				});
			}

			/**
			 * Background Async Task to Create new product
			 * */
			class CreateNewProduct extends AsyncTask<String, String, String> {

				/**
				 * Before starting background thread Show Progress Dialog
				 * */
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(Subir.this);
					pDialog.setMessage("Se está enviando la sugerencia :D");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}

				/**
				 * Creating product
				 * */
				protected String doInBackground(String... args) {
					Spinner mySpinner = (Spinner)findViewById(R.id.spinnercategoria);
					String categoria = mySpinner.getSelectedItem().toString();
					String titulo = edtitulo.getText().toString();
					//String price = inputPrice.getText().toString();
					String descripcion = eddescripcion.getText().toString();

					// Building Parameters
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("titulo", titulo));
					params.add(new BasicNameValuePair("categoria", categoria));
					params.add(new BasicNameValuePair("descriptcon", descripcion));

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
							Intent i = new Intent(getApplicationContext(), Recomendaciones.class);
							startActivity(i);
							
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
		

		
		/*final Button botonsolo = (Button)findViewById(R.id.botonsolo);
        final Button botongrupo = (Button)findViewById(R.id.botongrupo);
        final Button botonnoche = (Button)findViewById(R.id.botonnoche);
        final Button botondia= (Button)findViewById(R.id.botondia);
        final Button botonexterior = (Button)findViewById(R.id.botonexterior);
        final Button botoninterior = (Button)findViewById(R.id.botoninterior);
        
        
        botonexterior.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	if(exterior==false){
            		botonexterior.setPressed(true);
                	exterior = true;}
                	if(exterior==true){
                	 botonexterior.setPressed(false);
                	exterior = false;}
                	}
                return true;}            
        });
        
        botoninterior.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	if(interior==false){
            		botoninterior.setPressed(true);
                	exterior = true;}
                	if(interior==true){
                	 botoninterior.setPressed(false);
                	exterior = false;}
                	}
                return true;}            
        });
        

	 
		//addListenerOnButton();
		//addListenerOnSpinnerItemSelection();
	  }
	 
	  
	 
	  public void addListenerOnSpinnerItemSelection() {
		categorias = (Spinner) findViewById(R.id.spinner1);
		categorias.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	  }
	 
	   get the selected dropdown list value
	  public void addListenerOnButton() {
	 
		categorias = (Spinner) findViewById(R.id.spinner1);
		btnSubmit = (Button) findViewById(R.id.botonenviar);
	 
		btnSubmit.setOnClickListener(new OnClickListener() {
	 
		  public void onClick(View v) {
	 
		    Toast.makeText(Subir.this,
			"OnClickListener : " + 
	                "\nSpinner 1 : "+ String.valueOf(categorias.getSelectedItem()), 
	                //"\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()),
				Toast.LENGTH_SHORT).show();
		  }
	 
		});
	 */
	}
