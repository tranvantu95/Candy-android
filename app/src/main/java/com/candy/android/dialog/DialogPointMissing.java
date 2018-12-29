package com.candy.android.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.adapter.PurchasePointAdapter;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.ItemDecoration;
import com.candy.android.manager.InAppBillingManager;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.utils.HimecasUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Favo
 * Created on 09/11/2016.
 */

public class DialogPointMissing extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "IDK-PointMissing";
    public static final String ARG_OPTIONS = "Options";
    public static final int TYPE_VIDEO_CHAT = 0x1;
    public static final int TYPE_MESSAGE = 0x2;
    public static final int TYPE_MESSAGE_IMAGE_ATTACH = 0x3;

    private ArrayList<PurchasePointOption> mPurchasePointOptions;
    private OnPurchaseOptionClickListener mPurchaseOptionClickListener;
    private PurchasePointAdapter mPurchasePointAdapter;
    private int mFunctionType;

    public static DialogPointMissing newInstance(ArrayList<PurchasePointOption> options, int functionType) {
        DialogPointMissing dialog = new DialogPointMissing();

        // knv added
        dialog.mFunctionType = functionType;

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_OPTIONS, options);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View inflated = View.inflate(getContext(), R.layout.dialog_point_missing, null);
        builder.setView(inflated);
        if (getArguments() != null) {
            mPurchasePointOptions = getArguments().getParcelableArrayList(ARG_OPTIONS);
        }

        final AlertDialog dialog = builder.create();
        initDialogCreated(inflated);

        if(dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;

    }

    private void initDialogCreated(View view) {
        TextView itemView = (TextView) view.findViewById(R.id.tv_will_not_buy);
        itemView.setOnClickListener(this);

        // knv rem this
        if (null == mPurchasePointOptions) {
            mPurchasePointOptions = new ArrayList<>();
        }

        final RecyclerView rcOptions = (RecyclerView) view.findViewById(R.id.rc_list_purchase_option);
        rcOptions.addItemDecoration(new ItemDecoration(getContext(), ItemDecoration.VERTICAL_LIST));

        // knv added
        InAppBillingManager.getInstance().doRequestProductList(new InAppBillingManager.OnProductListLoadedListener() {
            @Override
            public void onProductListLoaded(boolean productListLoaded) {
                List<PurchasePointOption> productList = InAppBillingManager.getInstance().getProductList();

                if (productListLoaded && productList != null) {
                    mPurchasePointOptions.clear();

                    // add product list loaded from in-app manager to display list, reassign product's name
                    String label = null;
                    for (PurchasePointOption option : productList) {
                        if (HimecasUtils.canAppearInList(mFunctionType, option.getProductId())) {
                            if (mFunctionType == TYPE_MESSAGE) {
                                label = getString(R.string.dialog_point_missing_buy_1, option.getPoint() / Define.SETTINGS.MESSAGE_POINT_PAYMENT_COST);
                                option.setPosFix(getString(R.string.dialog_point_missing_buy_2));
                                option.setType(PurchasePointOption.Type.PURCHASE);
                            }else if (mFunctionType == TYPE_MESSAGE_IMAGE_ATTACH) {
                                label = getString(R.string.dialog_point_missing_buy_1, option.getPoint() / Define.SETTINGS.MESSAGE_WITH_IMAGE_POINT_PAYMENT_COST);
                                option.setPosFix(getString(R.string.dialog_point_missing_buy_2));
                                option.setType(PurchasePointOption.Type.PURCHASE);
                            } else if (mFunctionType == TYPE_VIDEO_CHAT) {
                                label = getString(R.string.dialog_point_missing_buy_3, option.getMinute());
                                option.setPosFix(getString(R.string.dialog_point_missing_buy_4));
                                option.setType(PurchasePointOption.Type.PURCHASE);
                            }

                            option.setName(label);
                            mPurchasePointOptions.add(0, option);
                        }
                    }

                    // Task #20939 Remove Get Free Point item in dialog get point
                    if (!SettingManager.getInstance(getActivity()).getKeyGetFreePoint()) {
                        // add free
                        mPurchasePointOptions.add(0,
                                new PurchasePointOption(PurchasePointOption.Type.FREE,
                                        getString(R.string.dialog_point_missing_get_for_free_1),
                                        1000, 0));
                    }
                }

                mPurchasePointAdapter = new PurchasePointAdapter(mPurchasePointOptions, mPurchaseOptionClickListener);
                rcOptions.setAdapter(mPurchasePointAdapter);
            }
        });
    }

    public void setPurchaseOptionClickListener(OnPurchaseOptionClickListener purchaseOptionClickListener) {
        this.mPurchaseOptionClickListener = purchaseOptionClickListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_will_not_buy:
                dismiss();
                break;
        }
    }

    public interface OnPurchaseOptionClickListener {
        void onOptionClick(PurchasePointOption option);
    }
}
