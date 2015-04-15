package mx.app.ambassador.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import mx.app.ambassador.R;

/**
 * Created by kreativeco on 14/04/15.
 */
public class FeedbackActivity extends SectionActivity {
    ScrollView svExperience, svClinical;
    LinearLayout llExperienceQuestions, llClinicalQuestions;
    LayoutInflater inflater;
    View item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Feedback Total");

        svExperience = (ScrollView) findViewById(R.id.sv_experience);
        svClinical = (ScrollView) findViewById(R.id.sv_clinical);

        llExperienceQuestions = (LinearLayout) findViewById(R.id.ll_experience_questions);
        llClinicalQuestions = (LinearLayout) findViewById(R.id.ll_clinical_questions);

        inflater = getLayoutInflater();

        Resources res = getResources();
        String[] experienceQuestions = res.getStringArray(R.array.txt_array_experience_questions);
        String[] clinicalQuestions = res.getStringArray(R.array.txt_array_clinical_questions);

        for(int i = 0; i<6; i++){
            if(i<3){
                item = inflater.inflate(R.layout.ui_question_feeedback_total, null);
                ((TextView) item.findViewById(R.id.tv_question_face)).setText(experienceQuestions[i]);
                llExperienceQuestions.addView(item);
            }else{
                item = inflater.inflate(R.layout.ui_question_txt_field, null);
                ((TextView) item.findViewById(R.id.tv_question)).setText(experienceQuestions[i]);
                llExperienceQuestions.addView(item);
            }
        }

        for(int i = 0; i<5; i++){
            if(i == 0 || i == 2 || i == 4){
                item = inflater.inflate(R.layout.ui_question_txt_field, null);
                ((TextView) item.findViewById(R.id.tv_question)).setText(clinicalQuestions[i]);
                llClinicalQuestions.addView(item);
            }else{
                item = inflater.inflate(R.layout.ui_question_with_btns, null);
                ((TextView) item.findViewById(R.id.tv_question_b)).setText(clinicalQuestions[i]);
                llClinicalQuestions.addView(item);
            }
        }
    }

    public void showExperienceScroll(View v){
        hideScrolls();
        svExperience.setVisibility(View.VISIBLE);
    }

    public void showClinicalScroll(View v){
        hideScrolls();
        svClinical.setVisibility(View.VISIBLE);
    }

    public void hideScrolls(){
        svExperience.setVisibility(View.INVISIBLE);
        svClinical.setVisibility(View.INVISIBLE);
    }
}
