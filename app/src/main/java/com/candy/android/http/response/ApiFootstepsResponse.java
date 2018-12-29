package com.candy.android.http.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Welby Dev on 12/27/17.
 */

public class ApiFootstepsResponse extends BaseApiResponse {
    @SerializedName("isSuccess")
    @Expose
    private boolean isSuccess;
    @SerializedName("performers")
    @Expose
    private List<ApiPerformerFootsteps> performers = null;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public List<ApiPerformerFootsteps> getPerformers() {
        return performers;
    }
}
