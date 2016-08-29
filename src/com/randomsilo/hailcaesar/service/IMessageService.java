package com.randomsilo.hailcaesar.service;

import android.app.Activity;
import android.net.Uri;

public interface IMessageService {
	Uri storeMessage(String encryptedMessage, Activity activity);
	void clear(Activity activity);
}
