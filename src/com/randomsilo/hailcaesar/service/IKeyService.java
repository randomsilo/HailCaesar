package com.randomsilo.hailcaesar.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;

import com.randomsilo.hailcaesar.model.Key;

public interface IKeyService {
	Map<UUID,Key> getKeyMap();
	List<Key> getKeyList();
	Key create(String name);
	Key get(String name);
	Key get(UUID id);
	Boolean save(Key key);
	Boolean export(Key key);
	Boolean remove(Key key);
	void refreshList();
	void setActivity(Activity activity);
	void clearAll();
}
