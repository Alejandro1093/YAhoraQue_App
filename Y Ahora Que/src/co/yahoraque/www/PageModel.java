package co.yahoraque.www;

//Esta actividad está relacionada con hacer que el viewpager sea "infinito" en las recomendaciones

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.ensalsaverde.apps.yahoraque.R;

public class PageModel extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmento);
				 
		}
	 private int index;
	 private String text;
	 public TextView textView;
	 public RelativeLayout rLayout;

	 
	 public PageModel(int index) {
	  this.index = index;
	  setIndex(index);
	 }

	 public int getIndex() {
	  return index;
	 }

	 public void setIndex(int index) {
	  this.index = index;
	  setText(index);	  
	 }

	 public String getText() {
	  return text;
	 }

	 private void setText(int index) {
	  this.text = String.format("Page %s", index);
	 }


}
