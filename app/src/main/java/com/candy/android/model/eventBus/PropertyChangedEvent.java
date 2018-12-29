package com.candy.android.model.eventBus;

/**
 * @author Favo
 * Created on 11/11/2016.
 */

public class PropertyChangedEvent extends BaseEvent {
    public static final int TYPE_POINT_CHANGED = 0;
    public static final int TYPE_NEW_MESSAGE_CHANGED = 1;
    public static final int TYPE_NEW_MESSAGE_INCREASE = 2;
    public static final int TYPE_LOG_OUT = 3;
    public static final int TYPE_MISSION_CHANGE = 4;
    public static final int TYPE_POINT_CHANGED_VIDEO  = 5;
    public static final int TYPE_PROFILE_UPDATED = 6;

    private int mValue;
    private int mType;

    public PropertyChangedEvent(int value, int type) {
        mValue = value;
        mType = type;
    }

    public int getValue() {
        return mValue;
    }

    public int getType() {
        return mType;
    }

}
