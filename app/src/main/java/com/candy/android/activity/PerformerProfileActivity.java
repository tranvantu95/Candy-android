package com.candy.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.candy.android.R;
import com.candy.android.adapter.ImagePagerAdapter;
import com.candy.android.configs.Define;
import com.candy.android.custom.CustomFragmentPagerAdapter;
import com.candy.android.custom.views.RoundCornerImageView;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogPointMissing;
import com.candy.android.dialog.ImageGalleryDialog;
import com.candy.android.fragment.BlogsListFragment;
import com.candy.android.fragment.FragmentPerformerBasicInfo;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.PerformerProfilesResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BaseMember;
import com.candy.android.model.PerformerDetail;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.model.eventBus.UpdateFavoritePerformerEvent;
import com.candy.android.model.eventBus.UpdatePerformerStatusEvent;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jp.fmaru.app.livechatapp.ChatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.candy.android.fragment.PerformersListFragment.sPerformerOnlines;

/**
 * - Check public: First check public in mCurrentPerformerOnline in initView(). When load data successfully,
 * check public in mCurrentPerformerDetail: check again in mCurrentPerformerDetail because need
 * check when go to PerformerProfile from avatar in blog(no has public field in blog)
 */

public class PerformerProfileActivity extends BasePopupActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, DialogBuilder.OnDialogBackPress {

    private static final String TAG = "PerformerProfileActivity";

    private static final int VIEW_PAGER_OFFSCREEN_LIMIT = 3;
    private static final int REFRESH_IMAGE_INTERVAL = 1000;
    private static final int UPDATE_STATUS = 1;
    private static final int UPDATE_ALL = 0;
    private static final int MAIN_TUTORIAL_REQUEST_CODE = 9999;

    //position when go to performerProfile not from performers list
    public static final int NO_POSITION = Integer.MIN_VALUE;
    private static final int MAX_OFFSET_TO_FINISH = 50;
    private static final int TIME_FINISH_LIVE = 60;

    private ViewPager mImageViewPager;

    private TextView mTvAge;
    private TextView mTvName;
    //    private TextView mStatus;
    private TextView mShortMessage;

    private View liveStatusLayout;
    private ImageView liveStatusImage;

    private View titleLayout;
    private View titleLayoutParent;
    private LinearLayout mGalleryLayout;
    private HorizontalScrollView mGalleryScrollview;

    private ImageView btnLike;
    private View mBtnCallVideo;
    private View mBtnPeepVideo;
    private TextView mTvEvent;
    private View mBtnChatMessage;
    private TextView mTvChatMessage;
    private PerformerOnline mCurrentPerformerOnline;
    private PerformerDetail mCurrentPerformerDetail;
    //Position of this performer in list
    private int mCurrentPosition;
    //position get from intent extra
    private int mPostion;
    private String mId, mPassword;

    private NestedScrollView mNsvInformations;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Handler mHandler;
    private Runnable mRunnable;
    private ImagePagerAdapter mImagePagerAdapter;

    //mNoticeErrorDialog: show when can not connect to server
    private DialogBuilder.NoticeDialog mRetryDialog;

    private List<PerformerOnline> mPerformerOnlines;

    private int mActionBarHeight;
    private int titleMarginStart;

    //Stop live if mTimeToFinishLive reach to 60 (1 minute)
    private int mTimeToFinishLive;

    //Check if activity is alive to prevent update UI when activity destroy
    private boolean isAlive;
    private boolean mPaused;

    protected ProgressDialog mProgressDialog;

    private LinearLayout mLlPopup;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static boolean active = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_profile);

        // Setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        isAlive = true;
        mId = SettingManager.getInstance(this).getMemberInformation().getId();
        mPassword = SettingManager.getInstance(this).getMemberInformation().getPass();

        mProgressDialog = new ProgressDialog(this, R.style.ProgressTheme);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        //knv rem this
//        mRetryDialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server), getString(R.string.retry));
//        mRetryDialog.setOnDialogBackPress(this);
        mHandler = new Handler();
        mPerformerOnlines = new ArrayList<>();
        getDataFromIntent();
        initView();
        firstSetup();
        /**
         * Load data in onPageSelected
         * */
//        loadData(UPDATE_ALL);
        if (getIntent() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SettingManager.getInstance().getIsFirstPerformerStart()) {
                        SettingManager.getInstance().setIsFirstPerformerStart(false);
                        showTutorial();
                    }
                }
            }, 200);
        }

