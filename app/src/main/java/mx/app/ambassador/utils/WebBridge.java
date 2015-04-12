package mx.app.ambassador.utils;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.*;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.ProfileActivity;
import mx.app.ambassador.ui.dialogs.ProgressDialog;


public class WebBridge {
	
	public AQuery aq;
	
	private ProgressDialog progress;
	private WebBridgeListener callback;
	
	static public ArrayList<WebBridge> instances = new ArrayList<WebBridge>();
	
	static public String url(String url) {
		if (url.indexOf("http://") == 0) return url;
		//url = url.replace("webservices.php", "webservices_fer.php");
		String u = "http://kreativeco.com/TESTING/femsa/actions/" + url;
		Log.e("REQUEST URL", u);
		return u;
	}
	
	static public String format(Map<String,String> params) {		
		StringBuilder sb = new StringBuilder();
		for(HashMap.Entry<String,String> e : params.entrySet()){
		      if(sb.length() > 0){
		          sb.append('&');
		      }
		      try {
				sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		  }
		return sb.toString();
	}
	
	static private void addVersion(Map<String,Object> params, Activity activity) {		
		String app_version = "N/A";
		try {
			app_version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
		} catch (Exception e) {}
		
		params.put("app_version", app_version);
		params.put("app_os", "android");
	}
	
	static public WebBridge send(String url, Map<String,Object> params, String message, Activity activity, WebBridgeListener callback) {
		final WebBridge wb = WebBridge.getInstance(activity, message, callback);
		if (wb != null) {
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};
			
			WebBridge.addVersion(params, activity);
			Log.e("REQUEST VARS", params.toString());
			
			wb.aq.ajax(WebBridge.url(url), params, String.class, ajaxCallback);
		}
		return wb;
	}
	
	static public WebBridge send(String url, Map<String,Object> params,  Activity activity, WebBridgeListener callback) {
		final WebBridge wb = WebBridge.getInstance(activity, null, callback);
		if (wb != null) {
			
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};
			
			WebBridge.addVersion(params, activity);
			Log.e("REQUEST VARS", params.toString());
			
			wb.aq.ajax(WebBridge.url(url), params, String.class, ajaxCallback);
			
		}
		return wb;
	}
	
	static public WebBridge send(String url, String message, Activity activity, ProfileActivity callback) {
		final WebBridge wb = WebBridge.getInstance(activity, message, callback);
		if (wb != null) {
			
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};

			wb.aq.ajax(WebBridge.url(url), String.class, ajaxCallback);
			
		}
		return wb;
	}
	
	static public WebBridge send(String url, Activity activity, WebBridgeListener callback) {
		final WebBridge wb = WebBridge.getInstance(activity, null, callback);
		if (wb != null) {
			
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};

			wb.aq.ajax(WebBridge.url(url), String.class, ajaxCallback);
			
		}
		return wb;
	}
	
	static public WebBridge send(String url, Map<String,Object> params, Activity activity) {
		final WebBridge wb = WebBridge.getInstance(activity, null, null);
		if (wb != null) {
			
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};
			
			WebBridge.addVersion(params, activity);
			Log.e("MSG", params.toString());
			
			wb.aq.ajax(WebBridge.url(url), params, String.class, ajaxCallback);
			
		}
		return wb;
	}
	
	static public WebBridge send(String url, Activity activity) {
		final WebBridge wb = WebBridge.getInstance(activity, null, null);
		if (wb != null) {
			
			AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
				@Override
		         public void callback(String url, String json, AjaxStatus status) { wb.result(url, json, status); }
			};

			wb.aq.ajax(WebBridge.url(url), String.class, ajaxCallback);
			
		}
		return wb;
	}
	
			
	static WebBridge getInstance(Activity activity, String message, WebBridgeListener callback) {

		if (WebBridge.haveNetworkConnection(activity) == false) {
			Toast.makeText(activity, activity.getResources().getString(R.string.error_connectivity), Toast.LENGTH_LONG).show();
			return null;
		}

		WebBridge wb = new WebBridge();
		wb.callback = callback;
		if (message != null) wb.progress = ProgressDialog.show(activity, message, true, false, null);	
		
		wb.aq = new AQuery(activity);
		WebBridge.instances.add(wb);
			
		return wb;
	}
	
	public void result (String url, String json, AjaxStatus status) {		
				
		Log.e("RESPONSE", json);
		
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
		
		if (callback != null) {
			JSONObject o = null;
			try {
				o = new JSONObject(json);
			} catch (Exception e) {
				// TODO: handle exception
			}
			callback.onWebBridgeResult(url, o, status);
		}
				
		WebBridge.instances.remove(this);
	}
	
	static public boolean haveNetworkConnection(Activity a) {
		
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected()) {
	                haveConnectedWifi = true;
	            }
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected()) {
	                haveConnectedMobile = true;
	            }
	    }	    
	    	    
	    return haveConnectedWifi || haveConnectedMobile;
	}
	
	public interface WebBridgeListener {
        void onWebBridgeResult(String url, JSONObject json, AjaxStatus ajaxStatus);
    }
	
}
