package mx.app.ambassador.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.app.ambassador.R;
import mx.app.ambassador.ui.dialogs.ProgressDialog;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class ProfileActivity extends SectionActivity implements WebBridge.WebBridgeListener, ImageChooserListener {


	/*------------*/
	/* PROPERTIES */

    TextView txtName;
    EditText txtArea;
    EditText txtRank;
    EditText txtEmail;
    ImageButton btPhoto;
    ProgressDialog progress;
    WebView wvGif;
    boolean evaluation = false;


    private ImageChooserManager icManager;
    private String icFilePath;
    private int icType;
    private ChosenImage icImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Perfil");

        txtName     = (TextView)findViewById(R.id.txt_name);
        txtArea     = (EditText)findViewById(R.id.txt_area);
        txtRank     = (EditText)findViewById(R.id.txt_rank);
        txtEmail    = (EditText)findViewById(R.id.txt_email);
        btPhoto     = (ImageButton)findViewById(R.id.bt_photo);
        wvGif       = (WebView)findViewById(R.id.wv_gif);

        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getProfile", params, "Cargando", this, this);

        wvGif.getSettings().setUseWideViewPort(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            evaluation = bundle.getBoolean("evaluation");
        }

        //wvGif.loadData(html, "text/html", "UTF-8");
    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickPhoto(View v) {

        final Dialog alert = new Dialog(this);
        alert.setTitle(getResources().getString(R.string.txt_select_option));
        alert.setContentView(getLayoutInflater().inflate(R.layout.dialog_alert_photo, null));
        ((Button) alert.findViewById(R.id.bt_select_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alert.cancel();
                showImageChooser(ChooserType.REQUEST_CAPTURE_PICTURE);
            }
        });

        ((Button) alert.findViewById(R.id.bt_select_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alert.cancel();
                showImageChooser(ChooserType.REQUEST_PICK_PICTURE);
            }
        });

        if (icImage != null) {
            ((Button) alert.findViewById(R.id.bt_delete_photo)).setVisibility(View.VISIBLE);
            ((Button) alert.findViewById(R.id.bt_delete_photo)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    alert.cancel();
                    icImage = null;
                    btPhoto.setImageResource(R.drawable.bt_profile_avatar);
                }
            });
        }

        ((Button) alert.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alert.cancel();
            }
        });
        alert.show();
    }

    public void clickSave(View v) {

        /*
        if (icImage == null) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Selecciona primero una imagen").setNeutralButton(R.string.bt_close, null).show();
            return;
        }
        */

        if (txtArea.getText().length() < 1) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Indica tu área").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtRank.getText().length() < 1) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Indica tu posición").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (!isEmailValid(txtEmail.getText().toString())) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Escribe un correo válido").setNeutralButton(R.string.bt_close, null).show();
            return;
        }


        Map<String, Object> t  = User.getToken(this);
        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("token", t.get("token"));
        params.put("business", txtArea.getText());
        params.put("business_position", txtRank.getText());
        params.put("email", txtEmail.getText());


        if (icImage != null) {
            File image = new File(icImage.getFilePathOriginal());
            try {
                params.put("file_image", image);
            } catch (FileNotFoundException e) {}
        }



        progress = ProgressDialog.show(this, "Enviando", true, false, null);

        client.post( WebBridge.url("webservices.php?task=updateProfile") , params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("", "Success");
                Log.e("", response.toString());
                progress.dismiss();
                onWebBridgeResult("updateProfile.php", response, new AjaxStatus());
            }
        });

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



	/*---------------*/
	/* IMAGE CHOOSER */

    protected void restartImageChooser() {
        icManager = new ImageChooserManager(this, icType, "ambassador", true);
        icManager.setImageChooserListener(this);
        icManager.reinitialize(icFilePath);
    }

    protected void showImageChooser(int type) {

        try {

            icType = type;
            icManager = new ImageChooserManager(this, type, "ambassador", true);
            icManager.setImageChooserListener(this);
            icFilePath = icManager.choose();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        // TODO Auto-generated method stub

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    icImage = image;
                    String path = new File(icImage.getFileThumbnailSmall()).toString();
                    btPhoto.setImageDrawable(Drawable.createFromPath(path));
                    btPhoto.setImageResource(0);
                }
            }
        });
    }

    @Override
    public void onError(String reason) {
        Log.e("", "onError: " + reason);
    }



	/*--------------------*/
	/* ACTIVIY LIFE CICLE */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (icManager == null) {
                restartImageChooser();
            }
            icManager.submit(requestCode, data);
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("chooser_type", icType);
        outState.putString("media_path", icFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                icType = savedInstanceState.getInt("chooser_type");
            }

            if (savedInstanceState.containsKey("media_path")) {
                icFilePath = savedInstanceState.getString("media_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
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

            if (url.contains("getProfile")) {

                String avatar = "", name = "", business = "", rank = "", email = "";
                int percent   = 0;

                try {

                    JSONObject profile  = json.getJSONObject("profile");
                    JSONObject user     = profile.getJSONObject("users");
                    JSONObject badgets  = profile.getJSONObject("badgets");
                    JSONObject skills   = profile.getJSONObject("skills");

                    Log.e("", user.toString());
                    Log.e("", badgets.toString());
                    Log.e("", skills.toString());


                    percent  = profile.getInt("skills_total_porcent");
                    avatar   = user.getString("file_image");
                    name 	 = user.getString("name") + " " + user.getString("last_name");
                    business = user.getString("business");
                    rank     = user.getString("business_position");
                    email    = user.getString("email");

                    ((TextView)findViewById(R.id.tv_heart_be_bold)).setText( "x"     + skills.get("be_bold") );
                    ((TextView)findViewById(R.id.tv_heart_be_proud)).setText( "x"    + skills.get("be_proud") );
                    ((TextView)findViewById(R.id.tv_heart_be_in_love)).setText( "x"  + skills.get("be_in_love") );
                    ((TextView)findViewById(R.id.tv_heart_be_aware)).setText( "x"    + skills.get("be_aware") );
                    ((TextView)findViewById(R.id.tv_heart_be_together)).setText( "x" + skills.get("be_together") );

                    int r1 = getResources().getIdentifier("icon_circle_bold_"     + (badgets.getInt("be_bold") + 1),      "drawable", getPackageName());
                    int r2 = getResources().getIdentifier("icon_circle_proud_"    + (badgets.getInt("be_proud") + 1),     "drawable", getPackageName());
                    int r3 = getResources().getIdentifier("icon_circle_in_love_"  + (badgets.getInt("be_in_love") + 1),   "drawable", getPackageName());
                    int r4 = getResources().getIdentifier("icon_circle_aware_"    + (badgets.getInt("be_aware") + 1),     "drawable", getPackageName());
                    int r5 = getResources().getIdentifier("icon_circle_together_" + (badgets.getInt("be_together") + 1),  "drawable", getPackageName());

                    ((TextView)findViewById(R.id.badge_be_bold)).setCompoundDrawablesWithIntrinsicBounds(r1, 0, 0, 0);
                    ((TextView)findViewById(R.id.badge_be_proud)).setCompoundDrawablesWithIntrinsicBounds(r2, 0, 0, 0);
                    ((TextView)findViewById(R.id.badge_be_in_love)).setCompoundDrawablesWithIntrinsicBounds(r3, 0, 0, 0);
                    ((TextView)findViewById(R.id.badge_be_aware)).setCompoundDrawablesWithIntrinsicBounds(r4, 0, 0, 0);
                    ((TextView)findViewById(R.id.badge_be_together)).setCompoundDrawablesWithIntrinsicBounds(r5, 0, 0, 0);

                } catch (Exception e) {e.printStackTrace();}

                String gif = "image_profile_coke_0.gif";
                if (percent >= 100)     gif = "image_profile_coke_5.gif";
                else if (percent >= 80) gif = "image_profile_coke_4.gif";
                else if (percent >= 60) gif = "image_profile_coke_3.gif";
                else if (percent >= 40) gif = "image_profile_coke_2.gif";
                else if (percent >= 20) gif = "image_profile_coke_1.gif";

                wvGif.loadUrl("file:///android_asset/" + gif);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)btPhoto.getLayoutParams();
                params.width  = btPhoto.getWidth();
                params.height = btPhoto.getHeight();
                btPhoto.setLayoutParams(params);

                AQuery aq = new AQuery(btPhoto);
                aq.image(avatar, true, true, 0, R.id.bt_photo, null, AQuery.FADE_IN);

                txtName.setText(name);
                txtArea.setText(business);
                txtRank.setText(rank);
                txtEmail.setText(email);


            } else if (url.contains("updateProfile")) {


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.txt_thanks);
                builder.setMessage("Se actualizaron los datos del perfil");
                builder.setCancelable(true);

                if (evaluation) {
                    builder.setPositiveButton(R.string.bt_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ProfileActivity.this, EvaluationActivity.class);
                            intent.putExtra("type", "pre");
                            startActivityForResult(intent, 1);
                        }
                    });
                } else {
                    builder.setPositiveButton(R.string.bt_close, null);
                }

                builder.create().show();

                /*
                new AlertDialog.Builder(this).setTitle(R.string.txt_thanks).setMessage("Se actualizaron los datos del perfil").setNeutralButton(R.string.bt_close, null).show();
                if (evaluation) {

                }
                */

            }

        }
    }

}
