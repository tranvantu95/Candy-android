package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.model.BaseMember;
import com.google.gson.annotations.SerializedName;

/**
 * Created by namhv on 10/19/16.
 * Modify: quannt
 */

public class ApiFavoriteResponse extends BaseApiResponse {
    @SerializedName(Define.Fields.FIELD_MEMBER)
    private BaseMember mMember;

    public BaseMember getMember() {
        return mMember;
    }
}
