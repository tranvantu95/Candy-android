package com.candy.android.custom.gallery.callbacks;


import com.candy.android.custom.gallery.models.Directory;
import com.candy.android.custom.gallery.models.Medium;

/**
 * Created by namhv on 10/18/16.
 * OnMediaClickListener
 */

public interface OnMediaClickListener {
    void onMediumClick(Medium medium);

    void onDirectoryClick(Directory medium);
}
