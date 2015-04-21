package mx.app.ambassador.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.info.InfoMapsActivity;

/**
 * Created by kreativeco on 07/04/15.
 */
public class InfoActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    RelativeLayout rlInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Día en la Operación");

        rlInfo = (RelativeLayout) findViewById(R.id.rl_info);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rlInfo.getLayoutParams();
        params.topMargin = getStatusBarHeight();
        rlInfo.setLayoutParams(params);

    }



    /*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {

        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        if (selected == 1) {
            show();
        } else if (selected == 2) {
            nav = new Intent(InfoActivity.this, ChecklistActivity.class);
        } else if (selected == 3) {
            nav = new Intent(InfoActivity.this, InfoMapsActivity.class);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(nav, 1);

    }


    public void clickHide(View v){

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlInfo, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                rlInfo.setVisibility(View.GONE);
            }
        });
        alpha1.start();
    }


	/*----------------*/
	/* CUSTOM METHODS */

    protected void show() {

        rlInfo.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlInfo, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlInfo, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlInfo, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }





}
