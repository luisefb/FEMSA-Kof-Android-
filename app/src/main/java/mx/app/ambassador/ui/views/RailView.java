package mx.app.ambassador.ui.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import mx.app.ambassador.R;

/**
 * Created by noisedan on 4/5/15.
 */
public class RailView extends RelativeLayout implements View.OnClickListener {

    int wRail;
    int duration = 4500;
    int max;
    RelativeLayout rlRails;
    RelativeLayout rlProducts;
    String direction;
    ArrayList<ImageView> images;
    Random random = new Random();
    int height;
    int offset = 200;
    OnRailProductListener listener;

    Handler handler = new Handler();
    private Runnable updateData = new Runnable(){
        public void run(){
            create();
            handler.postDelayed(updateData, 1600 - random.nextInt(200));
        }
    };

    public RailView(Context context, int h, String d, ArrayList<HashMap<String, String>> data, OnRailProductListener l) {

        super(context);

        direction = d;
        height    = h;
        max       = data.size();
        listener  = l;

        int w1 = getResources().getDrawable(R.drawable.image_game_products_top).getIntrinsicWidth();
        int w2 = getResources().getDrawable(R.drawable.image_game_products_rail).getIntrinsicWidth();
        int h1 = getResources().getDrawable(R.drawable.image_game_products_top).getIntrinsicHeight();
        int h2 = getResources().getDrawable(R.drawable.image_game_products_rail).getIntrinsicHeight();

        rlRails    = new RelativeLayout(context);
        rlProducts = new RelativeLayout(context);

        ImageView plataform = new ImageView(context);
        plataform.setX(0);
        rlRails.setY(-height);

        int posY;
        if (direction.equals("down")) {
            plataform.setY( height - h1 );
            plataform.setImageResource(R.drawable.image_game_products_bottom);
            posY = height;
        } else {
            plataform.setY(0);
            plataform.setImageResource(R.drawable.image_game_products_top);
            posY = -offset;
        }

        int total = 0;
        while (total < height * 3) {
            ImageView rail = new ImageView(context);
            rail.setImageResource(R.drawable.image_game_products_rail);
            rail.setY(total);
            rail.setX( (w1 - w2)/2 );
            total += h2;
            rlRails.addView(rail);
        }

        images = new ArrayList<ImageView>();

        for (int i=0; i<max; i++) {

            HashMap<String, String> row = data.get(i);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);

            ImageView img = new ImageView(context);
            int r = getResources().getIdentifier("image_game_products_" + row.get("i"), "drawable", getContext().getPackageName());
            img.setImageResource(r);
            img.setLayoutParams(params);
            img.setOnClickListener(this);
            img.setY(posY);
            img.setTag(row.get("n"));

            images.add(img);
            rlProducts.addView(img);

        }

        rlRails.setLayoutParams(new RelativeLayout.LayoutParams(w1, total));
        rlProducts.setLayoutParams(new RelativeLayout.LayoutParams(w1, total));
        rlProducts.setGravity(Gravity.CENTER_HORIZONTAL);

        addView(rlRails);
        addView(rlProducts);
        addView(plataform);

        setLayoutParams(new RelativeLayout.LayoutParams(w1, total));

        wRail = h2;

        move();

        handler.postDelayed(updateData, 0);

    }

    protected void create () {

        if (images.size() == 0) {
            return;
        }

        int destination, origin;
        if (direction.equals("down")) {
            origin = height;
            destination = -offset;
        } else {
            origin = -offset;
            destination = height;
        }

        int newDuration = ((height + offset) * duration)/wRail;
        int item = random.nextInt(images.size());
        final ImageView img = images.get(item);
        img.setEnabled(true);
        img.setAlpha(1.0f);
        images.remove(img);

        img.setRotation( random.nextInt(90) - 45);

        ObjectAnimator p = ObjectAnimator.ofFloat(img, "translationY", origin, destination);
        p.setInterpolator(new LinearInterpolator());
        p.setDuration(newDuration);
        p.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                images.add(img);
            }

        });
        p.start();

    }

    protected void move () {

        int destination = wRail;
        if (direction.equals("down")) {
            destination = -wRail;
        }

        TranslateAnimation p2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.ABSOLUTE, destination);
        p2.setInterpolator(new LinearInterpolator());
        p2.setDuration(duration);
        p2.setFillEnabled(true);
        p2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                move();
            }
        });
        rlRails.startAnimation(p2);
    }


    @Override
    public void onClick(View v) {
        v.setEnabled(false);

        AlphaAnimation a = new AlphaAnimation(1.0f, 0.0f);
        a.setDuration(300);
        a.setFillAfter(true);
        v.startAnimation(a);

        if (listener != null) {
            listener.onRailProductClick(v.getTag().toString());
        }

    }

    public interface OnRailProductListener {
        void onRailProductClick(String type);
    }

}
