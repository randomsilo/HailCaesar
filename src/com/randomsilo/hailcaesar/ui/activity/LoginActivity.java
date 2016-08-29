package com.randomsilo.hailcaesar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		HailCaesarSession.getInstance().Init(this);
	}
	
	public void login(View v) {
    	
		EditText password = (EditText) findViewById(R.id.Password);
				
		if(password == null || password.getText() == null || password.getText().toString().length() == 0) {
			Toast.makeText(getApplicationContext(), "Password Required", Toast.LENGTH_LONG).show();
			return;
		}
		
		String pass = password.getText().toString();
		HailCaesarSession.getInstance().getSecurityManager().authenticate(v.getContext(), pass);
    }
}
