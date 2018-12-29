package com.candy.android.model.eventBus.chat;

import com.candy.android.configs.Define;
import com.candy.android.utils.RkLogger;

/**
 * Notify me that the opponent performer is inputting mail
 * PartnerCode: partner's performerCode
 *
 * @author Favo
 * Created on 21/11/2016.
 */
public class WsWriteEvent extends WebSocketEvent {

    private String partnerCode;

    public int getPartnerCode() {
        try {
            return Integer.parseInt(partnerCode);
        } catch (Exception ex) {
            RkLogger.w(TAG, "Exception parsing partnerCode: ", ex);
        }
        return Define.REQUEST_FAILED;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
}
