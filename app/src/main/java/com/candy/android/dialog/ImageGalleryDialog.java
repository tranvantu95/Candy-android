package com.candy.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.candy.android.R;

/**
 * Created by quannt on 01/11/2016.
 * Des:
 */

public class ImageGalleryDialog extends DialogFragment implements View.OnClickListener {
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE_COMMENT = "image_comment";

    private String mImageUrl;
    private String mComment;

    private TextView mTvComment;

    private Context mContext;
    private View mProgressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static ImageGalleryDialog newInstance(String imageUrl, String comment) {
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        args.putString(IMAGE_COMMENT, comment);
        ImageGalleryDialog fragment = new ImageGalleryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mImageUrl = args.getString(IMAGE_URL, "");
            mComment = args.getString(IMAGE_COMMENT, "");
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window;
        if (dialog != null && dialog.getWindow() != null) {
            window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (ViewGroup.LayoutParams.MATCH_PARENT));
            window.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getColor(R.color.bg_campaign_color)));
            } else {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.bg_campaign_color)));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressDialog = view.findViewById(R.id.loading_progress);
        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_gallery_image);
        imageView.setOnClickListener(this);
        ImageView tvClose = (ImageView) view.findViewById(R.id.dialog_gallery_close);
        tvClose.setOnClickListener(this);
        mTvComment = (TextView) view.findViewById(R.id.tv_comment_image_gallery);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTvComment.setText(Html.fromHtml(mComment, Html.FROM_HTML_MODE_COMPACT));
        } else {
            mTvComment.setText(Html.fromHtml(mComment));
        }
        mProgressDialog.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mImageUrl)) {
            Glide.with(mContext)
                    .load(mImageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            mProgressDialog.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressDialog.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_gallery_close:
                dismiss();
                break;
            case R.id.dialog_gallery_image:
                mTvComment.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
