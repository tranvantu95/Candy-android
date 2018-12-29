package com.candy.android.http.response;

import com.candy.android.model.UncompletedMission;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by brianhoang on 6/2/17.
 */

public class ApiUncompletedMissionResponse extends BaseApiResponse {
    private Member member;

    public Member getMember() {
        return member;
    }

    public class Member {
        private MissionData mission;

        public MissionData getMission() {
            return mission;
        }
    }

    public class MissionData {
        @SerializedName("list")
        private List<UncompletedMission> mission;
        private int rows;

        public List<UncompletedMission> getMission() {
            return mission;
        }

        public int getRows() {
            return rows;
        }
    }
}
