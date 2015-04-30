package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by kreativeco on 22/04/15.
 */
public class OperatingUnitActivity extends SectionActivity implements WebBridge.WebBridgeListener {

    /*------------*/
	/* PROPERTIES */

    View[] operating_Unit = new View[5];
    ScrollView slOperatingUnit;
    LinearLayout llOperatingUnit;
    LayoutInflater inflater;
    View item;

    JSONObject answers1  = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operating_unit);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Unidad Operativa");

        slOperatingUnit = (ScrollView) findViewById(R.id.sv_operating_unit);
        llOperatingUnit = (LinearLayout) findViewById(R.id.ll_operating_unit);

        Resources res = getResources();
        String[] data = new String[0];

        data = res.getStringArray(R.array.txt_array_checklist_operating_unit);
        for(int i = 0; i<5; i++){
            inflater = getLayoutInflater();
            item = inflater.inflate(R.layout.ui_feedback_item_face, null);
            ((TextView) item.findViewById(R.id.tv_question_face)).setText(data[i]);
            llOperatingUnit.addView(item);
            operating_Unit[i] = item.findViewById(R.id.rg_face);
        }

    }


    /*--------------*/
	/* CLICK EVENTS */

    public void clickSend(View v) {

        boolean flag = true;
        Resources res = getResources();
        String[] experienceQuestions = res.getStringArray(R.array.txt_array_checklist_operating_unit);

        for(int i = 0; i<operating_Unit.length; i++){
            if (operating_Unit[i] instanceof RadioGroup) {
                int id = ((RadioGroup)operating_Unit[i]).getCheckedRadioButtonId();
                if (id == -1) {
                    flag = false;
                    Log.e("", "RADIO " + i);
                } else {
                    try {
                        JSONObject a = new JSONObject();
                        a.put("answerd", operating_Unit[i].findViewById(id).getTag().toString());
                        answers1.put("question_" + (i+1), a);
                    } catch (JSONException e) {}

                }
            }
        }

        if (!flag) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Por favor contesta todas las preguntas del feedback").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        JSONObject json = new JSONObject();
        JSONObject feed = new JSONObject();
        try {
            feed.put("operativa", answers1);
            json.put("feedback", feed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> params = User.getToken(this);
        params.put("json", json.toString());
        WebBridge.send("webservices.php?task=addAnswerdFeedback", params, "Cargando", this, this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }


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
        } else {


            User.set("operation", "true", this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Gracias por contestar la evaluación");
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.bt_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    setResult(Activity.RESULT_OK);
                    clickBack(null);
                }
            });

            builder.create().show();


        }
    }


}
