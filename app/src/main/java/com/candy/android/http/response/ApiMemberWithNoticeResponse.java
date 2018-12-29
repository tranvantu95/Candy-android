package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.model.MemberNoticeSetting;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 10/11/2016.
 */

public class ApiMemberWithNoticeResponse extends ApiMemberResponse {

    @SerializedName(Define.Fields.FIELD_NOTIFICATION)
    private MemberNoticeSetting mMemberNoticeSetting;
}
