package com.candy.android.http.response;

import com.google.gson.annotations.SerializedName;
import com.candy.android.configs.Define;
import com.candy.android.model.BasePerformer;

import java.util.ArrayList;


/**
 * Created by anhld on 8/24/2017.
 */

public class ApiMemberFavoriteListResponse {
    @SerializedName(Define.Fields.FIELD_ISSUCCES)
    private boolean isSuccess;
    @SerializedName(Define.Fields.FIELD_IS_MEMBER_CONTENTS_FULL_MODE)
    private boolean isMemberContentsFullMode;
    @SerializedName(Define.Fields.FIELD_PERFORMERS)
    private ArrayList<BasePerformer> performers;

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isMemberContentsFullMode() {
        return isMemberContentsFullMode;
    }

    public ArrayList<BasePerformer> getPerformers() {
        return performers;
    }
}
