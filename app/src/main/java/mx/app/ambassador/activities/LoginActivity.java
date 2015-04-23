package mx.app.ambassador.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.app.ambassador.R;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/2/15.
 */
public class LoginActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtUsername;
    EditText txtPassword;
    RelativeLayout rlWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);

        txtUsername = (EditText)findViewById(R.id.txt_username);
        txtPassword = (EditText)findViewById(R.id.txt_password);
        rlWelcome   = (RelativeLayout)findViewById(R.id.rl_welcome);

        if (User.getToken(this) != null) {
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            startActivityForResult(intent, 1);
        }

        //txtUsername.setText("daniel@kco.com");
        //txtPassword.setText("123qaz");

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickLogin(View v) {

        ArrayList<String> errors = new ArrayList<String>();
        if (txtUsername.getText().toString().length() < 1) errors.add(getString(R.string.error_username));
        //if (!isEmailValid(txtUsername.getText().toString())) errors.add(getString(R.string.error_username));
        if (txtPassword.getText().length() < 1) errors.add(getString(R.string.error_password));

        if (errors.size() != 0) {
            String msg = "";
            for (String s : errors) {
                msg += "- " + s + "\n";
            }
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg.trim()).setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", 	txtUsername.getText().toString());
        params.put("password", 	txtPassword.getText().toString());
        params.put("device", 	"android");

        WebBridge.send("webservices.php?task=login", params, getString(R.string.txt_sending), this, this);


    }

    public void clickContinue(View v) {
        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 1);
    }



	/*-----------------*/
	/* OVERRIDE RESULT */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != RESULT_OK) {
            finish();
        } else {
            rlWelcome.setVisibility(View.GONE);
        }
    }



	/*----------------*/
	/* CUSTOM METHODS */

    private boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) return true;
        else return false;
    }


	/*----------------*/
	/* CUSTOM METHODS */

    protected void show(View v) {

        v.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(v, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

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

            if (url.contains("login")) {

                String token   = "";
                try {
                    token 	   = json.getString("token");
                } catch (Exception e) {}

                User.setToken(txtUsername.getText().toString(), token, this);
                txtPassword.setText("");
                show(rlWelcome);
            }
        }
    }

}
