package com.candy.android.http.response;

import android.text.TextUtils;

/**
 * Created by brianhoang on 6/6/17.
 */

public class ApiMemberMissionInfoResponse extends BaseApiResponse {
    private Member member;

    public Member getMember() {
        return member;
    }

    public class Member {
        private String point;

        private String joinDate;

        private Mission mission;

        public int getPoint() {
            return !TextUtils.isEmpty(point) && TextUtils.isDigitsOnly(point) ? Integer.valueOf(point) : -1;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public Mission getMission() {
            return mission;
        }
    }

    public class Mission {
        int remain;

        public int getRemain() {
            return remain;
        }
    }
}
