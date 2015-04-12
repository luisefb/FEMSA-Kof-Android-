package mx.app.ambassador.utils;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class User {
	
	static public void set(String key, String value, Activity a) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
		e.putString("profile_" + key, value);
		e.commit();
	}
	
	static public String get(String key, Activity a) {
		
		Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
		Object result = data.get("profile_" + key);
		return result != null ? result.toString() : "";
						
	}
	
	static public void clear(Activity a) {
		
		Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
		Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
		
		for (Map.Entry<String,?> entry : data.entrySet()) {
			e.remove(entry.getKey());
		}
		
		e.commit();
	}
	
	static public void setToken(String id, String token, Activity a) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
		e.putString("token_user_id",    id);
		e.putString("token_user_token", token);
		e.commit();
	}
	
	static public Map<String, Object> getToken(Activity a) {
		
		Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
				
		if (!data.containsKey("token_user_token") || data.get("token_user_token") == null) {
			return null;
		}
				
		Map<String, Object> token = new HashMap<String, Object>();
		//token.put("token", "0707414a814b90cc7a417f764867cbb76dc708e0");
		token.put("token", 	 data.get("token_user_token").toString());
				
		return token;
		
	}
	
}
