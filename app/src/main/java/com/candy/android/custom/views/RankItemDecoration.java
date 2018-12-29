package com.candy.android.custom.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 8/29/2017.
 */

public class RankItemDecoration extends DividerItemDecoration {
    private RecyclerViewPositionHelper recyclerViewPositionHelper;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public RankItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        recyclerViewPositionHelper = RecyclerViewPositionHelper.createHelper(parent);
        int index = recyclerViewPositionHelper.findFirstVisibleItemPosition();
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}