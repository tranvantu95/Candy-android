package com.candy.android.model;

import android.text.TextUtils;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quannt on 10/17/2016.
 * Description: Detail about performer
 */

public class PerformerDetail extends BasePerformer {
    @SerializedName("slideImageUrl")
    private String slideImageUrl;
    @SerializedName("tall")
    private String tall;
    @SerializedName("bust")
    private String bust;
    @SerializedName("waist")
    private String waist;
    @SerializedName("hip")
    private String hip;
    @SerializedName("style")
    private String style;
    @SerializedName("mike")
    private String mike;
    @SerializedName("blood")
    private String blood;
    @SerializedName("job")
    private String job;
    @SerializedName("favorite")
    private String favorite;
    @SerializedName("infestedTime")
    private String infestedTime;
    @SerializedName("prMovie")
    private String prMovie;
    @SerializedName("gallery")
    private List<Gallery> gallery;
    @SerializedName("currentCampaign")
    private String currentCampaign;
    @SerializedName("pointPerMinuteCampaign")
    private String pointPerMinuteCampaign;
    @SerializedName("object")
    private String object;
    @SerializedName("favorited")
    private String favorited;
    @SerializedName("watchingCount")
    private int watchingCount;

    public PerformerDetail() {
        super();
        slideImageUrl = "";
        tall = "";
        bust = "";
        waist = "";
        hip = "";
        style = "";
        mike = "";
        blood = "";
        job = "";
        favorite = "";
        infestedTime = "";
        prMovie = "";
        gallery = new ArrayList<>();
        currentCampaign = "";
        pointPerMinuteCampaign = "0";
        object = "";
        favorited = "";
        watchingCount = 0;
    }

    public int getCurrentCampaign() {
        if (!TextUtils.isEmpty(currentCampaign) && TextUtils.isDigitsOnly(currentCampaign)) {
            return Integer.valueOf(currentCampaign);
        }
        return -1;
    }

    public int getPointPerMinuteCampaign() {
        if (!TextUtils.isEmpty(pointPerMinuteCampaign) && TextUtils.isDigitsOnly(pointPerMinuteCampaign)) {
            return Integer.valueOf(pointPerMinuteCampaign);
        }
        return -1;
    }

    public int getTall() {
        try {
            return Integer.valueOf(tall);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getBust() {
        try {
            return Integer.valueOf(bust);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getWaist() {
        try {
            return Integer.valueOf(waist);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getHip() {
        try {
            return Integer.valueOf(hip);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getStyle() {
        return style;
    }

    public int getMike() {
        try {
            return Integer.valueOf(mike);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getSlideImageUrl() {
        return slideImageUrl;
    }

    public void setSlideImageUrl(String slideImageUrl) {
        this.slideImageUrl = slideImageUrl;
    }

    public String getBlood() {
        return blood;
    }

    public String getJob() {
        try {
            return URLDecoder.decode(job, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return job;
    }

    public String getFavorite() {
        try {
            return URLDecoder.decode(favorite, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return favorite;
    }

    public String getInfestedTime() {
        return infestedTime;
    }

    public String getPrMovie() {
        return prMovie;
    }

    public List<Gallery> getGallery() {
        return isPublic.equals(NO_PUBLIC_STRING) ? new ArrayList<Gallery>() : gallery;
    }

    public String getObject() {
        return object;
    }

    public int getFavorited() {
        try {
            return Integer.valueOf(favorited);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public class Gallery {
        @SerializedName("thumbnail")
        private String thumbnail;
        @SerializedName("image")
        private String image;
        @SerializedName("comment")
        private String comment;

        public String getThumbnail() {
            return thumbnail;
        }

        public String getImage() {
            return image;
        }

        public String getComment() {
            try {
                String str = URLDecoder.decode(comment, "UTF-8");
                if (str != null) {
                    return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
                } else {
                    return "";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return comment;
        }
    }

    public int getWatchingCount() {
        return watchingCount;
    }
}
