package mx.app.ambassador.activities.games;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.SectionActivity;
import mx.app.ambassador.gestures.PanGestureListener;
import mx.app.ambassador.gestures.PanGestureRecognizer;
import mx.app.ambassador.ui.views.BottleSetView;
import mx.app.ambassador.ui.views.BottleView;
import mx.app.ambassador.ui.views.RailView;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/9/15.
 */
public class GameOrderActivity extends SectionActivity implements WebBridge.WebBridgeListener, PanGestureListener {

    int width, height, points, record, timer, max = 180;
    float offsetY;
    boolean started, finished;
    //missed,

    ArrayList<String> types;

    RelativeLayout rlContent;
    LinearLayout llInstructions;

    ImageView imgRail;
    ImageView imgTruck;
    TextView txtRecord;

    Random random    = new Random();
    Handler handler1 = new Handler();
    Handler handler2 = new Handler();

    BottleSetView images[];
    ArrayList<ImageView> truckImages;
    HashMap<String,ArrayList<BottleView>> bottleImages;

    private Runnable updateTimer1 = new Runnable(){
        public void run(){
            random();
            handler1.postDelayed(updateTimer1, 2000);
        }
    };

    private Runnable updateTimer2 = new Runnable(){
        public void run(){
            /*
            if (!finished) {
                String time = String.format("%02d:%02d", timer / 60, timer % 60);
                String old = String.format("%02d:%02d", record / 60, record % 60);
                txtRecord.setText("Récord: " + old + " || Tiempo: " + time);
                timer--;
                handler2.postDelayed(updateTimer2, 1000);
            }
            */
            if(finished) return;

            String time  = String.format("%02d:%02d", timer / 60, timer % 60);
            txtRecord.setText("Récord " + record + " completados || Tiempo: " + time);
            timer--;

            if (timer >= 0) {
                handler2.postDelayed(updateTimer2, 1000);
            } else {
                end();
                //clickFinish(null);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_order);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Todo en Orden");

        rlContent = (RelativeLayout)findViewById(R.id.rl_content);
        imgRail   = (ImageView)findViewById(R.id.img_rail);
        imgTruck  = (ImageView)findViewById(R.id.img_truck);
        txtRecord = (TextView)findViewById(R.id.txt_record);


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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            record = bundle.getInt("record");
        }

        txtRecord.setText("");

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
        finished = false;
        timer = max;

        handler1.postDelayed(updateTimer1, 0);
        handler2.postDelayed(updateTimer2, 0);

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

        started = true;
    }

    protected void end () {

        finished = true;

        JSONObject result  = new JSONObject();
        JSONObject answers = new JSONObject();
        JSONObject answer  = new JSONObject();

        //int less = (int)Math.floor((Math.min(missed, 100) * 25)/100);
        //points = 25 - less;
        points = 18 - truckImages.size();
        points = (int)(Math.floor(points * 200)/18) + timer;

        try {

            result.put("games_answerds", answers);
            answers.put("question_1", answer);
            answer.put("answerd_right", "1");
            answer.put("answerd", points);

        } catch (JSONException e) {}

        Map<String, Object> params = User.getToken(this);
        params.put("game_type", "todo-en-orden");
        params.put("json", result.toString());
        params.put("game_records", timer);

        WebBridge.send("webservices.php?task=addAnswerdGames", params, "Cargando", this, this);
        //

    }


    protected void create() {

        width        = rlContent.getWidth();
        height       = rlContent.getHeight();
        points       = 0;
        //missed       = 0;
        types        = new ArrayList<String>();
        images       = new BottleSetView[3];
        truckImages  = new ArrayList<ImageView>();
        bottleImages = new HashMap<String, ArrayList<BottleView>>();

        types.add("sm");
        types.add("md");
        types.add("bg");



        for(int i=0; i<types.size(); i++) {

            int r = getResources().getIdentifier("tv_set" + i, "id", getPackageName());

            images[i] = new BottleSetView(this);
            images[i].setTag(i);
            images[i].setType(types.get(i));
            images[i].setOut();
            images[i].setX(findViewById(r).getX());
            images[i].setY(findViewById(r).getY());
            images[i].setLevel(0);
            rlContent.addView(images[i]);

            new PanGestureRecognizer(images[i], this);
            images[i].setEnabled(false);

            bottleImages.put(types.get(i), new ArrayList<BottleView>());

        }
        /*
        images[0].setLevel(2);
        images[1].setLevel(3);
        images[2].setLevel(2);
        */
        images[0].setMaxLevel(3);
        images[1].setMaxLevel(5);
        images[2].setMaxLevel(3);


        int o    = 0;
        int resc = getResources().getIdentifier("image_game_order_sm_truck", "drawable", getPackageName());
        int truc = getResources().getDrawable(resc).getIntrinsicWidth() * 6;
        int posX = 0;
        int posY = (int)(imgTruck.getY() + 2);
        int truX = (int)(imgTruck.getX() + (imgTruck.getWidth() - truc)/2);
        int lgth = types.size() * 6;


        for (int i=0; i<lgth; i++) {

            int m = (int)Math.floor(i/6);
            String type = types.get(m);

            int r = getResources().getIdentifier("image_game_order_" + type + "_truck", "drawable", getPackageName());
            int b = getResources().getIdentifier("image_game_order_" + type + "_bottle", "drawable", getPackageName());
            int w = getResources().getDrawable(r).getIntrinsicWidth();
            int h = getResources().getDrawable(r).getIntrinsicHeight();

            o = i<6 ? h/2 : o;

            ImageView img = new ImageView(this);
            img.setImageResource(r);
            img.setX(posX + truX);
            img.setY(posY - o);

            rlContent.addView(img);
            posX = posX + w;
            truckImages.add(img);
            img.setTag(type);

            BottleView bottle = new BottleView(this);
            bottle.setImageResource(b);
            bottle.setX(-200);
            bottle.setType(type);
            bottleImages.get(type).add(bottle);
            rlContent.addView(bottle);

            if (posX >= truc) {
                posX = 0;
                posY = posY + h + 1;
            }

        }

        showInstructions(getString(R.string.txt_instructions), getString(R.string.txt_game_instructions_order), true);
        //findViewById(R.id.ll_instructions).setVisibility(View.GONE);
        //handler1.postDelayed(updateTimer1, 0);

    }


    protected void random() {

        String type = types.get(random.nextInt(types.size()));

        final BottleView img = bottleImages.get(type).get(0);
        bottleImages.get(type).remove(img);

        int w = img.getWidth();
        int h = img.getHeight();
        int y = (int)imgRail.getY() + (int)(imgRail.getHeight() * 0.42342342342342f) - h;

        img.setX(-w);
        img.setY(y);
        img.setEnabled(true);

        AnimatorSet set           = new AnimatorSet();
        ObjectAnimator translateX = ObjectAnimator.ofFloat(img, "translationX", -w, imgRail.getWidth() + imgRail.getWidth() * 0.18f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(img, "translationY", y, y + imgRail.getHeight() * 0.75f);
        ObjectAnimator rotation   = ObjectAnimator.ofFloat(img, "rotation", 0f, 45.0f);
        ObjectAnimator alpha      = ObjectAnimator.ofFloat(img, "alpha", 1.0f, 0.0f);

        translateX.setInterpolator(new LinearInterpolator());
        translateY.setInterpolator(new LinearInterpolator());

        translateX.setDuration(6000);
        translateY.setDuration(1000);
        rotation.setDuration(1000);
        alpha.setDuration(1000);

        translateY.setStartDelay(5000);
        rotation.setStartDelay(4850);
        alpha.setStartDelay(5000);

        set.playTogether(translateX, translateY, rotation, alpha);

        img.setAnimator(set);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                img.removeAnimator();
                img.setAlpha(1.0f);
                img.setRotation(0);
                img.setX(-200);
                bottleImages.get(img.getType()).add(img);
                /*
                if (started) {
                    missed++;
                }
                */
                //Log.e("", img.getType() + ":" + bottleImages.get(img.getType()).size());
            }
        });

