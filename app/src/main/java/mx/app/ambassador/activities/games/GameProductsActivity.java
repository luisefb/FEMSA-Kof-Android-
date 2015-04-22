package mx.app.ambassador.activities.games;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.SectionActivity;
import mx.app.ambassador.gestures.PanGestureListener;
import mx.app.ambassador.gestures.PanGestureRecognizer;
import mx.app.ambassador.ui.views.RailView;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/2/15.
 */
public class GameProductsActivity extends SectionActivity implements RailView.OnRailProductListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    Boolean finished, started;
    int width, height, timer, points, current, record, max = 60;

    LinearLayout llInstructions;
    RelativeLayout rlContent;
    LinearLayout llRails;
    LinearLayout llMessage;
    ImageView imgScore;
    ImageView imgMessage;
    TextView txtType;
    TextView txtScore;
    TextView txtTime;
    TextView txtMessage;
    TextView txtRecord;

    ArrayList<HashMap<String, String>> data;
    String[] types = new String[]{"Colas", "Sabores", "Jugos/Néctares", "Aguas", "Café", "Isotónica", "Energizante", "Lácteo", "Té"};
    Random random = new Random();
    Handler handler = new Handler();

    private Runnable updateTimer = new Runnable(){
        public void run(){
            timer();
            if (timer < 0) {
                end();
            } else {
                handler.postDelayed(updateTimer, 1000);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_products);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Caza Productos");

        data = new ArrayList<HashMap<String, String>>();
        for (int i=1;  i<=8; i++)  data.add(getItem(Integer.toString(i), "0"));
        for (int i=9;  i<=17; i++) data.add(getItem(Integer.toString(i), "1"));
        for (int i=18; i<=24; i++) data.add(getItem(Integer.toString(i), "2"));
        for (int i=25; i<=29; i++) data.add(getItem(Integer.toString(i), "3"));
        for (int i=30; i<=30; i++) data.add(getItem(Integer.toString(i), "4"));
        for (int i=31; i<=43; i++) data.add(getItem(Integer.toString(i), "5"));
        for (int i=44; i<=44; i++) data.add(getItem(Integer.toString(i), "6"));
        for (int i=45; i<=45; i++) data.add(getItem(Integer.toString(i), "7"));
        for (int i=46; i<=47; i++) data.add(getItem(Integer.toString(i), "8"));

        rlContent  = (RelativeLayout)findViewById(R.id.rl_content);
        llRails    = (LinearLayout)findViewById(R.id.ll_rails);
        llMessage  = (LinearLayout)findViewById(R.id.ll_message);
        imgScore   = (ImageView)findViewById(R.id.img_score);
        imgMessage = (ImageView)findViewById(R.id.img_message);
        txtType    = (TextView)findViewById(R.id.txt_type);
        txtScore   = (TextView)findViewById(R.id.txt_score);
        txtTime    = (TextView)findViewById(R.id.txt_time);
        txtMessage = (TextView)findViewById(R.id.txt_message);
        txtRecord  = (TextView)findViewById(R.id.txt_record);

        ViewTreeObserver vto = rlContent.getViewTreeObserver();
        if(vto.isAlive()){
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    create();
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rlContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        rlContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }


        llMessage.setAlpha(0);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            record = bundle.getInt("record");
        }


        txtType.setText("");
        txtScore.setText("");
        txtTime.setText("");
        txtRecord.setText("Récord anterior: " + record + " puntos");

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickStart (View v) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(llInstructions, "alpha",  1.0f, 0.0f);
        alpha.setDuration(400);
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                llInstructions.setVisibility(View.GONE);
            }
        });

        alpha.start();

        started = true;
        handler.postDelayed(updateTimer, 0);
        update(points);
    }



	/*----------------*/
	/* CUSTOM METHODS */

    public void showInstructions(String title, String message, boolean button) {

        llInstructions = (LinearLayout)findViewById(R.id.ll_instructions);
        llInstructions.setAlpha(0.0f);
        llInstructions.setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_instructions_title)).setText(title);
        ((TextView)findViewById(R.id.txt_instructions_msg)).setText(message);
        ((Button)findViewById(R.id.bt_instructions)).setText(button ? R.string.bt_start :  R.string.bt_done);

        if (!button) {
            ((Button)findViewById(R.id.bt_instructions)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_OK);
                    clickBack(null);
                }
            });
        }

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(llInstructions, "scaleX", 1.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(llInstructions, "scaleY", 1.5f, 1.0f);
        ObjectAnimator alpha  = ObjectAnimator.ofFloat(llInstructions, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(500);
        scaleY.setDuration(500);
        alpha.setDuration(500);

        AnimatorSet scale = new AnimatorSet();
        scale.play(scaleX).with(scaleY).with(alpha);
        scale.setStartDelay(1000);

        scale.start();
    }

    protected void end () {

        finished = true;

        JSONObject result  = new JSONObject();
        JSONObject answers = new JSONObject();
        JSONObject answer  = new JSONObject();

        try {

            result.put("games_answerds", answers);
            answers.put("question_1", answer);
            answer.put("answerd_right", "1");
            answer.put("answerd", points);

        } catch (JSONException e) {}

        Map<String, Object> params = User.getToken(this);
        params.put("game_type", "caza-productos");
        params.put("json", result.toString());
        params.put("game_records", points);

        WebBridge.send("webservices.php?task=addAnswerdGames", params, "Cargando", this, this);
        //

    }


    protected void create() {

        width    = rlContent.getWidth();
        height   = rlContent.getHeight();
        timer    = max;
        finished = false;
        started  = false;
        points   = 0;
        //answers  = new JSONArray();

        for (int i=0; i<3; i++) {
            RailView rail = new RailView(this, height, i%2==0?"up":"down", data, this);
            llRails.addView(rail);
        }

        float percent = imgScore.getHeight()/688.0f;
        float posX = width * 1.0f - 669.0f * percent;

        txtScore.setX(posX + 392.0f * percent);
        txtType.setX(posX + 392.0f * percent);
        txtTime.setX(posX + 527.0f * percent);

        txtType.setY(83.0f * percent);
        txtTime.setY(270.0f * percent);

        txtScore.setLayoutParams(new RelativeLayout.LayoutParams((int)(249 * percent), (int)(60 * percent)));
        txtType.setLayoutParams(new RelativeLayout.LayoutParams((int)(249 * percent), (int)(60 * percent)));
        txtTime.setLayoutParams(new RelativeLayout.LayoutParams((int)(126 * percent), (int)(47 * percent)));

        showInstructions(getString(R.string.txt_instructions), getString(R.string.txt_game_instructions_products), true);

    }

    protected void timer () {

        if (!started) return;

        String result = String.format("%02d:%02d", timer / 60, timer % 60);
        txtTime.setText(result);
        timer--;
    }

    protected void update(int p) {
        points += p;
        current = random.nextInt(types.length);
        txtScore.setText(points + " puntos");
        txtType.setText(types[current]);
    }

    protected void show(int image, String message, Boolean dismiss) {

        imgMessage.setImageResource(image);
        txtMessage.setText(message);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(llMessage, "scaleX", 1.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(llMessage, "scaleY", 1.5f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(llMessage, "alpha",  0.0f, 1.0f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(llMessage, "alpha",  1.0f, 0.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);
        alpha2.setDuration(400);

        alpha2.setStartDelay(1000);

        AnimatorSet scale = new AnimatorSet();
        if (dismiss) {
            scale.play(scaleX).with(scaleY).with(alpha1).before(alpha2);
        } else {
            scale.play(scaleX).with(scaleY).with(alpha1);
        }
        scale.start();

    }

    protected HashMap<String, String> getItem(final String i, final String t) {
        return new HashMap<String, String>() {{ put("i", i);  put("n", t);}};
    }



	/*-----------------------*/
	/* RAIL PRODUCT LISTENER */

    @Override
    public void onRailProductClick(String type) {

        if (finished) return;

        int selected = Integer.parseInt(type);
        boolean success = selected == current;

        update(success ? 5 : 0);
        show( success ? R.drawable.icon_game_success : R.drawable.icon_game_error, "Ahora busca \"" + types[current] + "\"", true);

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
            msg = json.getString("message");
        } catch (Exception e) {
        }

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
        } else {
            showInstructions(getString(R.string.txt_finished), "¡Ganaste \"" + points  + "\" puntos!", false);
        }
    }
}
