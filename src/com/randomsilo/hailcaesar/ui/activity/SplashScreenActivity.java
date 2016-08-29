package com.randomsilo.hailcaesar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;

public class SplashScreenActivity extends Activity {
	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		HailCaesarSession.getInstance().Init(this);
		
        new Handler().postDelayed(new Runnable() {
        	
            @Override
            public void run() {
            	
            	if(HailCaesarSession.getInstance().requireLogin()) {
            		Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
            	} else {
            		Intent i = new Intent(SplashScreenActivity.this, LandingActivity.class);
                    startActivity(i);
                    finish();	
            	}
                
            }
        }, SPLASH_TIME_OUT);
	}

}
