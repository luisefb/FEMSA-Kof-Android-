package mx.app.ambassador.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by noisedan on 4/9/15.
 */
public class BottleView extends ImageView {

    String type;
    AnimatorSet animator;

    public BottleView(Context context) {
        super(context);
    }

    public void setType(String t) {
        type = t;
    }

    public String getType() {
        return type;
    }

    public void setAnimator(AnimatorSet t) {
        animator = t;
        //t.addListener(this);
    }

    public AnimatorSet getAnimator() {
        return animator;
    }

    public void removeAnimator() {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
        }
    }
    /*
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        ViewGroup v = (ViewGroup)getParent();
        v.removeView(this);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
    */
}
