package com.candy.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.configs.Define;
import com.candy.android.custom.ClickHandler;
import com.candy.android.custom.views.DotIndicator;
import com.candy.android.model.Campaign;
import com.candy.android.model.PerformerOnline;
import com.candy.android.utils.RkLogger;

import java.util.ArrayList;

/**
 * Created by quannt on 10/14/2016.
 * <p>
 * Description: Adapter for PerformerOnlineList
 */

public class PerformerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int TIME_MOVE_CAMPAIGN = 2000;

    private static final String FAVORITE_REGISTED = "1";

    private Context mContext;
    private ArrayList<PerformerOnline> mPerformerOnlines;
    private ArrayList<Campaign> mCampaigns;
    private CampaignPagerAdapter.OnClickCampaignListener mOnClickCampaignListener;
    private int mCampaignPosition;
    private Handler mHandler;

    private boolean isNoHeader = false;
    private boolean isTablet;
    private ClickHandler mClickHandler;

    public PerformerAdapter(Context context, ArrayList<PerformerOnline> performerOnlines) {
        mContext = context;
        mPerformerOnlines = performerOnlines;
        mHandler = new Handler();
        isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
        mClickHandler = new ClickHandler(1000);
    }

    public static class PerformerHolder extends RecyclerView.ViewHolder {
        TextView shortMessage, name, area, age;
        ImageView girlImage;
        ImageView liveStatusImage;
        View liveStatusLayout;

        public PerformerHolder(View view) {
            super(view);
            shortMessage = (TextView) view.findViewById(R.id.tv_short_message);
            name = (TextView) view.findViewById(R.id.tv_name);
            area = (TextView) view.findViewById(R.id.tv_area);
            age = (TextView) view.findViewById(R.id.tv_age);
            girlImage = (ImageView) view.findViewById(R.id.iv_girl_image);
            liveStatusImage = (ImageView) view.findViewById(R.id.live_status_image);
            liveStatusLayout = view.findViewById(R.id.live_status_layout);
        }
    }

    static class CampaignHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        DotIndicator mDotIndicator;

        public CampaignHolder(View view) {
            super(view);
            viewPager = (ViewPager) view.findViewById(R.id.campaign_view_pager);
            mDotIndicator = (DotIndicator) view.findViewById(R.id.view_pager_indicator);
        }
    }

    static class TopBanner extends RecyclerView.ViewHolder {
        public TopBanner(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getContext() instanceof MainActivity)
                        ((MainActivity) v.getContext()).openMissionScreen();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_header, parent, false);
//            return new CampaignHolder(itemView);
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_banner, parent, false);
            return new TopBanner(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performer_view, parent, false);
            return new PerformerHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PerformerHolder) {
            PerformerHolder performerHolder = (PerformerHolder) holder;

            if (!isNoHeader) {
                position--;
            }

            final int realPosition = position;
            PerformerOnline performerOnline = mPerformerOnlines.get(realPosition);

            //add marginLeft
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (isTablet) {
                switch (position % 3) {
                    case 0:
                        layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.small_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding));
                        break;
                    case 1:
                        layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding));
                        break;
                    case 2:
                        layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                                (int) mContext.getResources().getDimension(R.dimen.small_padding),
                                (int) mContext.getResources().getDimension(R.dimen.tiny_padding));
                        break;
                }
            } else {
                if (position % 2 == 0) {
                    layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.small_padding),
                            (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                            (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                            (int) mContext.getResources().getDimension(R.dimen.tiny_padding));
                } else {
                    layoutParams.setMargins((int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                            (int) mContext.getResources().getDimension(R.dimen.tiny_padding),
                            (int) mContext.getResources().getDimension(R.dimen.small_padding),
                            (int) mContext.getResources().getDimension(R.dimen.tiny_padding));
                }
            }

            holder.itemView.setLayoutParams(layoutParams);
            final int finalPosition = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mClickHandler.isClickable(view.getId())) {
                        return;
                    }
