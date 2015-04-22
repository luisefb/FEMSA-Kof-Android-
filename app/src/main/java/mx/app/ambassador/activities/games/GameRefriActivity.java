package mx.app.ambassador.activities.games;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
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
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.SectionActivity;
import mx.app.ambassador.gestures.PanGestureListener;
import mx.app.ambassador.gestures.PanGestureRecognizer;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/2/15.
 */
public class GameRefriActivity extends SectionActivity implements PanGestureListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    int width, height, size, rack, border, total, points, timer, record, spaces = 4, max = 120;
    float offsetY;
    boolean finished;
    RelativeLayout rlContent;
    ImageView imgRefri;
    LinearLayout llInstructions;
    Button btFinish;
    TextView txtRecord;

    float[] rackRefri = new float[]{0.3417f, 0.5794f, 0.7949f};
    float[] rackWall  = new float[3];
    int[] rackSpaces  = new int[]{5, 6, 4};

    ImageView[] images;
    ArrayList<HashMap<String, String>> data;
    //ArrayList<HashMap<String, String>> answers;
    ArrayList<Integer> answers;

    Handler handler = new Handler();
    private Runnable updateTimer = new Runnable(){
        public void run(){

            if(finished) return;

            String time  = String.format("%02d:%02d", timer / 60, timer % 60);
            txtRecord.setText("Récord " + record + " aciertos || Tiempo: " + time);
            timer--;

            if (timer >= 0) {
                handler.postDelayed(updateTimer, 1000);
            } else {
                clickFinish(null);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_refri);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Reto Refri");

        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        items.add(new HashMap<String, String>() {{ put("i", "1");  put("n", "yes");  put("t", "1"); }});
        items.add(new HashMap<String, String>() {{ put("i", "2");  put("n", "yes");  put("t", "1"); }});
        items.add(new HashMap<String, String>() {{ put("i", "3");  put("n", "yes");  put("t", "1"); }});
        items.add(new HashMap<String, String>() {{ put("i", "4");  put("n", "yes");  put("t", "1"); }});
        items.add(new HashMap<String, String>() {{ put("i", "5");  put("n", "yes");  put("t", "1"); }});
        items.add(new HashMap<String, String>() {{ put("i", "6");  put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "7");  put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "8");  put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "9");  put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "10"); put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "11"); put("n", "yes");  put("t", "2"); }});
        items.add(new HashMap<String, String>() {{ put("i", "12"); put("n", "yes");  put("t", "3"); }});
        items.add(new HashMap<String, String>() {{ put("i", "13"); put("n", "yes");  put("t", "3"); }});
        items.add(new HashMap<String, String>() {{ put("i", "14"); put("n", "yes");  put("t", "3"); }});
        items.add(new HashMap<String, String>() {{ put("i", "15"); put("n", "yes");  put("t", "3"); }});
        items.add(new HashMap<String, String>() {{ put("i", "16"); put("n", "no");   put("t", "0"); }});
        items.add(new HashMap<String, String>() {{ put("i", "17"); put("n", "no");   put("t", "0"); }});
        items.add(new HashMap<String, String>() {{ put("i", "18"); put("n", "no");   put("t", "0"); }});
        items.add(new HashMap<String, String>() {{ put("i", "19"); put("n", "no");   put("t", "0"); }});
        items.add(new HashMap<String, String>() {{ put("i", "20"); put("n", "no");   put("t", "0"); }});
        items.add(new HashMap<String, String>() {{ put("i", "21"); put("n", "no");   put("t", "0"); }});
        //items.add(new HashMap<String, String>() {{ put("i", "22"); put("n", "no");   put("t", "0"); }});

        data = random(items);

        rlContent = (RelativeLayout)findViewById(R.id.rl_content);
        btFinish  = (Button)findViewById(R.id.bt_finish);
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

        btFinish.setEnabled(false);
        txtRecord.setText("");

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickStart (View v) {

        for (int i = 0; i < images.length; i++) {
            images[i].setEnabled(true);
        }

        btFinish.setEnabled(true);

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

        timer = max;
        finished = false;
        handler.postDelayed(updateTimer, 0);
        btFinish.setEnabled(true);

    }

    public void clickFinish(View v) {

        finished = true;
        points   = 0;

        for (int i = 0; i < images.length; i++) {
            images[i].setEnabled(false);
        }

        Drawable d = getResources().getDrawable(R.drawable.icon_game_success_sm);
        int width = d.getIntrinsicWidth();
        float x = imgRefri.getX() + border;
        AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);

        for (int i = 0; i < answers.size(); i++) {

            ImageView img = new ImageView(this);

            Log.e("", (answers.get(i)-1) + "==" + i);

            if (answers.get(i)-1 == i) {
                img.setImageResource(R.drawable.icon_game_success_sm);
                points++;
            } else {
                img.setImageResource(R.drawable.icon_game_error_sm);
            }

            int b = 0;
            for (int j=0; j<rackSpaces.length; j++) {

                int w   = (imgRefri.getWidth() - border * 2)/rackSpaces[j];
                int o   = (w - width)/2;
                img.setX( x + ((i - b) * w) + o );
                b = b + rackSpaces[j];
                img.setY( imgRefri.getY() + rackRefri[j] - 20 );

                if (b > i) break;
            }

            a = new AlphaAnimation(0.0f, 1.0f);
            a.setDuration(300);
            a.setStartOffset(150 * i);

            img.startAnimation(a);
            rlContent.addView(img);

        }

        btFinish.setEnabled(false);
        if (a != null) {
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    end();
                }
            });
        }

    }

    public void end() {

        points = (int)(Math.floor(points * 200)/answers.size()) + timer;

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
        params.put("game_type", "reto-refri");
        params.put("json", result.toString());
        params.put("game_records", points);

        WebBridge.send("webservices.php?task=addAnswerdGames", params, "Cargando", this, this);

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

    protected void create() {

        Drawable drack = getResources().getDrawable(R.drawable.image_game_refri_rack);

        width   = rlContent.getWidth();
        height  = rlContent.getHeight();
        size    = (int)Math.round(height/spaces);
        rack    = drack.getIntrinsicHeight();
        answers = new ArrayList<Integer>();
        images  = new ImageView[data.size()];

        for (int i=1; i<spaces; i++) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT);

            ImageView bar = new ImageView(this);
            bar.setImageResource(R.drawable.image_game_refri_rack);
            bar.setY(size * i - rack);
            bar.setLayoutParams(params);
            bar.setScaleType(ImageView.ScaleType.FIT_XY);

            rackWall[i-1] = size * i - rack;

            rlContent.addView(bar);
        }


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, size);
        ImageView floor = new ImageView(this);
        floor.setImageResource(R.drawable.image_game_refri_floor);
        floor.setScaleType(ImageView.ScaleType.FIT_XY);
        floor.setLayoutParams(params);
        floor.setY(size * rackWall.length);
        rlContent.addView(floor);


        Drawable drefri = getResources().getDrawable(R.drawable.image_game_refri_refri);
        int wrefri = drefri.getIntrinsicWidth();
        int hrefri = drefri.getIntrinsicHeight();
        ImageView refri = new ImageView(this);
        refri.setImageResource(R.drawable.image_game_refri_refri);
        refri.setY( (float)(size * rackWall.length + size * 0.75) - hrefri);
        refri.setX( (float)(width - wrefri * 1.3));
        rlContent.addView(refri);
        imgRefri = refri;

        for (int i=0; i<rackRefri.length; i++) {
            rackRefri[i] = hrefri * rackRefri[i] + refri.getY();
        }

        border = (int)Math.ceil(wrefri * 0.08064516129032);

        total = 0;
        int offsetX = 0;

        int perrow  = (int)Math.ceil(data.size()/(spaces-1));


        for (int i = 0; i < data.size(); i++) {

            if (i%perrow==0) {
                offsetX = 0;
            }

            HashMap<String, String> row = data.get(i);
            int drawable = getResources().getIdentifier("image_game_refri_" + row.get("i"), "drawable", getPackageName());

            Drawable d = getResources().getDrawable(drawable);
            int w = d.getIntrinsicWidth();
            int h = d.getIntrinsicHeight();

            ImageView img = new ImageView(this);
            img.setImageResource(drawable);
            img.setX( offsetX + 20);
            img.setY( (float) (Math.floor(i/perrow) * size) + size - h -  rack);
            img.setTag(i);

            offsetX += w + 20;

            rlContent.addView(img);

            img.setEnabled(false);

            new PanGestureRecognizer(img, this);

            if (row.get("n").equals("yes")) {
                total++;
                answers.add(-1);
            }

            images[i] = img;

        }

        showInstructions(getString(R.string.txt_instructions), getString(R.string.txt_game_instructions_refri), true);

    }


    protected ArrayList<HashMap<String, String>> random(ArrayList<HashMap<String, String>> images) {
        long seed = System.nanoTime();
        Collections.shuffle(images, new Random(seed));
        return images;
    }


    protected void hit(View v, boolean move) {

        imgRefri.setAlpha(1.0f);

        int[] location1 = new int[2];
        v.getLocationOnScreen(location1);

        int[] location2 = new int[2];
        imgRefri.getLocationOnScreen(location2);

        int posX = location1[0] + v.getWidth()/2;
        int posY = location1[1] + v.getHeight()/2;

        if (posX > location2[0] && posX < location2[0] + imgRefri.getWidth() && posY > location2[1] && posY < location2[1] + imgRefri.getHeight()) {

            if (!move) {
                imgRefri.setAlpha(0.65f);
            } else {

                if (location1[0] < location2[0] + border) {
                    v.setX(location2[0] + border);
                } else if (location1[0] + v.getWidth() > location2[0] + imgRefri.getWidth() - border) {
                    v.setX(location2[0] + imgRefri.getWidth() - border - v.getWidth());
                }
                bounce(v, rackRefri, false);

            }

        } else {
            if (move) {
                if (location1[0] + v.getWidth() > location2[0] && location1[0] < location2[0] + border) {
                    v.setX(location2[0] - v.getWidth());
                } else if (location1[0] < location2[0] + imgRefri.getWidth() && location1[0] > location2[0]) {
                    v.setX(location2[0] + imgRefri.getWidth());
                }
                bounce(v, rackWall, true);
            }
        }

    }

    protected void reset(final View v) {

        final HashMap<String, String> row = data.get( Integer.parseInt(v.getTag().toString()) );

        AlphaAnimation a = new AlphaAnimation(1.0f, 0.0f);
        a.setDuration(150);
        a.setRepeatMode(Animation.REVERSE);
        a.setRepeatCount(1);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {
                v.setX(Float.parseFloat(row.get("x").toString()));
                v.setY(Float.parseFloat(row.get("y").toString()));
            }
            @Override
            public void onAnimationEnd(Animation animation) {}
        });

        v.startAnimation(a);

    }

    protected void bounce(final View v, float[] pos, boolean outside) {

        int index = Integer.parseInt(v.getTag().toString());
        HashMap<String, String> row = data.get(index);

        int min = 0;
        int max = Integer.parseInt(row.get("t"));
        int rac = 0;

        min = max == 0 || outside ? 0 : max - 1;
        max = max == 0 || outside ? pos.length : max;

        int y = 0;
        for (int i=min; i<max; i++) {
            if (v.getY() + v.getHeight() < pos[i]) {
                y = (int)(pos[i] - v.getHeight());
                rac = i;
                break;
            }
        }

        if (!outside) {

            int b   = 0;
            int s   = 0;
            int l   = rackSpaces[rac];
            int w   = (imgRefri.getWidth() - border * 2)/l;
            float o = imgRefri.getX() + border;
            float c = v.getX() + v.getWidth() / 2;
            float x = 0;

            for (int i=0; i<l; i++) {
                int g = i*w;
                if (c >= o+g && c < o+(i+1)*w) {
                    b = i;
                    x = o + g + (w - v.getWidth())/2;
                    break;
                }
            }

            for (int i=0; i<rac; i++) {
                s = s + rackSpaces[i];
            }

            if (answers.get(s + b) == -1) {
                v.setX(x);
                answers.set(s + b, Integer.parseInt(row.get("i")));
            } else {
                y = 0;
            }
        }

        if (y == 0) {
            reset(v);
            return;
        }

        HashMap<String, String> img = data.get( Integer.parseInt(v.getTag().toString()) );

        if (img.get("t").equals("dwn")) {
            y = (int)(pos[pos.length-1] - v.getHeight());
        }

        v.setEnabled(false);
        final int posY = y;

        TranslateAnimation translation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, posY - v.getY());
        translation.setDuration(1000);
        translation.setFillEnabled(true);
        translation.setInterpolator(new BounceInterpolator());
        translation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setY(posY);
                v.setEnabled(true);
                //validate();
            }
        });
        v.startAnimation(translation);
    }



	/*-------------------*/
	/* GESTURES LISTENER */

    @Override
    public void onPanStart(View v, float deltaX, float deltaY) {

        v.bringToFront();
        offsetY = rlContent.getY();

        HashMap<String, String> row = data.get( Integer.parseInt(v.getTag().toString()) );
        row.put("x", Float.toString(v.getX()));
        row.put("y", Float.toString(v.getY()));

    }

    @Override
    public void onPanStop(View v, float deltaX, float deltaY) {
        HashMap<String, String> row = data.get(Integer.parseInt(v.getTag().toString()));
        int index = Integer.parseInt(row.get("i"));
        for (int i=0; i<answers.size(); i++) {
            if (answers.get(i).equals(index)) {
                answers.set(i, -1);
                break;
            }
        }
        hit(v, true);
    }

    @Override
    public void onPanMove(View v, float deltaX, float deltaY) {

        if (deltaX > rlContent.getWidth() - v.getWidth()/2 || deltaX < v.getWidth()/2) return;
        if (deltaY > rlContent.getHeight() - v.getHeight()/2 + offsetY || deltaY < offsetY + v.getHeight()/2) return;

        int posX = Math.round(deltaX) - v.getWidth()/2;
        int posY = Math.round(deltaY) - v.getHeight()/2;

        v.setX(posX);
        v.setY(posY - offsetY);

        hit(v, false);
    }

    @Override
    public void onLift(View v, float velocityX, float velocityY) {
    }

    @Override
    public void onDoubleTap(View v) {
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
