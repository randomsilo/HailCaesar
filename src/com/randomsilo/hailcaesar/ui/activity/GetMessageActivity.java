package com.randomsilo.hailcaesar.ui.activity;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.Utility;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.model.Lock;
import com.randomsilo.hailcaesar.ui.adapter.KeyListSpinnerAdapter;
import com.randomsilo.hailcaesar.ui.adapter.LockListSpinnerAdapter;
import com.randomsilo.hailcaesar.ui.dialog.SaveMessageDialog;

public class GetMessageActivity extends ImportActivity {
	String originalMessage;
	Spinner lockSpinner;
	Spinner keySpinner;
	EditText message;
	Button decrypt;

	String fileNamePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_message);

		HailCaesarSession.getInstance().Init(this);

		List<Lock> locks = HailCaesarSession.getInstance().getLockService()
				.getLockList();
		lockSpinner = (Spinner) findViewById(R.id.LockSpinner);
		lockSpinner.setAdapter(new LockListSpinnerAdapter(this, locks));

		List<Key> keys = HailCaesarSession.getInstance().getKeyService()
				.getKeyList();
		keySpinner = (Spinner) findViewById(R.id.KeySpinner);
		keySpinner.setAdapter(new KeyListSpinnerAdapter(this, keys));

		message = (EditText) findViewById(R.id.Message);
		decrypt = (Button) findViewById(R.id.DecryptBtn);

		Uri data = getIntent().getData();
		if (data != null) {
			getIntent().setData(null);
			try {
				goHomeOnFinish = true;
				originalMessage = readMessageFile(data);
				message.setText(originalMessage);
				fileNamePath = data.getPath();

			} catch (Exception e) {
				Log.e("GetMessageActivity", "onCreate", e);
				Toast.makeText(getApplicationContext(),
						getString(R.string.message_open_error),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	public void onBackPressed() {
		HailCaesarSession.getInstance().getMessageService().clear(this);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void onResume() {
		super.onResume();
		Utility.SetLockSpinnerItemByUuid(lockSpinner, HailCaesarSession
				.getInstance().getLockLast());
		Utility.SetKeySpinnerItemByUuid(keySpinner, HailCaesarSession
				.getInstance().getKeyLast());
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
			SaveMessageDialog.DisplaySaveMessageDialog(fileNamePath, originalMessage, this);
		}

		return super.onOptionsItemSelected(item);
	}

	public void decryptMessage(View view) {
		Lock lock = (Lock) lockSpinner.getSelectedItem();
		Key key = (Key) keySpinner.getSelectedItem();

		HailCaesarSession.getInstance().setLastLockKey(lock.getId(),
				key.getId());

		String decryptedMessage = "";
		lock.useForMessaging();
		try {
			decryptedMessage = DawsonCipher.decrypt(lock, key, originalMessage);
			message.setText(decryptedMessage);
		} catch (Exception e) {
			Log.i("GetMessageActivity",
					"decryptMessage - normal exception if using wrong lock/key",
					e);
			Toast.makeText(getApplicationContext(),
					getString(R.string.message_decrypt_error),
					Toast.LENGTH_LONG).show();
		}
	}

	public void resetMessage(View view) {
		message.setText(originalMessage);
	}

	public void replyMessage(View view) {
		Intent intent = new Intent(this, SendMessageActivity.class);
		startActivity(intent);
	}
	
	
}