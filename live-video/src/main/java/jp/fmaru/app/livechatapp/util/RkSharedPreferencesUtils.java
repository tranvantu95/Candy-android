package jp.fmaru.app.livechatapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by datpt2 on 5/5/2016.
 */
public class RkSharedPreferencesUtils {

    /**
     * Application SharedPreferences
     */
    private static SharedPreferences sAppSharedPreferences;

    /**
     * Instance of RkSharedPreferencesUtils Object
     */
    private static RkSharedPreferencesUtils sInstance;

    /**
     * Initialize SharedPreferences
     *
     * @param context
     */
    public static void initialize(Context context, int mode) {
        if (sAppSharedPreferences == null) {
            String name = context.getApplicationContext().getPackageName();
            sAppSharedPreferences = context.getApplicationContext()
                    .getSharedPreferences(name, mode);
        }
    }

    /**
     * Get instance of RkSharedPreferencesUtils Object
     *
     * @return Return an instance of RkSharedPreferencesUtils Object
     */
    public static RkSharedPreferencesUtils getInstance() {
        if (sInstance == null) {
            sInstance = new RkSharedPreferencesUtils();
        }
        return sInstance;
    }

    private static final String FIRST_TIME_KEY = "first_time_dialog_do_show";
    public void setNotShowDialogFirstTimeAgain(boolean isDoNotShowAgain) {
        saveBoolean(FIRST_TIME_KEY, isDoNotShowAgain);
    }

    public boolean isNeedShowFirstTimeDialog() {
        return !getBoolean(FIRST_TIME_KEY, false);
    }


    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name
     * that is not a boolean.
     */
    public boolean getBoolean(String key, boolean defValue) {
        return sAppSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Set a boolean value in the preferences editor, to be written back once commit() or apply() are called.
     *
     * @param key   String: The name of the preference to modify.
     * @param value boolean: The new value for the preference.
     * @return Returns true if the new values were successfully written to persistent storage.
     */
    public boolean saveBoolean(String key, boolean value) {
        return sAppSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * Clear all data in app sharedPreferences
     *
     * @return Return true if clear data successfully
     */
    public boolean clear() {
        return sAppSharedPreferences.edit().clear().commit();
    }

    /**
     * Mark in the editor that a preference value should be removed, which
     * will be done in the actual preferences once commit is
     * called.
     * <p/>
     * <p>Note that when committing back to the preferences, all removals
     * are done first, regardless of whether you called remove before
     * or after put methods on this editor.
     *
     * @param key The name of the preference to remove.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    public boolean remove(String key) {
        return sAppSharedPreferences.edit().remove(key).commit();
    }
}