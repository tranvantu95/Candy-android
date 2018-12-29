package com.candy.android.model.eventBus.inappbilling;

import com.candy.android.model.eventBus.BaseEvent;
import com.candy.android.utils.inappbillingv3.IabResult;

/**
 * An event instance showing that a purchase returns its result
 *
 * @author Favo
 * Created on 09/12/2016.
 */

public class InAppPurchaseEvent extends BaseEvent {

    private IabResult mResult;

    public InAppPurchaseEvent(IabResult result) {
        mResult = result;
    }

    public IabResult getResult() {
        return mResult;
    }
}
