package com.randomsilo.hailcaesar.ui.activity;

import java.util.UUID;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Lock;

public class LockEditActivity extends ImportActivity {

	private EditText lockNameEditText;
	private TextView lockUuidTextView;
	private Button saveBtn;
	private Button removeBtn;
	private Lock lock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_edit);
		
		HailCaesarSession.getInstance().Init(this);
		
		lockNameEditText = (EditText) findViewById(R.id.LockName);
		lockUuidTextView = (TextView) findViewById(R.id.LockUuid);
		saveBtn = (Button) findViewById(R.id.SaveBtn);
		removeBtn = (Button) findViewById(R.id.RemoveBtn);
		
		
		Uri data = getIntent().getData();
		if (data != null) {
			getIntent().setData(null);
			goHomeOnFinish = true;
			try {
				String lockJson = readDataFile(data);
				lock = Lock.fromJson(lockJson);
				lockNameEditText.setText(lock.getName());
				lockUuidTextView.setText(lock.getId().toString());
			} catch (Exception e) {
				saveBtn.setEnabled(false);
				removeBtn.setEnabled(false);
				Log.e("GetMessageActivity", "onCreate", e);
				Toast.makeText(getApplicationContext(), getString(R.string.message_open_error), Toast.LENGTH_LONG).show();
			}
			
		} else {
		
			Bundle b = getIntent().getExtras();
			if(b != null && b.getString("lockUuid") != null) {
				String lockUuid = b.getString("lockUuid");
				UUID id = UUID.fromString(lockUuid);
				lock = HailCaesarSession.getInstance().getLockService().get(id);
				
				if(lock != null) {
					lockNameEditText.setText(lock.getName());
					lockUuidTextView.setText(lock.getId().toString());
				} else {
					lockNameEditText.setText("");
					lockUuidTextView.setText("Not Found");
					saveBtn.setEnabled(false);
					removeBtn.setEnabled(false);
					Toast.makeText(getApplicationContext(), "Missing Lock: " + lockUuid, Toast.LENGTH_LONG).show();
				}
			} else {
				String lockName = "New"; 
				try {
					lock = DawsonCipher.generateLock(lockName);
					removeBtn.setEnabled(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				lockNameEditText.setText(lockName);
			}
		}
		
	}
	
	public void save(View view) {
		save();
	}
	
	public void save() {
		lock.setName(lockNameEditText.getText().toString());
		Boolean success = HailCaesarSession.getInstance().getLockService().save(lock);
		if(success) {
			Toast.makeText(getApplicationContext(), "Lock " + lock.getName() + " is saved", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to save lock " + lock.getName(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void export(View view) {
		save();
		Boolean success = HailCaesarSession.getInstance().getLockService().export(lock);
		if(success) {
			Toast.makeText(getApplicationContext(), "Lock " + lock.getName() + " is exported", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to export lock " + lock.getName(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void remove(View view) {
		Boolean success = HailCaesarSession.getInstance().getLockService().remove(lock);
		if(success) {
			Toast.makeText(getApplicationContext(), "Lock " + lock.getName() + " is removed", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to remove lock " + lock.getName(), Toast.LENGTH_SHORT).show();
		}
	}

}
