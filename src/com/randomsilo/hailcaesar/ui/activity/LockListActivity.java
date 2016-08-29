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
import com.randomsilo.hailcaesar.model.Lock;
import com.randomsilo.hailcaesar.ui.adapter.LockListArrayAdapter;
import com.randomsilo.hailcaesar.ui.listener.OnClickLockListItem;

public class LockListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_list);
		
		HailCaesarSession.getInstance().getLockService().setActivity(this);
		
		RefreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lock_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_lock_new) {
			navigateNewLock();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void RefreshList() {
		List<Lock> locks = HailCaesarSession.getInstance().getLockService().getLockList();
		ListView lockListView = (ListView)findViewById(R.id.LockListView);
		LockListArrayAdapter adapter = new LockListArrayAdapter(this, R.layout.list_lock_item, locks);
		lockListView.setAdapter(adapter);
		lockListView.setOnItemClickListener( new OnClickLockListItem());
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    RefreshList();
	}
	
	public void navigateNewLock() {
		Intent intent = new Intent(this, LockEditActivity.class);
        startActivity(intent);    	
    }
}
