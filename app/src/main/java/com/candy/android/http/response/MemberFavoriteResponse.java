package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.model.BaseMember;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 10/20/2016.
 * Description: Response for api: incMemberFavoriteDel.json and incMemberFavoriteAdd
 */

public class MemberFavoriteResponse extends BaseApiResponse {
    @SerializedName(Define.Fields.FIELD_MEMBER)
    private BaseMember member;
}
