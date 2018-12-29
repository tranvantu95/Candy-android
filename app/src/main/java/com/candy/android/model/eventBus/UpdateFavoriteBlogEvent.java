package com.candy.android.model.eventBus;

/**
 * Created by quannt on 06/12/2016.
 * Des: event to update favorite blog
 */

public class UpdateFavoriteBlogEvent extends BaseEvent {

    private int mPosition;
    private int mNiceCount;

    public UpdateFavoriteBlogEvent(int position, int niceCount) {
        mPosition = position;
        mNiceCount = niceCount;
    }

    public int getmPosition() {
        return mPosition;
    }

    public int getmNiceCount() {
        return mNiceCount;
    }
}
