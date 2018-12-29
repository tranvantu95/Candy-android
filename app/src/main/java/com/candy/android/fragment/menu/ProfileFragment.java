package com.candy.android.fragment.menu;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.candy.android.R;
import com.candy.android.activity.SplashScreen;
import com.candy.android.configs.Define;
import com.candy.android.custom.ClickHandler;
import com.candy.android.custom.MyDatePickerDialog;
import com.candy.android.custom.gallery.Utils;
import com.candy.android.custom.gallery.activities.GalleryActivity;
import com.candy.android.custom.image_crop.CropImageActivity;
import com.candy.android.custom.image_crop.image_crop.CropImageView;
import com.candy.android.custom.views.CircleImageView;
import com.candy.android.custom.views.MainViewPager;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogLogOut;
import com.candy.android.dialog.DialogSelectImage;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.ChangeEmailPasswordFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.http.response.ApiUploadAvatarResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";
    // Request permission code
    private static final int CAMERA_STORAGE_PERMISSION = 1;

    public static final int PICK_IMAGE = 100;
    public static final int TAKE_IMAGE = 101;

    private static final int TYPE_GOTO_PROFILE = 1;
    private static final int TYPE_GOTO_REGISTER_MAIL = 2;

    @BindView(R.id.name_input)
    EditText mEditName;
    @BindView(R.id.age_input)
    TextView mTvAge;
    @BindView(R.id.tv_blood_group)
    TextView mTvBloodGroup;
    @BindView(R.id.address_input)
    TextView mTvAddress;
    @BindView(R.id.message_input)
    EditText mEditMessage;
    @BindView(R.id.member_image)
    CircleImageView mMemberImage;
    @BindView(R.id.user_code)
    TextView mUserCode;
    @BindView(R.id.tv_current_point)
    TextView mTvCurrentPoint;
    @BindView(R.id.tv_mail_adress)
    TextView mTvMailAdress;
    @BindView(R.id.root_scroll_view)
    ScrollView mRootView;

    @BindView(R.id.job_input)
    EditText mEditJob;
    @BindView(R.id.typeWoman_input)
    EditText mEditTypeWoman;
    @BindView(R.id.pet_input)
    EditText mEditPet;
    @BindView(R.id.geek_input)
    EditText mEditGeek;
    @BindView(R.id.onlyOneWish_input)
    EditText mEditOnlyOneWish;
    @BindView(R.id.engrossed_input)
    EditText mEditEngrossed;
    private ClickHandler mClickHandler;

    //MemberInfo: store local change and use for post to server
    private MemberInfoChange mMemberInfoChange;
    //Real MemberInfo: use for: check whether info has changed
    private MemberInfoChange mRealMemberInfo;
    private Uri mImageDestination;

    private boolean isEditMessage;
    private boolean isEditEngrossed;
    private boolean isEditOnlyOneWish;
    private boolean isEditTypeWoman;
    private boolean isEditGeek;
    private boolean isEditPet;
    private boolean isEditJob;
    private boolean isEditName;
    private LoadDataFromProfile loadDataFromProfile;
    public static boolean isBackToMenu;

    private List<String> mProhibitedWords;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(Bundle bundle) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE) != null) {
            mMemberInfoChange = bundle.getParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE);
            mRealMemberInfo = bundle.getParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fm_menu_container);
        if (null != f) {
            if (f instanceof ProfileFragment) {
                MainViewPager.setTouch(false);
                Define.webFragment = (ProfileFragment) f;
            } else {
                MainViewPager.setTouch(true);
                Define.webFragment = null;
            }
        }

        ButterKnife.bind(this, rootView);
        mClickHandler = new ClickHandler(500);
        bindData();
        setViewProperty();
        loadProhibitedWorlds();
        return rootView;
    }

    private void bindData() {
        if (mRealMemberInfo != null) {
            MemberInfoChange.Member member = mRealMemberInfo.getMember();
            Log.i("login", SettingManager.getInstance(getActivity()).getMemberInformation().getEmail() + "\n" + SettingManager.getInstance(getActivity()).getMemberInformation().getPass());
            if (member != null) {
                String memberName = member.getName();
                //memberName only has length in [3, 10]
                if (TextUtils.isEmpty(memberName) || memberName.contains(Define.RAND_NAME_PREFIX)) {
                    if (SettingManager.getInstance(mContext).isNameSet()) {
                        SettingManager.getInstance(mContext).setIsNameSet(false);
                    }
                } else {
                    if (!SettingManager.getInstance(mContext).isNameSet()) {
                        SettingManager.getInstance(mContext).setIsNameSet(true);
                    }
                    mEditName.setText(memberName);
                }

                // member image
                String image = SettingManager.getInstance(mContext).getMemberInformation().getProfileImageUrl();
                Glide.with(getContext())
                        .load(image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable._icons_noimage)
                        .into(mMemberImage);

                mTvAge.setText(String.format("%s歳", member.getAge()));
                mTvBloodGroup.setText(Helpers.getStringOfIndexFromArray(member.getBlood(), Define.BLOOD_GROUP));
                mTvAddress.setText(Helpers.getStringOfIndexFromArray(member.getArea(), Define.MEMBER_AREA));
                if (TextUtils.isEmpty(member.getMessage())) {
                    if (TextUtils.isEmpty(memberName)) {
                        mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint), "NAMENAME"));
                    } else {
                        mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint), memberName));
                    }
                } else {
                    if (member.getMessage().equals("　")) {
                        mEditMessage.setText("");
                        mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint), memberName));
                    } else {
                        mEditMessage.setText(member.getMessage());
                    }
                }
                //
                if (TextUtils.isEmpty(member.getEngrossed())) {
                    mEditEngrossed.setVisibility(View.GONE);
                } else {
                    mEditEngrossed.setVisibility(View.VISIBLE);
                    mEditEngrossed.setText(member.getEngrossed());
                }
                //
                if (TextUtils.isEmpty(member.getJob())) {
                    mEditJob.setVisibility(View.GONE);
                } else {
                    mEditJob.setVisibility(View.VISIBLE);
                    mEditJob.setText(member.getJob());
                }
                //
                if (TextUtils.isEmpty(member.getPet())) {
                    mEditPet.setVisibility(View.GONE);
                } else {
                    mEditPet.setVisibility(View.VISIBLE);
                    mEditPet.setText(member.getPet());
                }
                //
                if (TextUtils.isEmpty(member.getOnlyOneWish())) {
                    mEditOnlyOneWish.setVisibility(View.GONE);
                } else {
                    mEditOnlyOneWish.setVisibility(View.VISIBLE);
                    mEditOnlyOneWish.setText(member.getOnlyOneWish());
                }
                //
                if (TextUtils.isEmpty(member.getGeek())) {
                    mEditGeek.setVisibility(View.GONE);
                } else {
                    mEditGeek.setVisibility(View.VISIBLE);
                    mEditGeek.setText(member.getGeek());
                }
                //
                if (TextUtils.isEmpty(member.getTypeWoman())) {
                    mEditTypeWoman.setVisibility(View.GONE);
                } else {
                    mEditTypeWoman.setVisibility(View.VISIBLE);
                    mEditTypeWoman.setText(member.getTypeWoman());
                }
                mUserCode.setText(member.getCode());
                mTvCurrentPoint.setText(String.format(Locale.US, getString(R.string.action_bar_n_pts), Integer.valueOf(member.getPoint())));
                String mail = member.getMail();
                if (!TextUtils.isEmpty(mail)) {
                    if (mail.contains(Define.RAND_EMAIL_POSIX)) {
                        mTvMailAdress.setText(getString(R.string.not_registered_notice));
                    } else {
                        mTvMailAdress.setText(mail);
                    }
                }
            }
            mRootView.scrollTo(0, 0);
        }
    }

    private void setViewProperty() {
        mEditName.clearFocus();
        mEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    mEditName.setBackgroundResource(android.R.color.transparent);
                    String name = mEditName.getText().toString();
                    String realName = mRealMemberInfo.getMember().getName();
                    //if has change
                    if (!name.equals(realName)) {
//                        if(!realName.contains(Define.RAND_NAME_PREFIX)) {
//                            mEditName.setText(realName);
//                        }
                        if (TextUtils.isEmpty(name)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.name_empty_message), null).show(getFragmentManager(), TAG);
                            mEditName.setText(realName);
                        } else if (name.length() < 3) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                            mEditName.setText(realName);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                            mEditName.setText(realName);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                mMemberInfoChange.getMember().setName(name);
                            }
                            pushMemberInfo();
                            Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                            if (fragment != null && fragment instanceof ProfileFragment) {
                                isBackToMenu = false;
                            } else {
                                isBackToMenu = true;
                            }
                            if (loadDataFromProfile != null) {
                                loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                            }
                        }
                    }
                    isEditName = false;
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    mEditName.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditName, InputMethodManager.SHOW_IMPLICIT);
                    isEditName = true;
                }
            }
        });
        mEditJob.clearFocus();
        mEditJob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditJob) {
                    String job = mEditJob.getText().toString();
                    String realJob = mRealMemberInfo.getMember().getJob();
                    //if has change
                    if (!job.equals(realJob)) {
                        if (job.length() > 32) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_job_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditJob.getText().toString())) {
                                    mMemberInfoChange.getMember().setJob(job);
                                }
                            }
                            if (isContainsProhibitedWords(mEditJob.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                mEditJob.setEnabled(false);
                                mEditJob.setBackgroundResource(android.R.color.transparent);
                                mEditJob.setGravity(Gravity.LEFT);
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    isEditJob = false;
                    if (job.isEmpty()) {
                        mEditJob.setVisibility(View.GONE);
                    } else {
                        mEditJob.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditTypeWoman.clearFocus();
        mEditTypeWoman.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditTypeWoman) {
                    String typeWoman = mEditTypeWoman.getText().toString();
                    String realTypeWoman = mRealMemberInfo.getMember().getTypeWoman();
                    //if has change
                    if (!typeWoman.equals(realTypeWoman)) {
                        if (typeWoman.length() > 20) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_type_women_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditTypeWoman.getText().toString())) {
                                    mMemberInfoChange.getMember().setTypeWoman(typeWoman);
                                }
                            }
                            if (isContainsProhibitedWords(mEditTypeWoman.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditTypeWoman.setEnabled(false);
                    mEditTypeWoman.setBackgroundResource(android.R.color.transparent);
                    mEditTypeWoman.setGravity(Gravity.LEFT);
                    isEditTypeWoman = false;
                    if (typeWoman.isEmpty()) {
                        mEditTypeWoman.setVisibility(View.GONE);
                    } else {
                        mEditTypeWoman.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditGeek.clearFocus();
        mEditGeek.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditGeek) {
                    String geek = mEditGeek.getText().toString();
                    String realGeek = mRealMemberInfo.getMember().getGeek();
                    //if has change
                    if (!geek.equals(realGeek)) {
                        if (geek.length() > 20) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditGeek.getText().toString())) {
                                    mMemberInfoChange.getMember().setGeek(geek);
                                }
                            }
                            if (isContainsProhibitedWords(mEditGeek.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditGeek.setEnabled(false);
                    mEditGeek.setBackgroundResource(android.R.color.transparent);
                    mEditGeek.setGravity(Gravity.LEFT);
                    isEditGeek = false;
                    if (geek.isEmpty()) {
                        mEditGeek.setVisibility(View.GONE);
                    } else {
                        mEditGeek.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditPet.clearFocus();
        mEditPet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditPet) {
                    String pet = mEditPet.getText().toString();
                    String realPet = mRealMemberInfo.getMember().getPet();
                    //if has change
                    if (!pet.equals(realPet)) {
                        if (pet.length() > 20) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditPet.getText().toString())) {
                                    mMemberInfoChange.getMember().setPet(pet);
                                }
                            }
                            if (isContainsProhibitedWords(mEditPet.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditPet.setEnabled(false);
                    mEditPet.setBackgroundResource(android.R.color.transparent);
                    mEditPet.setGravity(Gravity.LEFT);
                    isEditPet = false;
                    if (pet.isEmpty()) {
                        mEditPet.setVisibility(View.GONE);
                    } else {
                        mEditPet.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditOnlyOneWish.clearFocus();
        mEditOnlyOneWish.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditOnlyOneWish) {
                    String onlyOneWish = mEditOnlyOneWish.getText().toString();
                    String realOnlyOneWish = mRealMemberInfo.getMember().getOnlyOneWish();
                    //if has change
                    if (!onlyOneWish.equals(realOnlyOneWish)) {
                        if (onlyOneWish.length() > 20) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditOnlyOneWish.getText().toString())) {
                                    mMemberInfoChange.getMember().setOnlyOneWish(onlyOneWish);
                                }
                            }
                            if (isContainsProhibitedWords(mEditOnlyOneWish.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditOnlyOneWish.setEnabled(false);
                    mEditOnlyOneWish.setBackgroundResource(android.R.color.transparent);
                    mEditOnlyOneWish.setGravity(Gravity.LEFT);
                    isEditOnlyOneWish = false;
                    if (onlyOneWish.isEmpty()) {
                        mEditOnlyOneWish.setVisibility(View.GONE);
                    } else {
                        mEditOnlyOneWish.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditEngrossed.clearFocus();
        mEditEngrossed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && isEditEngrossed) {
                    String engrossed = mEditEngrossed.getText().toString();
                    String realEngrossed = mRealMemberInfo.getMember().getEngrossed();
                    //if has change
                    if (!engrossed.equals(realEngrossed)) {
                        if (engrossed.length() > 20) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (mMemberInfoChange != null) {
                                if (!isContainsProhibitedWords(mEditEngrossed.getText().toString())) {
                                    mMemberInfoChange.getMember().setEngrossed(engrossed);
                                }
                            }
                            if (isContainsProhibitedWords(mEditEngrossed.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }
                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditEngrossed.setEnabled(false);
                    mEditEngrossed.setBackgroundResource(android.R.color.transparent);
                    mEditEngrossed.setGravity(Gravity.LEFT);
                    isEditEngrossed = false;
                    if (engrossed.isEmpty()) {
                        mEditEngrossed.setVisibility(View.GONE);
                    } else {
                        mEditEngrossed.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mEditMessage.clearFocus();
        mEditMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String message = mEditMessage.getText().toString();
                    String realMessage = mRealMemberInfo.getMember().getMessage();
                    //if has change
                    if (!message.equals(realMessage) && !(message.equals("") && realMessage.equals("　"))) {
                        if (message.length() > 500) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                        } else if (!NetworkUtils.hasConnection(mContext)) {
                            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
                        } else {
                            resetMemberInfoChange();
                            if (isContainsProhibitedWords(mEditMessage.getText().toString())) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                            } else {
                                if (mMemberInfoChange != null) {
                                    mMemberInfoChange.getMember().setMessage(message);
                                }
                                pushMemberInfo();
                                Fragment fragment = getFragmentManager().findFragmentById(R.id.fm_menu_container);
                                if (fragment != null && fragment instanceof ProfileFragment) {
                                    isBackToMenu = false;
                                } else {
                                    isBackToMenu = true;
                                }
                                if (loadDataFromProfile != null) {
                                    loadDataFromProfile.doMenuUpdate(mMemberInfoChange.getMember());
                                }

                            }
                        }
                    }
                    //hide keyboard when lost focus
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mEditMessage.setEnabled(false);
                    mEditMessage.setBackgroundResource(android.R.color.transparent);
                    mEditMessage.setGravity(Gravity.LEFT);
                    isEditMessage = false;
                }
            }
        });
    }

    private String[] getBirthData() {
        if (mMemberInfoChange == null) {
            return null;
        }
        String birth = mMemberInfoChange.getMember().getBirth();
        if (!TextUtils.isEmpty(birth)) {
            String[] parseBirth = birth.split("-");
            if (parseBirth.length > 2) {
                return parseBirth;
            }
        }
        return null;
    }

    private void pushMemberInfo() {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
//            bindData();
            return;
        }
        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        if (memberInformation != null &&
                mMemberInfoChange != null && mMemberInfoChange.getMember() != null) {
            // Check prohibited words in name
            String newName = mMemberInfoChange.getMember().getName();
            String newComment = mMemberInfoChange.getMember().getMessage();
            String newJob = mMemberInfoChange.getMember().getJob();
            String newTypeWoman = mMemberInfoChange.getMember().getTypeWoman();
            String newGeek = mMemberInfoChange.getMember().getGeek();
            String newPet = mMemberInfoChange.getMember().getPet();
            String newOnlyOneWish = mMemberInfoChange.getMember().getOnlyOneWish();
            String newEngrossed = mMemberInfoChange.getMember().getEngrossed();
            if (isContainsProhibitedWords(newName) || isContainsProhibitedWords(newComment)) {
                // Show dialog
                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                if (!noticeDialog.isShowing()) {
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                }
                return;
            }
            String id = memberInformation.getId();
            String password = memberInformation.getPass();
            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);
            final MemberInfoChange.Member member = mMemberInfoChange.getMember();
            String[] birthData = getBirthData();
            int year = 1996;
            int month = 1;
            int day = 1;
            if (birthData != null) {
                year = Integer.parseInt(birthData[0]);
                month = Integer.parseInt(birthData[1]);
                day = Integer.parseInt(birthData[2]);
            }
//            String loginMailSubmit = "";
//            String mailMagaSubmit = "";
            //create call object

            Call<MemberInfoChange> call = apiService.changeMemberInfo(id,
                    password,
                    null,
//                    member.getCode(),
                    member.getName(),
                    member.getBlood(),
                    member.getArea(),
                    member.getMail(),
                    TextUtils.isEmpty(member.getMessage()) ? Define.FULL_WIDTH_SPACE : member.getMessage(),
//                    loginMailSubmit,
//                    mailMagaSubmit,
                    year,
                    month,
                    day,
                    member.getJob(),
                    member.getTypeWoman(),
                    member.getPet(),
                    member.getGeek(),
                    member.getOnlyOneWish(),
                    member.getEngrossed());

            // send call object
            call.enqueue(new Callback<MemberInfoChange>() {
                @Override
                public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                    // goto edit profile screen
                    if (response == null || response.body() == null) {
//                        if (isAlive) {
//                            bindData();
//                        }
                        return;
                    }
                    if (response.body().isSuccess()) {
                        ApiGeneralResponse changeInfoResponse = response.body().getChangeInfo();
                        if (changeInfoResponse.getSubmit() == Define.SUBMIT_OK) {
                            mRealMemberInfo = response.body();

                            mTvAge.setText(String.format("%s歳", mRealMemberInfo.getMember().getAge()));
                            String memberName = mRealMemberInfo.getMember().getName();
                            String message = mRealMemberInfo.getMember().getMessage();
                            if (TextUtils.isEmpty(message)) {
                                if (TextUtils.isEmpty(memberName)) {
                                    mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint), "NAMENAME"));
                                } else {
                                    mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint), memberName));
                                }
                            } else {
                                if (message.equals("　")) {
                                    mEditMessage.setText("");
                                } else {
                                    mEditMessage.setText(message);
                                }
                            }

                            if (isVisible()) {
                                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.update_profile_success), null);
                                if (!noticeDialog.isShowing()) {
                                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                                    if (loadDataFromProfile != null) {
                                        loadDataFromProfile.doMenuUpdate(mRealMemberInfo.getMember());
                                    }
                                }
                            }
                            // Update info
                            SettingManager.getInstance(mContext).saveUserInfo(mRealMemberInfo.getMember());
                            EventBus.getDefault().post(new PropertyChangedEvent(1,
                                    PropertyChangedEvent.TYPE_PROFILE_UPDATED));
                        } else {
                            String message = changeInfoResponse.getErrorMsg();
                            if (!TextUtils.isEmpty(message) && isVisible()) {
                                DialogBuilder.buildNoticeDialog(message, null).show(getFragmentManager(), TAG);
                            }
                        }
                    } else {
                        String errorMessage = response.body().getErrorMsg();
                        if (isVisible()) {
                            DialogBuilder.buildNoticeDialog(errorMessage, null).show(getFragmentManager(), TAG);
                        }
                    }

                }

                @Override
                public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "MemberInfoChange", t);
                    // TODO: dismiss loading mNoticeDialog and show errors
                    if (isAlive) {
//                        bindData();
                        DialogBuilder.buildNoticeDialog("不完全更新!", null).show(getFragmentManager(), TAG);
                    }
                }
            });
        }
    }

    @OnClick(R.id.edit_name)
    void doEditName(View view) {
        if (isEditName) {
//            mEditName.setEnabled(false);
//            mEditName.setBackgroundResource(android.R.color.transparent);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String name = mEditName.getText().toString();
                if (isContainsProhibitedWords(mEditName.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditName.setEnabled(true);
                    mEditName.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditName, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    DialogBuilder.buildNoticeDialog(getString(R.string.name_empty_message), null).show(getFragmentManager(), TAG);
                    mEditName.setEnabled(true);
                    mEditName.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditName, InputMethodManager.SHOW_IMPLICIT);
                    return;
                } else if (name.length() < 3) {
                    DialogBuilder.buildNoticeDialog(getString(R.string.profile_name_format), null).show(getFragmentManager(), TAG);
                    mEditName.setEnabled(true);
                    mEditName.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditName, InputMethodManager.SHOW_IMPLICIT);
                    return;
                } else if (!name.equals(mRealMemberInfo.getMember().getName())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditName.getText().toString())) {
                        mMemberInfoChange.getMember().setName(String.valueOf(mEditName.getText()));
                    }
