package com.candy.android.model.eventBus.inappbilling;

import com.candy.android.model.eventBus.BaseEvent;

/**
 * Created by Hungnq 1/3/18.
 */

public class PurchaseLimiedPointAdditionEvent extends BaseEvent {
    public int result;

    /**
     * @param result 0 = close dialog
     */
    public PurchaseLimiedPointAdditionEvent(int result) {
        this.result = result;
    }
}
