package com.candy.android.http.response;

import android.text.TextUtils;

/**
 * Created by brianhoang on 6/2/17.
 */

public class ApiRewardMissionResponse extends BaseApiResponse {
    private Member member;

    public Member getMember() {
        return member;
    }

    public class Member {
        private String point;
        private Mission mission;

        public int getPoint() {
            return !TextUtils.isEmpty(point) && TextUtils.isDigitsOnly(point) ? Integer.valueOf(point) : -1;
        }

        public Mission getMission() {
            return mission;
        }

        public class Mission {
            int point;

            public int getPoint() {
                return point;
            }
        }
    }
}
