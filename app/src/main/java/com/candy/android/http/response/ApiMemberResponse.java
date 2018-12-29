package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.http.input.MemberInformation;
import com.google.gson.annotations.SerializedName;

/**
 * A response which contains a member field
 *
 * @author Favo
 * Created on 19/10/2016.
 */
public class ApiMemberResponse extends ApiGeneralResponse {

    @SerializedName(Define.Fields.FIELD_MEMBER)
    private MemberInformation mMember;

    public MemberInformation getMember() {
        return mMember;
    }
}
