package com.candy.android.model.eventBus;

import com.candy.android.model.PerformerFavorite;
import com.candy.android.model.PerformerFootprint;

/**
 * Created by quannt on 27/10/2016.
 * EventBus: update favorite
 */

public class UpdateFavoritePerformerEvent extends BaseEvent {

    public static final int TYPE_ADD_FAVORITE = 0;
    public static final int TYPE_DEL_FAVORITE = 1;
    private PerformerFavorite mPerformerfavorite;

    private PerformerFootprint mPerformerfavoriteFootprint;
    private int mActionType;

    public UpdateFavoritePerformerEvent(PerformerFavorite performerFavorite, int type) {
        mPerformerfavorite = performerFavorite;
        mActionType = type;
    }

    public UpdateFavoritePerformerEvent(PerformerFootprint performerFavorite, int type) {
        mPerformerfavoriteFootprint = performerFavorite;
        mActionType = type;
    }

    public UpdateFavoritePerformerEvent(int type) {
        //Empty constructortype
        mActionType = type;
    }

    public PerformerFavorite getmPerformerfavorite() {
        return mPerformerfavorite;
    }

    public void setmPerformerfavorite(PerformerFavorite mPerformerfavorite) {
        this.mPerformerfavorite = mPerformerfavorite;
    }

    public PerformerFootprint getmPerformerfavoriteFootprint() {
        return mPerformerfavoriteFootprint;
    }

    public void setmPerformerfavoriteFootprint(PerformerFootprint mPerformerfavoriteFootprint) {
        this.mPerformerfavoriteFootprint = mPerformerfavoriteFootprint;
    }

    public int getmActionType() {
        return mActionType;
    }

    public void setmActionType(int mActionType) {
        this.mActionType = mActionType;
    }
}
