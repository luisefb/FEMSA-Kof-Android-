package mx.app.ambassador.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TableLayout;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.games.GameKofActivity;
import mx.app.ambassador.activities.games.GameOrderActivity;
import mx.app.ambassador.activities.games.GameProductsActivity;
import mx.app.ambassador.activities.games.GameRefriActivity;
import mx.app.ambassador.activities.games.GameTeamActivity;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class GameActivity extends SectionActivity implements WebBridge.WebBridgeListener {

    TableLayout tlGame;
    int[] records = new int[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Juegos");

        tlGame = (TableLayout)findViewById(R.id.tl_game);
        tlGame.setVisibility(View.GONE);

        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getLevelGames", params, "Cargando", this, this);

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

        int index = Integer.parseInt(v.getTag().toString()) - 1;

        nav.putExtra("record", records[index]);
        nav.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(nav, 1);

    }


    /*----------------*/
	/* CUSTOM METHODS */

    protected void fade(View v) {

        v.setVisibility(View.VISIBLE);

        AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);
        a.setDuration(300);
        a.setFillAfter(true);
        v.startAnimation(a);
    }



	/*-----------------*/
	/* OVERRIDE RESULT */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Map<String, Object> params = User.getToken(this);
            WebBridge.send("webservices.php?task=getLevelGames", params, "Cargando", this, this);
        }
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
            msg	   = json.getString("message");
        } catch (Exception e) {}

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
        } else {

            int level = 0;

            fade(tlGame);

            try {

                JSONObject data = json.getJSONObject("records");
                level      = json.getInt("game_level");
                records[0] = data.getInt("codigo_kof");
                records[1] = data.getInt("equipar_al_equipo");
                records[2] = data.getInt("caza_productos");
                records[3] = data.getInt("todo_en_orden");
                records[4] = data.getInt("reto_refri");

            } catch (Exception e) {}

            View[] buttons = new View[]{findViewById(R.id.bt_game_kof), findViewById(R.id.bt_game_team), findViewById(R.id.bt_game_products), findViewById(R.id.bt_game_order), findViewById(R.id.bt_game_refrigerator)};
            for (int i=0; i<buttons.length; i++) {
                buttons[i].setAlpha(i <= level ? 1.0f : 0.5f);
                buttons[i].setEnabled( i <= level ? true : false );
            }

            //clickSection(findViewById(R.id.bt_game_kof));

        }


    }
}
