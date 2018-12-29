package com.candy.android.fragment.menu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.CircleImageView;
import com.candy.android.custom.views.MainViewPager;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.ChangeEmailPasswordFragment;
import com.candy.android.fragment.FavoritePerformerFragment;
import com.candy.android.fragment.SMSAuthenticatePhoneFragment;
import com.candy.android.fragment.webpage.WebFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.MemberInfoUpdateEvent;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by quannt on 10/13/2016.
 * Menu screen
 */

public class MenuFragment extends BaseFragment implements ProfileFragment.LoadDataFromProfile {
    private static final String TAG = "MenuFragment";
    private static final String SCREEN_NAME = "メニュー";
    private static final int TYPE_GOTO_PROFILE = 1;
    private static final int TYPE_GOTO_REGISTER_MAIL = 2;
    private static final String AGE_TEXT = "歳";

    @BindView(R.id.points)
    TextView mPoints;
    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.tv_menu_user_age)
    TextView mAge;
    @BindView(R.id.tv_menu_blood_type)
    TextView mBloodType;
    @BindView(R.id.tv_menu_user_comment)
    TextView mUserComment;
    @BindView(R.id.tv_n_unread_message)
    TextView mUnreadMessageCount;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainViewPager.setTouch(true);
        Define.webFragment = null;
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewStub mVsMenuItems = (ViewStub) view.findViewById(R.id.vst_menu_items);

        boolean isGotFreePoint = SettingManager.getInstance(mContext).getKeyGetFreePoint();

        if (isGotFreePoint) {
            mVsMenuItems.setLayoutResource(R.layout.layout_one_row_per_menu_item_without_free_point);
            mVsMenuItems.inflate();
        } else {
//            mVsMenuItems.setLayoutResource(R.layout.layout_menu_items_normal);
            mVsMenuItems.setLayoutResource(R.layout.layout_one_row_per_menu_item_normal);
            mVsMenuItems.inflate();
            view.findViewById(R.id.free_point).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoSMSAuthentication();
                }
            });
        }
        ButterKnife.bind(this, view);
        bindData();
    }

    public void loadProfileData() {
        /**QuanNT add: reload name when back from EditProfile*/
        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        if (memberInformation != null) {
            if (!TextUtils.isEmpty(memberInformation.getName())) {
                mName.setText(memberInformation.getName());
            }
            if (!TextUtils.isEmpty(String.valueOf(memberInformation.getAge()))) {
                String tmpAge = String.valueOf(memberInformation.getAge()) + AGE_TEXT;
                mAge.setText(tmpAge);
            }
            if (!TextUtils.isEmpty(memberInformation.getmBlood())) {
                if (Integer.parseInt(memberInformation.getmBlood()) > 0) {
                    String tmpBlood = Helpers.getStringOfIndexFromArray(memberInformation.getmBlood(), Define.BLOOD_GROUP);
                    mBloodType.setText(tmpBlood);
                }
            }
            if (!TextUtils.isEmpty(memberInformation.getMessage())) {
                mUserComment.setText(memberInformation.getMessage());
            }
        }
    }

    // (knv added)
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //update points
        HimecasUtils.postEventBus(getActivity(), (MemberInformation) null);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * knv added
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMemberInfoUpdateEvent(MemberInfoUpdateEvent event) {
        bindData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(PropertyChangedEvent property) {
        //Update Menu when in Profile edit then push Menu icon
        if (property.getType() == PropertyChangedEvent.TYPE_PROFILE_UPDATED) {
            MemberInformation member = SettingManager.getInstance(mContext).getMemberInformation();
            RkLogger.d(TAG,"profile updated");
            if (mName!=null&&!TextUtils.isEmpty(member.getName())) {
                mName.setText(member.getName());
            }
            if (mAge!=null) {
                String tmpAge = member.getAge() + AGE_TEXT;
                mAge.setText(tmpAge);
            }
            if (mBloodType!=null&&!TextUtils.isEmpty(member.getmBlood())) {
                if (Integer.parseInt(member.getmBlood()) > 0) {
                    String tmpBlood = Helpers.getStringOfIndexFromArray(member.getmBlood(), Define.BLOOD_GROUP);
                    mBloodType.setVisibility(View.VISIBLE);
                    mBloodType.setText(tmpBlood);
                } else {
                    mBloodType.setVisibility(View.GONE);
                }
            }
            if (mUserComment!=null&&!TextUtils.isEmpty(member.getMessage())) {
                mUserComment.setText(member.getMessage());
            }
        }

        if (property.getType() == PropertyChangedEvent.TYPE_POINT_CHANGED) {
            RkLogger.d(TAG, "onPropertyChangedEvent update TYPE_POINT_CHANGED");
            if (property.getValue() >= 0) {
            } else {
                mPoints.setVisibility(View.GONE);
            }
        }
        if (property != null && mUnreadMessageCount != null) {

            RkLogger.d(TAG, "onPropertyChangedEvent property=" + property);
            //display unread message
            if (property.getType() == PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED) {
                RkLogger.d(TAG, "onPropertyChangedEvent update TYPE_NEW_MESSAGE_CHANGED");

                if (property.getValue() > 0) {
                    String count = property.getValue() > 99 ? "99+" : String.valueOf(property.getValue());
                    mUnreadMessageCount.setText(count);
                    if (count.length() >= 3) {
                        mUnreadMessageCount.setBackgroundResource(R.drawable.bg_count_msg_large);
                    } else {
                        mUnreadMessageCount.setBackgroundResource(R.drawable.bg_count_msg);
                    }
                    mUnreadMessageCount.setVisibility(View.VISIBLE);
                } else {
                    mUnreadMessageCount.setVisibility(View.GONE);
                }
            }
        }
    }

    private void bindData() {
        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        if (memberInformation != null) {
            if (!TextUtils.isEmpty(memberInformation.getName())) {
                mName.setText(memberInformation.getName());
            }
            if (!TextUtils.isEmpty(String.valueOf(memberInformation.getAge()))) {
                String tmpAge = String.valueOf(memberInformation.getAge()) + AGE_TEXT;
                mAge.setText(tmpAge);
            }
            if (!TextUtils.isEmpty(memberInformation.getmBlood())) {
                if (Integer.parseInt(memberInformation.getmBlood()) > 0) {
                    String tmpBlood = Helpers.getStringOfIndexFromArray(memberInformation.getmBlood(), Define.BLOOD_GROUP);
                    mBloodType.setText(tmpBlood);
                }
            }
            if (!TextUtils.isEmpty(memberInformation.getMessage())) {
                mUserComment.setText(memberInformation.getMessage());
            }

            String image = SettingManager.getInstance(mContext).getMemberInformation().getProfileImageUrl();

            RkLogger.e("ImageUrl: " + image);
            Glide.with(getContext())
                    .load(image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable._icons_noimage)
                    .into(mAvatar);
        }
    }

    private void getMemberInfoChange(final int type) {
        if (SettingManager.getInstance(getContext()).hasMember()) {
            if (!NetworkUtils.hasConnection(mContext)) {
                showRetryDialog(type);
                return;
            }
            Helpers.showCircleProgressDialog(getContext());
            String id = SettingManager.getInstance(getContext()).getID();
            String password = SettingManager.getInstance(getContext()).getPassword();

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
            //create call object
            Call<MemberInfoChange> call = apiService.getMemberInfoChange(id, password);

            // send call object
            call.enqueue(new Callback<MemberInfoChange>() {
                @Override
                public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                    Helpers.dismissCircleProgressDialog();

                    if (response == null || response.body() == null) {
                        showRetryDialog(type);
                        return;
                    }

                    // goto edit profile screen
                    if (null != response.body().getMember()) {
                        RkLogger.d(TAG, "Member: " + response.body().getMember().toString());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE, response.body());
                    switch (type) {
                        case TYPE_GOTO_PROFILE:
                            ProfileFragment profileFragment = ProfileFragment.newInstance(bundle);
                            profileFragment.setLoadDataFromProfile(MenuFragment.this);
                            openNewFragment(profileFragment);
                            break;
                        case TYPE_GOTO_REGISTER_MAIL:
                            openNewFragment(ChangeEmailPasswordFragment.newInstance(bundle));
                            break;
                    }
                }

                @Override
                public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "MemberInfoChange", t);
                    Helpers.dismissCircleProgressDialog();
                    showRetryDialog(type);
                }
            });
        }
    }

    private void showRetryDialog(final int type) {
        try {
            final DialogBuilder.NoticeDialog retryDialog = DialogBuilder.NoticeDialog.newInstance(mContext.getString(R.string.can_not_connect_to_server), mContext.getString(R.string.retry));
            retryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    retryDialog.dismiss();
                    getMemberInfoChange(type);
                }

                @Override
                public void onCancelClick() {

                }
            });
            retryDialog.show(getChildFragmentManager(), TAG);
        }catch (Exception e){

        }
    }

    @OnClick(R.id.profile)
    void gotoProfile(View view) {
        inActiveMenu();
        RkLogger.d(TAG, "goto profile");
        getMemberInfoChange(TYPE_GOTO_PROFILE);
    }

    @OnClick(R.id.rlt_member_sort_info)
    void gotoProfileFromSortInfo(View view) {
        inActiveMenu();
        RkLogger.d(TAG, "goto profile");
        getMemberInfoChange(TYPE_GOTO_PROFILE);
    }

    @OnClick({R.id.point, R.id.tv_goto_point})
    void gotoPoint(View view) {
        inActiveMenu();
        RkLogger.d(TAG, "goto point");
        openNewFragment(WebFragment.newInstance(Define.WebUrl.URL_PURCHASE, true));
    }

    void gotoSMSAuthentication() {
        openNewFragment(SMSAuthenticatePhoneFragment.newInstance());
    }

    @OnClick(R.id.message)
    void gotoMessages(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_CHAT);
        inActiveMenu();
        startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @OnClick(R.id.mission)
    void gotoMission(View view) {
        RkLogger.d(TAG, "goto mission");
        inActiveMenu();
        openNewFragment(MissionFragmentContainer.newInstance());
    }

    @OnClick(R.id.notification)
    void gotoNotifications(View view) {
        inActiveMenu();
        openNewFragment(NotificationSettingFragment.newInstance());
    }

    @OnClick(R.id.favorite)
    void gotoFavorites(View view) {
        RkLogger.d(TAG, "goto favorite");
        inActiveMenu();
        openNewFragment(FavoritePerformerFragment.newInstance());
    }

    @OnClick(R.id.footprint)
    void gotoFootprint(View view) {
        RkLogger.d(TAG, "goto footprint");
        inActiveMenu();
        openNewFragment(FootprintPerformerFragment.newInstance());
    }

    @OnClick(R.id.block_list)
    void gotoBlockList(View view) {
        RkLogger.d(TAG, "goto unfavorite");
        inActiveMenu();
        openNewFragment(BlockListFragment.newInstance());
    }

    @OnClick(R.id.guide_usage)
    void gotoUsageGuide(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.USAGE_GUIDE));
    }

    @OnClick(R.id.guide_fee)
    void gotoGuideFee(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.FEE));
    }

    @OnClick(R.id.term)
    void gotoTerm(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.TERM));
    }

    @OnClick(R.id.private_policy)
    void gotoPrivatePolicy(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.POLICY));
    }

    @OnClick(R.id.trade_law)
    void gotoTradeLaw(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.TRADE_LAW));
    }

    @OnClick(R.id.faq)
    void gotoFaq(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.URL_INQUIRY_HISTORY, true));
    }

    @OnClick(R.id.faq2)
    void gotoFaq2(View view) {
        inActiveMenu();
        openNewFragment(WebFragment.newInstance(Define.WebUrl.URL_FAQ, true));
    }

    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }

    public void openNewFragment(BaseFragment fragment) {
        Fragment parentFragment = getParentFragment();
        if (fragment != null && parentFragment != null && parentFragment instanceof MainMenuContainer) {
            ((MainMenuContainer) parentFragment).replaceFragment(fragment);
        }
    }

    private void inActiveMenu() {
        MainActivity activity = (MainActivity) getActivity();
        activity.disableSelectMenu();
    }

    @Override
    public void doMenuUpdate(MemberInfoChange.Member member) {
        if (!TextUtils.isEmpty(member.getName())) {
            mName.setText(member.getName());
        }
        if (!TextUtils.isEmpty(member.getAge())) {
            String tmpAge = member.getAge() + AGE_TEXT;
            mAge.setText(tmpAge);
        }
        if (!TextUtils.isEmpty(member.getBlood())) {
            if (Integer.parseInt(member.getBlood()) > 0) {
                String tmpBlood = Helpers.getStringOfIndexFromArray(member.getBlood(), Define.BLOOD_GROUP);
                mBloodType.setVisibility(View.VISIBLE);
                mBloodType.setText(tmpBlood);
            } else {
                mBloodType.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(member.getMessage())) {
            mUserComment.setText(member.getMessage());
        }
        if (ProfileFragment.isBackToMenu) {
            DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.update_profile_success), null);
            if (!noticeDialog.isShowing()) {
                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                ProfileFragment.isBackToMenu = false;
            }
        }
    }

    @Override
    public void doAvatarUpdate(File file) {
        Uri mImageDestination = null;
        mImageDestination = Uri.fromFile(file);
        Glide.with(getContext())
                .load(mImageDestination)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable._icons_noimage)
                .into(mAvatar);
    }
}
