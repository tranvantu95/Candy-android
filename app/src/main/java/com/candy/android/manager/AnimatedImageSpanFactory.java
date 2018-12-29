package com.candy.android.manager;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.gifutils.AnimatedGifDrawable;
import com.candy.android.utils.gifutils.AnimatedImageSpan;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Favo
 * Created on 17/11/2016.
 */

public class AnimatedImageSpanFactory {

    private static final String TAG = "IDK-AnimatedImageSpanFactory";
    //TYPE: .gif
    public static final int CODE_TYPE_GIFF = 1;
    //TYPE : Ex: [i:2]
    public static final int CODE_TYPE_MESSAGE = 2;
    public static Map<String, Integer> mEmoCodeMaps = new HashMap<>();

    static {
        mEmoCodeMaps.put("[i:1]", R.raw.emogif_001);
        mEmoCodeMaps.put("[i:2]", R.raw.emogif_002);
        mEmoCodeMaps.put("[i:3]", R.raw.emogif_003);
        mEmoCodeMaps.put("[i:4]", R.raw.emogif_004);
        mEmoCodeMaps.put("[i:5]", R.raw.emogif_005);

        mEmoCodeMaps.put("[i:6]", R.raw.emogif_006);
        mEmoCodeMaps.put("[i:7]", R.raw.emogif_007);
        mEmoCodeMaps.put("[i:8]", R.raw.emogif_008);
        mEmoCodeMaps.put("[i:9]", R.raw.emogif_009);
        mEmoCodeMaps.put("[i:10]", R.raw.emogif_010);

        mEmoCodeMaps.put("[i:11]", R.raw.emogif_011);
        mEmoCodeMaps.put("[i:12]", R.raw.emogif_012);
        mEmoCodeMaps.put("[i:13]", R.raw.emogif_013);
        mEmoCodeMaps.put("[i:14]", R.raw.emogif_014);
        mEmoCodeMaps.put("[i:15]", R.raw.emogif_015);

        mEmoCodeMaps.put("[i:16]", R.raw.emogif_016);
        mEmoCodeMaps.put("[i:17]", R.raw.emogif_017);
        mEmoCodeMaps.put("[i:18]", R.raw.emogif_018);
        mEmoCodeMaps.put("[i:19]", R.raw.emogif_019);
        mEmoCodeMaps.put("[i:20]", R.raw.emogif_020);

        mEmoCodeMaps.put("[i:21]", R.raw.emogif_021);
        mEmoCodeMaps.put("[i:22]", R.raw.emogif_022);
        mEmoCodeMaps.put("[i:23]", R.raw.emogif_023);
        mEmoCodeMaps.put("[i:24]", R.raw.emogif_024);
        mEmoCodeMaps.put("[i:25]", R.raw.emogif_025);

        mEmoCodeMaps.put("[i:26]", R.raw.emogif_026);
        mEmoCodeMaps.put("[i:27]", R.raw.emogif_027);
        mEmoCodeMaps.put("[i:28]", R.raw.emogif_028);
        mEmoCodeMaps.put("[i:29]", R.raw.emogif_029);
        mEmoCodeMaps.put("[i:30]", R.raw.emogif_030);

        mEmoCodeMaps.put("[i:31]", R.raw.emogif_031);
        mEmoCodeMaps.put("[i:32]", R.raw.emogif_032);
        mEmoCodeMaps.put("[i:33]", R.raw.emogif_033);
        mEmoCodeMaps.put("[i:34]", R.raw.emogif_034);
        mEmoCodeMaps.put("[i:35]", R.raw.emogif_035);

        mEmoCodeMaps.put("[i:36]", R.raw.emogif_036);
        mEmoCodeMaps.put("[i:37]", R.raw.emogif_037);
        mEmoCodeMaps.put("[i:38]", R.raw.emogif_038);
        mEmoCodeMaps.put("[i:39]", R.raw.emogif_039);
        mEmoCodeMaps.put("[i:40]", R.raw.emogif_040);

        mEmoCodeMaps.put("[i:41]", R.raw.emogif_041);
        mEmoCodeMaps.put("[i:42]", R.raw.emogif_042);
        mEmoCodeMaps.put("[i:43]", R.raw.emogif_043);
        mEmoCodeMaps.put("[i:44]", R.raw.emogif_044);
        mEmoCodeMaps.put("[i:45]", R.raw.emogif_045);

        mEmoCodeMaps.put("[i:46]", R.raw.emogif_046);
        mEmoCodeMaps.put("[i:47]", R.raw.emogif_047);
        mEmoCodeMaps.put("[i:48]", R.raw.emogif_048);
        mEmoCodeMaps.put("[i:49]", R.raw.emogif_049);
        mEmoCodeMaps.put("[i:50]", R.raw.emogif_050);

        mEmoCodeMaps.put("[i:51]", R.raw.emogif_051);
        mEmoCodeMaps.put("[i:52]", R.raw.emogif_052);
        mEmoCodeMaps.put("[i:53]", R.raw.emogif_053);
        mEmoCodeMaps.put("[i:54]", R.raw.emogif_054);
        mEmoCodeMaps.put("[i:55]", R.raw.emogif_055);

        mEmoCodeMaps.put("[i:56]", R.raw.emogif_056);
        mEmoCodeMaps.put("[i:57]", R.raw.emogif_057);
        mEmoCodeMaps.put("[i:58]", R.raw.emogif_058);
        mEmoCodeMaps.put("[i:59]", R.raw.emogif_059);
        mEmoCodeMaps.put("[i:60]", R.raw.emogif_060);

        mEmoCodeMaps.put("[i:61]", R.raw.emogif_061);
        mEmoCodeMaps.put("[i:62]", R.raw.emogif_062);
        mEmoCodeMaps.put("[i:63]", R.raw.emogif_063);
        mEmoCodeMaps.put("[i:64]", R.raw.emogif_064);
        mEmoCodeMaps.put("[i:65]", R.raw.emogif_065);

        mEmoCodeMaps.put("[i:66]", R.raw.emogif_066);
        mEmoCodeMaps.put("[i:67]", R.raw.emogif_067);
        mEmoCodeMaps.put("[i:68]", R.raw.emogif_068);
        mEmoCodeMaps.put("[i:69]", R.raw.emogif_069);
        mEmoCodeMaps.put("[i:70]", R.raw.emogif_070);

        mEmoCodeMaps.put("[i:71]", R.raw.emogif_071);
        mEmoCodeMaps.put("[i:72]", R.raw.emogif_072);
        mEmoCodeMaps.put("[i:73]", R.raw.emogif_073);
        mEmoCodeMaps.put("[i:74]", R.raw.emogif_074);
        mEmoCodeMaps.put("[i:75]", R.raw.emogif_075);

        mEmoCodeMaps.put("[i:76]", R.raw.emogif_076);
        mEmoCodeMaps.put("[i:77]", R.raw.emogif_077);
        mEmoCodeMaps.put("[i:78]", R.raw.emogif_078);
        mEmoCodeMaps.put("[i:79]", R.raw.emogif_079);
        mEmoCodeMaps.put("[i:80]", R.raw.emogif_080);

        mEmoCodeMaps.put("[i:81]", R.raw.emogif_081);
        mEmoCodeMaps.put("[i:82]", R.raw.emogif_082);
        mEmoCodeMaps.put("[i:83]", R.raw.emogif_083);
        mEmoCodeMaps.put("[i:84]", R.raw.emogif_084);
        mEmoCodeMaps.put("[i:85]", R.raw.emogif_085);

        mEmoCodeMaps.put("[i:86]", R.raw.emogif_086);
        mEmoCodeMaps.put("[i:87]", R.raw.emogif_087);
        mEmoCodeMaps.put("[i:88]", R.raw.emogif_088);
        mEmoCodeMaps.put("[i:89]", R.raw.emogif_089);
        mEmoCodeMaps.put("[i:90]", R.raw.emogif_090);

        mEmoCodeMaps.put("[i:91]", R.raw.emogif_091);
    }

//    public static AnimatedImageSpan getAnimatedImageSpanFromRaw(@NonNull final TextView gifTv, String emoCode) {
//        // if an animated image span specified by emoCode already exist, return it
////        if (mAnimatedImageSpanMap.containsKey(gifTv.hashCode())) {
////            return mAnimatedImageSpanMap.get(gifTv.hashCode());
////        }
//
//        //else, we create one, and add it to underlying map
//        return createAnimatedImageSpan(gifTv, emoCode);
//    }

