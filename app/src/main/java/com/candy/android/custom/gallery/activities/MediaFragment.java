package com.candy.android.custom.gallery.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.custom.gallery.Utils;
import com.candy.android.custom.gallery.adapters.MediaAdapter;
import com.candy.android.custom.gallery.models.Medium;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class MediaFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String ARG_PATH = "ARG_PATH";

    private RecyclerView recyclerView;
    private MediaAdapter adapter;

    private String mPath;

    public static MediaFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PATH, path);
        MediaFragment menuFragment = new MediaFragment();
        menuFragment.setArguments(bundle);
        return menuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPath = getArguments().getString(ARG_PATH);
        }

        adapter = new MediaAdapter((GalleryActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.media_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerView.setAdapter(null);
        recyclerView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((GalleryActivity) getActivity()).setScreenTitle(getString(R.string.gallery_title));
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadGallery();
    }

    private void reloadGallery() {
        if (Utils.hasReadStoragePermission(getContext().getApplicationContext())) {
            loadGallery();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGallery();
            } else {
                Utils.showToast(getContext(), R.string.no_permissions_read_storage);
                getActivity().finish();
            }
        }
    }

    private void loadGallery() {
        adapter.setItems(getMedia());
        adapter.notifyDataSetChanged();

        if(adapter.getItemCount() == 0) {
            deleteDirectoryIfEmpty();
            getActivity().onBackPressed();
        }
    }

    private List<Medium> getMedia() {
        final List<Medium> media = new ArrayList<>();
        final List<String> invalidFiles = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Images.Media.DATA + " like ? ";
        final String[] args = new String[]{mPath + "%"};
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
        final Cursor cursor = getActivity().getContentResolver().query(uri, columns, where, args, null);
        final String pattern = Pattern.quote(mPath) + "/[^/]*";

        if (cursor != null && cursor.moveToFirst()) {
            final int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                final String curPath = cursor.getString(pathIndex);
                if (curPath.matches(pattern)) {
                    final File file = new File(curPath);
                    if (file.exists()) {
                        final int dateIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                        final long timestamp = cursor.getLong(dateIndex);
                        media.add(new Medium(curPath, false, timestamp));
                    } else {
                        invalidFiles.add(file.getAbsolutePath());
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        Collections.sort(media, new Comparator<Medium>() {
            @Override
            public int compare(Medium medium, Medium medium2) {
                float result = medium2.getTimestamp() - medium.getTimestamp();
                return result < 0 ? -1 : result > 0 ? 1 : 0;
            }
        });

        final String[] invalids = invalidFiles.toArray(new String[invalidFiles.size()]);
        MediaScannerConnection.scanFile(getActivity(), invalids, null, null);

        return media;
    }

    private void deleteDirectoryIfEmpty() {
        final File file = new File(mPath);
        if (file.isDirectory() && file.listFiles().length == 0) {
            file.delete();
        }
    }

}
