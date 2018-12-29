package com.candy.android.custom.gallery;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by namhv on 29/10/2016.
 * GalleryItemDecoration
 */

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public GalleryItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
    }
}
