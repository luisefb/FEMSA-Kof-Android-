package mx.app.ambassador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.games.GameKofActivity;
import mx.app.ambassador.activities.games.GameOrderActivity;
import mx.app.ambassador.activities.games.GameProductsActivity;
import mx.app.ambassador.activities.games.GameRefriActivity;
import mx.app.ambassador.activities.games.GameTeamActivity;

/**
 * Created by noisedan on 4/6/15.
 */
public class GameActivity extends SectionActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Juegos");

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {
        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        if (selected == 1) {
            nav = new Intent(GameActivity.this, GameKofActivity.class);
        } else if (selected == 2) {
            nav = new Intent(GameActivity.this, GameTeamActivity.class);
        } else if (selected == 3) {
            nav = new Intent(GameActivity.this, GameProductsActivity.class);
        } else if (selected == 4) {
            nav = new Intent(GameActivity.this, GameOrderActivity.class);
        } else if (selected == 5) {
            nav = new Intent(GameActivity.this, GameRefriActivity.class);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(nav, 1);

    }


}