    public static AnimatedImageSpan createAnimatedImageSpan(@NonNull final TextView gifTv, String emoCode, int type) {
        try {
            InputStream resourceStream;
            if (type == CODE_TYPE_GIFF) {
                resourceStream = gifTv.getResources().openRawResource(getEmojiResourceId(emoCode));
            } else {
                resourceStream = gifTv.getResources().openRawResource(mEmoCodeMaps.get(emoCode));
            }

            AnimatedGifDrawable.UpdateListener updateListener = new AnimatedGifDrawable.UpdateListener() {
                @Override
                public void update() {
                    gifTv.postInvalidate();
                }
            };

            Drawable drawable = new AnimatedGifDrawable(resourceStream, updateListener);
            return new AnimatedImageSpan(drawable);
        } catch (Exception ex) {
            RkLogger.e(TAG, "Warning: ", ex);
        }

        return null;
    }

    /**
     * @Param: gifUrl Example: https://livede55.com/common/img/emoji/058.gif
     * @return: Expect: R.raw.emogif_058
     */
    public static int getEmojiResourceId(String gifUrl) {
        final int EMOJI_INDEX_START = 7;
        final int EMOJI_INDEX_END = 4;

        if (gifUrl == null)
            return -1;
        int len = gifUrl.length();
        if (len > EMOJI_INDEX_START && gifUrl.contains(".gif")) {
            String emojiText = gifUrl.substring(len - EMOJI_INDEX_START, len - EMOJI_INDEX_END);
            if (TextUtils.isDigitsOnly(emojiText)) {
                int emojiNumber = Integer.valueOf(emojiText);
                String emojiCode = String.format(Locale.US, "[i:%d]", emojiNumber);
                if (mEmoCodeMaps.get(emojiCode) != null) {
                    return mEmoCodeMaps.get(emojiCode);
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }
}
