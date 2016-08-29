package com.randomsilo.hailcaesar.service;

import android.app.Activity;
import android.content.Context;

import com.randomsilo.hailcaesar.model.Identity;

public interface ISecurityManager {
	Identity getIdentity();
	Boolean save(Identity identity);
	void authenticate(Context context, String password);
	Boolean challengeUser(Context context, String password);
	Boolean isLoggedIn();
	Boolean isEulaAccepted();
	void refreshIdentity();
	void setActivity(Activity activity);
	void clearAll();
}
