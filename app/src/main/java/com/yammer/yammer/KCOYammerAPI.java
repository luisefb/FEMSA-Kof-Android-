package com.yammer.yammer;

/**
 * Created by Cockponcher .
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KCOYammerAPI {
    public static final String LOGIN_URL = "https://www.yammer.com/dialog/oauth?client_id=%s";
    public static final String LOGIN_OAUTH = "https://www.yammer.com/oauth2/access_token.json?client_id=%s&client_secret=%s&code=%s";
    public static final String CLIENT_ID = "FMpIy5fUOrO9fu2tWghg"; //cambiar por ID de cliente
    public static final String CLIENT_SECRET = "rSTgL7zkhb2cZ8ZlRoz18VXgr1kPBkGhfjHiqdisJ4"; //cambiar por llave secreta


    public static boolean isLoggedIn(Context context)
    {
        return !KCOYammerPreference.getYammerToken(context).isEmpty();
    }

    public static JSONArray getMessage(Context context)
    {
        String url = "https://www.yammer.com/api/v1/messages.json";
        return KCOWebServices.SendHttpGetArray(url, KCOYammerPreference.getYammerToken(context));
    }

    public static void postMessage(Context context, String body)
    {
        String url = "https://www.yammer.com/api/v1/messages.json";
        JSONObject json = new JSONObject();
        try {
            json.put("body", body);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        KCOWebServices.SendHttpPost(url, json, KCOYammerPreference.getYammerToken(context));
    }

    public static void postPrivateMessage(Context context, String body, int toId)
    {
        String url = "https://www.yammer.com/api/v1/messages.json";
        JSONObject json = new JSONObject();
        try {
            json.put("body", body);
            json.put("direct_to_id", toId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        KCOWebServices.SendHttpPost(url, json, KCOYammerPreference.getYammerToken(context));
    }

    public static JSONArray getUsers(Context context)
    {
        String url = "https://www.yammer.com/api/v1/users.json";

        return KCOWebServices.SendHttpGetArray(url, KCOYammerPreference.getYammerToken(context));
    }
}
