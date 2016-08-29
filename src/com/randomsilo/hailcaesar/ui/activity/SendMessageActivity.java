package com.randomsilo.hailcaesar.ui.activity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.Utility;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.model.Lock;
import com.randomsilo.hailcaesar.ui.adapter.KeyListSpinnerAdapter;
import com.randomsilo.hailcaesar.ui.adapter.LockListSpinnerAdapter;
import com.randomsilo.hailcaesar.ui.dialog.SaveMessageDialog;

public class SendMessageActivity extends Activity {
	Spinner lockSpinner;
	Spinner keySpinner;
	EditText message;
	String encryptedMessage = "";
	
	String fileNamePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);
		
		HailCaesarSession.getInstance().Init(this);
		
		List<Lock> locks = HailCaesarSession.getInstance().getLockService().getLockList();
		lockSpinner = (Spinner)findViewById(R.id.LockSpinner);
		lockSpinner.setAdapter(new LockListSpinnerAdapter(this, locks));
		
		List<Key> keys = HailCaesarSession.getInstance().getKeyService().getKeyList();
		keySpinner = (Spinner)findViewById(R.id.KeySpinner);
		keySpinner.setAdapter(new KeyListSpinnerAdapter(this, keys));
		
		message = (EditText)findViewById(R.id.Message);
	}
	
	public void onResume() {
		super.onResume();
		Utility.SetLockSpinnerItemByUuid(lockSpinner, HailCaesarSession.getInstance().getLockLast());
		Utility.SetKeySpinnerItemByUuid(keySpinner, HailCaesarSession.getInstance().getKeyLast());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.msg_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_home) {
			Intent intent = new Intent(this, LandingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		
		if (id == R.id.action_save) {
			try {
				if( readyForEncryption()) {
					File msgFile = saveMessageToDownload();
					fileNamePath = msgFile.getAbsolutePath();
					SaveMessageDialog.DisplaySaveMessageDialog(fileNamePath, encryptedMessage, this);
				}
			} catch(Exception e) {
				Log.e("SendMessageActivity", "SaveMessageFile", e);
				Toast.makeText(getApplicationContext(), "Failed to Save Message", Toast.LENGTH_LONG).show();
			}
		}

		
		return super.onOptionsItemSelected(item);
	}
	
	private boolean readyForEncryption() {
		boolean ready = false;
		
		Lock lock = (Lock)lockSpinner.getSelectedItem();
		Key key = (Key)keySpinner.getSelectedItem();
		
		if( lock == null || key == null) {
			Toast.makeText(getApplicationContext(), "Please select a Lock and Key", Toast.LENGTH_LONG).show();
		} else {
			ready = true;
		}
		
		return ready;
	}
	
	private void SetEncryptedMessage() {
		String msg = message.getText().toString();
		
		// Encrypt Message
		Lock lock = (Lock)lockSpinner.getSelectedItem();
		Key key = (Key)keySpinner.getSelectedItem();
		
		HailCaesarSession.getInstance().setLastLockKey(lock.getId(), key.getId());
		
		lock.useForMessaging();
		try {
			encryptedMessage = DawsonCipher.encrypt(lock, key, msg);
		} catch (Exception e) {
			Log.e("SendMessageActivity", "sendMessage", e);
		}
	}
	
	private File saveMessageToDownload() throws IOException {
	
		SetEncryptedMessage();
		
		File msgFile = null;
		
		if(fileNamePath == null) {
			File downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if(downloadPath.getName().contains(GlobalSettings.FolderDownload)) {
				downloadPath = new File(downloadPath, GlobalSettings.FolderMessages);
			}
			
			String fileName = UUID.randomUUID().toString() + GlobalSettings.ExtensionMessage;
			msgFile = new File(downloadPath, fileName);
			msgFile.getParentFile().mkdirs();
			msgFile.createNewFile();
			fileNamePath = msgFile.getAbsolutePath();
			
		} else {
			msgFile = new File(fileNamePath);
		}
		
		if (msgFile.exists()) {
			msgFile.delete();
		}

		PrintWriter out;
		out = new PrintWriter(msgFile);
		out.println(encryptedMessage);
		out.flush();
		out.close();
		
		return msgFile;
	}
	
	public void sendMessage(View view) {
		
		if( !readyForEncryption()) {
			return;
		}
		
		// Set Encrypted Message
		SetEncryptedMessage();
		
		// Store Message
		Uri messageUri = HailCaesarSession.getInstance().getMessageService().storeMessage(encryptedMessage, this);
		
		// Lock and Key
		Lock lock = (Lock)lockSpinner.getSelectedItem();
		Key key = (Key)keySpinner.getSelectedItem();
		
		// Share File
		Intent shareIntent = new Intent();
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("*/*");
		shareIntent.putExtra(Intent.EXTRA_STREAM, messageUri);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.hail_caesar_subject));
		shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.hail_caesar_disclaimer) + "\n\nLock: " + lock.getName() + "\n Key: " + key.getName());
		startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.hail_caesar_message)));
		
		// Exit
		finish();
	}
	
	public void clearMessage(View view) {
		message.setText("");
	}
}
