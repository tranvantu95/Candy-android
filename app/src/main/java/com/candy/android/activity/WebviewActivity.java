package com.candy.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.CustomWebView;
import com.candy.android.fragment.webpage.WebFragment;
import com.candy.android.utils.HimecasUtils;

public class WebviewActivity extends AppCompatActivity {
    public static final String ARG = "web";
    public static final int FROM_MAIN = 0;
    public static final int FROM_FRAGMENT_WEB = 1;
    private TextView tvTitle;
    private TextView tvCloseFg, tvTitleFg;
    private CustomWebView mWvContent;
    private ProgressBar mProgressBar;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Intent intent = getIntent();
        mode = intent.getIntExtra(ARG, FROM_FRAGMENT_WEB);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView btnBack = (ImageView) toolbar.findViewById(R.id.iv_back);
        tvTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        tvCloseFg = (TextView) toolbar.findViewById(R.id.tvClose_from_fg);
        tvTitleFg = (TextView) toolbar.findViewById(R.id.title_from_fg);
        if (mode == FROM_MAIN) {
            WebFragment webViewFragment = WebFragment.newInstance(Define.WebUrl.URL_TRAIL);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, webViewFragment).commitAllowingStateLoss();
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } else {
            btnBack.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            tvCloseFg.setVisibility(View.VISIBLE);
            tvTitleFg.setVisibility(View.VISIBLE);
            mProgressBar = (ProgressBar) findViewById(R.id.pb_webview_progress);
            mProgressBar.setVisibility(View.VISIBLE);
            String url = intent.getStringExtra(Define.WEBVIEW_URL);
            mWvContent = (CustomWebView) findViewById(R.id.webview_activity);
            mWvContent.setVisibility(View.VISIBLE);
            mWvContent.setWebChromeClient(new WebChromeClient());
            mWvContent.setWebViewClient(new MyWebViewClient());
            WebSettings webSettings = mWvContent.getSettings();
            if (webSettings != null) {
                webSettings.setJavaScriptEnabled(true);
            }
            tvCloseFg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            mWvContent.loadUrl(url);
        }

    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void onBackPressed() {
        if (mode == FROM_FRAGMENT_WEB) {
            if (mWvContent.canGoBack()) {
                mWvContent.goBack();
            } else {
                super.onBackPressed();
                HimecasUtils.checkAndUpdatePoint(mWvContent.getUrl());
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    // WebClient
    @Deprecated
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("candy://")) {
                String link = "";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (url.contains("freepoint")) {
                    link = url;
                } else {
                    link = url.replace("link", getString(R.string.himecas_iap_host));
                }
                intent.setData(Uri.parse(link));
                startActivity(intent);

                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
