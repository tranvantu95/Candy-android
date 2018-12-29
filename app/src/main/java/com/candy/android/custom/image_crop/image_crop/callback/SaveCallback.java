package com.candy.android.custom.image_crop.image_crop.callback;


import android.net.Uri;

public interface SaveCallback extends ErrorCallback {
    void onSuccess(Uri outputUri);

    void onError();
}
