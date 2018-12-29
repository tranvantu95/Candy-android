package com.candy.android.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;

/**
 * Show selection for choosing image to upload avatar
 * Created by namhv on 10/12/2016.
 */
public class DialogSelectImage extends DialogFragment implements View.OnClickListener {
    private OnOptionSelectListener mSelectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tv_select_from_gallery).setOnClickListener(this);
        view.findViewById(R.id.tv_take_picture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_from_gallery:
                dismissAllowingStateLoss();
                if (null != mSelectListener) {
                    mSelectListener.onSelectFromGallery();
                }
                break;

            case R.id.tv_take_picture:
                dismissAllowingStateLoss();
                if (null != mSelectListener) {
                    mSelectListener.onSelectTakePicture();
                }
                break;
        }
    }

    public void setSelectListener(OnOptionSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface OnOptionSelectListener {
        void onSelectFromGallery();

        void onSelectTakePicture();
    }
}
