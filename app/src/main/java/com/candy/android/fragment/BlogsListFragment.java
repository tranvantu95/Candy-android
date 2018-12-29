package com.candy.android.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.adapter.BlogAdapter;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.BlogsListResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BlogData;
import com.candy.android.model.eventBus.UpdateBlockPerformerEvent;
import com.candy.android.model.eventBus.UpdateFavoriteBlogEvent;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BlogAdapter.OnBlogClickListener {

    private SwipeRefreshLayout mBlogLayout;
    private RecyclerView mBlogsListView;
    private BlogAdapter mBlogAdapter;
    private List<BlogData> mAllBlogDatas;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mNoContentLayout;

    private int mPage = 1;

    /**
     * -1: all
     */
    private int mPerformerCode = PERFORMERCODE_NOVALUE;
    private String mPerformerName;
    private String mCategory;
    //    private boolean isTablet;
    private int mTotalBlogsCount;

    /**
     * true: when click other blogs in blog list
     */
    private boolean isBlogsOfAPerformer;

    public static final int PAGE_LIMIT = 5;
    private static final int PAGE_START = 0;
    public static final int PERFORMERCODE_NOVALUE = -1;
    public static final String FIELD_OS = "Android";
    public static final String FIELD_VERSION = "1";

    //List category of a performer. From PerformerProfile
    public static final String CATEGORY_PERFORMER = "category_performer";

    public static final String PERFORMER_CODE = "performerCode";
    public static final String CATEGORY = "category";
    public static final String PERFORMER_NAME = "performerName";
    private static final String TAG = "BlogListFragment";

    public BlogsListFragment() {
        // Required empty public constructor
    }

    public static BlogsListFragment newInstance(int performerCode, String category) {

        Bundle args = new Bundle();
        args.putInt(PERFORMER_CODE, performerCode);
        args.putString(CATEGORY, category);
        BlogsListFragment fragment = new BlogsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static BlogsListFragment newInstance(int performerCode, String category, String performerName) {

        Bundle args = new Bundle();
        args.putInt(PERFORMER_CODE, performerCode);
        args.putString(CATEGORY, category);
        args.putString(PERFORMER_NAME, performerName);
        BlogsListFragment fragment = new BlogsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RkLogger.d("Check isBlogOfAPerformer: ", "oncreate" + "/" + this.hashCode());

        Bundle args = getArguments();
        if (args != null) {
            mPerformerCode = args.getInt(PERFORMER_CODE, PERFORMERCODE_NOVALUE);
            mCategory = args.getString(CATEGORY, "");
            mPerformerName = args.getString(PERFORMER_NAME, "");
        }
        mAllBlogDatas = new ArrayList<>();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blogs_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mCategory.equals(CATEGORY_PERFORMER)) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getResources().getDimensionPixelOffset(R.dimen.performer_profile_bottom_height));
        }
        mBlogLayout = (SwipeRefreshLayout) view.findViewById(R.id.swr_blog);
        mBlogLayout.setOnRefreshListener(this);
        mBlogsListView = (RecyclerView) view.findViewById(R.id.blogs_list);
        mBlogsListView.setHasFixedSize(true);
        mNoContentLayout = (RelativeLayout) view.findViewById(R.id.no_content_layout);

//        isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
//        if(isTablet) {
//            mLayoutManager  = new GridLayoutManager(mContext, 2);
//        } else {
        mLayoutManager = new LinearLayoutManager(mContext);
