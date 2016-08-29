package com.randomsilo.hailcaesar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.model.Identity;

public class IdentityActivity extends Activity {

	private EditText safePasswordEditText;
	private EditText distressPasswordEditText;
	private Identity identity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identity);
		
		HailCaesarSession.getInstance().Init(this);
		
		safePasswordEditText = (EditText) findViewById(R.id.SafePassword);
		distressPasswordEditText = (EditText) findViewById(R.id.DistressPassword);
		
		identity = HailCaesarSession.getInstance().getSecurityManager().getIdentity();
		safePasswordEditText.setText(identity.getSafePassword());
		distressPasswordEditText.setText(identity.getDistressPassword());
		
	}
	
	public void save(View view) {
		identity.setSafePassword( safePasswordEditText.getText().toString());
		identity.setDistressPassword( distressPasswordEditText.getText().toString());
		identity.setEulaAccepted(true);
		
		Boolean success = HailCaesarSession.getInstance().getSecurityManager().save(identity);
		if(success) {
			Toast.makeText(getApplicationContext(), "Passwords saved.", Toast.LENGTH_SHORT).show();
			HailCaesarSession.getInstance().getSecurityManager().refreshIdentity();
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to save passwords.", Toast.LENGTH_SHORT).show();
		}
	}
	

}
