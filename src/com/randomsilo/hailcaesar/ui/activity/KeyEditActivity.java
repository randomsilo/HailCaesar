package com.randomsilo.hailcaesar.ui.activity;

import java.util.Random;
import java.util.UUID;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Key;

public class KeyEditActivity extends ImportActivity {
	private static Random random = new Random();
	private EditText keyNameEditText;
	private EditText ward0;
	private EditText ward1;
	private EditText ward2;
	private EditText ward3;
	private EditText ward4;
	private EditText ward5;
	private EditText ward6;
	private EditText ward7;
	private EditText ward8;
	private EditText ward9;
	private TextView keyUuidTextView;
	private Button saveBtn;
	private Button removeBtn;
	private Key key;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_edit);
		
		HailCaesarSession.getInstance().Init(this);
		
		keyNameEditText = (EditText) findViewById(R.id.KeyName);
		ward0 = (EditText) findViewById(R.id.Ward0);
		ward1 = (EditText) findViewById(R.id.Ward1);
		ward2 = (EditText) findViewById(R.id.Ward2);
		ward3 = (EditText) findViewById(R.id.Ward3);
		ward4 = (EditText) findViewById(R.id.Ward4);
		ward5 = (EditText) findViewById(R.id.Ward5);
		ward6 = (EditText) findViewById(R.id.Ward6);
		ward7 = (EditText) findViewById(R.id.Ward7);
		ward8 = (EditText) findViewById(R.id.Ward8);
		ward9 = (EditText) findViewById(R.id.Ward9);
		keyUuidTextView = (TextView) findViewById(R.id.KeyUuid);
		saveBtn = (Button) findViewById(R.id.SaveBtn);
		removeBtn = (Button) findViewById(R.id.RemoveBtn);
		
		
		Uri data = getIntent().getData();
		if (data != null) {
			getIntent().setData(null);
			goHomeOnFinish = true;
			try {
				String keyJson = readDataFile(data);
				key = Key.fromJson(keyJson);
				keyNameEditText.setText(key.getName());
				keyUuidTextView.setText(key.getId().toString());
				ward0.setText(key.getWard(0)+"");
				ward1.setText(key.getWard(1)+"");
				ward2.setText(key.getWard(2)+"");
				ward3.setText(key.getWard(3)+"");
				ward4.setText(key.getWard(4)+"");
				ward5.setText(key.getWard(5)+"");
				ward6.setText(key.getWard(6)+"");
				ward7.setText(key.getWard(7)+"");
				ward8.setText(key.getWard(8)+"");
				ward9.setText(key.getWard(9)+"");
				
			} catch (Exception e) {
				saveBtn.setEnabled(false);
				removeBtn.setEnabled(false);
				Log.e("GetMessageActivity", "onCreate", e);
				Toast.makeText(getApplicationContext(), getString(R.string.message_open_error), Toast.LENGTH_LONG).show();
			}
			
		} else {
		
		
			Bundle b = getIntent().getExtras();
			if(b != null && b.getString("keyUuid") != null) {
				String keyUuid = b.getString("keyUuid");
				UUID id = UUID.fromString(keyUuid);
				key = HailCaesarSession.getInstance().getKeyService().get(id);
				
				if(key != null) {
					keyNameEditText.setText(key.getName());
					keyUuidTextView.setText(key.getId().toString());
					try {
						ward0.setText(key.getWard(0)+"");
						ward1.setText(key.getWard(1)+"");
						ward2.setText(key.getWard(2)+"");
						ward3.setText(key.getWard(3)+"");
						ward4.setText(key.getWard(4)+"");
						ward5.setText(key.getWard(5)+"");
						ward6.setText(key.getWard(6)+"");
						ward7.setText(key.getWard(7)+"");
						ward8.setText(key.getWard(8)+"");
						ward9.setText(key.getWard(9)+"");
					} catch (Exception e) {
						Log.e("KeyEditActivity", "Setting Wards Exception", e);
					}
				} else {
					keyNameEditText.setText("");
					keyUuidTextView.setText("Not Found");
					ward0.setText("00");
					ward1.setText("00");
					ward2.setText("00");
					ward3.setText("00");
					ward4.setText("00");
					ward5.setText("00");
					ward6.setText("00");
					ward7.setText("00");
					ward8.setText("00");
					ward9.setText("00");
					saveBtn.setEnabled(false);
					removeBtn.setEnabled(false);
					Toast.makeText(getApplicationContext(), "Missing Key: " + keyUuid, Toast.LENGTH_LONG).show();
				}
			} else {
				String keyName = "New"; 
				try {
					key = DawsonCipher.generateKey(keyName);
					removeBtn.setEnabled(false);
				} catch (Exception e) {
					Log.e("KeyEditActivity", "New Key Generation Exception", e);
				}
				
				keyNameEditText.setText(key.getName());
				keyUuidTextView.setText(key.getId().toString());
				try {
					ward0.setText(key.getWard(0)+"");
					ward1.setText(key.getWard(1)+"");
					ward2.setText(key.getWard(2)+"");
					ward3.setText(key.getWard(3)+"");
					ward4.setText(key.getWard(4)+"");
					ward5.setText(key.getWard(5)+"");
					ward6.setText(key.getWard(6)+"");
					ward7.setText(key.getWard(7)+"");
					ward8.setText(key.getWard(8)+"");
					ward9.setText(key.getWard(9)+"");
				} catch (Exception e) {
					Log.e("KeyEditActivity", "Setting Wards Exception", e);
				}
			}
		}
		
	}
	
	private Boolean handleWardInput(Key key, int ward, EditText field) {
		Boolean good = true;
		String msg = "";
		Integer value = 0;
		
		try {
			if(field != null && field.getText() != null && field.getText().toString().length() != 0) {
				value = Integer.parseInt(field.getText().toString());
			}
			if(value > GlobalSettings.LockMaxIndex) {
				value = GlobalSettings.LockMaxIndex;
				field.setText(value.toString());
				good = false;
				msg = "Ward " + ward + " value exceeds " + GlobalSettings.LockMaxIndex;
			}
			if(ward > GlobalSettings.KeyMaxIndex) {
				ward = 0;
				good = false;
				msg = "Ward " + ward + " index exceeds ward count " + GlobalSettings.KeyMaxIndex + " ";
			}
			key.setWard(ward, value);
		} catch(Exception e) {
			msg = "Ward " + ward + " value did not parse, setting to " + GlobalSettings.LockMaxIndex;
			key.setWard(ward, GlobalSettings.LockMaxIndex);
			good = false;
		}
		
		if(!good) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
		
		return good;
	}
	
	public void save(View view) {
		save();
	}
	
	public void save() {
		key.setName(keyNameEditText.getText().toString());

		Boolean b0 = handleWardInput(key, 0, ward0);
		Boolean b1 = handleWardInput(key, 1, ward1);
		Boolean b2 = handleWardInput(key, 2, ward2);
		Boolean b3 = handleWardInput(key, 3, ward3);
		Boolean b4 = handleWardInput(key, 4, ward4);
		Boolean b5 = handleWardInput(key, 5, ward5);
		Boolean b6 = handleWardInput(key, 6, ward6);
		Boolean b7 = handleWardInput(key, 7, ward7);
		Boolean b8 = handleWardInput(key, 8, ward8);
		Boolean b9 = handleWardInput(key, 9, ward9);
		
		Boolean goodInput = b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9;
		if(goodInput) {
			Boolean saveSuccess = HailCaesarSession.getInstance().getKeyService().save(key);
			if(saveSuccess) {
				Toast.makeText(getApplicationContext(), "Key " + key.getName() + " is saved", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Failed to save key " + key.getName(), Toast.LENGTH_SHORT).show();
			}	
		} else {
			Toast.makeText(getApplicationContext(), "Check your ward values, they must be " + GlobalSettings.LockMaxIndex + " or under", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void export(View view) {
		save();
		Boolean success = HailCaesarSession.getInstance().getKeyService().export(key);
		if(success) {
			Toast.makeText(getApplicationContext(), "Key " + key.getName() + " is exported", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to export key " + key.getName(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void remove(View view) {
		Boolean success = HailCaesarSession.getInstance().getKeyService().remove(key);
		if(success) {
			Toast.makeText(getApplicationContext(), "Key " + key.getName() + " is removed", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to remove key " + key.getName(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void randomizeWards(View view) {
		ward0.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward1.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward2.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward3.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward4.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward5.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward6.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward7.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward8.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
		ward9.setText(random.nextInt(GlobalSettings.LockMaxIndex)+"");
	}

}