//        }
        mBlogsListView.setLayoutManager(mLayoutManager);
        if (mCategory.equals(CATEGORY_PERFORMER)) {
            mBlogLayout.setBackgroundColor(getResources().getColor(R.color.common_background_color_3));
            mBlogAdapter = new BlogAdapter(mContext, mAllBlogDatas, mBlogsListView, mPerformerCode, true);
        } else {
            mBlogAdapter = new BlogAdapter(mContext, mAllBlogDatas, mBlogsListView, mPerformerCode, false);
        }
        mBlogAdapter.setBlogListener(this);
        mBlogAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mAllBlogDatas.add(null);
                mBlogAdapter.notifyItemInserted(mAllBlogDatas.size() - 1);
                loadData(mPage + 1);
            }
        });
        mBlogsListView.setAdapter(mBlogAdapter);
        mBlogLayout.setRefreshing(true);
        if (!Define.BLOG_CATEGORIES[4].equals(mCategory)) {
            loadData(PAGE_START);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Define.BLOG_CATEGORIES[4].equals(mCategory)) {
            loadData(PAGE_START);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RkLogger.d("Check isBlogOfAPerformer: ", "onDestroy" + "/" + this.hashCode());

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public String getTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(mPerformerName, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(mPerformerName).toString();
        }
    }

    @Override
    protected boolean isUpActionDisplayVisible() {
        return true;
    }

    @Override
    public void onRefresh() {
        loadData(PAGE_START);
    }

    private void loadData(final int page) {

        final DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mMainActivity.dismissRetryDialog();
                loadData(page);
            }

            @Override
            public void onCancelClick() {

            }
        };

        if (!NetworkUtils.hasConnection(mContext)) {
            if (mBlogLayout.isRefreshing()) {
                mBlogLayout.setRefreshing(false);
            }
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
            return;
        }
        if (page == PAGE_START) {
            if (mBlogLayout != null && !mBlogLayout.isRefreshing()) {
                mBlogLayout.setRefreshing(true);
            }
        }
        String mId = SettingManager.getInstance(mContext).getID();
        String mPassword = SettingManager.getInstance(mContext).getPassword();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<BlogsListResponse> call;
        if (mPerformerCode != PERFORMERCODE_NOVALUE) {
            call = apiService.getBlogListOfAPerformer(mId, mPassword, page, PAGE_LIMIT, mPerformerCode);
        } else {
            call = apiService.getBlogList(mId, mPassword, page, PAGE_LIMIT, mCategory, FIELD_OS, FIELD_VERSION);
        }
        call.enqueue(new Callback<BlogsListResponse>() {
            @Override
            public void onResponse(Call<BlogsListResponse> call, Response<BlogsListResponse> response) {
                if (!isAlive)
                    return;
                if (mBlogLayout.isRefreshing()) {
                    mBlogLayout.setRefreshing(false);
                } else {
                    //If not isRefreshing, it is loadmore case. So remove loading item
                    if (mAllBlogDatas.size() > PAGE_LIMIT) {
                        mAllBlogDatas.remove(mAllBlogDatas.size() - 1);
                        mBlogAdapter.notifyItemRemoved(mAllBlogDatas.size());
                        mBlogAdapter.setLoaded();
                    }
                }

                if (response == null || response.body() == null)
                    return;

                if (response.body().isSuccess()) {
                    ArrayList<BlogData> blogDatas = response.body().getPerformerBlog().getData();
                    int numberOfBlogs = response.body().getPerformerBlog().getRow();
                    mBlogAdapter.setTotalBlogsCount(numberOfBlogs);
                    if (mContext instanceof PerformerProfileActivity) {
                        ((PerformerProfileActivity) mContext).setNumberOfBlogs(numberOfBlogs);
                    }
                    if (mAllBlogDatas != null) {
                        //If refresh, clear mAllBlogDatas
                        if (page == PAGE_START && mAllBlogDatas.size() > 0) {
                            mAllBlogDatas.clear();
                        }
                        if (blogDatas != null) {
                            mAllBlogDatas.addAll(blogDatas);
                        }
                    }
                    if (mAllBlogDatas.size() == 0) {
                        mNoContentLayout.setVisibility(View.VISIBLE);
                    } else {
                        mNoContentLayout.setVisibility(View.GONE);
                    }
                    mBlogAdapter.notifyDataSetChanged();
                    mPage = page;
                }
            }

            @Override
            public void onFailure(Call<BlogsListResponse> call, Throwable t) {
                mBlogAdapter.setLoaded();
                if (mBlogLayout != null && mBlogLayout.isRefreshing()) {
                    mBlogLayout.setRefreshing(false);
                }
                if (isVisible() && mMainActivity != null) {
                    mMainActivity.showRetryDialog(onClickRetry);
                }
            }
        });
    }

    @Override
    public void onClickOtherBlogs(int performerCode, String performerName) {
        // TODO: 06/11/2016 show list other blogs
//        openNewFragment(BlogsListFragment.newInstance(performerCode, "", performerName));
//        Fragment blogsFragment = getParentFragment();
//        if(blogsFragment instanceof BlogsFragment) {
//            ((BlogsFragment)getParentFragment()).updateBlogsOfPerformer(performerCode, performerName);
//        }
        mAllBlogDatas.clear();
        mBlogAdapter.notifyDataSetChanged();

        setBlogsOfAPerformer(true);
        mMainActivity.setScreenTitle(Html.fromHtml(performerName).toString());
        mMainActivity.setScreenUpActionDisplay(true);
        mPerformerCode = performerCode;
        mBlogLayout.setRefreshing(true);
        mBlogAdapter.setmPerformerCode(mPerformerCode);
        loadData(PAGE_START);
        Fragment blogsFragment = getParentFragment();
        if (blogsFragment instanceof BlogsFragment) {
            ((BlogsFragment) blogsFragment).removeHightLightTab();
            ((BlogsFragment) blogsFragment).setmScreenTitle(Html.fromHtml(performerName).toString());
        }
    }

    @Override
    public void onClickBlog(int postId, String performerName, int position) {
        if (mMainActivity != null) {
            openNewFragment(BlogDetailFragment.newInstance(postId, performerName, position));
        } else if (mContext instanceof PerformerProfileActivity) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setAction(Define.IntentActions.ACTION_BLOG_DETAIL);
            intent.putExtra(Define.IntentExtras.POST_ID, postId);
            intent.putExtra(Define.IntentExtras.PERFORMER_NAME, performerName);
            intent.putExtra(Define.IntentExtras.POSITION, position);
            startActivity(intent);
            ((PerformerProfileActivity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Subscribe
    public void onEvent(UpdateFavoriteBlogEvent event) {
        if (event == null || mAllBlogDatas == null || mBlogAdapter == null)
            return;
        int position = event.getmPosition();
        if (position >= 0) {
            mAllBlogDatas.get(position).setNiced(String.valueOf(Define.LIKED));
            mAllBlogDatas.get(position).setNiceCount(String.valueOf(event.getmNiceCount()));
            mBlogAdapter.notifyDataSetChanged();
        } else {
            reloadData();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private void openNewFragment(BaseFragment fragment) {
        Fragment parentFragment = getParentFragment();
        if (fragment != null && parentFragment != null) {
            /**In case blog list is in BlogsFragment*/
            if (parentFragment instanceof BlogsFragment) {
                ((BlogsFragment) parentFragment).openNewFragment(fragment);
                /**In case blog list of a performer*/
            } else if (parentFragment instanceof BlogContainerFragment) {
                ((BlogContainerFragment) parentFragment).replaceFragment(fragment);
            }
        }
    }

    @Subscribe
    public void onEvent(UpdateBlockPerformerEvent event) {
        reloadData();
    }

    /**
     * Usage: Reload data of a category when displaying data is performer's blogs.
     */
    public void reloadData() {
        mAllBlogDatas.clear();
        mBlogAdapter.notifyDataSetChanged();

        setBlogsOfAPerformer(false);
        mPerformerCode = PERFORMERCODE_NOVALUE;
        mBlogAdapter.setmPerformerCode(mPerformerCode);
        mBlogLayout.setRefreshing(true);
        loadData(PAGE_START);
    }

    public boolean isBlogsOfAPerformer() {
        RkLogger.d("Check isBlogOfAPerformer: ", "" + isBlogsOfAPerformer + "/" + this.hashCode());
        return isBlogsOfAPerformer;
    }

    public void setBlogsOfAPerformer(boolean blogsOfAPerformer) {
        isBlogsOfAPerformer = blogsOfAPerformer;
        RkLogger.d("Check isBlogOfAPerformer: ", "set: " + isBlogsOfAPerformer + "/" + this.hashCode());
    }
}
