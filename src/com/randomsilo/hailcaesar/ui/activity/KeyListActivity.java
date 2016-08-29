package com.randomsilo.hailcaesar.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.randomsilo.hailcaesar.HailCaesarSession;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.ui.adapter.KeyListArrayAdapter;
import com.randomsilo.hailcaesar.ui.listener.OnClickKeyListItem;

public class KeyListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_list);
		RefreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.key_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_key_new) {
			navigateNewKey();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void RefreshList() {
		List<Key> keys = HailCaesarSession.getInstance().getKeyService().getKeyList();
		ListView keyListView = (ListView)findViewById(R.id.KeyListView);
		KeyListArrayAdapter adapter = new KeyListArrayAdapter(this, R.layout.list_key_item, keys);
		keyListView.setAdapter(adapter);
		keyListView.setOnItemClickListener( new OnClickKeyListItem());
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    RefreshList();
	}
	
	public void navigateNewKey() {
		Intent intent = new Intent(this, KeyEditActivity.class);
        startActivity(intent);    	
    }
}
