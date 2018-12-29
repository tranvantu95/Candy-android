package com.candy.android.custom.gallery.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.custom.gallery.callbacks.OnMediaClickListener;
import com.candy.android.custom.gallery.models.Directory;
import com.candy.android.custom.gallery.models.Medium;
import com.candy.android.custom.image_crop.CropImageActivity;
import com.candy.android.custom.image_crop.image_crop.CropImageView;
import com.candy.android.utils.RkLogger;

import java.io.File;

public class GalleryActivity extends AppCompatActivity implements OnMediaClickListener, View.OnClickListener {

    // Intent code
    public static final int PICK_MEDIA = 111;
    public static final String PICK_IMAGE = "PICK_IMAGE";
    public static final String PICK_VIDEO = "PICK_VIDEO";

    private static boolean mIsPickImageIntent;
    private static boolean mIsPickVideoIntent;
    private static CropImageView.CropMode mCropMode;

    private TextView mTvScnTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Set-up actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.iv_back).setOnClickListener(this);
        mTvScnTitle = (TextView) toolbar.findViewById(R.id.gallery_title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        final Intent intent = getIntent();
        // knv added
        if (intent.hasExtra(CropImageActivity.ARG_CROP_MODE)) {
            mCropMode = (CropImageView.CropMode) intent.getSerializableExtra(CropImageActivity.ARG_CROP_MODE);
        } else {
            mCropMode = CropImageView.CropMode.SQUARE;
        }

        mIsPickImageIntent = isPickImageIntent(intent);
        mIsPickVideoIntent = isPickVideoIntent(intent);

        replaceFragment(DirectoryFragment.newInstance(), false);
        setScreenTitle(getString(R.string.gallery_title));
    }

    public void replaceFragment(Fragment fragment, boolean isAddToBST) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        boolean isExistInBackStack = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName()) != null;
        // if fragment existed in fragments stack, we will remove it to ensure that only one instance of
        // that fragment in stack
        if (isExistInBackStack) {
            RkLogger.d("Check crash >> ", "popBackStack");
            getSupportFragmentManager().popBackStack(fragment.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        RkLogger.d("Helpers", "is fragment in back stack: " + isExistInBackStack);
        fragmentTransaction.add(R.id.gallery_container, fragment, fragment.getClass().getSimpleName());
        if (isAddToBST) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void setScreenTitle(String title) {
        if (null != mTvScnTitle) {
            mTvScnTitle.setText(title);
        }
    }

    private boolean isPickImageIntent(Intent intent) {
        return PICK_IMAGE.contentEquals(intent.getAction());
    }

    private boolean isPickVideoIntent(Intent intent) {
        return PICK_VIDEO.contentEquals(intent.getAction());
    }

    @Override
    public void onMediumClick(Medium medium) {
        final String curItemPath = medium.getPath();
        if (mIsPickImageIntent || mIsPickVideoIntent) {
            if (medium.getIsVideo()) {
                final Intent result = new Intent();
                result.setData(Uri.parse(curItemPath));
                setResult(RESULT_OK, result);
                finish();
            } else {
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra(CropImageActivity.ARG_IMAGE_URI, curItemPath);
                intent.putExtra(CropImageActivity.ARG_CROP_MODE, mCropMode);
                startActivityForResult(intent, PICK_MEDIA);
            }
        }
    }

    @Override
    public void onDirectoryClick(Directory medium) {
        setScreenTitle(medium.getName());
        String path = medium.getPath();
        replaceFragment(MediaFragment.newInstance(path), true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_MEDIA && data != null) {
                final Intent result = new Intent();
                final String path = data.getData().getPath();
                final Uri uri = Uri.fromFile(new File(path));
                result.setData(uri);
                result.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                setResult(RESULT_OK, result);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
