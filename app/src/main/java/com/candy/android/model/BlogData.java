package com.candy.android.model;

import android.text.TextUtils;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by quannt on 03/11/2016.
 * Des: blog data property
 */

public class BlogData {
    @SerializedName("postId")
    private String postId;
    @SerializedName("performerCode")
    private String performerCode;
    @SerializedName("performerName")
    private String performerName;
    @SerializedName("performerImageUrl")
    private String performerImageUrl;
    @SerializedName("performerStatus")
    private String performerStatus;
    @SerializedName("performerSmartPhone")
    private String performerSmartPhone;
    @SerializedName("title")
    private String title;
    @SerializedName("niceCount")
    private String niceCount;
    @SerializedName("body")
    private String body;
    @SerializedName("picture")
    private String picture;
    @SerializedName("movie")
    private String movie;
    @SerializedName("movieDuration")
    private String movieDuration;
    @SerializedName("postDate")
    private String postDate;
    @SerializedName("niced")
    private String niced;
    @SerializedName("favorited")
    private String favorited;
    @SerializedName("postCount")
    private String postCount;

    public int getPostId() {
        if (!TextUtils.isEmpty(postId) && TextUtils.isDigitsOnly(postId)) {
            return Integer.valueOf(postId);
        }
        return 0;
    }

    public int getPerformerCode() {
        if (!TextUtils.isEmpty(performerCode) && TextUtils.isDigitsOnly(performerCode)) {
            return Integer.valueOf(performerCode);
        }
        return 0;
    }

    public String getPerformerName() {
        try {
            String str = URLDecoder.decode(performerName, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return performerName;
    }

    public String getTitle() {
        try {
            String str = URLDecoder.decode(title, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return title;
    }

    public int getNiceCount() {
        if (!TextUtils.isEmpty(niceCount) && TextUtils.isDigitsOnly(niceCount)) {
            return Integer.valueOf(niceCount);
        }
        return 0;
    }

    public String getBody() {
        return getBody(false);
    }

    public String getBody(boolean truncateLineFeed) {
        try {
            String str = URLDecoder.decode(body, "UTF-8");
            if (str != null) {

                str = str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
                if (!truncateLineFeed) {
                    str = str.replaceAll("(\\r\\n|\\n)", "<br />");
                }

                return str;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return body;
    }

    public String getPicture() {
        return picture;
    }

    public String getMovie() {
        return movie;
    }

    public int getMovieDuration() {
        if (!TextUtils.isEmpty(movieDuration) && TextUtils.isDigitsOnly(movieDuration)) {
            return Integer.valueOf(movieDuration);
        }
        return 0;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getPerformerImageUrl() {
        return performerImageUrl;
    }

    public int getPerformerStatus() {
        if (!TextUtils.isEmpty(performerStatus) && TextUtils.isDigitsOnly(performerStatus)) {
            if (getPerformerSmartPhone() == Define.SMARTPHONE_ON) {
                return 5;
            }
            return Integer.valueOf(performerStatus);
        }
        return 0;
    }

    public int getNiced() {
        if (!TextUtils.isEmpty(niced) && TextUtils.isDigitsOnly(niced)) {
            return Integer.valueOf(niced);
        }
        return 0;
    }

    public void setNiced(String niced) {
        this.niced = niced;
    }

    public int getFavorited() {
        if (!TextUtils.isEmpty(favorited) && TextUtils.isDigitsOnly(favorited)) {
            return Integer.valueOf(favorited);
        }
        return 0;
    }

    public int getPostCount() {
        if (!TextUtils.isEmpty(postCount) && TextUtils.isDigitsOnly(postCount)) {
            return Integer.valueOf(postCount);
        }
        return 0;
    }

    public void setNiceCount(String niceCount) {
        this.niceCount = niceCount;
    }

    public int getPerformerSmartPhone() {
        try {
            return Integer.valueOf(performerSmartPhone);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
