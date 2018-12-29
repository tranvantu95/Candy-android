package com.candy.android.http.response;

import com.candy.android.model.BaseMember;
import com.candy.android.model.PerformerBlog;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 03/11/2016.
 * Des: response on blogs list
 */

public class BlogsListResponse extends BaseApiResponse {
    @SerializedName("member")
    private BaseMember member;
    @SerializedName("blog")
    private PerformerBlog performerBlog;

    public BaseMember getMember() {
        return member;
    }

    public PerformerBlog getPerformerBlog() {
        return performerBlog;
    }
}
