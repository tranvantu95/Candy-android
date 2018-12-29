package com.candy.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.VideoCallActivity;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.utils.HimecasUtils;

import java.util.List;
import java.util.Locale;


/**
 * Adapting list purchase option
 * Created by namhv on 03/12/2016.
 */

public class VideoPurchasePointAdapter extends RecyclerView.Adapter<VideoPurchasePointAdapter.PurchaseOptionHolder> {

    private List<PurchasePointOption> mPurchasePointOptions;

    private VideoCallActivity mVideoCallActivity;

    public VideoPurchasePointAdapter(List<PurchasePointOption> purchasePointOptions, VideoCallActivity videoCallActivity) {
        mPurchasePointOptions = purchasePointOptions;
        mVideoCallActivity = videoCallActivity;
    }

    @Override
    public PurchaseOptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        PurchasePointOption.Type type = PurchasePointOption.Type.values()[viewType];
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_point_video, parent, false);
        return new PurchaseOptionHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseOptionHolder holder, int position) {
        final PurchasePointOption option = mPurchasePointOptions.get(position);
        holder.bindView(option);
        holder.mBuyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (option.getType() == PurchasePointOption.Type.PURCHASE) {
                    HimecasUtils.buildBuyPointIntent(option.getProductId(), mVideoCallActivity);
                } else if (option.getType() == PurchasePointOption.Type.FREE) {
                    HimecasUtils.routeToFreePointScreen(mVideoCallActivity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPurchasePointOptions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPurchasePointOptions.get(position).getType().ordinal();
    }

    class PurchaseOptionHolder extends RecyclerView.ViewHolder {

        TextView mBuyView, mPurchaseTitle, mPointNumber, mPointDescriptionView;

        PurchaseOptionHolder(View itemView) {
            super(itemView);
            mBuyView = (TextView) itemView.findViewById(R.id.tv_buy);
            mPurchaseTitle = (TextView) itemView.findViewById(R.id.tv_purchase_title);
            mPointNumber = (TextView) itemView.findViewById(R.id.tv_point_number);
            mPointDescriptionView = (TextView) itemView.findViewById(R.id.desc_pos_fix);
        }

        void bindView(PurchasePointOption option) {
            mPurchaseTitle.setText(option.getName());
            mPointDescriptionView.setText(option.getPosFix());
            mPointNumber.setText(String.format(Locale.JAPAN, itemView.getResources().getString(R.string.point_number), option.getPoint()));
            mBuyView.setText(String.format(Locale.JAPAN, itemView.getResources().getString(R.string.price), option.getPrice()));
        }
    }
}
