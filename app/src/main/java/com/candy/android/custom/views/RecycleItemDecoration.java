package com.candy.android.custom.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by namhv on 29/10/2016.
 * GalleryItemDecoration
 */

public class RecycleItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public RecycleItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}
