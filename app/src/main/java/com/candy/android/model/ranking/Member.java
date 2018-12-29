package com.candy.android.model.ranking;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Member {
        @SerializedName("point")
        private String point;
        @SerializedName("ranking")
        private Ranking ranking;

        public int getPoint() {
            return !TextUtils.isEmpty(point) && TextUtils.isDigitsOnly(point) ? Integer.valueOf(point) : -1;
        }

        public Ranking getRanking() {
            return ranking;
        }
    }