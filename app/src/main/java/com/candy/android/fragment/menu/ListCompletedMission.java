package com.candy.android.fragment.menu;

import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiCompletedMissionResponse;
import com.candy.android.manager.SettingManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * List completed mission
 * Created by brianhoang on 6/2/17.
 */

public class ListCompletedMission extends ListMissionFragment {

    public static ListCompletedMission getInstance() {
        return new ListCompletedMission();
    }

    @Override
    public void loadData() {
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

        Call<ApiCompletedMissionResponse> call = apiService.getListCompletedMission(mPage, SettingManager.getInstance(getActivity()).getID(),
                SettingManager.getInstance(getActivity()).getPassword());
        call.enqueue(completedMissionResponseCallback);
    }

    private Callback<ApiCompletedMissionResponse> completedMissionResponseCallback = new Callback<ApiCompletedMissionResponse>() {
        @Override
        public void onResponse(Call<ApiCompletedMissionResponse> call, Response<ApiCompletedMissionResponse> response) {
            if (response.isSuccessful()) {
                mTotalMissionCount = response.body().getMember().getMission().getRows();
                handleResponseData(response.body().getMember().getMission().getMission());
            } else {
                handleError();
            }
        }

        @Override
        public void onFailure(Call<ApiCompletedMissionResponse> call, Throwable t) {
            handleError();
        }
    };
}
