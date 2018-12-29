package com.candy.android.custom.image_crop.image_crop.callback;

import android.graphics.Bitmap;

public interface CropCallback extends ErrorCallback {
    void onSuccess(Bitmap cropped);

    void onError();
}