//        showPopupMessage("title", "message", "https://picture2.livede55.com/images/p5-1288173088", 1288173088, this, mLlPopup, 23); //test
    }

    private void showTutorial() {
        Intent intent = new Intent(this, MainTutorialActivity.class);
        intent.putExtra(MainTutorialActivity.PERFORMER, mCurrentPerformerDetail != null ? mCurrentPerformerDetail : mCurrentPerformerOnline);
        startActivityForResult(intent, MAIN_TUTORIAL_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPoint(true, 0);
        if (mPaused) {
            mPaused = false;
            if (mHandler != null && mRunnable != null) {
                mHandler.postDelayed(mRunnable, REFRESH_IMAGE_INTERVAL);
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Define.IntentActions.ACTION_INAPP));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void initView() {
        mImageViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        if (isOnlyShowOnePerformer()) {
            mImagePagerAdapter = new ImagePagerAdapter(mPerformerOnlines, true);
        } else {
            mImagePagerAdapter = new ImagePagerAdapter(mPerformerOnlines, false);
        }
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImageViewPager.addOnPageChangeListener(mOnImagePageChangeListener);
        mImageViewPager.setOffscreenPageLimit(VIEW_PAGER_OFFSCREEN_LIMIT);
        mImageViewPager.setCurrentItem(mCurrentPosition);
        ImageView ivBack = (ImageView) findViewById(R.id.btn_back);
        ivBack.setOnClickListener(this);
        ImageView ivNextPerformer = (ImageView) findViewById(R.id.iv_next_performer);
        ivNextPerformer.setOnClickListener(this);
        ImageView ivPreviousPerformer = (ImageView) findViewById(R.id.iv_previous_performer);
        ivPreviousPerformer.setOnClickListener(this);
        if (isOnlyShowOnePerformer()) {
            ivNextPerformer.setVisibility(View.GONE);
            ivPreviousPerformer.setVisibility(View.GONE);
        }

//        mStatus = (TextView) findViewById(R.id.tv_performer_status);

        mTvAge = (TextView) findViewById(R.id.tv_age);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mShortMessage = (TextView) findViewById(R.id.tv_short_message);

        liveStatusLayout = findViewById(R.id.live_status_layout);
        liveStatusImage = (ImageView) findViewById(R.id.live_status_image);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(liveStatusImage);
        Glide.with(this).load(R.raw.ic_live).into(imageViewTarget);

        mBtnCallVideo = findViewById(R.id.iv_call_video);
        mBtnPeepVideo = findViewById(R.id.iv_peep_video);
        mBtnCallVideo.setOnClickListener(this);
        mBtnPeepVideo.setOnClickListener(this);

        mTvEvent = (TextView) findViewById(R.id.tv_event);

        mLlPopup = (LinearLayout) findViewById(R.id.line_popup);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Define.IntentActions.ACTION_INAPP)) {
                    final String title = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_NAME);
                    final String img = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_IMAGE);
                    final String message = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_MESSAGE);
                    final int code = (int) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_CODE);
                    final int age = (int) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_AGE);

                    showPopupMessage(title, message, img, code, context, mLlPopup, age);
                }
            }
        };

        mBtnChatMessage = findViewById(R.id.iv_chat_message);
        mBtnChatMessage.setOnClickListener(this);
        mTvChatMessage = (TextView) findViewById(R.id.tv_chat_message);
        btnLike = (ImageView) findViewById(R.id.btn_like);
        btnLike.setOnClickListener(this);

        titleLayout = findViewById(R.id.title_layout);
        titleLayoutParent = findViewById(R.id.title_layout_parent);
        mGalleryScrollview = (HorizontalScrollView) findViewById(R.id.gallery_scrollview);
        mGalleryLayout = (LinearLayout) findViewById(R.id.gallery_layout);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        mNsvInformations = (NestedScrollView) findViewById(R.id.sc_contents);
        mNsvInformations.setFillViewport(true);
        mTabLayout = (TabLayout) findViewById(R.id.tb_performer_tabs);
        mViewPager = (ViewPager) findViewById(R.id.vp_performer_info);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        params.height = height - getResources().getDimensionPixelOffset(R.dimen.performer_detail_info_tab_height)
                - getResources().getDimensionPixelOffset(R.dimen.performer_detail_toolbar_height) - statusBarHeight;
        mViewPager.setLayoutParams(params);

        titleMarginStart = getResources().getDimensionPixelOffset(R.dimen.margin_title_performer);

    }

    private void firstSetup() {
        mShortMessage.setVisibility(View.INVISIBLE);
        mTvName.setText("");
        mTvAge.setText("");
        mBtnChatMessage.setEnabled(false);
        mTvChatMessage.setEnabled(false);
        btnLike.setVisibility(View.INVISIBLE);
        liveStatusLayout.setVisibility(View.GONE);
        mBtnCallVideo.setVisibility(View.GONE);
        mBtnPeepVideo.setVisibility(View.GONE);
        mTvEvent.setVisibility(View.GONE);

        if (mCurrentPerformerOnline != null) {
            if (!TextUtils.isEmpty(mCurrentPerformerOnline.getCharacter())) {
                mShortMessage.setVisibility(View.VISIBLE);
                mShortMessage.setText(Html.fromHtml(mCurrentPerformerOnline.getCharacter()));
            }

            if (!TextUtils.isEmpty(mCurrentPerformerOnline.getName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mTvName.setText(Html.fromHtml(mCurrentPerformerOnline.getName(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mTvName.setText(Html.fromHtml(mCurrentPerformerOnline.getName()));
                }
            }

            if (mCurrentPerformerOnline.isPublicAge()) {
                mTvAge.setText(String.format(getResources().getString(R.string.age), mCurrentPerformerOnline.getAge()));
            }

            if (!mCurrentPerformerOnline.isNoPublic()) {

                mBtnChatMessage.setEnabled(true);
                mTvChatMessage.setEnabled(true);
                btnLike.setVisibility(View.VISIBLE);

                if (mCurrentPerformerOnline.isLive()) {
                    liveStatusLayout.setVisibility(View.VISIBLE);
                    mBtnCallVideo.setVisibility(View.VISIBLE);
                }

                if (mCurrentPerformerOnline.canPeep()) mBtnPeepVideo.setVisibility(View.VISIBLE);

            }

        }

    }

    private void setupViewPager() {
        List<Fragment> listFragment = new ArrayList<>();
        List<String> listTitle = new ArrayList<>();

        listFragment.add(FragmentPerformerBasicInfo.newInstance());
        listTitle.add(getString(R.string.performer_detail_tab_basic_info));

        if (!mCurrentPerformerDetail.isNoPublic()) {
            BlogsListFragment blogsListFragment = BlogsListFragment.newInstance(mCurrentPerformerOnline.getCode(), BlogsListFragment.CATEGORY_PERFORMER);
            listFragment.add(blogsListFragment);
            listTitle.add(String.format(getString(R.string.performer_detail_tab_blog), 0));
        }

        CustomFragmentPagerAdapter mAdapter = new CustomFragmentPagerAdapter(listFragment, listTitle, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

//        mNsvInformations.setFillViewport(true);
    }

    ViewPager.OnPageChangeListener mOnImagePageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (isOnlyShowOnePerformer() && positionOffsetPixels > MAX_OFFSET_TO_FINISH) {
                finish();
            }
        }

        @Override
        public void onPageSelected(int position) {
            int pageCount = mImagePagerAdapter.getCount();
            if (position == 0) {
                mImageViewPager.setCurrentItem(pageCount - 2, false);
                return;
            } else if (position == pageCount - 1) {
                mImageViewPager.setCurrentItem(1, false);
                return;
            }
            mCurrentPosition = position;
            mCurrentPerformerOnline = mPerformerOnlines.get(mCurrentPosition);
            /**QUANNT
             * Load data in onPageSelected
             * */
//            //If not first page: load data
//            if (mCurrentPosition != mPostion) {
            loadData(UPDATE_ALL);
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.iv_call_video:
                reloadPoint(false, ChatActivity.NORMAL_MODE);
                HimecasUtils.updateMemberInfoInSilent(this);
                break;

            case R.id.iv_peep_video:
                reloadPoint(false, ChatActivity.PEEP_MODE);
                HimecasUtils.updateMemberInfoInSilent(this);
                break;

            case R.id.iv_chat_message: {
                if (mCurrentPerformerDetail == null)
                    return;
                Intent intent = new Intent(PerformerProfileActivity.this, MainActivity.class);
                intent.setAction(Define.IntentActions.ACTION_CHAT);
                /** knv modified this, by QuanNT suggestion */
                intent.putExtra(Define.IntentExtras.PERFORMER_CODE, mCurrentPerformerOnline.getCode());
                intent.putExtra(Define.IntentExtras.PERFORMER_NAME, mCurrentPerformerDetail.getOrignalName());
                intent.putExtra(Define.IntentExtras.PERFORMER_AGE, mCurrentPerformerDetail.getAge());
                intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, mCurrentPerformerDetail.getProfileImageUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            break;

            case R.id.btn_like:
                btnLike.setClickable(false);
                if (btnLike.isSelected()) {
                    unFavorite();
                } else {
                    addFavorite();
                }
                break;

            case R.id.iv_next_performer:
//                if(!NetworkUtils.hasConnection(this)) {
//                    if(mNoticeErrorDialog != null && !mNoticeErrorDialog.isShowing()) {
//                        mNoticeErrorDialog.show(getSupportFragmentManager(), TAG);
//                    }
//                    return;
//                }
                if (mCurrentPosition < mPerformerOnlines.size() - 1) {
                    mCurrentPosition++;
                    mImageViewPager.setCurrentItem(mCurrentPosition, true);
                    mCurrentPerformerOnline = mPerformerOnlines.get(mCurrentPosition);
                }
                break;

            case R.id.iv_previous_performer:
//                if(!NetworkUtils.hasConnection(this)) {
//                    if(mNoticeErrorDialog != null && !mNoticeErrorDialog.isShowing()) {
//                        mNoticeErrorDialog.show(getSupportFragmentManager(), TAG);
//                    }
//                    return;
//                }
                if (mCurrentPosition > 0) {
                    mCurrentPosition--;
                    mImageViewPager.setCurrentItem(mCurrentPosition, true);
                    mCurrentPerformerOnline = mPerformerOnlines.get(mCurrentPosition);
                }
                break;

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("ReportPerformerFragment") != null) {
            super.onBackPressed();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadData(final int updateType) {
//        RkLog.d("IDK-zzz", "loadData: " + updateType);

        if (!NetworkUtils.hasConnection(this)) {
            // knv modified these
            boolean dialogShown = mRetryDialog != null && mRetryDialog.getDialog() != null &&
                    mRetryDialog.getDialog().isShowing();

//            RkLog.d("IDK-zzz", "dialogShown=" + dialogShown);

            if (!dialogShown) {
                mRetryDialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server), getString(R.string.retry));
                mRetryDialog.setOnDialogBackPress(this);
                mRetryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                    @Override
                    public void onOkClick(Object object) {
//                        RkLog.d("IDK-zzz", "onOkClick");
                        if (mRetryDialog != null) {
                            mRetryDialog.dismiss();
                        }
                        loadData(updateType);
                    }

                    @Override
                    public void onCancelClick() {
//                        RkLog.d("IDK-zzz", "onCancelClick");
                        if (mRetryDialog != null) {
                            mRetryDialog.dismiss();
                        }
                    }
                });

                try {
//                    RkLog.d("IDK-zzz", "Checking mRetryDialog isShowing: " + mRetryDialog.isShowing());
//                    RkLog.d("IDK-zzz", "Show mRetryDialog");
                    Helpers.showDialogFragment(getSupportFragmentManager(), mRetryDialog);

                } catch (Exception ex) {
//                    RkLog.e("IDK-zzz", "Exception: ", ex);
                }
            }
            return;
        }
        int performerCode = mCurrentPerformerOnline.getCode();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<PerformerProfilesResponse> call = apiService.getPerformerProfiles(mId, mPassword, performerCode);
        call.enqueue(new Callback<PerformerProfilesResponse>() {
            @Override
            public void onResponse(Call<PerformerProfilesResponse> call, Response<PerformerProfilesResponse> response) {
                if (response == null || response.body() == null)
                    return;
                int status = response.body().getStatus();
                if (status == 1 && isAlive) {
                    PerformerDetail performerDetail = response.body().getPerformer();
                    mCurrentPerformerDetail = performerDetail;
                    if (performerDetail != null) {
                        RkLogger.d("Check satus >> ", "" + performerDetail.getStatus());
                        //if status in fetched data is different from static list -> update static list and post event to update PerformerList
                        if (performerDetail.getStatus() != mCurrentPerformerOnline.getStatus() && !isOnlyShowOnePerformer()) {
                            sPerformerOnlines.get(mCurrentPosition - 1).setStatus(performerDetail.getStatus() + "");
                            UpdatePerformerStatusEvent event = new UpdatePerformerStatusEvent();
                            EventBus.getDefault().post(event);
                        }

                        if (updateType == UPDATE_ALL) {
                            setViewData(performerDetail);
                            // Notice for pages
                            setupViewPager();
                        } else if (updateType == UPDATE_STATUS) {
                            //only update status in PerformerProfile
                            updateStatusLiveOff(performerDetail);
                            mPerformerOnlines.get(mCurrentPosition).setStatus(performerDetail.getStatus() + "");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PerformerProfilesResponse> call, Throwable t) {
                if (isAlive) {
                    mCurrentPerformerDetail = new PerformerDetail();
                    setViewData((mCurrentPerformerDetail));
                }
            }
        });
    }

    private void getDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPostion = bundle.getInt(Define.IntentExtras.POSITION);
            if (sPerformerOnlines == null || sPerformerOnlines.isEmpty()) {
                finish();
                return;
            }
            if (isOnlyShowOnePerformer()) {
                PerformerOnline performerOnline = bundle.getParcelable(Define.IntentExtras.PERFORMER);
                mPerformerOnlines.add(new PerformerOnline());
                mPerformerOnlines.add(performerOnline);
                mPerformerOnlines.add(new PerformerOnline());
                mCurrentPosition = 1;
            } else {
                mPerformerOnlines.add(sPerformerOnlines.get(sPerformerOnlines.size() - 1));
                mPerformerOnlines.addAll(sPerformerOnlines);
                mPerformerOnlines.add(sPerformerOnlines.get(0));
                mCurrentPosition = mPostion + 1;
            }
            mCurrentPerformerOnline = mPerformerOnlines.get(mCurrentPosition);
        }
    }

    private void setViewData(final PerformerDetail performerDetail) {
        RkLogger.d("Check no image >> ", "setViewData");
        setupViewPager();
        mHandler.removeCallbacksAndMessages(null);

        btnLike.setSelected(performerDetail.getFavorited() == Define.LIKED);

        if (!performerDetail.isNoPublic() && performerDetail.isLive()) {
            liveStatusLayout.setVisibility(View.VISIBLE);
            mBtnCallVideo.setVisibility(View.VISIBLE);

            if (performerDetail.canShowSlideImage()) {
                View currentView = mImageViewPager.findViewWithTag(ImagePagerAdapter.TAG + mImageViewPager.getCurrentItem());
                if (currentView != null) {
                    final ImageView currentImageView = (ImageView) currentView.findViewById(R.id.qiv_image);
                    final String slideImageUrl = performerDetail.getSlideImageUrl();
                    loadImageNotCache(currentImageView, slideImageUrl);

                    mTimeToFinishLive = 0;
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mTimeToFinishLive >= TIME_FINISH_LIVE) {
                                mHandler.removeCallbacksAndMessages(null);
                                Glide.with(PerformerProfileActivity.this)
                                        .load(performerDetail.getProfileImageUrl())
                                        .into(currentImageView);
                                return;
                            }
                            mTimeToFinishLive++;
                            loadImageNotCache(currentImageView, slideImageUrl);
                            mHandler.postDelayed(mRunnable, REFRESH_IMAGE_INTERVAL);
                        }
                    };
                    mHandler.postDelayed(mRunnable, REFRESH_IMAGE_INTERVAL);
                }
            }
        } else {
            liveStatusLayout.setVisibility(View.GONE);
            mBtnCallVideo.setVisibility(View.GONE);
        }

        mBtnPeepVideo.setVisibility(!performerDetail.isNoPublic() && performerDetail.canPeep() ? View.VISIBLE : View.GONE);

        if (performerDetail.isNoPublic()) {
            RkLogger.d("Check no image >> ", "isNoPublic");

            mBtnChatMessage.setEnabled(false);
            mTvChatMessage.setEnabled(false);
            btnLike.setVisibility(View.GONE);
            View currentView = mImageViewPager.findViewWithTag(ImagePagerAdapter.TAG + mImageViewPager.getCurrentItem());
            if (currentView != null) {
                RkLogger.d("Check no image >> ", "set no image");
                final ImageView currentImageView = (ImageView) currentView.findViewById(R.id.qiv_image);
                currentImageView.setImageResource(R.drawable.no_image);
            }
        } else {
            mBtnChatMessage.setEnabled(true);
            mTvChatMessage.setEnabled(true);
            btnLike.setVisibility(View.VISIBLE);
            if (isOnlyShowOnePerformer()) {
                View currentView = mImageViewPager.findViewWithTag(ImagePagerAdapter.TAG + mImageViewPager.getCurrentItem());
                if (currentView != null) {
                    RkLogger.d("Check no image >> ", "set no image");
                    final ImageView currentImageView = (ImageView) currentView.findViewById(R.id.qiv_image);
                    Glide.with(this).load(mCurrentPerformerDetail.getProfileImageUrl()).into(currentImageView);
                }
            }
        }
        if (performerDetail.isPublicAge()) {
            mTvAge.setVisibility(View.VISIBLE);
            mTvAge.setText(String.format(getResources().getString(R.string.age), performerDetail.getAge()));
        } else {
            mTvAge.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(performerDetail.getName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTvName.setText(Html.fromHtml(performerDetail.getName(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTvName.setText(Html.fromHtml(performerDetail.getName()));
            }
        } else {
            mTvName.setText("");
        }

//        if (performerDetail.getIsNew() == Define.PERFORMER_NEW || performerDetail.getCurrentCampaign() == Define.CAMPAIGN_IN_PROGRESS) {
//            int pointPerMinute;
//            if (performerDetail.getCurrentCampaign() == Define.CAMPAIGN_IN_PROGRESS) {
//                pointPerMinute = performerDetail.getPointPerMinuteCampaign();
//            } else {
//                pointPerMinute = performerDetail.getPointPerMinute();
//            }
//
//            String eventString = String.format(getString(R.string.profile_event_text_format), pointPerMinute);
//            SpannableString spanEventString = new SpannableString(eventString);
//            spanEventString.setSpan(new RelativeSizeSpan(1.2f), 7, 7 + HimecasUtils.numberDigits(pointPerMinute), 0);
//            mTvEvent.setText(spanEventString);
//        } else {
//            mTvEvent.setVisibility(View.GONE);
//        }

        if(performerDetail.getPointPerMinute()==0){
            mTvEvent.setVisibility(View.VISIBLE);

        }else{
            mTvEvent.setVisibility(View.GONE);
        }

//        mStatus.setText(Define.OnlineStatus.getStatusFromCode(performerDetail.getStatus()).getName());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mStatus.setBackground(getDrawable(Define.OnlineStatus.getBackgroundTransFromCode(performerDetail.getStatus())));
//        } else {
//            mStatus.setBackground(getResources().getDrawable(Define.OnlineStatus.getBackgroundTransFromCode(performerDetail.getStatus())));
//        }

        if (TextUtils.isEmpty(performerDetail.getCharacter())) {
            mShortMessage.setVisibility(View.INVISIBLE);
        } else {
            mShortMessage.setVisibility(View.VISIBLE);
            mShortMessage.setText(Html.fromHtml(performerDetail.getCharacter()));
        }

        //Load gallery image
        mGalleryLayout.removeAllViews();
        final List<PerformerDetail.Gallery> galleries = performerDetail.getGallery();
        if (galleries != null && galleries.size() > 0) {
            int imageSize = getResources().getDimensionPixelSize(R.dimen.gallery_item_size);
            for (int i = 0; i < galleries.size(); i++) {
                final PerformerDetail.Gallery gallery = galleries.get(i);
                RoundCornerImageView imageView = new RoundCornerImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageSize, imageSize);
                lp.setMargins((int) getResources().getDimension(R.dimen.medium_padding), 0, 0, 0);
                imageView.setLayoutParams(lp);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageGalleryDialog dialog = ImageGalleryDialog.newInstance(gallery.getImage(), gallery.getComment());
                        dialog.show(getSupportFragmentManager(), TAG);
                    }
                });
                mGalleryLayout.addView(imageView);
                Glide.with(this).load(gallery.getThumbnail()).into(imageView);
            }
            mGalleryScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
    }

