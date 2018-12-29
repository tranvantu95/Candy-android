package com.candy.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.VideoCallActivity;
import com.candy.android.adapter.VideoPurchasePointAdapter;
import com.candy.android.manager.InAppBillingManager;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.utils.HimecasUtils;

import java.util.ArrayList;
import java.util.List;

import jp.fmaru.app.livechatapp.ChatActivity;

public class VideoPurchasePointFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = VideoPurchasePointFragment.class.getSimpleName();
    private static final String EXTRAS_LIST_PURCHASE = "list_purchase";
    private static final String EXTRAS_FUNCTION_TYPE = "function_type";
    private static final String EXTRAS_ENOUGH = "enough";

    private Context mContext;
    private VideoCallActivity mCandyChatActivity;
    private RecyclerView mRvPurchaseList;
    private TextView mTvCancel;
    private ArrayList<PurchasePointOption> mPurchasePointOptions;
    private VideoPurchasePointAdapter mPurchasePointAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mFunctionType;
    private TextView tvNotEnoughPoint;
    private boolean mEnough;

    public VideoPurchasePointFragment() {
        // Required empty public constructor
    }


    public static VideoPurchasePointFragment newInstance(ArrayList<PurchasePointOption> purchasePointOptions, int functionType, boolean isEnoughPoint) {
        VideoPurchasePointFragment fragment = new VideoPurchasePointFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRAS_LIST_PURCHASE, purchasePointOptions);
        args.putInt(EXTRAS_FUNCTION_TYPE, functionType);
        args.putBoolean(EXTRAS_ENOUGH, isEnoughPoint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPurchasePointOptions = args.getParcelableArrayList(EXTRAS_LIST_PURCHASE);
            mFunctionType = args.getInt(EXTRAS_FUNCTION_TYPE);
            mEnough = args.getBoolean(EXTRAS_ENOUGH);
        }
        if (mPurchasePointOptions == null) {
            mPurchasePointOptions = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchase_point, container, false);
        initView(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mContext instanceof ChatActivity) {
            mCandyChatActivity = (VideoCallActivity) mContext;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initView(View view) {
        mRvPurchaseList = (RecyclerView) view.findViewById(R.id.purchase_list);
        mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mRvPurchaseList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRvPurchaseList.setLayoutManager(mLayoutManager);
//        RecycleItemDecoration dividerItemDecoration = new RecycleItemDecoration((int) getResources().getDimension(R.dimen.min_padding));
//        mRvPurchaseList.addItemDecoration(dividerItemDecoration);
        tvNotEnoughPoint = (TextView) view.findViewById(R.id.not_enough_point);
        tvNotEnoughPoint.setVisibility(!mEnough ? View.VISIBLE : View.GONE);

        InAppBillingManager.getInstance().doRequestProductList(new InAppBillingManager.OnProductListLoadedListener() {
            @Override
            public void onProductListLoaded(boolean productListLoaded) {
                List<PurchasePointOption> productList = InAppBillingManager.getInstance().getProductList();

                if (productListLoaded && productList != null) {
                    mPurchasePointOptions.clear();

                    // add product list loaded from in-app manager to display list, reassign product's name
                    for (PurchasePointOption option : productList) {
                        if (HimecasUtils.canAppearInList(mFunctionType, option.getProductId())) {
                            option.setName(getString(R.string.purchase_title_header, option.getMinute()));
                            option.setPosFix(getString(R.string.purchase_title_tail));
                            option.setType(PurchasePointOption.Type.PURCHASE);
                            mPurchasePointOptions.add(option);
                        }
                    }

//                     Task #20939 Remove Get Free Point item in dialog get point
//                    if (!SettingManager.getInstance(getActivity()).getKeyGetFreePoint()) {
//                        // add free
//                        mPurchasePointOptions.add(0,
//                                new PurchasePointOption(PurchasePointOption.Type.FREE,
//                                        getString(com.candy.android.R.string.dialog_point_missing_get_for_free_1),
//                                        1000, 0));
//                    }
                }

                mPurchasePointAdapter = new VideoPurchasePointAdapter(mPurchasePointOptions, mCandyChatActivity);
                mRvPurchaseList.setAdapter(mPurchasePointAdapter);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mCandyChatActivity.hidePurchasePointDialog();
                break;
        }
    }
}
