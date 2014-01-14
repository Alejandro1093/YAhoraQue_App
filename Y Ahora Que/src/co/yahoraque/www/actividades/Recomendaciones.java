package co.yahoraque.www.actividades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.ensalsaverde.apps.yahoraque.R;
import co.yahoraque.www.JSONParser;
import co.yahoraque.www.NetworkUtil;
import co.yahoraque.www.PageModel;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Recomendaciones extends SherlockFragmentActivity {

	// we name the left, middle and right page
	private static final int PAGE_LEFT = 0;
	private static final int PAGE_MIDDLE = 1;
	private static final int PAGE_RIGHT = 2;

	// ViewPager
	ViewPager viewPager = null;

	private LayoutInflater mInflater;
	private int mSelectedPageIndex = 1;
	// To know if need to run soft page turn when start/refresh
	private Boolean firstLoad = true;
	// we save each page in a model
	private PageModel[] mPageModel = new PageModel[3];

	// ** == versi�n est�tica **//
	// **ViewPager myPager = (ViewPager) findViewById(R.id.swype);
	// **private String[] frags = {FragmentoRecomendaciones.class.getName(),
	// FragmentoRecomendaciones.class.getName(),
	// FragmentoRecomendaciones.class.getName()};

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = "http://ensalsaverdeco.domain.com/yahoraquesugerencias/get_all_products.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "sugerencias";
	private static final String TAG_PID = "id_sugerencias";
	private static final String TAG_NAME = "titulo";
	private static final String TAG_DESCRIPTION = "descripcion";
	private static final String TAG_CATEGORY = "categoria";
	private static final String TAG_OPTION_1 = "opcion_1";
	private static final String TAG_OPTION_2 = "opcion_2";
	private static final String TAG_OPTION_3 = "opcion_3";
	private static final String TAG_LIKES = "likes";
	private static final String TAG_DISLIKES = "dislikes";
	private static final String TAG_SPAM = "spam";
	private static final String TAG_USERS_ID = "id_usuarios";

	TextView titulo1, titulo2, titulo3, recom1, recom2, recom3;
	Button flechaderecha, flechaizquierda;

	int contadorSugerencias = 0;
	int contadorPaginas = 0;
	int c1, c2, c3; // Contadores para setText usando contadorPaginas
	Boolean primeraPagina = true;// Para distinguir entre la primer pagina o
									// avanzar
	Boolean segundaPagina = false;// Para que se muestre la segunda pagina una
									// vez y no se la salte
	int lengthMin1, lengthMin2, lengthMin3;

	// products JSONArray
	JSONArray products = null;

	// Arreglos bonitos
	String[] nameArr;
	String[] descriptionArr;
	String[] categoryArr;
	String[] option1Arr;
	String[] option2Arr;
	String[] option3Arr;
	String[] likesArr;
	String[] dislikesArr;
	String[] spamArr;
	String[] usersidArr;

	ListView lv; // ????

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titulo1 = (TextView) findViewById(R.id.titulo3pag2);

		setContentView(R.layout.recomendaciones);

		// initializing the model
		initPageModel();

		// ACTION BAR SHERLOCK
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();
		
		//Checar conexión a internet
		int status = NetworkUtil.getConnectivityStatus(getBaseContext());
		if(status == 0){
			Toast.makeText(Recomendaciones.this,
					"Error de conexión (por culpa de los aliens)",
					Toast.LENGTH_LONG).show();
		}
		else{
		// Loading products in Background Thread
		new LoadAllProducts().execute();
		}

		// Get listview
		// ListView lv = (ListView)findViewById(R.id.list);

		// BOTONES FLECHA
		// Derecha
		flechaderecha = (Button) findViewById(R.id.flechaderecha2);
		// Al hacer click
		flechaderecha.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Cambia a la pagina de la derecha con animacion
				viewPager.setCurrentItem(PAGE_RIGHT, true);
				return true;
			}
		});

		// Izquierda
		flechaizquierda = (Button) findViewById(R.id.flechaizquierda2);
		// Al hacer click
		flechaizquierda.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Cambia a la pagina de la izquierda con animacion
				viewPager.setCurrentItem(PAGE_LEFT, true);
				return true;
			}
		});

		// ViewPgar Setup
		mInflater = getLayoutInflater();
		MyagerAdaper adapter = new MyagerAdaper();

		viewPager = (ViewPager) findViewById(R.id.swype);
		viewPager.setAdapter(adapter);
		// we dont want any smoothscroll. This enables us to switch the page
		// without the user notifiying this
		viewPager.setCurrentItem(PAGE_MIDDLE, false);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			public void onPageSelected(int position) {
				mSelectedPageIndex = position;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE) {

					final PageModel leftPage = mPageModel[PAGE_LEFT];
					final PageModel middlePage = mPageModel[PAGE_MIDDLE];
					final PageModel rightPage = mPageModel[PAGE_RIGHT];

					final int oldLeftIndex = leftPage.getIndex();
					final int oldMiddleIndex = middlePage.getIndex();
					final int oldRightIndex = rightPage.getIndex();

					// user swiped to right direction --> left page
					if (mSelectedPageIndex == PAGE_LEFT) {

						// moving each page content one page to the right
						leftPage.setIndex(oldLeftIndex - 1);
						middlePage.setIndex(oldLeftIndex);
						rightPage.setIndex(oldMiddleIndex);

						setContent(PAGE_RIGHT);
						setContent(PAGE_MIDDLE);
						setContent(PAGE_LEFT);

						PaginaAtrasNuevas();

						// user swiped to left direction --> right page
					} else if (mSelectedPageIndex == PAGE_RIGHT) {

						leftPage.setIndex(oldMiddleIndex);
						middlePage.setIndex(oldRightIndex);
						rightPage.setIndex(oldRightIndex + 1);

						setContent(PAGE_LEFT);
						setContent(PAGE_MIDDLE);
						setContent(PAGE_RIGHT);

						PaginaAvanzaNuevas();
					}
					viewPager.setCurrentItem(PAGE_MIDDLE, false);
				}
			}

			private void PaginaAvanzaRandom() {
			}

			private void PaginaAtrasRandom() {

			}

			private void PaginaAtrasNuevas() {
				// Con contador de paginas

				if (contadorPaginas >= 1
						&& contadorPaginas * 3 <= products.length()) {
					contadorPaginas--;

					c1 = contadorPaginas * 3;
					c2 = contadorPaginas * 3 + 1;
					c3 = contadorPaginas * 3 + 2;

					titulo1.setText(nameArr[c1]);
					recom1.setText(descriptionArr[c1]);
					titulo2.setText(nameArr[c2]);
					recom2.setText(descriptionArr[c2]);
					titulo3.setText(nameArr[c3]);
					recom3.setText(descriptionArr[c3]);
					// recom3.setText(""+contadorPaginas);

				}

				if (contadorPaginas == 0) {
					primeraPagina = true;
					contadorPaginas = 1;
					segundaPagina = true;
				}

				if (contadorPaginas * 3 + 2 >= products.length()) {
					contadorPaginas--;
				}

				/*
				 * if(contadorSugerencias<=4){ titulo1.setText("Yolo");
				 * recom1.setText("Swag"); titulo2.setText("Yolo");
				 * recom2.setText("Swag"); titulo3.setText("Yolo");
				 * recom3.setText("Swag"); } else{
				 * contadorSugerencias=contadorSugerencias-3;
				 * titulo3.setText(nameArr[contadorSugerencias]);
				 * recom3.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias--;
				 * titulo2.setText(nameArr[contadorSugerencias]);
				 * recom2.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias--;
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]); }
				 */
			}

			private void PaginaAvanzaNuevas() {
				// con Contador Paginas

				c1 = contadorPaginas * 3;
				c2 = contadorPaginas * 3 + 1;
				c3 = contadorPaginas * 3 + 2;

				if (c1 >= products.length()) {
					titulo1.setText("");
					recom1.setText("¡OMG, has alcanzado el final de las sugerencias!");
					titulo2.setText("");
					recom2.setText("Puedes subir una en el botón de + arriba a la derecha...");
					titulo3.setText("");
					recom3.setText("Comparte tus mejores ideas anti-aburrimiento ;D");
				}

				if (c2 == products.length()) {
					titulo1.setText(nameArr[c1]);
					recom1.setText(descriptionArr[c1]);
					titulo2.setText("");
					recom2.setText("Has alcanzado el final de las sugerencias...");
					titulo3.setText("");
					recom3.setText("¡comparte algunas en el botón + de arriba!");
					contadorPaginas++;
				}

				if (c3 == products.length()) {
					titulo1.setText(nameArr[c1]);
					recom1.setText(descriptionArr[c1]);
					titulo2.setText(nameArr[c2]);
					recom2.setText(descriptionArr[c2]);
					titulo3.setText("");
					recom3.setText("¡Woah! Has alcanzado el final de las sugerencias");
					contadorPaginas++;
				}

				if (contadorPaginas > 0 && c3 < products.length()) {
					if (segundaPagina == false) {
						contadorPaginas++;
					}

					titulo1.setText(nameArr[c1]);
					recom1.setText(descriptionArr[c1]);
					titulo2.setText(nameArr[c2]);
					recom2.setText(descriptionArr[c2]);
					titulo3.setText(nameArr[c3]);
					recom3.setText(descriptionArr[c3]);
					// recom3.setText(""+contadorPaginas);

					if (segundaPagina == true) {
						segundaPagina = false;
					}
				}

				if (contadorPaginas == 0
						&& contadorPaginas * 3 + 2 < products.length()) {

					titulo1.setText(nameArr[c1]);
					recom1.setText(descriptionArr[c1]);
					titulo2.setText(nameArr[c2]);
					recom2.setText(descriptionArr[c2]);
					titulo3.setText(nameArr[c3]);
					recom3.setText(descriptionArr[c3]);
					// recom3.setText(""+contadorPaginas);

					if (primeraPagina == true) {
						recom2.setText("Debug: Esta es la primera pagina");
						primeraPagina = false;
						segundaPagina = true;
						contadorPaginas = 1;
					}
				}

				/*
				 * Con contador de sugerencias
				 * 
				 * lengthMin1 = lengthMin1-1;
				 * 
				 * int lengthMin2 = products.length(); lengthMin2 =
				 * lengthMin2-2;
				 * 
				 * int lengthMin3 = products.length(); lengthMin3 =
				 * lengthMin3-3;
				 * 
				 * if(contadorSugerencias==products.length()){
				 * titulo1.setText("");
				 * recom1.setText("Has alcanzado el final de las sugerencias");
				 * titulo2.setText("");
				 * recom2.setText("Has alcanzado el final de las sugerencias");
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * 
				 * if(contadorSugerencias==lengthMin1){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * titulo2.setText("");
				 * recom2.setText("Has alcanzado el final de las sugerencias");
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * if(contadorSugerencias==lengthMin2){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo2.setText(nameArr[contadorSugerencias]);
				 * recom2.setText(descriptionArr[contadorSugerencias]);
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * if(contadorSugerencias==lengthMin3){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo2.setText(nameArr[contadorSugerencias]);
				 * recom2.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo3.setText(nameArr[contadorSugerencias]);
				 * recom3.setText(descriptionArr[contadorSugerencias]); }
				 * 
				 * if(contadorSugerencias==0){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo2.setText(nameArr[contadorSugerencias]);
				 * recom2.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo3.setText(nameArr[contadorSugerencias]);
				 * recom3.setText(descriptionArr[contadorSugerencias]); }
				 * 
				 * if(contadorSugerencias<lengthMin3){ contadorSugerencias++;
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo2.setText(nameArr[contadorSugerencias]);
				 * recom2.setText(descriptionArr[contadorSugerencias]);
				 * contadorSugerencias++;
				 * titulo3.setText(nameArr[contadorSugerencias]);
				 * recom3.setText(descriptionArr[contadorSugerencias]); }
				 */

				/*
				 * Version vieja int lengthMin1 = products.length(); lengthMin1
				 * = lengthMin1-1;
				 * 
				 * int lengthMin2 = products.length(); lengthMin2 =
				 * lengthMin2-2;
				 * 
				 * int lengthMin3 = products.length(); lengthMin3 =
				 * lengthMin3-3;
				 * 
				 * if(contadorSugerencias==products.length()){
				 * titulo1.setText("");
				 * recom1.setText("Has alcanzado el final de las sugerencias");
				 * titulo2.setText("");
				 * recom2.setText("Has alcanzado el final de las sugerencias");
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * 
				 * if(contadorSugerencias==lengthMin1){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * titulo2.setText("");
				 * recom2.setText("Has alcanzado el final de las sugerencias");
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * if(contadorSugerencias==lengthMin2){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * titulo2.setText(nameArr[contadorSugerencias]+1);
				 * recom2.setText(descriptionArr[contadorSugerencias+1]);
				 * titulo3.setText("");
				 * recom3.setText("Has alcanzado el final de las sugerencias");
				 * }
				 * 
				 * if(contadorSugerencias==lengthMin3){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * titulo2.setText(nameArr[contadorSugerencias+1]);
				 * recom2.setText(descriptionArr[contadorSugerencias+1]);
				 * titulo3.setText(nameArr[contadorSugerencias+2]);
				 * recom3.setText(descriptionArr[contadorSugerencias+2]); }
				 * 
				 * if(contadorSugerencias<lengthMin3){
				 * titulo1.setText(nameArr[contadorSugerencias]);
				 * recom1.setText(descriptionArr[contadorSugerencias]);
				 * titulo2.setText(nameArr[contadorSugerencias+1]);
				 * recom2.setText(descriptionArr[contadorSugerencias+1]);
				 * titulo3.setText(nameArr[contadorSugerencias+2]);
				 * recom3.setText(descriptionArr[contadorSugerencias+2]);
				 * contadorSugerencias++; }
				 */

			}
		});
	}

	// ActionBar Sherlock Buttons
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.recomendaciones, menu);
		return true;
	}

	// OnClick Botones del ActionBar
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		// Recomendar
		case R.id.recomendar:

			Intent i = new Intent(getApplicationContext(), Recomendar.class);
			startActivity(i);

			return true;
			
			// Refrescar
		case R.id.refrescar:	
			//Checar conexión a internet
			int status = NetworkUtil.getConnectivityStatus(getBaseContext());
			
			if(status == 0){
				Toast.makeText(Recomendaciones.this,
						"Error de conexión (por culpa de los aliens)",
						Toast.LENGTH_LONG).show();
			}

			else{
			new LoadAllProducts().execute();
			}

			return true;
			// Feedback
		case R.id.feedback:
			Intent j = new Intent(getApplicationContext(), Feedback.class);
			startActivity(j);

			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

	private void setContent(int index) {
		final PageModel model = mPageModel[index];
		// �Aqu� debo de meter que se obtenga la info de la BD?

		/*
		 * if(contadorSugerencias<lengthMin1){
		 * 
		 * titulo1.setText(nameArr[contadorSugerencias]);
		 * recom1.setText(descriptionArr[contadorSugerencias]);
		 * titulo2.setText(nameArr[contadorSugerencias+1]);
		 * recom2.setText(descriptionArr[contadorSugerencias+1]);
		 * titulo3.setText(""+(contadorSugerencias+2));
		 * recom3.setText(descriptionArr[contadorSugerencias+2]);
		 * contadorSugerencias++; }else{ titulo1.setText("");
		 * titulo2.setText(""); titulo3.setText(""); }
		 */

		model.textView.setText("hola");

	}

	private void initPageModel() {
		for (int i = 0; i < mPageModel.length; i++) {
			// initing the pagemodel with indexes of -1, 0 and 1
			mPageModel[i] = new PageModel(i - 1);
		}
	}

	private class MyagerAdaper extends PagerAdapter {

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			// we only need three pages
			return 3;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			RelativeLayout rLayout = (RelativeLayout) mInflater.inflate(
					R.layout.fragmento, null);

			if (position == 0) {
				rLayout = (RelativeLayout) mInflater.inflate(
						R.layout.fragmento, null);
				((ViewPager) container).addView(rLayout, 0);
			}
			if (position == 1) {
				rLayout = (RelativeLayout) mInflater.inflate(
						R.layout.fragmento2, null);
				((ViewPager) container).addView(rLayout, 0);
				titulo1 = (TextView) findViewById(R.id.titulo3pag2);
				titulo2 = (TextView) findViewById(R.id.titulo2pag2);
				titulo3 = (TextView) findViewById(R.id.titulo1pag2);
				recom1 = (TextView) findViewById(R.id.recom3pag2);
				recom2 = (TextView) findViewById(R.id.recom2pag2);
				recom3 = (TextView) findViewById(R.id.recom1pag2);

			}
			if (position == 2) {
				rLayout = (RelativeLayout) mInflater.inflate(
						R.layout.fragmento3, null);
				((ViewPager) container).addView(rLayout, 0);
			}

			// DEBUGGING PARA SABER QUÉ PÁGINA SE ESTÁ MOSTRANDO?

			TextView textView = (TextView) mInflater.inflate(R.layout.recomen3,
					null);
			PageModel currentPage = mPageModel[position];
			currentPage.textView = textView;
			textView.setText(currentPage.getText()); //			
			return rLayout;

			/*
			 * ¿QUÉ ERA ESTO? ORIGINAL public Object instantiateItem(ViewGroup
			 * container, int position) { TextView textView =
			 * (TextView)mInflater.inflate(R.layout.infinitecontent, null);
			 * PageModel currentPage = mPageModel[position];
			 * currentPage.textView = textView;
			 * textView.setText(currentPage.getText());
			 * container.addView(textView); return textView;
			 */

		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}
	}

	// **int pager =1;

	/*
	 * Adaptadores con la clase de SwypePagerAdapter SwypePagerAdapter adapter =
	 * new SwypePagerAdapter(); myPager.setAdapter(adapter);
	 * myPager.setCurrentItem(1);
	 */

	// **myPager.setAdapter(new MyAdapter(this));
	// **myPager.setOnPageChangeListener(new
	// ViewPager.SimpleOnPageChangeListener() {
	// **@Override
	// **public void onPageSelected(int position) {
	// **Toast.makeText(getApplicationContext(), "swyped " + position ,
	// Toast.LENGTH_SHORT).show();
	// **}
	// **});

	/*
	 * sugerir.setOnTouchListener(new View.OnTouchListener() {
	 * 
	 * public boolean onTouch(View arg0, MotionEvent arg1) { if
	 * (arg1.getAction()==0){ Intent i = new Intent(getApplicationContext(),
	 * Subir.class); startActivity(i);
	 * 
	 * } return true;} });
	 */

	// **}

	// public void onClick(View v) {
	// viewPager.setCurrentItem(PAGE_MIDDLE, true);

	// viewPager.setCurrentItem(pagina, true);
	/*
	 * case R.id.botonsugerir:
	 * 
	 * Intent i = new Intent(getApplicationContext(), Recomendar.class);
	 * startActivity(i);
	 * 
	 * break;
	 * 
	 * 
	 * case R.id.botonaleatorio: aleatorio.setPressed(true);
	 * ultimos.setPressed(false);
	 * 
	 * break;
	 */
	// }

	// }

	// **private class MyAdapter extends FragmentPagerAdapter{

	// **private Context mContext;

	// **public MyAdapter(FragmentActivity activity) {
	// **super(activity.getSupportFragmentManager());
	// **mContext = activity;
	// **}

	// **@Override
	// **public Object instantiateItem(ViewGroup container, int position) {
	// **Fragment frag = (Fragment) super.instantiateItem(container, position);
	// **return frag;
	// **}

	// **@Override
	// **public Fragment getItem(int pos) {
	// **return Fragment.instantiate(mContext, frags[pos]);
	// **}

	// **@Override
	// **public int getCount() {
	// **return frags.length;
	// **}

	// } //del on click

	public void getRecomendaciones() {

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Recomendaciones.this);
			pDialog.setMessage("Actualizando el contenido");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */

		protected String doInBackground(String... args) {
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_all_products,
						"GET", params);

				// Check your log cat for JSON reponse
				Log.d("All Products: ", json.toString());

				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					int longitud = products.length();
					nameArr = new String[longitud];
					descriptionArr = new String[longitud];
					categoryArr = new String[longitud];
					option1Arr = new String[longitud];
					option2Arr = new String[longitud];
					option3Arr = new String[longitud];
					likesArr = new String[longitud];
					dislikesArr = new String[longitud];
					spamArr = new String[longitud];
					usersidArr = new String[longitud];

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_NAME);
						String description = c.getString(TAG_DESCRIPTION);
						String category = c.getString(TAG_CATEGORY);
						String option1 = c.getString(TAG_OPTION_1);
						String option2 = c.getString(TAG_OPTION_2);
						String option3 = c.getString(TAG_OPTION_3);
						String likes = c.getString(TAG_LIKES);
						String dislikes = c.getString(TAG_DISLIKES);
						String spam = c.getString(TAG_SPAM);
						String usersid = c.getString(TAG_USERS_ID);

						nameArr[i] = name;
						descriptionArr[i] = description;
						categoryArr[i] = category;
						option1Arr[i] = option1;
						option2Arr[i] = option2;
						option3Arr[i] = option3;
						likesArr[i] = likes;
						dislikesArr[i] = dislikes;
						spamArr[i] = spam;
						usersidArr[i] = usersid;

						/*
						 * // creating new HashMap HashMap<String, String> map =
						 * new HashMap<String, String>();
						 * 
						 * // adding each child node to HashMap key => value
						 * map.put(TAG_PID, id); map.put(TAG_NAME, name);
						 * 
						 * // adding HashList to ArrayList
						 * productsList.add(map);
						 */
					}
				} else {
					// no products found
					// Launch Add New product Activity
					// INNESEARIO LOL
					/*
					 * La clase Subir ya no existe: //Intent i = new
					 * 
					 * Intent(getApplicationContext(), Subir.class); // Closing
					 * all previous activities
					 * i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(i);
					 */
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
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// Desplegar toda la info jarcor

			titulo1.setText(nameArr[contadorSugerencias]);
			recom1.setText(descriptionArr[contadorSugerencias]);
			contadorSugerencias++;

			titulo2.setText(nameArr[contadorSugerencias]);
			recom2.setText(descriptionArr[contadorSugerencias]);
			contadorSugerencias++;

			titulo3.setText(nameArr[contadorSugerencias]);
			recom3.setText(descriptionArr[contadorSugerencias]);
			contadorSugerencias++;

			contadorPaginas = 1;
			segundaPagina = true;

			if (firstLoad == true) {
				viewPager.setCurrentItem(PAGE_MIDDLE, false);
				firstLoad = false;
			} else
				viewPager.setCurrentItem(PAGE_LEFT, true);

			/*
			 * if(contadorSugerencias>products.length() ||
			 * contadorSugerencias==products.length()
			 * ||contadorSugerencias==products.length()-1){ titulo1.setText("");
			 * recom1.setText("Haz alcanzado el final de las sugerencias");}
			 * 
			 * else { titulo1.setText(nameArr[contadorSugerencias]);
			 * recom1.setText(descriptionArr[contadorSugerencias]);
			 * contadorSugerencias++;}
			 * 
			 * 
			 * if(contadorSugerencias>products.length() ||
			 * contadorSugerencias==products.length() ||
			 * contadorSugerencias==products.length()-1){ titulo2.setText("");
			 * recom2.setText("Haz alcanzado el final de las sugerencias");}
			 * else { titulo2.setText(nameArr[contadorSugerencias]);
			 * recom2.setText(descriptionArr[contadorSugerencias]);
			 * contadorSugerencias++;}
			 * 
			 * if(contadorSugerencias>products.length() ||
			 * contadorSugerencias==products.length() ||
			 * contadorSugerencias==products.length()-1){ titulo3.setText("");
			 * recom3.setText("Haz alcanzado el final de las sugerencias");}
			 * else { titulo3.setText(nameArr[contadorSugerencias]);
			 * recom3.setText(descriptionArr[contadorSugerencias]);
			 * contadorSugerencias++;}
			 */

		}

		/*
		 * runOnUiThread(new Runnable() { public void run() { /** Updating
		 * parsed JSON data into ListView
		 */
		/*
		 * ListAdapter adapter = new SimpleAdapter( Recomendaciones.this,
		 * productsList, R.layout.list_item, new String[] { TAG_PID, TAG_NAME},
		 * new int[] { R.id.pid, R.id.name }); // updating listview
		 * //setListAdapter(adapter); lv.setAdapter(adapter); } });
		 */

	}
}
