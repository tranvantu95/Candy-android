package com.candy.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.configs.Define;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PerformerFootprint;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.UpdateFavoritePerformerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Welby Dev on 12/8/17.
 */

public class FootprintPerformerAdapter extends RecyclerView.Adapter<FootprintPerformerAdapter.FootprintViewHolder> {
    private Context mContext;
    private List<PerformerFootprint> mListsFootprint;

    public FootprintPerformerAdapter(Context context, List<PerformerFootprint> mListsFootprint) {
        this.mContext = context;
        this.mListsFootprint = mListsFootprint;
    }

    @Override
    public FootprintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footprint_performer, parent, false);
        return new FootprintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FootprintViewHolder holder, int position) {
        PerformerFootprint performerFootprint = mListsFootprint.get(position);
        holder.bindData(performerFootprint);
    }

    @Override
    public int getItemCount() {
        return mListsFootprint.size();
    }

    public class FootprintViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvDate, tvArea;
        ImageButton ibtnFavorite;
        ImageView imgAvatar, imLiveAnimation;
        View imLiveBackground;

        public FootprintViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvDate = itemView.findViewById(R.id.tv_date);
            ibtnFavorite = itemView.findViewById(R.id.ibtn_favorite);
            imgAvatar = itemView.findViewById(R.id.iv_avatar);
            imLiveBackground = itemView.findViewById(R.id.live_status_footprint);
            imLiveAnimation = itemView.findViewById(R.id.footprint_live_status_image);
            tvArea = itemView.findViewById(R.id.tv_area);
        }

        void bindData(final PerformerFootprint performer) {
            // Status
            String stt = performer.getStatusString();
            int sttCode = performer.getStatus();
            if (1 <= sttCode && sttCode <= 2) {
                imLiveBackground.setVisibility(View.VISIBLE);
                imLiveAnimation.setVisibility(View.VISIBLE);
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imLiveAnimation);
                Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
            } else {
                imLiveBackground.setVisibility(View.GONE);
                imLiveAnimation.setVisibility(View.GONE);
            }
            // Message
            String name = performer.getName();
            if (!TextUtils.isEmpty(name)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvName.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvName.setText(Html.fromHtml(name));
                }
            } else {
                tvName.setText(null);
            }
            if (performer.getAreaString() != null) {
                tvArea.setText(performer.getAreaString());
                tvArea.setVisibility(View.VISIBLE);
            } else {
                tvArea.setVisibility(View.INVISIBLE);
            }
            if (performer.getPublicAge()) {
                tvAge.setText(String.format(mContext.getString(R.string.age), performer.getAge()));
                tvAge.setVisibility(View.VISIBLE);
            } else {
                tvAge.setVisibility(View.INVISIBLE);
            }

            String imageUrl = performer.getProfileImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(itemView.getContext())
                        .load(imageUrl).error(R.drawable._icons_noimage)
                        .into(imgAvatar);
            }
            if (performer.isFavorite()) {
                ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_active);
                ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_active);
            } else {
                ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_inactive);
                ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_inactive);
            }
            if (performer.getFootprintsTime() != null) {
                tvDate.setText(performer.getFootprintsTime());
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PerformerProfileActivity.class);
                    PerformerOnline performerOnline = new PerformerOnline();
                    //Only pass code and profileImageUrl
                    performerOnline.setCode(performer.getCode());
                    performerOnline.setProfileImageUrl(performer.getProfileImageUrl());
                    intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
                    intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
                    mContext.startActivity(intent);
                }
            });
            ibtnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer resource = (Integer) ibtnFavorite.getTag();
                    if (resource == R.drawable._atoms_favoritebtn_active) {
                        unFavorite(performer);
                    } else {
                        addFavorite(performer);
                    }
                }
            });
        }

        private void addFavorite(final PerformerFootprint performerFootprint) {
            String id = SettingManager.getInstance(mContext).getMemberInformation().getId();
            String password = SettingManager.getInstance(mContext).getMemberInformation().getPass();
            final int performerCode = performerFootprint.getCode();

            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
            Call<ApiFavoriteResponse> call = apiService.favoriteAdd(id, password, performerCode);
            call.enqueue(new Callback<ApiFavoriteResponse>() {
                @Override
                public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                    if (response == null || response.body() == null) {
                        return;
                    }
                    if (response.body().isSuccess()) {
                        Log.d("Favorite", "addFavorite");
                        ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_active);
                        ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_active);
                        EventBus.getDefault().post(new UpdateFavoritePerformerEvent(performerFootprint,
                                UpdateFavoritePerformerEvent.TYPE_ADD_FAVORITE));
                    }
                }

                @Override
                public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                    ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_inactive);
                    ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_inactive);
                }
            });
        }

        private void unFavorite(final PerformerFootprint performer) {
            int performerCode = performer.getCode();
            String id = SettingManager.getInstance(mContext).getMemberInformation().getId();
            String password = SettingManager.getInstance(mContext).getMemberInformation().getPass();
            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
            Call<ApiFavoriteResponse> call = apiService.favoriteDelete(id, password, performerCode);
            call.enqueue(new Callback<ApiFavoriteResponse>() {
                @Override
                public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                    if (response == null || response.body() == null)
                        return;
                    if (response.body().isSuccess()) {
                        Log.d("Favorite", "unFavorite");
                        ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_inactive);
                        ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_inactive);
                        EventBus.getDefault().post(new UpdateFavoritePerformerEvent(performer,
                                UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE));
                    }
                }

                @Override
                public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                    ibtnFavorite.setImageResource(R.drawable._atoms_favoritebtn_active);
                    ibtnFavorite.setTag(R.drawable._atoms_favoritebtn_active);
                }
            });
        }
    }
}
