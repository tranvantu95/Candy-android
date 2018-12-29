package com.candy.android;

import android.app.Activity;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.utils.AdjustUtils;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.fmaru.app.livechatapp.Globals;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

//import com.deploygate.sdk.DeployGate;

/**
 * Created by quannt on 10/12/2016.
 * <p>
 * Description: Application class for initialize app environment
 */

public class HimecasApplication extends Globals {

    @Override
    public void onCreate() {
        super.onCreate();
        // for distribution
//        DeployGate.install(this);
        Glide.get(this)
                .setMemoryCategory(MemoryCategory.HIGH);
        try {
            ShortcutBadger.removeCount(this);
        }catch (Exception e){

        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //Initialize Adjust sdk
        AdjustUtils.initialize(this);

        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        EmojiManager.install(new EmojiOneProvider());
    }

    @Override
    public void onTerminate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onTerminate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(PropertyChangedEvent property) {
        if (property != null) {
            if (property.getType() == PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED) {
                int badgeCount = property.getValue();
                if (0 == badgeCount) {
                    try {
                        ShortcutBadger.removeCountOrThrow(this);
                    } catch (ShortcutBadgeException ignored) {
                    }
                } else {
                    try {
                        ShortcutBadger.applyCountOrThrow(this, badgeCount);
                    } catch (ShortcutBadgeException ignored) {
                    }
                }
            } else if (property.getType() == PropertyChangedEvent.TYPE_NEW_MESSAGE_INCREASE) {
                MemberInformation memberInfo = SettingManager.getInstance(this).getMemberInformation();
                int messageCount = memberInfo.getNotOpenCount();
                int badgeCount = messageCount + 1;
                ShortcutBadger.applyCount(this, badgeCount);
                // Update user info
                memberInfo.setNotOpenCount(badgeCount);
                SettingManager.getInstance(this).save(memberInfo);
            } else if (property.getType() == PropertyChangedEvent.TYPE_LOG_OUT) {
                ShortcutBadger.removeCount(this);
            }

        }
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }


    }

}
