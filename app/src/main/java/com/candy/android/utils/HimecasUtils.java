package com.candy.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.BaseActivity;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.VideoCallActivity;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogPointMissing;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.AnimatedImageSpanFactory;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BaseMember;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.model.eventBus.UpdateMissionInfoEvent;
import com.candy.android.utils.gifutils.AnimatedImageSpan;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.fmaru.app.livechatapp.util.RkLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Favo
 * Created on 19/10/2016.
 */

public class HimecasUtils {
    private static final String TAG = "IDK-HimecasUtils";
    private static final Object IMAGE_TEMP_FOLDER = "HIME_IMAGE";

    private static final String GIF_NOTATION_REGEX = "\\[i:[0-9]+\\]";
    private static final String GIF_URL_REGEX = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
    private static final String TODAY_JP = "本日";
    private static final String YESTERDAY_JP = "昨日(EEE)";
    private static final String ONLY_TIME = "HH:mm";
    private static final String FULL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_WITHOUT_TIME_AND_YEAR_JP = "MM/dd (EEE)";
    private static final String DATE_WITHOUT_TIME_JP = "yyyy/MM/dd (EEE)";
    private static final String DATE_WITHOUT_YEAR_JP = "MM/dd (EEE) HH:mm";
    private static final String DATE_WITHOUT_YEAR_JP_2 = "MM月dd日(E) HH時mm分";
    private static final String DATE_WITH_YEAR_JP = "YYYY/MM/dd (EEE) HH:mm";


    private static final int VALID_AGE = 18;
    private static final String TODAY_WITH_TIME_JP = "本日 HH:mm";
    private static final String YESTERDAY_WITH_TIME_JP = "昨日(EEE) HH:mm";

