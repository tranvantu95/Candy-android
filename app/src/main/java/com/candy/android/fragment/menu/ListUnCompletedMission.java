package com.candy.android.fragment.menu;

import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiUncompletedMissionResponse;
import com.candy.android.manager.SettingManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * List UnCompleted(not got point yet) mission
 * Created by brianhoang on 6/2/17.
 */

public class ListUnCompletedMission extends ListMissionFragment {

    public static ListUnCompletedMission getInstance() {
        return new ListUnCompletedMission();
    }

    @Override
    public void loadData() {
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

        Call<ApiUncompletedMissionResponse> call = apiService.getListUncompletedMission(mPage, SettingManager.getInstance(getActivity()).getID(),
                SettingManager.getInstance(getActivity()).getPassword());
        call.enqueue(unCompletedMissionResponseCallback);
    }

    private Callback<ApiUncompletedMissionResponse> unCompletedMissionResponseCallback = new Callback<ApiUncompletedMissionResponse>() {
        @Override
        public void onResponse(Call<ApiUncompletedMissionResponse> call, Response<ApiUncompletedMissionResponse> response) {
            if (response.isSuccessful()) {
                mTotalMissionCount = response.body().getMember().getMission().getRows();
                handleResponseData(response.body().getMember().getMission().getMission());
            } else {
                handleError();
            }
        }

        @Override
        public void onFailure(Call<ApiUncompletedMissionResponse> call, Throwable t) {
            handleError();
        }
    };
}
