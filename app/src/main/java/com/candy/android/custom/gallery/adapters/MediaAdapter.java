package com.candy.android.custom.gallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.candy.android.R;
import com.candy.android.custom.gallery.activities.GalleryActivity;
import com.candy.android.custom.gallery.models.Medium;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private final GalleryActivity activity;

    private List<Medium> items = new ArrayList<>();

    public List<Medium> getItems() {
        return items;
    }

    public void setItems(List<Medium> items) {
        this.items = items;
    }

    public MediaAdapter(GalleryActivity activity) {
        this.activity = activity;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_video_item, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        Medium medium = items.get(position);
        holder.bindData(activity, medium);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView photoThumbnail;
        View playOutline;

        MediaViewHolder(View view) {
            super(view);

            photoThumbnail = (ImageView) view.findViewById(R.id.medium_thumbnail);
            playOutline = view.findViewById(R.id.play_outline);
        }

        void bindData(final GalleryActivity activity, final Medium medium) {
            if (medium.getIsVideo()) {
                playOutline.setVisibility(View.VISIBLE);
            } else {
                playOutline.setVisibility(View.GONE);
            }

            final String path = medium.getPath();
            final StringSignature timestampSignature = new StringSignature(String.valueOf(medium.getTimestamp()));

            Glide.with(itemView.getContext()).load(path).diskCacheStrategy(DiskCacheStrategy.NONE).signature(timestampSignature)
                    .placeholder(R.color.tmb_background).centerCrop().crossFade().into(photoThumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onMediumClick(medium);
                }
            });
        }
    }
}
