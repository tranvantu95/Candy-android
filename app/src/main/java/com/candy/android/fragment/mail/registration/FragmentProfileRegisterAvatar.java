package com.candy.android.fragment.mail.registration;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.candy.android.R;
import com.candy.android.custom.gallery.Utils;
import com.candy.android.custom.gallery.activities.GalleryActivity;
import com.candy.android.custom.image_crop.CropImageActivity;
import com.candy.android.custom.image_crop.image_crop.CropImageView;
import com.candy.android.dialog.DialogSelectImage;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

import java.io.File;
import java.io.IOException;

/**
 * @author Favo
 * Created on 03/11/2016.
 */

public class FragmentProfileRegisterAvatar extends FragmentProfileRegister {
    // Request permission code
    private static final int CAMERA_STORAGE_PERMISSION = 1;

    public static final int PICK_IMAGE = 100;
    public static final int TAKE_IMAGE = 101;

    private ImageView mChangeAvatarImg;
    private Uri mImageDestination;
    private Uri mImageToCapture;

    public FragmentProfileRegisterAvatar() {
        mStep = SET_AVATAR_STEP;
    }

    public static FragmentProfileRegisterAvatar newInstance() {
        return new FragmentProfileRegisterAvatar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_register_avatar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChangeAvatarImg = (ImageView) view.findViewById(R.id.action_change_avatar);

        mChangeAvatarImg.setOnClickListener(this);

        loadRegistrationData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_change_avatar:
                actionChangeAvatar();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void saveDataAndProceed() {
        RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
        registrationData.setUriAvatar(mImageDestination);
        goNextStep();
    }

    @Override
    protected void loadRegistrationData() {
        if (RegistrationDataV2.hasData()) {
            RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();
            mImageDestination = registrationData.getUriAvatar();
        }

        if (mImageDestination != null && !TextUtils.isEmpty(mImageDestination.getPath())) {
            setAvatar(mImageDestination);
        }
    }

    private void setAvatar(@NonNull Uri image) {
        try {
            mNextBtn.setText(R.string.next);
            mCameraButton.setVisibility(View.GONE);
            Glide.with(this)
                    .load(image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(mChangeAvatarImg);
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    private void actionChangeAvatar() {
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (Activity.RESULT_OK == resultCode) {
//            if (PICK_IMAGE == requestCode) {
//                final String path = data.getData().getPath();
//                mUriAvatar = Uri.fromFile(new File(path));
//                RkLog.d(TAG, "PICK_IMAGE path=" + path + ", uri=" + mUriAvatar);
//                Glide.with(this)
//                        .load(mUriAvatar)
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .into(mChangeAvatarImg);
//                return;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

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
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!file.delete() || !file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageToCapture = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        } else {
            mImageToCapture = Uri.fromFile(file);
        }
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageToCapture);
        startActivityForResult(i, TAKE_IMAGE);
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
                mImageDestination = Uri.fromFile(new File(path));
                setAvatar(mImageDestination);
                return;
            } else if (TAKE_IMAGE == requestCode) {
                // Start intent crop image
                Intent intent = new Intent(getActivity(), CropImageActivity.class);
                intent.putExtra(CropImageActivity.ARG_IMAGE_URI, mImageToCapture);
                intent.putExtra(CropImageActivity.ARG_CROP_MODE, CropImageView.CropMode.SQUARE);
                startActivityForResult(intent, PICK_IMAGE);
//                uploadAvatar(fileToUpload);
                return;
            }
        } else if (Activity.RESULT_CANCELED == resultCode) {
            mImageToCapture = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
