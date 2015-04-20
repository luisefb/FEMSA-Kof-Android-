package mx.app.ambassador.activities;


import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yammer.yammer.KCOWebServices;
import com.yammer.yammer.KCOYammerAPI;
import com.yammer.yammer.KCOYammerPreference;

import mx.app.ambassador.R;


public class YammerActivity extends SectionActivity {

    private final int YAMMER_OAUTH =0;

    public String scheme= "smartconsole";
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yammer);
        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Yammer");

        mWebView = (WebView)findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
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
        });

        mWebView.loadUrl(String.format(KCOYammerAPI.LOGIN_URL,KCOYammerAPI.CLIENT_ID));

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

