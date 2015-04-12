package mx.app.ambassador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.checklist.ChecklistInfoActivity;
import mx.app.ambassador.activities.checklist.ChecklistOptsActivity;
import mx.app.ambassador.utils.User;

/**
 * Created by kreativeco on 09/04/15.
 */
public class ChecklistActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Check List");

        if (User.get("checklist_type", this) != null) {

            String type = User.get("checklist_type", this);

            findViewById(R.id.bt_checklist_1).setEnabled(false);
            findViewById(R.id.bt_checklist_2).setEnabled(false);
            findViewById(R.id.bt_checklist_3).setEnabled(false);
            findViewById(R.id.bt_checklist_4).setEnabled(false);

            findViewById(R.id.bt_checklist_1).setAlpha(0.5f);
            findViewById(R.id.bt_checklist_2).setAlpha(0.5f);
            findViewById(R.id.bt_checklist_3).setAlpha(0.5f);
            findViewById(R.id.bt_checklist_4).setAlpha(0.5f);

            int id = getResources().getIdentifier("bt_checklist_" + type, "id", getPackageName());
            findViewById(R.id.bt_checklist_1).setEnabled(true);
            findViewById(R.id.bt_checklist_1).setAlpha(1.0f);

        }


    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSection(View v) {

        int selected = Integer.parseInt(v.getTag().toString());
        Intent nav   = new Intent(ChecklistActivity.this, ChecklistOptsActivity.class);

        if (selected == 1) {
            nav.putExtra("option", selected);
        } else if (selected == 2) {
            nav.putExtra("option", selected);
        } else if (selected == 3) {
            nav.putExtra("option", selected);
        } else if (selected == 4) {
            nav.putExtra("option", selected);
        }else if (selected == 5) {
            nav = new Intent(ChecklistActivity.this, ChecklistInfoActivity.class);
        }

        startActivity(nav);

    }
}
