package com.candy.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.MainViewPager;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.fragment.webpage.WebFragment;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

import java.lang.reflect.Field;

/**
 * Menu tab
 */
public class MainMenuContainer extends BaseFragmentContainer {
    private static final String TAG = MainMenuContainer.class.getSimpleName();

    FragmentManager fragmentManager;

    public MainMenuContainer() {
        // Required empty public constructor
    }

    /**
     * Create new instance of fragment MainMenuContainer.
     */
    public static MainMenuContainer newInstance() {
        return new MainMenuContainer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getChildFragmentManager();
        replaceFragmentAndAddToBackStack(MenuFragment.newInstance());

        FragmentManager fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fm_menu_container);
        if(null != f){
            if(f instanceof WebFragment){
                MainViewPager.setTouch(false);
                Define.webFragment = (WebFragment) f;
            }
            else {
                MainViewPager.setTouch(true);
                Define.webFragment = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RkLogger.d("Check screen >> ", "onResume");
    }

    public void replaceFragment(BaseFragment fragment) {
        if (null != fragment) {
            Helpers.replaceFragment(getChildFragmentManager(), R.id.fm_menu_container, fragment);
        }
    }

    /*
     * For fix app force close because of activity has been destroyed
     * I do not know why it work
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void replaceFragmentAndAddToBackStack(BaseFragment fragment) {
        if (null != fragment) {
            Helpers.replaceFragmentAndAddToBackStack(getChildFragmentManager(), R.id.fm_menu_container, fragment);
        }
    }

    public void removeCurrentFragment() {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().commitAllowingStateLoss();
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.menu_main_screen_title);
    }

    //old
//    public boolean doSelfBackPressed() {
//        if (null != fragmentManager && fragmentManager.getBackStackEntryCount() > 1) {
//            RkLogger.e(TAG, "doSelfBackPressed");
//            Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.fm_menu_container);
//            if (currentFragment != null && currentFragment instanceof WebFragment) {
//                if (!((WebFragment) currentFragment).goBack()) {
//                    getChildFragmentManager().popBackStack();
//                }
//            } else {
//                getChildFragmentManager().popBackStack();
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    public int doSelfBackPressed() {
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(WebFragment.class.getSimpleName());
        if(currentFragment != null && currentFragment instanceof WebFragment) {
            return ((WebFragment) currentFragment).goBack() ? Define.CAN_GO_BACK : goBack();
        } else {
            return goBack();
        }
    }

    private int goBack() {
        if (fragmentManager != null ) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count>1){
                fragmentManager.popBackStackImmediate();
                return fragmentManager.getBackStackEntryCount() > 1 ? Define.CAN_GO_BACK : Define.MAIN_MENU;
            }
        }
        return Define.LAST_BACK;
    }

    public int popToFirstFragment() {
        if(fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 1) {
                for (int i = 0; i < count - 1; i++) {
                    fragmentManager.popBackStack();
                }
                return Define.MAIN_MENU;
            }
        }
        Define.webFragment = null;
        MainViewPager.setTouch(true);
        return Define.LAST_BACK;
    }

    public MenuFragment getMenuFragment(){
        return (MenuFragment) fragmentManager.findFragmentByTag(MenuFragment.class.getSimpleName());
    }

}
