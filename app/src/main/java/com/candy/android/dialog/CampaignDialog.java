package com.candy.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.custom.CustomFragmentPagerAdapter;
import com.candy.android.fragment.CampaignDetailFragment;
import com.candy.android.model.Campaign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quannt on 23/11/2016.
 * Des:
 */

public class CampaignDialog extends DialogFragment implements View.OnClickListener {

    private static final String EXTRAS_CAMPAIGNS_LIST = "campaigns_list";
    private static final String EXTRAS_CAMPAIGN_POSITION = "campaign_position";
    private static final String COMMING_TODAY = "today";
    private static final String COMMING_SOON = "soon";
    private static final String COMMING_CURRENT = "current";

    private TextView mTvEventStatus, mTvClose;

    private ViewPager mCampaignViewPager;
    private CustomFragmentPagerAdapter mCampaignPagerAdapter;
    private ArrayList<Campaign> mCampaigns;
    private List<Fragment> mListFragments;
    private List<String> mListTitles;
    private Context mContext;

    private int mPosition;

    public static CampaignDialog newInstance(ArrayList<Campaign> campaigns, int position) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRAS_CAMPAIGNS_LIST, campaigns);
        args.putInt(EXTRAS_CAMPAIGN_POSITION, position);
        CampaignDialog fragment = new CampaignDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCampaigns = args.getParcelableArrayList(EXTRAS_CAMPAIGNS_LIST);
            mPosition = args.getInt(EXTRAS_CAMPAIGN_POSITION);
        }
        mListFragments = new ArrayList<>();
        mListTitles = new ArrayList<>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            try {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getColor(R.color.bg_campaign_color)));
            } else {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.bg_campaign_color)));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_campaign, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        mTvEventStatus = (TextView) view.findViewById(R.id.tv_event_status);
        mTvClose = (TextView) view.findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(this);
        mCampaignViewPager = (ViewPager) view.findViewById(R.id.campaign_view_pager);

        if (mListFragments == null) {
            mListFragments = new ArrayList<>();
        } else if (mListFragments.size() > 0) {
            mListFragments.clear();
        }

        if (mListTitles == null) {
            mListTitles = new ArrayList<>();
        } else if (mListTitles.size() > 0) {
            mListTitles.clear();
        }

        for (int i = 0; i < mCampaigns.size(); i++) {
            Campaign campaign = mCampaigns.get(i);
            mListFragments.add(CampaignDetailFragment.newInstance(campaign));

            String coming = campaign.getComing();
            if (coming.equals(COMMING_CURRENT)) {
                mListTitles.add(getString(R.string.event_now));
            } else if (coming.equals(COMMING_SOON)) {
                mListTitles.add(getString(R.string.event_soon));
            } else {
                mListTitles.add(getString(R.string.event_today));
            }
        }
        mCampaignPagerAdapter = new CustomFragmentPagerAdapter(mListFragments, mListTitles, getChildFragmentManager());
        mCampaignViewPager.setAdapter(mCampaignPagerAdapter);
        mCampaignViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvEventStatus.setText(mCampaignPagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCampaignViewPager.setCurrentItem(mPosition);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                dismiss();
                break;
        }
    }
}
