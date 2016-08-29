package com.randomsilo.hailcaesar.ui.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.MasterKey;
import com.randomsilo.hailcaesar.model.MasterLock;

public abstract class ImportActivity extends Activity {
	boolean goHomeOnFinish = false;

	@Override
	public void onResume() {
		super.onResume();
		challengeUserAuthorization();
	}
	
	public void challengeUserAuthorization() {

		if(!HailCaesarSession.getInstance().getSecurityManager().isLoggedIn()) {
			goHomeOnFinish = true;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
			alert.setTitle(getString(R.string.dialog_login_title));
			alert.setMessage(getString(R.string.dialog_login_message));
	
			final EditText password = new EditText(this);
			password.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			alert.setView(password);
	
			alert.setPositiveButton(getString(R.string.dialog_login_button_authorize), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String pass = password.getText().toString();
					Boolean success = HailCaesarSession.getInstance().getSecurityManager().challengeUser(getApplicationContext(), pass);
					if(success) {
						dialog.cancel();
					} else {
						finish();
					}
				}
			});
	
			alert.setNegativeButton(getString(R.string.dialog_login_button_exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				});
	
			final AlertDialog dialog = alert.show();
			final Resources res = getResources();
		    final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
		    final View titleDivider = dialog.findViewById(titleDividerId);
		    titleDivider.setBackgroundColor(res.getColor(R.color.theme_main));
		    
		    int alertTitleId = res.getIdentifier("alertTitle", "id", "android");
	        TextView alertTitle = (TextView) dialog.getWindow().getDecorView().findViewById(alertTitleId);
	        alertTitle.setTextColor(res.getColor(R.color.theme_main)); 
			
		}
	}
	
	protected void done() {
		if(goHomeOnFinish) {
			Intent i = new Intent(ImportActivity.this, LandingActivity.class);
            startActivity(i);
            finish();
		} else {
			finish();
		}
	}
	
	public void onBackPressed() {
		done();
	}
	
	
	protected String readMessageFile(Uri data) {
		final String scheme = data.getScheme();
		StringBuffer buf = new StringBuffer();
		buf.append("");

		if (ContentResolver.SCHEME_FILE.equals(scheme) || ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			try {
				ContentResolver cr = getApplicationContext().getContentResolver();
				InputStream is = cr.openInputStream(data);
				if (is == null) {
					return "";
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String str;
				if (is != null) {
					while ((str = reader.readLine()) != null) {
						buf.append(str);
					}
				}
				is.close();

			} catch (Exception e) {
				Log.e("ImportActivity", "readDataFile", e);
			}
		}

		return buf.toString();
	}
	
	protected String readDataFile(Uri data) {
		final String scheme = data.getScheme();
		StringBuffer buf = new StringBuffer();
		String decrypted = "";
		buf.append("");

		if (ContentResolver.SCHEME_FILE.equals(scheme) || ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			try {
				ContentResolver cr = getApplicationContext().getContentResolver();
				InputStream is = cr.openInputStream(data);
				if (is == null) {
					return "";
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String str;
				if (is != null) {
					while ((str = reader.readLine()) != null) {
						buf.append(str);
					}
					
					decrypted = DawsonCipher.decrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), buf.toString());
				}
				is.close();

			} catch (Exception e) {
				Log.e("ImportActivity", "readDataFile", e);
			}
		}

		return decrypted;
	}
}
