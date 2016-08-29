package com.randomsilo.hailcaesar.model;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.randomsilo.hailcaesar.GlobalSettings;

@SuppressLint("UseSparseArrays")
public class Lock {
	private String name;
	private UUID id;
	private HashMap<Integer,Integer> wardOffset; // 0-99 wards with ascii offsets
	private int blockFillLength = GlobalSettings.BlockLengthMessage;
	
	public Lock(String lockName) {
		wardOffset = new HashMap<Integer,Integer>();
		id = UUID.randomUUID();
		name = lockName;
	}
	
	public void setKeyedOffset(HashMap<Integer,Integer> wardOffset) {
		this.wardOffset = wardOffset;
	}
	
	public int getOffset(int wardIndex) throws Exception {
		int offset = 0;
		
		if(wardOffset.containsKey(wardIndex)) {
			offset = wardOffset.get(wardIndex);
		} else {
			throw new Exception("No ward index: " + wardIndex);
		}
		
		return offset;
	}
	
	public void useForMessaging() {
		blockFillLength = GlobalSettings.BlockLengthMessage;
	}
	
	public void useForAttachments() {
		blockFillLength = GlobalSettings.BlockLengthAttachment;
	}
	
	public int getBlockFillLength() {
		return blockFillLength;
	}
	
	public void setBlockFillLength(int length) {
		blockFillLength = length;
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
	
	public JSONObject toJson() throws JSONException {
		JSONObject j = new JSONObject();
		
		j.put("name", name);
		j.put("id", id.toString());
		j.put("blockFillLength", blockFillLength);
		for(Integer key : wardOffset.keySet()) {
			j.put("key_"+key.toString(), wardOffset.get(key).toString());
		}
		
		return j;
	}
	
	public static Lock fromJson(String json) throws JSONException {
		Lock l = new Lock("");
		JSONObject j = new JSONObject(json);
	
		l.setName(j.get("name").toString());
		l.setId(j.get("id").toString());
		l.setBlockFillLength(Integer.parseInt(j.get("blockFillLength").toString()));
		
		for(int i=0; i<=GlobalSettings.LockMaxIndex;i++) {
			l.wardOffset.put(i, Integer.parseInt(j.get("key_"+i).toString()));
		}
		
		return l;
	}
}
