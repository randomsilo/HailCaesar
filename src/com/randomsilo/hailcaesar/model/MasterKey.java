package com.randomsilo.hailcaesar.model;

import java.util.HashMap;

import android.annotation.SuppressLint;

public class MasterKey extends Key {

	@SuppressLint("UseSparseArrays")
	public MasterKey() {
		super("MasterKey");
		
		HashMap<Integer,Integer> wards = new HashMap<Integer,Integer>();
		wards.put(0, 2);
		wards.put(1, 3);
		wards.put(2, 5);
		wards.put(3, 7);
		wards.put(4, 11);
		wards.put(5, 13);
		wards.put(6, 17);
		wards.put(7, 19);
		wards.put(8, 23);
		wards.put(9, 29);
				
		setWards(wards);
	}

	public static Key getMasterKey() {
		return new MasterKey();
	}
}
