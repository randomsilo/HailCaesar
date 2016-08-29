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
import com.randomsilo.hailcaesar.model.Lock;

public class LockListArrayAdapter extends ArrayAdapter<Lock> {
	Context context;
	int layoutResourceId;
	List<Lock> data = null;

	public LockListArrayAdapter(Context context, int layoutResourceId,
			List<Lock> data) {
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

		Lock lock = data.get(position);
		TextView lockName = (TextView) convertView.findViewById(R.id.lock_name);
		lockName.setText(lock.getName());
		
		TextView lockUuid = (TextView) convertView.findViewById(R.id.lock_uuid);
		lockUuid.setText(lock.getId().toString());
		
		return convertView;
	}

}
