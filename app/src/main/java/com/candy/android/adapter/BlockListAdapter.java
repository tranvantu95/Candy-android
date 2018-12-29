package com.candy.android.adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.custom.swipelib.SwipeRevealLayout;
import com.candy.android.custom.swipelib.ViewBinderHelper;
import com.candy.android.model.PerformerFavorite;

import java.util.List;

/**
 * Created by namhv on 10/18/16.
 * Adapter for list favorite performers
 */

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.ViewHolder> {

    private List<PerformerFavorite> mListFavoritePerformers;
    private Callback callback;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public BlockListAdapter(List<PerformerFavorite> listFavoritePerformers, Callback callback) {
        mListFavoritePerformers = listFavoritePerformers;
        this.callback = callback;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_list, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
        if(holder.swipeRevealLayout.isOpened()) holder.swipeRevealLayout.close(true);

        final PerformerFavorite performer = mListFavoritePerformers.get(position);
        holder.bindData(performer);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewBinderHelper.isOpen()) viewBinderHelper.closeAll();
                else callback.onOpenDetail(performer);
            }
        });

        holder.mBtnRemoveUnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRemove(performer);
            }
        });

    }

    @Override
    public int getItemCount() {
        return null == mListFavoritePerformers ? 0 : mListFavoritePerformers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        SwipeRevealLayout swipeRevealLayout;
        View mainLayout;
        ImageView mIvAvatar;
        TextView mTvName, mTvAge;
        View mBtnRemoveUnFavorite;
        ImageView liveStatusImage;
        View liveStatusLayout;

        ViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_reveal_layout);
            mainLayout = itemView.findViewById(R.id.main_layout);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvAge = (TextView) itemView.findViewById(R.id.tv_age);
            mBtnRemoveUnFavorite = itemView.findViewById(R.id.btn_unblock);
            liveStatusImage = (ImageView) itemView.findViewById(R.id.live_status_image);
            liveStatusLayout = itemView.findViewById(R.id.live_status_layout);
        }

        void bindData(PerformerFavorite performer) {
            // Message
            String name = performer.getName();
            if (!TextUtils.isEmpty(name)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mTvName.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mTvName.setText(Html.fromHtml(name));
                }
            }

            mTvAge.setText(String.format(mTvAge.getResources().getString(R.string.age), performer.getAge()));

            String imageUrl = performer.getmProfileImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(mIvAvatar);
            }

            if(performer.isLive()){
                liveStatusLayout.setVisibility(View.VISIBLE);
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(liveStatusImage);
                Glide.with(liveStatusImage.getContext()).load(R.raw.ic_live).into(imageViewTarget);
            }
            else {
                liveStatusLayout.setVisibility(View.GONE);
            }
        }
    }

    public interface Callback {
        void onOpenDetail(PerformerFavorite performerFavorite);
        void onRemove(PerformerFavorite performerFavorite);
    }
}
