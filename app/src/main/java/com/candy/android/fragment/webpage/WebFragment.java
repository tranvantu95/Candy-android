package com.candy.android.fragment.webpage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.activity.BaseActivity;
import com.candy.android.activity.CreditPurchaseActivity;
import com.candy.android.configs.Define;
import com.candy.android.custom.ClickHandler;
import com.candy.android.custom.gallery.Utils;
import com.candy.android.custom.views.MainViewPager;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiWebTokenResponse;
import com.candy.android.manager.InAppBillingManager;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.inappbilling.PurchaseRetryEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.RkSharedPreferencesUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base for all WebPage
 */
public class WebFragment extends BaseFragment {
    public static final String CREDIT_CGISERVER = "credit-cgiserver.com";
    private static final String TAG = "IDK-WebFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final int INPUT_FILE_REQUEST_CODE = 1001;
    private static String WEB_VIEW_BASE_URL;
    private ClickHandler mClickHandler;

    @BindView(R.id.wv_content)
    WebView mWvContent;
    @BindView(R.id.wv_content2)
    WebView mWvContentGuide;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout;
    @BindView(R.id.tv_restore)
    TextView tv_restore;
    private String mUrl;
    private boolean needWebToken;
    private String mTitle;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.frame)
    FrameLayout frameLayout;
    DialogBuilder.NoticeTitleDialog mNoticeCheckCodeDialog;
    private ProgressBar mProgressBar;
    private ValueCallback<Uri[]> mFilePathCallback;

    public WebFragment() {
        // (knv added)
        if (null != SettingManager.getInstance(getActivity()).getConfig()) {
            WEB_VIEW_BASE_URL = SettingManager.getInstance(getActivity()).getConfig().getWebviewBaseUrl();
        }
    }

    /**
     * Create new Instance of
     *
     * @param url WebPage url
     * @return A new instance of fragment WebFragment.
     */

    public static WebFragment newInstance(String url) {
        return newInstance(url, false);
    }

    public static WebFragment newInstance(String url, boolean needWebToken) {
        WebFragment fragment = new WebFragment();
        fragment.needWebToken = needWebToken;
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String endpoint = getArguments().getString(ARG_PARAM1);
            mUrl = WEB_VIEW_BASE_URL + endpoint;
            if (endpoint != null) {
                mTitle = calculateTitleBy(endpoint);
            }
        }
        if (mMainActivity != null) {
            mMainActivity.showAppBarLayout();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_web, container, false);
    }

    @Subscribe
    public void onPuchaseRetryEvent(PurchaseRetryEvent event) {
        Helpers.dismissCircleProgressDialog();
        if (event.result == 1 || !mTitle.equals(getString(R.string.menu_point))) {
            linearLayout.setVisibility(View.GONE);
            RkSharedPreferencesUtils mRspu;
            mRspu = RkSharedPreferencesUtils.getInstance();
            final int memberCode = SettingManager.getInstance().getMemberInformation().getCode();
            if (InAppBillingManager.mRetry && InAppBillingManager.mListPurchaseError.size() == 0) {
                showPurchaseRetrySuccess();// dialog message purchase success
                InAppBillingManager.mRetry = false;
            }

        } else if (event.result == 0 && mTitle.equals(getString(R.string.menu_point))) {
            linearLayout.setVisibility(View.VISIBLE);
            if (InAppBillingManager.mRetry && InAppBillingManager.mListPurchaseError.size() > 0) {
                showPurchaseRetryFailed();// dialog message purchase failed
                InAppBillingManager.mRetry = false;
            }
        }
    }

    private void showPurchaseRetrySuccess() {
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(getActivity().getString(R.string.purchase_point_retry_success), null);
        Helpers.showDialogFragment(getActivity().getSupportFragmentManager(), noticeDiaRkLogger);
    }

    private void showPurchaseRetryFailed() {
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(getActivity().getString(R.string.purchase_point_retry_failed), null);
        Helpers.showDialogFragment(getActivity().getSupportFragmentManager(), noticeDiaRkLogger);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_webview_progress);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fm_menu_container);
        if (null != f) {
            if (f instanceof WebFragment) {
                MainViewPager.setTouch(false);
                Define.webFragment = (WebFragment) f;
            } else {
                MainViewPager.setTouch(true);
                Define.webFragment = null;
            }
        }

        ButterKnife.bind(this, view);
        RkSharedPreferencesUtils mRspu;
        mClickHandler = new ClickHandler(2000);
        mRspu = RkSharedPreferencesUtils.getInstance();
        int memberCode = SettingManager.getInstance().getMemberInformation().getCode();
        if (mRspu.getString(InAppBillingManager.KEY_STORED_PURCHASE + "_" + memberCode, null) == null || !mTitle.equals(getString(R.string.menu_point))) {
            linearLayout.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
        if (mTitle.equals(getString(R.string.menu_guide_usage))) {
            mWvContentGuide.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            initializeWebView(mWvContentGuide);
        } else {
            mWvContentGuide.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            initializeWebView(mWvContent);
        }
        EventBus.getDefault().register(this);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClickHandler.isClickable(v.getId())) {
                    return;
                }
                Helpers.showCircleProgressDialog(mContext);
                EventBus.getDefault().post(new PurchaseRetryEvent(2));
            }
        });

        // Load url
        if (needWebToken) {
            // knv added

//            ApiWebTokenResponse tokenResponse = SettingManager.getInstance(getActivity()).getWebToken();
//            boolean hasToken = tokenResponse != null && !TextUtils.isEmpty(tokenResponse.getToken()) &&
//                    !TextUtils.isEmpty(tokenResponse.getSecretKey());

//            if (hasToken) {
//                loadUrlWithToken(tokenResponse);
//            } else {
//                getWebToken();
//            }
            getWebToken();

        } else {
            mWvContent.loadUrl(mUrl);
            mWvContentGuide.loadUrl(mUrl);
        }
    }

    private void getWebToken() {

        ApiInterface apiService = ApiClientManager.createNewInstance(WEB_VIEW_BASE_URL).create(ApiInterface.class);

        Call<ApiWebTokenResponse> call = apiService.getWebToken(
                SettingManager.getInstance(getActivity()).getID(),
                SettingManager.getInstance(getActivity()).getPassword());

        RkLogger.d(TAG, "Calling getWebToken");
        call.enqueue(new Callback<ApiWebTokenResponse>() {
            @Override
            public void onResponse(Call<ApiWebTokenResponse> call, Response<ApiWebTokenResponse> response) {
                if (response == null || response.body() == null) {
                    return;
                }
                ApiWebTokenResponse tokenResponse = response.body();
                RkLogger.d(TAG, "getWebToken return: " + new Gson().toJson(tokenResponse));

                // knv added, update web token
//                SettingManager.getInstance(getActivity()).saveWebToken(tokenResponse);

                String error = tokenResponse.getErrorMessage();
                if (TextUtils.isEmpty(error)) {
                    loadUrlWithToken(tokenResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiWebTokenResponse> call, Throwable t) {
                // Show notice error
                RkLogger.e(TAG, "Exception: ", t);
            }
        });
    }

    private void loadUrlWithToken(ApiWebTokenResponse tokenResponse) {
        try {
            String token = tokenResponse.getToken();
            String secret = tokenResponse.getSecretKey();

            Uri builder = Uri.parse(mUrl)
                    .buildUpon()
                    .appendQueryParameter(Define.Fields.FIELD_TOKEN, token)
                    .appendQueryParameter(Define.Fields.FIELD_SECRET, secret)
                    .appendQueryParameter(Define.Fields.FIELD_VERSION, BuildConfig.VERSION_NAME)
                    .build();
            RkLogger.d(TAG, "Uri builder: " + builder.toString());
            RkLogger.d(TAG, "loading url...");
            mWvContent.loadUrl(builder.toString());
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    public void initializeWebView(WebView webView) {
        // Set client
        webView.setWebChromeClient(new CandyWebChromeClient());
        webView.setWebViewClient(new CandyClient());

        WebSettings webSettings = webView.getSettings();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);
        }
        if (mTitle.equals(getString(R.string.menu_point))) {
            String colorString = getResources().getString(R.string.purchase_point_textview1);
            tv_restore.setText(Html.fromHtml(colorString));

//            linearLayout.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    private String calculateTitleBy(@NonNull String endpoint) {
        if (endpoint.contentEquals(Define.WebUrl.URL_PURCHASE)) {
            return getString(R.string.menu_point);
        } else if (endpoint.contentEquals(Define.WebUrl.USAGE_GUIDE)) {
            return getString(R.string.menu_guide_usage);
        } else if (endpoint.contentEquals(Define.WebUrl.FEE)) {
            return getString(R.string.menu_guide_fee);
        } else if (endpoint.contentEquals(Define.WebUrl.TERM)) {
            return getString(R.string.menu_term);
        } else if (endpoint.contentEquals(Define.WebUrl.POLICY)) {
            return getString(R.string.menu_private_policy);
        } else if (endpoint.contentEquals(Define.WebUrl.URL_INQUIRY_HISTORY)) {
            return getString(R.string.menu_inquisite_history);
        } else if (endpoint.contentEquals(Define.WebUrl.URL_FAQ)) {
            return getString(R.string.menu_faq2);
        } else if (endpoint.contentEquals(Define.WebUrl.TRADE_LAW)) {
            return getString(R.string.menu_commerce_setting);
        }
        return null;
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(true);
        }
    }

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private class CandyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {


            mFilePathCallback = filePathCallback;

            if (!Utils.hasReadStoragePermission(getContext().getApplicationContext())) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                return false;
            }

            requestSelectImage();

            return true;
        }
    }

    private void requestSelectImage(){

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");


        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }


    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == INPUT_FILE_REQUEST_CODE ) {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
                if( mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(results);
                }
                mFilePathCallback = null;
            }
        }else{
            if( mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = null;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//               requestSelectImage();
            } else {
                mFilePathCallback = null;
                Utils.showToast(getContext(), R.string.no_permissions_read_storage);
            }
        }
    }
    // WebClient
    @Deprecated
    private class CandyClient extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
        }

        /**
         * knv added
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            RkLogger.d(TAG, "shouldOverrideUrlLoading url: " + url);
            scrollView.scrollTo(0, 0);
            scrollView.pageScroll(View.FOCUS_UP);

            if (url.contains(CREDIT_CGISERVER)) {
                // re-load point
                Intent intent = new Intent(getActivity(), CreditPurchaseActivity.class);
                intent.putExtra(CreditPurchaseActivity.INTENT_URL, url);
                startActivity(intent);
                return true;
            }

            if (url.contains("candy://")) {
                if ("candy://buy.point?id=candy1080jpy&discount=1".equals(url)) {
//                    Dialog72Hour dialog72Hour = new Dialog72Hour();
//                    dialog72Hour.show(getChildFragmentManager(), dialog72Hour.getClass().getSimpleName());
                    return true;
                }

                if (SettingManager.getInstance().getMemberInformation().getCode() == 0) {
                    ((BaseActivity) getActivity()).showDialogErrorMemberCode(getActivity());
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);

        }
    }

    private void showDialogErrorMemberCode() {
        mNoticeCheckCodeDialog = DialogBuilder.NoticeTitleDialog.newInstance(getString(R.string.purchase_point_member_code_zero_title), getString(R.string.purchase_point_member_code_zero_message));
        mNoticeCheckCodeDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mNoticeCheckCodeDialog.dismiss();
            }

            @Override
            public void onCancelClick() {

            }
        });
        mNoticeCheckCodeDialog.show(getChildFragmentManager(), TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainViewPager.setTouch(true);
        Define.webFragment = null;
    }

    /**
     * return true: webview go back
     * false: webview cannot go back
     */
    public boolean goBack() {
        if (mWvContent != null && mWvContent.canGoBack()) {
            mWvContent.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
