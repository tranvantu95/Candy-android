package com.candy.android.model.eventBus;

/**
 * Created by quannt on 10/11/2016.
 */

public class ReceiveSmsEvent extends BaseEvent {
    private String message;

    public ReceiveSmsEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
