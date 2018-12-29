package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.candy.android.model.MailList;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 21/10/2016.
 */

public class ApiMailResponse extends ApiMemberResponse {

    @SerializedName(Define.Fields.FIELD_MAIL)
    private MailList mMailList;

    public MailList getMailList() {
        return mMailList;
    }
}
