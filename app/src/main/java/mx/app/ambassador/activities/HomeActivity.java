package mx.app.ambassador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mx.app.ambassador.R;

/**
 * Created by noisedan on 4/6/15.
 */
public class HomeActivity extends SectionActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Menu");

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {

        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = null;

        if (selected == 1) {
            nav = new Intent(HomeActivity.this, LogbookActivity.class);
        } else if (selected == 2) {
            nav = new Intent(HomeActivity.this, MapActivity.class);
        } else if (selected == 4) {
            nav = new Intent(HomeActivity.this, ProfileActivity.class);
        } else if (selected == 5) {
            nav = new Intent(HomeActivity.this, WallActivity.class);
        }

        if (nav == null) return;

        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(nav, 1);

    }


}
