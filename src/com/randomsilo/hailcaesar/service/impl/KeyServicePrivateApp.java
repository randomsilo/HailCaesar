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
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.model.MasterKey;
import com.randomsilo.hailcaesar.model.MasterLock;
import com.randomsilo.hailcaesar.service.IKeyService;

public class KeyServicePrivateApp implements IKeyService {
	private Map<UUID,Key> objects = new HashMap<UUID,Key>();
	private Activity activity;
	
	public KeyServicePrivateApp() {
		
	}
	
	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void refreshList() {
		objects = new HashMap<UUID,Key>();
		File keyPath = new File(activity.getFilesDir(), GlobalSettings.FolderKeys);
		
		if(keyPath.isDirectory()) {
			File[] listOfFiles = keyPath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionKey)) {
					Key key = getKey(f);
					if(key != null) {
						objects.put(key.getId(), key);
					} else {
						f.delete();  // remove bad keys
					}
				}
			}
		}
	}
	
	private Key getKey(File file) {
		Key k = new Key(file.getName());
		
		try {
			String data = Utility.ReadFile(file);
			k = Key.fromJson(data);
		} catch(Exception e) {
			k = null;
			Log.e("KeyServicePrivateApp", "getKey exception", e);
		}
		
		return k;
	}
	
	@Override
	public Map<UUID,Key> getKeyMap() {
		return objects;
	}
	
	@Override
	public List<Key> getKeyList() {
	
		List<Key> list = new ArrayList<Key>(objects.values());
	    Collections.sort(list, new Comparator<Key>() {

	        public int compare(Key o1, Key o2) {
	            return o1.getName().compareTo(o2.getName());
	        }
	    });
		
		return list;
	}

	@Override
	public Key create(String name) {
		Key key = null;
		try {
			key = DawsonCipher.generateKey(name);
			objects.put(key.getId(), key);
		} catch (Exception e) {
			Log.e("KeyServicePrivateApp", "create exception", e);
		}

		return key;
	}

	@Override
	public Boolean save(Key key) {
		try {
			File keyPath = new File(activity.getFilesDir(), GlobalSettings.FolderKeys);
			String fileName = key.getId().toString() + GlobalSettings.ExtensionKey;
			File keyFile = new File(keyPath, fileName);
			keyFile.getParentFile().mkdirs();
			if (keyFile.exists()) {
				keyFile.delete();
			}
			
			keyFile.createNewFile();
			
			PrintWriter out;
			out = new PrintWriter(keyFile);
			String json = key.toJson().toString();
			String encrypted = DawsonCipher.encrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), json);
			out.println(encrypted);
			out.flush();
			out.close();
			
			objects.put(key.getId(), key);
		} catch (Exception e) {
			Log.e("KeyServicePrivateApp", "save exception", e);
		}
		
		return objects.containsKey(key.getId());
	}
	
	@Override
	public Boolean export(Key key) {
		Boolean fileFound = false;
		
		try {
			File keyPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if(keyPath.getName().contains(GlobalSettings.FolderDownload)) {
				keyPath = new File(keyPath, GlobalSettings.FolderKeys);
			}
			String fileName = key.getName() + GlobalSettings.ExtensionKey;
			
			File keyFile = new File(keyPath, fileName);
			keyFile.getParentFile().mkdirs();
			if (keyFile.exists()) {
				keyFile.delete();
			}
			
			keyFile.createNewFile();
			
			PrintWriter out;
			out = new PrintWriter(keyFile);
			String json = key.toJson().toString();
			String encrypted = DawsonCipher.encrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), json);
			out.println(encrypted);
			out.flush();
			out.close();
			
			fileFound = keyFile.exists();
			
		} catch (Exception e) {
			Log.e("KeyService", "export exception", e);
		}
		
		return fileFound;
	}

	@Override
	public Boolean remove(Key key) {
		try {
			File keyPath = new File(activity.getFilesDir(), GlobalSettings.FolderKeys);
			
			if(keyPath.isDirectory()) {
				File[] listOfFiles = keyPath.listFiles();
				for(File f : listOfFiles) {
					if(f.getName().endsWith(GlobalSettings.ExtensionKey)) {
						String fileName = key.getId().toString() + GlobalSettings.ExtensionKey;
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
		
		return !objects.containsKey(key.getId());
	}

	@Override
	public Key get(String name) {
		Key foundKey = null;
		for(UUID id : objects.keySet()) {
			Key key = objects.get(id);
			if(key.getName().equals(name)) {
				foundKey = key;
			}
		}
		
		return foundKey;
	}

	@Override
	public Key get(UUID id) {
		Key key = null;
		if(objects.containsKey(id)) {
			key = objects.get(id);
		}
		
		return key;
	}

	@Override
	public void clearAll() {
		objects = new HashMap<UUID,Key>();
		File keyPath = new File(activity.getFilesDir(), GlobalSettings.FolderKeys);
		
		if(keyPath.isDirectory()) {
			File[] listOfFiles = keyPath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionKey)) {
					f.delete();
				}
			}
		}
		keyPath.delete();
	}

}
