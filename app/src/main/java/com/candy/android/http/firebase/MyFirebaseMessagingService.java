package com.candy.android.http.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.activity.VideoCallActivity;
import com.candy.android.configs.Define;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.PerformerProfilesResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PerformerDetail;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.utils.RkLogger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "IDK-FirebaseMessaging";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RkLogger.e(TAG + remoteMessage.getData().keySet().toString());
        // Check user logged in
        boolean isLoggedIn = SettingManager.getInstance(this).hasMember();
        if (isLoggedIn) {
            sendNotification(remoteMessage);
        }
    }

    /**
     * Message format
     * <p>
     * {
     * "GCM":{
     * "data":{
     * "title":"☆ちょこ☆☆rちゃんからメールを受信",
     * "message":"☆ちょこ☆☆rちゃんのメールを今すぐチェックしましょう！",
     * "url":"performerCode=123323687",
     * "img":"http://picture.hime-cas.com/images/p3-123323687",
     * "sound":"mail_push.wav",
     * "vibrate":true,
     * "lights":true,
     * "type":2,
     * "code":"123323687"
     * }
     * },
     * "APNS":{
     * "aps":{
     * "sound":"default"
     * }
     * }
     * }
     *
     * @param remoteMessage Message from remote
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        final String title = data.containsKey("title") ? data.get("title") : null;
        final String message = data.containsKey("message") ? data.get("message") : null;
        final String img = data.containsKey("img") ? data.get("img") : null;
        final boolean vibrate = data.containsKey("vibrate") && Boolean.parseBoolean(data.get("vibrate"));
        final boolean lights = data.containsKey("lights") && Boolean.parseBoolean(data.get("lights"));
        final int type = data.containsKey("type") ? Integer.parseInt(data.get("type")) : Integer.MIN_VALUE;
        final String code = data.containsKey("code") ? data.get("code") : null;

        final Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        if (TextUtils.isEmpty(img)) {
            sendNotification(title, message, img, defaultBitmap, vibrate, lights, type, code);
        } else {
            try {
                Glide.with(this)
                        .load(img)
                        .asBitmap()
                        .override(defaultBitmap.getWidth(), defaultBitmap.getHeight())
                        .centerCrop()
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                sendNotification(title, message, img, defaultBitmap, vibrate, lights, type, code);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                RkLogger.e("W:" + resource.getWidth() + " H:" + resource.getHeight());
                                sendNotification(title, message, img, resource, vibrate, lights, type, code);
                                return false;
                            }
                        })
                        .into(defaultBitmap.getWidth(), defaultBitmap.getHeight())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title, String message, String img, Bitmap largeIcon, boolean vibrate, boolean light, int type, final String code) {
        RkLogger.e(TAG, "::sendNotification Title=" + title);
        RkLogger.e(TAG, "::sendNotification Message=" + message);
        RkLogger.e(TAG, "::sendNotification Code=" + code);
        RkLogger.e(TAG, "::sendNotification Type=" + type);
        Spanned newTitle = fromHtml(title);
        Spanned newMessage = fromHtml(message);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (type) {
            case 3:
                intent.setAction(Define.IntentActions.ACTION_OPEN_HOME);
                intent.setClass(this, MainActivity.class);
                break;
            case 2: {

                if (MainActivity.active || PerformerProfileActivity.active || VideoCallActivity.active) {
                    EventBus.getDefault().post(new PropertyChangedEvent(1,
                            PropertyChangedEvent.TYPE_NEW_MESSAGE_INCREASE));
                    //in app
                    String performerName = title;
                    final Intent pushNotification = new Intent(Define.IntentActions.ACTION_INAPP);
                    if (!TextUtils.isEmpty(title)) {
                        if (title.contains(getString(R.string.nfc_performer_name_chat_suffix))) {
                            performerName = title.substring(0, title.indexOf(getString(R.string.nfc_performer_name_chat_suffix)));
                        }
                        pushNotification.putExtra(Define.IntentExtras.PERFORMER_NAME, performerName);
                    }
                    pushNotification.putExtra(Define.IntentExtras.PERFORMER_MESSAGE, message);
                    pushNotification.putExtra(Define.IntentExtras.PERFORMER_IMAGE, img);
                    pushNotification.putExtra(Define.IntentExtras.PERFORMER_CODE, Integer.valueOf(code));

                    ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
                    String mId = SettingManager.getInstance(this).getMemberInformation().getId();
                    String mPassword = SettingManager.getInstance(this).getMemberInformation().getPass();
                    Call<PerformerProfilesResponse> call = apiService.getPerformerProfiles(mId, mPassword, Integer.valueOf(code));
                    call.enqueue(new Callback<PerformerProfilesResponse>() {
                        @Override
                        public void onResponse(Call<PerformerProfilesResponse> call, Response<PerformerProfilesResponse> response) {
                            if (response == null || response.body() == null)
                                return;
                            PerformerDetail performerDetail = response.body().getPerformer();
                            pushNotification.putExtra(Define.IntentExtras.PERFORMER_AGE, performerDetail.getAge());
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                        }

                        @Override
                        public void onFailure(Call<PerformerProfilesResponse> call, Throwable t) {
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                        }
                    });
                    return;
                } else {
                    // Update icon
                    EventBus.getDefault().post(new PropertyChangedEvent(1,
                            PropertyChangedEvent.TYPE_NEW_MESSAGE_INCREASE));

                    intent.setAction(Define.IntentActions.ACTION_CHAT);
                    intent.setClass(this, MainActivity.class);
                    intent.putExtra(Define.IntentExtras.PERFORMER_CODE, Integer.valueOf(code));
                    if (!TextUtils.isEmpty(title)) {
                        String performerName = title;
                        if (title.contains(getString(R.string.nfc_performer_name_chat_suffix))) {
                            performerName = title.substring(0, title.indexOf(getString(R.string.nfc_performer_name_chat_suffix)));
                        }
                        intent.putExtra(Define.IntentExtras.PERFORMER_NAME, performerName);
                    }
                    intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, img);
                    // knv added, Bug #21051 [015-Notification] di chuyển MH khi tab vào push
                    intent.putExtra(Define.IntentExtras.PERFORMER_AGE, Define.REQUEST_FAILED);
                }
            }
            break;
            case 0:
                intent.setAction(Define.IntentActions.ACTION_PERFORMER_DETAIL);
                intent.setClass(this, PerformerProfileActivity.class);
                PerformerOnline performerOnline = new PerformerOnline();
                //Only pass code and profileImageUrl
                performerOnline.setCode(Integer.parseInt(code));
                performerOnline.setProfileImageUrl(img);
                intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
                intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
                break;
            case 10: {
                intent.setAction(Define.IntentActions.ACTION_BLOG_DETAIL);
                intent.setClass(this, MainActivity.class);
                intent.putExtra(Define.IntentExtras.POST_ID, Integer.parseInt(code));
                if (!TextUtils.isEmpty(title)) {
                    String performerName = title;
                    if (title.contains(getString(R.string.nfc_performer_name_blog_suffix))) {
                        performerName = title.substring(0, title.indexOf(getString(R.string.nfc_performer_name_blog_suffix)));
                    }
                    intent.putExtra(Define.IntentExtras.PERFORMER_NAME, performerName);
                }
                intent.putExtra(Define.IntentExtras.POSITION, -1);
            }
            break;
        }
        Bitmap notificationIcon = largeIcon == null ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : largeIcon;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(notificationIcon)
                .setSmallIcon(R.drawable.ic_ntf_small)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentTitle(newTitle)
                .setContentText(newMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (vibrate) {
            notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        if (light) {
            notificationBuilder.setLights(Color.RED, 3000, 3000);
        }

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        RkLogger.d(TAG, "::sendNotification => notificationManager.notify");
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
