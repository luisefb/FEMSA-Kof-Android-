package mx.app.ambassador.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.info.InfoMapsActivity;

/**
 * Created by kreativeco on 07/04/15.
 */
public class InfoActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    RelativeLayout rlTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Información");

        rlTable = (RelativeLayout) findViewById(R.id.rl_table);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rlTable.getLayoutParams();
        params.topMargin = getStatusBarHeight();
        rlTable.setLayoutParams(params);

    }



    /*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {

        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        if (selected == 1) {
            nav = new Intent(InfoActivity.this, InfoMapsActivity.class);
        } else if (selected == 2) {
            show();
        } else if (selected == 3) {
            nav = new Intent(InfoActivity.this, InfoMapsActivity.class);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(nav, 1);

    }



	/*----------------*/
	/* CUSTOM METHODS */

    protected void show() {

        rlTable.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlTable, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlTable, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlTable, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }



    public void clickHide(View v){

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlTable, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                rlTable.setVisibility(View.GONE);
            }
        });
        alpha1.start();
    }

}
