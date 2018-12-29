package com.candy.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.dialog.DialogPointMissing;
import com.candy.android.model.PurchasePointOption;

import java.util.List;
import java.util.Locale;

/**
 * Adapting list purchase option
 * Created by namhv on 03/12/2016.
 */

public class PurchasePointAdapter extends RecyclerView.Adapter<PurchasePointAdapter.PurchaseOptionHolder> {

    private List<PurchasePointOption> mPurchasePointOptions;
    private DialogPointMissing.OnPurchaseOptionClickListener mOptionClickListener;


    public PurchasePointAdapter(List<PurchasePointOption> purchasePointOptions, DialogPointMissing.OnPurchaseOptionClickListener listener) {
        mPurchasePointOptions = purchasePointOptions;
        mOptionClickListener = listener;
    }

    @Override
    public PurchaseOptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PurchasePointOption.Type type = PurchasePointOption.Type.values()[viewType];
        switch (type) {
            case FREE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_get_free_point, parent, false);
                return new PurchaseOptionHolder(view);
            }
            case PURCHASE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_point, parent, false);
                return new PurchaseOptionHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(PurchaseOptionHolder holder, int position) {
        final PurchasePointOption option = mPurchasePointOptions.get(position);
        holder.bindView(option);
        holder.mBuyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOptionClickListener) {
                    mOptionClickListener.onOptionClick(option);
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
        TextView mPointDescPosFixView;
        TextView mPointDescriptionView;
        TextView mPointValueView;
        Button mBuyView;

        PurchaseOptionHolder(View itemView) {
            super(itemView);
            mPointDescriptionView = (TextView) itemView.findViewById(R.id.tv_point_description);
            mPointDescPosFixView = (TextView) itemView.findViewById(R.id.desc_pos_fix);
            mPointValueView = (TextView) itemView.findViewById(R.id.tv_point_value);
            mBuyView = (Button) itemView.findViewById(R.id.btn_add_point);
        }

        void bindView(PurchasePointOption option) {
            if (PurchasePointOption.Type.FREE != option.getType()) {
                mPointDescriptionView.setText(option.getName());
                mPointDescPosFixView.setText(option.getPosFix());
                mPointValueView.setText(String.format(Locale.JAPAN, itemView.getResources().getString(R.string.dialog_point_missing_n_pts), option.getPoint()));
                mBuyView.setText(String.format(Locale.JAPAN, itemView.getResources().getString(R.string.btn_buy), option.getPrice()));
            }
        }
    }
}
