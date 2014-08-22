package com.is3av.datacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText edit;
	private Button button;
	private String userN;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setting the button
		button = (Button) findViewById(R.id.button1);
		button.setText("Enter");
		button.setOnClickListener(listener);
		//setting up EditText
		edit = (EditText)findViewById(R.id.editText1);
				
	}
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			userN = edit.getText().toString();
			Intent intent = new Intent(MainActivity.this,Collect.class);
			intent.putExtra("userN", userN);
			startActivity(intent);
		}

	};
}
