package com.candy.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

/**
 * Created by quannt on 03/11/2016.
 * Des: Blog girl screen
 */

public class BlogContainerFragment extends BaseFragmentContainer {

    public static BlogContainerFragment newInstance() {

        Bundle args = new Bundle();

        BlogContainerFragment fragment = new BlogContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        RkLogger.d("Check screen >> ", "onResume");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RkLogger.d("Check onSaveInstance >> ", "onSaveInstance");
    }

    @Override
    public String getTitle() {
        return "Blog Container";
    }

    public void replaceFragment(BaseFragment fragment) {
        if (null != fragment) {
            Helpers.replaceFragment(getChildFragmentManager(), R.id.blog_container, fragment);
        }
    }

    public boolean doSelfBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        } else {
            return false;
        }
    }

    public void init() {
        BlogsFragment blogsListFragment = BlogsFragment.newInstance();
        Helpers.replaceFragment(getChildFragmentManager(), R.id.blog_container, blogsListFragment);
    }

    public void onClickOtherBlogs(int performerCode, String performerName) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment blogsFragment = fragmentManager.findFragmentByTag(BlogsFragment.class.getSimpleName());
        if (blogsFragment != null && blogsFragment instanceof BlogsFragment) {
            ((BlogsFragment) blogsFragment).onClickOtherBlogsOnCurrentPage(performerCode, performerName);
        }
    }
}
