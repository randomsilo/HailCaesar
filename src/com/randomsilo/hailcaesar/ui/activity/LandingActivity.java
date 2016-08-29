package com.randomsilo.hailcaesar.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.model.Identity;

public class LandingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        
        challengeEulaAuthorization();
    }
    
    public void navigateLocks(View v) {
    	Intent intent = new Intent(this, LockListActivity.class);
        startActivity(intent);
    }
    
    public void navigateKeys(View v) {
    	Intent intent = new Intent(this, KeyListActivity.class);
        startActivity(intent); 	
    }
    
    public void navigateMessages(View v) {
    	Intent intent = new Intent(this, SendMessageActivity.class);
        startActivity(intent);    	
    }
    
    public void navigateIdentity(View v) {
    	Intent i = new Intent(this, IdentityActivity.class);
        startActivity(i);
    }
    
    public void onBackPressed() {
		HailCaesarSession.getInstance().getMessageService().clear(this);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
    
    public void challengeEulaAuthorization() {

		if(!HailCaesarSession.getInstance().getSecurityManager().isEulaAccepted()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setCancelable(false);
	
			alert.setTitle(getString(R.string.dialog_eula_title));
			alert.setMessage(
					getString(R.string.dialog_eula_message_start)
					+ "\n\n" + getString(R.string.dialog_eula_message_part1)
					+ "\n" + getString(R.string.dialog_eula_message_part2)
					+ "\n" + getString(R.string.dialog_eula_message_part3)
					+ "\n" + getString(R.string.dialog_eula_message_part4)
					+ "\n" + getString(R.string.dialog_eula_message_part5)
					+ "\n" + getString(R.string.dialog_eula_message_part6)
					+ "\n\n" + getString(R.string.dialog_eula_message_end)
					+ "\n\n" + getString(R.string.dialog_eula_message_thankyou)
			);
	
			alert.setPositiveButton(getString(R.string.dialog_eula_button_authorize), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Identity i = HailCaesarSession.getInstance().getSecurityManager().getIdentity();
					i.setEulaAccepted(true);
					HailCaesarSession.getInstance().getSecurityManager().save(i);
					dialog.cancel();
				}
			});
	
			alert.setNegativeButton(getString(R.string.dialog_eula_button_exit),
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

}
