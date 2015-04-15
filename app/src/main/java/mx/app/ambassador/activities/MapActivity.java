package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.games.GameKofActivity;
import mx.app.ambassador.activities.games.GameProductsActivity;
import mx.app.ambassador.activities.games.GameRefriActivity;
import mx.app.ambassador.activities.games.GameTeamActivity;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class MapActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    LinearLayout llMenu;

    Button btLogbook;
    Button btGame;
    Button btGuidelines;
    Button btProfile;
    Button btWall;
    Button btYammer;
    Button btInfo;
    Button btChecklist;
    Button btFeedback;
    Button btTop10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Mapa");

        llMenu       = (LinearLayout)findViewById(R.id.ll_map_menu);
        btLogbook    = (Button)findViewById(R.id.bt_map_logbook);
        btGame       = (Button)findViewById(R.id.bt_map_game);
        btGuidelines = (Button)findViewById(R.id.bt_map_guidelines);
        btProfile    = (Button)findViewById(R.id.bt_map_profile);
        btWall       = (Button)findViewById(R.id.bt_map_wall);
        btYammer     = (Button)findViewById(R.id.bt_map_yammer);
        btInfo       = (Button)findViewById(R.id.bt_map_info);
        btChecklist  = (Button)findViewById(R.id.bt_map_checklist);
        btFeedback   = (Button)findViewById(R.id.bt_map_feedback);
        btTop10      = (Button)findViewById(R.id.bt_map_top10);

        llMenu.setVisibility(View.GONE);
        btFeedback.setVisibility(View.GONE);
        btChecklist.setVisibility(View.GONE);
        btGame.setVisibility(View.GONE);


        if (User.get("prevaluation", this).equals("true")) {
            Map<String, Object> params = User.getToken(this);
            WebBridge.send("webservices.php?task=getStatusMap", params, "Cargando", this, this);
        } else {
            Intent intent = new Intent(MapActivity.this, EvaluationActivity.class);
            intent.putExtra("type", "pre");
            startActivityForResult(intent, 1);
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {
        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        if (selected == 1) {
            nav = new Intent(MapActivity.this, LogbookActivity.class);
        } else if (selected == 2) {
            nav = new Intent(MapActivity.this, GameActivity.class);
        } else if (selected == 4) {
            nav = new Intent(MapActivity.this, ProfileActivity.class);
        } else if (selected == 5) {
            nav = new Intent(MapActivity.this, WallActivity.class);
        } else if (selected == 7) {
            nav = new Intent(MapActivity.this, InfoActivity.class);
        } else if (selected == 8) {
            nav = new Intent(MapActivity.this, ChecklistActivity.class);
        } else if (selected == 10) {
            nav = new Intent(MapActivity.this, RankingActivity.class);
        } else if (selected == 11) {
            User.clear(this);
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.static_motion, R.anim.fade_out);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(nav);

    }



	/*----------------*/
	/* CUSTOM METHODS */

    protected void fade(View v) {

        v.setVisibility(View.VISIBLE);

        AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);
        a.setDuration(300);
        a.setFillAfter(true);
        v.startAnimation(a);
    }



	/*-----------------*/
	/* OVERRIDE RESULT */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != RESULT_OK) {
            Map<String, Object> params = User.getToken(this);
            WebBridge.send("webservices.php?task=getStatusMap", params, "Cargando", this, this);
        }
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
            if (url.contains("getStatusMap")) {

                int level = 0;

                try {
                    level = json.getInt("map_level_access");
                } catch (Exception e) {}

                ImageView path = (ImageView)findViewById(R.id.img_map_path);
                ImageView dflt = (ImageView)findViewById(R.id.img_map_path_default);

                int r = getResources().getIdentifier("image_map_status_" + level, "drawable", getPackageName());
                path.setImageResource(r);

                AlphaAnimation a = new AlphaAnimation(1.0f, 0.0f);
                a.setDuration(300);
                a.setFillAfter(true);
                dflt.startAnimation(a);

                fade(path);
                fade(llMenu);

                if (level > 1  || true) fade(btGame);
                if (level == 4 || true) btChecklist.setVisibility(View.VISIBLE);
                if (level == 5 || true) btFeedback.setVisibility(View.VISIBLE);

            }
        }
    }
}
