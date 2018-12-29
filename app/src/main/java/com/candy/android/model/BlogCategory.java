package com.candy.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BlogCategory {

    @SerializedName("name")
    private String name;

    @SerializedName("parameter")
    private String parameter;

    private String jpName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getJpName() {
        if(jpName == null) {
            try {
                jpName = URLDecoder.decode(parameter, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                jpName = "";
            }
        }
        return jpName;
    }

}
