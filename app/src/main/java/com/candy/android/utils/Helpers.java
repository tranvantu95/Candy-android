package com.candy.android.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.manager.SettingManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by quybro on 18/10/2016.
 */

public class Helpers {
    private static ProgressDialog sProgressDialog;

    public static void replaceFragment(FragmentManager fragmentManager, @IdRes int id, @NonNull Fragment fragment) {
        if (fragmentManager == null)
            return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean isExistInBackStack = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName()) != null;
        // if fragment existed in fragments stack, we will remove it to ensure that only one instance of
        // that fragment in stack
        if (isExistInBackStack) {
            RkLogger.d("Check crash >> ", "popBackStack");
            fragmentManager.popBackStack(fragment.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        RkLogger.d("Helpers", "is fragment in back stack: " + isExistInBackStack);
        fragmentTransaction.add(id, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * get top fragment in back stack
     */
    public static Fragment getCurrentFragment(FragmentManager fragmentManager) {
        int fragmentStackSize = fragmentManager.getBackStackEntryCount();
        if (fragmentStackSize < 1) {
            return null;
        }
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentStackSize - 1).getName();
        RkLogger.d("Helpers", "fragment name: " + fragmentTag);
        if (!TextUtils.isEmpty(fragmentTag)) {
            return fragmentManager.findFragmentByTag(fragmentTag);
        }
        return null;
    }

    public static void replaceFragmentAndAddToBackStack(FragmentManager fragmentManager, @IdRes int id, @NonNull Fragment fragment) {
        if (fragmentManager == null)
            return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean isExistInBackStack = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName()) != null;
        // if fragment existed in fragments stack, we will remove it to ensure that only one instance of
        // that fragment in stack
        if (isExistInBackStack) {
            fragmentManager.popBackStack(fragment.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        RkLogger.d("Helpers", "is fragment in back stack: " + isExistInBackStack);
        fragmentTransaction.replace(id, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            if (dialogFragment.isAdded()) {
                dialogFragment.dismiss();
            }
//            dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
            fragmentManager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
        }
    }

    public static void showCircleProgressDialog(Context context) {
        try {
            if (sProgressDialog != null && sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sProgressDialog == null) {
            sProgressDialog = new ProgressDialog(context, R.style.ProgressTheme);
            //TODO (knv) @Quy bro, we might need these 2 lines
            sProgressDialog.setCancelable(false);
            sProgressDialog.setCanceledOnTouchOutside(false);
            sProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        }
        try {
            sProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissCircleProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }

    public static int getIndexOfStringFromArray(@NonNull String stringWantToIndex, @NonNull String[] targets) {
        for (int i = 0; i < targets.length; i++) {
            if (targets[i].equalsIgnoreCase(stringWantToIndex)) {
                return i;
            }
        }
        return 0;
    }

    public static String getStringOfIndexFromArray(String indexStr, @NonNull String[] targets) {
        int index = 0;
        if (!TextUtils.isEmpty(indexStr) && TextUtils.isDigitsOnly(indexStr)) {
            index = Integer.parseInt(indexStr);
            if (index >= targets.length || index < 0) {
                index = 0;
            }
        }
        return targets[index];
    }

    public static AlertDialog showAlertDialog(Context context, CharSequence title, CharSequence message,
                                       CharSequence btnStart, CharSequence btnEnd, boolean cancelable,
                                       final View.OnClickListener onStartClick,
                                       final View.OnClickListener onEndClick,
                                       final View.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(cancelable);
        if(cancelable) builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(onCancel != null) onCancel.onClick(null);
            }
        });

        View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvMessage = (TextView) view.findViewById(R.id.message);
        TextView tvBtnStart = (TextView) view.findViewById(R.id.btn_start);
        TextView tvBtnEnd = (TextView) view.findViewById(R.id.btn_end);

        if(TextUtils.isEmpty(title)) tvTitle.setVisibility(View.GONE);
        else tvTitle.setText(title);

        if(TextUtils.isEmpty(message)) tvMessage.setVisibility(View.GONE);
        else tvMessage.setText(message);

        if(TextUtils.isEmpty(btnStart)) tvBtnStart.setVisibility(View.GONE);
        else {
            tvBtnStart.setText(btnStart);
            tvBtnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onStartClick != null) onStartClick.onClick(v);
                    dialog.dismiss();
                }
            });
        }

        if(TextUtils.isEmpty(btnEnd)) tvBtnEnd.setVisibility(View.GONE);
        else {
            tvBtnEnd.setText(btnEnd);
            tvBtnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onEndClick != null) onEndClick.onClick(v);
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    /**
     * knv added
     */
    public static String getStringAreaFromArray(int index, @NonNull String[] targets) {
        return getStringOfIndexFromArray(String.valueOf(index), targets);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Resources res, Bitmap src, float radius) {
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(res, src);
        dr.setCornerRadius(radius);
        return dr;
    }

    public static Bitmap getRandomAvatarAsBitmap(Context context, int reqWidth, int reqHeight) {
        try {
            String[] avatarPaths = context.getAssets().list(Define.AVATARS_ASSETS_FOLDER);
            if (avatarPaths.length > 0) {
                String fileName = avatarPaths[new Random().nextInt(avatarPaths.length)];
                InputStream isBitmap = context.getAssets().open(Define.AVATARS_ASSETS_FOLDER + "/" + fileName);
                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                Rect rect = new Rect(0, 0, reqWidth, reqHeight);
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(isBitmap, rect, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.outWidth = reqWidth;
                options.outHeight = reqHeight;

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                return scaleCenterCrop(BitmapFactory.decodeStream(isBitmap, rect, options), reqWidth, reqHeight);
            }
        } catch (IOException e) {
            //knv added
            RkLogger.e("IDK-Helpers", "IOException:", e);
        } catch (NullPointerException npe) {
            //knv added
            RkLogger.e("IDK-Helpers", "NullPointerException:", npe);
        }
        return null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getImageUrlDomain(Context context, String s) {
        return SettingManager.getInstance(context).getConfig().getProfileImageUrlDomain() + s;
    }

    public static void setTextStatus(TextView textView, int status, String statusMsg) {
        switch (status) {
            case 0:
                textView.setText(Define.STATUS_0);
                break;
//            case 1:
//                textView.setText(Define.STATUS_1);
//                break;
//            case 2:
//                textView.setText(Define.STATUS_2);
//                break;
//            case 3:
//                textView.setText(Define.STATUS_3);
//                break;
            default:
                textView.setText(statusMsg);
//                textView.setText(Define.STATUS_0);
                break;
        }
    }


    public static String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return Define.Fields.FIELD_MONDAY;
            case 2:
                return Define.Fields.FIELD_TUESDAY;
            case 3:
                return Define.Fields.FIELD_WEDNESDAY;
            case 4:
                return Define.Fields.FIELD_THURSDAY;
            case 5:
                return Define.Fields.FIELD_FRIDAY;
            case 6:
                return Define.Fields.FIELD_SATURDAY;
            default:
                return Define.Fields.FIELD_SUNDAY;
        }
    }

    public static float pxToDp(Context context, float pixel){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return pixel / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float pxToSp(Context context, float pixel){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return pixel / metrics.scaledDensity;
    }

    public static int[] getCenterViewLocation(View view, Activity activity) {
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        return loc;
    }
}
