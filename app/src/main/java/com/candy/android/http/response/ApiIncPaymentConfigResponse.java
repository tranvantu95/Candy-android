package com.candy.android.http.response;

import com.candy.android.model.PurchasePointOption;
import com.google.gson.Gson;

import java.util.List;

/**
 * @author Favo
 * Created on 09/12/2016.
 */

public class ApiIncPaymentConfigResponse {

    private List<PurchasePointOption> android;

    public List<PurchasePointOption> getProductList() {
        return android;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
