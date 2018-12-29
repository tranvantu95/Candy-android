package com.candy.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.utils.RkLogger;

/**
 * Created by Quybro on 27/02/2016.
 * DialogBuilder
 */
public class DialogBuilder {
    public static NumberPickerDialog buildNumberPickerDialog(int currentValue, int min, int max, @NonNull String[] displayValues, OnClickListener listener) {
        if (currentValue == Integer.MIN_VALUE) {
            currentValue = 1;
        }
        NumberPickerDialog numberPickerDialog = NumberPickerDialog.newInstance(currentValue, max, min, displayValues);
        numberPickerDialog.setOnClickListener(listener);
        return numberPickerDialog;
    }

    /**
     * Create new ConfirmDialog
     * knv edited
     *
     * @param message  Message to be shown
     * @param listener Listener
     * @return New instance of ConfirmDialog
     */
    public static ConfirmDialog buildConfirmDialog(String title, String message, OnClickListener listener) {
        final ConfirmDialog confirmDialog = ConfirmDialog.newInstance(title, message);
        if (null == listener) {
            confirmDialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (confirmDialog != null) {
                        confirmDialog.dismiss();
                    }
                }

                @Override
                public void onCancelClick() {
                    if (confirmDialog != null) {
                        confirmDialog.dismiss();
                    }
                }
            });
        } else {
            confirmDialog.setOnClickListener(listener);
        }
        return confirmDialog;
    }

    /**
     * knv added
     */
    public static ConfirmDialog buildConfirmDialog(String message, OnClickListener listener) {
        return buildConfirmDialog(null, message, listener);
    }

    public static NoticeDialog buildNoticeDialog(String message, OnClickListener listener) {
        final NoticeDialog noticeDialog = NoticeDialog.newInstance(message);
        if (null == listener) {
            noticeDialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (noticeDialog != null) {
                        noticeDialog.dismiss();
                    }
                }

                @Override
                public void onCancelClick() {
                    if (noticeDialog != null) {
                        noticeDialog.dismiss();
                    }
                }
            });
        } else {
            noticeDialog.setOnClickListener(listener);
        }
        return noticeDialog;
    }

    public static NoticeDialog buildNoticeDialog(String message, String buttonText, OnClickListener listener) {
        final NoticeDialog noticeDialog = NoticeDialog.newInstance(message, buttonText);
        if (null == listener) {
            noticeDialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (noticeDialog != null) {
                        noticeDialog.dismiss();
                    }
                }

                @Override
                public void onCancelClick() {
                    if (noticeDialog != null) {
                        noticeDialog.dismiss();
                    }
                }
            });
        } else {
            noticeDialog.setOnClickListener(listener);
        }
        return noticeDialog;
    }

    public static NoticeDialog2 buildNoticeDialog2(String message, OnClickListener listener) {
        final NoticeDialog2 noticeDialog2 = NoticeDialog2.newInstance(message);
        if (null == listener) {
            noticeDialog2.setOnClickListener(new OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (noticeDialog2 != null) {
                        noticeDialog2.dismiss();
                    }
                }

                @Override
                public void onCancelClick() {
                    if (noticeDialog2 != null) {
                        noticeDialog2.dismiss();
                    }
                }
            });
        } else {
            noticeDialog2.setOnClickListener(listener);
        }
        return noticeDialog2;
    }

    public static NoticeDialog2 buildNoticeDialog2(String message, String buttonText, OnClickListener listener) {
        final NoticeDialog2 noticeDialog2 = NoticeDialog2.newInstance(message, buttonText);
        if (null == listener) {
            noticeDialog2.setOnClickListener(new OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (noticeDialog2 != null) {
                        noticeDialog2.dismiss();
                    }
                }

                @Override
                public void onCancelClick() {
                    if (noticeDialog2 != null) {
                        noticeDialog2.dismiss();
                    }
                }
            });
        } else {
            noticeDialog2.setOnClickListener(listener);
        }
        return noticeDialog2;
    }

    /**
     * ConfirmDialog
     */
    public static class NoticeDialog extends DialogFragment implements View.OnClickListener {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_BUTTON_TEXT = "button";
        private static final String ARG_CANCELED_ON_TOUCH_OUTSIDE = "cancel_outside";

        private OnClickListener onClickListener;
        private OnDialogBackPress onDialogBackPress;

        private String mMessage, mButtonText;

        public static NoticeDialog newInstance(String message) {
            NoticeDialog noticeDialog = new NoticeDialog();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            noticeDialog.setArguments(bundle);
            return noticeDialog;
        }

        public static NoticeDialog newInstance(String message, String button) {
            NoticeDialog noticeDialog = new NoticeDialog();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            bundle.putString(ARG_BUTTON_TEXT, button);
            noticeDialog.setArguments(bundle);
            return noticeDialog;
        }

        public static NoticeDialog newInstance(String message, String button, boolean canceledOnTouchOutSide) {
            NoticeDialog noticeDialog = newInstance(message, button);

            Bundle bundle = noticeDialog.getArguments();
            bundle.putBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE, canceledOnTouchOutSide);
            noticeDialog.setArguments(bundle);
            return noticeDialog;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMessage = bundle.getString(ARG_MESSAGE);
                mButtonText = bundle.getString(ARG_BUTTON_TEXT);
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new Dialog(getActivity(), getTheme()) {
                @Override
                public void onBackPressed() {
                    super.onBackPressed();
                    if (onDialogBackPress != null) {
                        onDialogBackPress.onDialogBackPress();
                    }
                }
            };
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle arg = getArguments();
            if (null != arg && null != getDialog()) {
                boolean cancelOutside = arg.getBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE);
                getDialog().setCanceledOnTouchOutside(cancelOutside);
            }
            return inflater.inflate(R.layout.layout_dialog_notice, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView tvOk = (TextView) view.findViewById(R.id.btn_ok);
            tvOk.setOnClickListener(this);
            if (!TextUtils.isEmpty(mButtonText)) {
                tvOk.setText(mButtonText);
            }

            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(mMessage);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnDialogBackPress(OnDialogBackPress onDialogBackPress) {
            this.onDialogBackPress = onDialogBackPress;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_ok:
                    if (onClickListener != null) {
                        if (isShowing()) {
                            dismiss();
                        }
                        onClickListener.onOkClick(null);
                        RkLogger.d("Check retry >>", "onclick");
                    }
                    break;
            }
        }

        public boolean isShowing() {
            return getDialog() != null
                    && getDialog().isShowing();
        }

        @Override
        public int show(FragmentTransaction transaction, String tag) {
            return super.show(transaction, tag);
        }
    }

    public static class NoticeDialog2 extends DialogFragment implements View.OnClickListener {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_BUTTON_TEXT = "button";
        private static final String ARG_CANCELED_ON_TOUCH_OUTSIDE = "cancel_outside";

        private OnClickListener onClickListener;
        private OnDialogBackPress onDialogBackPress;

        private String mMessage, mButtonText;

        public static NoticeDialog2 newInstance(String message) {
            NoticeDialog2 noticeDialog2 = new NoticeDialog2();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            noticeDialog2.setArguments(bundle);
            return noticeDialog2;
        }

        public static NoticeDialog2 newInstance(String message, String button) {
            NoticeDialog2 noticeDialog2 = new NoticeDialog2();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            bundle.putString(ARG_BUTTON_TEXT, button);
            noticeDialog2.setArguments(bundle);
            return noticeDialog2;
        }

        public static NoticeDialog2 newInstance(String message, String button, boolean canceledOnTouchOutSide) {
            NoticeDialog2 noticeDialog2 = newInstance(message, button);

            Bundle bundle = noticeDialog2.getArguments();
            bundle.putBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE, canceledOnTouchOutSide);
            noticeDialog2.setArguments(bundle);
            return noticeDialog2;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyCustomDialog);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMessage = bundle.getString(ARG_MESSAGE);
                mButtonText = bundle.getString(ARG_BUTTON_TEXT);
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new Dialog(getActivity(), getTheme()) {
                @Override
                public void onBackPressed() {
                    super.onBackPressed();
                    if (onDialogBackPress != null) {
                        onDialogBackPress.onDialogBackPress();
                    }
                }
            };
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle arg = getArguments();
            if (null != arg && null != getDialog()) {
                boolean cancelOutside = arg.getBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE);
                getDialog().setCanceledOnTouchOutside(cancelOutside);
            }
            return inflater.inflate(R.layout.layout_dialog_notice_2, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView tvOk = (TextView) view.findViewById(R.id.btn_ok);
            tvOk.setOnClickListener(this);
            if (!TextUtils.isEmpty(mButtonText)) {
                tvOk.setText(mButtonText);
            }

            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(mMessage);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnDialogBackPress(OnDialogBackPress onDialogBackPress) {
            this.onDialogBackPress = onDialogBackPress;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_ok:
                    if (onClickListener != null) {
                        if (isShowing()) {
                            dismiss();
                        }
                        onClickListener.onOkClick(null);
                        RkLogger.d("Check retry >>", "onclick");
                    }
                    break;
            }
        }

        public boolean isShowing() {
            return getDialog() != null
                    && getDialog().isShowing();
        }

        @Override
        public int show(FragmentTransaction transaction, String tag) {
            return super.show(transaction, tag);
        }
    }

    public static class NoticeTitleDialog extends DialogFragment implements View.OnClickListener {

        private static final String ARGS_TITLE = "title";
        private static final String ARGS_MESSAGE = "message";

        private String mTitle, mMessage;
        private TextView mTvTitle, mTvMessage, mTvOk;
        private OnClickListener onClickListener;

        public static NoticeTitleDialog newInstance(String title, String message) {

            Bundle args = new Bundle();
            args.putString(ARGS_TITLE, title);
            args.putString(ARGS_MESSAGE, message);

            NoticeTitleDialog fragment = new NoticeTitleDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMessage = bundle.getString(ARGS_MESSAGE);
                mTitle = bundle.getString(ARGS_TITLE);
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_dialog_notice_title, container, false);
            mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            mTvTitle.setText(mTitle);
            mTvMessage = (TextView) rootView.findViewById(R.id.tv_message);
            mTvMessage.setText(mMessage);
            mTvOk = (TextView) rootView.findViewById(R.id.btn_ok);
            mTvOk.setOnClickListener(this);
            return rootView;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_ok:
                    dismiss();
                    if (onClickListener != null) {
                        onClickListener.onOkClick(null);
                    }
                    break;
            }
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public boolean isShowing() {
            return getDialog() != null
                    && getDialog().isShowing();
        }
    }

    public static class SMSTitleDialog extends DialogFragment implements View.OnClickListener {

        private static final String ARGS_TITLE = "title";
        private static final String ARGS_MESSAGE = "message";

        private String mTitle, mMessage;
        private TextView mTvTitle, mTvMessage, mTvOk;
        private OnClickListener onClickListener;

        public static SMSTitleDialog newInstance(String title, String message) {

            Bundle args = new Bundle();
            args.putString(ARGS_TITLE, title);
            args.putString(ARGS_MESSAGE, message);

            SMSTitleDialog fragment = new SMSTitleDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SMSDialog);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMessage = bundle.getString(ARGS_MESSAGE);
                mTitle = bundle.getString(ARGS_TITLE);
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_dialog_notice_title_2, container, false);
            mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            mTvTitle.setText(mTitle);
            mTvMessage = (TextView) rootView.findViewById(R.id.tv_message);
            mTvMessage.setText(mMessage);
            mTvOk = (TextView) rootView.findViewById(R.id.btn_ok);
            mTvOk.setOnClickListener(this);
            return rootView;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_ok:
                    dismiss();
                    if (onClickListener != null) {
                        onClickListener.onOkClick(null);
                    }
                    break;
            }
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public boolean isShowing() {
            return getDialog() != null
                    && getDialog().isShowing();
        }
    }

    /**
     * ConfirmDialog
     */
    public static class ConfirmDialog extends DialogFragment implements View.OnClickListener {
        private static final String ARG_MESSAGE = "message";
        private static final String ARG_TITLE = "title";

        private OnClickListener onClickListener;


        private String mMessage;
        private String mTitle;
        private String mOkString;

        private TextView mTvOk;

        /**
         * knv added
         *
         * @param message
         * @return
         */
        public static ConfirmDialog newInstance(String title, String message) {
            ConfirmDialog noticeDialog = new ConfirmDialog();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_TITLE, title);
            bundle.putString(ARG_MESSAGE, message);
            noticeDialog.setArguments(bundle);
            return noticeDialog;
        }

        public static ConfirmDialog newInstance(String message) {
            return newInstance(null, message);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMessage = bundle.getString(ARG_MESSAGE);
                mTitle = bundle.getString(ARG_TITLE);
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.layout_dialog_confirm, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.findViewById(R.id.btn_cancel).setOnClickListener(this);
            mTvOk = (TextView) view.findViewById(R.id.btn_ok);
            if (!TextUtils.isEmpty(mOkString)) {
                mTvOk.setText(mOkString);
            }
            mTvOk.setOnClickListener(this);

            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(mMessage);

            /** knv added */
            tvMessage = (TextView) view.findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(mTitle)) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(mTitle);
            } else {
                tvMessage.setVisibility(View.GONE);
            }
        }

        /**
         * knv added
         */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new Dialog(getActivity(), getTheme()) {
                @Override
                public void onBackPressed() {
                    if (null != onClickListener) {
                        onClickListener.onCancelClick();
                    }
                }
            };
        }

        /**
         * knv added
         */
        @Override
        public void onCancel(DialogInterface dialog) {
            if (onClickListener != null) {
                onClickListener.onCancelClick();
            }
        }

        //        @Override
