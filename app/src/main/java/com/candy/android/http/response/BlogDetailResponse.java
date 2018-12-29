package com.candy.android.http.response;

import com.candy.android.model.BaseMember;
import com.candy.android.model.PerformerBlogDetail;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 06/11/2016.
 * Des: Response for blog detail
 */

public class BlogDetailResponse extends BaseApiResponse {
    @SerializedName("member")
    private BaseMember member;
    @SerializedName("blog")
    private PerformerBlogDetail performerBlogDetail;

    public BaseMember getMember() {
        return member;
    }

    public PerformerBlogDetail getPerformerBlogDetail() {
        return performerBlogDetail;
    }
}
