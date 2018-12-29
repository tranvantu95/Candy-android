package com.candy.android.custom.gallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.candy.android.R;
import com.candy.android.custom.gallery.activities.GalleryActivity;
import com.candy.android.custom.gallery.models.Directory;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {

    private final GalleryActivity activity;

    private List<Directory> items = new ArrayList<>();

    public List<Directory> getItems() {
        return items;
    }

    public void setItems(List<Directory> items) {
        this.items = items;
    }

    public DirectoryAdapter(GalleryActivity activity) {
        this.activity = activity;
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_item, parent, false);
        return new DirectoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        Directory directory = items.get(position);
        holder.bindData(activity, directory);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class DirectoryViewHolder extends RecyclerView.ViewHolder {
        TextView dirName;
        TextView photoCnt;
        ImageView dirThumbnail;

        DirectoryViewHolder(View view) {
            super(view);
            dirName = (TextView) view.findViewById(R.id.dir_name);
            photoCnt = (TextView) view.findViewById(R.id.photo_cnt);
            dirThumbnail = (ImageView) view.findViewById(R.id.dir_thumbnail);
            ButterKnife.bind(this, view);
        }

        void bindData(final GalleryActivity activity, final Directory dir) {
            dirName.setText(dir.getName());
            photoCnt.setText(String.valueOf(dir.getMediaCnt()));
            final String tmb = dir.getThumbnail();
            final StringSignature timestampSignature = new StringSignature(String.valueOf(dir.getTimestamp()));
            if (tmb.endsWith(".gif")) {
                Glide.with(activity).load(tmb).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).signature(timestampSignature)
                        .placeholder(R.color.tmb_background).centerCrop().crossFade().into(dirThumbnail);
            } else {
                Glide.with(activity).load(tmb).diskCacheStrategy(DiskCacheStrategy.RESULT).signature(timestampSignature)
                        .placeholder(R.color.tmb_background).centerCrop().crossFade().into(dirThumbnail);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onDirectoryClick(dir);
                }
            });
        }
    }
}
