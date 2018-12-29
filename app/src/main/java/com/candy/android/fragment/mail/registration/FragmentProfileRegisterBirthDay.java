package com.candy.android.fragment.mail.registration;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.MyDatePickerDialog;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;

import org.joda.time.DateTimeComparator;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Favo
 * Created on 02/11/2016.
 */

public final class FragmentProfileRegisterBirthDay extends FragmentProfileRegister {

    private static final String TAG = "IDK-FragmentProfileRegisterBirthDay";

    private TextView mBirthDayInput;
    private int mBirthYear = Define.FIX_BIRTH_YEAR;
    private int mBirthMonth = 0;
    private int mBirthDay = Define.FIX_BIRTHDAY;

    private DatePickerDialog.OnDateSetListener mBirthdayDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                mBirthDayInput.setClickable(true);

                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Date selectedDate = calendar.getTime();

                if (DateTimeComparator.getDateOnlyInstance().compare(selectedDate, today) <= 0) {
                    mBirthDayInput.setText(getString(R.string.fragment_profile_register_birthday_format,
                            year, monthOfYear + 1, dayOfMonth));
                    mBirthDay = dayOfMonth;
                    mBirthYear = year;
                    mBirthMonth = monthOfYear;
                } else {
                    //selected date is after today
                    Toast.makeText(getActivity(), R.string.text_invalid_birthday, Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning:", ex);
            }
        }
    };

    public FragmentProfileRegisterBirthDay() {
        mStep = SET_BIRTH_DAY_STEP;
    }

    public static FragmentProfileRegisterBirthDay newInstance() {
        return new FragmentProfileRegisterBirthDay();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_register_birthday, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBirthDayInput = (TextView) view.findViewById(R.id.input_birthday);
        mBirthDayInput.setOnClickListener(this);

        loadRegistrationData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_birthday:
                showDatePickerDialog();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void saveDataAndProceed() {
        RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
        registrationData.setYear(mBirthYear);
        registrationData.setMonth(mBirthMonth);
        registrationData.setDay(mBirthDay);
        goNextStep();
    }

    @Override
    protected void loadRegistrationData() {
        if (RegistrationDataV2.hasData()) {
            RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
            mBirthYear = registrationData.getYear();
            mBirthMonth = registrationData.getMonth();
            mBirthDay = registrationData.getDay();
        }

        mBirthDayInput.setText(getString(R.string.fragment_profile_register_birthday_format,
                mBirthYear, mBirthMonth + 1, mBirthDay));
    }

    private void showDatePickerDialog() {
        //disable
        mBirthDayInput.setClickable(false);

        // Create a new instance of DatePickerDialog
        MyDatePickerDialog pickerDialog = new MyDatePickerDialog(getActivity(),
                R.style.DatePickerDialogTheme,
                mBirthdayDialogListener,
                mBirthYear,
                mBirthMonth,
                mBirthDay);


        pickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.str_completion), pickerDialog);
        pickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel), pickerDialog);
        pickerDialog.getDatePicker().setMaxDate(HimecasUtils.calculateMinDate());

        pickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //re-enable
                mBirthDayInput.setClickable(true);
            }
        });

        pickerDialog.show();
    }

}
