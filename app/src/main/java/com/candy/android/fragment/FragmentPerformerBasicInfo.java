package com.candy.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.configs.Define;
import com.candy.android.model.PerformerDetail;
import com.candy.android.model.PerformerOnline;

import java.util.Locale;

/**
 * Created by namhv on 03/11/2016.
 * Show basic info of performer
 */
public class FragmentPerformerBasicInfo extends BaseFragment implements View.OnClickListener {

    private PerformerOnline mCurrentPerformerOnline;
    private PerformerDetail mCurrentPerformerDetail;
    private TextView mArea, mJob, mFavorite, mMyType, mBlood, mTvInfrestedTime, mTall;

    public static FragmentPerformerBasicInfo newInstance() {
        return new FragmentPerformerBasicInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_performer_detail_basic_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayoutContents(view);
        clear();

        Activity activity = getActivity();
        if (activity != null && activity instanceof PerformerProfileActivity) {
            mCurrentPerformerOnline = ((PerformerProfileActivity) activity).getCurrentPerformerOnline();
            mCurrentPerformerDetail = ((PerformerProfileActivity) activity).getCurrentPerformerDetail();
            if (null != mCurrentPerformerDetail) {
                binData(mCurrentPerformerDetail);
            }
        }
    }

    private void initLayoutContents(View view) {
        mBlood = (TextView) view.findViewById(R.id.tv_blood);
        mJob = (TextView) view.findViewById(R.id.tv_job);
        mTall = (TextView) view.findViewById(R.id.tv_tall);
        mArea = (TextView) view.findViewById(R.id.tv_area);
        mMyType = (TextView) view.findViewById(R.id.tv_my_type);
        mFavorite = (TextView) view.findViewById(R.id.tv_favorite);
        mTvInfrestedTime = (TextView) view.findViewById(R.id.tv_infrested_time);
        view.findViewById(R.id.btn_more).setOnClickListener(this);
    }

    private void clear() {
        mJob.setText("");
        mTall.setText("");
        mFavorite.setText("");
        mMyType.setText("");
        mArea.setText("");
        mBlood.setText("");
        mTvInfrestedTime.setText("");
    }

    private void binData(PerformerDetail performerDetail) {

        try {
            if (performerDetail.getIsPublic() == PerformerDetail.NO_PUBLIC_INT) {
                clear();
            }
            else {
                mTall.setText(String.format(Locale.US, "%dcm", performerDetail.getTall()));
                mJob.setText(Html.fromHtml(performerDetail.getJob()));
                mFavorite.setText(Html.fromHtml(performerDetail.getFavorite()));
                mMyType.setText(Html.fromHtml(performerDetail.getMyType()));
                mArea.setText(performerDetail.getArea());
                mBlood.setText(performerDetail.getBlood());
                mTvInfrestedTime.setText(performerDetail.getInfestedTime());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_more:
                gotoBlockScreen();
                break;
        }
    }

    private void gotoBlockScreen() {
//        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.setAction(Define.IntentActions.ACTION_BLOCK);
//        intent.putExtra(Define.IntentExtras.PERFORMER_CODE, mCurrentPerformerOnline.getCode());
//        startActivity(intent);
//        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, ReportPerformerFragment.newInstance(
                        String.valueOf(mCurrentPerformerOnline.getCode())),
                        "ReportPerformerFragment")
                .addToBackStack(null)
                .commit();
    }

}