        new PanGestureRecognizer(img, this);

        //rlContent.addView(img);
        set.start();

    }



    protected void hit(final BottleView v, boolean move) {

        for (int i=0; i< images.length; i++) {

            BottleSetView img = images[i];

            int[] location1 = new int[2];
            v.getLocationOnScreen(location1);

            int[] location2 = new int[2];
            img.getLocationOnScreen(location2);

            int posX = location1[0] + v.getWidth()/2;
            int posY = location1[1] + v.getHeight()/2;

            if (posX > location2[0] && posX < location2[0] + img.getWidth() && posY > location2[1] && posY < location2[1] + img.getHeight()) {

                if (!move) {
                    if (img.getLevel() < img.getMaxLevel()) {
                        img.setOver();
                    }
                } else {

                    Log.e("", v.getType() + ":" + img.getType() + " - " + img.getLevel() + ":" + img.getMaxLevel());
                    if (v.getType().equals(img.getType())) {
                        if (img.getLevel() + 1 <= img.getMaxLevel()) {
                            img.setLevel(img.getLevel()+1);
                            if (img.getLevel() == img.getMaxLevel()) {
                                img.setEnabled(true);
                            }
                        }
                    }
                }
                break;
            }

            img.setOut();
        }

        if (move) {

            AnimatorSet set = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0.0f);
            set.play(alpha);
            v.setAnimator(set);

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    v.removeAnimator();
                    v.setAlpha(1.0f);
                    v.setRotation(0);
                    v.setX(-200);
                    bottleImages.get(v.getType()).add(v);
                    //Log.e("", v.getType() + ":" + bottleImages.get(v.getType()).size());
                }
            });
            set.start();
        }

    }

    protected void hitSet(final BottleSetView v, boolean move) {

        final int r = getResources().getIdentifier("tv_set" + v.getTag().toString(), "id", getPackageName());

        for (int i=0; i< truckImages.size(); i++) {

            final ImageView img = truckImages.get(i);

            int[] location1 = new int[2];
            v.getLocationOnScreen(location1);

            int[] location2 = new int[2];
            img.getLocationOnScreen(location2);

            int posX = location1[0] + v.getWidth()/2;
            int posY = location1[1] + v.getHeight()/2;



            if (posX > location2[0] && posX < location2[0] + img.getWidth() && posY > location2[1] && posY < location2[1] + img.getHeight()) {

                if (!move) {
                    img.setAlpha(0.5f);
                    Log.e("", img.getTag().toString());
                } else {
                    if (v.getType().equals(img.getTag())) {

                        truckImages.remove(img);

                        ObjectAnimator movX = ObjectAnimator.ofFloat(v, "x", img.getX());
                        ObjectAnimator movY = ObjectAnimator.ofFloat(v, "y", img.getY());

                        movX.setDuration(500);
                        movY.setDuration(500);

                        v.setEnabled(false);
                        img.setAlpha(1.0f);


                        AnimatorSet mov = new AnimatorSet();
                        mov.play(movX).with(movY);
                        mov.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                int r1 = getResources().getIdentifier("image_game_order_" + v.getType() + "_" + v.getMaxLevel(), "drawable", getPackageName());
                                img.setImageResource(r1);
                                v.setX(findViewById(r).getX());
                                v.setY(findViewById(r).getY());
                                v.setLevel(0);
                                for (int j=0; j< truckImages.size(); j++) {
                                    truckImages.get(j).setAlpha(1.0f);
                                }
                                if (truckImages.size() == 0) {
                                    end();
                                }

                            }
                        });
                        mov.start();
                        return;
                    }
                }
                break;
            }
            img.setAlpha(1.0f);
        }

        if (move) {

            for (int i=0; i< truckImages.size(); i++) {
                //truckImages.get(i).setAlpha(1.0f);
            }

            ObjectAnimator retX = ObjectAnimator.ofFloat(v, "x", findViewById(r).getX());
            ObjectAnimator retY = ObjectAnimator.ofFloat(v, "y", findViewById(r).getY());

            retX.setDuration(300);
            retY.setDuration(300);

            AnimatorSet ret = new AnimatorSet();
            ret.play(retX).with(retY);
            ret.start();

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
            msg = json.getString("message");
        } catch (Exception e) {
        }

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
        } else {
            showInstructions(getString(R.string.txt_finished), "¡Ganaste \"" + points  + "\" puntos!", false);
        }
    }



	/*-------------------*/
	/* GESTURES LISTENER */

    @Override
    public void onPanStart(View v, float deltaX, float deltaY) {

        if (finished) return;

        if (v instanceof BottleView) {
            v.setAlpha(1.0f);
            v.setRotation(0.0f);
            ((BottleView)v).removeAnimator();
        }
        v.bringToFront();
        offsetY = rlContent.getY();

    }

    @Override
    public void onPanStop(View v, float deltaX, float deltaY) {

        if (finished) return;

        if (v instanceof BottleView) {
            v.setEnabled(false);
            hit((BottleView)v, true);
        } else if (v instanceof BottleSetView) {
            hitSet((BottleSetView)v, true);
        }

    }

    @Override
    public void onPanMove(View v, float deltaX, float deltaY) {

        if (finished) return;

        if (deltaX > rlContent.getWidth() - v.getWidth()/2 || deltaX < v.getWidth()/2) return;
        if (deltaY > rlContent.getHeight() - v.getHeight()/2 + offsetY || deltaY < offsetY + v.getHeight()/2) return;

        int posX = Math.round(deltaX) - v.getWidth()/2;
        int posY = Math.round(deltaY) - v.getHeight()/2;

        v.setX(posX);
        v.setY(posY - offsetY);

        if (v instanceof BottleView) {
            hit((BottleView)v, false);
        } else if (v instanceof BottleSetView) {
            hitSet((BottleSetView)v, false);
        }

    }

    @Override
    public void onLift(View v, float velocityX, float velocityY) {

    }

    @Override
    public void onDoubleTap(View v) {

    }
}
