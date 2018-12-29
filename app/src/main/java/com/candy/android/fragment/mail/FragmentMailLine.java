package com.candy.android.fragment.mail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.adapter.MailLineAdapter;
import com.candy.android.configs.Define;
import com.candy.android.custom.gallery.activities.GalleryActivity;
import com.candy.android.custom.image_crop.CropImageActivity;
import com.candy.android.custom.image_crop.image_crop.CropImageView;
import com.candy.android.custom.views.LinearLayoutManagerWrapper;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogPointMissing;
import com.candy.android.dialog.ImageGalleryDialog;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.mail.registration.FragmentProfileRegister;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMailResponse;
import com.candy.android.http.response.ApiSendMailPictureSubPointResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.manager.chat.ChatClientManager;
import com.candy.android.model.AttachImage;
import com.candy.android.model.FooterMessage;
import com.candy.android.model.IMessage;
import com.candy.android.model.MailList;
import com.candy.android.model.MessageDetail;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.PurchasePointOption;
import com.candy.android.model.eventBus.chat.WsReadEvent;
import com.candy.android.model.eventBus.chat.WsSendEvent;
import com.candy.android.model.eventBus.chat.WsWriteEvent;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiRange;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.candy.android.configs.Define.SETTINGS.MESSAGE_POINT_PAYMENT_COST;
import static com.candy.android.configs.Define.SETTINGS.MESSAGE_WITH_IMAGE_POINT_PAYMENT_COST;

/**
 * @author Favo
 * Created on 21/10/2016.
 */
public class FragmentMailLine extends BaseFragment implements View.OnClickListener,
        MailLineAdapter.OnMessageBubbleClickedListener,
        SwipeRefreshLayout.OnRefreshListener,
        MailLineAdapter.OnPerformerImageClickedListener, MailLineAdapter.OnErrorPlaceHolderClickedListener {

    private WeakReference<UpdateMailLineStateTask> mTaskWeakRef;

    private enum UPDATE_MAIL_LINE_STATE {
        NONE,
        FIRST_LOAD,
        RELOAD,
        SENDING_MESSAGE,
        RECENTLY_RECEIVED_NEW_MESSAGE,
        MESSAGE_SENT
    }

    private enum UpdateMailLineTaskState {
        USUAL,
        NEWLY,
        NONE
    }

    public InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    Toast.makeText(getActivity(), getString(R.string.emoji_not_supported), Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
            return null;
        }
    };

    private static final String TAG = "IDK-FragmentMailLine";
    public static final int PICK_IMAGE = 100;
    private static final long DISMISS_TYPING_MESSAGE_DELAY = 3000;  // 3 SECONDS delayed before remove typing message
    private static final long SCROLL_TO_POSITION_DELAY = 333;  // 0.33 SECONDS delayed before remove typing message

    private int mPerformerCode;

    private MailLineAdapter mAdapter;
    private List<IMessage> mMailList;

    private DialogBuilder.ConfirmDialog mConfirmDialog;

    // view references
    private RecyclerView mMailLineListView;
    private EmojiEditText mNewMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mImageToBeSent;
    private Button mSendMessageBtn;
    private ImageButton mSendImageBtn;
    private ImageButton mEmojiBtn;
    private EmojiPopup emojiPopup;
    // performer typing view
    private View mPerformerTypingView;

    // property references
    private UPDATE_MAIL_LINE_STATE mMailLineState;
    private DialogPointMissing mPointMissingDialog;
    private String mTitle;
    private String mPerformerOriginName;
    private int mPerformerAge;
    private boolean mFirstLoad;
    private File mImageFile;

    // typing message related
    private String mPerformerImageUrl;
    private Handler mHandlerHelper = new Handler();
    private Runnable mDismissTypingMessage = new Runnable() {
        @Override
        public void run() {
            try {
                RkLogger.d(TAG, "dismiss typing message...");
                mPerformerTypingView.setVisibility(View.GONE);

            } catch (Exception ex) {
                RkLogger.e(TAG, "Error hide mPerformerTypingView: ", ex);
            }
        }
    };
//    private DialogBuilder.NoticeDialog mFailedDialog;
    private View mEmptyView;
    private List<String> mProbihitedWords;

    public static FragmentMailLine newInstance(int performerCode,
                                               String performerOriginName,
                                               int age,
                                               String imageUrl) {
        RkLogger.d("IDK", "FragmentMailLine " + performerCode + ", " + performerOriginName + ", " +
                imageUrl + ", age=" + age);
        FragmentMailLine fml = new FragmentMailLine();
        fml.mPerformerCode = performerCode;
        fml.mPerformerOriginName = HimecasUtils.convertFromHtml(performerOriginName).toString();
        fml.mPerformerAge = age;
        fml.mPerformerImageUrl = imageUrl;
        fml.mFirstLoad = true;
        return fml;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateUI(mPerformerAge);
    }

    @Override
    protected boolean isUpActionDisplayVisible() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create a typing message
