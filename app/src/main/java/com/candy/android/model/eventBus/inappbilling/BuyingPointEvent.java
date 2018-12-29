package com.candy.android.model.eventBus.inappbilling;

import android.app.Activity;

import com.candy.android.model.eventBus.BaseEvent;

/**
 * An event instance showing that a product gonna be bought
 *
 * @author Favo
 * @Created on 05/12/2016.
 */

public class BuyingPointEvent extends BaseEvent {
    private final Activity mActivity;
    private final String mProductID;
    private final boolean withAddition;

    public BuyingPointEvent(Activity mActivity, String mProductID, boolean withAddition) {
        this.mActivity = mActivity;
        this.mProductID = mProductID;
        this.withAddition = withAddition;
    }

    public BuyingPointEvent(Activity activity, String productID) {
        mActivity = activity;
        mProductID = productID;
        this.withAddition = false;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public String getProductID() {
        return mProductID;
    }

    public boolean isWithAddition() {
        return withAddition;
    }
}
