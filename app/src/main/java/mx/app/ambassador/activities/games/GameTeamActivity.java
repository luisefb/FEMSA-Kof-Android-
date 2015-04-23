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
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/2/15.
 */
public class GameTeamActivity extends SectionActivity implements PanGestureListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    int width, height, points, record, timer, max = 40;
    float offsetY;
    boolean finished;
    RelativeLayout rlContent;
    LinearLayout llInstructions;

    ImageView[] images;
    Boolean[] answers;
    ImageView[] cards;

    ArrayList<HashMap<String, String>> data;
    String[] chars = new String[]{"presales", "storage", "autoservice", "delivery"};
    TextView txtRecord;


    Handler handler = new Handler();
    private Runnable updateTimer = new Runnable(){
        public void run(){

            if (finished) return;

            String time  = String.format("%02d:%02d", timer / 60, timer % 60);
            txtRecord.setText("Récord: " + record + " puntos || Tiempo: " + time);
            timer--;
            if (timer >= 0) {
                handler.postDelayed(updateTimer, 1000);
            } else {
                finished = true;
                end();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_team);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Equipa al Equipo");

        data = new ArrayList<HashMap<String, String>>();
        data.add(new HashMap<String, String>() {{ put("i", "1");  put("n", "storage");}});
        //data.add(new HashMap<String, String>() {{ put("i", "2");  put("n", "delivery");}});
        data.add(new HashMap<String, String>() {{ put("i", "3");  put("n", "presales");}});

        data.add(new HashMap<String, String>() {{ put("i", "4");  put("n", "autoservice");}});
        data.add(new HashMap<String, String>() {{ put("i", "5");  put("n", "presales");}});
        data.add(new HashMap<String, String>() {{ put("i", "6");  put("n", "delivery");}});

        data.add(new HashMap<String, String>() {{ put("i", "7");  put("n", "delivery");}});
        data.add(new HashMap<String, String>() {{ put("i", "8");  put("n", "storage");}});
        data.add(new HashMap<String, String>() {{ put("i", "9");  put("n", "storage");}});

        data.add(new HashMap<String, String>() {{ put("i", "10"); put("n", "delivery");}});
        data.add(new HashMap<String, String>() {{ put("i", "11"); put("n", "autoservice");}});
        data.add(new HashMap<String, String>() {{ put("i", "12"); put("n", "presales");}});

        data.add(new HashMap<String, String>() {{ put("i", "13"); put("n", "storage");}});
        data.add(new HashMap<String, String>() {{ put("i", "14"); put("n", "storage");}});
        //data.add(new HashMap<String, String>() {{ put("i", "15"); put("n", "presales");}});

        data.add(new HashMap<String, String>() {{ put("i", "16"); put("n", "storage");}});
        data.add(new HashMap<String, String>() {{ put("i", "17"); put("n", "delivery");}});
        data.add(new HashMap<String, String>() {{ put("i", "18"); put("n", "autoservice");}});

        data.add(new HashMap<String, String>() {{ put("i", "19"); put("n", "autoservice");}});
        data.add(new HashMap<String, String>() {{ put("i", "20"); put("n", "autoservice");}});
        data.add(new HashMap<String, String>() {{ put("i", "21"); put("n", "autoservice");}});
        data.add(new HashMap<String, String>() {{ put("i", "22"); put("n", "autoservice");}});

        data.add(new HashMap<String, String>() {{ put("i", "23"); put("n", "presales");}});
        data.add(new HashMap<String, String>() {{ put("i", "24"); put("n", "storage");}});


        random();

        rlContent = (RelativeLayout)findViewById(R.id.rl_content);
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

        for (int i = 0; i < cards.length; i++) {
            cards[i].setEnabled(true);
        }

        points = 0;

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
        handler.postDelayed(updateTimer, 0);

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


    protected void end() {

        JSONObject result  = new JSONObject();
        JSONObject items  = new JSONObject();

        for (int i=0; i<answers.length; i++) {

            if (answers[i] == null) {
                if (finished) continue;
                return;
            }

            try {

                JSONObject answer = new JSONObject();
                answer.put("answerd_right", answers[i] ? "1" : "0");
                answer.put("answerd", (i+1) + "");

                items.put("question_" + (i + 1), answer);

            } catch (JSONException e) {}
        }

        try {
            result.put("games_answerds", items);
        } catch (JSONException e) {}

        finished = true;
        points = (int)(Math.floor(points * 200)/9) + timer;

        Map<String, Object> params = User.getToken(this);
        params.put("game_type", "equipar-al-equipo");
        params.put("json", result.toString());
        params.put("game_records", points);

        WebBridge.send("webservices.php?task=addAnswerdGames", params, "Cargando", this, this);
    }

    protected void create() {

        width  = rlContent.getWidth();
        height = rlContent.getHeight();

        images      = new ImageView[chars.length];
        answers     = new Boolean[9];
        cards       = new ImageView[9];

        int size    = (int)Math.round((height * 0.9)/3.0);
        int gap     = (int)Math.round((height * 0.1)/4.0);
        int offset  = width - (size + gap) * 3;
        int space   = (int)Math.round((height * 0.9)/images.length);

        for (int i = 0; i < chars.length; i++) {

            int drawable = getResources().getIdentifier("image_game_team_char_" + chars[i], "drawable", getPackageName());
            float x = (i%2) * 180 + gap;
            float y = (float)(Math.floor(i/2) * (height/2)) + gap;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (height-gap)/2);
            ImageView img = new ImageView(this);
            img.setImageResource(drawable);
            img.setLayoutParams(params);
            img.setX(x);
            img.setY(y);
            img.setTag(chars[i]);
            img.setPadding(0,0,0,20);

            images[i] = img;

            rlContent.addView(img);

        }


        for (int i = 0; i < cards.length; i++) {

            HashMap<String, String> row = data.get(i);

            int drawable = getResources().getIdentifier("image_game_team_" + row.get("i"), "drawable", getPackageName());
            float x = (i%3) * (size + gap) + offset;
            float y = (float)(Math.floor(i/3) * (size + gap)) + gap;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            ImageView img = new ImageView(this);
            img.setImageResource(drawable);
            img.setLayoutParams(params);
            img.setX(x);
            img.setY(y);
            img.setTag(i);

            row.put("r", Integer.toString(i));

            cards[i] = img;
            img.setEnabled(false);

            rlContent.addView(img);

            new PanGestureRecognizer(img, this);
        }

        showInstructions(getString(R.string.txt_instructions), getString(R.string.txt_game_instructions_team), true);

    }


    protected void random() {
        int index;
        HashMap<String, String> temp;
        Random random = new Random();
        for (int i = data.size() - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = data.get(index);
            data.set(index, data.get(i));
            data.set(i, temp);
        }
    }


    protected void hit(View v, boolean move) {

        for (int i=0; i< images.length; i++) {

            final ImageView img = images[i];
            img.setAlpha(1.0f);

            int[] location1 = new int[2];
            v.getLocationOnScreen(location1);

            int[] location2 = new int[2];
            img.getLocationOnScreen(location2);

            int posX = location1[0] + v.getWidth()/2;
            int posY = location1[1] + v.getHeight()/2;

            if (posX > location2[0] && posX < location2[0] + img.getWidth() && posY > location2[1] && posY < location2[1] + img.getHeight()) {

                if (!move) {
                    img.setAlpha(0.5f);
                } else {

                    float offX = (img.getWidth() - v.getWidth())/2;
                    float offY = (img.getHeight() - v.getHeight())/2;

                    float piX = img.getX() - v.getX() + offX;
                    float piY = img.getY() - v.getY() + offY;

                    final ImageView tile  = (ImageView)v;
                    final float pfX = img.getX() + offX;
                    final float pfY = img.getY() + offY;

                    img.setAlpha(1.0f);

                    TranslateAnimation position = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, piX,
                                                                         Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, piY);

                    position.setDuration(500);
                    position.setFillEnabled(true);
                    position.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tile.setX(pfX);
                            tile.setY(pfY);
                            validate(tile, img);
                        }
                    });
                    v.startAnimation(position);

                }
                return;
            }
        }

        if (move) {
            reset((ImageView) v);
        }
    }

    protected void reset(final ImageView v) {

        HashMap<String, String> row = data.get( Integer.parseInt(v.getTag().toString()) );

        int i       = Integer.parseInt(row.get("r").toString());
        int size    = (int)Math.round((height * 0.9)/3.0);
        int gap     = (int)Math.round((height * 0.1)/4.0);
        int offset  = width - (size + gap) * 3;

        final float x = (i%3) * (size + gap) + offset;
        final float y = (float)(Math.floor(i/3) * (size + gap)) + gap;

        TranslateAnimation p2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, x - v.getX(), Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, y - v.getY());
        p2.setDuration(500);
        p2.setFillEnabled(true);
        p2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setX(x);
                v.setY(y);
            }
        });
        v.startAnimation(p2);
    }

    protected void validate (ImageView card, ImageView chr) {

        int index  = Integer.parseInt(card.getTag().toString());
        String cat = chr.getTag().toString();

        HashMap<String, String> row = data.get(index);

        final ImageView icon = new ImageView(this);
        Drawable d = getResources().getDrawable(R.drawable.icon_game_success);
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();

        if (row.get("n").equals(cat)) {
            icon.setImageResource(R.drawable.icon_game_success);
            answers[index] = true;
            points++;
        } else {
            icon.setImageResource(R.drawable.icon_game_error);
            answers[index] = false;
        }

        icon.setX( chr.getX() + (chr.getWidth() - w)/2  );
        icon.setY( chr.getY() + (chr.getHeight() - h)/2  );
        rlContent.addView(icon);

        AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);
        a.setDuration(300);
        a.setStartOffset(250);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                remove(icon, 500);
            }
        });
        icon.startAnimation(a);

        remove(card, 0);

        Log.e("VALIDATE", answers.toString());

    }

    protected void remove(final View v, final int delay) {
        AlphaAnimation a = new AlphaAnimation(1.0f, 0.0f);
        a.setDuration(300);
        a.setStartOffset(delay);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                rlContent.removeView(v);
                if (delay == 500) {
                    end();
                }
            }
        });
        v.startAnimation(a);
    }



	/*-------------------*/
	/* GESTURES LISTENER */

    @Override
    public void onPanStart(View v, float deltaX, float deltaY) {
        if (finished) return;
        v.bringToFront();
        offsetY = rlContent.getY();
    }

    @Override
    public void onPanStop(View v, float deltaX, float deltaY) {
        hit(v, true);
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
