package com.randomsilo.hailcaesar.model;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.randomsilo.hailcaesar.GlobalSettings;

@SuppressLint("UseSparseArrays") 
public class Key {
	private UUID id;
	private String name;
	private HashMap<Integer,Integer> wards; // 0-9 indexed key ids
	
	public Key(String name) {
		wards = new HashMap<Integer,Integer>();
		id = UUID.randomUUID();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = UUID.fromString(id);
	}

	public void setWards(HashMap<Integer,Integer> wards) {
		this.wards = wards;
	}
	
	public void setWard(Integer index, Integer key) {
		this.wards.put(index, key);
	}
	
	public int getWard(int index) throws Exception {
		int offset = 0;
		
		if(wards.containsKey(index)) {
			offset = wards.get(index);
		} else {
			throw new Exception("No ward for index: " + index);
		}
		
		return offset;
	}

	public String getWards() {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<=GlobalSettings.KeyMaxIndex; i++) {
			if(i>0) {
				sb.append(" ");
			}
			sb.append(String.format("%02d", wards.get(i)));
		}
		
		return sb.toString();
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject j = new JSONObject();
		
		j.put("name", name);
		j.put("id", id.toString());

		for(Integer key : wards.keySet()) {
			j.put("ward_"+key.toString(), wards.get(key).toString());
		}
		
		return j;
	}
	
	public static Key fromJson(String json) throws JSONException {
		Key k = new Key("");
		JSONObject j = new JSONObject(json);
	
		k.setName(j.get("name").toString());
		k.setId(j.get("id").toString());
		
		for(int i=0; i<=GlobalSettings.KeyMaxIndex;i++) {
			k.wards.put(i, Integer.parseInt(j.get("ward_"+i).toString()));
		}
		
		return k;
	}
}
