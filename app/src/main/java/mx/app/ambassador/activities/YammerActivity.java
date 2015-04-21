package mx.app.ambassador.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yammer.yammer.KCOWebServices;
import com.yammer.yammer.KCOYammerAPI;
import com.yammer.yammer.KCOYammerPreference;

import mx.app.ambassador.R;
import mx.app.ambassador.ui.dialogs.ProgressDialog;


public class YammerActivity extends SectionActivity {

    private final int YAMMER_OAUTH =0;
    public String scheme= "smartconsole";
    WebView mWebView;
    ProgressDialog mProgress;
    RelativeLayout rlInstructions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yammer);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Yammer");

        mWebView = (WebView)findViewById(R.id.webView);
        rlInstructions = (RelativeLayout)findViewById(R.id.rl_instructions);



        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setDomStorageEnabled(true);


        //mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);


        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.showSoftInput(mWebView, InputMethodManager.SHOW_IMPLICIT);


        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Manejador de errores
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.e("url", url);
                Uri uri = Uri.parse(url);

                if (uri.getScheme().equalsIgnoreCase(scheme)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", uri.getQueryParameter("code"));
                    getLoaderManager().initLoader(YAMMER_OAUTH, bundle, callbacks);
                }

                return true;
            }

            // when finish loading page
            public void onPageFinished(WebView view, String url) {
                if(mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }
        });


    }

    public void clickHide(View v) {
        rlInstructions.setVisibility(View.GONE);
        mProgress = ProgressDialog.show(this, "Espere un momento...", true, false, null);
        mWebView.loadUrl(String.format(KCOYammerAPI.LOGIN_URL, KCOYammerAPI.CLIENT_ID));

        onUserInteraction();
    }



    public void onUserInteraction(){
        super.onUserInteraction();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
    }

    private LoaderManager.LoaderCallbacks<JSONObject> callbacks = new LoaderManager.LoaderCallbacks<JSONObject>() {
        @Override
        public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
            return new ResultAsyncTaskLoader(YammerActivity.this, args.getString("code"));
        }

        @Override
        public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {



            if(data.has("access_token"))
            {
                try {
                    if(data.getJSONObject("access_token").has("token"))
                    {
                        KCOYammerPreference.CachingToken(YammerActivity.this, data.getJSONObject("access_token").getString("token"));
                        YammerActivity.this.setResult(RESULT_OK);
                        YammerActivity.this.finish();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<JSONObject> loader) {


        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Esto es lo que hace mi botón al pulsar ir a atrás
            Toast.makeText(getApplicationContext(), "Borrado!!", Toast.LENGTH_SHORT).show();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.clearSslPreferences();
            mWebView.clearDisappearingChildren();
            mWebView.clearFocus();
            mWebView.clearFormData();
            mWebView.clearMatches();
            mWebView.loadUrl(String.format(KCOYammerAPI.LOG_OUT));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private static class ResultAsyncTaskLoader extends AsyncTaskLoader<JSONObject> {

        private JSONObject result;
        private String mCode;
        public ResultAsyncTaskLoader(Context context, String code) {
            super(context);
            mCode = code;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (result != null) {
                deliverResult(result);
            } else {
                forceLoad();
            }
        }

        @Override
        public JSONObject loadInBackground() {
            /**
             * envia solicitud al servidor
             */

            result =
                    KCOWebServices.SendHttpPost(
                            String.format(KCOYammerAPI.LOGIN_OAUTH, KCOYammerAPI.CLIENT_ID, KCOYammerAPI.CLIENT_SECRET, mCode), new JSONObject(), "");
            return result;
        }
    }


}

