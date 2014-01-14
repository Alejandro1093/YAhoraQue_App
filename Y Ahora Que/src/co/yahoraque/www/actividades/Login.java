package co.yahoraque.www.actividades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import co.ensalsaverde.apps.yahoraque.R;
import co.yahoraque.www.JSONParser;
import co.yahoraque.www.TutoPagerAdapter;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class Login extends Activity implements OnClickListener {			
	private static final List<String> PERMISSIONS = Arrays
			.asList("email, user_relationshipsrelationship_status");
	// private final String PENDING_ACTION_BUNDLE_KEY =
	// "com.facebook.samples.hellofacebook:PendingAction";
	private final String PENDING_ACTION_BUNDLE_KEY = "co.yahoraque.www:PendingAction";
	private GraphUser user;
	private PendingAction pendingAction = PendingAction.NONE;
	int pager;
	
	private final String FIRSTNAME = "first_name";
	private final String FBID = "fb_id";
	private final String MAIL = "correo";
	private final String BIRTHDAY = "cumple";
	private final String SEX = "sexo";
	private final String RELATIONSHIP = "situacion_sentimental";
	
	String first_name, correo, fb_id, sexo, cumple, situacion_sentimental;
	
	//"Boolean" To know if user creation was successful
	int success = 0;
	//To know if the device is connected to the internet (mobile or wifi) or not
	int connectionType = 0;


	// url to check if user exists and/or create a new one
	private static String url_get_or_create_user = "http://ensalsaverdeco.domain.com/yahoraqueusuarios/get_or_create_user.php";
	// Progress Dialog
	private ProgressDialog pDialog;
	// Json Parse
	JSONParser jsonParser = new JSONParser();
	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	// Animation
	// Animation animFadeOut;W

	// Unique PagerAdapter
	TutoPagerAdapter adapter = new TutoPagerAdapter();

	// ViewPager Tutorial
	ViewPager myPager = null;
	// Imagen de mano a animar
	ImageView manoflecha;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	LoginButton loginButton;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		setContentView(R.layout.login);

		// Tutorial View Pager
		myPager = (ViewPager) findViewById(R.id.tutorial);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);

		/*
		 * ANIMATIONS
		 * 
		 * manoflecha = (ImageView) findViewById(R.id.tutomano); //Declaro
		 * Animacion RotateAnimation anim = new RotateAnimation(0f, 350f, 15f,
		 * 15f); anim.setInterpolator(new LinearInterpolator());
		 * anim.setRepeatCount(Animation.INFINITE); anim.setDuration(700);
		 * //Ejecuto Animacion
		 *manoflecha.setAnimation(anim);
		 */

		Button flechaderecha = (Button) findViewById(R.id.flechaderecha1);
		flechaderecha.setOnClickListener(this);
		Button flechaizquierda = (Button) findViewById(R.id.flechaizquierda1);
		flechaizquierda.setOnClickListener(this);

		loginButton = (LoginButton) findViewById(R.id.buttonLoginLogout);
		loginButton.setBackgroundResource(R.drawable.botonfblogin);
		loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Removes
																			// "f"
																			// logo
																			// on
																			// button
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						Login.this.user = user;
						updateUI();
						// It's possible that we were waiting for this.user to
						// be populated in order to post a
						// status update.
						handlePendingAction();
					}
				});
		loginButton.setReadPermissions(Arrays.asList("email", "user_birthday",
				"user_relationships"));
		

		// LOAD AMINATION
		/*
		 * animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
		 * R.anim.fade_out);
		 * 
		 * // start animation(s) loginButton.startAnimation(animFadeOut);
		 */

	}

	public void onClick(View v) {
		
		pager = myPager.getCurrentItem();

		switch (v.getId()) {

		case R.id.flechaderecha1:

			if (pager == 3) {
				pager = 3;
			}

			else
				pager++;

			myPager.setCurrentItem(pager, true);

			break;

		case R.id.flechaizquierda1:

			if (pager == 0) {
				pager = 0;
			}

			else
				pager--;

			myPager.setCurrentItem(pager, true);

		}// del switch
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(Login.this).setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
		updateUI();

		if (state.isOpened()) {

			// Request user data and show the results
				//This function is Deprecated!
			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {								
								
								//Save data on SharedPreferences
								first_name = user.getFirstName();
								fb_id = user.getId();
								correo = (String) user.getProperty("email");
								sexo = (String) user.getProperty("gender");
								cumple = user.getBirthday();
								situacion_sentimental = (String) user.getProperty("relationship_status");
								
								savePrefs();
								new QueryCreateUser().execute();	
								

								/*
								 * Intent i = new
								 * Intent(getApplicationContext(), Datos.class);
								 * startActivity(i);
								 */

								// Mandar toda la info al archivo de PHP

							}
						}
					});
		}
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class QueryCreateUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Espere un momento por favor");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("correo", correo));
			params.add(new BasicNameValuePair("fb_id", fb_id));
			params.add(new BasicNameValuePair("sexo", sexo));
			params.add(new BasicNameValuePair("cumple", cumple));
			params.add(new BasicNameValuePair("situacion_sentimental",
					situacion_sentimental));
			

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_get_or_create_user, "POST",
					params);

			// check log cat from response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				success = json.getInt(TAG_SUCCESS);

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
				Log.e("log_tag", "Error parsing data " + e.toString());
				Log.e("log_tag", "Failed data was:\n" + json);
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if (success == 1) {
				// successfully created product
				Intent i = new Intent(getApplicationContext(),
						Recomendaciones.class);
				startActivity(i);
				// closing this screen
				
			} else {
				// failed to create product
				Toast.makeText(Login.this,"¡Uy! Algo salió mal",Toast.LENGTH_LONG).show();
			}			

		}

	}

	protected void savePrefs() {
		
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit(); 
		edit.putString(FBID, fb_id);
		edit.putString(FIRSTNAME, first_name);
		edit.putString(MAIL, correo);
		edit.putString(SEX, sexo);
		edit.putString(BIRTHDAY, cumple);
		edit.putString(RELATIONSHIP, situacion_sentimental);
		edit.commit();
				
	}

	//Se puede usar para actualizar la interfaz en la misma actividad
	private void updateUI() {
		Session session = Session.getActiveSession();
		boolean enableButtons = (session != null && session.isOpened());

		if (enableButtons && user != null && success==1) {

			/*Intent i = new Intent(getApplicationContext(),
					Recomendaciones.class);
			startActivity(i);*/

		} else {
					
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

}