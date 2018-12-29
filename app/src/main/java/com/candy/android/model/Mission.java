package com.candy.android.model;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Mission object
 * Created by brianhoang on 6/2/17.
 */

public abstract class Mission {
    protected String id;

    protected String point;

    protected String title;


    public int getId() {
        return !TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id) ? Integer.valueOf(id) : -1;
    }

    public int getPoint() {
        return !TextUtils.isEmpty(point) && TextUtils.isDigitsOnly(point) ? Integer.valueOf(point) : 0;
    }

    public String getTitle() {
        try {
            return URLDecoder.decode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return title;
        }
    }

    public abstract String getProgress();
}
