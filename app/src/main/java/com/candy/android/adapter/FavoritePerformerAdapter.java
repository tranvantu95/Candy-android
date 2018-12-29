package com.candy.android.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.ClickHandler;
import com.candy.android.custom.callbacks.OnFavoritePerformerClickListener;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.custom.swipelib.SwipeRevealLayout;
import com.candy.android.custom.swipelib.ViewBinderHelper;
import com.candy.android.utils.RkLogger;

import java.util.List;

/**
 * Created by namhv on 10/18/16.
 * Adapter for list favorite performers
 */

public class FavoritePerformerAdapter extends RecyclerView.Adapter<FavoritePerformerAdapter.FavoritePerformerViewHolder> {
    private static final String AGE = "歳";
    private List<PerformerFavorite> mListFavoritePerformers;
    private OnFavoritePerformerClickListener mPerformerClickListener;
    private Context mContext;
    private ClickHandler mClickHandler;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public FavoritePerformerAdapter(Context context, List<PerformerFavorite> listFavoritePerformers, OnFavoritePerformerClickListener performerClickListener) {
        mListFavoritePerformers = listFavoritePerformers;
        mPerformerClickListener = performerClickListener;
        mContext = context;
        viewBinderHelper.setOpenOnlyOne(true);
        mClickHandler = new ClickHandler(1000);
    }

    @Override
    public FavoritePerformerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_performer, parent, false);
        return new FavoritePerformerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final FavoritePerformerViewHolder holder, int position) {
//        final PerformerFavorite performer = mListFavoritePerformers.get(position);

        if (mListFavoritePerformers != null && 0 <= position && position < mListFavoritePerformers.size()) {
            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
            if(holder.swipeRevealLayout.isOpened()) holder.swipeRevealLayout.close(true);

            final PerformerFavorite performer = mListFavoritePerformers.get(position);
            holder.bindData(performer);

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mClickHandler.isClickable(v.getId())) {
                        return;
                    }
                    if(viewBinderHelper.isOpen()) {
                        viewBinderHelper.closeAll();
                        return;
                    }
                }
            });

            holder.mIvAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mClickHandler.isClickable(v.getId())) {
                        return;
                    }
                    mPerformerClickListener.onOpenDetail(performer);
                }
            });

            holder.mIvRemoveFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mClickHandler.isClickable(v.getId())) {
                        return;
                    }
                    mPerformerClickListener.onRemove(performer);
//                    holder.swipeRevealLayout.close(true);
                }
            });

            holder.mBtnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mClickHandler.isClickable(v.getId())) {
                        return;
                    }
                    mPerformerClickListener.onChat(performer);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mListFavoritePerformers ? 0 : mListFavoritePerformers.size();
    }

    class FavoritePerformerViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvAvatar;
        TextView mIvRemoveFavorite;
        TextView mTvStatus;
        TextView mTvName;
        TextView mTvAge;
        View mBtnChat;
        ImageView imLiveAnimation;
        View liveStatus;
        SwipeRevealLayout swipeRevealLayout;
        View mainLayout;

        FavoritePerformerViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mIvRemoveFavorite = (TextView) itemView.findViewById(R.id.iv_remove_favorite);
            //mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            mTvAge = (TextView) itemView.findViewById(R.id.tv_age);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mBtnChat = itemView.findViewById(R.id.btn_chat);
            liveStatus = itemView.findViewById(R.id.live_status_favorite);
            imLiveAnimation = (ImageView) itemView.findViewById(R.id.favorite_live_status_image);
            swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById((R.id.swipe_reveal_layout));
            mainLayout = itemView.findViewById(R.id.main_layout);
        }

        void bindData(PerformerFavorite performer) {
            // Status
            try {
                int stt = performer.getStatus();
                if (1 <= stt && stt <= 2) {
                    liveStatus.setVisibility(View.VISIBLE);
                    imLiveAnimation.setVisibility(View.VISIBLE);
                    GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imLiveAnimation);
                    Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
                } else {
                    liveStatus.setVisibility(View.GONE);
                    imLiveAnimation.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Message
            String name = performer.getName();
            if (!TextUtils.isEmpty(name)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mTvName.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mTvName.setText(Html.fromHtml(name));
                }
            } else {
                mTvName.setText("");
            }

            String imageUrl = performer.getmProfileImageUrl();
            RkLogger.d("favorite image",imageUrl);
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .override(Define.SMALL_AVATAR_SIZE, Define.SMALL_AVATAR_SIZE)
                        .into(mIvAvatar);
            }

            String age = String.valueOf(performer.getAge());
            if (!TextUtils.isEmpty(age)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String tmp = Html.fromHtml(age, Html.FROM_HTML_MODE_COMPACT) + AGE;
                    mTvAge.setText(tmp);
                } else {
                    String tmp = Html.fromHtml(age)+ AGE;
                    mTvAge.setText(tmp);
                }
            } else {
                mTvAge.setText("");
            }
        }
    }
}
