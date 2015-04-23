package mx.app.ambassador.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class RankingActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    LinearLayout llRanking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Top Ten");

        llRanking = (LinearLayout)findViewById(R.id.ll_ranking);

        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getTopScore", params, "Cargando", this, this);

    }



	/*----------------*/
	/* CUSTOM METHODS */

    protected View getItem(String name, String url, int points, int percent) {

        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.ui_ranking_item, null);

        ((TextView)item.findViewById(R.id.txt_name)).setText(name);
        ((TextView)item.findViewById(R.id.txt_points)).setText(points + " puntos");

        ImageView img1 = (ImageView)item.findViewById(R.id.img_bar1);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)img1.getLayoutParams();
        params1.weight = percent * 1.0f;
        img1.setLayoutParams(params1);

        ImageView img2 = (ImageView)item.findViewById(R.id.img_bar2);
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)img2.getLayoutParams();
        params2.weight = 100.0f - percent;
        img2.setLayoutParams(params2);

        if(url != null) {
            ImageView img3 = (ImageView)item.findViewById(R.id.img_avatar);
            AQuery aq = new AQuery(img3);
            aq.image(url, true, true, 0, R.id.img_wall, null, AQuery.FADE_IN);
        }

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

            if (url.contains("getTopScore")) {

                JSONArray topscore = new JSONArray();
                int max = 100;

                try {
                    topscore = json.getJSONArray("topscore");
                } catch (Exception e) {}


                for (int i=0; i<topscore.length(); i++) {
                    String name = "", image = "";
                    int points  = 0, percent = 0;

                    try {
                        JSONObject g = topscore.getJSONObject(i);

                        name   = g.getString("name");
                        image  = g.getString("file_image");
                        points = g.getInt("points");
                        points = points > max ? max : points;

                    } catch (Exception e) {}

                    percent = (points * 100)/max;

                    llRanking.addView(getItem(name, image, points, percent));

                }
            }
        }
    }


}
