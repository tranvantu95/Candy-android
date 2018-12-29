package com.candy.android.model;

/**
 * Mission object
 * Format:
 * <p>
 * {
 * "id": "13",
 * "title": "%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8%E3%82%92100%E9%80%9A%E9%80%81%E3%82%8D%E3%81%86",
 * "point": "10",
 * "date": "2017-03-27"
 * }
 * <p>
 * Created by brianhoang on 6/2/17.
 */

public class CompletedMission extends Mission {
    private String date;

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Mission [id = " + id + ", point = " + point + ", title = " + title + ", date = " + date + "]";
    }

    @Override
    public String getProgress() {
        return "CLEAR";
    }
}
