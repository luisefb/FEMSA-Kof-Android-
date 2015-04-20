package mx.app.ambassador.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class MapActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    LinearLayout llMenu;
    RelativeLayout rlMap;
    RelativeLayout rlFemsa;

    Button btLogbook;
    Button btGame;
    Button btLibrary;
    Button btProfile;
    Button btWall;
    Button btYammer;
    Button btInfo;
    Button btChecklist;
    Button btFeedback;
    Button btTop10;
    Button btEvaluation;
    Button btFemsa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Mapa");

        rlMap        = (RelativeLayout)findViewById(R.id.rl_map);
        rlFemsa      = (RelativeLayout)findViewById(R.id.rl_femsa);
        llMenu       = (LinearLayout)findViewById(R.id.ll_map_menu);
        btLogbook    = (Button)findViewById(R.id.bt_map_logbook);
        btGame       = (Button)findViewById(R.id.bt_map_game);
        btLibrary    = (Button)findViewById(R.id.bt_map_library);
        btProfile    = (Button)findViewById(R.id.bt_map_profile);
        btWall       = (Button)findViewById(R.id.bt_map_wall);
        btYammer     = (Button)findViewById(R.id.bt_map_yammer);
        btInfo       = (Button)findViewById(R.id.bt_map_info);
        btChecklist  = (Button)findViewById(R.id.bt_map_checklist);
        btFeedback   = (Button)findViewById(R.id.bt_map_feedback);
        btTop10      = (Button)findViewById(R.id.bt_map_top10);
        btEvaluation = (Button)findViewById(R.id.bt_map_evaluation);
        btFemsa      = (Button)findViewById(R.id.bt_map_femsa);

        llMenu.setVisibility(View.GONE);
        rlMap.setVisibility(View.GONE);

        hide();

        /*
        if (User.get("prevaluation", this).equals("true")) {
            Map<String, Object> params = User.getToken(this);
            WebBridge.send("webservices.php?task=getStatusMap", params, "Cargando", this, this);
        } else {
            Intent intent = new Intent(MapActivity.this, EvaluationActivity.class);
            intent.putExtra("type", "pre");
            startActivityForResult(intent, 1);
        }
        */

        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getStatusMap", params, "Cargando", this, this);

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
        } else if (selected == 6) {
            nav = new Intent(MapActivity.this, YammerActivity.class);
        } else if (selected == 7) {
            nav = new Intent(MapActivity.this, InfoActivity.class);
        } else if (selected == 8) {
            User.set("checklist_type", null, this);
            nav = new Intent(MapActivity.this, ChecklistActivity.class);
        } else if (selected == 9) {
            nav = new Intent(MapActivity.this, FeedbackActivity.class);
        } else if (selected == 10) {
            nav = new Intent(MapActivity.this, EvaluationActivity.class);
            nav.putExtra("type", "post");
        } else if (selected == 11) {
            nav = new Intent(MapActivity.this, RankingActivity.class);
        } else if (selected == 12) {
            showFemsa();
        } else if (selected == 13) {
            User.clear(this);
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.static_motion, R.anim.fade_out);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(nav);

    }

    public void clickHide(View v){

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlFemsa, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                rlFemsa.setVisibility(View.GONE);
            }
        });
        alpha1.start();
    }

    public void clickFemsa(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ccf.tuola.mx/"));
        startActivity(browserIntent);
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

    protected void hide() {
        btFeedback.setVisibility(View.GONE);
        btChecklist.setVisibility(View.GONE);
        btGame.setVisibility(View.GONE);
        btEvaluation.setVisibility(View.GONE);
    }

    protected void showFemsa() {

        rlFemsa.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlFemsa, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlFemsa, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlFemsa, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }



	/*-----------------*/
	/* OVERRIDE RESULT */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fade(rlMap);
            Map<String, Object> params = User.getToken(this);
            WebBridge.send("webservices.php?task=getStatusMap", params, "Cargando", this, this);
        } else if (requestCode == 1 && resultCode != RESULT_OK) {
            clickBack(null);
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

                int level = 0, pre = 0, checklist = 0, feedback = 0, post = 0;

                try {
                    level       = json.getInt("map_level_access");
                    pre         = json.getInt("pre");
                    checklist   = json.getInt("checklist");
                    feedback    = json.getInt("feedback");
                    post        = json.getInt("post");
                } catch (Exception e) {}

                if (pre == 0) {
                    Intent intent = new Intent(MapActivity.this, EvaluationActivity.class);
                    intent.putExtra("type", "pre");
                    startActivityForResult(intent, 1);
                    return;
                }

                hide();

                ImageView path = (ImageView)findViewById(R.id.img_map_path);
                ImageView dflt = (ImageView)findViewById(R.id.img_map_path_default);

                int r = getResources().getIdentifier("image_map_status_" + level, "drawable", getPackageName());
                path.setImageResource(r);

                AlphaAnimation a = new AlphaAnimation(1.0f, 0.0f);
                a.setDuration(300);
                a.setFillAfter(true);
                dflt.startAnimation(a);

                fade(rlMap);
                fade(path);
                fade(llMenu);

                if (level > 1  || true) fade(btGame);
                if (level == 4 || true && checklist == 0) btChecklist.setVisibility(View.VISIBLE);
                if (level == 5 || true) {
                    if (feedback == 0)  btFeedback.setVisibility(View.VISIBLE);
                    else if (post == 0) btEvaluation.setVisibility(View.VISIBLE);
                }
                if (level == 7 || true) btFemsa.setVisibility(View.VISIBLE);

            }
        }
    }
}