//        public void onStart() {
//            super.onStart();
//            Dialog dialog = getDialog();
//            if (dialog != null && dialog.getWindow() != null) {
//                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            }
//        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancel:  // FIXME: 21/10/2016 QuyNV
                    if (onClickListener != null) {
                        onClickListener.onCancelClick();
                    }
                    break;
                case R.id.btn_ok:
                    if (onClickListener != null) {
                        onClickListener.onOkClick(null);
                    }
                    break;
            }
        }

        public void setTextOkButton(String okString) {
            mOkString = okString;
        }
    }

    public static class NumberPickerDialog extends DialogFragment implements View.OnClickListener {
        private static final String CURRENT_VALUE = "current_value";
        private static final String MAX_VALUE = "max_value";
        private static final String MIN_VALUE = "min_value";
        private static final String DISPLAY_VALUE = "display_value";
        private OnClickListener onClickListener;
        private int currentValue;
        private int maxValue;
        private int minValue;
        String[] displayValues;

        private NumberPicker numberPicker;

        public NumberPickerDialog() {
        }

        public static NumberPickerDialog newInstance(int currentValue, int max, int min, @NonNull String[] displayValues) {
            NumberPickerDialog fragment = new NumberPickerDialog();
            Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_VALUE, currentValue);
            bundle.putInt(MAX_VALUE, max);
            bundle.putInt(MIN_VALUE, min);
            if (displayValues.length == max - min + 1) {
                bundle.putStringArray(DISPLAY_VALUE, displayValues);
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (bundle != null) {
                currentValue = bundle.getInt(CURRENT_VALUE);
                maxValue = bundle.getInt(MAX_VALUE);
                minValue = bundle.getInt(MIN_VALUE);
                displayValues = bundle.getStringArray(DISPLAY_VALUE);
            }
            setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        public void setOnClickListener(OnClickListener listener) {
            onClickListener = listener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.number_picker, container);
            numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
            numberPicker.setMaxValue(maxValue);
            numberPicker.setMinValue(minValue);
            numberPicker.setValue(currentValue);
//            numberPicker.setWrapSelectorWheel(false);
            if (null != displayValues) {
                numberPicker.setDisplayedValues(displayValues);
            }
            view.findViewById(R.id.action_cancel).setOnClickListener(this);
            view.findViewById(R.id.action_ok).setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.action_cancel:
                    if (onClickListener != null) {
                        onClickListener.onCancelClick();
                    }
                    break;
                case R.id.action_ok:
                    if (onClickListener != null) {
                        onClickListener.onOkClick(numberPicker.getValue());
                    }
                    break;
            }
        }
    }

    public interface OnClickListener {
        void onOkClick(Object object);

        void onCancelClick();
    }

    public interface OnDialogBackPress {
        void onDialogBackPress();
    }

    public static void showRadioDialog(String title, String[] items, int checkedItem, Context context, final OnClickRadioItemListener listener) {
        AlertDialog.Builder alertDiaBuilder = new AlertDialog.Builder(context);
        if (title != null) {
            alertDiaBuilder.setTitle(title);
        }
        alertDiaBuilder.setSingleChoiceItems(new ArrayAdapter(context,
                R.layout.custom_dialog_title_rtl, items), checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onItemClick(i);
                dialogInterface.dismiss();
            }
        });
        alertDiaBuilder.create().show();
    }

    public static void showAlertDialog(String message, Context context) {
        RkLogger.d("Check show alert :", "show alert");
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_notice, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        TextView tvOk = (TextView) dialogView.findViewById(R.id.btn_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public interface OnClickRadioItemListener {
        void onItemClick(int pos);
    }
}
