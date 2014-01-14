package co.yahoraque.www.actividades;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import co.ensalsaverde.apps.yahoraque.R;
 
public class SplashScreen extends Activity {
 
    // Splash screen timer (1000 is one second)
    private static int SPLASH_TIME_OUT = 3000;

    //To improve code-generated gradient quality in certain phones.
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_asv);
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
            
            
        }, SPLASH_TIME_OUT);
        
        
    }
 
}