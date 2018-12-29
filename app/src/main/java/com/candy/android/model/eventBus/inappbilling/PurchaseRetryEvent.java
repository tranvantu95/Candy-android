package com.candy.android.model.eventBus.inappbilling;

import com.candy.android.model.eventBus.BaseEvent;

/**
 * Created by Hungnq on 12/19/17.
 */

public class PurchaseRetryEvent extends BaseEvent {
    public int result;

    /**
     * @param result 0 = fail, 1 = success, 2 = retry
     */
    public PurchaseRetryEvent(int result) {
        this.result = result;
    }
}
