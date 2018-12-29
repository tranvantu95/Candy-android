package com.candy.android.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candy.android.R;
import com.meg7.widget.CircleImageView;

import jp.fmaru.app.livechatapp.ChatActivity;

/**
 * Created by Hungnq on 12/11/17.
 */

public abstract class VideoCallBasePopupActivity extends ChatActivity {
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void showPopupMessage(final String title, String message, final String img, final int code, final Context context, final LinearLayout mLl_popup, final int age) {
        if (mLl_popup.getChildCount() < 3) {
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(context).inflate(
                    R.layout.popup_new_message, mLl_popup, false);
            //
            if (mLl_popup.getChildCount() == 0) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(10, 2, 10, 8);
                newView.setLayoutParams(lp);
            }
            CircleImageView iv_Avatar = (CircleImageView) newView.findViewById(R.id.iv_avatar_popup);
            final Bitmap defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            if (!img.isEmpty()) {
                Glide.with(context)
                        .load(img)
                        .centerCrop()
                        .into(iv_Avatar);
            } else {
                iv_Avatar.setImageBitmap(defaultBitmap);
            }
            TextView tv_Name = (TextView) newView.findViewById(R.id.tv_name_popup);
            if (!TextUtils.isEmpty(title)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_Name.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tv_Name.setText(Html.fromHtml(title));
                }
            } else {
                tv_Name.setText("");
            }
            newView.findViewById(R.id.fl_avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLl_popup.removeView(newView);
                }
            });
            //
            mLl_popup.addView(newView, 0);
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mLl_popup.removeView(newView);
                }
            }.start();

            newView.findViewById(R.id.item_popup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickOpenDetail(title, code, img, age);
                }
            });
        }
    }

    abstract protected void onClickOpenDetail(String name, int code, String img, int age);
}
