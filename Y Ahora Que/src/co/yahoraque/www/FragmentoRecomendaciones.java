package co.yahoraque.www;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import co.ensalsaverde.apps.yahoraque.R;

public class FragmentoRecomendaciones extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflating layout
		View v = inflater.inflate(R.layout.fragmento, container, false);
		return v;
	}
	
	/*@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// We set clear listener
		mClearButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// We clear view
				mMessageView.setText("- empty -");
				// We visualize toast in chaining
				Toast.makeText(getActivity(), "Message loader cleared.", Toast.LENGTH_SHORT).show();
			}
		});
	}*/

	
}





