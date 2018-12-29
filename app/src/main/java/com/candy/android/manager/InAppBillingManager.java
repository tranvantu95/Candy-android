package com.candy.android.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiIncPaymentConfigResponse;
import com.candy.android.http.response.ApiPointAdditionResponse;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.model.eventBus.inappbilling.BuyingPointEvent;
import com.candy.android.model.eventBus.inappbilling.InAppPurchaseEvent;
import com.candy.android.model.eventBus.inappbilling.PurchaseLimiedPointAdditionEvent;
import com.candy.android.model.eventBus.inappbilling.PurchaseRetryEvent;
import com.candy.android.utils.AdjustUtils;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.RkSharedPreferencesUtils;
import com.candy.android.utils.inappbillingv3.IabBroadcastReceiver;
import com.candy.android.utils.inappbillingv3.IabHelper;
import com.candy.android.utils.inappbillingv3.IabResult;
import com.candy.android.utils.inappbillingv3.Inventory;
import com.candy.android.utils.inappbillingv3.Purchase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.StringEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manager class for Android in-app billing
 *
 * @author Favo
 * Created on 05/12/2016.
 */

public final class InAppBillingManager {

    private static final String TAG = "InAppBillingManager";
    public static final String KEY_STORED_PURCHASE = "stored_purchase";
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnDR78VaL4P3vUhLRbuU611x/lg2bbN5UPDTANOXvxbvIhvw+2ngXp/b+rPFxClCihUOrajEJdeUZkzMmU1SNEZJjY3jvFoFlNmsVw6r3XdVHm7kGeqRSfEuD7Ss5k+RFDSc/QKNYBQgh4egFzBUxMRZ85F761zke7UbOHQWcpgZQMXcNLUbnGDVwPeeIvPGL8+4YEask7Y1K25HZcbp0g3+EAXnk3j9jz5MWaJwkEb7f7oJCvs7/lbv78o5chP+kwG14x9DwO0impAMUI/RbA6LFZmCvVSOEM4L70VKamiMZiWUUxyD8ykODx+GOg3NCmVfNuqG+R1WtjTfxC3MT2QIDAQAB";
    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 0x1001;
    private static final int NUMBER_RETRY_ADD_POINT = 0;
    private static final int REQUEST_TIME_OUT = 10000;

    private static InAppBillingManager instance;

    private List<PurchasePointOption> mProductList;

    private boolean isInitialize;
    private boolean mProductListLoaded;
    private WeakReference<AppCompatActivity> mContext;

    // The helper object
    private IabHelper mHelper;
    // Provides purchase notification while this app is running
    private IabBroadcastReceiver mBroadcastReceiver;
    private IabBroadcastReceiver.IabBroadcastListener mIabListener;
    private IabResult mIabSetupError;

    private InAppBillingManager() {
        // no-op
    }

    public static InAppBillingManager getInstance() {
        if (null == instance) {
            instance = new InAppBillingManager();
        }

        return instance;
    }

    public List<PurchasePointOption> getProductList() {
        return mProductList;
    }

    private Gson mGson = new GsonBuilder().create();
    private RkSharedPreferencesUtils mRspu;
    public static ArrayList<Purchase> mListPurchaseError = new ArrayList<>();
    private ArrayList<Purchase> mListPurchase = new ArrayList<>();
    public static boolean mRetry = false;

