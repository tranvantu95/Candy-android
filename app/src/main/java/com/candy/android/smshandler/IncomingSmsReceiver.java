package com.candy.android.smshandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.candy.android.model.eventBus.ReceiveSmsEvent;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by quannt on 10/11/2016.
 * Des:
 */

public class IncomingSmsReceiver extends BroadcastReceiver {
    //CANDY認証コード： 734069
    private static final String SMS_FORMAT = "CANDY認証コード：";

    @Override
    public void onReceive(Context context, Intent intent) {
        RkLogger.d("Check sms >>", "receiver onReceive");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                for (int i = 0; i < pdusObj.length; i++) {
                    RkLogger.d("Check sms >>", "i: " + i);
                    SmsMessage currentMessage;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    } else {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    }
//                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (message.contains(SMS_FORMAT)) {
                            String token = message.substring(message.length() - 6);
                            ReceiveSmsEvent event = new ReceiveSmsEvent(token);
                            RkLogger.d("Check sms >>", "receiver post event");
                            EventBus.getDefault().post(event);
//                            abortBroadcast();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
