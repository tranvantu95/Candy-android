package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.model.BaseMember;
import com.candy.android.model.PerformerDetail;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 10/20/2016.
 * Description: response for performerprofile
 */

public class PerformerProfilesResponse extends BaseApiResponse {
    @SerializedName(Define.Fields.FIELD_MEMBER)
    private BaseMember member;
    @SerializedName(Define.Fields.FIELD_PERFORMER)
    private PerformerDetail performer;

    public BaseMember getMember() {
        return member;
    }

    public PerformerDetail getPerformer() {
        return performer;
    }
}
