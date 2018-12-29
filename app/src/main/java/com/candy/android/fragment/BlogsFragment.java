package com.candy.android.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.CustomFragmentPagerAdapter;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.BlogCategoriesResponse;
import com.candy.android.model.BlogCategory;
import com.candy.android.utils.RkLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogsFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> mListTitle;
    private List<Fragment> mListFragment;
    private CustomFragmentPagerAdapter mAdapter;
    private String mScreenTitle;
    public boolean isUpActionDisplay;

    public BlogsFragment() {
        // Required empty public constructor
    }

    public static BlogsFragment newInstance() {

        Bundle args = new Bundle();

        BlogsFragment fragment = new BlogsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenTitle = mContext.getResources().getString(R.string.blog_list_screen_name);
        isUpActionDisplay = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RkLogger.d("Check life cycle >> ", "blogs oncreateview");

        return inflater.inflate(R.layout.fragment_blogs, container, false);
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(isUpActionDisplayVisible());
        }
    }

    @Override
    protected boolean isUpActionDisplayVisible() {
        return isUpActionDisplay;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = view.findViewById(R.id.blog_tabs);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
        mViewPager = view.findViewById(R.id.blog_list_viewpager);
        setupViewpager();
        getBlogCategories();
    }

    @Override
    public String getTitle() {
        return mScreenTitle;
    }

    public void setmScreenTitle(String mScreenTitle) {
        this.mScreenTitle = mScreenTitle;
    }

    public void setUpActionDisplay(boolean upActionDisplay) {
        isUpActionDisplay = upActionDisplay;
    }

    TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            restoreHightLightTab();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            int tabPos = tab.getPosition();
            BlogsListFragment blogsListFragment = (BlogsListFragment) mAdapter.getItem(tabPos);
            if (blogsListFragment.isBlogsOfAPerformer()) {
                blogsListFragment.reloadData();
                if (mMainActivity != null) {
                    mMainActivity.setScreenTitle(getString(R.string.blog_list_screen_name));
                    mMainActivity.setScreenUpActionDisplay(false);
                }
                setUpActionDisplay(false);
                setmScreenTitle(getString(R.string.blog_list_screen_name));
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            int tabPos = tab.getPosition();
            BlogsListFragment blogsListFragment = (BlogsListFragment) mAdapter.getItem(tabPos);
            if (blogsListFragment.isBlogsOfAPerformer()) {
                restoreHightLightTab();
                if (mMainActivity != null) {
                    mMainActivity.setScreenTitle(getString(R.string.blog_list_screen_name));
                    mMainActivity.setScreenUpActionDisplay(false);
                }
                setUpActionDisplay(false);
                setmScreenTitle(getString(R.string.blog_list_screen_name));
            }
            blogsListFragment.reloadData();
        }
    };

    public void setupViewpager() {
        RkLogger.d("Check life cycle >> ", "blog setupVIewpager");
        mListFragment = new ArrayList<>();
        for (int i = 0; i < Define.BLOG_CATEGORIES.length; i++) {
            mListFragment.add(BlogsListFragment.newInstance(BlogsListFragment.PERFORMERCODE_NOVALUE, Define.BLOG_CATEGORIES[i]));
        }
        mListTitle = new ArrayList<>(Arrays.asList(Define.BLOG_CATEGORIES_TITLE));

        mAdapter = new CustomFragmentPagerAdapter(mListFragment, mListTitle, getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mListFragment.size() - 1);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void getBlogCategories() {
        ApiInterface apiInterface = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        apiInterface.getBlogCategories().enqueue(new Callback<BlogCategoriesResponse>() {
            @Override
            public void onResponse(Call<BlogCategoriesResponse> call, Response<BlogCategoriesResponse> response) {
                if(response == null || response.body() == null) return;

                List<BlogCategory> blogCategories = response.body().getBlog().getCategories();
                int size = blogCategories.size();
                if(size == 0) return;

                mListFragment.clear();
                mListTitle.clear();

                for(int i = 0; i < size; i++) {
                    BlogCategory blogCategory = blogCategories.get(i);
                    mListFragment.add(BlogsListFragment.newInstance(BlogsListFragment.PERFORMERCODE_NOVALUE, blogCategory.getName()));
                    mListTitle.add(blogCategory.getJpName());
                }

                mViewPager.setAdapter(mAdapter);
                mViewPager.setOffscreenPageLimit(mListFragment.size() - 1);
                mTabLayout.setupWithViewPager(mViewPager);
            }

            @Override
            public void onFailure(Call<BlogCategoriesResponse> call, Throwable t) {

            }
        });
    }

    protected void openNewFragment(BaseFragment fragment) {
        Fragment parentFragment = getParentFragment();
        if (fragment != null && parentFragment != null && parentFragment instanceof BlogContainerFragment) {
            ((BlogContainerFragment) parentFragment).replaceFragment(fragment);
        }
    }

    public boolean isBlogsOfPerformer() {
        if (mAdapter != null && mViewPager != null) {
            return ((BlogsListFragment) mAdapter.getItem(mViewPager.getCurrentItem())).isBlogsOfAPerformer();
        }
        return false;
    }

    public void reloadCurrentBlogsList() {
        if (mAdapter != null && mViewPager != null) {
            ((BlogsListFragment) (mAdapter.getItem(mViewPager.getCurrentItem()))).reloadData();
        }
    }

    public void removeHightLightTab() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blog_page_tab_color));
        mTabLayout.setTabTextColors(getResources().getColor(R.color.mission_title_color), getResources().getColor(R.color.blog_page_tab_color));
        //wrapTabIndicatorToTitle(mTabLayout, 40, 65);
    }

    public void restoreHightLightTab() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blog_page_tab_color));
        mTabLayout.setTabTextColors(getResources().getColor(R.color.mission_title_color), getResources().getColor(R.color.blog_page_tab_color));
        //wrapTabIndicatorToTitle(mTabLayout, 40, 65);
    }

    public void onClickOtherBlogsOnCurrentPage(int performerCode, String performerName) {
        if (mAdapter != null && mViewPager != null) {
            ((BlogsListFragment) mAdapter.getItem(mViewPager.getCurrentItem())).onClickOtherBlogs(performerCode, performerName);
            setUpActionDisplay(true);
        }
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }
}
