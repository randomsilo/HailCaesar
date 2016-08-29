package com.randomsilo.hailcaesar.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Identity {
	private String safePassword;
	private String distressPassword;
	private Boolean eulaAccepted;
	
	public Identity() {
		safePassword = "";
		distressPassword = "";
		eulaAccepted = false;
	}

	public String getSafePassword() {
		return safePassword;
	}

	public void setSafePassword(String safePassword) {
		this.safePassword = safePassword;
	}

	public String getDistressPassword() {
		return distressPassword;
	}

	public void setDistressPassword(String distressPassword) {
		this.distressPassword = distressPassword;
	}
	
	public Boolean isEulaAccepted() {
		return eulaAccepted;
	}

	public void setEulaAccepted(Boolean eulaAccepted) {
		this.eulaAccepted = eulaAccepted;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject j = new JSONObject();
		
		j.put("safePassword", safePassword);
		j.put("distressPassword", distressPassword);
		j.put("eulaAccepted", eulaAccepted.toString());
	
		return j;
	}
	
	public static Identity fromJson(String json) throws JSONException {
		Identity i = new Identity();
		JSONObject j = new JSONObject(json);
	
		i.setSafePassword(j.get("safePassword").toString());
		i.setDistressPassword(j.get("distressPassword").toString());
		i.setEulaAccepted(Boolean.valueOf(j.get("eulaAccepted").toString()));
				
		return i;
	}
}
