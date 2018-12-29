package com.candy.android.http;

import com.candy.android.BuildConfig;
import com.candy.android.http.gsonconverter.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;

/**
 * @author Favo
 * Created on 18/10/2016.
 */

public class ApiClientManager {
    private static Retrofit retrofit = null;

    public static Retrofit createNewInstance(String baseUrl) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getApiClientManager() {
        if (retrofit == null) {
            //add pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
