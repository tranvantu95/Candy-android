package com.candy.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogPointMissing;
import com.candy.android.fragment.VideoPurchasePointFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.MemberCameraPointConsumptionResponse;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by quannt on 23/03/2017.
 * Des: Add purchase point function to ChatActivity
 */

public class VideoCallActivity extends VideoCallBasePopupActivity implements View.OnClickListener {

    private static final String TAG = "VideoCallActivity";

    private FrameLayout mPurchaseContainer;
    private boolean isShowPurchasePointDialog;
    boolean isShowed1000 = false;
    boolean isShowed600 = false;
    ArrayList<Integer> arrayList = new ArrayList<>();

    private LinearLayout mLl_popup;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static boolean active = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPurchaseContainer = (FrameLayout) findViewById(jp.fmaru.app.livechatapp.R.id.purchase_point_container);
        mAddPointLayout.setOnClickListener(this);
        findViewById(jp.fmaru.app.livechatapp.R.id.add_point_layout).setOnClickListener(this);
        mLl_popup = (LinearLayout) findViewById(R.id.line_popup);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Define.IntentActions.ACTION_INAPP)) {
                    final String title = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_NAME);
                    final String img = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_IMAGE);
                    final String message = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_MESSAGE);
                    final int code = (int) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_CODE);
                    final int age = (int) intent.getIntExtra(Define.IntentExtras.PERFORMER_AGE, 0);

                    showPopupMessage(title, message, img, code, context, mLl_popup, age);
                }
            }
        };

//        showPopupMessage("title", "message", "https://picture2.livede55.com/images/p5-1288173088", 1288173088, this, mLl_popup, 23); //test
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case jp.fmaru.app.livechatapp.R.id.add_point_layout:
                hideKeyboard();
                showPurchasePointDialog(true);
                break;
        }
    }

    public void autoShowMessageIfPointIsLow() {
        int max1000 = 0;
        int min1000 = 0;
        int max600 = 0;
        int min600 = 0;
        for (int i = 0; i < arrayList.size(); i++) {

            if (arrayList.get(i) > MIN_POINT_TO_SHOW_MESSAGE) {
                max600 = arrayList.get(i);
            }
            if (arrayList.get(i) < MIN_POINT_TO_SHOW_MESSAGE) {
                min600 = arrayList.get(i);
            }
            if (arrayList.get(i) > MAX_POINT_TO_SHOW_MESSAGE) {
                max1000 = arrayList.get(i);
            }
            if (arrayList.get(i) < MAX_POINT_TO_SHOW_MESSAGE) {
                min1000 = arrayList.get(i);
            }
            if (!isShowed600 && max600 > 0 && min600 > 0) {
                showPurchasePointDialog(false);
                isShowed600 = true;
            }
            if (!isShowed1000 && max1000 > 0 && min1000 > 0) {
                showPurchasePointDialog(false);
                isShowed1000 = true;
            }
        }
    }

    public void showPurchasePointDialog(boolean isEnoughPoint) {
        isShowPurchasePointDialog = true;
        mAddPointLayout.setOnClickListener(null);
        mPurchaseContainer.setVisibility(View.VISIBLE);
        mBottomBarLayout.setVisibility(View.GONE);
        VideoPurchasePointFragment videoPurchasePointFragment = VideoPurchasePointFragment.newInstance(null, DialogPointMissing.TYPE_VIDEO_CHAT, isEnoughPoint);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(jp.fmaru.app.livechatapp.R.id.purchase_point_container, videoPurchasePointFragment);
        fragmentTransaction.commit();
    }

    public void hidePurchasePointDialog() {
        isShowPurchasePointDialog = false;
        mAddPointLayout.setOnClickListener(this);
        mPurchaseContainer.setVisibility(View.INVISIBLE);
        mPurchaseContainer.removeAllViews();
        mBottomBarLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(PropertyChangedEvent property) {
        if (property != null) {
            if (property.getType() == PropertyChangedEvent.TYPE_POINT_CHANGED_VIDEO) {
                RkLogger.d(TAG, "onPropertyChangedEvent update TYPE_POINT_CHANGED");
                int point = property.getValue();
                changePointText(point);
                if (isShowPurchasePointDialog) {
                    hidePurchasePointDialog();
                }
            }
        }
    }

    @Override
    public void changePointText(JSONObject json) {
        super.changePointText(json);
        try {
            String point = json.getString("point");
            int realpoint = Integer.parseInt(point);
            arrayList.add(realpoint);
            autoShowMessageIfPointIsLow();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isShowPurchasePointDialog) {
            hidePurchasePointDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Define.IntentActions.ACTION_INAPP));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Define.IntentActions.ACTION_INAPP));
    }

    @Override
    protected void onClickOpenDetail(String name, int code, String img, int age) {
        mLl_popup.removeAllViews();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_CHAT);
        intent.putExtra(Define.IntentExtras.PERFORMER_CODE, code);
        intent.putExtra(Define.IntentExtras.PERFORMER_NAME, name);
        intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, img);
        intent.putExtra(Define.IntentExtras.PERFORMER_AGE, age);
        startActivity(intent);
        finish();
    }

    private DialogBuilder.NoticeTitleDialog mNoticeCheckCodeDialog;

    public void showDialogErrorMemberCode(Context context) {
        mNoticeCheckCodeDialog = DialogBuilder.NoticeTitleDialog.newInstance(context.getString(R.string.purchase_point_member_code_zero_title), context.getString(R.string.purchase_point_member_code_zero_message));
        mNoticeCheckCodeDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mNoticeCheckCodeDialog.dismiss();
            }

            @Override
            public void onCancelClick() {

            }
        });
        Helpers.showDialogFragment(((FragmentActivity) context).getSupportFragmentManager(), mNoticeCheckCodeDialog);
    }

    @Override
    protected void sendMemberCameraPointConsumption() {
        super.sendMemberCameraPointConsumption();

        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        apiService.sendMemberCameraPointConsumption(mMemberId, mMemberPass, mPerformerCode).enqueue(
                new Callback<MemberCameraPointConsumptionResponse>() {
                    @Override
                    public void onResponse(Call<MemberCameraPointConsumptionResponse> call, Response<MemberCameraPointConsumptionResponse> response) {
//                        if(response != null && response.body() != null && response.body().isSuccess()) {
//                            Toast.makeText(getApplicationContext(), "sendMemberCameraPointConsumption success", Toast.LENGTH_LONG).show();
//                        }
                    }

                    @Override
                    public void onFailure(Call<MemberCameraPointConsumptionResponse> call, Throwable t) {

                    }
                }
        );
    }
}