//    public void animLike() {
//        // final ArrayList spriteList = new ArrayList();
//        final int spriteNums = mFavoriteBitmapSource.getWidth() / mFavoriteBitmapSource.getHeight();
//        final AnimationDrawable anim = new AnimationDrawable();
//        for (int i = 0; i < spriteNums; i++) {
//            Bitmap bmp = Bitmap.createBitmap(mFavoriteBitmapSource, mFavoriteBitmapSource.getHeight() * i, 0, mFavoriteBitmapSource.getHeight(), mFavoriteBitmapSource.getHeight());
//            anim.addFrame(new BitmapDrawable(getResources(), bmp), Define.DURATION_FAVORITE);
//        }
//        anim.setOneShot(true);
//        btnLike.setImageDrawable(anim);
//        btnLike.post(new Runnable() {
//            @Override
//            public void run() {
//                anim.start();
//            }
//        });
//    }

    private void addFavorite() {
        int performerCode = mCurrentPerformerOnline.getCode();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiFavoriteResponse> call = apiService.favoriteAdd(mId, mPassword, performerCode);
        call.enqueue(new Callback<ApiFavoriteResponse>() {
            @Override
            public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                if (isAlive) {
                    btnLike.setClickable(true);
                }
                if (response == null || response.body() == null) {
                    return;
                }
                if (response.body().isSuccess()) {
                    if (isAlive) {
                        btnLike.setSelected(true);
                    }
                    EventBus.getDefault().post(new UpdateFavoritePerformerEvent(new PerformerFavorite(mPerformerOnlines.get(mCurrentPosition)),
                            UpdateFavoritePerformerEvent.TYPE_ADD_FAVORITE));
                }
            }

            @Override
            public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                if (isAlive) {
                    btnLike.setClickable(true);
                }
            }
        });
    }

    private void unFavorite() {
        int performerCode = mCurrentPerformerOnline.getCode();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiFavoriteResponse> call = apiService.favoriteDelete(mId, mPassword, performerCode);
        call.enqueue(new Callback<ApiFavoriteResponse>() {
            @Override
            public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                if (isAlive) {
                    btnLike.setClickable(true);
                }
                if (response == null || response.body() == null) {
                    return;
                }
                if (response.body().isSuccess()) {
                    if (isAlive) {
                        btnLike.setSelected(false);
                    }
                    EventBus.getDefault().post(new UpdateFavoritePerformerEvent(new PerformerFavorite(mPerformerOnlines.get(mCurrentPosition)),
                            UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE));
                }
            }

            @Override
            public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                if (isAlive) {
                    btnLike.setClickable(true);
                }
            }
        });
    }

    public PerformerOnline getCurrentPerformerOnline() {
        return mCurrentPerformerOnline;
    }

    public PerformerDetail getCurrentPerformerDetail() {
        return mCurrentPerformerDetail;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (0 == mActionBarHeight) {
            getDefaultSize();
        }
        int space = Math.abs(verticalOffset) + mActionBarHeight - appBarLayout.getTotalScrollRange();
        if (space >= 0) {
            float f = 1f * space / mActionBarHeight;

            int newX = titleMarginStart + Math.round(f * (mActionBarHeight - titleMarginStart));
            titleLayout.setX(newX);

            titleLayoutParent.getBackground().setAlpha((int) ((1 - f) * 255));

            mGalleryScrollview.setAlpha(1 - f);
            if (f == 1 && mGalleryScrollview.getVisibility() == View.VISIBLE)
                mGalleryScrollview.setVisibility(View.GONE);
            else if (f != 1 && mGalleryScrollview.getVisibility() != View.VISIBLE)
                mGalleryScrollview.setVisibility(View.VISIBLE);

            liveStatusLayout.setAlpha(1 - f);

//            mStatus.setVisibility(View.INVISIBLE);
//            if (mCurrentPerformerDetail != null && mCurrentPerformerDetail.getStatus() == Define.PerformerStatus.CALL_WAITING) {
//                mLive.setVisibility(View.INVISIBLE);
//            }
        } else {
            titleLayout.setX(titleMarginStart);
            titleLayoutParent.getBackground().setAlpha(255);
            mGalleryScrollview.setAlpha(1);
            mGalleryScrollview.setVisibility(View.VISIBLE);
            liveStatusLayout.setAlpha(1);

//            mStatus.setVisibility(View.VISIBLE);
//            if (mCurrentPerformerDetail != null && mCurrentPerformerDetail.getStatus() == Define.PerformerStatus.CALL_WAITING) {
//                mLive.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void getDefaultSize() {
        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        mActionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
    }

    @Override
    protected void onDestroy() {
        isAlive = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onClickOpenDetail(String name, int code, String img, int age) {
        mLlPopup.removeAllViews();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_CHAT);
        intent.putExtra(Define.IntentExtras.PERFORMER_CODE, code);
        intent.putExtra(Define.IntentExtras.PERFORMER_NAME, name);
        intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, img);
        intent.putExtra(Define.IntentExtras.PERFORMER_AGE, age);
        startActivity(intent);
        finish();
    }

    private void loadImageNotCache(final ImageView imageView, String url) {
        Glide.with(PerformerProfileActivity.this)
                .load(url)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        loadData(UPDATE_STATUS);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageView.setImageBitmap(resource);
                        return true;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView.getWidth(), imageView.getWidth());
    }

    public void setNumberOfBlogs(int numberOfBlogs) {
        if (mTabLayout != null && mTabLayout.getTabAt(1) != null) {
            String blogTabTitle = String.format(getString(R.string.performer_detail_tab_blog), numberOfBlogs);
            //Set different size for pointPerMinute
            SpannableString spanBlogTabTitle = new SpannableString(blogTabTitle);
            spanBlogTabTitle.setSpan(new RelativeSizeSpan(5), 4, blogTabTitle.length() - 1, 0);
            mTabLayout.getTabAt(1).setText(blogTabTitle);
        }
    }

    public void updateStatusLiveOff(PerformerDetail performerDetail) {
        if (performerDetail.isLive()) return;

        mHandler.removeCallbacksAndMessages(null);

        liveStatusLayout.setVisibility(View.GONE);
        mBtnCallVideo.setVisibility(View.GONE);
        mBtnPeepVideo.setVisibility(View.GONE);
        mTvEvent.setVisibility(View.GONE);

//        mStatus.setText(Define.OnlineStatus.getStatusFromCode(performerDetail.getStatus()).getName());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mStatus.setBackground(getDrawable(Define.OnlineStatus.getBackgroundTransFromCode(performerDetail.getStatus())));
//        } else {
//            mStatus.setBackground(getResources().getDrawable(Define.OnlineStatus.getBackgroundTransFromCode(performerDetail.getStatus())));
//        }

        //Load imageProfileUrl
        View currentView = mImageViewPager.findViewWithTag(ImagePagerAdapter.TAG + mImageViewPager.getCurrentItem());
        ImageView imageView = (ImageView) currentView.findViewById(R.id.qiv_image);
        Glide.with(this).load(mCurrentPerformerDetail.getProfileImageUrl())
                .into(imageView);

    }

    private boolean isOnlyShowOnePerformer() {
        return mPostion == NO_POSITION;
    }

    @Override
    public void onDialogBackPress() {
        // In case LIVE, we do request each second, so dialog retry show
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void reloadPoint(final boolean isOnResume, final int mode) {
        if (!isOnResume && !NetworkUtils.hasConnection(this)) {
            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getSupportFragmentManager(), TAG);
            return;
        }
        if (!isOnResume) {
            showCircleDialog();
        }
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

        //create call object
        Call<MemberInfoChange> call = apiService.getMemberInfoChange(mId, mPassword);
        call.enqueue(new Callback<MemberInfoChange>() {
            @Override
            public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                if (!isOnResume) {
                    hideCircleDialog();
                }
                if (response != null) {
                    MemberInfoChange body = response.body();
                    if (body != null && body.isSuccess()) {
                        MemberInfoChange.Member member = body.getMember();
                        if (member != null) {
                            HimecasUtils.postEventBus(PerformerProfileActivity.this, new BaseMember(member));

                            if (!isOnResume && mCurrentPerformerDetail != null
                                    && !TextUtils.isEmpty(member.getPoint())
                                    && TextUtils.isDigitsOnly(member.getPoint())) {

                                int point = Integer.valueOf(member.getPoint());

                                if (!isEnoughPoint(point, mode)) showDialogPointMissing();
                                else doCallVideo(mode);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                if (!isOnResume) {
                    hideCircleDialog();
                    DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getSupportFragmentManager(), TAG);
                }
            }
        });
    }

    private boolean isEnoughPoint(int memberPoint, int mode) {

        if (mode == ChatActivity.NORMAL_MODE) {
            if (mCurrentPerformerDetail.getCurrentCampaign() == Define.CAMPAIGN_IN_PROGRESS) {
                return memberPoint >= mCurrentPerformerDetail.getPointPerMinuteCampaign();
            }
            return memberPoint >= mCurrentPerformerDetail.getPointPerMinute();
        } else {
            return memberPoint >= Define.PEEP_VIDEO_POINT_PER_MINUTE;
        }
    }

    private void showDialogPointMissing() {
        ArrayList<PurchasePointOption> options = new ArrayList<>();
        options.add(new PurchasePointOption(PurchasePointOption.Type.FREE, "無料", 100, 10));
        options.add(new PurchasePointOption(PurchasePointOption.Type.PURCHASE, "メール1", 1010, 160));
        options.add(new PurchasePointOption(PurchasePointOption.Type.PURCHASE, "メール2", 1030, 120));
        options.add(new PurchasePointOption(PurchasePointOption.Type.PURCHASE, "メール3", 1200, 140));

        final DialogPointMissing requestPointDialog = DialogPointMissing.newInstance(options, DialogPointMissing.TYPE_VIDEO_CHAT);
        requestPointDialog.setPurchaseOptionClickListener(new DialogPointMissing.OnPurchaseOptionClickListener() {
            @Override
            public void onOptionClick(PurchasePointOption option) {
                RkLogger.e("onOptionClick: " + option.getPrice());
                if (option.getType() == PurchasePointOption.Type.PURCHASE) {
                    requestPointDialog.dismiss();
                    HimecasUtils.buildBuyPointIntent(option.getProductId(), PerformerProfileActivity.this);
                } else if (option.getType() == PurchasePointOption.Type.FREE) {
                    requestPointDialog.dismiss();
                    HimecasUtils.routeToFreePointScreen(PerformerProfileActivity.this);
                }
            }
        });
        Helpers.showDialogFragment(getSupportFragmentManager(), requestPointDialog);
    }

    private void doCallVideo(int mode) {
        if (mCurrentPerformerDetail == null) return;

        Intent webIntent = new Intent(PerformerProfileActivity.this, VideoCallActivity.class);
        webIntent.putExtra(ChatActivity.EXTRA_ID, mId);
        webIntent.putExtra(ChatActivity.EXTRA_PASS, mPassword);
//        webIntent.putExtra("performerCode", String.valueOf(mPerformerOnlines.get(mCurrentPosition).getCode()));
//        webIntent.putExtra(Define.IntentExtras.PERFORMER_NAME, mPerformerOnlines.get(mCurrentPosition).getName());
//        webIntent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, mPerformerOnlines.get(mCurrentPosition).getProfileImageUrl());
        webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_CODE, String.valueOf(mPerformerOnlines.get(mCurrentPosition).getCode()));
        webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_NAME, mCurrentPerformerDetail.getName());
        webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_IMAGE, mCurrentPerformerDetail.getProfileImageUrl());
        webIntent.putExtra(ChatActivity.EXTRA_APP_CODE, SettingManager.getInstance().getConfig().getAppCode());
        webIntent.putExtra(Define.IntentExtras.VIDEO_MODE, Define.VIDEO_MODE_VALUE_DIRECT);
        webIntent.putExtra(ChatActivity.EXTRA_MODE, mode);
        startActivityForResult(webIntent, 100);
    }

    protected void showCircleDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void hideCircleDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_CANCELED) {
//                boolean is3DayWithin = SettingManager.getInstance().getMemberInformation().getmBuyTime() < 1 && SettingManager.getInstance().getMemberInformation().ismIs3Day();
////                if (Dialog72Hour.isNewAccount72h()) {
//                if (is3DayWithin) {
//                    Dialog72Hour dialog72Hour = new Dialog72Hour();
//                    dialog72Hour.show(getSupportFragmentManager(), dialog72Hour.getClass().getSimpleName());
//                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }
}
