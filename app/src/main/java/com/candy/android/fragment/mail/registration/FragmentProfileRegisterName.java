package com.candy.android.fragment.mail.registration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.ProfileRegistrationV2Activity;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.utils.Helpers;

import java.util.Random;

/**
 * @author Favo
 * Created on 01/11/2016.
 */

public final class FragmentProfileRegisterName extends FragmentProfileRegister {

    private EditText mInputName;

    private String[] mLastNames;
    private String[] mFirstNames;

    public FragmentProfileRegisterName() {
        mStep = SET_NAME_STEP;
    }

    public static FragmentProfileRegisterName newInstance() {
        return new FragmentProfileRegisterName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_register_name, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLastNames = getResources().getStringArray(R.array.last_names);
        mFirstNames = getResources().getStringArray(R.array.first_names);

        TextView mRandom_name = (TextView) view.findViewById(R.id.tv_random_name);
        mInputName = (EditText) view.findViewById(R.id.edt_name);

        mRandom_name.setOnClickListener(this);

        loadRegistrationData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_random_name:
                makeRandomName();
                break;
            default:
                super.onClick(view);
                break;

        }
    }

    @Override
    protected void saveDataAndProceed() {
        hideSoftKeyboard();
        String inputName = mInputName.getText().toString();
        if (TextUtils.isEmpty(inputName)) {
            showDialog(getString(R.string.fragment_profile_register_str_name_not_entered));
        } else if (inputName.length() < 3) {
            showDialog(getString(R.string.profile_name_format));
        } else {
            // Check prohibited worlds
            boolean isContainProhibitedWorld = false;
            Activity parentActivity = getActivity();
            if (parentActivity instanceof ProfileRegistrationV2Activity) {
                isContainProhibitedWorld = ((ProfileRegistrationV2Activity) parentActivity).isContainsProhibitedWorlds(inputName);
            }

            if (isContainProhibitedWorld) {
                // Show dialog
                DialogBuilder.NoticeDialog2 noticeDialog = DialogBuilder.buildNoticeDialog2(getString(R.string.profile_registration_prohibited_words), null);
                Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
            } else {
                RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
                registrationData.setName(mInputName.getText().toString());
                goNextStep();
            }
        }
    }

    @Override
    protected void loadRegistrationData() {
        if (RegistrationDataV2.hasData()) {
            RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
            mInputName.setText(registrationData.getName());
        }
    }

    private void makeRandomName() {
        Random random = new Random();
        String firstName = mFirstNames[random.nextInt(mFirstNames.length - 1)];
        String lastName = mLastNames[random.nextInt(mLastNames.length - 1)];
        mInputName.setText(getString(R.string.first_last_name, firstName, lastName));
    }

    private void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