    private void onConsumePurchaseSuccess(final Purchase purchase) {
        final int memberCode = SettingManager.getInstance().getMemberInformation().getCode();
        RkSharedPreferencesUtils.initialize(mContext.get(), Context.MODE_PRIVATE);
        mRspu = RkSharedPreferencesUtils.getInstance();
        String jsonPurchase = mRspu.getString(KEY_STORED_PURCHASE + "_" + memberCode, null);
        Log.i(TAG, KEY_STORED_PURCHASE + "_" + memberCode);
        if (jsonPurchase != null) {
            mListPurchaseError = mGson.fromJson(jsonPurchase, new TypeToken<ArrayList<Purchase>>() {
            }.getType());
        } else {
            mListPurchaseError.clear();
        }
        //
        Log.e(TAG, new Gson().toJson(purchase).toString());
//        mNumberRetry++;
//        RkLogger.d(TAG, "onConsumePurchaseSuccess: " + mNumberRetry);
        try {
            // get member information
            final MemberInformation member = SettingManager.getInstance(mContext.get()).getMemberInformation();

            // gather your request parameters
            JSONObject jsonParams = new JSONObject();
            jsonParams.put(Define.Fields.FIELD_OWNER_CODE, Define.Fields.FIELD_OWNER_CODE_VALUE);
            jsonParams.put(Define.Fields.FIELD_MEMBER_CODE, member.getCode());
            jsonParams.put(Define.Fields.FIELD_APP_CODE, Define.Fields.FIELD_APP_CODE_VALUE);
            jsonParams.put(Define.Fields.FIELD_RECEIPT, purchase.getOriginalJson());
            jsonParams.put(Define.Fields.FIELD_SIGNATURE, purchase.getSignature());
            Log.i(TAG, "" + member.getCode() + "");
            RkLogger.d(TAG, "Purchase Info: " + jsonParams.toString());
            // send request
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(REQUEST_TIME_OUT);

            RkLogger.d(TAG, "consumption result: post");
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(mContext.get(), Define.API.API_ADD_POINT, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                    RkLogger.d(TAG, "consumption result: success");
                    // handle success response
                    if (statusCode == HttpStatus.SC_OK) {
                        Log.i(TAG, "SC OK");
                        try {
                            String response = (new String(bytes, Define.CONST_UTF_8)).trim();
                            if (Define.SubmitCode.OK.equals(response)) {
                                Log.i(TAG, "SubmitCode: OK");
//                                mNumberRetry = 0;
                                updateMemberPoint(purchase);
                                try {
                                    for (PurchasePointOption option : mProductList) {
                                        if (purchase.getSku().contentEquals(option.getProductId())) {
                                            int price = option.getPrice();
                                            // Send track event
                                            AdjustUtils.trackEventPurchasePointSuccess(price);
                                            RkLogger.d(TAG, "Adjust track sent: " + price);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    // lazy catching
                                }
                                for (int i = 0; i < mListPurchaseError.size(); i++) {
                                    if (mListPurchaseError.get(i).getOrderId().equals(purchase.getOrderId())) {
                                        mListPurchaseError.remove(i);
                                    }
                                }
                                EventBus.getDefault().post(new PurchaseRetryEvent(0));
                                //success hide layout button retry
                                if (mListPurchaseError.size() == 0 || mListPurchaseError == null) {
                                    mRspu.remove(KEY_STORED_PURCHASE + "_" + memberCode);
                                    EventBus.getDefault().post(new PurchaseRetryEvent(1));
                                }
                            } else if (Define.SubmitCode.NG.equals(response)) {
                                Log.i(TAG, "SubmitCode: NG");
//                                if (mNumberRetry >= NUMBER_RETRY_ADD_POINT) {
//                                    mNumberRetry = 0;
//                                    showPurchaseErrorDiaRkLogger();
//                                } else {
//                                    onConsumePurchaseSuccess(purchase);
//                                }
                                // show layout button retry
                                savePurchaseFailed(purchase);

                            } else if (Define.SubmitCode.INVALID.equals(response)) {
                                Log.i(TAG, "SubmitCode: INVALID");
                                savePurchaseFailed(purchase);
                            } else {
                                Log.i(TAG, "SubmitCode: ERROR");
//                                mNumberRetry = 0;
                                showPurchaseErrorDiaRkLogger();
                            }
                        } catch (Exception ex) {
                            RkLogger.w(TAG, "Exception: ", ex);
                        }
                    } else {
//                        if (mNumberRetry >= NUMBER_RETRY_ADD_POINT) {
//                            RkLogger.d(TAG, "response: ELSE");
//                            mNumberRetry = 0;
//                            showPurchaseErrorDiaRkLogger();
//                        } else {
//                            RkLogger.d(TAG, "response: ELSE Request again");
//                            onConsumePurchaseSuccess(purchase);
//                        }
//                        EventBus.getDefault().post(new PurchaseRetryEvent(0));
                        showPurchaseErrorDiaRkLogger();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                    // handle failure response
                    RkLogger.d(TAG, "consumption result: failure");
                    RkLogger.e(TAG, "Failed: ", throwable);
                    savePurchaseFailed(purchase);
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "Failed: ", ex);
        }
    }

    private void savePurchaseFailed(Purchase purchase) {
        if (mListPurchaseError != null) {
            if (mListPurchaseError.size() == 0) {
                mListPurchaseError.add(purchase);
            } else {
                boolean isContains = false;
                for (int i = 0; i < mListPurchaseError.size(); i++) {
                    if (mListPurchaseError.get(i).getOrderId().equals(purchase.getOrderId())) {
                        isContains = true;
                    }
                }
                if (!isContains) mListPurchaseError.add(purchase);
            }
        }
        Log.i(TAG, "mListPurchaseError: " + mListPurchaseError.size());
        EventBus.getDefault().post(new PurchaseRetryEvent(0));
        EventBus.getDefault().post(new PurchaseLimiedPointAdditionEvent(1));
    }

    private void showPurchaseErrorDiaRkLogger() {
        AppCompatActivity activity = mContext.get();
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(activity.getString(R.string.purchase_point_failed), null);
        Helpers.showDialogFragment(activity.getSupportFragmentManager(), noticeDiaRkLogger);
    }

    private void showPurchaseRetrySuccess() {
        AppCompatActivity activity = mContext.get();
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(activity.getString(R.string.purchase_point_retry_success), null);
        Helpers.showDialogFragment(activity.getSupportFragmentManager(), noticeDiaRkLogger);
    }

    private void showPurchaseRetryFailed() {
        AppCompatActivity activity = mContext.get();
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(activity.getString(R.string.purchase_point_retry_failed), null);
        Helpers.showDialogFragment(activity.getSupportFragmentManager(), noticeDiaRkLogger);
    }

    private void showErrorLimitedPoint(String errorMessages) {
        AppCompatActivity activity = mContext.get();
        DialogBuilder.NoticeDialog noticeDiaRkLogger = DialogBuilder.buildNoticeDialog(errorMessages, null);
        Helpers.showDialogFragment(activity.getSupportFragmentManager(), noticeDiaRkLogger);
    }

    //    private void savePurchaseFail(MemberInformation member, Purchase purchase) {
//        String memberCode = Integer.toString(member.getCode());
//        HashMap<String, String> purchaseFailMap = SettingManager.getInstance(mContext.get()).getPurchaseFailMap(memberCode);
//        if(purchaseFailMap.get(purchase.getOriginalJson()) == null) {
//            purchaseFailMap.put(purchase.getOriginalJson(), String.format("%s,%s", purchase.getSignature(), purchase.getSku()));
//        }
//        SettingManager.getInstance(mContext.get()).savePurchaseFailMap(memberCode, purchaseFailMap);
//    }
    int addedPoint = 0;
    String purchaseSku;

    private void updateMemberPoint(@NonNull Purchase purchase) {
        if (!mProductListLoaded) {
            return;
        }
        purchaseSku = purchase.getSku();
        String id = SettingManager.getInstance().getMemberInformation().getId();
        String pass = SettingManager.getInstance().getMemberInformation().getPass();
        ApiInterface apiInterface = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        if (purchaseSku.contentEquals("candy1080jpy") && purchase.isWithAdditionPoint()) {
            Call<ApiPointAdditionResponse> call = apiInterface.getPointAddition(id, pass, Define.Fields.FIELD_DEVICE_VALUE);
            call.enqueue(new Callback<ApiPointAdditionResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiPointAdditionResponse> call, @NonNull Response<ApiPointAdditionResponse> response) {
                    Log.i(TAG, "onResponse");
                    if (response.body() == null)
                        return;
                    Log.i(TAG, response.toString());
                    if (response.body().getIsSuccess()) {
                        if (response.body().getAddPoint().getIsSuccess()) {
                            Log.i(TAG, "ApiPointAddition true:" + response.body().getAddPoint().getIsSuccess() + "");
                            for (PurchasePointOption option : mProductList) {
                                if (purchaseSku.contentEquals(option.getProductId())) {
                                    addedPoint = 1500;
                                    EventBus.getDefault().post(new PurchaseLimiedPointAdditionEvent(0));
                                    break;
                                }
                            }
                            MemberInformation member = SettingManager.getInstance(mContext.get()).getMemberInformation();
                            member.setPoint(member.getPoint() + addedPoint);
                            member.setmBuyTime(SettingManager.getInstance().getMemberInformation().getmBuyTime() + 1);
                            HimecasUtils.postEventBus(mContext.get(), member);
                        } else {
                            Log.i(TAG, "ApiPointAddition f:" + response.body().getAddPoint().getIsSuccess() + "");
                            for (PurchasePointOption option : mProductList) {
                                if (purchaseSku.contentEquals(option.getProductId())) {
                                    addedPoint = option.getPoint();
                                    EventBus.getDefault().post(new PurchaseLimiedPointAdditionEvent(0));
                                    break;
                                }
                            }
                            MemberInformation member = SettingManager.getInstance(mContext.get()).getMemberInformation();
                            member.setmBuyTime(SettingManager.getInstance().getMemberInformation().getmBuyTime() + 1);
                            member.setPoint(member.getPoint() + addedPoint);
                            HimecasUtils.postEventBus(mContext.get(), member);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiPointAdditionResponse> call, Throwable t) {
                    Log.i(TAG, "onFailure");
                }
            });
        } else {
            for (PurchasePointOption option : mProductList) {
                if (purchaseSku.contentEquals(option.getProductId())) {
                    addedPoint = option.getPoint();
                    EventBus.getDefault().post(new PurchaseLimiedPointAdditionEvent(0));
                    break;
                }
            }
            MemberInformation member = SettingManager.getInstance(mContext.get()).getMemberInformation();
            member.setPoint(member.getPoint() + addedPoint);
            member.setmBuyTime(SettingManager.getInstance().getMemberInformation().getmBuyTime() + 1);
            HimecasUtils.postEventBus(mContext.get(), member);
        }
    }

    public void doRequestProductList(final OnProductListLoadedListener listener) {
        if (mProductListLoaded) {
            if (listener != null) {
                listener.onProductListLoaded(true);
            }
            return;
        }

        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);

        //create call object
        Call<ApiIncPaymentConfigResponse> call = apiService.getIncPaymentConfig();

        // send call object
        call.enqueue(new Callback<ApiIncPaymentConfigResponse>() {
            @Override
            public void onResponse(Call<ApiIncPaymentConfigResponse> call, Response<ApiIncPaymentConfigResponse> response) {
                handleGetIncPaymentConfigResponse(response, null, listener);
            }

            @Override
            public void onFailure(Call<ApiIncPaymentConfigResponse> call, Throwable t) {
                handleGetIncPaymentConfigResponse(null, t, listener);
            }
        });
    }

    private void handleGetIncPaymentConfigResponse(Response<ApiIncPaymentConfigResponse> response,
                                                   Throwable t,
                                                   OnProductListLoadedListener listener) {
        if (response != null && response.body() != null) {
            RkLogger.d(TAG, "getIncPaymentConfig response success");

            mProductList = response.body().getProductList();
            mProductListLoaded = true;

        } else {
            RkLogger.w(TAG, "getIncPaymentConfig failed: ", t);
            mProductListLoaded = false;
        }

        if (listener != null) {
            listener.onProductListLoaded(mProductListLoaded);
        }
    }

    public void initialize(AppCompatActivity context, IabBroadcastReceiver.IabBroadcastListener listener) {
        if (isInitialize) {
            return;
        }

        mContext = new WeakReference<>(context);
        mIabListener = listener;

        mHelper = new IabHelper(mContext.get(), BASE64_PUBLIC_KEY);
        mHelper.enableDebugLogging(BuildConfig.DEBUG);

        setupIABHelper();
        EventBus.getDefault().register(this);

        isInitialize = true;
    }

    private void checkCorrectSetup() {
        if (!isInitialize) {
            throw new RuntimeException("Must call initialize() first");
        }

        if (null == mContext || null == mContext.get()) {
            throw new RuntimeException("Context must not be null");
        }

        if (null == mIabListener) {
            throw new RuntimeException("IabListener must not be null");
        }
    }

    private void setupIABHelper() {

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                RkLogger.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    mIabSetupError = result;
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                mIabSetupError = null;

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(mIabListener);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                mContext.get().registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                RkLogger.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    // Called when consumption is complete
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            purchase.setWithAdditionPoint(isWithAddition);
            isWithAddition = false;
            RkLogger.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isSuccess()) {
                RkLogger.d(TAG, "Consumption successful. Provisioning.");
                if (!NetworkUtils.hasConnection(mContext.get())) {
                    Log.i(TAG, "SubmitCode: DISCONNECT");
                    savePurchaseFailed(purchase);
                } else {
                    onConsumePurchaseSuccess(purchase);
                }
            } else {
                complain("Error while consuming: " + result);
            }
            RkLogger.d(TAG, "End consumption flow.");
        }
    };

