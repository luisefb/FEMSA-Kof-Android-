package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


import mx.app.ambassador.R;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/9/15.
 */
public class EvaluationActivity extends SectionActivity implements WebBridge.WebBridgeListener {

    Button btNext;
    JSONObject data;
    TextView txtEvaluationTitle;

    String categoryOrder[] = new String[]{"portfolio", "managment", "indicators", "lunching", "model", "others"};
    String categoryNames[] = new String[]{"Portafolio", "Manejo de Producto", "Indicadores de negocio", "Ejecución", "Modelo de negocio", "Embajadores"};
    int currentQuestion;
    int currentCategory;

    JSONObject answers;

    LinearLayout llQuestion1;
    LinearLayout llQuestion2;

    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Evaluación");

        btNext      = (Button)findViewById(R.id.bt_next);
        llQuestion1 = (LinearLayout)findViewById(R.id.ll_question1);
        llQuestion2 = (LinearLayout)findViewById(R.id.ll_question2);
        txtEvaluationTitle = (TextView)findViewById(R.id.txt_evaluation_title);

        StringBuilder bufferer = new StringBuilder();
        BufferedReader reader  = null;
        String row             = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
        }

        try {
            InputStream stream = getAssets().open("evaluation.json");
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((row = reader.readLine()) != null) {
                bufferer.append(row);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = new JSONObject(bufferer.toString());
            data = json.getJSONObject("evaluation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentQuestion = 0;
        currentCategory = 0;
        answers = new JSONObject();

        answers();

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickNext (View v) {

        int selected1 = selected(llQuestion1);

        if (selected1 == -1) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Selecciona una opción en las respuestas").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (llQuestion2.isShown()) {
            int selected2 = selected(llQuestion2);
            if (selected2 == -1) {
                new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Selecciona una opción en las respuestas").setNeutralButton(R.string.bt_close, null).show();
                return;
            }
        }

        answers();
    }

    @Override
    public void onBackPressed() {
        clickBack(null);
    }



	/*----------------*/
	/* CUSTOM METHODS */

    private void answers() {

        if (currentCategory < categoryOrder.length) {

            JSONArray category = category(categoryOrder[currentCategory]);

            if (currentQuestion < category.length()) {

                txtEvaluationTitle.setText(categoryNames[currentCategory]);

                try {
                    fill(llQuestion1, category.getJSONObject(currentQuestion));
                } catch (JSONException e) {}

                currentQuestion++;

                if (currentQuestion < category.length()) {

                    llQuestion2.setVisibility(View.VISIBLE);
                    try {
                        fill(llQuestion2, category.getJSONObject(currentQuestion));
                    } catch (JSONException e) {}
                    currentQuestion++;

                } else {
                    currentQuestion = 0;
                    currentCategory++;
                    llQuestion2.setVisibility(View.GONE);
                }

            } else {
                currentQuestion = 0;
                currentCategory++;
                answers();
            }

        } else {
            end();
        }

    }

    private JSONArray category(String type) {
        JSONArray items = null;
        try {
            JSONObject category = data.getJSONObject(type);
            items = category.getJSONArray("item");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void fill (View v, JSONObject item) {

        String question = "", answer1 = "", answer2 = "", answer3 = "";

        try {
            question = item.getString("question");
            answer1  = item.getString("answer1");
            answer2  = item.getString("answer2");
            answer3  = item.getString("answer3");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RadioGroup group   = (RadioGroup)v.findViewById(R.id.rg_evaluation_answers);
        RadioButton radio1 = (RadioButton)v.findViewById(R.id.rb_evaluation_answer1);
        RadioButton radio2 = (RadioButton)v.findViewById(R.id.rb_evaluation_answer2);
        RadioButton radio3 = (RadioButton)v.findViewById(R.id.rb_evaluation_answer3);

        group.setTag(currentCategory + "_" + currentQuestion);
        group.clearCheck();

        ((TextView)v.findViewById(R.id.txt_evaluation_question)).setText(question);
        radio1.setText(answer1);
        radio2.setText(answer2);
        radio3.setText(answer3);

        radio1.setVisibility(answer1.isEmpty() ? View.GONE : View.VISIBLE);
        radio2.setVisibility(answer2.isEmpty() ? View.GONE : View.VISIBLE);
        radio3.setVisibility(answer3.isEmpty() ? View.GONE : View.VISIBLE);

    }

    private void end() {

        JSONObject evaluation = new JSONObject();
        try {
            evaluation.put("type", type);
            evaluation.put("evaluations", answers);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> params = User.getToken(this);
        params.put("json", evaluation.toString());
        params.put("type", type);
        WebBridge.send("webservices.php?task=addAnswerdEvaluation", params, "Cargando", this, this);

    }

    private int selected(View v) {

        int id = ((RadioGroup)v.findViewById(R.id.rg_evaluation_answers)).getCheckedRadioButtonId();
        if (id == -1) return -1;

        RadioButton rb = ((RadioButton)v.findViewById(id));
        String option  = rb.getTag().toString();
        String info[]  = ((RadioGroup)v.findViewById(R.id.rg_evaluation_answers)).getTag().toString().split("_");

        int topic    = Integer.parseInt(info[0]) + 1;
        int question = Integer.parseInt(info[1]) + 1;

        if (!answers.has("tema_" + topic)) {
            try { answers.put("tema_" + topic, new JSONObject()); }
            catch (JSONException e) {}
        }

        try {

            int o = Integer.parseInt(option) + 1;
            JSONObject q = category(categoryOrder[(topic - 1)]).getJSONObject(question-1);

            if (q == null) return -1;

            JSONObject answer = new JSONObject();

            answer.put("answerd", q.getString("answer" + o));
            answer.put("answerd_right", q.getInt("right") == o ? 1 : 0);

            answers.getJSONObject("tema_" + topic).put("question_" + question, answer);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return Integer.parseInt(option);
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
        } else {

            String amsg = type.equals("pre") ? "Gracias por contestar la evaluación" : "Estás listo para ser un Embajador en Acción.  Lleva el conocimiento a tu día a día, en la cultura KOF,  en tu participación activa en eventos de desarrollo y responsabilidad social y en el reporte de oportunidades en el mercado.";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("¡Felicidades!");
            builder.setMessage(amsg);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.bt_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(EvaluationActivity.this, ResultsActivity.class);
                    intent.putExtra("type", type);
                    startActivityForResult(intent, 1);

                }
            });

            builder.create().show();
        }
    }

}
