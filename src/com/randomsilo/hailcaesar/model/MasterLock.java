package com.randomsilo.hailcaesar.model;

import android.annotation.SuppressLint;
import java.util.HashMap;

import com.randomsilo.hailcaesar.GlobalSettings;

public class MasterLock extends Lock{

	@SuppressLint("UseSparseArrays")
	public MasterLock() {
		super("Master");
		this.useForMessaging();
		
		HashMap<Integer,Integer> keyedOffset = new HashMap<Integer,Integer>();
		int offset = GlobalSettings.AsciiCharacterCount;
		boolean countDown = true;
		for(int i=0; i<=GlobalSettings.LockMaxIndex; i++) {
			keyedOffset.put(i, offset);
			
			if(countDown) {
				offset--;
			} else {
				offset++;
			}
			
			if(offset == 0) {
				countDown = false;
				offset++;
			}
		}
		
		this.setKeyedOffset(keyedOffset);
	}
	
	public static Lock getMasterLock() {
		return new MasterLock();
	}

}
