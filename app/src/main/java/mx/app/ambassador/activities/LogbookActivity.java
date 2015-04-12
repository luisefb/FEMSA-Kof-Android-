package mx.app.ambassador.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import mx.app.ambassador.R;
import mx.app.ambassador.ui.dialogs.ProgressDialog;
import mx.app.ambassador.ui.paint.PaintView;
import mx.app.ambassador.utils.User;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by noisedan on 4/6/15.
 */
public class LogbookActivity extends SectionActivity implements WebBridge.WebBridgeListener, ImageChooserListener, View.OnClickListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtNotes;
    ImageView imgCanvas;
    PaintView pView;
    RelativeLayout rlLogbook;
    RelativeLayout rlCanvas;
    LinearLayout llDays;
    ProgressDialog progress;


    Button[] tabs;
    int maxDay = -1, currentDay = -1, color = 0;
    JSONArray logbooks;
    Bitmap bmImage;


    private ImageChooserManager icManager;
    private String icFilePath;
    private int icType;
    private ChosenImage icImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Bitácora");


        imgCanvas = (ImageView)findViewById(R.id.img_canvas);
        rlCanvas  = (RelativeLayout)findViewById(R.id.rl_canvas);
        rlLogbook = (RelativeLayout)findViewById(R.id.rl_logbook);
        llDays    = (LinearLayout)findViewById(R.id.ll_days);
        txtNotes  = (EditText)findViewById(R.id.txt_notes);


        pView     = new PaintView(this);
        rlCanvas.addView(pView);
        pView.setEnabled(false);


        rlLogbook.setVisibility(View.INVISIBLE);

        load();

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickPencil(View v) {

        String[] options = new String[]{"Rojo", "Azul"};

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getResources().getString(R.string.txt_select_option));

        b.setSingleChoiceItems(options, color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();

                int[] colors = new int[]{Color.parseColor("#FF0000"), Color.parseColor("#2200FF")};
                color = which;
                pView.setColor(colors[color]);;
            }
        });
        b.setNegativeButton(R.string.bt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        b.show();

    }

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
                    pView.setEnabled(false);
                    pView.clear();
                    if (bmImage == null) {
                        imgCanvas.setImageResource(R.drawable.image_logbook_camera);
                    } else {
                        imgCanvas.setImageBitmap(bmImage);
                    }
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

        String notes = txtNotes.getText().toString();
        if (notes.length() < 5) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Por favor escribe una nota en el campo de texto").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        Map<String, Object> t  = User.getToken(this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", t.get("token"));
        params.put("notes", notes);
        params.put("date_number", currentDay + 1);

        if (icImage != null) {

            rlCanvas.setDrawingCacheEnabled(true);
            Bitmap bitmap = rlCanvas.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] image = stream.toByteArray();

            params.put("file_image", new ByteArrayInputStream(image), "imagen.png");

            Log.e("IMAGEN", "ENVIANDO");
        }

        progress = ProgressDialog.show(this, "Enviando", true, false, null);

        client.post(WebBridge.url("webservices.php?task=addLogBook") , params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("FAILURE", responseString);

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progress.dismiss();
                Log.e("RESPONSE", responseString);
                JSONObject json = null;
                try {
                    json = new JSONObject(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onWebBridgeResult("addLogBook.php", json, new AjaxStatus());
            }
        });

        /*
        client.post( WebBridge.url("webservices.php?task=addLogBook") , params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("", "Success");
                progress.dismiss();
                onWebBridgeResult("addLogBook.php", response, new AjaxStatus());
            }
        });
        */

    }

    @Override
    public void onClick(View v) {

        int new_day = Integer.parseInt(v.getTag().toString());
        if (new_day == currentDay) return;

        currentDay = new_day;
        show(currentDay);

    }

    private void show(int d) {

        d = d + 1;
        txtNotes.setText("");
        bmImage = null;
        imgCanvas.setImageResource(R.drawable.image_logbook_camera);
        icImage = null;

        txtNotes.setText("");
        ((ImageView )findViewById(R.id.img_canvas)).setImageBitmap(null);
        imgCanvas.setImageResource(R.drawable.image_logbook_camera);
        icImage = null;
        pView.setEnabled(false);
        pView.clear();

        for (int i=0; i<tabs.length; i++) {
            int r = getResources().getIdentifier("bt_tab_" + (i==currentDay?"on":"off"), "drawable", getPackageName());
            tabs[i].setBackgroundResource(r);
        }

        for (int i=0; i<logbooks.length(); i++) {

            int logbook_day = 0;
            String note = "", image = "";
            JSONObject logbook = null;

            try {
                logbook     = logbooks.getJSONObject(i);
                logbook_day = logbook.getInt("date_number");
                note        = logbook.getString("notes");
                image       = logbook.getString("file_image");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (logbook_day == d) {

                txtNotes.setText(note);

                if (!image.equals("")) {

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(image, new FileAsyncHttpResponseHandler(this) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            imgCanvas.setImageURI(Uri.fromFile(file));

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bmImage = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                            pView.setEnabled(true);
                        }
                    });

                    break;
                }
            }
        }
    }

    private void load() {
        Map<String, Object> params = User.getToken(this);
        WebBridge.send("webservices.php?task=getLogBook", params, "Cargando", this, this);
    }

    private void create() {

        if (tabs != null) return;

        tabs = new Button[maxDay];

        for (int i=0; i<maxDay; i++) {

            int r = getResources().getIdentifier("bt_tab_off", "drawable", getPackageName());

            Button bt = new Button(this);
            bt.setText("Día " + (i+1));
            bt.setTextColor(Color.parseColor("#FFFFFF"));
            bt.setBackgroundResource(r);
            bt.setTag(i);
            bt.setOnClickListener(this);

            tabs[i] = bt;

            llDays.addView(bt);

        }
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
                    imgCanvas.setImageDrawable(Drawable.createFromPath(path));
                    imgCanvas.setImageResource(0);
                    pView.clear();
                    pView.setEnabled(true);
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
            msg = json.getString("message");
        } catch (Exception e) {
        }

        if (status == 0) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
        } else {

            if (url.contains("getLogBook")) {

                rlLogbook.setVisibility(View.VISIBLE);
                logbooks = new JSONArray();

                try {

                    logbooks  = json.getJSONArray("logbook");
                    if (currentDay == -1) {
                        currentDay = json.getInt("logbook_day") - 1;
                    }
                    maxDay = json.getInt("logbook_day");

                } catch (Exception e) {}

                maxDay = Math.min(maxDay, 5);
                currentDay = Math.min(currentDay, 4);
                create();
                show(currentDay);

            } else if (url.contains("addLogBook")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("La bitácora ha sido actualizada");
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.bt_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        load();
                    }
                });

                builder.create().show();
            }
        }
    }
}
