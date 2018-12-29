package com.candy.android.adapter;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candy.android.R;
import com.candy.android.model.BasePerformer;
import com.candy.android.model.PerformerOnline;
import com.candy.android.utils.RkLogger;

import java.util.List;

/**
 * Created by quannt on 27/10/2016.
 * Description: Adapter for performer image
 */

public class ImagePagerAdapter extends PagerAdapter {
    private List<PerformerOnline> mPerformerOnlines;

    /***
     * Adapter only user to show one performer: in case go to performerProfile from blog, mail, favorite
     */
    private boolean isOnePerformer;
    public static final String TAG = "Image";

    public ImagePagerAdapter(List<PerformerOnline> performerOnlines, boolean isOnePerformer) {
        mPerformerOnlines = performerOnlines;
        this.isOnePerformer = isOnePerformer;
    }

    @Override
    public int getCount() {
        return mPerformerOnlines.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.image_pager_item, container, false);
        PerformerOnline performerOnline = mPerformerOnlines.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.qiv_image);
        if (!isOnePerformer) {
            if (performerOnline != null && performerOnline.getIsPublic() == BasePerformer.NO_PUBLIC_INT) {
                imageView.setImageResource(R.drawable.no_image);
            } else {
                Uri uri = Uri.parse(mPerformerOnlines.get(position).getProfileImageUrl());
                RkLogger.e(uri.toString());
                Glide.with(container.getContext())
                        .load(uri)
                        .into(imageView);
            }
        }

        itemView.setTag(TAG + position);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
