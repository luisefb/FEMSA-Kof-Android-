package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.ui.layouts.FlowLayout;
import mx.app.ambassador.ui.views.PieChartView;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/14/15.
 */
public class ResultsActivity extends SectionActivity implements WebBridge.WebBridgeListener {

    FlowLayout flResults;
    String type = "pre";
    String categoryNames[] = new String[]{"Portafolio", "Manejo del Producto", "Ejecución", "Indicadores de negocio", "Modelo de negocio", "Otros"};
    TextView txtScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Resultados");

        flResults = (FlowLayout)findViewById(R.id.fl_results);
        txtScore  = (TextView)findViewById(R.id.txt_score);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
        }

        Map<String, Object> params = User.getToken(this);
        params.put("type", type);
        WebBridge.send("webservices.php?task=getScoreEvaluation", params, "Cargando", this, this);

    }


	/*--------------*/
	/* CLICK EVENTS */

    public void clickBack(View v) {
        setResult(Activity.RESULT_OK);
        finish();
        overridePendingTransition(R.anim.static_motion, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        clickBack(null);
    }



	/*----------------*/
	/* CUSTOM METHODS */

    protected View getItem(String title, float value) {

        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.ui_results_item, null);

        ((TextView)item.findViewById(R.id.txt_result)).setText(title);
        PieChartView pie = (PieChartView)item.findViewById(R.id.pc_result);

        pie.setAnimDuration(1000);
        pie.setValueWidthPercent(25f);
        pie.setFormatDigits(0);
        pie.setDimAlpha(50);
        pie.setStepSize(0.5f);
        pie.setTouchEnabled(false);
        pie.showValue(value, 100f, true);
        pie.setColor(Color.parseColor("#ec103e"));

        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(flResults.getWidth()/3, FlowLayout.LayoutParams.WRAP_CONTENT);
        item.setLayoutParams(params);

        return item;
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
            msg = json.getString("message");
        } catch (Exception e) {
        }

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
            clickBack(null);
        } else {

            try {

                JSONObject evaluation = json.getJSONObject("evaluation").getJSONObject(type);
                for (int i=1; true && i<=categoryNames.length; i++) {
                    if (!evaluation.has("team_" + i)) break;
                    int value = evaluation.getInt("team_" + i);
                    flResults.addView(getItem(categoryNames[i-1], value * 1.0f));
                }

                if (evaluation.has("total")) {
                    txtScore.setText(evaluation.getString("total"));
                }

                findViewById(R.id.ll_results).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
