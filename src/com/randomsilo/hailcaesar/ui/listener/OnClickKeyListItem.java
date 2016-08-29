package com.randomsilo.hailcaesar.ui.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.ui.activity.KeyEditActivity;

public class OnClickKeyListItem implements OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		Context context = view.getContext();
        TextView keyUuid = ((TextView) view.findViewById(R.id.key_uuid));
        
        Intent intent = new Intent(view.getContext(), KeyEditActivity.class);
        Bundle b = new Bundle();
        b.putString("keyUuid", keyUuid.getText().toString()); 
        intent.putExtras(b); 
        context.startActivity(intent);
	}

}
