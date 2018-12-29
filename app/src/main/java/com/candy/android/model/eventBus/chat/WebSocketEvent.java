package com.candy.android.model.eventBus.chat;

import com.candy.android.model.eventBus.BaseEvent;
import com.google.gson.Gson;

/**
 * @author Favo
 * Created on 21/11/2016.
 */

public class WebSocketEvent extends BaseEvent {
    protected static final String TAG = "IDK-Wss";

    private String code;
    private String action;

    public String getAction() {
        return action;
    }

    public String getCode() {
        return code;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
