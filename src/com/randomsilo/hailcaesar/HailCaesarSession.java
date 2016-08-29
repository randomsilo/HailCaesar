package com.randomsilo.hailcaesar;

import java.util.UUID;

import android.app.Activity;

import com.randomsilo.hailcaesar.service.IKeyService;
import com.randomsilo.hailcaesar.service.ILockService;
import com.randomsilo.hailcaesar.service.IMessageService;
import com.randomsilo.hailcaesar.service.ISecurityManager;
import com.randomsilo.hailcaesar.service.impl.KeyServicePrivateApp;
import com.randomsilo.hailcaesar.service.impl.LockServicePrivateApp;
import com.randomsilo.hailcaesar.service.impl.MessageServicePrivateApp;
import com.randomsilo.hailcaesar.service.impl.SecurityManagerPrivateApp;

public class HailCaesarSession {

	private final static HailCaesarSession INSTANCE = new HailCaesarSession();
	private ILockService lockService;
	private IKeyService keyService;
	private ISecurityManager securityManager;
	private IMessageService messageService;
	
	private UUID lockLast;
	private UUID keyLast;

	private HailCaesarSession() {
		lockLast = UUID.randomUUID();
		keyLast = UUID.randomUUID();
		
		lockService = new LockServicePrivateApp();
		keyService = new KeyServicePrivateApp();
		securityManager = new SecurityManagerPrivateApp();
		messageService = new MessageServicePrivateApp();
	}
	
	public void Init(Activity activity) {
		getLockService().setActivity(activity);
		getKeyService().setActivity(activity);
		getSecurityManager().setActivity(activity);
		
		getLockService().refreshList();
		getKeyService().refreshList();
		getSecurityManager().refreshIdentity();
	}
	
	public void Distress(Activity activity) {
		lockLast = UUID.randomUUID();
		keyLast = UUID.randomUUID();
		
		messageService.clear(activity);
		
		lockService.clearAll();
		keyService.clearAll();
		securityManager.clearAll();
		
		activity.finish();
	}
	
	public static HailCaesarSession getInstance() {
		return INSTANCE;
	}

	public ILockService getLockService() {
		return lockService;
	}
	
	public IKeyService getKeyService() {
		return keyService;
	}
	
	public ISecurityManager getSecurityManager() {
		return securityManager;
	}
	
	public IMessageService getMessageService() {
		return messageService;
	}
	
	public Boolean requireLogin() {
		Boolean foundPassword = false;
		
		if(securityManager.getIdentity().getSafePassword().length() > 0) {
			foundPassword = true;
		}
		
		return foundPassword;
	}

	public UUID getLockLast() {
		return lockLast;
	}

	public void setLockLast(UUID lockLast) {
		this.lockLast = lockLast;
	}

	public UUID getKeyLast() {
		return keyLast;
	}

	public void setKeyLast(UUID keyLast) {
		this.keyLast = keyLast;
	}
	
	public void setLastLockKey(UUID lock, UUID key) {
		lockLast = lock;
		keyLast = key;
	}
}
