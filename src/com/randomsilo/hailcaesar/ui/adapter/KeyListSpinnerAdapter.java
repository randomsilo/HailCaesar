package com.randomsilo.hailcaesar.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.model.Key;

public class KeyListSpinnerAdapter extends BaseAdapter implements SpinnerAdapter{
	Activity activity;
	List<Key> data = null;

	public KeyListSpinnerAdapter(Activity activity, List<Key> data) {
		this.activity = activity;
		this.data = data;
	}
	
	public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		View spinView;
	    if( convertView == null ){
	        LayoutInflater inflater = activity.getLayoutInflater();
	        spinView = inflater.inflate(R.layout.spinner_keys, null);
	    } else {
	         spinView = convertView;
	    }
	    TextView name = (TextView) spinView.findViewById(R.id.KeyName);
	    name.setText(data.get(position).getName());
	    return spinView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
