package com.candy.android.model.eventBus;

/**
 * Created by Hungnq on 14/12/2016.
 */

public class RandomCallVideoEvent extends BaseEvent {
    public int result;

    public RandomCallVideoEvent(int result) {
        this.result = result;
    }
}
