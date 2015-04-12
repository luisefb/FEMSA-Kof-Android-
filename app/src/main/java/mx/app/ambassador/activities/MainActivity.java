package mx.app.ambassador.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;

import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;
import mx.app.ambassador.R;


public class MainActivity extends SectionActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                this.cancel();
                Intent nav = new Intent(MainActivity.this, LoginActivity.class);
                nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(nav, 1);
            }
        };
        timer.schedule(timerTask, 4000);



    }



	/*-----------------*/
	/* OVERRIDE RESULT */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

}