    // Listener that's called when we finish querying the items and subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            RkLogger.d(TAG, "Query inventory finished");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            RkLogger.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */
            List<Purchase> purchases = inventory.getAllPurchases();
            for (Purchase pointPurchase : purchases) {
                if (null != pointPurchase) {
                    RkLogger.d(TAG, "We ar consuming: " + pointPurchase);
                    try {
                        mHelper.consumeAsync(pointPurchase, mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error consuming product. Another async operation in progress.");
                    }
                }
            }
        }
    };

    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            RkLogger.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                EventBus.getDefault().post(new InAppPurchaseEvent(result));
                complain("Error purchasing: " + result);
                return;
            }

            RkLogger.d(TAG, "Purchase successful.");

            if (null != purchase) {
                RkLogger.d(TAG, "Purchase is: " + purchase);
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming product. Another async operation in progress.");
                }
            }
        }
    };

    public void receivedBroadcast() {
        checkCorrectSetup();

        // Received a broadcast notification that the inventory of items has changed
        RkLogger.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Subscribe
    public void onPuchaseRetryEvent(PurchaseRetryEvent event) {
        int memberCode = SettingManager.getInstance().getMemberInformation().getCode();
        if (event.result == 0) {// event invalid, ng
            mRspu = RkSharedPreferencesUtils.getInstance();
            String jsonPurchase = mGson.toJson(mListPurchaseError);
            mRspu.remove(KEY_STORED_PURCHASE + "_" + memberCode);
            mRspu.saveString(KEY_STORED_PURCHASE + "_" + memberCode, jsonPurchase);
        }
        if (event.result == 2) {// event click button retry
            mRetry = true;
            mRspu = RkSharedPreferencesUtils.getInstance();
            String jsonPurchase = mRspu.getString(KEY_STORED_PURCHASE + "_" + memberCode, null);
            mListPurchase.clear();
            mListPurchase = mGson.fromJson(jsonPurchase, new TypeToken<ArrayList<Purchase>>() {
            }.getType());
            if (jsonPurchase != null) {
                Log.d(TAG, mListPurchase.size() + "");
                for (int i = 0; i < mListPurchase.size(); i++) {
                    Log.i(TAG, mListPurchase.get(i).getOriginalJson());
                    onConsumePurchaseSuccess(mListPurchase.get(i));
                }
                if (mListPurchaseError == null) {
                    mRspu.saveString(KEY_STORED_PURCHASE + "_" + memberCode, null);
                } else {
                    mRspu.saveString(KEY_STORED_PURCHASE + "_" + memberCode, mGson.toJson(mListPurchaseError));
                }
            }
            Log.i(TAG, "retry purchase point");
        }
    }

    boolean isWithAddition;

    @Subscribe
    public void onBuyingPointEvent(BuyingPointEvent event) {
        checkCorrectSetup();
        isWithAddition = event.isWithAddition();
        if (mIabSetupError != null) {
            if (mIabSetupError.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE) {
                alertBuyingUnAvailable(event.getActivity());
            }
        } else {
            RkLogger.d(TAG, "Launching purchase flow for product: " + event.getProductID());
            String payload = "";


            try {
                mHelper.launchPurchaseFlow(event.getActivity(), event.getProductID(), RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
            }
        }
    }

    private void alertBuyingUnAvailable(@NonNull Activity activity) {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        bld.setMessage(activity.getString(R.string.str_iab_should_update_google_play));
        bld.setNeutralButton("OK", null);
        bld.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface diaRkLoggerInterface) {
                EventBus.getDefault().post(new InAppPurchaseEvent(mIabSetupError));
            }
        });

        bld.create().show();
    }

    private void complain(String message) {
        RkLogger.e(TAG, "**** InAppBillingManager Error: " + message);
//        alert("Error: " + message);   // show no diaRkLogger at all
    }

//    private void alert(String message) {
//        AlertDiaRkLogger.Builder bld = new AlertDiaRkLogger.Builder(mContext.get());
//        bld.setMessage(message);
//        bld.setNeutralButton("OK", null);
//        RkRkLogger.d(TAG, "Showing alert diaRkLogger: " + message);
//        bld.create().show();
//    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        checkCorrectSetup();

        if (mHelper == null) return false;

        // Pass on the activity result to the helper for handling
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void onActivityDestroy() {
        checkCorrectSetup();
        EventBus.getDefault().unregister(this);
        // very important:
        if (mBroadcastReceiver != null) {
            mContext.get().unregisterReceiver(mBroadcastReceiver);
        }


        // very important:
        RkLogger.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            try {
                mHelper.disposeWhenFinished();
            }catch (Exception e){

            }
            mHelper = null;
        }

        isInitialize = false;
    }

    public interface OnProductListLoadedListener {
        void onProductListLoaded(boolean productListLoaded);
    }
}
