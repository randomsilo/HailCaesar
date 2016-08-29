package com.randomsilo.hailcaesar.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;

import com.randomsilo.hailcaesar.model.Lock;

public interface ILockService {
	Map<UUID,Lock> getLockMap();
	List<Lock> getLockList();
	Lock create(String name);
	Lock get(String name);
	Lock get(UUID id);
	Boolean save(Lock lock);
	Boolean export(Lock lock);
	Boolean remove(Lock lock);
	void refreshList();
	void setActivity(Activity activity);
	void clearAll();
}
