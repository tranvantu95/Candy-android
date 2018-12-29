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
import com.candy.android.custom.gallery.adapters.DirectoryAdapter;
import com.candy.android.custom.gallery.models.Directory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DirectoryFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private RecyclerView recyclerView;

    private DirectoryAdapter adapter;

    public static DirectoryFragment newInstance() {
        return new DirectoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DirectoryAdapter((GalleryActivity) getActivity());
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerView.setAdapter(null);
        recyclerView = null;
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
        adapter.setItems(getDirectories());
        adapter.notifyDataSetChanged();
    }

    private List<Directory> getDirectories() {
        final Map<String, Directory> directories = new LinkedHashMap<>();
        final List<String> invalidFiles = new ArrayList<>();
        Uri uriToLoad;
        uriToLoad = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
        final Cursor cursor = getActivity().getContentResolver().query(uriToLoad, columns, null, null, MediaStore.Images.Media.DATE_TAKEN);

        if (cursor != null && cursor.moveToFirst()) {
            final int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                final String fullPath = cursor.getString(pathIndex);
                final File file = new File(fullPath);
                final String parentDir = file.getParent();

                if (!file.exists()) {
                    invalidFiles.add(file.getAbsolutePath());
                    continue;
                }

                final int dateIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                final long timestamp = cursor.getLong(dateIndex);
                if (directories.containsKey(parentDir)) {
                    final Directory directory = directories.get(parentDir);
                    final int newImageCnt = directory.getMediaCnt() + 1;
                    directory.setMediaCnt(newImageCnt);
                    directory.setThumbnail(fullPath);
                } else {
                    String dirName = Utils.getFilename(parentDir);
                    directories.put(parentDir, new Directory(parentDir, fullPath, dirName, 1, timestamp));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        final List<Directory> dirs = new ArrayList<>(directories.values());
        removeNoMediaFolders(dirs);
        Collections.sort(dirs, new Comparator<Directory>() {
            @Override
            public int compare(Directory directory, Directory t1) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(directory.getName(), t1.getName());
                if (res == 0) {
                    res = directory.compareTo(t1);
                }
                return res;
            }
        });

        final String[] invalids = invalidFiles.toArray(new String[invalidFiles.size()]);
        MediaScannerConnection.scanFile(getActivity(), invalids, null, null);

        return dirs;
    }

    private void removeNoMediaFolders(List<Directory> dirs) {
        final List<Directory> ignoreDirs = new ArrayList<>();
        for (final Directory d : dirs) {
            final File dir = new File(d.getPath());
            if (dir.exists() && dir.isDirectory()) {
                final String[] res = dir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String filename) {
                        return filename.equals(".nomedia");
                    }
                });

                if (res.length > 0)
                    ignoreDirs.add(d);
            }
        }

        dirs.removeAll(ignoreDirs);
    }
}
