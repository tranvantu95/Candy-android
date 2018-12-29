package com.candy.android.utils;

import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import com.candy.android.configs.Define;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by anhld on 8/23/2017.
 */

public class TextHelper {

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String text) {
        if (text == null || text.isEmpty()) {
            return new SpannedString("");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static String decodeUrl(String text) {
        String result = "";
        try {
            result = URLDecoder.decode(text, "UTF-8");
            if (result != null) {
                return result.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return result;
        }
    }
}
