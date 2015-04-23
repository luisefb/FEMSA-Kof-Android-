package mx.app.ambassador.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.ui.layouts.FlowLayout;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class WallActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    FlowLayout flWall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Cuadro de Honor");

        flWall = (FlowLayout)findViewById(R.id.fl_wall);

        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getWallOfFame", params, "Cargando", this, this);

    }



	/*----------------*/
	/* CUSTOM METHODS */

    protected View getItem(String message, String url) {
        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.ui_wall_item, null);

        ((TextView)item.findViewById(R.id.txt_wall)).setText(message);
        ImageView img = (ImageView)item.findViewById(R.id.img_wall);

        AQuery aq = new AQuery(img);
        aq.image(url, true, true, 0, R.id.img_wall, null, AQuery.FADE_IN);

        return item;
    }



	/*--------------------*/
	/* WEBBRIDGE LISTENER */

    @Override
    public void onWebBridgeResult(String url, JSONObject json, AjaxStatus ajaxStatus) {

        // TODO Auto-generated method stub
        int status = 0;
        String msg = "";

        try {
            status = json.getInt("status");
            msg	   = json.getString("message");
        } catch (Exception e) {}

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
        } else {

            if (url.contains("getWallOfFame")) {

                JSONArray generations = new JSONArray();
                String message = "", wall = "";

                try {
                    JSONObject me = json.getJSONObject("my_generation");

                    generations = json.getJSONArray("history_generation");
                    message     = me.getString("generation");
                    wall        = me.getString("photo_generation");

                } catch (Exception e) {}


                flWall.addView(getItem(message, wall));

                for (int i=0; i<generations.length(); i++) {
                    String m = "", w = "";

                    try {
                        JSONObject g = generations.getJSONObject(i);

                        m = g.getString("generation");
                        w = g.getString("photo_generation");

                    } catch (Exception e) {}

                    flWall.addView(getItem(m, w));

                }
            }
        }
    }
}
