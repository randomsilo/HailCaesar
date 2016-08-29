package com.randomsilo.hailcaesar.service.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.UUID;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.service.IMessageService;

public class MessageServicePrivateApp implements IMessageService {
	
	@Override
	public Uri storeMessage(String encryptedMessage, Activity activity) {
		Uri messageUri = null;
		
		try {
			
			File messagePath = new File(activity.getFilesDir(), GlobalSettings.FolderMessages);
			String fileName = UUID.randomUUID().toString() + GlobalSettings.ExtensionMessage;
			File messageFile = new File(messagePath, fileName);
			messageFile.getParentFile().mkdirs();
			if (messageFile.exists()) {
				messageFile.delete();
			}
			
			messageFile.createNewFile();
			PrintWriter out;
			out = new PrintWriter(messageFile);
			out.println(encryptedMessage);
			out.flush();
			out.close();
			
			messageUri = FileProvider.getUriForFile(activity.getApplicationContext(), "com.randomsilo.hailcaesar.fileprovider", messageFile);
		} catch (Exception e) {
			Log.e("InternalStorageMessageService", "storeMessage exception", e);
		}
		
		return messageUri;
	}

	@Override
	public void clear(Activity activity) {
		File messagePath = new File(activity.getFilesDir(), GlobalSettings.FolderMessages);
		
		if(messagePath.isDirectory()) {
			File[] listOfFiles = messagePath.listFiles();
			for(File f : listOfFiles) {
				if(f.getName().endsWith(GlobalSettings.ExtensionMessage)) {
					f.delete();
				}
			}
		}
		messagePath.delete();
	}

}
