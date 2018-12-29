package com.candy.android.custom.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.candy.android.activity.MainActivity;
import com.candy.android.configs.Define;
import com.candy.android.fragment.SMSAuthenticatePhoneFragment;
import com.candy.android.fragment.menu.MissionFragmentContainer;
import com.candy.android.fragment.menu.ProfileFragment;
import com.candy.android.fragment.webpage.WebFragment;

/**
 * Created by Administrator on 8/22/2017.
 */

public class MainViewPager extends ViewPager {
    public static boolean isTouch = false;

    public static boolean isTouch() {
        return isTouch;
    }

    public static void setTouch(boolean touch) {
        isTouch = touch;
    }

    public MainViewPager(Context context) {
        super(context);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        if (MainActivity.mViewPager.getCurrentItem() == 4 && (Define.webFragment instanceof WebFragment ||
//                Define.webFragment instanceof MissionFragmentContainer ||
//                Define.webFragment instanceof SMSAuthenticatePhoneFragment ||
//                Define.webFragment instanceof ProfileFragment) && Define.webFragment != null) {
//            return isTouch;
//        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (MainActivity.mViewPager.getCurrentItem() == 4 && (Define.webFragment instanceof WebFragment ||
//                Define.webFragment instanceof MissionFragmentContainer ||
//                Define.webFragment instanceof SMSAuthenticatePhoneFragment ||
//                Define.webFragment instanceof ProfileFragment) && Define.webFragment != null) {
//            return isTouch;
//        }
        return super.onTouchEvent(event);
    }
}