//        constructTypingMessage();
        return inflater.inflate(R.layout.fragment_mail_line, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmptyView = view.findViewById(R.id.fl_message_empty);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);  // at startup, disable swipe-refresh

        mMailLineListView = view.findViewById(R.id.mail_line_list);
        mMailLineListView.setHasFixedSize(true);
        LinearLayoutManagerWrapper lm = new LinearLayoutManagerWrapper(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mMailLineListView.setLayoutManager(lm);

        mMailList = new ArrayList<>();
        mAdapter = new MailLineAdapter(getActivity(), mMailList);
        mAdapter.setMessageBubbleClickedListener(this);
        mAdapter.setPerformerImageClickedListener(this);
        mAdapter.setErrorPlaceHolderClickedListener(this);

        mMailLineListView.setAdapter(mAdapter);

        mNewMessage = view.findViewById(R.id.edt_new_message);
        mImageToBeSent = (ImageView) view.findViewById(R.id.image_to_be_sent);
        mSendImageBtn = view.findViewById(R.id.btn_send_image);
        mEmojiBtn = view.findViewById(R.id.btn_emojiOne);
        mSendMessageBtn = view.findViewById(R.id.btn_send_message);
        mImageToBeSent.setVisibility(View.GONE);
        mMailLineState = UPDATE_MAIL_LINE_STATE.NONE;
        ImageView performerTypingImage = view.findViewById(R.id.performerImage);
        TextView performerTypingStr = view.findViewById(R.id.tv_performer_typing);
        mPerformerTypingView = view.findViewById(R.id.ll_performer_typing_container);
        mPerformerTypingView.setVisibility(View.GONE);
        emojiPopup = EmojiPopup.Builder.fromRootView(getView()).build(mNewMessage);

        // register events
        mImageToBeSent.setOnClickListener(this);
        mSendImageBtn.setOnClickListener(this);
        mEmojiBtn.setOnClickListener(this);
        mSendMessageBtn.setOnClickListener(this);
        mNewMessage.setOnClickListener(this);
        mNewMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //no-op
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //no-op
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // member is typing, post an event to web socket
                if (null != editable && editable.length() > 0) {
                    ChatClientManager.getInstance(getActivity()).sendTypingMessage();
                }
            }
        });

        mTitle = mPerformerOriginName;

        // set typing message
        performerTypingStr.setText(getString(R.string.fragment_mail_line_performer_is_typing,
                mPerformerOriginName));

        // load avatar url
        Glide.with(this)
                .load(mPerformerImageUrl)
                .into(performerTypingImage);

        mMailLineState = UPDATE_MAIL_LINE_STATE.FIRST_LOAD;

        // disable toolbox buttons
        // sorry. wrong logic
