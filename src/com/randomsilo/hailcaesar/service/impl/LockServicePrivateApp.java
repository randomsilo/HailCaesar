package com.randomsilo.hailcaesar.service.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.Utility;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Lock;
import com.randomsilo.hailcaesar.model.MasterKey;
import com.randomsilo.hailcaesar.model.MasterLock;
import com.randomsilo.hailcaesar.service.ILockService;

public class LockServicePrivateApp implements ILockService {
	private Map<UUID,Lock> objects = new HashMap<UUID,Lock>();
	private Activity activity;
	
	public LockServicePrivateApp() {
		
	}
	
	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void refreshList() {
		objects = new HashMap<UUID,Lock>();
		File lockPath = new File(activity.getFilesDir(), GlobalSettings.FolderLocks);
		
		if(lockPath.isDirectory()) {
			File[] listOfFiles = lockPath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionLock)) {
					Lock lock = getLock(f);
					if(lock != null) {
						objects.put(lock.getId(), lock);
					} else {
						f.delete();  // remove bad locks
					}
				}
			}
		}
	}
	
	private Lock getLock(File file) {
		Lock l = new Lock(file.getName());
		
		try {
			String data = Utility.ReadFile(file);
			l = Lock.fromJson(data);
		} catch(Exception e) {
			l = null;
			Log.e("LockServicePrivateApp", "getLock exception", e);
		}
		
		return l;
	}
	
	@Override
	public Map<UUID,Lock> getLockMap() {
		return objects;
	}
	
	@Override
	public List<Lock> getLockList() {
		
		List<Lock> list = new ArrayList<Lock>(objects.values());
	    Collections.sort(list, new Comparator<Lock>() {

	        public int compare(Lock o1, Lock o2) {
	            return o1.getName().compareTo(o2.getName());
	        }
	    });
		
		return list;
	}

	@Override
	public Lock create(String name) {
		Lock lock = null;
		try {
			lock = DawsonCipher.generateLock(name);
			save(lock);
		} catch (Exception e) {
			Log.e("LockService", "create exception", e);
		}

		return lock;
	}

	@Override
	public Boolean save(Lock lock) {
		
		try {
			File lockPath = new File(activity.getFilesDir(), GlobalSettings.FolderLocks);
			String fileName = lock.getId().toString() + GlobalSettings.ExtensionLock;
			File lockFile = new File(lockPath, fileName);
			lockFile.getParentFile().mkdirs();
			if (lockFile.exists()) {
				lockFile.delete();
			}
			
			lockFile.createNewFile();
			
			PrintWriter out;
			out = new PrintWriter(lockFile);
			String json = lock.toJson().toString();
			String encrypted = DawsonCipher.encrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), json);
			out.println(encrypted);
			out.flush();
			out.close();
			
			objects.put(lock.getId(), lock);
		} catch (Exception e) {
			Log.e("LockService", "save exception", e);
		}
		
		return objects.containsKey(lock.getId());
	}

	
	@Override
	public Boolean remove(Lock lock) {
		try {
			File lockPath = new File(activity.getFilesDir(), GlobalSettings.FolderLocks);
			
			if(lockPath.isDirectory()) {
				File[] listOfFiles = lockPath.listFiles();
				for(File f : listOfFiles) {
					if(f.getName().endsWith(GlobalSettings.ExtensionLock)) {
						String fileName = lock.getId().toString() + GlobalSettings.ExtensionLock;
						if(f.getName().equals(fileName)) {
							f.delete();
						}
					}
				}
			}
					
			refreshList();
		} catch (Exception e) {
			Log.e("LockService", "save exception", e);
		}
		
		return !objects.containsKey(lock.getId());
	}

	@Override
	public Lock get(String name) {
		Lock foundLock = null;
		for(UUID id : objects.keySet()) {
			Lock lock = objects.get(id);
			if(lock.getName().equals(name)) {
				foundLock = lock;
			}
		}
		
		return foundLock;
	}

	@Override
	public Lock get(UUID id) {
		Lock lock = null;
		if(objects.containsKey(id)) {
			lock = objects.get(id);
		}
		
		return lock;
	}

	@Override
	public Boolean export(Lock lock) {
		Boolean fileFound = false;
		
		try {
			File lockPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if(lockPath.getName().contains(GlobalSettings.FolderDownload)) {
				lockPath = new File(lockPath, GlobalSettings.FolderLocks);
			}

			String fileName = lock.getName() + GlobalSettings.ExtensionLock;
			File lockFile = new File(lockPath, fileName);
			lockFile.getParentFile().mkdirs();
			if (lockFile.exists()) {
				lockFile.delete();
			}
			
			lockFile.createNewFile();
			
			PrintWriter out;
			out = new PrintWriter(lockFile);
			String json = lock.toJson().toString();
			String encrypted = DawsonCipher.encrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), json);
			out.println(encrypted);
			out.flush();
			out.close();
			
			fileFound = lockFile.exists();
			
		} catch (Exception e) {
			Log.e("LockService", "export exception", e);
		}
		
		return fileFound;
	}

	@Override
	public void clearAll() {
		objects = new HashMap<UUID,Lock>();
		File lockPath = new File(activity.getFilesDir(), GlobalSettings.FolderLocks);
		
		if(lockPath.isDirectory()) {
			File[] listOfFiles = lockPath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionLock)) {
					f.delete();
				}
			}
		}
		lockPath.delete();
	}

}
