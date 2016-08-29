package com.randomsilo.hailcaesar.service.impl;

import java.io.File;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.Utility;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Identity;
import com.randomsilo.hailcaesar.model.MasterKey;
import com.randomsilo.hailcaesar.model.MasterLock;
import com.randomsilo.hailcaesar.service.ISecurityManager;
import com.randomsilo.hailcaesar.ui.activity.LandingActivity;

public class SecurityManagerPrivateApp implements ISecurityManager {
	private Identity identity;
	private Activity activity;
	
	private boolean loggedIn = false;
	
	public SecurityManagerPrivateApp() {

	}
	
	@Override
	public Identity getIdentity() {
		return identity;
	}

	@Override
	public void authenticate(Context context, String password) {
		if(identity.getSafePassword().equals(password)) {
			loggedIn = true;
			Intent intent = new Intent(context, LandingActivity.class);
			context.startActivity(intent);
			((Activity)context).finish();	
		} else if(identity.getDistressPassword().equals(password)) {
			HailCaesarSession.getInstance().Distress(activity);
		} else {
			Toast.makeText(context.getApplicationContext(), "Invalid Password!", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public Boolean challengeUser(Context context, String password) {
		if(identity.getSafePassword().equals(password)) {
			loggedIn = true;
			return true;	
		} else if(identity.getDistressPassword().equals(password)) {
			Toast.makeText(context.getApplicationContext(), "Distress Password Detected", Toast.LENGTH_LONG).show();
			HailCaesarSession.getInstance().Distress(activity);
			return false;
		} else {
			Toast.makeText(context.getApplicationContext(), "Invalid Password!", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public Boolean isLoggedIn() {
		if(identity.getSafePassword().length() == 0) {
			return true;
		} else {
			return loggedIn;
		}
	}

	@Override
	public void refreshIdentity() {
		identity = new Identity();
		File keyPath = new File(activity.getFilesDir(), GlobalSettings.FolderIdentity);
		
		if(keyPath.isDirectory()) {
			File[] listOfFiles = keyPath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionIdentity)) {
					identity = getIdentity(f);
					break;
				}
			}
		}
	}
	
	private Identity getIdentity(File file) {
		Identity i = new Identity();
		
		try {
			String data = Utility.ReadFile(file);
			i = Identity.fromJson(data);
		} catch(Exception e) {
			Log.e("SecurityManagerPrivateApp", "getIdentity", e);
		}
		
		return i;
	}

	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
			
	@Override
	public Boolean save(Identity identity) {
		Boolean success = false;
		try {
			File identityPath = new File(activity.getFilesDir(), GlobalSettings.FolderIdentity);
			String fileName = GlobalSettings.FileNameIdentity + GlobalSettings.ExtensionIdentity;
			File identityFile = new File(identityPath, fileName);
			identityFile.getParentFile().mkdirs();
			if (identityFile.exists()) {
				identityFile.delete();
			}
			
			identityFile.createNewFile();
			
			PrintWriter out;
			out = new PrintWriter(identityFile);
			String encrypted = DawsonCipher.encrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), identity.toJson().toString());
			out.println(encrypted);
			out.flush();
			out.close();
			
			success = identityFile.exists();
			if(success) {
				loggedIn = true;
			}
			
		} catch (Exception e) {
			Log.e("LockService", "save exception", e);
		}
		
		return success;
	}

	@Override
	public void clearAll() {
		try {
			identity = new Identity();
			
			File identityPath = new File(activity.getFilesDir(), GlobalSettings.FolderIdentity);
			String fileName = GlobalSettings.FileNameIdentity + GlobalSettings.ExtensionIdentity;
			File identityFile = new File(identityPath, fileName);
			identityFile.delete();
			identityPath.delete();
		} catch (Exception e) {
			Log.e("LockService", "clearAll exception", e);
		}
		
	}

	@Override
	public Boolean isEulaAccepted() {
		return identity.isEulaAccepted();
	}

}
