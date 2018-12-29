package com.candy.android.model.eventBus.chat;

import com.candy.android.utils.RkLogger;

/**
 * @author Favo
 * @Created on 21/11/2016.
 */
public class WsReadEvent extends WebSocketEvent {
    private String mailCode;

    public int getMailCode() {
        try {
            return Integer.parseInt(mailCode);
        } catch (NumberFormatException nfe) {
            RkLogger.w(TAG, "Warning parsing: ", nfe);
        }

        return 0;
    }
}
