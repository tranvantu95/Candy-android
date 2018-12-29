package com.candy.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by quannt on 23/11/2016.
 * Des:
 */

public class Campaign implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("coming")
    private String coming;
    @SerializedName("title")
    private String title;
    @SerializedName("duration")
    private String duration;
    @SerializedName("message")
    private String message;
    @SerializedName("image")
    private String image;
    @SerializedName("blog")
    private String blog;
    @SerializedName("shortName")
    private String shortName;
    @SerializedName("category")
    private String category;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("endTime")
    private String endTime;

    protected Campaign(Parcel in) {
        id = in.readString();
        coming = in.readString();
        title = in.readString();
        duration = in.readString();
        message = in.readString();
        image = in.readString();
        blog = in.readString();
        shortName = in.readString();
        category = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }

    public static final Creator<Campaign> CREATOR = new Creator<Campaign>() {
        @Override
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        @Override
        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };

    public int getId() {
        if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id)) {
            return Integer.valueOf(id);
        }
        return 0;
    }

    public String getComing() {
        return coming;
    }

    public String getTitle() {
        try {
            return URLDecoder.decode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return title;
    }

    public String getDuration() {
        try {
            return URLDecoder.decode(duration, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public String getMessage() {
        try {
            return URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return message;
    }

    public String getImage() {
        return image;
    }

    public int getBlog() {
        if (!TextUtils.isEmpty(blog) && TextUtils.isDigitsOnly(blog)) {
            return Integer.valueOf(blog);
        }
        return 0;
    }

    public String getShortName() {
        try {
            return URLDecoder.decode(shortName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return shortName;
    }

    public String getCategory() {
        return category;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(coming);
        parcel.writeString(title);
        parcel.writeString(duration);
        parcel.writeString(message);
        parcel.writeString(image);
        parcel.writeString(blog);
        parcel.writeString(shortName);
        parcel.writeString(category);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
    }
}

