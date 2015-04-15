package mx.app.ambassador.activities.info;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import mx.app.ambassador.R;
import mx.app.ambassador.activities.SectionActivity;
import mx.app.ambassador.ui.dialogs.ProgressDialog;
import mx.app.ambassador.utils.WebBridge;

/**
 * Created by kreativeco on 07/04/15.
 */
public class InfoMapsActivity extends SectionActivity implements View.OnClickListener, LocationListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    ProgressDialog progress;
    RelativeLayout rlMap;
    LinearLayout llContent;

    JSONArray data;

    GoogleMap mapView;
    LocationManager locationManager = null;
    LatLng user;
    Polyline route;
    Marker agency;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_maps);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Croquis");
        
        rlMap     = (RelativeLayout) findViewById(R.id.rl_map);
        llContent = (LinearLayout) findViewById(R.id.ll_content);


        StringBuilder bufferer = new StringBuilder();
        BufferedReader reader  = null;
        String row             = "";

        try {
            InputStream stream = getAssets().open("maps.json");
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((row = reader.readLine()) != null) {
                bufferer.append(row);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = new JSONArray();
        try {
            data = new JSONArray(bufferer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int s20 = dpToPx(20);
        int s18 = dpToPx(18);
        int s10 = dpToPx(10);

        for (int i=0; i<data.length(); i++) {

            String name = "";
            try {
                name = data.getJSONObject(i).getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            params.bottomMargin = s20;

            Button bt = new Button(this);
            bt.setText(name);
            bt.setTextSize(s18);
            bt.setTextColor(Color.parseColor("#E31820"));
            bt.setTag(i);
            bt.setPadding(s10, 0, s10, 0);
            bt.setBackgroundResource(R.drawable.bt_gray);
            bt.setLayoutParams(params);
            bt.setGravity(Gravity.LEFT);
            bt.setOnClickListener(this);

            llContent.addView(bt);

        }


        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {

            progress 		= ProgressDialog.show(this, getResources().getString(R.string.txt_getting_location), true, false, null);
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

            progress.show();
            mapView = ((MapFragment)getFragmentManager().findFragmentById(R.id.map_view)).getMap();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            agency = mapView.addMarker(new MarkerOptions().position( new LatLng(0.0f,0.0f)));
            //.position(new LatLng(lat, lng)).title(bt.getText().toString());

        } else if (code == ConnectionResult.SERVICE_MISSING || code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || code == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(code, this, 1);
            dialog.show();
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickHide(View v){

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlMap, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                rlMap.setVisibility(View.GONE);
            }
        });
        alpha1.start();
    }

    @Override
    public void onClick(View v) {

        Button bt = (Button)v;
        int index = Integer.parseInt(v.getTag().toString());

        long lat = 0;
        long lng = 0;

        try {
            lat = data.getJSONObject(index).getLong("lat");
            lng = data.getJSONObject(index).getLong("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        agency.setPosition(new LatLng(lat, lng));
        agency.setTitle(bt.getText().toString());
        agency.showInfoWindow();

        if (user != null) {
            String url = "http://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + user.latitude + "," + user.longitude
                    + "&destination=" + lat + "," + lng
                    + "&sensor=false&units=metric&mode=driving";
            WebBridge.send(url, "", this, this);
        } else {

            show();

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            CameraUpdate zoom	= CameraUpdateFactory.zoomTo(15);

            mapView.moveCamera(center);
            mapView.animateCamera(zoom);

        }

    }




	/*----------------*/
	/* CUSTOM METHODS */

    protected void show() {

        rlMap.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlMap, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlMap, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(rlMap, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }




	/*--------------*/
	/* GPS LISTENER */

    public void onLocationChanged(Location location) {

        // TODO Auto-generated method stub
        if (progress != null) progress.dismiss();

        locationManager.removeUpdates(this);
        user = new LatLng(location.getLatitude(), location.getLongitude());

        LatLng MELBOURNE = new LatLng(-37.813, 144.962);

        MarkerOptions o = new MarkerOptions().position(user).title("Tu ubicación").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mapView.addMarker(o);

    }


    public void onProviderDisabled(String provider) {

        if (progress != null) progress.dismiss();

        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String gpsAlert = getResources().getString(R.string.txt_gps_alert);
        String gpsActivate = getResources().getString(R.string.txt_gps_activate);

        builder.setMessage(gpsAlert).setCancelable(false).setPositiveButton(gpsActivate,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
                });

        builder.setNegativeButton(getResources().getString(R.string.bt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void onProviderEnabled(String provider) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (progress != null) progress.dismiss();
        // TODO Auto-generated method stub
    }


    @Override
    public void onWebBridgeResult(String url, JSONObject json, AjaxStatus ajaxStatus) {

        if (mapView == null) return;

        JSONArray steps;
        try {
            steps = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            steps = new JSONArray();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        PolylineOptions path = new PolylineOptions().width(3).color(Color.RED);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < steps.length(); i++) {
            JSONObject end;
            try {
                end = steps.getJSONObject(i).getJSONObject("end_location");
                LatLng p = new LatLng(end.getDouble("lat"), end.getDouble("lng"));
                path.add(p);
                builder.include(p);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        if (route != null) {
            route.remove();
        }

        route = mapView.addPolyline(path);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 40);
        mapView.animateCamera(cameraUpdate);

        show();

    }
}
