package mx.app.ambassador.activities.checklist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.androidquery.callback.AjaxStatus;
import com.kbeanie.imagechooser.api.ChooserType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.ChecklistActivity;
import mx.app.ambassador.activities.GameActivity;
import mx.app.ambassador.activities.RankingActivity;
import mx.app.ambassador.activities.SectionActivity;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by kreativeco on 09/04/15.
 */
public class ChecklistOptsActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    ScrollView slChecklist;
    LinearLayout llCheckList;
    CheckBox[] checkboxes;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_opts);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Check List");

        slChecklist = (ScrollView) findViewById(R.id.sv_checklist);
        llCheckList = (LinearLayout) findViewById(R.id.ll_checklist);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("option", 1);

        Resources res = getResources();
        String[] data = new String[0];

        boolean finished  = !User.get("checklist_type", this).isEmpty();
        String[] checked = new String[0];

        if (finished) {
            type    = Integer.parseInt(User.get("checklist_type", this));
            checked = User.get("checklist_answers", this).split(",");
            findViewById(R.id.bt_send).setVisibility(View.GONE);
        }

        if (type == 1) data      = res.getStringArray(R.array.array_checklist_sales);
        else if (type == 2) data = res.getStringArray(R.array.array_checklist_storage);
        else if (type == 3) data = res.getStringArray(R.array.array_checklist_service);
        else if (type == 4) data = res.getStringArray(R.array.array_checklist_distribution);

        checkboxes = new CheckBox[data.length];

        for (int i = 0; i < data.length; i++) {
            LayoutInflater inflater = getLayoutInflater();

            View item = inflater.inflate(R.layout.ui_checkbox, null);
            checkboxes[i] = (CheckBox)item.findViewById(R.id.cb_checkbox);

            checkboxes[i].setText(data[i]);
            checkboxes[i].setEnabled(!finished);

            llCheckList.addView(item);

            if (finished) {
                for (int j = 0; j < checked.length; j++) {
                    if (i+1 == Integer.parseInt(checked[j])) {
                        checkboxes[i].setChecked(true);
                    }
                }
            }

        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSave(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro de enviar el checklist? Posteriormente no podrá modificarlo.");
        builder.setCancelable(true);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        send();
                    }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
        });
        builder.create().show();
    }


    protected void send() {

        String[] types = new String[]{"autoservicio", "bodega", "preventa", "reparto"};

        Map<String, Object> params = User.getToken(this);
        params.put("category", types[type-1]);
        params.put("checklist", getOptions(true));
        WebBridge.send("webservices.php?task=addAnswerdChecklist", params, "Cargando", this, this);

    }


    protected String getOptions(boolean enabled) {
        ArrayList<String> answers = new ArrayList<String>();
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].isChecked()) {
                answers.add( Integer.toString(i + 1) );
            }
            checkboxes[i].setEnabled(enabled);
        }
        return TextUtils.join(",", answers);
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

            User.set("checklist_answers", getOptions(false), this);
            User.set("checklist_type", Integer.toString(type), this);
            findViewById(R.id.bt_send).setVisibility(View.GONE);
            for (int i = 0; i < checkboxes.length; i++) {
                checkboxes[i].setEnabled(false);
            }

            new AlertDialog.Builder(this).setTitle(R.string.txt_thanks).setMessage("Gracias por completar el checklist").setNeutralButton(R.string.bt_close, null).show();

        }
    }
}
