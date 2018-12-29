package com.candy.android.fragment.mail.registration;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.candy.android.R;
import com.candy.android.activity.ProfileRegistrationV2Activity;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.fragment.mail.FragmentMailContainer;
import com.candy.android.fragment.mail.FragmentMailLine;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.MemberInfoUpdateEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;
import org.joda.time.Years;

/**
 * @author Favo
 * Created on 02/11/2016.
 */
public abstract class FragmentProfileRegister extends BaseFragment implements View.OnClickListener {
    protected static final String TAG = "IDK-FragmentProfileRegister";

    public static final int SET_NAME_STEP = 1;
    public static final int SET_BIRTH_DAY_STEP = SET_NAME_STEP + 1;
    public static final int SET_ADDRESS_STEP = SET_BIRTH_DAY_STEP + 1;
    public static final int SET_AVATAR_STEP = SET_ADDRESS_STEP + 1;
    public static final int CONFIRMING_STEP = SET_AVATAR_STEP + 1;

    private static int mPerformerCode;
    private static int mPerformerAge;
    private static String mPerformerOName;
    private static String mPerformerImage;

    private DialogBuilder.NoticeDialog mNoticeDialog;

    protected int mStep;

    protected Button mNextBtn;
    protected ImageView mCameraButton;

    @Override
    public String getTitle() {
        return getString(R.string.fragment_profile_registration_title);
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(isUpActionDisplayVisible());
            // set keyboard mode
            mMainActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    /**
     * Child must call this to have on next click event
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mNextBtn = (Button) view.findViewById(R.id.btn_next);
        mCameraButton = (ImageView) view.findViewById(R.id.camera_icon);

        if (mNextBtn != null) {
            mNextBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                saveDataAndProceed();
                break;
        }
    }

    /**
     * when next step occurred, handle this callback to persist  the step's data
     */
    protected abstract void saveDataAndProceed();

    /**
     * when child fragment want to retrieve old input
     */
    protected abstract void loadRegistrationData();

    public void goToStep(int step) {
        BaseFragmentContainer mailContainer = (BaseFragmentContainer) getParentFragment();
        if (mailContainer == null) {
            RkLogger.e(TAG, "Error, don't have reference to mail container");
        }

        goToStep(mailContainer, step);
    }

    public void goNextStep() {
        BaseFragmentContainer mailContainer = (BaseFragmentContainer) getParentFragment();
        if (mailContainer == null) {
            RkLogger.e(TAG, "Error, don't have reference to mail container");
        }

        goToStep(mailContainer, mStep + 1);
    }

    /**
     * Fragment container version
     */
    public static void goToStep(BaseFragmentContainer fragmentContainer, int step) {

        switch (step) {
            case SET_NAME_STEP:
                fragmentContainer.gotoFragment(FragmentProfileRegisterName.newInstance());
                break;
            case SET_BIRTH_DAY_STEP:
                fragmentContainer.gotoFragment(FragmentProfileRegisterBirthDay.newInstance());
                break;
            case SET_ADDRESS_STEP:
                fragmentContainer.gotoFragment(FragmentProfileRegisterAddress.newInstance());
                break;
            case SET_AVATAR_STEP:
                fragmentContainer.gotoFragment(FragmentProfileRegisterAvatar.newInstance());
                break;
            case CONFIRMING_STEP:
                fragmentContainer.gotoFragment(FragmentProfileRegisterConfirming.newInstance());
                break;
            default:
                Toast.makeText(fragmentContainer.getActivity(), "There is no next step", Toast.LENGTH_SHORT)
                        .show();
        }
    }

    public static void storeReference(int performerCode, String name, int age, String image) {
        mPerformerCode = performerCode;
        mPerformerOName = name;
        mPerformerAge = age;
        mPerformerImage = image;
    }

    protected void showDialog(String message) {
        boolean dialogShown = mNoticeDialog != null && mNoticeDialog.getDialog() != null
                && mNoticeDialog.isShowing();

        if (!dialogShown) {
            mNoticeDialog = DialogBuilder.buildNoticeDialog(message, null);
            Helpers.showDialogFragment(getChildFragmentManager(), mNoticeDialog);
        }
    }

    protected void onProfileRegisterSuccess() {
        updateMemberInfo();

        // reset registrationDataV2 after update
        if (RegistrationDataV2.hasData()) {
            RegistrationDataV2.destroy();
        }

        BaseFragmentContainer parentFragment = (BaseFragmentContainer) getParentFragment();
        if (parentFragment == null) {
            RkLogger.e(TAG, "Error, don't have reference to mail container");
        } else {
            if (parentFragment instanceof FragmentMailContainer) {
                parentFragment.gotoFragment(FragmentMailLine.newInstance(mPerformerCode,
                        mPerformerOName,
                        mPerformerAge,
                        mPerformerImage));
            } else if (parentFragment instanceof ProfileRegistrationV2Activity.ProfileRegistrationV2FragmentContainer) {
                ((ProfileRegistrationV2Activity.ProfileRegistrationV2FragmentContainer) parentFragment).onProfileRegisterSuccess();
            }
        }
        EventBus.getDefault().post(new MemberInfoUpdateEvent());
    }

    private void updateMemberInfo() {
        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        final RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();

        if (memberInformation != null && registrationData != null) {
            try {
                SettingManager.getInstance(getActivity()).setIsNameSet(true);
                memberInformation.setAge(Integer.valueOf(registrationData.getAgeStr()));
                memberInformation.setArea(registrationData.getArea());
                memberInformation.setName(registrationData.getName());
                memberInformation.setProfileImageUrl(String.valueOf(registrationData.getUriAvatar()));

                SettingManager.getInstance(getActivity()).save(memberInformation);
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning: ", ex);
            }
        }
    }

    /**
     * data structure for registered member
     */
    public static class RegistrationDataV2 implements Parcelable {
        private static RegistrationDataV2 instance;

        String name;
        int year = Define.FIX_BIRTH_YEAR;
        int month = 0;
        int day = 1;
        int area;
        Uri mUriAvatar;

        public static RegistrationDataV2 getInstance() {
            if (instance == null) {
                instance = new RegistrationDataV2();
            }

            return instance;
        }

        public static boolean hasData() {
            return instance != null;
        }

        public static void destroy() {
            instance = null;
        }

        private RegistrationDataV2() {
            //no-op
        }

        RegistrationDataV2(Parcel in) {
            name = in.readString();
            year = in.readInt();
            month = in.readInt();
            day = in.readInt();
            area = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(year);
            dest.writeInt(month);
            dest.writeInt(day);
            dest.writeInt(area);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RegistrationDataV2> CREATOR = new Creator<RegistrationDataV2>() {
            @Override
            public RegistrationDataV2 createFromParcel(Parcel in) {
                return new RegistrationDataV2(in);
            }

            @Override
            public RegistrationDataV2[] newArray(int size) {
                return new RegistrationDataV2[size];
            }
        };

        public String getName() {
            return name;
        }

        int getYear() {
            return year;
        }

        int getMonth() {
            return month;
        }

        int getDay() {
            return day;
        }

        public int getArea() {
            return area;
        }

        public void setName(String name) {
            this.name = name;
        }

        void setYear(int year) {
            this.year = year;
        }

        void setMonth(int month) {
            this.month = month;
        }

        void setDay(int day) {
            this.day = day;
        }

        public void setArea(int area) {
            this.area = area;
        }

        String getAgeStr() {
            LocalDate birthday = new LocalDate(year, month + 1, day);
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birthday, now);
            return String.valueOf(age.getYears());
        }

        void setUriAvatar(Uri uriAvatar) {
            mUriAvatar = uriAvatar;
        }

        Uri getUriAvatar() {
            return mUriAvatar;
        }

        @Override
        public String toString() {
            return name + ", " + area + ", " + getAgeStr() + ", " + mUriAvatar;
        }
    }
}
