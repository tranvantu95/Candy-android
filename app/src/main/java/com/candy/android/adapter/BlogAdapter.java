package com.candy.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapShader;
import android.os.Build;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.candy.android.R;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.RoundCornerImageView;
import com.candy.android.fragment.BlogsListFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.manager.AnimatedImageSpanFactory;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BlogData;
import com.candy.android.model.PerformerOnline;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.gifutils.AnimatedGifDrawable;

import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.candy.android.fragment.BlogsListFragment.PERFORMERCODE_NOVALUE;

/**
 * Created by quannt on 03/11/2016.
 * Des: Blog list
 */

public class BlogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<BlogData> mPerformerBlogs;
    private BlogsListFragment.OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;
    //is loading more
    private boolean isLoading;
    //number of Items is fit with screen
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;

    /**
     * Type of list blogs:  0: list blogs of all performer
     * otherwise: list blogs of one performer -> Don't have otherBlogs view
     */
    private int mPerformerCode;
    private int mTotalBlogsCount;

    /**
     * isNoFooter: Don't show performer avatar layout
     */
    private boolean isNoFooter;

    private OnBlogClickListener mListener;


    public BlogAdapter(Context context, List<BlogData> performerBlogs, RecyclerView recyclerView, int performerCode, boolean isNoFooter) {
        mContext = context;
        mPerformerBlogs = performerBlogs;
        mRecyclerView = recyclerView;
        mPerformerCode = performerCode;
        this.isNoFooter = isNoFooter;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount > 1) {
                    if (mOnLoadMoreListener != null && mPerformerBlogs.size() < mTotalBlogsCount && mPerformerBlogs.size() >= BlogsListFragment.PAGE_LIMIT) {
                        isLoading = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public static class BlogHolder extends RecyclerView.ViewHolder {
        ImageView blogItemImage, liveAnimation, btBlogLike;
        TextView blogLike, blogTitle, postDate, blogBody, performerStatus, performerName, otherBlogs;
        RoundCornerImageView blogPerformerAvatar;
        RelativeLayout footerLayout, cvInsideLayout;
        FrameLayout frButtonLike;
        CardView cvBlogItem;
        LinearLayout mOtherBlogLayout;
        View ftDivivder, userLiveStatus;

        public BlogHolder(View itemView) {
            super(itemView);
            cvBlogItem = (CardView) itemView.findViewById(R.id.blog_card_view);
            blogItemImage = (ImageView) itemView.findViewById(R.id.iv_blog_item_image);
            blogLike = (TextView) itemView.findViewById(R.id.blog_like);
            frButtonLike = (FrameLayout) itemView.findViewById(R.id.blog_like_button_layout);
            btBlogLike = (ImageView) itemView.findViewById(R.id.blog_like_button);
            blogTitle = (TextView) itemView.findViewById(R.id.blog_title);
            postDate = (TextView) itemView.findViewById(R.id.tv_post_date);
            blogBody = (TextView) itemView.findViewById(R.id.blog_body);
            performerStatus = (TextView) itemView.findViewById(R.id.tv_blog_performer_status);
            blogTitle = (TextView) itemView.findViewById(R.id.blog_title);
            performerName = (TextView) itemView.findViewById(R.id.blog_performer_name);
            otherBlogs = (TextView) itemView.findViewById(R.id.tv_other_blogs);
            blogPerformerAvatar = (RoundCornerImageView) itemView.findViewById(R.id.blog_performer_avatar);
            userLiveStatus = itemView.findViewById(R.id.live_status_blog_list);
            liveAnimation = (ImageView) itemView.findViewById(R.id.blog_live_status_image);
            cvInsideLayout = (RelativeLayout) itemView.findViewById(R.id.cardview_inside_layout);
            footerLayout = (RelativeLayout) itemView.findViewById(R.id.blog_footer_layout);
            mOtherBlogLayout = (LinearLayout) itemView.findViewById(R.id.other_blogs_layout);
            ftDivivder = (View) itemView.findViewById(R.id.footer_divider);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_view, parent, false);
            return new BlogHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BlogHolder) {
            final BlogHolder blogHolder = (BlogHolder) holder;
            final BlogData performerBlog = mPerformerBlogs.get(position);

//            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP){
//                blogHolder.cvBlogItem.setRadius(4);
//            }
            String picture = performerBlog.getPicture();
            if (!TextUtils.isEmpty(picture)) {
                Glide.with(mContext).load(picture).into(blogHolder.blogItemImage);
            } else {
                blogHolder.blogItemImage.setImageDrawable(null);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                blogHolder.blogTitle.setText(Html.fromHtml(performerBlog.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                blogHolder.blogTitle.setText(Html.fromHtml(performerBlog.getTitle()));
            }

            blogHolder.postDate.setText(HimecasUtils.formatSentEmailDateAndTime(performerBlog.getPostDate()));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                blogHolder.blogBody.setText(Html.fromHtml(performerBlog.getBody(), Html.FROM_HTML_MODE_COMPACT));
//            } else {
//                blogHolder.blogBody.setText(Html.fromHtml(performerBlog.getBody()));
//            }
            final AnimatedGifDrawable.UpdateListener updateListener = new AnimatedGifDrawable.UpdateListener() {
                @Override
                public void update() {
                    blogHolder.blogBody.postInvalidate();
                }
            };

            blogHolder.blogBody.setText(Html.fromHtml(Emojione.shortnameToUnicode(performerBlog.getBody(true)), new Html.ImageGetter() {
                @Override
                public AnimatedGifDrawable getDrawable(String s) {
                    int emojiResourceId = AnimatedImageSpanFactory.getEmojiResourceId(s);
                    if (emojiResourceId > 0) {
                        InputStream resourceStream = mContext.getResources().openRawResource(emojiResourceId);
                        return new AnimatedGifDrawable(resourceStream, updateListener);
                    }
                    return null;
                }
            }, null));

            final int niceCount = performerBlog.getNiceCount();
            blogHolder.blogLike.setText(String.format("%s", niceCount));
            if (performerBlog.getNiced() == Define.LIKED) {
                blogHolder.blogLike.setSelected(true);
                blogHolder.btBlogLike.setSelected(true);
                blogHolder.blogLike.setTextColor(mContext.getResources().getColor(R.color.like_button_selected_color));
            } else {
                blogHolder.blogLike.setSelected(false);
                blogHolder.btBlogLike.setSelected(false);
                blogHolder.blogLike.setTextColor(mContext.getResources().getColor(R.color.like_button_color));
            }
            blogHolder.frButtonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (performerBlog.getNiced() == Define.LIKED)
                        return;
                    String id = SettingManager.getInstance(mContext).getID();
                    String pass = SettingManager.getInstance(mContext).getPassword();
                    ApiInterface apiSevice = ApiClientManager.getApiClientManager().create(ApiInterface.class);
                    Call<ApiFavoriteResponse> call = apiSevice.likeBlog(id, pass, performerBlog.getPostId());
                    call.enqueue(new Callback<ApiFavoriteResponse>() {
                        @Override
                        public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                            if (response == null || response.body() == null)
                                return;
                            if (response.body().isSuccess()) {
                                //update data
                                mPerformerBlogs.get(position).setNiced(Define.LIKED + "");
                                mPerformerBlogs.get(position).setNiceCount(niceCount + 1 + "");
                                //update view
                                blogHolder.blogLike.setSelected(true);
                                blogHolder.btBlogLike.setSelected(true);
                                blogHolder.blogLike.setTextColor(mContext.getResources().getColor(R.color.like_button_selected_color));
                                blogHolder.blogLike.setText(String.format("%s", niceCount + 1));
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {

                        }
                    });
                }
            });

            if (isNoFooter) {
                blogHolder.footerLayout.setVisibility(View.GONE);
                blogHolder.ftDivivder.setVisibility(View.GONE);
//                blogHolder.cvInsideLayout.setBackgroundResource(R.drawable.bg_white_round_corner_4dp);
            } else {
                int performerStatus = performerBlog.getPerformerStatus();
                if (1 <= performerStatus && performerStatus <= 2) {
                    blogHolder.userLiveStatus.setVisibility(View.VISIBLE);
                    blogHolder.liveAnimation.setVisibility(View.VISIBLE);
                    GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((BlogHolder) holder).liveAnimation);
                    Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
                } else {
                    blogHolder.userLiveStatus.setVisibility(View.GONE);
                    blogHolder.liveAnimation.setVisibility(View.GONE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    blogHolder.performerName.setText(Html.fromHtml(performerBlog.getPerformerName(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    blogHolder.performerName.setText(Html.fromHtml(performerBlog.getPerformerName()));
                }

                if (mPerformerCode != PERFORMERCODE_NOVALUE) {
//                    blogHolder.otherBlogs.setVisibility(View.INVISIBLE);
                    blogHolder.mOtherBlogLayout.setVisibility(View.INVISIBLE);
                } else {
                    int postCount = performerBlog.getPostCount();
                    if (postCount == 0) {
//                        blogHolder.otherBlogs.setVisibility(View.INVISIBLE);
                        blogHolder.mOtherBlogLayout.setVisibility(View.INVISIBLE);
                    } else {
                        blogHolder.mOtherBlogLayout.setVisibility(View.VISIBLE);
                        blogHolder.otherBlogs.setText(String.format(mContext.getString(R.string.other_blogs_format), postCount));
                        blogHolder.mOtherBlogLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListener.onClickOtherBlogs(performerBlog.getPerformerCode(), performerBlog.getPerformerName());
                            }
                        });
                    }
                }

                Glide.with(mContext).load(performerBlog.getPerformerImageUrl())
                        .into(blogHolder.blogPerformerAvatar);
                blogHolder.blogPerformerAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PerformerProfileActivity.class);
                        PerformerOnline performerOnline = new PerformerOnline();
                        //Only pass code and profileImageUrl
                        performerOnline.setCode(performerBlog.getPerformerCode());
                        performerOnline.setProfileImageUrl(performerBlog.getPerformerImageUrl());
                        intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
                        intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
                        mContext.startActivity(intent);
                    }
                });
            }

            blogHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClickBlog(performerBlog.getPostId(), performerBlog.getPerformerName(), position);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mPerformerBlogs == null ? 0 : mPerformerBlogs.size();
    }

    public void setOnLoadMoreListener(BlogsListFragment.OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mPerformerBlogs.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public interface OnBlogClickListener {
        void onClickOtherBlogs(int performerCode, String performerName);

        void onClickBlog(int postId, String performerName, int position);
    }

    public void setBlogListener(OnBlogClickListener onBlogClickListener) {
        mListener = onBlogClickListener;
    }

    public void setTotalBlogsCount(int totalBlogsCount) {
        mTotalBlogsCount = totalBlogsCount;
    }

    public void setmPerformerCode(int mPerformerCode) {
        this.mPerformerCode = mPerformerCode;
    }
}
