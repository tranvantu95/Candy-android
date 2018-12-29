package com.candy.android.custom;

import android.os.Handler;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Hungnq on 12/15/17.
 */

public class ClickHandler {
    private Handler handler;
    private long delayTime;
    private Set<Integer> blockedViews;

    public ClickHandler(long delayTime) {
        this.handler = new Handler();
        this.delayTime = delayTime;
        this.blockedViews = new LinkedHashSet<>();
    }

    public boolean isClickable(final int viewId) {
        boolean allowClick = !blockedViews.contains(viewId);
        if (allowClick) {
            blockedViews.add(viewId);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blockedViews.remove(viewId);
                }
            }, delayTime);
        }
        return allowClick;
    }
}
