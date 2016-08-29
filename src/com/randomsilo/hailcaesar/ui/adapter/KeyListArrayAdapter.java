package com.randomsilo.hailcaesar.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.model.Key;

public class KeyListArrayAdapter extends ArrayAdapter<Key> {
	Context context;
	int layoutResourceId;
	List<Key> data = null;

	public KeyListArrayAdapter(Context context, int layoutResourceId,
			List<Key> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}

		Key key = data.get(position);
		TextView keyName = (TextView) convertView.findViewById(R.id.key_name);
		keyName.setText(key.getName());
		
		TextView keyWards = (TextView) convertView.findViewById(R.id.key_wards);
		keyWards.setText(key.getWards());
		
		TextView keyUuid = (TextView) convertView.findViewById(R.id.key_uuid);
		keyUuid.setText(key.getId().toString());
		
		return convertView;
	}

}
