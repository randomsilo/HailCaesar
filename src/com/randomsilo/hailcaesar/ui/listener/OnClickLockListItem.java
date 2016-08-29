package com.randomsilo.hailcaesar.ui.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.ui.activity.LockEditActivity;

public class OnClickLockListItem implements OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		Context context = view.getContext();
        TextView lockUuid = ((TextView) view.findViewById(R.id.lock_uuid));
        
        Intent intent = new Intent(view.getContext(), LockEditActivity.class);
        Bundle b = new Bundle();
        b.putString("lockUuid", lockUuid.getText().toString()); 
        intent.putExtras(b); 
        context.startActivity(intent);
	}

}
