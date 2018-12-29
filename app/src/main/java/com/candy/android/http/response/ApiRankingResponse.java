package com.candy.android.http.response;

import com.candy.android.model.ranking.Member;
import com.google.gson.annotations.SerializedName;
import com.candy.android.configs.Define;

/**
 * Created by Administrator on 8/29/2017.
 */

public class ApiRankingResponse extends ApiGeneralResponse {
    @SerializedName(Define.Fields.FIELD_MEMBER)
    private Member member;

    public Member getMember() {
        return member;
    }

}
