package com.candy.android.fragment;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.BlogDetailResponse;
import com.candy.android.manager.AnimatedImageSpanFactory;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BlogData;
import com.candy.android.model.PerformerBlogDetail;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.UpdateFavoriteBlogEvent;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.gifutils.AnimatedGifDrawable;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogDetailFragment extends BaseFragment implements View.OnClickListener {

    private static final String BLOG_POST_ID = "blog_post_id";
    private static final String BLOG_POSITION = "blog_position";
    private static final String BLOG_PERFORMER_NAME = "blog_performer_name";
    private static final String BLOG_PERFORMER_CODE = "blog_performer_code";
    private static final String TAG = "Blog Detail";

    private int mPostId, mPosition;
    private TextView mBlogTitle, mPostDate, mLikeTop, mLikeBottom,
            mTvPerformerName, mPerformerStatus, mOtherBlogs, mBlockAndReport;
    private LinearLayout mOtherBlogLayout;
    private TextView mBlogBody;
    private FullscreenVideoLayout mBlogVideoView;
    private RelativeLayout mThumbLayout;
    private ImageView imLiveAnimation;
    private View mLiveStatus;
    private ProgressBar mVideoProgress;
    private ImageView mIvThumbnail;
    private FrameLayout mBlogVideoLayout, frTopLike, frBottomLike;
    private ImageView mBlogImage, btTopLike, btBottomLike;
    private ImageView mPerformerAvatar;
    private String mPerformerName;
    private String mUserId, mUserPassword;
    private PerformerBlogDetail mPerformerBlogDetail;
    private String mScreenTitle;

    public BlogDetailFragment() {
        // Required empty public constructor
    }

    public static BlogDetailFragment newInstance(int postId, String performerName, int position) {

        Bundle args = new Bundle();
        args.putInt(BLOG_POST_ID, postId);
        args.putString(BLOG_PERFORMER_NAME, performerName);
        args.putInt(BLOG_POSITION, position);
        BlogDetailFragment fragment = new BlogDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPostId = args.getInt(BLOG_POST_ID, 0);
            mPosition = args.getInt(BLOG_POSITION, 0);
            mPerformerName = args.getString(BLOG_PERFORMER_NAME, "");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setTitle(Html.fromHtml(mPerformerName, Html.FROM_HTML_MODE_COMPACT).toString());
        } else {
            setTitle(Html.fromHtml(mPerformerName).toString());
        }

        mUserId = SettingManager.getInstance(mContext).getID();
        mUserPassword = SettingManager.getInstance(mContext).getPassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBlogTitle = (TextView) view.findViewById(R.id.blog_detail_title);
        mPostDate = (TextView) view.findViewById(R.id.blog_detail_post_date);
        mLikeTop = (TextView) view.findViewById(R.id.blog_detail_like_top);
        frTopLike = (FrameLayout) view.findViewById(R.id.blog_detail_like_button_top_layout);
        frTopLike.setOnClickListener(this);
        btTopLike = (ImageView) view.findViewById(R.id.blog_detail_like_button_top);
        mLikeBottom = (TextView) view.findViewById(R.id.blog_detail_like_bottom);
        frBottomLike = (FrameLayout) view.findViewById(R.id.blog_detail_like_button_bottom_layout);
        frBottomLike.setOnClickListener(this);
        btBottomLike = (ImageView) view.findViewById(R.id.blog_detail_like_button_bottom);
        mBlogBody = (TextView) view.findViewById(R.id.blog_detail_body);
//        mBlogBody.setWebChromeClient(new WebChromeClient());
//        mBlogBody.getSettings().setJavaScriptEnabled(true);
        mTvPerformerName = (TextView) view.findViewById(R.id.blog_detail_performer_name);
        mPerformerStatus = (TextView) view.findViewById(R.id.tv_blog_detail_performer_status);
        mBlockAndReport = (TextView) view.findViewById(R.id.blog_detail_blog_report);
        mBlockAndReport.setOnClickListener(this);
        mOtherBlogs = (TextView) view.findViewById(R.id.tv_detail_other_blogs);
        mOtherBlogLayout = (LinearLayout) view.findViewById(R.id.other_blogs_layout);
        mOtherBlogLayout.setOnClickListener(this);
        mBlogImage = (ImageView) view.findViewById(R.id.blog_detail_image);
        mBlogVideoView = (FullscreenVideoLayout) view.findViewById(R.id.blog_detail_video);
        mBlogVideoView.setActivity((Activity) mContext);
        mBlogVideoLayout = (FrameLayout) view.findViewById(R.id.blog_video_layout);
        mThumbLayout = (RelativeLayout) view.findViewById(R.id.thumb_layout);
        mLiveStatus = view.findViewById(R.id.live_status_blog);
        imLiveAnimation = (ImageView) view.findViewById(R.id.blog_detail_live_status_image);
        mVideoProgress = (ProgressBar) view.findViewById(R.id.video_progress);
        mIvThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
        mPerformerAvatar = (ImageView) view.findViewById(R.id.blog_detail_performer_avatar);
        mPerformerAvatar.setOnClickListener(this);
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMainActivity != null) {
            mMainActivity.setScreenUpActionDisplay(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMainActivity != null) {
            mMainActivity.setScreenUpActionDisplay(true);
        }
    }

    @Override
    public String getTitle() {
        return mScreenTitle;
    }

    private void setTitle(String title) {
        mScreenTitle = title;
    }

    private void loadData() {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener mOnClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    mMainActivity.dismissRetryDialog();
                    loadData();
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(mOnClickRetry);
            }
            return;
        }
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<BlogDetailResponse> call = apiService.getBlogDetail(mUserId, mUserPassword, mPostId);
        call.enqueue(new Callback<BlogDetailResponse>() {
            @Override
            public void onResponse(Call<BlogDetailResponse> call, Response<BlogDetailResponse> response) {
                if (response == null || response.body() == null)
                    return;
                if (response.body().isSuccess()) {
                    mPerformerBlogDetail = response.body().getPerformerBlogDetail();
                    if (mPerformerBlogDetail != null && isAlive) {
                        setViewData(mPerformerBlogDetail);
                    }
                }
            }

            @Override
            public void onFailure(Call<BlogDetailResponse> call, Throwable t) {

            }
        });
    }

    private void setViewData(PerformerBlogDetail performerBlogDetail) {
        if (performerBlogDetail == null)
            return;
        BlogData blogData = performerBlogDetail.getData();
        if (blogData == null)
            return;
        int postCount = blogData.getPostCount();
        String title = blogData.getTitle();
        if (!TextUtils.isEmpty(title)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBlogTitle.setText(Html.fromHtml(blogData.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mBlogTitle.setText(Html.fromHtml(blogData.getTitle()));
            }
        }

        final AnimatedGifDrawable.UpdateListener updateListener = new AnimatedGifDrawable.UpdateListener() {
            @Override
            public void update() {
                mBlogBody.postInvalidate();
            }
        };

        mBlogBody.setText(Html.fromHtml(Emojione.shortnameToUnicode(blogData.getBody(false)), new Html.ImageGetter() {
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


//        try {
//            String body = Emojione.shortnameToUnicode(blogData.getBody(true));
//            HimecasUtils.buildGiffableSpanTextView(mBlogBody, body);
//        } catch (IllegalArgumentException ignored) {
//        }

        String postDate = blogData.getPostDate();
        if (!TextUtils.isEmpty(postDate)) {
            mPostDate.setText(HimecasUtils.formatSentEmailDateAndTime(postDate));
        }

        String performerName = blogData.getPerformerName();
        if (!TextUtils.isEmpty(performerName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTvPerformerName.setText(Html.fromHtml(performerName, Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTvPerformerName.setText(Html.fromHtml(performerName));
            }
        }

        int performerStatus = blogData.getPerformerStatus();
//        mPerformerStatus.setText(Define.OnlineStatus.getStatusFromCode(performerStatus).getName());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mPerformerStatus.setBackground(mContext.getDrawable(Define.OnlineStatus.getBackgroundDrawableCornerFromCode(performerStatus)));
//        } else {
//            mPerformerStatus.setBackground(mContext.getResources().getDrawable(Define.OnlineStatus.getBackgroundDrawableCornerFromCode(performerStatus)));
//        }
        if (1 <= performerStatus && performerStatus <= 2) {
            mLiveStatus.setVisibility(View.VISIBLE);
            imLiveAnimation.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imLiveAnimation);
            Glide.with(mContext).load(R.raw.ic_live).into(imageViewTarget);
        } else {
            mLiveStatus.setVisibility(View.GONE);
            imLiveAnimation.setVisibility(View.GONE);
        }
        mLikeTop.setText(String.format("%s", blogData.getNiceCount()));
        mLikeBottom.setText(String.format("%s", blogData.getNiceCount()));

        if (blogData.getNiced() == Define.LIKED) {
            setViewLiked(frTopLike, mLikeTop, btTopLike);
            setViewLiked(frBottomLike, mLikeBottom, btBottomLike);
        } else {
            setViewNotLiked(frTopLike, mLikeTop, btTopLike);
            setViewNotLiked(frBottomLike, mLikeBottom, btBottomLike);
        }

        mOtherBlogs.setText(String.format(getString(R.string.other_blogs_format), postCount));

        String movie = blogData.getMovie();
        if (TextUtils.isEmpty(movie)) {
            mBlogImage.setVisibility(View.VISIBLE);
            mBlogVideoLayout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(blogData.getPicture()))
                Glide.with(mContext).load(blogData.getPicture()).into(mBlogImage);
        } else {
            mBlogImage.setVisibility(View.GONE);
            mBlogVideoLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(blogData.getPicture())) {
                Glide.with(mContext).load(blogData.getPicture()).into(mIvThumbnail);
            }
            Uri videoUri = Uri.parse(movie);
            try {
                mBlogVideoView.setVideoURI(videoUri);
                mBlogVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                mThumbLayout.setVisibility(View.GONE);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                mBlogVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mVideoProgress.setVisibility(View.GONE);
                        mBlogVideoView.showControls();
                        mBlogVideoView.bringControlToFront();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(blogData.getPerformerImageUrl())) {
            Glide.with(mContext).load(blogData.getPerformerImageUrl()).into(mPerformerAvatar);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RkLogger.d("Check onSaveInstance >> ", "onSaveInstance BlogDetail");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.blog_detail_like_button_top_layout:
            case R.id.blog_detail_like_button_bottom_layout:
                String id = SettingManager.getInstance(mContext).getID();
                String pass = SettingManager.getInstance(mContext).getPassword();
                ApiInterface apiSevice = ApiClientManager.getApiClientManager().create(ApiInterface.class);
                Call<ApiFavoriteResponse> call = apiSevice.likeBlog(id, pass, mPerformerBlogDetail.getData().getPostId());
                call.enqueue(new Callback<ApiFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                        if (response == null || response.body() == null)
                            return;
                        if (response.body().isSuccess()) {
                            int niceCount = mPerformerBlogDetail.getData().getNiceCount() + 1;
                            UpdateFavoriteBlogEvent event = new UpdateFavoriteBlogEvent(mPosition, niceCount);
                            EventBus.getDefault().post(event);
                            mLikeTop.setText(String.format("%2s", niceCount));
                            mLikeBottom.setText(String.format("%2s", niceCount));
                            setViewLiked(frTopLike, mLikeTop, btTopLike);
                            setViewLiked(frBottomLike, mLikeBottom, btBottomLike);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {

                    }
                });
                break;

            case R.id.blog_detail_blog_report:
                    if (mPerformerBlogDetail != null && mPerformerBlogDetail.getData() != null
                            && getParentFragment() instanceof BlogContainerFragment) {
//                        mMainActivity.gotoBlockAndReport(mPerformerBlogDetail.getData().getPerformerCode());

                        ((BlogContainerFragment) getParentFragment()).replaceFragment(
                                ReportPerformerFragment.newInstance(String.valueOf(
                                        mPerformerBlogDetail.getData().getPerformerCode())));
                    }
                break;
            case R.id.blog_detail_performer_avatar:
                BlogData blogData;
                if (mPerformerBlogDetail != null && mPerformerBlogDetail.getData() != null) {
                    blogData = mPerformerBlogDetail.getData();
                    Intent intent = new Intent(mContext, PerformerProfileActivity.class);
                    PerformerOnline performerOnline = new PerformerOnline();
                    //Only pass code and profileImageUrl
                    performerOnline.setCode(blogData.getPerformerCode());
                    performerOnline.setProfileImageUrl(blogData.getPerformerImageUrl());
                    intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
                    intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
                    startActivity(intent);
                }

                break;
            case R.id.other_blogs_layout:
                if (mPerformerBlogDetail == null || mPerformerBlogDetail.getData() == null)
                    return;
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null && parentFragment instanceof BlogContainerFragment) {
//                    ((BlogContainerFragment) parentFragment).replaceFragment(BlogsListFragment.newInstance(mPerformerBlogDetail.getData().getPerformerCode(),
//                            "", mPerformerName));
                    ((BlogContainerFragment) parentFragment).doSelfBackPressed();
                    ((BlogContainerFragment) parentFragment).onClickOtherBlogs(mPerformerBlogDetail.getData().getPerformerCode(), mPerformerName);
                }
                break;
        }
    }

    private void setViewLiked(FrameLayout frLayout, TextView textView, ImageView imageView) {
        if (textView == null)
            return;
        frLayout.setClickable(false);
        textView.setSelected(true);
        imageView.setSelected(true);
        textView.setTextColor(mContext.getResources().getColor(R.color.like_button_selected_color));
        textView.setBackgroundResource(R.drawable.bg_like_button_selected_rounded_corner);
    }

    private void setViewNotLiked(FrameLayout frLayout, TextView textView, ImageView imageView) {
        if (textView == null)
            return;
        frLayout.setClickable(true);
        textView.setSelected(false);
        imageView.setSelected(false);
        textView.setTextColor(mContext.getResources().getColor(R.color.dialog_cancel_trolley_grey));
    }

    @Override
    protected boolean isUpActionDisplayVisible() {
        return true;
    }
}
