package com.candy.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.candy.android.R;
import com.candy.android.manager.InAppBillingManager;
import com.candy.android.model.eventBus.inappbilling.BuyingPointEvent;
import com.candy.android.model.eventBus.inappbilling.InAppPurchaseEvent;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.inappbillingv3.IabHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Favo
 * Created on 07/12/2016.
 */

public class MemberPointPurchaseActivity extends BaseActivity {

    private static final String TAG = "IDK-MemberPointPurchase";
    private static final String PARAM_ID = "id";
    private static final String PARAM_DISCOUNT = "discount";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_point_purchase);

        // register event
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        Uri data = intent.getData();
        parseUri(data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onInAppPurchaseEvent(InAppPurchaseEvent event) {
        RkLogger.d(TAG, "event result=" + event.getResult());
        if (event.getResult().getResponse() == IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR ||
                event.getResult().getResponse() == IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE) {
            finish();
        }
    }

    private void parseUri(Uri data) {
        RkLogger.d(TAG, "parse: " + data);

        try {
            String productId = data.getQueryParameter(PARAM_ID);
            String discount = data.getQueryParameter(PARAM_DISCOUNT);
            boolean isWithAddition = "1".equals(discount);
            EventBus.getDefault().post(new BuyingPointEvent(this, productId, isWithAddition));
        } catch (Exception ex) {
            RkLogger.e(TAG, "Exception: ", ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RkLogger.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // knv added

        // Pass on the activity result to the helper for handling
        if (!InAppBillingManager.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            RkLogger.d(TAG, "onActivityResult handled by IABUtil.");
            finish();
        }
    }
}
