package com.candy.android.utils;

import android.content.Context;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.candy.android.BuildConfig;
import com.candy.android.configs.Define;

/**
 * Created by HoangVuNam on 4/6/17.
 */

public class AdjustUtils {

    public static void initialize(Context context) {
        String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(context, Define.ADJUST.APP_TOKEN, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.onCreate(config);
    }

    public static void trackEventConfirmIDPassword(String id, String pass) {
        AdjustEvent event = new AdjustEvent(Define.ADJUST.EVENT_EMAIL_PASSWORD_TOKEN);
        event.addCallbackParameter(Define.ADJUST.MEM_ID, id);
        event.addCallbackParameter(Define.ADJUST.MEM_PASS, pass);
        Adjust.trackEvent(event);
    }

    public static void trackEventPurchasePointSuccess(double point) {
        AdjustEvent event = new AdjustEvent(Define.ADJUST.EVENT_PURCHASE_SUCCESS_TOKEN);
        event.setRevenue(point, Define.ADJUST.EVENT_CURRENCY);
        Adjust.trackEvent(event);
    }

}
