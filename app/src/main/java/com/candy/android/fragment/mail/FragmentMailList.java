package com.candy.android.fragment.mail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.adapter.MailAdapter;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMailResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.IMessage;
import com.candy.android.model.MailList;
import com.candy.android.model.MessageDetail;
import com.candy.android.model.eventBus.chat.WsSendEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Kenvin Favo
 * Created on 10/27/2016.
 */
public class FragmentMailList extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        MailAdapter.OnMemberMailClickedListener {

    private static final String TAG = "IDK-FragmentMailList";
    private static final String SCREEN_NAME = "メッセージ";

    private View mNoContent;
    private RecyclerView mMailListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private MailAdapter mAdapter;
    private List<IMessage> mMailList;

    private DialogBuilder.ConfirmDialog mNoticeDialog;
    private boolean mBusyLoading;

    public static FragmentMailList newInstance() {

        Bundle args = new Bundle();

        FragmentMailList fragment = new FragmentMailList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        postMemberMailListApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoContent = view.findViewById(R.id.no_content);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMailListView = (RecyclerView) view.findViewById(R.id.chat_messages_list);

        mMailListView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mMailListView.setLayoutManager(lm);

        mMailList = new ArrayList<>();
        mAdapter = new MailAdapter(getActivity(), mMailList);
        mAdapter.setListener(this);

        mMailListView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onRetryConnectionOk() {
        if (NetworkUtils.hasConnection(getActivity())) {
            postMemberMailListApi();
        } else {
            showRetryConnectionDialog();
        }
    }

    /**
     * This callback is fired when there is an incoming WsSendEvent from web socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveWebSocketMessage(final WsSendEvent event) {
        // if received message from same performer code
        if (event.getSenderCode() != Define.REQUEST_FAILED) {
            // reload list
            onRefresh();
        }
    }

    private void postMemberMailListApi() {
        if (checkConnectivity()) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        try {
            if (mBusyLoading)
                return;

            mBusyLoading = true;

            if (!SettingManager.getInstance(getActivity()).hasMember()) {
                return;
            }

            final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiMailResponse> call = apiService.getMemberMailList(
                    member.getId(),
                    member.getPass(),
                    Integer.MAX_VALUE,
                    0,
                    Define.SETTINGS.DEFAULT_SORT_FIELD,
                    Define.SETTINGS.DEFAULT_SORT_ORDER
            );

            mSwipeRefreshLayout.setRefreshing(true);

            // send call object
            call.enqueue(new Callback<ApiMailResponse>() {
                @Override
                public void onResponse(Call<ApiMailResponse> call, Response<ApiMailResponse> response) {

                    mBusyLoading = false;

                    if (response == null || response.body() == null || !isAdded())
                        return;

                    handleGetMemberMailListSuccess(response.body());
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ApiMailResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "getMemberMailList failed: ", t);
                    mBusyLoading = false;

                    handleGetMemberMailListFailure(call, t);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "Exception:", ex);
            mBusyLoading = false;

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void handleGetMemberMailListFailure(final Call<ApiMailResponse> call, Throwable throwable) {
        checkNoContent();
    }

    private void handleGetMemberMailListSuccess(final ApiMailResponse response) {
        RkLogger.d(TAG, "getMemberMailList return " + response.isSuccess());
        if (response.isSuccess()) {
            cookMailList(response.getMailList());

            // update toolbar ui
            HimecasUtils.postEventBus(getActivity(), response.getMember());
        }
    }

    private void cookMailList(final MailList mailList) {

        List<MessageDetail> mailDetailList = mailList.getMailDetailList();

        // if successfully fetch mail detail list, increase page number for next time
        // and add to mail list
        if (mailDetailList.size() > 0) {
            mMailList.clear();
            mMailList.addAll(mailDetailList);

            mAdapter.notifyDataSetChanged();
        }

        // control visibility
        checkNoContent();
    }

    private void checkNoContent() {
        if (mMailList.size() == 0) {
            // mail list is empty, show -no content- view
            mNoContent.setVisibility(View.VISIBLE);
            mMailListView.setVisibility(View.GONE);
        } else {
            // mail list has data, show items
            mNoContent.setVisibility(View.GONE);
            mMailListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        postMemberMailListApi();
    }

    @Override
    public void onMailClicked(int performerCode, String performerOriginName, int age, String imageUrl) {
        gotoFragmentMailLine(performerCode, performerOriginName, age, imageUrl);
    }

    @Override
    public void onDeleteMessageClick(MessageDetail messageDetail) {
        Helpers.showAlertDialog(getContext(), getString(R.string.title_dialog_delete_message), null,
                getString(R.string.btn_no_jp), getString(R.string.btn_yes_jp),
                true, null, null, null);
    }

    private void gotoFragmentMailLine(int performerCode, String performerOriginName, int age, String imageUrl) {
        FragmentMailContainer mailContainer = (FragmentMailContainer) getParentFragment();
        mailContainer.gotoFragment(FragmentMailLine.newInstance(performerCode, performerOriginName, age, imageUrl));
    }

    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }
}
