package com.candy.android.model.eventBus.chat;

import com.candy.android.configs.Define;
import com.candy.android.utils.RkLogger;

/**
 * Notice that new mail has been received from specific Performer
 *
 * @author Favo
 * @Created on 21/11/2016.
 */
public class WsSendEvent extends WebSocketEvent {

    private String isAttached;
    private String mailCode;
    private String senderCode;
    private String senderName;
    private String subject;
    private String unreadCount;

    public boolean IsAttached() {
        return Boolean.parseBoolean(isAttached);
    }

    public String getMailCode() {
        return mailCode;
    }

    public int getSenderCode() {
        try {
            return Integer.parseInt(senderCode);
        } catch (NumberFormatException nfe) {
            RkLogger.w("IDK", "Warning parsing unreadCount: ", nfe);
        }

        return Define.REQUEST_FAILED;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSubject() {
        return subject;
    }

    public int getUnreadCount() {
        try {
            return Integer.parseInt(unreadCount);
        } catch (NumberFormatException nfe) {
            RkLogger.w("IDK", "Warning parsing unreadCount: ", nfe);
        }

        return 0;
    }
}
