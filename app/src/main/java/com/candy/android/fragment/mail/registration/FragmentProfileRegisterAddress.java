package com.candy.android.fragment.mail.registration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

/**
 * @author Favo
 * Created on 02/11/2016.
 */
public class FragmentProfileRegisterAddress extends FragmentProfileRegister {

    private DialogFragment mPickerDialogFragment;
    private TextView mInputArea;

    private int mArea;

    public FragmentProfileRegisterAddress() {
        mStep = SET_ADDRESS_STEP;
    }

    public static FragmentProfileRegisterAddress newInstance() {
        return new FragmentProfileRegisterAddress();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_register_address, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputArea = (TextView) view.findViewById(R.id.input_address);
        mInputArea.setOnClickListener(this);

        RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();

        TextView itemView = (TextView) view.findViewById(R.id.tv_register_address_question);
        itemView.setText(getString(R.string.fragment_profile_register_address_question, registrationData.getAgeStr()));

        loadRegistrationData();
    }

    private void onShowArea() {
        int currentValue = mArea;
        Helpers.showDialogFragment(getChildFragmentManager(), getAreaDialog(currentValue, 0, Define.MEMBER_AREA.length - 1, Define.MEMBER_AREA));
    }

    private DialogFragment getAreaDialog(int current, int min, int max, String[] displayValues) {
        mPickerDialogFragment = DialogBuilder.buildNumberPickerDialog(current, min, max, displayValues, new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                onSetArea(String.valueOf(object));
                mPickerDialogFragment.dismiss();
            }

            @Override
            public void onCancelClick() {
                mPickerDialogFragment.dismiss();
            }
        });
        return mPickerDialogFragment;
    }

    private void onSetArea(String area) {
        RkLogger.d("onSetArea", "Area: " + area);
        if (!TextUtils.isEmpty(area)) {
            mInputArea.setText(Helpers.getStringOfIndexFromArray(area, Define.MEMBER_AREA));
            try {
                mArea = Integer.parseInt(area);
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning:", ex);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_address:
                onShowArea();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void saveDataAndProceed() {
        RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
        registrationData.setArea(mArea);
        goNextStep();
    }

    @Override
    protected void loadRegistrationData() {
        if (RegistrationDataV2.hasData()) {
            RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
            mArea = registrationData.getArea();
        }
        mInputArea.setText(Helpers.getStringAreaFromArray(mArea, Define.MEMBER_AREA));
    }
}
