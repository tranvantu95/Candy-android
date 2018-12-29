package com.candy.android.model;

import android.text.TextUtils;
import android.util.Log;

import com.candy.android.configs.Define;
import com.candy.android.utils.HimecasUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

/**
 * Mission object
 * Format:
 * <p>
 * {
 * "id": "18",
 * "title": "%E5%A5%B3%E3%81%AE%E5%AD%90%E3%81%A8360%E5%88%86%E3%83%93%E3%83%87%E3%82%AA%E9%80%9A%E8%A9%B1%E3%81%97%E3%82%88%E3%81%86",
 * "completed": "0",
 * "unit": "%E5%88%86",
 * "point": "20",
 * "message": "%E3%83%93%E3%83%87%E3%82%AA%E9%80%9A%E8%A9%B1%E3%82%92%E5%90%88%E8%A8%88360%E5%88%86%E3%81%97%E3%81%BE%E3%81%97%E3%82%87%E3%81%86%E3%80%82",
 * "description": "%E3%81%8A%E8%A9%B1%E3%81%8C%E4%B8%8A%E6%89%8B%E3%81%8F%E3%81%AF%E3%81%9A%E3%81%BF%E3%81%BE%E3%81%9B%E3%82%93%E3%81%8B%EF%BC%9F%0D%0A%E3%82%B3%E3%83%84%E3%81%AF%E7%9B%B8%E6%89%8B%E3%82%92%E3%83%8E%E3%82%BB%E3%82%8B%E3%81%93%E3%81%A8%E3%80%82%E3%83%AA%E3%82%A2%E3%82%AF%E3%82%B7%E3%83%A7%E3%83%B3%E4%B8%8A%E6%89%8B%E3%81%AB%E3%81%AA%E3%81%A3%E3%81%A6%E3%81%BF%E3%81%BE%E3%81%97%E3%82%87%E3%81%86%E3%80%82",
 * "page": "ap_online_list",
 * "count": "297",
 * "allCount": "360"
 * }
 * <p>
 * Created by brianhoang on 6/2/17.
 */

public class UncompletedMission extends Mission {
    private String message;

    private String unit;

    private String allCount;

    private String count;

    private String page;

    private String description;

    private String completed;

    public String getMessage() {
        try {
            return URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }

    public String getUnit() {
        return unit;
    }

    public int getAllCount() {
        return !TextUtils.isEmpty(allCount) && TextUtils.isDigitsOnly(allCount) ? Integer.valueOf(allCount) : -1;
    }

    public int getCount() {
        return !TextUtils.isEmpty(count) && TextUtils.isDigitsOnly(count) ? Integer.valueOf(count) : 0;
    }

    public String getPage() {
        return page;
    }

    public String getDescription() {
        try {
            return URLDecoder.decode(description, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return description;
        }
    }

    public boolean isCompleted() {
        return !TextUtils.isEmpty(completed) && TextUtils.isDigitsOnly(completed) && Define.SUBMIT_OK == Integer.valueOf(completed);
    }

    @Override
    public String toString() {
        return "Mission [message = " + message + ", id = " + id + ", point = " + point + ", unit = " + unit + ", title = " + title + ", allCount = " + allCount + ", count = " + count + ", page = " + page + ", description = " + description + ", completed = " + completed + "]";
    }

    @Override
    public String getProgress() {
        return isCompleted() ? "CLEAR" : getProgressText();
    }

    public float getProgressPercent() {
        return (float) getCount() / (float) getAllCount();
    }

    private String getProgressText() {
        String encodedUnit;
        try {
            encodedUnit = URLDecoder.decode(unit, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedUnit = unit;
        }
        Log.e(this.getClass().getSimpleName(), "Unit: " + encodedUnit);
        if (Define.MissionType.TIME_24H.equals(encodedUnit) || Define.MissionType.TIME_12H.equals(encodedUnit)) {
            long remain = getAllCount() - (getCount() == 0 ? 1 : getCount());
            String dateFormatted = HimecasUtils.millisecondToDayTime(remain);
            return String.format(Locale.getDefault(), Define.MissionTextFormat.TIME_FORMAT, dateFormatted);
        } else {
            String value = getCount() + " / " + getAllCount();
            return String.format(Locale.getDefault(), Define.MissionTextFormat.NUMBER_FORMAT, value);
        }
    }
}
