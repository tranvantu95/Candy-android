package com.candy.android.model.eventBus;

/**
 * Created by quannt on 27/10/2016.
 * Description
 */

public class BaseEvent {
    public final long id;

    public BaseEvent() {
        id = System.nanoTime();
    }
}
