package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by kreativeco on 14/04/15.
 */
public class FeedbackActivity extends SectionActivity implements WebBridge.WebBridgeListener {

    ScrollView svExperience, svClinical;
    LinearLayout llExperienceQuestions, llClinicalQuestions;
    LayoutInflater inflater;
    View item;
    Button btExperience, btClinic;

    View[] experiencie = new View[6];
    View[] clinic      = new View[5];

    JSONObject answers1  = new JSONObject();
    JSONObject answers2  = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Evaluación de la Clínica");


        svExperience          = (ScrollView) findViewById(R.id.sv_experience);
        svClinical            = (ScrollView) findViewById(R.id.sv_clinic);
        llExperienceQuestions = (LinearLayout) findViewById(R.id.ll_experience_questions);
        llClinicalQuestions   = (LinearLayout) findViewById(R.id.ll_clinical_questions);
        btExperience          = (Button) findViewById(R.id.bt_experience);
        btClinic              = (Button) findViewById(R.id.bt_clinic);
        inflater              = getLayoutInflater();


        Resources res = getResources();
        String[] experienceQuestions = res.getStringArray(R.array.txt_array_experience_questions);
        String[] clinicalQuestions = res.getStringArray(R.array.txt_array_clinical_questions);


        for(int i = 0; i<6; i++){
            if(i<3){
                item = inflater.inflate(R.layout.ui_feedback_item_face, null);
                ((TextView) item.findViewById(R.id.tv_question_face)).setText(experienceQuestions[i]);
                llExperienceQuestions.addView(item);
                experiencie[i] = item.findViewById(R.id.rg_face);
            } else{
                item = inflater.inflate(R.layout.ui_feedback_item_text, null);
                ((TextView) item.findViewById(R.id.tv_question)).setText(experienceQuestions[i]);
                llExperienceQuestions.addView(item);
                experiencie[i] = item.findViewById(R.id.txt_answer);
                //((EditText)item.findViewById(R.id.txt_answer)).setText("--" + i);
            }
        }

        for(int i = 0; i<5; i++){
            if(i == 0 || i == 2 || i == 4){
                item = inflater.inflate(R.layout.ui_feedback_item_text, null);
                ((TextView) item.findViewById(R.id.tv_question)).setText(clinicalQuestions[i]);
                llClinicalQuestions.addView(item);
                clinic[i] = item.findViewById(R.id.txt_answer);
                //((EditText)item.findViewById(R.id.txt_answer)).setText("--" + i);
            }else{
                item = inflater.inflate(R.layout.ui_feedback_item_yes_no, null);
                ((TextView) item.findViewById(R.id.tv_question_b)).setText(clinicalQuestions[i]);
                llClinicalQuestions.addView(item);
                clinic[i] = item.findViewById(R.id.rg_yesno);
            }
        }

        svExperience.scrollTo(0, 0);
        svClinical.scrollTo(0, 0);

    }

    public void clickNext(View v) {

        boolean flag = true;
        Resources res = getResources();
        String[] experienceQuestions = res.getStringArray(R.array.txt_array_experience_questions);

        for(int i = 0; i<experiencie.length; i++){
            if (experiencie[i] instanceof RadioGroup) {
                int id = ((RadioGroup)experiencie[i]).getCheckedRadioButtonId();
                if (id == -1) {
                    flag = false;
                    Log.e("", "RADIO " + i);
                } else {
                    try {
                        JSONObject a = new JSONObject();
                        a.put("answerd", experiencie[i].findViewById(id).getTag().toString());
                        answers1.put("question_" + (i+1), a);
                    } catch (JSONException e) {}

                }
            } else if (experiencie[i] instanceof EditText) {
                EditText txt = (EditText)experiencie[i];
                if (txt.getText().length() < 2) {
                    flag = false;
                    Log.e("", "TEXT " + i);
                } else {
                    try {
                        JSONObject a = new JSONObject();
                        a.put("answerd", txt.getText());
                        answers1.put("question_" + (i+1), a);
                    } catch (JSONException e) {}
                }
            }
        }

        if (!flag) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Por favor contesta todas las preguntas del feedback").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        clickClinic(null);

    }

    public void clickPrevious(View v) {
        clickExperience(null);
    }

    public void clickSend(View v) {

        boolean flag = true;
        for(int i = 0; i<clinic.length; i++){
            if (clinic[i] instanceof RadioGroup) {
                int id = ((RadioGroup)clinic[i]).getCheckedRadioButtonId();
                if (id == -1) {
                    flag = false;
                    Log.e("", "RADIO " + i);
                } else {
                    try {
                        JSONObject a = new JSONObject();
                        a.put("answerd", clinic[i].findViewById(id).getTag().toString());
                        answers2.put("question_" + (i+1), a);
                    } catch (JSONException e) {}
                }
            } else if (clinic[i] instanceof EditText) {
                EditText txt = (EditText)clinic[i];
                if (txt.getText().length() < 2) {
                    flag = false;
                    Log.e("", "TEXT " + i);
                } else {
                    try {
                        JSONObject a = new JSONObject();
                        a.put("answerd", txt.getText());
                        answers2.put("question_" + (i+1), a);
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
            feed.put("experiencia", answers1);
            feed.put("clinica", answers2);
            json.put("feedback", feed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.e("", json.toString());
        //Log.e("", "FINISHED");

        Map<String, Object> params = User.getToken(this);
        params.put("json", json.toString());
        WebBridge.send("webservices.php?task=addAnswerdFeedback", params, "Cargando", this, this);

    }



    public void clickExperience(View v){
        change(btExperience, btClinic);
        svClinical.setVisibility(View.INVISIBLE);
        svExperience.setVisibility(View.VISIBLE);
        svExperience.scrollTo(0, 0);
    }

    public void clickClinic(View v){
        change(btClinic, btExperience);
        svExperience.setVisibility(View.INVISIBLE);
        svClinical.setVisibility(View.VISIBLE);
        svClinical.scrollTo(0, 0);
    }


    public void change(Button btActive, Button btInactive) {
        btActive.setTextColor(Color.parseColor("#FFFFFF"));
        btInactive.setTextColor(Color.parseColor("#ef7d7d"));
        btActive.setBackgroundColor(Color.parseColor("#b6171b"));
        btInactive.setBackgroundColor(Color.parseColor("#E31820"));

    }




	/*-----------------*/
	/* OVERRIDE RESULT */

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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Gracias por contestar el feedback");
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.bt_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(FeedbackActivity.this, EvaluationActivity.class);
                    intent.putExtra("type", "post");
                    startActivityForResult(intent, 1);
                }
            });

            builder.create().show();
        }
    }
}
