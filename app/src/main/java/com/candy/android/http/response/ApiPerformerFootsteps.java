package com.candy.android.http.response;

import com.candy.android.model.PerformerFootprint;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Welby Dev on 12/27/17.
 */

public class ApiPerformerFootsteps {
    @SerializedName("isSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("performer")
    @Expose
    private PerformerFootprint performer;

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public PerformerFootprint getPerformer() {
        return performer;
    }

    public void setPerformer(PerformerFootprint performer) {
        this.performer = performer;
    }

}