//                    pushMemberInfo();
                }
            }
            isEditName = false;
        } else {
            mEditName.setEnabled(true);
            mEditName.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditName.requestFocus();
            isEditName = true;
        }
    }

    @OnClick(R.id.edit_age)
    void doEditAge(View view) {
        String[] birthData = getBirthData();
        int year = 1996;
        int month = 1;
        int day = 1;
        if (birthData != null) {
            year = Integer.parseInt(birthData[0]);
            month = Integer.parseInt(birthData[1]);
            day = Integer.parseInt(birthData[2]);
        }
        showCalendar(year, month, day);
    }

    @OnClick(R.id.edit_blood_group)
    void doEditBloodGroup(View view) {
        String blood = mMemberInfoChange.getMember().getBlood();
        if (!TextUtils.isEmpty(blood) && TextUtils.isDigitsOnly(blood)) {
            int bloodPos = Integer.valueOf(blood);
            DialogBuilder.showRadioDialog(mContext.getResources().getString(R.string.profile_blood), Define.BLOOD_GROUP, bloodPos, mContext, new DialogBuilder.OnClickRadioItemListener() {
                @Override
                public void onItemClick(int pos) {
                    String realBlood = mRealMemberInfo.getMember().getBlood();
                    if (!TextUtils.isEmpty(realBlood) && TextUtils.isDigitsOnly(realBlood)) {
                        int realBloodPos = Integer.valueOf(realBlood);
                        if (realBloodPos == pos)
                            return;
                    }
                    String blood = Define.BLOOD_GROUP[pos];
                    mTvBloodGroup.setText(blood);
                    if (mMemberInfoChange != null) {
                        resetMemberInfoChange();
                        mMemberInfoChange.getMember().setBlood(pos + "");
                        pushMemberInfo();
                    }
                }
            });
        }
    }

    @OnClick(R.id.edit_address_layout)
    void doEditAddress(View view) {
        String area = mMemberInfoChange.getMember().getArea();
        if (!TextUtils.isEmpty(area) && TextUtils.isDigitsOnly(area)) {
            int areaPos = Integer.valueOf(area);
            DialogBuilder.showRadioDialog(mContext.getResources().getString(R.string.profile_area), Define.MEMBER_AREA, areaPos, mContext, new DialogBuilder.OnClickRadioItemListener() {
                @Override
                public void onItemClick(int pos) {
                    String realArea = mRealMemberInfo.getMember().getArea();
                    if (!TextUtils.isEmpty(realArea) && TextUtils.isDigitsOnly(realArea)) {
                        int realAreaPos = Integer.valueOf(realArea);
                        if (realAreaPos == pos)
                            return;
                    }
                    String area = Define.MEMBER_AREA[pos];
                    mTvAddress.setText(area);
                    if (mMemberInfoChange != null) {
                        resetMemberInfoChange();
                        mMemberInfoChange.getMember().setArea(pos + "");
                        pushMemberInfo();
                    }
                }
            });
        }
    }

    @OnClick(R.id.edit_message)
    void doEditComment(View view) {
        if (isEditMessage) {
            mEditMessage.setEnabled(false);
            mEditMessage.setMinLines(1);
            mEditMessage.setBackgroundResource(android.R.color.transparent);
            mEditMessage.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditMessage.getText().toString();
                if (isContainsProhibitedWords(mEditMessage.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditMessage.setEnabled(true);
                    mEditMessage.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditMessage.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getMessage())
                        && !(message.equals("") && mRealMemberInfo.getMember().getMessage().equals("　"))) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditMessage.getText().toString())) {
                        mMemberInfoChange.getMember().setMessage(String.valueOf(mEditMessage.getText()));
                    }
                    pushMemberInfo();
                }
                if (message.equals("")) {
                    mEditMessage.setHint(String.format(getString(R.string.profile_comment_hint),
                            mRealMemberInfo.getMember().getName()));
                }
            }
            isEditMessage = false;
        } else {
            mEditMessage.setEnabled(true);
            mEditMessage.setMinLines(3);
            mEditMessage.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditMessage.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditMessage, InputMethodManager.SHOW_IMPLICIT);
            isEditMessage = true;
        }
    }

    @OnClick(R.id.edit_engrossed)
    void doEditEngrossed(View view) {
        mEditEngrossed.setVisibility(View.VISIBLE);
        if (isEditEngrossed) {
            mEditEngrossed.setEnabled(false);
            mEditEngrossed.setMinLines(1);
            mEditEngrossed.setBackgroundResource(android.R.color.transparent);
            mEditEngrossed.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditEngrossed.getText().toString();
                if (isContainsProhibitedWords(mEditEngrossed.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditEngrossed.setEnabled(true);
                    mEditEngrossed.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditEngrossed.requestFocus();
                    isEditEngrossed = !isEditEngrossed;
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getEngrossed())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditEngrossed.getText().toString())) {
                        mMemberInfoChange.getMember().setEngrossed(String.valueOf(mEditEngrossed.getText()));
                    }
                    pushMemberInfo();
                }
                if (message.isEmpty()) {
                    isEditEngrossed = false;
                    mEditEngrossed.setVisibility(View.GONE);
                } else {
                    mEditEngrossed.setVisibility(View.VISIBLE);
                }
            }
            isEditEngrossed = false;
        } else {
            mEditEngrossed.setEnabled(true);
            mEditEngrossed.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditEngrossed.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditEngrossed, InputMethodManager.SHOW_IMPLICIT);
            isEditEngrossed = true;
        }
    }

    @OnClick(R.id.edit_onlyOneWish)
    void doEditOnlyOneWish(View view) {
        mEditOnlyOneWish.setVisibility(View.VISIBLE);
        if (!mClickHandler.isClickable(view.getId())) {
            return;
        }
        if (isEditOnlyOneWish) {
            mEditOnlyOneWish.setEnabled(false);
            mEditOnlyOneWish.setMinLines(1);
            mEditOnlyOneWish.setBackgroundResource(android.R.color.transparent);
            mEditOnlyOneWish.setGravity(Gravity.LEFT);
            if (mMemberInfoChange != null) {
                String message = mEditOnlyOneWish.getText().toString();
                if (isContainsProhibitedWords(mEditOnlyOneWish.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditOnlyOneWish.setEnabled(true);
                    mEditOnlyOneWish.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditOnlyOneWish.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getOnlyOneWish())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditOnlyOneWish.getText().toString())) {
                        mMemberInfoChange.getMember().setOnlyOneWish(String.valueOf(mEditOnlyOneWish.getText()));
                    }
                    isBackToMenu = false;
                    pushMemberInfo();
                }
                if (message.isEmpty()) {
                    isEditOnlyOneWish = false;
                    mEditOnlyOneWish.setVisibility(View.GONE);
                } else {
                    mEditOnlyOneWish.setVisibility(View.VISIBLE);
                }
            }
            isEditOnlyOneWish = false;
        } else {
            mEditOnlyOneWish.setEnabled(true);
            mEditOnlyOneWish.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditOnlyOneWish.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditOnlyOneWish, InputMethodManager.SHOW_IMPLICIT);
            isEditOnlyOneWish = true;
        }
    }

    @OnClick(R.id.edit_pet)
    void doEditPet(View view) {
        mEditPet.setVisibility(View.VISIBLE);
        if (isEditPet) {
            mEditPet.setEnabled(false);
            mEditPet.setMinLines(1);
            mEditPet.setBackgroundResource(android.R.color.transparent);
            mEditPet.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditPet.getText().toString();
                if (isContainsProhibitedWords(mEditPet.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditPet.setEnabled(true);
                    mEditPet.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditPet.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getPet())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditPet.getText().toString())) {
                        mMemberInfoChange.getMember().setPet(String.valueOf(mEditPet.getText()));
                    }
                    isBackToMenu = false;
                    pushMemberInfo();
                }
                if (message.isEmpty()) {
                    isEditPet = false;
                    mEditPet.setVisibility(View.GONE);
                } else {
                    mEditPet.setVisibility(View.VISIBLE);
                }
            }
            isEditPet = false;
        } else {
            mEditPet.setEnabled(true);
            mEditPet.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditPet.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditPet, InputMethodManager.SHOW_IMPLICIT);
            isEditPet = true;
        }
    }

    @OnClick(R.id.edit_typeWoman)
    void doEditTypeWoman(View view) {
        mEditTypeWoman.setVisibility(View.VISIBLE);
        if (isEditTypeWoman) {
            mEditTypeWoman.setEnabled(false);
            mEditTypeWoman.setMinLines(1);
            mEditTypeWoman.setBackgroundResource(android.R.color.transparent);
            mEditTypeWoman.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditTypeWoman.getText().toString();
                if (isContainsProhibitedWords(mEditTypeWoman.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditTypeWoman.setEnabled(true);
                    mEditTypeWoman.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditTypeWoman.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getTypeWoman())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditTypeWoman.getText().toString())) {
                        mMemberInfoChange.getMember().setTypeWoman(String.valueOf(mEditTypeWoman.getText()));
                    }
                    isBackToMenu = false;
                    pushMemberInfo();
                }
                if (message.isEmpty()) {
                    isEditTypeWoman = false;
                    mEditTypeWoman.setVisibility(View.GONE);
                } else {
                    mEditTypeWoman.setVisibility(View.VISIBLE);
                }
            }
            isEditTypeWoman = false;
        } else {
            mEditTypeWoman.setEnabled(true);
            mEditTypeWoman.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditTypeWoman.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditTypeWoman, InputMethodManager.SHOW_IMPLICIT);
            isEditTypeWoman = true;
        }
    }

    @OnClick(R.id.edit_geek)
    void doEditGeek(View view) {
        mEditGeek.setVisibility(View.VISIBLE);
        if (isEditGeek) {
            mEditGeek.setEnabled(false);
            mEditGeek.setMinLines(1);
            mEditGeek.setBackgroundResource(android.R.color.transparent);
            mEditGeek.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditGeek.getText().toString();
                if (isContainsProhibitedWords(mEditGeek.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditGeek.setEnabled(true);
                    mEditGeek.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditGeek.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getGeek())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditGeek.getText().toString())) {
                        mMemberInfoChange.getMember().setGeek(String.valueOf(mEditGeek.getText()));
                    }
                    isBackToMenu = false;
                    pushMemberInfo();
                }
                if (message.isEmpty()) {
                    isEditGeek = false;
                    mEditGeek.setVisibility(View.GONE);
                } else {
                    mEditGeek.setVisibility(View.VISIBLE);
                }
            }
            isEditGeek = false;
        } else {
            mEditGeek.setEnabled(true);
            mEditGeek.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditGeek.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditGeek, InputMethodManager.SHOW_IMPLICIT);
            isEditGeek = true;
        }
    }

    @OnClick(R.id.edit_job)
    void doEditJob(View view) {
        mEditJob.setVisibility(View.VISIBLE);
        if (isEditJob) {
            mEditJob.setEnabled(false);
            mEditJob.setMinLines(1);
            mEditJob.setBackgroundResource(android.R.color.transparent);
            mEditJob.setGravity(Gravity.LEFT);
            if (!mClickHandler.isClickable(view.getId())) {
                return;
            }
            if (mMemberInfoChange != null) {
                String message = mEditJob.getText().toString();
                if (isContainsProhibitedWords(mEditJob.getText().toString())) {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.profile_registration_prohibited_words), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                    mEditJob.setEnabled(true);
                    mEditJob.setBackgroundResource(R.drawable.bg_edit_text_profile);
                    mEditJob.requestFocus();
                    return;
                }
                if (!message.equals(mRealMemberInfo.getMember().getJob())) {
                    resetMemberInfoChange();
                    if (!isContainsProhibitedWords(mEditJob.getText().toString())) {
                        mMemberInfoChange.getMember().setJob(String.valueOf(mEditJob.getText()));
                    }
                    pushMemberInfo();
                    if (message.isEmpty()) {
                        isEditJob = false;
                        mEditJob.setVisibility(View.GONE);
                    } else {
                        mEditJob.setVisibility(View.VISIBLE);
                    }
                }
            }
            isEditJob = false;
        } else {
            mEditJob.setEnabled(true);
            mEditJob.setBackgroundResource(R.drawable.bg_edit_text_profile);
            mEditJob.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditJob, InputMethodManager.SHOW_IMPLICIT);
            isEditJob = true;
        }
    }

    @OnClick(R.id.btn_change_email_password)
    void openChangeEmailPass(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE, mMemberInfoChange);
        BaseFragment fragment = ChangeEmailPasswordFragment.newInstance(bundle);
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof MainMenuContainer) {
            ((MainMenuContainer) parentFragment).replaceFragment(fragment);
        }
    }

    @OnClick(R.id.member_image_layout)
    void openEditImageProfile(View view) {
//        Toast.makeText(mMainActivity, "We are implementing this feature", Toast.LENGTH_SHORT).show();
        DialogSelectImage dialogSelectImage = new DialogSelectImage();
        dialogSelectImage.setSelectListener(new DialogSelectImage.OnOptionSelectListener() {
            @Override
            public void onSelectFromGallery() {
                Intent intent = new Intent(mContext, GalleryActivity.class);
                intent.setAction(GalleryActivity.PICK_IMAGE);
                startActivityForResult(intent, PICK_IMAGE);
            }

            @Override
            public void onSelectTakePicture() {
                requestTakePicture();
            }
        });

        Helpers.showDialogFragment(getChildFragmentManager(), dialogSelectImage);
    }

    @OnClick(R.id.tv_logout)
    void gotoLogout(View view) {
        final DialogLogOut dialogLogOut = new DialogLogOut();
        dialogLogOut.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                dialogLogOut.dismiss();
                doLogOut();
            }

            @Override
            public void onCancelClick() {
                dialogLogOut.dismiss();
                getMemberInfoChange(TYPE_GOTO_REGISTER_MAIL);
            }
        });
        Helpers.showDialogFragment(getChildFragmentManager(), dialogLogOut);
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
                            openNewFragment(ProfileFragment.newInstance(bundle));
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

    public void openNewFragment(BaseFragment fragment) {
        Fragment parentFragment = getParentFragment();
        if (fragment != null && parentFragment != null && parentFragment instanceof MainMenuContainer) {
            ((MainMenuContainer) parentFragment).replaceFragment(fragment);
        }
    }

    private void showRetryDialog(final int type) {
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
    }

    private void doLogOut() {
        // Remove all notification
        NotificationManager nMgr = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        // Update icon
        EventBus.getDefault().post(new PropertyChangedEvent(1,
                PropertyChangedEvent.TYPE_LOG_OUT));
        //clear data
        SettingManager.getInstance(getActivity()).clearStoredData();
        //route new intent
        Intent intent = new Intent(getActivity(), SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    /**
     * Reset mMemberInfoChange before set new value and pushMemberInfo
     */
    private void resetMemberInfoChange() {
        if (mMemberInfoChange == null || mRealMemberInfo == null)
            return;
        mMemberInfoChange.getMember().setName(mRealMemberInfo.getMember().getName());
        mMemberInfoChange.getMember().setAge(mRealMemberInfo.getMember().getAge());
        mMemberInfoChange.getMember().setBirth(mRealMemberInfo.getMember().getBirth());
        mMemberInfoChange.getMember().setBlood(mRealMemberInfo.getMember().getBlood());
        mMemberInfoChange.getMember().setArea(mRealMemberInfo.getMember().getArea());
        mMemberInfoChange.getMember().setMessage(mRealMemberInfo.getMember().getMessage());

        mMemberInfoChange.getMember().setJob(mRealMemberInfo.getMember().getJob());
        mMemberInfoChange.getMember().setPet(mRealMemberInfo.getMember().getPet());
        mMemberInfoChange.getMember().setGeek(mRealMemberInfo.getMember().getGeek());
        mMemberInfoChange.getMember().setOnlyOneWish(mRealMemberInfo.getMember().getOnlyOneWish());
        mMemberInfoChange.getMember().setEngrossed(mRealMemberInfo.getMember().getEngrossed());
        mMemberInfoChange.getMember().setTypeWoman(mRealMemberInfo.getMember().getTypeWoman());
    }

    private void enableEditText(TextView editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showCalendar(int year, int month, int day) {
        MyDatePickerDialog dialog = new MyDatePickerDialog(mContext,
                R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if (mMemberInfoChange != null && datePicker.isShown()) {
                    String realBirth = mRealMemberInfo.getMember().getBirth();
                    String birth = String.format(Locale.US, "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    if (realBirth != null && realBirth.equals(birth)) {
                        return;
                    }
                    resetMemberInfoChange();
                    mMemberInfoChange.getMember().setBirth(birth);
                    pushMemberInfo();
                }
            }
        }, year, month - 1, day);

        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.str_completion), dialog);
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), dialog);
        dialog.getDatePicker().setMaxDate(HimecasUtils.calculateMinDate());
        dialog.show();
    }

    @Override
    public String getTitle() {
        return getString(R.string.my_profile_title);
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(true);
        }
    }

    /*
     * For fix app force close because of activity has been destroyed
     * I do not know why it work
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestTakePicture() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            callTakePicture();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_STORAGE_PERMISSION);
        }
    }

    private void callTakePicture() {
        File candyFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CANDY_Images");
        if (!candyFile.exists()) {
            if (!candyFile.mkdir()) {
                return;
            }
        }

        File file = new File(candyFile, ("IMG_" + System.currentTimeMillis() + ".jpg"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        mImageDestination = Uri.fromFile(file);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageDestination = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        }else {
            mImageDestination = Uri.fromFile(file);
        }
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageDestination);
        startActivityForResult(i, TAKE_IMAGE);
    }

    private void uploadAvatar(@NonNull final File file) {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.NoticeDialog dialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server),
                    getString(R.string.retry));
            dialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    uploadAvatar(file);
                }

                @Override
                public void onCancelClick() {

                }
            });
            getChildFragmentManager().beginTransaction().add(dialog, dialog.getClass().getSimpleName()).commitAllowingStateLoss();
            return;
        }
        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

        // gather your request parameters
        RequestParams params = new RequestParams();
        try {
            params.put(Define.Fields.FIELD_ID, member.getId());
            params.put(Define.Fields.FIELD_PASS, member.getPass());
            params.put(Define.Fields.FIELD_IMAGE, file);
        } catch (FileNotFoundException ex) {
            RkLogger.w(TAG, "doSendMessage exception: ", ex);
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unknown exception: ", ex);
        }
        Helpers.showCircleProgressDialog(getActivity());
        // send request
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60 * 1000);
        String baseUrl = SettingManager.getInstance(mContext).getConfig().getWebviewBaseUrl();
        client.post(baseUrl + "member/profile/pictureUpload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                Helpers.dismissCircleProgressDialog();
                try {
                    String response = new String(bytes, Define.CONST_UTF_8);
                    Gson gson = new GsonBuilder().setLenient().create();
                    ApiUploadAvatarResponse apiResponse = gson.fromJson(response, ApiUploadAvatarResponse.class);
                    if (null != apiResponse) {
                        // handle success response
                        DialogBuilder.NoticeDialog noticeDialog;
                        if (apiResponse.isSuccess()) {
                            noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.update_profile_success), null);
                            Glide.with(getActivity())
                                    .load(file.getAbsolutePath())
                                    .asBitmap()
                                    .listener(new RequestListener<String, Bitmap>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                            mMemberImage.setImageResource(R.drawable._icons_noimage);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            mMemberImage.setImageBitmap(resource);
                                            return true;
                                        }
                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(mMemberImage.getWidth(), mMemberImage.getWidth());
                            // Update info
                            HimecasUtils.updateMemberInfoInSilent(mContext);
                            if (loadDataFromProfile != null) {
                                loadDataFromProfile.doAvatarUpdate(file);
                            }
                        } else {
                            noticeDialog = DialogBuilder.buildNoticeDialog(apiResponse.getErrorMessage(), null);
                        }
                        if (null != noticeDialog) {
                            Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                Helpers.dismissCircleProgressDialog();
                RkLogger.e(TAG, "Failed: ", throwable);
                RkLogger.e(TAG, "Failed: " + statusCode);
                /// 0 is network error
                if (0 == statusCode) {
                    DialogBuilder.NoticeDialog dialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server),
                            getString(R.string.retry));
                    dialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                        @Override
                        public void onOkClick(Object object) {
                            uploadAvatar(file);
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                    Helpers.showDialogFragment(getChildFragmentManager(), dialog);
                } else {
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(R.string.update_profile_unsuccess), null);
                    Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_STORAGE_PERMISSION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                callTakePicture();
            } else {
                Utils.showToast(mContext, R.string.no_permissions_capture);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (PICK_IMAGE == requestCode) {
                final String path = data.getData().getPath();
                final Uri uri = Uri.fromFile(new File(path));
                File fileToUpload = new File(uri.getPath());
                uploadAvatar(fileToUpload);
                return;
            } else if (TAKE_IMAGE == requestCode) {
                // Start intent crop image
                Intent intent = new Intent(getActivity(), CropImageActivity.class);
                intent.putExtra(CropImageActivity.ARG_IMAGE_URI, mImageDestination);
                intent.putExtra(CropImageActivity.ARG_CROP_MODE, CropImageView.CropMode.SQUARE);
                startActivityForResult(intent, PICK_IMAGE);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadProhibitedWorlds() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(mContext, Define.API.API_PROBIHITED_WORDS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProhibitedWords = new ArrayList<>();
                try {
                    String response = new String(responseBody, Define.CONST_UTF_8);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Define.Fields.FIELD_PROHIBITED)) {
                        JSONArray prohibitedWorldJA = jsonObject.getJSONArray(Define.Fields.FIELD_PROHIBITED);
                        for (int i = 0; i < prohibitedWorldJA.length(); i++) {
                            String word = prohibitedWorldJA.getString(i);
                            if (!TextUtils.isEmpty(word)) {
                                mProhibitedWords.add(word);
                            }
                        }
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public boolean isContainsProhibitedWords(String word) {
        if (word != null) {
            if (null != mProhibitedWords && !mProhibitedWords.isEmpty()) {
                for (int i = 0; i < mProhibitedWords.size(); i++) {
                    if (word.contains(mProhibitedWords.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public interface LoadDataFromProfile {
        void doMenuUpdate(MemberInfoChange.Member member);
        void doAvatarUpdate(File file);
    }

    public void setLoadDataFromProfile(LoadDataFromProfile loadDataFromProfile) {
        this.loadDataFromProfile = loadDataFromProfile;
    }
}
