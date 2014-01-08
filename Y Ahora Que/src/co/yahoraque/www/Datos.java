
package co.yahoraque.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import co.ensalsaverde.apps.yahoraque.R;

public class Datos extends Activity {    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos);
        
        
        Button botonsiguiente = (Button)findViewById(R.id.botonsiguiente);
        final Button botontranquilo = (Button)findViewById(R.id.botontranquilo);
        final Button botonemocionante = (Button)findViewById(R.id.botonemocionante);
        final Button botoncalor = (Button)findViewById(R.id.botoncalor);
        final Button botonfrio= (Button)findViewById(R.id.botonfrio);
        final Button botonexterior = (Button)findViewById(R.id.botonexterior);
        final Button botoninterior = (Button)findViewById(R.id.botoninterior);
        
        botonfrio.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botonfrio.setClickable(false);
            		botonfrio.setPressed(true);
            		botoncalor.setClickable(true);
                    botoncalor.setPressed(false);
                return true;}	
                else return false;
            }
        });
        
        botoncalor.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botonfrio.setClickable(true);
            		botonfrio.setPressed(false);
            		botoncalor.setClickable(false);
                    botoncalor.setPressed(true);
                return true;}
                else return false;
            }
        });
        
        botontranquilo.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botontranquilo.setClickable(false);
            		botontranquilo.setPressed(true);
            		botonemocionante.setClickable(true);
                    botonemocionante.setPressed(false);
                return true;}
                else return false;
            }
        });
        
        botonemocionante.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botontranquilo.setClickable(true);
            		botontranquilo.setPressed(false);
            		botonemocionante.setClickable(false);
                    botonemocionante.setPressed(true);
                return true;}
                else return false;
            }
        });

        
        botonexterior.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botonexterior.setClickable(false);
            		botonexterior.setPressed(true);
            		botoninterior.setClickable(true);
                    botoninterior.setPressed(false);
                return true;}
                else return false;
            }
        });
        
        botoninterior.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==1){
                	botonexterior.setClickable(true);
            		botonexterior.setPressed(false);
            		botoninterior.setClickable(false);
                    botoninterior.setPressed(true);
                return true;}
                else return false;
            }
        });
        
        /*botonfrio.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		 
        		botonfrio.setClickable(false);
        		botonfrio.setPressed(true);
        		botoncalor.setClickable(true);
                botoncalor.setPressed(true);
        		
        	   }});*/
               
    

        botonsiguiente.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		Intent i = new Intent(getApplicationContext(), Recomendaciones.class);
        		startActivity(i);    		            		            		

        	   }});
        
    }
                            
}
    