//        mSendImageBtn.setEnabled(false);
//        mSendMessageBtn.setEnabled(false);
//        mNewMessage.setEnabled(false);

        // we call the api here, because checking isNameSet is useless, we can't have freeMail property, unless we call the api
        if (isAlive) {
            postMemberMailLineApi();
            loadProhibitedWorlds();
        }

        EventBus.getDefault().register(this);
    }

    private void loadProhibitedWorlds() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getContext(), Define.API.API_PROBIHITED_WORDS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProbihitedWords = new ArrayList<>();
                try {
                    String response = new String(responseBody, Define.CONST_UTF_8);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Define.Fields.FIELD_PROHIBITED)) {
                        JSONArray prohibitedWorldJA = jsonObject.getJSONArray(Define.Fields.FIELD_PROHIBITED);
                        for (int i = 0; i < prohibitedWorldJA.length(); i++) {
                            String word = prohibitedWorldJA.getString(i);
                            if (!TextUtils.isEmpty(word)) {
                                mProbihitedWords.add(word);
                            }
                        }
                    }

                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public boolean isContainsProhibitedWorlds(String word) {
        if (null != mProbihitedWords && !mProbihitedWords.isEmpty()) {
            for (int i = 0; i < mProbihitedWords.size(); i++) {
                if (word.contains(mProbihitedWords.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        try {
            HimecasUtils.deleteImageCacheFolder(getActivity());
        } catch (Exception ex) {
            RkLogger.e(TAG, "Exception: ", ex);
        }

        if (null != mTaskWeakRef && null != mTaskWeakRef.get() &&
                !mTaskWeakRef.get().getStatus().equals(AsyncTask.Status.FINISHED)) {
            mTaskWeakRef.get().cancel(true);
        }
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    protected void onRetryConnectionOk() {
        if (NetworkUtils.hasConnection(getActivity())) {
            if (mMailLineState == UPDATE_MAIL_LINE_STATE.FIRST_LOAD) {
                postMemberMailLineApi();
            } else if (mMailLineState == UPDATE_MAIL_LINE_STATE.RELOAD) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            showRetryConnectionDialog();
        }
    }

    /**
     * This callback is fired when there is an incoming WsSendEvent from web socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveWebSocketMessage(final WsSendEvent event) {
        // if received message from same performer code
        if (event.getSenderCode() == mPerformerCode) {
            // reset everything
            mHandlerHelper.removeCallbacks(mDismissTypingMessage);
            mPerformerTypingView.setVisibility(View.GONE);

            mFirstLoad = true;
            mMailLineState = UPDATE_MAIL_LINE_STATE.RECENTLY_RECEIVED_NEW_MESSAGE;
            // reload list
            postMemberMailLineApi();
        }
    }

    /**
     * This callback is fired when there is an incoming WsReadEvent from web socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveWebSocketMessage(final WsReadEvent event) {
        try {
            int mailCode = event.getMailCode();

            for (int pos = mMailList.size() - 1; pos >= 0; pos--) {
                // only check MessageDetail type, skip other types
                if (mMailList.get(pos) instanceof MessageDetail) {
                    MessageDetail detail = (MessageDetail) mMailList.get(pos);

                    // we found the desired message detail, specified by mailCode
                    if (detail.getMailCode() == mailCode) {
                        RkLogger.d(TAG, "we found the desired message detail, specified by mailCode=" + mailCode);
                        // set it's read property to true
                        detail.setRead(true);
                        // notify changes
                        mAdapter.notifyItemChanged(pos);
                        RkLogger.d(TAG, "Does " + mailCode + " change to read state?");
                        //finally, return
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            RkLogger.w(TAG, "Exception: ", ex);
        }
    }

    /**
     * This callback is fired when there is an incoming WsWriteEvent from web socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveWebSocketMessage(final WsWriteEvent event) {
        try {
            if (event.getPartnerCode() == mPerformerCode) {
                // present the typing event
                mPerformerTypingView.setVisibility(View.VISIBLE);

                // start callback to dismiss typing message
                RkLogger.d(TAG, "and start callback to dismiss typing message after " + DISMISS_TYPING_MESSAGE_DELAY + "ms");
                mHandlerHelper.removeCallbacks(mDismissTypingMessage);
                mHandlerHelper.postDelayed(mDismissTypingMessage, DISMISS_TYPING_MESSAGE_DELAY);
            }
        } catch (Exception ex) {
            RkLogger.w(TAG, "Exception: ", ex);
        }
    }

    private void showFreeTrialIntroduce() {
    }

    private void showIntroduceView(boolean isEmpty) {
        if (isEmpty) {
            mEmptyView.setVisibility(View.VISIBLE);
            ImageView emptyImg = mEmptyView.findViewById(R.id.no_content_image);
            emptyImg.setImageResource(R.drawable.ic_message_gray);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void postMemberMailLineApi() {
        try {
            if (checkConnectivity()) {
                return;
            }

            final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiMailResponse> call = apiService.getMemberMailLine(member.getId(),
                    member.getPass(),
                    Integer.MAX_VALUE,  // no limit, load all item
                    mPerformerCode,
                    Define.SETTINGS.DEFAULT_SORT_ORDER); // sort default field <send date> ascending

            if (mFirstLoad) {
                try {
//                    Helpers.showCircleProgressDialog(getActivity());
                    mSwipeRefreshLayout.setRefreshing(true);
                } catch (Exception ex) {
                    RkLogger.w(TAG, "Warning: ", ex);
                }
            }

            // send call object
            call.enqueue(new Callback<ApiMailResponse>() {
                @Override
                public void onResponse(Call<ApiMailResponse> call, Response<ApiMailResponse> response) {
                    RkLogger.d(TAG, "response=" + response);
                    if (response == null || response.body() == null || !isAdded())
                        return;

                    handleGetMemberMailLineSuccess(response.body());
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ApiMailResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "getMemberMailLine failed: ", t);
                    Helpers.dismissCircleProgressDialog();
                    handleCallApiFailure();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "Exception:", ex);
            Helpers.dismissCircleProgressDialog();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void handleCallApiFailure() {
        RkLogger.e(TAG, "Error calling api");
    }

    private void showDialogPointMissing(boolean imageAttached) {
        // hide soft keyboard
        hideSoftKeyboard();

        boolean dialogIsShowing = mPointMissingDialog != null &&
                mPointMissingDialog.getDialog() != null &&
                mPointMissingDialog.getDialog().isShowing();

        if (!dialogIsShowing) {

            mPointMissingDialog = DialogPointMissing.newInstance(null, !imageAttached ? DialogPointMissing.TYPE_MESSAGE : DialogPointMissing.TYPE_MESSAGE_IMAGE_ATTACH);
            mPointMissingDialog.setPurchaseOptionClickListener(new DialogPointMissing.OnPurchaseOptionClickListener() {
                @Override
                public void onOptionClick(PurchasePointOption option) {
                    if (option.getType() == PurchasePointOption.Type.PURCHASE) {
                        mPointMissingDialog.dismiss();
                        HimecasUtils.buildBuyPointIntent(option.getProductId(), getActivity());
                    } else if (option.getType() == PurchasePointOption.Type.FREE) {
                        mPointMissingDialog.dismiss();
                        HimecasUtils.routeToFreePointScreen(getActivity());
                    }
                }
            });
            mPointMissingDialog.show(getChildFragmentManager(), DialogPointMissing.TAG);
        }
    }

    private void handleGetMemberMailLineSuccess(ApiMailResponse response) {
        RkLogger.d(TAG, "getMemberMailLine return " + response.isSuccess());
        if (response.isSuccess()) {
            // update member' point and free email
            HimecasUtils.postEventBus(getActivity(), response.getMember());
            cookMailLine(response.getMailList());
            showFreeTrialIntroduce();
        }
    }

    //    boolean isFirst=true;
    private void cookMailLine(MailList mailList) {
        // if mPerformerAge is wrong, update performerAge, and the screne title again
        if (mPerformerAge == Define.REQUEST_FAILED) {
            List<MessageDetail> mailDetailList = mailList.getMailDetailList();
            if (mailDetailList != null && mailDetailList.size() > 0) {
                MessageDetail topMessage = mailDetailList.get(0);
                mPerformerAge = topMessage.getPerformerAge();
                // update age
                updateUI(mPerformerAge);
            }
        }

        mTaskWeakRef = new WeakReference<>(new UpdateMailLineStateTask());
        mTaskWeakRef.get().execute(mailList);
    }

    private IMessage constructFirstWelcomeMessage(String body) {
        if (TextUtils.isEmpty(body)) {
            RkLogger.e(TAG, "empty message, do nothing");
            return null;
        }

        IMessage message = new MessageDetail();
        message.setPerformerCode(mPerformerCode);
        message.setPerformerImageUrl(mPerformerImageUrl);
        message.setRead(true);
        message.setMemberMail(false);
        message.setBody(body);

        message.setSendDate(null);

        return message;
    }

    private IMessage constructMemberMessage() {
        //which message to be sent
        String message = null;
        try {
            message = mNewMessage.getText().toString();
        } catch (Exception ex) {
            //no-op
        }

        // check if having image, and file path to image
        boolean hasImageToSend = mImageToBeSent.getVisibility() == View.VISIBLE && mImageFile != null;

        AttachImage attachImage = new AttachImage();

        if (hasImageToSend) {
            // set attached image
            attachImage.setSmall(mImageFile.getPath());
            attachImage.setBig(mImageFile.getPath());
            attachImage.setFromCached();

        } else {
            // if not having image to send, reset image path reference
            mImageFile = null;

            if (TextUtils.isEmpty(message)) {
                // having nothing to send, return
                RkLogger.e(TAG, "empty message, do nothing");
                return null;
            }
        }

        MessageDetail theMessage = new MessageDetail();
        theMessage.setPerformerCode(mPerformerCode);
        theMessage.setMemberMail(true);
        theMessage.setBody(message);
        theMessage.setAttachImageUrl(attachImage);

        theMessage.setSendDate(HimecasUtils.constructToday());

        return theMessage;
    }

    private void prepareImageToBeSent(final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        // image file to be sent
        mImageFile = HimecasUtils.createUniqueFileFrom(getActivity(), filePath);
        RkLogger.d(TAG, "mImageFile cached: " + (null == mImageFile ? null : mImageFile.getPath()));

        // display image
        Glide.with(this)
                .load(mImageFile)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mImageToBeSent.setVisibility(View.GONE);
                        mMailLineState = UPDATE_MAIL_LINE_STATE.NONE;
                        mImageFile = null;
                        Toast.makeText(getActivity(), getString(R.string.str_unable_to_pick_this_image), Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mImageToBeSent.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(mImageToBeSent);
    }

    private void preDoSendMessage() {
        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();
        int memberPoint = member.getPoint();
        String message = mNewMessage.getText().toString();
        boolean isContainProhibitedWorld = isContainsProhibitedWorlds(message);
        if (isContainProhibitedWorld) {
            // Show dialog
            DialogBuilder.NoticeDialog2 noticeDialog = DialogBuilder.buildNoticeDialog2(getString(R.string.profile_registration_prohibited_words), null);
            Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
        } else {

            if (mImageFile != null && memberPoint < MESSAGE_WITH_IMAGE_POINT_PAYMENT_COST){
                showDialogPointMissing(true);
            } else if( mImageFile == null && memberPoint < MESSAGE_POINT_PAYMENT_COST) {
                showDialogPointMissing(false);
            } else {
                doSendMessage();
            }
        }
    }

    /**
     * Send entered message when user presses [Send] button
     */
    private void doSendMessage() {
        final IMessage messageDetail = constructMemberMessage();
        if (null == messageDetail) {
            return;
        }

        // get member information
        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

        // gather your request parameters
        RequestParams params = new RequestParams();
        try {
            params.put(Define.Fields.FIELD_ID, member.getId());
            params.put(Define.Fields.FIELD_PASS, member.getPass());
            // knv fix a bug when sending message with encoded FULL_WIDTH_SPACE, the server api refuses
            // if having image to send and message is empty, send full width space instead
            if (TextUtils.isEmpty(messageDetail.getBody(true))) {
                params.put(Define.Fields.FIELD_MESSAGE, Define.FULL_WIDTH_SPACE);
            } else {
                String message = mNewMessage.getText().toString();
                List<EmojiRange> findAllEmojis = EmojiManager.getInstance().findAllEmojis(message);
                for (EmojiRange emojiRange : findAllEmojis) {
                    String unicode = emojiRange.emoji.getUnicode();
                    String emojiStr = Emojione.unicodeToShortname(emojiRange.emoji.getUnicode());
                    message = message.replace(unicode, ":" + emojiStr + ":");
                }

//                params.put(Define.Fields.FIELD_MESSAGE, URLEncoder.encode(mNewMessage.getText().toString(),"UTF-8"));
                params.put(Define.Fields.FIELD_MESSAGE, message);
            }

            params.put(Define.Fields.FIELD_PERFORMER_CODE, mPerformerCode);
            if (mImageFile != null) {
                params.put(Define.Fields.FIELD_IMAGE, mImageFile);
            }
        } catch (FileNotFoundException ex) {
            RkLogger.w(TAG, "doSendMessage exception: ", ex);
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unknown exception: ", ex);
        }

        if (checkConnectivity()) {
            return;
        }

        mMailLineState = UPDATE_MAIL_LINE_STATE.SENDING_MESSAGE;
        // send request
        AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(20 * 1000);
        client.post(BuildConfig.SERVER_URL + Define.API.API_MEMBER_MAIL_PAY_POST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                // handle success response
                ApiMailResponse apiResponse = new ApiMailResponse();
                try {
                    String response = new String(bytes, Define.CONST_UTF_8);
                    Gson gson = new GsonBuilder().setLenient().create();
                    apiResponse = gson.fromJson(response, ApiMailResponse.class);
                } catch (Exception ex) {
                    RkLogger.w(TAG, "Exception: ", ex);
                }

                if (mImageFile != null) {
                    sendRequestSubPoint(apiResponse, (MessageDetail) messageDetail);
                } else {
                    handlePayPostMailSuccess(apiResponse, (MessageDetail) messageDetail);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                // handle failure response
                handleCallApiFailure();
                RkLogger.e(TAG, "Failed: ", throwable);
                mMailLineState = UPDATE_MAIL_LINE_STATE.NONE;
            }
        });

        // update UI
        updateToolBoxUI();
        int len = mMailList.size();
        // remove last footer
        if (len > 0) {
            if (mMailList.get(len - 1) instanceof FooterMessage) {
                mMailList.add(len - 1, messageDetail);
                mAdapter.notifyItemInserted(len - 1);
            } else {
                mMailList.add(messageDetail);
                mAdapter.notifyItemInserted(len - 1);
            }
        }
        else {
            mMailList.add(messageDetail);
            mAdapter.notifyItemInserted(0);
        }

    }

    private void sendRequestSubPoint(final ApiMailResponse apiMailResponse, final MessageDetail messageDetail) {
        // get member information
        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

        RequestParams params = new RequestParams();
        params.put(Define.Fields.FIELD_ID, member.getId());
        params.put(Define.Fields.FIELD_PASS, member.getPass());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(BuildConfig.SERVER_URL + Define.API.API_MEMBER_MAIL_PAY_POST_PICTURE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                // handle success response
                ApiSendMailPictureSubPointResponse apiResponse = new ApiSendMailPictureSubPointResponse();
                try {
                    String response = new String(bytes, Define.CONST_UTF_8);
                    Gson gson = new GsonBuilder().setLenient().create();
                    apiResponse = gson.fromJson(response, ApiSendMailPictureSubPointResponse.class);
                } catch (Exception ex) {
                    RkLogger.w(TAG, "Exception: ", ex);
                }
                if (!apiResponse.isSuccess()) {
                    // Show dialog
                    DialogBuilder.NoticeDialog2 noticeDialog = DialogBuilder.buildNoticeDialog2(apiResponse.getErrorMessage(), null);
                    Helpers.showDialogFragment(getFragmentManager(), noticeDialog);
                }
                handlePayPostMailSuccess(apiMailResponse, messageDetail);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                // handle failure response
                handleCallApiFailure();
                RkLogger.e(TAG, "Failed: ", throwable);

            }
        });
    }

    private void handlePayPostMailSuccess(ApiMailResponse response, MessageDetail messageDetail) {
        RkLogger.d(TAG, "incMemberMailPayPost response=" + response);
        String errorMsg = null;
        try {
            errorMsg = response.getMailList().getErrorMessage();
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unhandled exception: ", ex);
        }

        int memberPoint = 0;
        try {
            memberPoint = response.getMember().getPoint();
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unhandled exception: ", ex);
        }

        if (null != errorMsg && errorMsg.equals(Define.CONST_NOT_POINT)) {
            // display error placeholder
            messageDetail.setSentError(true);
            Log.i(TAG, errorMsg);
            mAdapter.notifyItemChanged(mMailList.indexOf(messageDetail));
            if (mImageFile != null && memberPoint < MESSAGE_WITH_IMAGE_POINT_PAYMENT_COST){
                showDialogPointMissing(true);
            } else if( mImageFile == null && memberPoint < MESSAGE_POINT_PAYMENT_COST) {
                showDialogPointMissing(false);
            }
        } else if (response.isSuccess()) {
            mFirstLoad = true;
            mMailLineState = UPDATE_MAIL_LINE_STATE.MESSAGE_SENT;
            // reload list
            postMemberMailLineApi();

        }
    }

    private void updateToolBoxUI() {
        mNewMessage.setText(null);
        mImageToBeSent.setVisibility(View.GONE);
        mMailLineState = UPDATE_MAIL_LINE_STATE.NONE;

        // hide soft keyboard
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSoftKeyboardText() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            if (emojiPopup.isShowing()) {
                emojiPopup.dismiss();
            }
        }
    }

    private void startProfileRegistration() {
        // preparation
        FragmentProfileRegister.storeReference(mPerformerCode, mPerformerOriginName, mPerformerAge, mPerformerImageUrl);
        // replace fragment
        FragmentMailContainer mailContainer = (FragmentMailContainer) getParentFragment();
        FragmentProfileRegister.goToStep(mailContainer, FragmentProfileRegister.SET_NAME_STEP);
    }

    @Override
    public void onLongClicked(int position) {
        showDeleteMailConfirmDialog(position);
    }

    @Override
    public void onImageClicked(int position) {
        showImageModalDialog(position);
    }

    private void showImageModalDialog(int position) {
        IMessage message = mMailList.get(position);
        if (message instanceof MessageDetail) {
            String imgUrl = ((MessageDetail) message).getAttachImageUrl().getBig();
            RkLogger.d(TAG, "imgUrl=" + imgUrl);
            ImageGalleryDialog dialog = ImageGalleryDialog.newInstance(imgUrl, "");
            Helpers.showDialogFragment(getChildFragmentManager(), dialog);
        }
    }

    private void showDeleteMailConfirmDialog(final int position) {
        Helpers.showAlertDialog(getContext(), getString(R.string.title_dialog_delete_message), null,
                getString(R.string.btn_no_jp), getString(R.string.btn_yes_jp),
                true, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //dismiss dialog
                        dismissConfirmDialog();

                        doDeleteMail(position);
                    }
                }, null);
    }

    private void doDeleteMail(final int position) {
        if (!SettingManager.getInstance(getActivity()).hasMember()) {
            return;
        }

        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);

        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

        int mailCode = Define.REQUEST_FAILED;

        // find which mail code to be deleted
        try {
            MessageDetail message = (MessageDetail) mMailList.get(position);
            mailCode = message.getMailCode();
        } catch (Exception ex) {
            RkLogger.e(TAG, "Error: ", ex);
        }

        //create call object
        Call<ApiMailResponse> call = apiService.incMemberMailDelete(member.getId(),
                member.getPass(),
                mailCode);

        Helpers.showCircleProgressDialog(getActivity());
        // send call object
        call.enqueue(new Callback<ApiMailResponse>() {
            @Override
            public void onResponse(Call<ApiMailResponse> call, Response<ApiMailResponse> response) {
                Helpers.dismissCircleProgressDialog();

                if (response == null || response.body() == null || !isAdded())
                    return;

                handleDeleteMailSuccess(response.body(), position);
            }

            @Override
            public void onFailure(Call<ApiMailResponse> call, Throwable t) {
                // Log error here since request failed
                RkLogger.e(TAG, "incMemberMailDelete failed: ", t);
                Helpers.dismissCircleProgressDialog();
            }
        });
    }

    private void handleDeleteMailSuccess(ApiMailResponse response, int position) {
        RkLogger.d(TAG, "incMemberMailDelete response=" + response);
        if (response.isSuccess()) {
            try {
                mMailList.remove(position);
                mAdapter.notifyItemRemoved(position);
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning deleting message: " + position, ex);
            }
        }
    }

    private void dismissConfirmDialog() {
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
            mConfirmDialog = null;
        }
    }

    @Override
    public void onRefresh() {
        mMailLineState = UPDATE_MAIL_LINE_STATE.RELOAD;
        postMemberMailLineApi();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_message:
                preDoSendMessage();
                // Auto scroll to bottom
                int bottom = mMailLineListView.getAdapter().getItemCount();
                mMailLineListView.scrollToPosition(bottom - 1);
                break;
            case R.id.btn_send_image:
                showDialogBeforeChooseImage();
                break;
            case R.id.image_to_be_sent:
                showRemoveChosenImageDialog();
                break;
            case R.id.edt_new_message:
                showSoftKeyboardText();
                break;
            case R.id.btn_emojiOne:
                emojiPopup.toggle(); // Toggles visibility of the Popup.
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mNewMessage, InputMethodManager.SHOW_IMPLICIT);
                break;
            default:
                break;
        }
    }

    private void showRemoveChosenImageDialog() {
        boolean dialogShown = mConfirmDialog != null && mConfirmDialog.getDialog() != null
                && mConfirmDialog.getDialog().isShowing();
        if (!dialogShown) {
            mConfirmDialog = DialogBuilder.buildConfirmDialog(getString(R.string.fragment_mail_line_remove_chosen_image_confirming), new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (null != mConfirmDialog) {
                        mConfirmDialog.dismiss();
                    }
                    mImageToBeSent.setVisibility(View.GONE);
                    mImageFile = null;
                }

                @Override
                public void onCancelClick() {
                    if (null != mConfirmDialog) {
                        mConfirmDialog.dismiss();
                    }
                }
            });
            mConfirmDialog.setTextOkButton(getString(R.string.str_cancel_attachment));

            Helpers.showDialogFragment(getChildFragmentManager(), mConfirmDialog);
        }
    }

    private void showDialogBeforeChooseImage() {
        Helpers.showAlertDialog(getContext(),
                null,
                Html.fromHtml(getString(R.string.message_dialog_before_choose_image)),
                getString(R.string.cancel),
                getString(R.string.btn_yes_jp),
                true,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoChooseImage();
                    }
                },
                null
        );
    }

    private void gotoChooseImage() {
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        intent.setAction(GalleryActivity.PICK_IMAGE);
        intent.putExtra(CropImageActivity.ARG_CROP_MODE, CropImageView.CropMode.FREE);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (PICK_IMAGE == requestCode) {
                final String path = data.getData().getPath();
                RkLogger.d(TAG, "PICK_IMAGE path=" + path + ", imageFile=" + path);
                prepareImageToBeSent(path);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPerformerImageClicked() {
        Intent intent = new Intent(getActivity(), PerformerProfileActivity.class);
        PerformerOnline performerOnline = new PerformerOnline();
        //Only pass code and profileImageUrl
        performerOnline.setCode(mPerformerCode);
        performerOnline.setProfileImageUrl(mPerformerImageUrl);
        if (mSendMessageBtn.isEnabled()) {
            performerOnline.setIsPublic("1");
        } else {
            performerOnline.setIsPublic("0");
        }
        intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
        intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
        getActivity().startActivity(intent);
    }

    @Override
    public void onErrorPlaceHolderClicked(int position) {
        showDialogReSendMessage(position);
    }

    private void showDialogReSendMessage(final int position) {
        boolean dialogShown = mConfirmDialog != null && mConfirmDialog.getDialog() != null
                && mConfirmDialog.getDialog().isShowing();

        if (!dialogShown) {
            mConfirmDialog = DialogBuilder.buildConfirmDialog(
                    getString(R.string.fragment_mail_line_re_send_message_prompt),
                    new DialogBuilder.OnClickListener() {
                        @Override
                        public void onOkClick(Object object) {
                            dismissConfirmDialog();
                            tryReSendMessage(position);
                        }

                        @Override
                        public void onCancelClick() {
                            dismissConfirmDialog();
                        }
                    });
            mConfirmDialog.setTextOkButton(getString(R.string.str_retransmission));
            Helpers.showDialogFragment(getChildFragmentManager(), mConfirmDialog);
        }
    }

    private void tryReSendMessage(int position) {
        try {
            final MessageDetail failedMessage = (MessageDetail) mMailList.get(position);

            if (null == failedMessage || !failedMessage.isSentError()) {
                return;
            }

            // get member information
            final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

            // gather your request parameters
            RequestParams params = new RequestParams();
            try {
                params.put(Define.Fields.FIELD_ID, member.getId());
                params.put(Define.Fields.FIELD_PASS, member.getPass());
                // knv fix a bug when sending message with encoded FULL_WIDTH_SPACE, the server api refuses
                // if having image to send and message is empty, send full width space instead
                params.put(Define.Fields.FIELD_MESSAGE, failedMessage.getBody(true));
                params.put(Define.Fields.FIELD_PERFORMER_CODE, mPerformerCode);
                if (failedMessage.getAttachImageUrl() != null && failedMessage.getAttachImageUrl().getSmall() != null) {
                    File imageFile = new File(failedMessage.getAttachImageUrl().getSmall());
                    if (imageFile.exists()) {
                        params.put(Define.Fields.FIELD_IMAGE, imageFile);
                    }
                }
            } catch (Exception ex) {
                RkLogger.e(TAG, "Unknown exception: ", ex);
            }

            if (checkConnectivity()) {
                return;
            }

            mMailLineState = UPDATE_MAIL_LINE_STATE.SENDING_MESSAGE;
            // send request
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(BuildConfig.SERVER_URL + Define.API.API_MEMBER_MAIL_PAY_POST, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                    // handle success response
                    ApiMailResponse apiResponse = new ApiMailResponse();
                    try {
                        String response = new String(bytes, Define.CONST_UTF_8);
                        Gson gson = new GsonBuilder().setLenient().create();
                        apiResponse = gson.fromJson(response, ApiMailResponse.class);
                    } catch (Exception ex) {
                        RkLogger.w(TAG, "Exception: ", ex);
                    }

                    if (mImageFile != null) {
                        sendRequestSubPoint(apiResponse, failedMessage);
                    } else {
                        handlePayPostMailSuccess(apiResponse, failedMessage);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                    // handle failure response
                    handleCallApiFailure();
                    RkLogger.e(TAG, "Failed: ", throwable);
                    mMailLineState = UPDATE_MAIL_LINE_STATE.NONE;
                }
            });

            // update UI
            failedMessage.setSentError(false);
            mAdapter.notifyDataSetChanged();

        } catch (Exception ex) {
            RkLogger.e(TAG, "Exception: ", ex);
        }
    }

    private class SmoothScrollToPosition implements Runnable {

        private final int mPosition;

        SmoothScrollToPosition(int position) {
            mPosition = position;
        }

        @Override
        public void run() {
            try {
                mMailLineListView.scrollToPosition(mPosition);
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning scrollToPosition: ", ex);
            }
        }
    }

    // return number of message, except Footer
    private int getMailListCount() {
        if (null == mMailList) {
            return 0;
        }

        int count = mMailList.size();

        if (mMailList.get(count - 1) instanceof FooterMessage) {
            return count - 1;
        }

        return count;
    }

    private final class UpdateMailLineStateTask extends AsyncTask<MailList, Void, UpdateMailLineTaskState> {
        private int mAddedCount;
        private boolean mPerformerIsPublic;
        private boolean mIsFinalPage;

        @Override
        protected UpdateMailLineTaskState doInBackground(MailList... mailLists) {
            try {
                if (!isCancelled()) {
                    // reset
                    mAddedCount = 0;
                    MailList mailList = mailLists[0];

                    List<MessageDetail> mailLineList = mailList.getMailDetailList();

                    // and add to mail list
                    if (mailLineList.size() > 0) {
                        // if we have sent a message, we should update toolbox UI, and set a cached image to the sent message
                        // because we can't display the image with sent message right away

                        if (mMailLineState == UPDATE_MAIL_LINE_STATE.MESSAGE_SENT) {
                            // get sent message
                            MessageDetail topMessage = mailLineList.get(0);

                            try {
                                // fixed it's attached image
                                if (null != mImageFile) {
                                    RkLogger.d(TAG, "reuse mImageFile cache: " + mImageFile.getPath());
                                    topMessage.getAttachImageUrl().setSmall(mImageFile.getPath());
                                    topMessage.getAttachImageUrl().setFromCached();
                                }
                            } catch (Exception ex) {
                                RkLogger.e(TAG, "Exception: ", ex);
                            }
                        }

                        int keptLimit = Define.SETTINGS.DEFAULT_LIMIT;

                        // calculate keptLimit
                        if (mMailLineState == UPDATE_MAIL_LINE_STATE.MESSAGE_SENT) {
                            keptLimit = getMailListCount();
                            mMailList.clear();
                        } else if (mMailLineState == UPDATE_MAIL_LINE_STATE.RECENTLY_RECEIVED_NEW_MESSAGE) {
                            keptLimit = getMailListCount() + 1;
                            mMailList.clear();
                        }

                        mAddedCount = addAllKeepPriority(mailLineList, mMailList, keptLimit);

                        // if it's final page, disable refresh
                        mIsFinalPage = getMailListCount() >= mailList.getRowCount();

                        // if performer is offline, disable send button
                        MessageDetail first = mailLineList.get(0);
                        mPerformerIsPublic = first.isPerformerPublic();

                        // add footer
                        if (mFirstLoad) {
                            mMailList.add(new FooterMessage());
                        }

                        return UpdateMailLineTaskState.USUAL;
                    } else {
                        return UpdateMailLineTaskState.NEWLY;
                    }
                }

            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning: ", ex);
            }

            return UpdateMailLineTaskState.NONE;
        }

        @Override
        protected void onPostExecute(UpdateMailLineTaskState result) {
            if (result == UpdateMailLineTaskState.USUAL) {
                if (mFirstLoad) {
                    mAdapter.notifyItemRangeChanged(0, mMailList.size());
                } else {
                    mAdapter.notifyItemRangeInserted(0, mAddedCount);
                }

                //scroll to bottom after 0.33s
                if (mMailLineState == UPDATE_MAIL_LINE_STATE.FIRST_LOAD) {
                    mHandlerHelper.postDelayed(
                            new SmoothScrollToPosition(mMailList.size() - 1), SCROLL_TO_POSITION_DELAY);
                } else if (mMailLineState == UPDATE_MAIL_LINE_STATE.RELOAD) {
                    if (mAddedCount > 0) {
                        mHandlerHelper.postDelayed(
                                new SmoothScrollToPosition(mAddedCount - 1), SCROLL_TO_POSITION_DELAY);
                    }
                }

                if (mFirstLoad) {
                    // un-track first loading time
                    mFirstLoad = false;
                }

                mSwipeRefreshLayout.setEnabled(!mIsFinalPage);

                mSendImageBtn.setEnabled(mPerformerIsPublic);
                mEmojiBtn.setEnabled(mPerformerIsPublic);
                mSendMessageBtn.setEnabled(mPerformerIsPublic);
                mNewMessage.setEnabled(mPerformerIsPublic);
                showIntroduceView(false);
            } else if (result == UpdateMailLineTaskState.NEWLY) {
                showIntroduceView(true);
            }
        }

        /**
         * Add source collection to destination collection in priority,
         * such that, the lower index the item has, the sooner it is inserted
         *
         * @return number of item added
         */
        private int addAllKeepPriority(List<MessageDetail> from,
                                       List<IMessage> to, int limit) {
            int count = 0;
            for (MessageDetail message : from) {
                int mailCode = message.getMailCode();
                if (mailNotExistIn(mailCode, to) &&
                        count < limit) {
                    to.add(0, message);
                    count = count + 1;
                }
            }
            return count;
        }

        /**
         * Helper to check if given mailCode already exist in list
         */
        private boolean mailNotExistIn(int mailCodeToCheck, List<IMessage> list) {
            try {
                for (IMessage message : list) {
                    if (message instanceof MessageDetail) {
                        int mailCode = ((MessageDetail) message).getMailCode();
//                    RkLog.d(TAG, "To source: mailCode=" + mailCode);
                        if (mailCodeToCheck == mailCode) {
                            return false;
                        }
                    }
                }

                return true;
            } catch (Exception ex) {
                RkLogger.w(TAG, "Warning: ", ex);
            }

            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftKeyboard();
    }
}
