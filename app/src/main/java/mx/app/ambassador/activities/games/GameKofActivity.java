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
import android.view.Gravity;
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
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/2/15.
 */
public class GameKofActivity extends SectionActivity implements PanGestureListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    int width, height, space, points, record, timer, max = 40;
    float offsetY;
    boolean finished;
    RelativeLayout rlContent;
    LinearLayout llInstructions;
    ImageView[] images;
    TextView[] texts;
    TextView[] answers;
    ArrayList<HashMap<String, String>> data;
    Button btFinish;
    TextView txtRecord;


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
        setContentView(R.layout.activity_game_kof);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Código KOF");

        data = new ArrayList<HashMap<String, String>>();
        data.add(new HashMap<String, String>() {{ put("i", "1");  put("n", "Hoja de Carga");}});
        data.add(new HashMap<String, String>() {{ put("i", "2");  put("n", "Autoservicio");}});
        data.add(new HashMap<String, String>() {{ put("i", "3");  put("n", "Consumidor");}});
        data.add(new HashMap<String, String>() {{ put("i", "4");  put("n", "Ruta");}});
        data.add(new HashMap<String, String>() {{ put("i", "5");  put("n", "Embajador");}});
        data.add(new HashMap<String, String>() {{ put("i", "6");  put("n", "Portafolio");}});
        data.add(new HashMap<String, String>() {{ put("i", "7");  put("n", "Cliente");}});
        data.add(new HashMap<String, String>() {{ put("i", "8");  put("n", "DILO");}});
        data.add(new HashMap<String, String>() {{ put("i", "9");  put("n", "Producto o Marca");}});
        data.add(new HashMap<String, String>() {{ put("i", "10"); put("n", "Comunidad");}});
        data.add(new HashMap<String, String>() {{ put("i", "12"); put("n", "Multiserve");}});
        data.add(new HashMap<String, String>() {{ put("i", "13"); put("n", "Planta");}});
        data.add(new HashMap<String, String>() {{ put("i", "14"); put("n", "Sabores");}});
        data.add(new HashMap<String, String>() {{ put("i", "15"); put("n", "CMTO");}});
        data.add(new HashMap<String, String>() {{ put("i", "16"); put("n", "Centro de Canje");}});
        data.add(new HashMap<String, String>() {{ put("i", "17"); put("n", "Crédito Formal");}});
        data.add(new HashMap<String, String>() {{ put("i", "18"); put("n", "Botelleo");}});
        data.add(new HashMap<String, String>() {{ put("i", "19"); put("n", "Café Blak");}});
        data.add(new HashMap<String, String>() {{ put("i", "20"); put("n", "Caja");}});
        data.add(new HashMap<String, String>() {{ put("i", "21"); put("n", "Caja Unidad");}});
        data.add(new HashMap<String, String>() {{ put("i", "22"); put("n", "Combo");}});
        data.add(new HashMap<String, String>() {{ put("i", "23"); put("n", "Efectividad");}});
        data.add(new HashMap<String, String>() {{ put("i", "24"); put("n", "FPC");}});
        data.add(new HashMap<String, String>() {{ put("i", "25"); put("n", "Hola Coca Cola");}});
        data.add(new HashMap<String, String>() {{ put("i", "26"); put("n", "Inventario Físico");}});
        data.add(new HashMap<String, String>() {{ put("i", "27"); put("n", "Pallet");}});
        data.add(new HashMap<String, String>() {{ put("i", "28"); put("n", "Pet");}});
        data.add(new HashMap<String, String>() {{ put("i", "29"); put("n", "Producto Extraño");}});
        data.add(new HashMap<String, String>() {{ put("i", "30"); put("n", "Material POP");}});
        data.add(new HashMap<String, String>() {{ put("i", "31"); put("n", "Zapatos de Seguridad");}});
        data.add(new HashMap<String, String>() {{ put("i", "32"); put("n", "Franela");}});
        data.add(new HashMap<String, String>() {{ put("i", "33"); put("n", "Hand Held");}});
        data.add(new HashMap<String, String>() {{ put("i", "34"); put("n", "Enfriador");}});
        data.add(new HashMap<String, String>() {{ put("i", "35"); put("n", "Isla");}});
        data.add(new HashMap<String, String>() {{ put("i", "36"); put("n", "Credencial");}});

        //data.add(new HashMap<String, String>() {{ put("i", "11"); put("n", "Base de Datos");}});

        random();

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

        txtRecord.setText("");
        btFinish.setVisibility(View.GONE);
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

        for (int i = 0; i < texts.length; i++) {
            TextView txt = texts[i];
            txt.setEnabled(true);
        }

        timer = max;
        handler.postDelayed(updateTimer, 0);

    }

    public void clickFinish(View v) {

        finished = true;

        Drawable d = getResources().getDrawable(R.drawable.icon_game_success);
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();

        final JSONObject result  = new JSONObject();
        AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);;

        for (int i=0; i<answers.length; i++) {

            HashMap<String, String> row = data.get(i);
            ImageView img = new ImageView(this);
            ImageView ans = images[i];

            if (answers[i] == null) continue;

            boolean success = answers[i].getTag().equals(row.get("i"));

            if (success) {
                img.setImageResource(R.drawable.icon_game_success);
                points++;
            } else {
                img.setImageResource(R.drawable.icon_game_error);
            }
            answers[i].setEnabled(false);
            img.setX(ans.getX() + (ans.getWidth() - w)/2);
            img.setY(ans.getY() + (ans.getHeight() - h) / 2);

            a = new AlphaAnimation(0.0f, 1.0f);
            a.setDuration(300);
            a.setStartOffset(150 * i);

            try {

                JSONObject answer  = new JSONObject();
                result.put("answerd_right", success ? "1" : "0");
                answer.put("answerd", row.get("n"));

                result.put("question_" + (i+1), answer);

            } catch (JSONException e) {}

            /*
            if (i == answers.length - 1) {
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        end(result);
                    }
                });
            }
            */

            img.startAnimation(a);
            rlContent.addView(img);
        }

        if (a != null) {
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    end(result);
                }
            });
        }

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

    protected void end (JSONObject json) {

        JSONObject result  = new JSONObject();

        try {
            result.put("games_answerds", json);
        } catch (JSONException e) {}

        points = (int)(Math.floor(points * 200)/9) + timer;

        Map<String, Object> params = User.getToken(this);
        params.put("game_type", "codigo-kof");
        params.put("game_records", points);
        params.put("json", result.toString());

        WebBridge.send("webservices.php?task=addAnswerdGames", params, "Cargando", this, this);
    }

    protected void create() {

        width  = rlContent.getWidth();
        height = rlContent.getHeight();

        int[] rand  = new int[]{0,1,2,3,4,5,6,7,8};
        images      = new ImageView[9];
        texts       = new TextView[images.length];
        answers     = new TextView[images.length];
        space   = (int)Math.round((height * 0.95)/images.length);

        int size    = (int)Math.round((height * 0.9)/3.0);
        int gap     = (int)Math.round((height * 0.1)/4.0);
        int offset  = width - (size + gap) * 3;

        shuffle(rand);

        for (int i = 0; i < images.length; i++) {

            HashMap<String, String> row = data.get(i);

            int drawable = getResources().getIdentifier("image_game_kof_" + row.get("i"), "drawable", getPackageName());
            float x = (i%3) * (size + gap) + offset;
            float y = (float)(Math.floor(i/3) * (size + gap)) + gap;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            ImageView img = new ImageView(this);
            img.setImageResource(drawable);
            img.setLayoutParams(params);
            img.setX(x);
            img.setY(y);

            TextView txt = (TextView)getLayoutInflater().inflate(R.layout.ui_game_kof_item, null);
            txt.setText(row.get("n"));
            txt.setX(20);
            txt.setY((float)(space * rand[i] + 20));
            txt.setGravity(Gravity.CENTER);

            images[i] = img;
            texts[i]  = txt;

            img.setTag(i);
            txt.setTag( row.get("i") );
            row.put("r", Integer.toString(rand[i]));

            rlContent.addView(img);
            rlContent.addView(txt);

            txt.setEnabled(false);
            new PanGestureRecognizer(txt, this);
        }

        showInstructions(getString(R.string.txt_instructions), getString(R.string.txt_game_instructions_kof), true);

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

    private void shuffle(int[] array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
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

                int index = Integer.parseInt(img.getTag().toString());

                if (!move) {
                    img.setAlpha(0.5f);
                } else {

                    if (answers[index] != null) {
                        reset(answers[index], v.getTag().toString());
                    }

                    answers[index] = (TextView)v;

                    float offX = (img.getWidth() - v.getWidth())/2;
                    float offY = (int)((img.getHeight() - v.getHeight()) * 0.95);

                    float piX = img.getX() - v.getX() + offX;
                    float piY = img.getY() - v.getY() + offY;

                    final TextView txt  = (TextView)v;
                    final float pfX = img.getX() + offX;
                    final float pfY = img.getY() + offY;

                    img.setAlpha(1.0f);

                    TranslateAnimation p2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, piX, Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, piY);
                    p2.setDuration(500);
                    p2.setFillEnabled(true);
                    p2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            txt.setX(pfX);
                            txt.setY(pfY);
                            validate();
                        }
                    });
                    v.startAnimation(p2);

                }
                break;
            }
        }
    }

    protected void reset(final TextView v, String rand) {

        float posY = 0;
        for (int i = 0; i < images.length; i++) {
            HashMap<String, String> row = data.get(i);
            if (row.get("i").equals(rand)) {
                posY = (float)(space * Integer.parseInt(row.get("r").toString()) + 20);
            }
        }

        final float destY = posY;

        TranslateAnimation p2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, 20 - v.getX(), Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, destY - v.getY());
        p2.setDuration(500);
        p2.setFillEnabled(true);
        p2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setX(20);
                v.setY(destY);
                validate();
            }
        });
        v.startAnimation(p2);
    }

    protected void validate () {

        boolean flag = true;

        for (int i=0; i<answers.length; i++) {
            if (answers[i] == null) {
                flag = false;
                break;
            }
        }

        if (flag) {
            btFinish.setVisibility(View.VISIBLE);
            AlphaAnimation a = new AlphaAnimation(btFinish.getAlpha(), 1.0f);
            a.setDuration(300);
            btFinish.startAnimation(a);
        } else {
            AlphaAnimation a = new AlphaAnimation(btFinish.getAlpha(), 0.0f);
            a.setDuration(300);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    btFinish.setVisibility(View.GONE);
                }
            });
            btFinish.startAnimation(a);
        }


    }



	/*-------------------*/
	/* GESTURES LISTENER */

    @Override
    public void onPanStart(View v, float deltaX, float deltaY) {
        v.bringToFront();
        offsetY = rlContent.getY();
    }

    @Override
    public void onPanStop(View v, float deltaX, float deltaY) {
        for (int i=0; i<answers.length; i++) {
            if (answers[i] == v) {
                answers[i] = null;
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

