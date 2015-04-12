package mx.app.ambassador.ui.views;

import android.animation.AnimatorSet;
import android.content.Context;
import android.widget.ImageView;

/**
 * Created by noisedan on 4/9/15.
 */
public class BottleSetView extends ImageView {

    String type  = "sm";
    int level    = 0;
    int maxLevel = 0;
    boolean isOver = false;

    public BottleSetView(Context context) {
        super(context);
    }

    public void setType(String t) {
        type = t;
    }

    public String getType() {
        return type;
    }

    public void setLevel(int t) {
        level = t;
        int r = getResources().getIdentifier("image_game_order_" + type + "_" + level, "drawable", getContext().getPackageName());
        setImageResource(r);
        //setOut();
    }

    public int getLevel() {
        return level;
    }

    public void setMaxLevel(int t) {
        maxLevel = t;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setOver() {
        //setAlpha(0.5f);
        if (isOver) return;
        isOver = true;
        int r = getResources().getIdentifier("image_game_order_" + type + "_over", "drawable", getContext().getPackageName());
        setImageResource(r);
    }

    public void setOut() {
        //setAlpha(1.0f);
        if (!isOver) return;
        isOver = false;
        int r = getResources().getIdentifier("image_game_order_" + type + "_" + level, "drawable", getContext().getPackageName());
        setImageResource(r);
    }

}