    private static final DateTimeFormatter mTodayFormatter =
            DateTimeFormat.forPattern(TODAY_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mYesterdayFormatter =
            DateTimeFormat.forPattern(YESTERDAY_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mOnlyTimeFormatter =
            DateTimeFormat.forPattern(ONLY_TIME);

    private static final DateTimeFormatter mFullDateTimeFormatter =
            DateTimeFormat.forPattern(FULL_DATE_TIME);

    private static final DateTimeFormatter mDateWithoutTimeAndYearFormatter =
            DateTimeFormat.forPattern(DATE_WITHOUT_TIME_AND_YEAR_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mDateWithoutTimeFormatter =
            DateTimeFormat.forPattern(DATE_WITHOUT_TIME_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mDateWithoutYearFormatter =
            DateTimeFormat.forPattern(DATE_WITHOUT_YEAR_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mDateWithoutYearFormatter2 =
            DateTimeFormat.forPattern(DATE_WITHOUT_YEAR_JP_2).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mDateWithYearFormatter =
            DateTimeFormat.forPattern(DATE_WITH_YEAR_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mTodayWithTimeFormatter =
            DateTimeFormat.forPattern(TODAY_WITH_TIME_JP).withLocale(Locale.JAPAN);

    private static final DateTimeFormatter mYesterdayWithTimeFormatter =
            DateTimeFormat.forPattern(YESTERDAY_WITH_TIME_JP).withLocale(Locale.JAPAN);

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @SuppressWarnings("deprecation")
    public static String decodeUrlEncodedString(final String s, boolean format) {
        try {
            if (TextUtils.isEmpty(s)) {
                return null;
            }
            //decoding
            final String decoded = URLDecoder.decode(s, Define.CONST_UTF_8);
            RkLog.d(TAG, s + " decode => " + decoded);

            //replace new line feed
            String sequence = replaceLineFeed(decoded);
            // replace space character
            sequence = replaceWhiteSpace(sequence);
            // convert from html
            sequence = convertFromHtml(sequence).toString();

            RkLog.d(TAG, s + " fromHtml => " + sequence);

            //remove empty line
            sequence = noTrailingWhiteLines(sequence).toString();

            return sequence.trim();

        } catch (Exception uee) {
            RkLogger.w(TAG, "Warning:", uee);
        }

        return s.trim();
    }

    private static String replaceWhiteSpace(String sequence) {
        return sequence != null ? sequence.replace("&nbsp;", " ") : null;
    }

    public static Spanned convertFromHtml(String sequence) {
        if (TextUtils.isEmpty(sequence)) {
            sequence = "";
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(sequence);
        } else {
            return Html.fromHtml(sequence, Html.FROM_HTML_MODE_LEGACY);
        }
    }

    private static String replaceLineFeed(String s) {
        return s != null ? s.replace("\n", "<br />") : null;
    }

    public static String encodeUrlString(String text) {
        try {
            return URLEncoder.encode(text, Define.CONST_UTF_8);
        } catch (Exception uee) {
            RkLogger.w(TAG, "Warning:", uee);
        }

        return text;
    }

    /**
     * Remove empty new line
     * fix an issue when Html automatically adds new empty line
     */
    private static CharSequence noTrailingWhiteLines(CharSequence text) {

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    /**
     * construct a today representation
     *
     * @return today representation
     */
    public static String constructToday() {
        //calculate today
        return new DateTime().toString(mFullDateTimeFormatter);
    }

    private static String formatSentEmailOnlyTime(DateTime date) {
        return date.toString(mOnlyTimeFormatter);
    }

    public static String formatSentEmailOnlyTime(String sendDate) {
        try {
            DateTime sentDate = mFullDateTimeFormatter.parseDateTime(sendDate);
            return formatSentEmailOnlyTime(sentDate);
        } catch (NullPointerException npe) {
            RkLogger.w(TAG, "unable to parse null date");
            return null;
        }
    }

    private static String formatSentEmailDateOnlyDate(DateTime sentDate) {

        Date today = Calendar.getInstance().getTime();

        if (isToday(sentDate)) {
            return sentDate.toString(mTodayFormatter);
        } else if (isYesterday(sentDate)) {
            return sentDate.toString(mYesterdayFormatter);
        } else {
            if (isInThisYear(sentDate)) {
                return sentDate.toString(mDateWithoutTimeAndYearFormatter);
            } else {
                return sentDate.toString(mDateWithoutTimeFormatter);
            }
        }
    }

    public static String formatSentEmailDateOnlyDate(String sendDateStr) {
        if (TextUtils.isEmpty(sendDateStr)) {
            return null;
        }

        try {
            DateTime sentDate = mFullDateTimeFormatter.parseDateTime(sendDateStr);

            return formatSentEmailDateOnlyDate(sentDate);

        } catch (Exception e) {
            RkLogger.w(TAG, "unable to parse given date " + sendDateStr);
            return sendDateStr;
        }
    }

    private static String formatSentEmailDateAndTime(DateTime date) {

        if (isToday(date)) {
            return date.toString(mTodayWithTimeFormatter);
        } else if (isYesterday(date)) {
            return date.toString(mYesterdayWithTimeFormatter);
        } else {
            if (isInThisYear(date)) {
                return date.toString(mDateWithoutYearFormatter);
            } else {
                return date.toString(mDateWithYearFormatter);
            }
        }
    }

    public static String formatSentEmailDateAndTime(String sendDateStr) {
        try {
            DateTime sentDate = mFullDateTimeFormatter.parseDateTime(sendDateStr);

            return formatSentEmailDateAndTime(sentDate);

        } catch (Exception e) {
            RkLogger.w(TAG, "unable to parse given date " + sendDateStr);
            return sendDateStr;
        }
    }

    public static boolean isToday(DateTime day) {
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime inputDay = day.withTimeAtStartOfDay();

        return inputDay.isEqual(today);
    }

    public static boolean isYesterday(DateTime day) {
        DateTime yesterday = new DateTime().withTimeAtStartOfDay().minusDays(1);
        DateTime inputDay = day.withTimeAtStartOfDay();

        return inputDay.isEqual(yesterday);
    }

    public static boolean isInThisYear(DateTime day) {
        DateTime today = new DateTime().withTimeAtStartOfDay();

        return day.getYear() == today.getYear();
    }

    public static Date convertStringToDate(String format, String time) {
        DateFormat df = new SimpleDateFormat(format, Locale.JAPANESE);
        try {
            return df.parse(time);
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
        return null;
    }

    /**
     * Use this method to tell about point and/or notice changed
     */
    public static void postEventBus(Context context, @Nullable final MemberInformation member) {

        SettingManager manager = SettingManager.getInstance(context);

        if (member != null) {
            manager.save(member);
        }

        if (manager.hasMember()) {
            final MemberInformation storedMember = manager.getMemberInformation();

            RkLogger.d("IDK-EventBus", "postEventBus for new message: " + storedMember.getNotOpenCount());
            EventBus.getDefault().post(new PropertyChangedEvent(storedMember.getNotOpenCount(),
                    PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED));

            RkLogger.d("IDK-EventBus", "postEventBus for new point: " + storedMember.getPoint());
            EventBus.getDefault().post(new PropertyChangedEvent(storedMember.getPoint(),
                    PropertyChangedEvent.TYPE_POINT_CHANGED));
        }
    }

    /**
     * BaseMember version
     * Use this method to tell about point and/or notice changed
     *
     * @param member
     */
    public static void postEventBus(Context context, @Nullable final BaseMember member) {

        SettingManager manager = SettingManager.getInstance(context);

        if (manager.hasMember()) {
            final MemberInformation storedMember = manager.getMemberInformation();
            if (member != null) {
                storedMember.setPoint(member.getPoint());
                storedMember.setNotOpenCount(member.getNotOpenCount());
                manager.save(storedMember);
            }

            RkLogger.d("IDK-EventBus", "postEventBus for new message: " + storedMember.getNotOpenCount());
            EventBus.getDefault().post(new PropertyChangedEvent(storedMember.getNotOpenCount(),
                    PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED));

            RkLogger.d("IDK-EventBus", "postEventBus for new point: " + storedMember.getPoint());
            EventBus.getDefault().post(new PropertyChangedEvent(storedMember.getPoint(),
                    PropertyChangedEvent.TYPE_POINT_CHANGED));
        }
    }

    /**
     * Set textview text to a spanned string, contains gif image
     *
     * @param textView
     * @param text
     */
    public static void buildGiffableSpanTextView(@NonNull final TextView textView, final String text) {
        Pattern pattern = Pattern.compile(GIF_NOTATION_REGEX);

        Matcher matcher = pattern.matcher(text);

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);

        while (matcher.find()) {
            System.out.format("In \"%s\", I found the text" +
                            " \"%s\" starting at " +
                            "index %d and ending at index %d.%n",
                    text,
                    matcher.group(),    // this is emoCode
                    matcher.start(),
                    matcher.end());

            AnimatedImageSpan ais = AnimatedImageSpanFactory.createAnimatedImageSpan(textView, matcher.group(), AnimatedImageSpanFactory.CODE_TYPE_MESSAGE);
            stringBuilder.setSpan(ais, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(stringBuilder);
    }

    public static void buildGiffTextView(@NonNull TextView textView, String text) {
        Pattern pattern = Pattern.compile(GIF_URL_REGEX);

        Matcher matcher = pattern.matcher(text);

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);

        int start = 0;
        while (matcher.find()) {
            AnimatedImageSpan ais = AnimatedImageSpanFactory.createAnimatedImageSpan(textView, matcher.group(), AnimatedImageSpanFactory.CODE_TYPE_GIFF);
            stringBuilder.setSpan(ais, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Spanned strHtml = Html.fromHtml(text.substring(start, matcher.start()));
            stringBuilder.setSpan(strHtml, start, matcher.start(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = matcher.end();
        }

        textView.setText(stringBuilder);
    }

    /**
     * Thanks to http://stackoverflow.com/a/9293885/1890713
     * Alternatively, you can use FileChannel to copy a file. It might be faster than the byte copy method when copying a large file. You can't use it if your file is bigger than 2GB though.
     */
    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static File createUniqueFileFrom(@NonNull Context context, String filePath) {
        try {
            File imageCacheDir = new File(context.getCacheDir().getPath() + File.separator + IMAGE_TEMP_FOLDER);

            if (!imageCacheDir.exists()) {
                boolean result = imageCacheDir.mkdirs();
                RkLogger.d(TAG, "Create image cache directory + " + imageCacheDir.getPath() + ": " + result);
            }

            File origin = new File(filePath);
            File tempFile = File.createTempFile(
                    Define.IMAGE_TEMP_PREFIX, Define.IMAGE_JPG_POSTFIX, imageCacheDir);
            copy(origin, tempFile);

            return tempFile;
        } catch (IOException ioe) {
            RkLogger.e(TAG, "createTempFile exception: ", ioe);
        }

        return null;
    }

    public static void deleteImageCacheFolder(Context context) {
        try {
            // get image cache directory
            File imageCacheDir = new File(context.getCacheDir().getPath() + File.separator + IMAGE_TEMP_FOLDER);

            // is it exists
            if (imageCacheDir.exists()) {
                // get files in this directory
                File[] imageFiles = imageCacheDir.listFiles();

                // there are files
                if (imageFiles != null) {

                    // looping, and delete files
                    boolean result;
                    for (File file : imageFiles) {
                        if (file != null) {
                            result = file.delete();
                            RkLogger.d(TAG, "deleting " + file.getPath() + ": " + result);
                        }
                    }
                }
            }

        } catch (Exception ioe) {
            RkLogger.e(TAG, "createTempFile exception: ", ioe);
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, DisplayMetrics metrics) {
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        RkLogger.d(TAG, "comparing version: " + oldVersionName + " vs " + newVersionName);
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

    public static void buildBuyPointIntent(@NonNull String productId, @NonNull Context context) {
        if (SettingManager.getInstance().getMemberInformation().getCode() == 0) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showDialogErrorMemberCode(context);
            } else {
                ((VideoCallActivity) context).showDialogErrorMemberCode(context);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String dataStr = context.getString(R.string.himecas_web_scheme);
            dataStr += "://";
            dataStr += context.getString(R.string.himecas_iap_host);
            dataStr += String.format("/?id=%s", productId);

            Uri data = Uri.parse(dataStr);
            intent.setData(data);

            context.startActivity(intent);
        }
    }

    public static void routeToFreePointScreen(@NonNull Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_FREE_POINT);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void routeToPurchasePointScreen(@NonNull Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_PURCHASE_POINT);
        activity.startActivity(intent);
    }

    public static boolean canAppearInList(int functionType, String productId) {
        if (functionType == DialogPointMissing.TYPE_MESSAGE) {

            for (int i = Define.PRODUCTS_FOR_MESSAGING.length - 1; i >= 0; i--) {
                if (Define.PRODUCTS_FOR_MESSAGING[i].contentEquals(productId)) {
                    return true;
                }
            }

        }else if (functionType == DialogPointMissing.TYPE_MESSAGE_IMAGE_ATTACH) {

            for (int i = Define.PRODUCTS_FOR_MESSAGING_IMAGE_ATTACH.length - 1; i >= 0; i--) {
                if (Define.PRODUCTS_FOR_MESSAGING_IMAGE_ATTACH[i].contentEquals(productId)) {
                    return true;
                }
            }

        } else if (functionType == DialogPointMissing.TYPE_VIDEO_CHAT) {
            for (int i = Define.PRODUCTS_FOR_VIDEO_CHAT.length - 1; i >= 0; i--) {
                if (Define.PRODUCTS_FOR_VIDEO_CHAT[i].contentEquals(productId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void updateMemberInfoInSilent(final Context context) {
        if (SettingManager.getInstance(context).hasMember()) {
            String id = SettingManager.getInstance(context).getMemberInformation().getId();
            String password = SettingManager.getInstance(context).getMemberInformation().getPass();

            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
            //create call object
            Call<ApiMemberResponse> call = apiService.getMemberInfo(id, password);
            // send call object
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {

                    if (response == null || response.body() == null || response.body().getMember() == null)
                        return;
                    MemberInformation memberInfo = response.body().getMember();
                    Log.i("3 day", "" + memberInfo.ismIs3Day());
                    HimecasUtils.postEventBus(context, memberInfo);
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                }
            });
        }
    }

    public static int numberDigits(int n) {
        int i = 0;
        if (n == 0) {
            return 1;
        }
        while (n > 0) {
            n /= 10;
            i++;
        }
        return i;
    }

    public static long calculateMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - VALID_AGE);
        return calendar.getTimeInMillis();
    }


    public static final String DAY_TIME_FORMAT = "%d:%02d:%02d";

    public static String millisecondToDayTime(long millisecond) {
        int hour, minute, second;
        hour = (int) (millisecond / 3600);
        minute = (int) ((millisecond - hour * 3600) / 60);
        second = (int) (millisecond % 60);
        return String.format(Locale.getDefault(), DAY_TIME_FORMAT, hour, minute, second);
    }

    public static void checkAndUpdatePoint(String currentUrl) {
        if(currentUrl != null && currentUrl.equals("https://galaxy-live.jp/webview/member/point/bitcash/complete/")
                ||currentUrl.contains("credit-cgiserver.com")){
            EventBus.getDefault().post(new UpdateMissionInfoEvent());
        }
    }
}
