package com.candy.android.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.candy.android.R;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditPurchaseActivity extends AppCompatActivity {
    public static final String TAG = "CreditPurchase";
    public static final String INTENT_URL = "CreditPurchaseUrl";

    public static final String CREDIT_CGISERVER = "credit-cgiserver.com";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_purchase);
        webView = (WebView) findViewById(R.id.wvWebContent);
        initializeWebView(webView);

        String url = getIntent().getStringExtra(INTENT_URL);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    public void initializeWebView(WebView webView) {
        // Set client
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new CandyClient());

        WebSettings webSettings = webView.getSettings();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private class CandyClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        /**
         * knv added
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "URL: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public void closeWeb(View view) {
        onBackPressed();
    }

    public static void updateMemberInfoInSilent(final Context context) {
        if (SettingManager.getInstance(context).hasMember()) {
            String id = SettingManager.getInstance(context).getMemberInformation().getId();
            String password = SettingManager.getInstance(context).getMemberInformation().getPass();

            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
            //create call object
            Call<ApiMemberResponse> call = apiService.getMemberInfo(id, password);
            // send call object
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {

                    if (response == null || response.body() == null || response.body().getMember() == null)
                        return;
                    MemberInformation memberInfo = response.body().getMember();
                    Log.i("3 day", "" + memberInfo.ismIs3Day());
                    HimecasUtils.postEventBus(context, memberInfo);
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String url = webView.getUrl();
        if (!TextUtils.isEmpty(url) && url.contains(CREDIT_CGISERVER)) {
            updateMemberInfoInSilent(this);
        }
    }
}
