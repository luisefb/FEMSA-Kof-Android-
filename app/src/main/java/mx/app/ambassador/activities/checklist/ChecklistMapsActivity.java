package mx.app.ambassador.activities.checklist;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.SectionActivity;

/**
 * Created by kreativeco on 07/04/15.
 */
public class ChecklistMapsActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    RelativeLayout rlMap;
    ImageView imgMap;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_maps);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Croquis");
        
        rlMap = (RelativeLayout) findViewById(R.id.rl_map);
        imgMap = (ImageView) findViewById(R.id.img_map);

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {

        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        int r = getResources().getIdentifier("image_map_" + selected, "drawable", getPackageName());
        imgMap.setImageResource(r);
        show();

    }





	/*----------------*/
	/* CUSTOM METHODS */

    protected void show() {

        rlMap.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlMap, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlMap, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlMap, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }

    public void clickHide(View v){

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlMap, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                rlMap.setVisibility(View.GONE);
            }
        });
        alpha1.start();
    }

}
