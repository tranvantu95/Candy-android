package com.candy.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.custom.views.CircleImageView;
import com.candy.android.fragment.ranking.OnRankItemClickListener;
import com.candy.android.model.ranking.BaseRankTime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by Anhtriuhihi on 8/11/2017.
 */

public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER_VIEW_TYPE = 1;
    private static final int LIST_PERFORMER_TYPE = 2;
    private static final String ORDER = "位";
    private static final String AGE = "歳";
    private static final String STAR = "☆☆☆";
    private List<BaseRankTime> mRankDatas;
    private Context mContext;
    private OnRankItemClickListener mRankItemClickListener;
    private Integer tabPosition;
    private String prevDate, nextDate, datetime, currentTime;
    private DateSelectionListener dateSelectionListener;

    public RankingAdapter(Context context, List<BaseRankTime> rankDatas, OnRankItemClickListener rankItemClickListener,
                          int tabPosition, String prevDate, String nextDate, String datetime, String currentTime) {
        this.mRankDatas = rankDatas;
        this.mContext = context;
        this.mRankItemClickListener = rankItemClickListener;
        this.tabPosition = tabPosition;
        this.prevDate = prevDate;
        this.nextDate = nextDate;
        this.datetime = datetime;
        this.currentTime = currentTime;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LIST_PERFORMER_TYPE) {
            View rv = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performer_rank_order, parent, false);
            return new PerformerRankHolder(rv);
        } else {
            View rv = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_header_view, parent, false);
            return new RankHeaderViewHolder(rv, tabPosition);
        }
    }

    public void setData(List<BaseRankTime> list) {
        this.mRankDatas = list;
        notifyDataSetChanged();
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener) {
        this.dateSelectionListener = dateSelectionListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RankHeaderViewHolder) {
            processHeaderView((RankHeaderViewHolder) holder);
            RankHeaderViewHolder rankHeaderViewHolder = (RankHeaderViewHolder) holder;
            {
                BaseRankTime brt = mRankDatas.get(0);
                // load picture of gold perfomer
                Glide.with(mContext).load(brt.getProfileImageUrl()).dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable._icons_noimage)
                        .error(R.drawable._icons_noimage)
                        .into(rankHeaderViewHolder.civFirst);
                try {
                    String tmpName = URLDecoder.decode(brt.getOrignalName(), "UTF-8") + STAR;
                    rankHeaderViewHolder.tvFPName.setText(tmpName);

                    if (1 <= Integer.parseInt(brt.getStatus()) && Integer.parseInt(brt.getStatus()) <= 2) {
                        rankHeaderViewHolder.llLiveFirst.setVisibility(View.VISIBLE);
                        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(rankHeaderViewHolder.iconLiveFirst);
                        Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
                    } else {
                        ((RankHeaderViewHolder) holder).llLiveFirst.setVisibility(View.GONE);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onClickTopThree(((RankHeaderViewHolder) holder).civFirst, brt);
            }

            {
                BaseRankTime brt1 = mRankDatas.get(1);

                // load picture of silver perfomer
                Glide.with(mContext).load(brt1.getProfileImageUrl()).dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable._icons_noimage)
                        .error(R.drawable._icons_noimage)
                        .into(rankHeaderViewHolder.civSecond);
                try {
                    String tmpName = URLDecoder.decode(brt1.getOrignalName(), "UTF-8") + STAR;
                    rankHeaderViewHolder.tvSPName.setText(tmpName);

                    if (1 <= Integer.parseInt(brt1.getStatus()) && Integer.parseInt(brt1.getStatus()) <= 2) {
                        rankHeaderViewHolder.llLiveSecond.setVisibility(View.VISIBLE);
                        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(rankHeaderViewHolder.iconLiveSecond);
                        Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
                    } else {
                        rankHeaderViewHolder.llLiveSecond.setVisibility(View.GONE);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onClickTopThree(rankHeaderViewHolder.civSecond, brt1);
            }

            {
                BaseRankTime brt2 = mRankDatas.get(2);

                Glide.with(mContext).load(brt2.getProfileImageUrl()).dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable._icons_noimage)
                        .error(R.drawable._icons_noimage)
                        .into(rankHeaderViewHolder.civThirdRank);
                try {
                    String tmpName = URLDecoder.decode(brt2.getOrignalName(), "UTF-8") + STAR;
                    rankHeaderViewHolder.tvTPName.setText(tmpName);

                    if (1 <= Integer.parseInt(brt2.getStatus()) && Integer.parseInt(brt2.getStatus()) <= 2) {
                        rankHeaderViewHolder.llLiveThird.setVisibility(View.VISIBLE);
                        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(rankHeaderViewHolder.iconLiveThird);
                        Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
                    } else {
                        ((RankHeaderViewHolder) holder).llLiveThird.setVisibility(View.GONE);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onClickTopThree(rankHeaderViewHolder.civThirdRank, brt2);
            }
        } else {
            processPerformerView((PerformerRankHolder) holder, position);
        }

    }

    private void onClickTopThree(View v, final BaseRankTime brt) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRankItemClickListener.onOpenDetail(brt);
            }
        });
    }

    private void processHeaderView(RankHeaderViewHolder holder) {
        holder.tvDatetime1.setText(datetime);
        holder.tvDatetime2.setText(datetime);
        if (nextDate != null) {
            holder.tvNextDate.setVisibility(View.VISIBLE);
            holder.tvNextMonth.setVisibility(View.VISIBLE);
        } else {
            holder.tvNextDate.setVisibility(View.INVISIBLE);
            holder.tvNextMonth.setVisibility(View.INVISIBLE);
            holder.tvDatetime1.setText(currentTime);
            holder.tvDatetime2.setText(currentTime);
        }
        if (prevDate != null) {
            holder.tvPrevDate.setVisibility(View.VISIBLE);
            holder.tvPrevMonth.setVisibility(View.VISIBLE);
        } else {
            holder.tvPrevDate.setVisibility(View.INVISIBLE);
            holder.tvPrevMonth.setVisibility(View.INVISIBLE);
        }
    }

    private void processPerformerView(PerformerRankHolder holder, int position) {
        final int newPos = position + 2;
        final BaseRankTime btr = mRankDatas.get(newPos);
        String tmpOrder = String.valueOf(newPos + 1) + ORDER;
        holder.tvPerformerOrder.setText(tmpOrder);
        Glide.with(mContext).load(btr.getProfileImageUrl()).dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable._icons_noimage)
                .error(R.drawable._icons_noimage)
                .into(holder.civPerformerAvatar);

        try {
            holder.tvPerformerName.setText(URLDecoder.decode(btr.getOrignalName(), "UTF-8"));
            if (!btr.getAge().equals("")) {
                String tmpAge = String.valueOf(btr.getAge()) + AGE;
                holder.tvRankingAge.setVisibility(View.VISIBLE);
                holder.tvRankingAge.setText(tmpAge);
            } else {
                holder.tvRankingAge.setVisibility(View.GONE);
            }
            int sttCode = Integer.parseInt(btr.getStatus());
            if (1 <= sttCode && sttCode <= 2) {
                holder.imLiveBackground.setVisibility(View.VISIBLE);
                holder.imLiveAnimation.setVisibility(View.VISIBLE);
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.imLiveBackground);
                Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
            } else {
                holder.imLiveBackground.setVisibility(View.GONE);
                holder.imLiveAnimation.setVisibility(View.GONE);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRankItemClickListener.onOpenDetail(btr);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_VIEW_TYPE;
        else
            return LIST_PERFORMER_TYPE;
    }


    @Override
    public int getItemCount() {
        return null == mRankDatas ? 0 : mRankDatas.size() - 2;
    }

    public void setPrevDate(String prevDate) {
        this.prevDate = prevDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public class RankHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView civThirdRank, civSecond, civFirst;
        private ImageView imBackground, iconLiveFirst, iconLiveSecond, iconLiveThird;
        private View llLiveFirst, llLiveSecond, llLiveThird;
        private TextView tvFPName, tvSPName, tvTPName;
        private TextView tvPrevDate, tvNextDate, tvDatetime1,
                tvDatetime2, tvDatetime3, tvNextMonth, tvPrevMonth;
        private RelativeLayout rankingNavigation1, rankingNavigation2, rankingNavigation3;


        public RankHeaderViewHolder(View itemView, int tabPosition) {
            super(itemView);
            rankingNavigation1 = (RelativeLayout) itemView.findViewById(R.id.ranking_day_navigation_layout);
            rankingNavigation2 = (RelativeLayout) itemView.findViewById(R.id.ranking_month_navigation_layout);
            rankingNavigation3 = (RelativeLayout) itemView.findViewById(R.id.ranking_week_navigation_layout);
            tvPrevDate = (TextView) itemView.findViewById(R.id.prev_date);
            tvNextDate = (TextView) itemView.findViewById(R.id.next_date);
            tvDatetime1 = (TextView) itemView.findViewById(R.id.ranking_daily_datetime);
            tvPrevMonth = (TextView) itemView.findViewById(R.id.prev_month);
            tvNextMonth = (TextView) itemView.findViewById(R.id.next_month);
            tvDatetime2 = (TextView) itemView.findViewById(R.id.ranking_monthly_datetime);
            tvDatetime3 = (TextView) itemView.findViewById(R.id.ranking_newcomer_datetime);
            imBackground = (ImageView) itemView.findViewById(R.id.ranking_background_image);
            civFirst = (CircleImageView) itemView.findViewById(R.id.civ_first_rank);
            civSecond = (CircleImageView) itemView.findViewById(R.id.civ_second_rank);
            civThirdRank = (CircleImageView) itemView.findViewById(R.id.civ_third_rank);
            llLiveFirst = itemView.findViewById(R.id.ll_icon_live_first);
            llLiveSecond = itemView.findViewById(R.id.ll_icon_live_second);
            llLiveThird = itemView.findViewById(R.id.ll_icon_live_third);
            iconLiveFirst = (ImageView) itemView.findViewById(R.id.im_first_rank_performer_status);
            iconLiveSecond = (ImageView) itemView.findViewById(R.id.im_second_rank_performer_status);
            iconLiveThird = (ImageView) itemView.findViewById(R.id.im_third_rank_performer_status);
            tvFPName = (TextView) itemView.findViewById(R.id.tv_first_performer_name);
            tvSPName = (TextView) itemView.findViewById(R.id.tv_second_performer_name);
            tvTPName = (TextView) itemView.findViewById(R.id.tv_third_performer_name);
            switch (tabPosition) {
                case 0:
                    imBackground.setImageResource(R.drawable.ranking_yellow);
                    rankingNavigation1.setVisibility(View.VISIBLE);
                    tvPrevDate.setOnClickListener(this);
                    tvNextDate.setOnClickListener(this);
                    break;
                case 1:
                    imBackground.setImageResource(R.drawable.ranking_red);
                    rankingNavigation2.setVisibility(View.VISIBLE);
                    tvPrevMonth.setOnClickListener(this);
                    tvNextMonth.setOnClickListener(this);
                    break;
                case 2:
                    imBackground.setImageResource(R.drawable.ranking_green);
                    rankingNavigation3.setVisibility(View.VISIBLE);
                    tvDatetime3.setText(datetime);
                    break;
                default:
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.prev_date:
                    if (dateSelectionListener != null) {
                        dateSelectionListener.onPrevDateSelect(prevDate);
                    }
                    break;
                case R.id.next_date:
                    if (dateSelectionListener != null) {
                        dateSelectionListener.onNextDateSelect(nextDate);
                    }
                    break;
                case R.id.prev_month:
                    if (dateSelectionListener != null) {
                        dateSelectionListener.onPrevMonthSelect(prevDate);
                    }
                    break;
                case R.id.next_month:
                    if (dateSelectionListener != null) {
                        dateSelectionListener.onNextMonthSelect(nextDate);
                    }
            }
        }
    }

    public interface DateSelectionListener {
        void onPrevDateSelect(String prevDate);

        void onNextDateSelect(String nextDate);

        void onPrevMonthSelect(String prevDate);

        void onNextMonthSelect(String nextDate);
    }

    private class PerformerRankHolder extends RecyclerView.ViewHolder {
        private TextView tvPerformerOrder;
        private CircleImageView civPerformerAvatar;
        private TextView tvPerformerName;
        private TextView tvRankingAge;
        private ImageView imLiveBackground;
        private View imLiveAnimation;

        public PerformerRankHolder(View itemView) {
            super(itemView);
            tvPerformerOrder = (TextView) itemView.findViewById(R.id.tv_order);
            civPerformerAvatar = (CircleImageView) itemView.findViewById(R.id.civ_perfomer_in_rank);
            tvPerformerName = (TextView) itemView.findViewById(R.id.performer_name_in_rank);
            tvRankingAge = (TextView) itemView.findViewById(R.id.tv_ranking_age);
            imLiveBackground = (ImageView) itemView.findViewById(R.id.ranking_live_status_image);
            imLiveAnimation = itemView.findViewById(R.id.live_status_ranking);
        }
    }
}
