package com.candy.android.custom.views;

import android.content.Context;
import android.util.AttributeSet;

import com.candy.android.R;

import java.util.ArrayList;


/**
 * Created by brianhoang on 4/4/2018.
 */

public class TabItemView extends android.support.v7.widget.AppCompatImageView{
    public static final int STATE_LIVE_STAR_STATE = R.attr.state_candy_tab_state;
    private ArrayList<Integer> customStates = new ArrayList<Integer>();

    public TabItemView(Context context) {

        super(context);

    }

    public TabItemView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        init();

    }

    public TabItemView(Context context, AttributeSet attrs) {

        super(context, attrs);

        init();

    }

    private void init() {

        if (null == customStates) customStates = new ArrayList<Integer>();

    }

    public void resetCustomStates() {

        customStates.clear();

    }

    public void addCustomState(int state) {

        if (!customStates.contains(state)) {

            customStates.add(state);

        }

    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {

        init();

        int customStateSize = customStates.size();

        if (customStateSize > 0) {

            final int[] drawableState = super.onCreateDrawableState(extraSpace + customStateSize);

            int[] stateArray = new int[customStateSize];

            int i = 0;

            for (Integer state : customStates) {

                stateArray[i] = state;

                i++;

            }

            mergeDrawableStates(drawableState, stateArray);

            return drawableState;

        } else {

            return super.onCreateDrawableState(extraSpace);

        }

    }
}