//                    if (finalPosition == 0) { // #random_chat
//                        mOnClickItemView.onClickItemView(finalPosition);
//                    } else
                    {
                        Intent intent = new Intent(mContext, PerformerProfileActivity.class);
//                intent.putParcelableArrayListExtra(Define.IntentExtras.LIST_PERFORMERS, mPerformerOnlines);
                        intent.putExtra(Define.IntentExtras.POSITION, realPosition);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });
            RkLogger.d("logindate",performerOnline.isLive()+"---"+performerOnline.getStatus()+"---" + performerOnline.getLastLoginDate());
            if(performerOnline.isLive()){
                RkLogger.d("isLive","" + performerOnline.getName() + " - " + performerOnline.getStatus());
                performerHolder.liveStatusLayout.setVisibility(View.VISIBLE);
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(performerHolder.liveStatusImage);
                Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
            } else {
                performerHolder.liveStatusLayout.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(performerOnline.getCharacter())) {
                performerHolder.shortMessage.setVisibility(View.INVISIBLE);
            } else {
                performerHolder.shortMessage.setText(Html.fromHtml(performerOnline.getCharacter()));
                performerHolder.shortMessage.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(performerOnline.getName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    performerHolder.name.setText(Html.fromHtml(performerOnline.getName(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    performerHolder.name.setText(Html.fromHtml(performerOnline.getName()));
                }
            } else {
                performerHolder.name.setText("");
            }
            if (!TextUtils.isEmpty(performerOnline.getArea())) {
                performerHolder.area.setText(performerOnline.getArea());
            } else {
                performerHolder.area.setText("");
            }
            if (mPerformerOnlines.get(position).isPublicAge()) {
                performerHolder.age.setVisibility(View.VISIBLE);
                performerHolder.area.setVisibility(View.VISIBLE);
                performerHolder.age.setText(String.format(mContext.getResources().getString(R.string.age), performerOnline.getAge()));
            } else {
                performerHolder.age.setVisibility(View.INVISIBLE);
            }

//            if (position == 0) { // #random_chat
//                //item performer random
//                performerHolder.shortMessage.setText(mContext.getString(R.string.dialog72_text_message));
//                performerHolder.name.setText(mContext.getString(R.string.name_random_video_call));
//                performerHolder.age.setVisibility(View.INVISIBLE);
//                performerHolder.girlImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_random_call));
//                performerHolder.area.setVisibility(View.INVISIBLE);
//            } else
            {
//                RequestOptions requestOptions = new RequestOptions();
//                requestOptions = requestOptions.transform(new MyTransformation()).sizeMultiplier(0.5f);
                RkLogger.d("performer-image",performerOnline.getProfileImageUrl()+"");
                Uri uri = Uri.parse(performerOnline.getProfileImageUrl());
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .override(320,320)
                        .into(performerHolder.girlImage);
            }
        }
        else if (holder instanceof CampaignHolder) {
            final CampaignHolder campaignHolder = (CampaignHolder) holder;
            CampaignPagerAdapter campaignPagerAdapter = new CampaignPagerAdapter(mCampaigns, mOnClickCampaignListener);
            campaignHolder.viewPager.setAdapter(campaignPagerAdapter);
            campaignHolder.viewPager.setOffscreenPageLimit(2);
            campaignHolder.viewPager.setCurrentItem(mCampaignPosition);
            campaignHolder.mDotIndicator.setCurrentPage(mCampaignPosition);
            autoNextCampaign(campaignHolder.viewPager);
            campaignHolder.mDotIndicator.setViewPager(campaignHolder.viewPager);
            campaignHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(final int position) {
                    mCampaignPosition = position;
                    autoNextCampaign(campaignHolder.viewPager);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
//        else if (holder instanceof TopBanner) {
//        }
    }

    private void autoNextCampaign(final ViewPager viewPager) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewPager == null)
                    return;
                if (mCampaignPosition == mCampaigns.size() - 1) {
                    viewPager.setCurrentItem(0, true);
                } else {
                    viewPager.setCurrentItem(mCampaignPosition + 1, true);
                }
            }
        }, TIME_MOVE_CAMPAIGN);
    }

    @Override
    public int getItemCount() {
        return isNoHeader ? mPerformerOnlines.size() : mPerformerOnlines.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) && !isNoHeader ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public void setNoHeader(boolean noHeader) {
//        isNoHeader = noHeader;
    }

    public boolean isNoHeader() {
        return isNoHeader;
    }

    public void setOnClickCampaignListener(CampaignPagerAdapter.OnClickCampaignListener onClickCampaignListener) {
        mOnClickCampaignListener = onClickCampaignListener;
    }

    public void setmCampaigns(ArrayList<Campaign> mCampaigns) {
        this.mCampaigns = mCampaigns;
    }

    public void setmCampaignPosition(int mCampaignPosition) {
        this.mCampaignPosition = mCampaignPosition;
    }

    public interface OnClickItemView {
        void onClickItemView(int position);
    }
}
