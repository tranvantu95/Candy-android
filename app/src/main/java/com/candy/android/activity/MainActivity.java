package com.candy.android.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.CustomFragmentPagerAdapter;
import com.candy.android.custom.views.CustomViewPager;
import com.candy.android.custom.views.TabItemView;
import com.candy.android.dialog.CampaignDialog;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.fragment.BlogContainerFragment;
import com.candy.android.fragment.BlogDetailFragment;
import com.candy.android.fragment.BlogsFragment;
import com.candy.android.fragment.PerformersListFragment;
import com.candy.android.fragment.ReportPerformerFragment;
import com.candy.android.fragment.SMSAuthenticatePhoneFragment;
import com.candy.android.fragment.mail.FragmentMailContainer;
import com.candy.android.fragment.mail.FragmentMailLine;
import com.candy.android.fragment.mail.FragmentMailList;
import com.candy.android.fragment.menu.BlockListFragment;
import com.candy.android.fragment.menu.MainMenuContainer;
import com.candy.android.fragment.menu.MenuFragment;
import com.candy.android.fragment.menu.MissionFragmentContainer;
import com.candy.android.fragment.menu.ProfileFragment;
import com.candy.android.fragment.performer.FragmentPerformerContainer;
import com.candy.android.fragment.ranking.RankingContainerFragment;
import com.candy.android.fragment.tutorial.MainTutorialFragment;
import com.candy.android.fragment.webpage.WebFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMemberMissionInfoResponse;
import com.candy.android.http.response.ApiMemberWithNoticeResponse;
import com.candy.android.manager.InAppBillingManager;
import com.candy.android.manager.SettingManager;
import com.candy.android.manager.chat.ChatClientManager;
import com.candy.android.model.Campaign;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.model.eventBus.UpdateMissionInfoEvent;
import com.candy.android.model.eventBus.chat.WsSendEvent;
import com.candy.android.model.eventBus.inappbilling.PurchaseRetryEvent;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.RkSharedPreferencesUtils;
import com.candy.android.utils.inappbillingv3.IabBroadcastReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BasePopupActivity implements View.OnClickListener,
        IabBroadcastReceiver.IabBroadcastListener {

    private static final String TAG = "MainActivity";
    public static final int TAB_PERFORMERS_POSITION = 0;
    public static final int TAB_BLOG_POSITION = 2;
    public static final int TAB_MESSAGE_POSITION = 1;
    public static final int TAB_FAVORITE_POSITION = 3;
    public static final int TAB_MENU_POSITION = 4;
    private static final int TOAST_LENGTH_SHORT_TIME = 2000;
    private static final int MAIN_TUTORIAL_REQUEST_CODE = 9999;

    private Toolbar mToolbar;
    public static CustomViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> mListFragment;
    private CustomFragmentPagerAdapter mAdapter;
    private TextView mScreenTitle;
    private TextView mAgeView;
    private TextView mTvUnreadMessage;
    private View btnBack, btnSearch;
//    private AppBarLayout mMainAppBarLayout;
    private boolean doBackToQuitApp;
    private Handler mHandler;

    private LinearLayout mLl_popup;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static boolean active = false;

    /**
     * false: has not moved to any main tab
     * true: has moved other tab
     * Usage: use for set screen title when initialize app
     */
    private boolean hasChangedTab;

    private DialogBuilder.NoticeDialog mRetryDialog;

    private boolean mHasNewIntent;
    private boolean isShowingRetryDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        mHandler = new Handler();

        if (SettingManager.getInstance(this).hasMember()) {
            ChatClientManager.getInstance(this).initialize();
            doRegisterPNSetting();
            InAppBillingManager.getInstance().doRequestProductList(null);
        }

        // (knv added)
        InAppBillingManager.getInstance().initialize(this, this);

        EventBus.getDefault().register(this);
        initView();
        if (getIntent() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = getIntent();
                    parseIntent(intent);


                }
            }, 1000);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingManager.getInstance().getIsFirstStart()) {
                    showTutorial();
                    SettingManager.getInstance().setIsFirstStart(false);
                }
            }
        }, 1000);
        // Load mission
//        updateMemberMissionInfo();
        Log.i("login", "user:" + SettingManager.getInstance(this).getID() + "\n" + "pass:" + SettingManager.getInstance(this).getPassword() +
                "\n" + "3 day:" + SettingManager.getInstance(this).getMemberInformation().ismIs3Day() + "\n" + "Buytime:" + SettingManager.getInstance(this).getMemberInformation().getmBuyTime());
    }

    private void showTutorial() {
        int width = mTabLayout.getWidth();
        int height = mTabLayout.getHeight();
        int[] centerViewLocation = Helpers.getCenterViewLocation(mTabLayout.getTabAt(TAB_PERFORMERS_POSITION).getCustomView(), this);
        Intent intent = new Intent(this, MainTutorialActivity.class);
        intent.putExtra("TAB_ITEM_LEFT", centerViewLocation[0]);
        intent.putExtra("TAB_ITEM_TOP", centerViewLocation[1]);
        intent.putExtra("TAB_ITEM_WIDTH", width / 5);
        intent.putExtra("TAB_ITEM_HEIGHT", height);
        intent.putExtra("UN_READ_MESSAGE", mTvUnreadMessage.getText());
        startActivityForResult(intent, MAIN_TUTORIAL_REQUEST_CODE);
    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setCustomView(R.layout.custom_toolbar);
            ab.setDisplayShowTitleEnabled(false);
            ab.setDisplayShowCustomEnabled(true);
            //knv added

            //register event
            mScreenTitle = (TextView) mToolbar.findViewById(R.id.screen_title);
            mAgeView = (TextView) mToolbar.findViewById(R.id.performer_age);

            btnBack = mToolbar.findViewById(R.id.btn_back);
            btnBack.setOnClickListener(this);

            btnSearch = mToolbar.findViewById(R.id.btn_search);
            btnSearch.setOnClickListener(this);

            setBtnSearchVisible(true);

            findViewById(R.id.btn_add_point).setOnClickListener(this);
        }
    }

    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ChatClientManager.getInstance(this).close();
        // (knv) added
        InAppBillingManager.getInstance().onActivityDestroy();
        SettingManager.getInstance(this).removeWebToken();
        super.onDestroy();
    }

    @Override
    protected void onClickOpenDetail(String name, int code, String img, int age) {
        mLl_popup.removeAllViews();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_CHAT);
        intent.putExtra(Define.IntentExtras.PERFORMER_CODE, code);
        intent.putExtra(Define.IntentExtras.PERFORMER_NAME, name);
        intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, img);
        intent.putExtra(Define.IntentExtras.PERFORMER_AGE, age);
        startActivity(intent);
    }

    private void initView() {
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
        createTabIcon();
        mRetryDialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server),
                getString(R.string.retry));
        mRetryDialog.setOnDialogBackPress(new DialogBuilder.OnDialogBackPress() {
            @Override
            public void onDialogBackPress() {
                isShowingRetryDialog = false;
            }
        });
//        mMainAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar_layout);
        mLl_popup = (LinearLayout) findViewById(R.id.line_popup);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Define.IntentActions.ACTION_INAPP)) {
                    final String title = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_NAME);
                    final String img = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_IMAGE);
                    final String message = (String) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_MESSAGE);
                    final int code = (int) intent.getSerializableExtra(Define.IntentExtras.PERFORMER_CODE);
                    final int age = (int) intent.getIntExtra(Define.IntentExtras.PERFORMER_AGE, 0);


                    showPopupMessage(title, message, img, code, context, mLl_popup, age);
                }
            }
        };

//        showPopupMessage("title", "message", "https://picture2.livede55.com/images/p5-1288173088", 1288173088, this, mLl_popup, 23); //test
    }

    //knv added
    @Override
    protected void onResume() {
        super.onResume();
        RkLogger.d("Check crash >> ", "main onresume");
        ChatClientManager.getInstance(this).onResume();

        //update action bar
        HimecasUtils.postEventBus(this, (MemberInformation) null);

        // knv added
        if (mHasNewIntent) {
            mHasNewIntent = false;
            Intent intent = getIntent();
            parseIntent(intent);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Define.IntentActions.ACTION_INAPP));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    /**
     * knv added
     */
    private void parseIntent(@NonNull Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri.toString().equals(getString(R.string.deep_link_profile))) {
                gotoProfile();
            } else if (uri.toString().equals(getString(R.string.deep_link_blocked))) {
                gotoBlocked();
            } else {
//                String temp_post_id = uri.getQueryParameter("id");
//                if (temp_post_id.equals("72h_buy-point")) {
//                    Dialog72Hour dialog72Hour = new Dialog72Hour();
//                    dialog72Hour.show(getSupportFragmentManager(), dialog72Hour.getClass().getSimpleName());
//                }
            }
        }
        if (!TextUtils.isEmpty(action)) {
            if (Define.IntentActions.ACTION_CHAT.equals(action)) {
                int code = null == bundle ? Integer.MIN_VALUE : bundle.getInt(Define.IntentExtras.PERFORMER_CODE);
                String name = null == bundle ? null : bundle.getString(Define.IntentExtras.PERFORMER_NAME);
                int age = null == bundle ? 0 : bundle.getInt(Define.IntentExtras.PERFORMER_AGE);
                String image = null == bundle ? null : bundle.getString(Define.IntentExtras.PERFORMER_IMAGE);

                navigateToChatScreen(code, name, age, image);
            } else if (Define.IntentActions.ACTION_FREE_POINT.contentEquals(action)) {
                navigateToFreePointScreen();
            } else if (Define.IntentActions.ACTION_PURCHASE_POINT.contentEquals(action)) {
                navigateToPurchasePointScreen();
            } else if (Define.IntentActions.ACTION_BLOG_DETAIL.equals(action)) {
                int postId = 0;
                int position = 0;
                String performerName = "";
                if (bundle != null) {
                    postId = bundle.getInt(Define.IntentExtras.POST_ID);
                    performerName = bundle.getString(Define.IntentExtras.PERFORMER_NAME);
                    position = bundle.getInt(Define.IntentExtras.POSITION);
                }
                mViewPager.setCurrentItem(TAB_BLOG_POSITION);
                BlogContainerFragment blogContainerFragment = (BlogContainerFragment) mAdapter.getItem(TAB_BLOG_POSITION);
                blogContainerFragment.replaceFragment(BlogDetailFragment.newInstance(postId, performerName, position));
            } else if (Define.IntentActions.ACTION_BLOCK.equals(action)) {
                int code = null == bundle ? Integer.MIN_VALUE : bundle.getInt(Define.IntentExtras.PERFORMER_CODE);
                RkLogger.e(TAG, "got ID:" + code);
                gotoBlockAndReport(code);
            } else if (Define.IntentActions.ACTION_FAVORITE.equals(action)) {
                mViewPager.setCurrentItem(TAB_FAVORITE_POSITION);
            } else if (Define.IntentActions.ACTION_OPEN_HOME.equals(action)) {
                mViewPager.setCurrentItem(TAB_PERFORMERS_POSITION);
            } else if (Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intent.getData();
                if (uri != null && getResources().getString(R.string.himecas_web_host).equals(uri.getHost())) {
                    processUriFromWeb(uri);
                }
            }
        }
    }

    private void gotoBlocked() {
        mViewPager.setCurrentItem(TAB_MENU_POSITION);
        MainMenuContainer mainMenuFragment = (MainMenuContainer) mListFragment.get(TAB_MENU_POSITION);
        mainMenuFragment.replaceFragment(BlockListFragment.newInstance());
    }

    private void processUriFromWeb(Uri uri) {
        if (uri == null || mViewPager == null) return;

        String param = uri.toString();
        String id = uri.getQueryParameter("id");
        if (id == null) {
            Matcher matcher = Pattern.compile(getString(R.string.app_link_host_performer) + "=(.+?)").matcher(param);
            if (matcher.find()) {
                id = matcher.group(1);
            }
        }
        String code = uri.getQueryParameter("code");
        if (startUriFromWeb(param)) {
            return;
        }

        // If id is digis only --> performer
        if (id != null && TextUtils.isDigitsOnly(id)) {
            Intent performerIntent = new Intent(this, PerformerProfileActivity.class);
            PerformerOnline performerOnline = new PerformerOnline();
            //Only pass code and profileImageUrl
            performerOnline.setCode(Integer.parseInt(id));
            performerOnline.setChatStatusString("");
            performerOnline.setPrimaryName("");
            performerOnline.setAge("");
            performerOnline.setProfileImage("");
            performerIntent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
            performerIntent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
            startActivity(performerIntent);
            return;
        }

        if (Define.UriId.FREE_POINT.equals(id)) {
            navigateToFreePointScreen();
        } else if (Define.UriId.BLOG_LIST.equals(id)) {
            mViewPager.setCurrentItem(TAB_BLOG_POSITION);
            BlogContainerFragment fragment = (BlogContainerFragment) mAdapter.getItem(TAB_BLOG_POSITION);
            fragment.init();
        } else if (Define.UriId.FAVOURITE.equals(id)) {
            mViewPager.setCurrentItem(TAB_FAVORITE_POSITION);
        } else if (Define.UriId.PERFORMER.equals(id)) {
            if (!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code)) {
                Intent intent = new Intent(this, PerformerProfileActivity.class);
                PerformerOnline performerOnline = new PerformerOnline();
                //Only pass code and profileImageUrl
                performerOnline.setCode(Integer.valueOf(code));
                performerOnline.setProfileImageUrl("");
                intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
                intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
                startActivity(intent);
            }
        } else if (Define.UriId.PERFORMER_LIST.equals(id)) {
            mViewPager.setCurrentItem(TAB_PERFORMERS_POSITION);
            FragmentPerformerContainer fragmentPerformerContainer = (FragmentPerformerContainer) mAdapter.getItem((TAB_PERFORMERS_POSITION));
            fragmentPerformerContainer.gotoFragment(PerformersListFragment.newInstance());
        } else if (Define.UriId.MENU.equals(id) || Define.UriId.MENU.equals(uri.getHost())) {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            enableSelectMenu();
            MainMenuContainer mainMenuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
            mainMenuContainer.popToFirstFragment();
        } else if (Define.UriId.MESSAGE.equals(id)) {
            if (!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code)) {
                navigateToChatScreen(Integer.valueOf(code), "", 0, "");
            }
        } else if (Define.UriId.MESSAGE_LIST.equals(id)) {
            mViewPager.setCurrentItem(TAB_MESSAGE_POSITION);
            FragmentMailContainer fragmentMailContainer = (FragmentMailContainer) mAdapter.getItem(TAB_MESSAGE_POSITION);
            fragmentMailContainer.gotoFragment(FragmentMailList.newInstance());
        } else {
            moveScreenToTab(TAB_PERFORMERS_POSITION);
        }
    }

    private boolean startUriFromWeb(String uri) {
        if (uri.endsWith(getString(R.string.app_link_host_mission))) {
            return openUriFragment(R.id.mission);
        } else if (uri.endsWith(getString(R.string.app_link_host_beginner))) {
            return openUriFragment(R.id.guide_usage);
        } else if (uri.endsWith(getString(R.string.app_link_host_sms))) {
            return openUriFragment(R.id.free_point);
        } else if (uri.endsWith(getString(R.string.app_link_host_purchase))) {
            return openUriFragment(R.id.point);
        } else if (uri.endsWith(getString(R.string.app_link_host_trial))) {
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra(WebviewActivity.ARG, WebviewActivity.FROM_MAIN);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private MenuFragment menuFragment;

    public boolean openUriFragment(int id) {
        try {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            if (menuFragment == null) {
                MainMenuContainer menuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
                menuFragment = menuContainer.getMenuFragment();
            }
            if (menuFragment != null) {
                View view = menuFragment.getView().findViewById(id);
                if (view != null && view.getVisibility() != View.GONE) {
                    view.performClick();
                    return true;
                }
            }
        } catch (Exception e) {
            RkLogger.w(TAG, "Warning: ", e);
        }
        return false;
    }

    public void enableSelectMenu() {
//        if (mTabLayout.getTabAt(TAB_MENU_POSITION).isSelected()) {
//            TabItemView customView = (TabItemView) mTabLayout.getTabAt(TAB_MENU_POSITION).getCustomView().findViewById(R.id.icon);
//            customView.resetCustomStates();
//            customView.refreshDrawableState();
//        }
////        mTabLayout.setSelectedTabIndicatorHeight(Helpers.dpToPx(MainActivity.this, 2));
    }

    public void disableSelectMenu() {
//        if (mTabLayout.getTabAt(TAB_MENU_POSITION).isSelected()) {
//            TabItemView customView = (TabItemView) mTabLayout.getTabAt(TAB_MENU_POSITION).getCustomView().findViewById(R.id.icon);
////            customView.setSelected(false);
////            mTabLayout.setSelectedTabIndicatorHeight(0);
//            customView.resetCustomStates();
//            customView.addCustomState(TabItemView.STATE_LIVE_STAR_STATE);
//            customView.refreshDrawableState();
//        }
    }

    /**
     * knv added
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(PropertyChangedEvent property) {
        if (property != null && mToolbar != null) {
            RkLogger.d(TAG, "onPropertyChangedEvent property=" + property);
            //display unread message
            switch (property.getType()) {
                case PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED:
                    int value = property.getValue();
                    if (property.getValue() > 0) {
                        String count = value > 99 ? "99+" : String.valueOf(value);
                        mTvUnreadMessage.setText(count);
                        if (count.length() >= 3) {
                            mTvUnreadMessage.setBackgroundResource(R.drawable.bg_count_msg_large);
                        } else {
                            mTvUnreadMessage.setBackgroundResource(R.drawable.bg_count_msg);
                        }
                        mTvUnreadMessage.setVisibility(View.VISIBLE);
                    } else {
                        mTvUnreadMessage.setVisibility(View.GONE);
                    }
                    break;
                case PropertyChangedEvent.TYPE_POINT_CHANGED:
                    RkLogger.d(TAG, "onPropertyChangedEvent update TYPE_POINT_CHANGED");
                    TextView temp = (TextView) mToolbar.findViewById(R.id.n_pts);
                    if (property.getValue() >= 0) {
                        temp.setText(String.format(Locale.US, getString(R.string.action_bar_n_pts), property.getValue()));
                        temp.setVisibility(View.VISIBLE);
                    } else {
                        temp.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    }

    @Subscribe
    public void onPuchaseRetryEvent(PurchaseRetryEvent event) {

    }

    /**
     * This callback is fired when there is an incoming WsSendEvent from web socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveWebSocketMessage(final WsSendEvent event) {
        // update toolbar UI
        EventBus.getDefault().post(new PropertyChangedEvent(event.getUnreadCount(),
                PropertyChangedEvent.TYPE_NEW_MESSAGE_CHANGED));
//        minor (suggested by NamHV)
//        createNotification(event.getSenderName(), event.getSubject(), Define.IntentActions.ACTION_CHAT);
    }

    /**
     * Listent mission change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(UpdateMissionInfoEvent event) {
        updateMemberMissionInfo();
    }

    private void setupViewPager() {
        if (mListFragment == null || mListFragment.isEmpty()) {
            mListFragment = new ArrayList<>();

            mListFragment.add(FragmentPerformerContainer.newInstance());
            mListFragment.add(FragmentMailContainer.newInstance());
            mListFragment.add(BlogContainerFragment.newInstance());
            mListFragment.add(RankingContainerFragment.newInstance());
            mListFragment.add(MainMenuContainer.newInstance());
        }

        List<String> listTitle = new ArrayList<>();
        listTitle.add("BasePerformer");
        listTitle.add("Message");
        listTitle.add("Blog");
        listTitle.add("Favorite");
        listTitle.add("Menu");

        mAdapter = new CustomFragmentPagerAdapter(mListFragment, listTitle, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(mListFragment.size() - 1);
        setScreenTitle(getString(R.string.home_title));
        setScreenUpActionDisplay(false);
    }

    private void createTabIcon() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Tab performer
        View performerView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((ImageView) performerView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_performers);
        ((TextView) performerView.findViewById(R.id.title)).setText(R.string.title_performer_tab);
        performerView.setLayoutParams(layoutParams);
        mTabLayout.getTabAt(TAB_PERFORMERS_POSITION).setCustomView(performerView);

        // Tab message
        View messageView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((ImageView) messageView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_message);
        ((TextView) messageView.findViewById(R.id.title)).setText(R.string.title_message_tab);
        mTvUnreadMessage = (TextView) messageView.findViewById(R.id.unread_message_count);
        messageView.setLayoutParams(layoutParams);
        mTabLayout.getTabAt(TAB_MESSAGE_POSITION).setCustomView(messageView);

        // Tab blog
        View blogView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((ImageView) blogView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_blog);
        ((TextView) blogView.findViewById(R.id.title)).setText(R.string.title_blog_tab);
        blogView.setLayoutParams(layoutParams);
        mTabLayout.getTabAt(TAB_BLOG_POSITION).setCustomView(blogView);

        // Tab favorite
        View favoriteView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((ImageView) favoriteView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_ranking);
        ((TextView) favoriteView.findViewById(R.id.title)).setText(R.string.title_ranking_tab);
        favoriteView.setLayoutParams(layoutParams);
        mTabLayout.getTabAt(TAB_FAVORITE_POSITION).setCustomView(favoriteView);

        // Tab menu
        View menuView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((ImageView) menuView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_menu);
        ((TextView) menuView.findViewById(R.id.title)).setText(R.string.title_menu_tab);
        menuView.setLayoutParams(layoutParams);
        mTabLayout.getTabAt(TAB_MENU_POSITION).setCustomView(menuView);

        // Tab's config
        mTabLayout.addOnTabSelectedListener(mOnMainTabSelectedListener);
        mViewPager.addOnPageChangeListener(mOnPageChangListener);
        mViewPager.setOffscreenPageLimit(mListFragment.size() - 1);
    }

    private ViewPager.OnPageChangeListener mOnPageChangListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (!hasChangedTab) {
                hasChangedTab = true;
            }
//            mMainAppBarLayout.setExpanded(true, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            invalidatePageIfViewPagerDoneSettled(state);
        }
    };

    private TabLayout.OnTabSelectedListener mOnMainTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case TAB_PERFORMERS_POSITION:
                    setBtnSearchVisible(true);
                    break;
                default:
                    setBtnSearchVisible(false);
                    break;
            }
            enableSelectMenu();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case TAB_PERFORMERS_POSITION:
                    if (mAdapter != null) {
//                        mMainAppBarLayout.setExpanded(true, true);
//                        PerformersListFragment performersListFragment = (PerformersListFragment) mAdapter.getItem(TAB_PERFORMERS_POSITION);
//                        performersListFragment.scrollToTop();
//                        performersListFragment.loadCampaign();
                        // knv added
                        FragmentPerformerContainer performerContainer =
                                (FragmentPerformerContainer) mAdapter.getItem(TAB_PERFORMERS_POSITION);
                        BaseFragment currentDisplayed = performerContainer.getCurrentFragment();
                        if (currentDisplayed != null) {
                            if (currentDisplayed instanceof PerformersListFragment) {
                                // reuse above
                                PerformersListFragment performersListFragment = (PerformersListFragment) currentDisplayed;
                                performersListFragment.scrollToTop();
                                performersListFragment.loadData();
                            }
                        }
                    }
                    break;
                case TAB_MESSAGE_POSITION:
//                    mMainAppBarLayout.setExpanded(true, true);
                    if (mAdapter != null) {
                        FragmentMailContainer fragmentMailContainer = (FragmentMailContainer) mAdapter.getItem(TAB_MESSAGE_POSITION);
                        fragmentMailContainer.gotoFragment(FragmentMailList.newInstance());
                    }
                    break;
                case TAB_BLOG_POSITION:
//                    mMainAppBarLayout.setExpanded(true, true);
                    if (mAdapter != null) {
                        BlogContainerFragment blogContainerFragment = (BlogContainerFragment) mAdapter.getItem(TAB_BLOG_POSITION);
                        blogContainerFragment.init();
                    }
                    break;
                case TAB_FAVORITE_POSITION:
//                    mMainAppBarLayout.setExpanded(true, true);
                    if (mAdapter != null) {
//                        FavoritePerformerFragment favoritePerformerFragment = (FavoritePerformerFragment) mAdapter.getItem(TAB_FAVORITE_POSITION);
//                        favoritePerformerFragment.scrollToTop();
//                        favoritePerformerFragment.loadData();
                        RankingContainerFragment rankingContainerFragment = (RankingContainerFragment) mAdapter.getItem(TAB_FAVORITE_POSITION);
                        rankingContainerFragment.init();
                    }
                    break;
                case TAB_MENU_POSITION:
//                    mMainAppBarLayout.setExpanded(true, true);
                    if (mAdapter != null) {
                        MainMenuContainer mainMenuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
                        mainMenuContainer.replaceFragment(MenuFragment.newInstance());
                    }
                    break;
            }

            enableSelectMenu();
        }
    };

    private void invalidatePageIfViewPagerDoneSettled(int state) {
        // if the view pager done settled
        if (state == ViewPager.SCROLL_STATE_IDLE) {

            int post = mViewPager.getCurrentItem();
            Fragment fragment = mAdapter.getItem(post);

            // re-validate last child ui
            if (null != fragment) {
                if (fragment instanceof BaseFragmentContainer) {
                    ((BaseFragmentContainer) fragment).updateUI();
                } else if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).updateUI();
                }
            }

        } else {
            // hide soft keyboard, if any is showing
            hideSoftKeyBoard();
        }
    }

    /**
     * (knv added)
     */
    private void hideSoftKeyBoard() {
        View focused = getCurrentFocus();
        if (focused != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        int post = mViewPager.getCurrentItem();
        Fragment fragment = mAdapter.getItem(post);
        if (fragment != null) {
            if (fragment instanceof FragmentPerformerContainer) {
                //I don't want to kill this activity
                boolean isFragmentSeftBack = ((FragmentPerformerContainer) fragment).doSelfBackPressed();
                if (!isFragmentSeftBack) {
                    doLastBack();
                }
            } else if (fragment instanceof MainMenuContainer) {
                int isFragmentSelfBack = ((MainMenuContainer) fragment).doSelfBackPressed();
                if (isFragmentSelfBack == Define.LAST_BACK) {
                    doLastBack();
                } else if (isFragmentSelfBack == Define.MAIN_MENU) {
                    enableSelectMenu();
                }
            } else if (fragment instanceof FragmentMailContainer) {   //knv added
                //I don't want to kill this activity
                boolean isFragmentSelfBack = ((FragmentMailContainer) fragment).doSelfBackPressed();
                if (!isFragmentSelfBack) {
                    doLastBack();
                }
            } else if (fragment instanceof BlogContainerFragment) {
                BlogContainerFragment blogContainerFragment = (BlogContainerFragment) fragment;
                boolean isFragmentSelfBack = blogContainerFragment.doSelfBackPressed();
                if (!isFragmentSelfBack) {
                    Fragment blogsFragment = blogContainerFragment.findLastFragment();
                    if (blogsFragment instanceof BlogsFragment) {
                        if (((BlogsFragment) blogsFragment).isBlogsOfPerformer()) {
                            ((BlogsFragment) blogsFragment).reloadCurrentBlogsList();
                            ((BlogsFragment) blogsFragment).restoreHightLightTab();
                            setScreenTitle(getString(R.string.blog_list_screen_name));
                            setScreenUpActionDisplay(false);
                            ((BlogsFragment) blogsFragment).setUpActionDisplay(false);
                            ((BlogsFragment) blogsFragment).setmScreenTitle(getString(R.string.blog_list_screen_name));
                        } else {
                            doLastBack();
                        }
                    } else {
                        doLastBack();
                    }
                }
            } else if (fragment instanceof RankingContainerFragment) {
                RankingContainerFragment rankingContainerFragment = (RankingContainerFragment) fragment;
                boolean isFragmentSelfBack = rankingContainerFragment.doSelfBackPressed();
                if (!isFragmentSelfBack) {
                    doLastBack();
                }
            }
        } else {
            super.onBackPressed();
        }
    }
    /**
     * knv added
     */
    private void navigateToSMSAuthenScreen() {
        RkLogger.d(TAG, "goto [sms authen]");
        try {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            MainMenuContainer menuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
            menuContainer.replaceFragment(SMSAuthenticatePhoneFragment.newInstance());
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RkLogger.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (requestCode == MAIN_TUTORIAL_REQUEST_CODE && resultCode == MainTutorialActivity.RESULT_BACK) {
            if (data.hasExtra(MainTutorialFragment.GO_SMS_AUTHEN)) {
                navigateToSMSAuthenScreen();
            }
        }

        // Pass on the activity result to the helper for handling
        if (!InAppBillingManager.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            RkLogger.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
        RkLogger.d("Check crash >> ", "main onSaveInstatnce");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        RkLogger.d("Check crash >> ", "main onNewInstance");
        mHasNewIntent = true;
        setIntent(intent);
        super.onNewIntent(intent);

    }

    public void gotoBlockAndReport(int code) {
        if (mViewPager == null || mListFragment == null)
            return;
        mViewPager.setCurrentItem(TAB_MENU_POSITION);
        MainMenuContainer mainMenuFragment = (MainMenuContainer) mListFragment.get(TAB_MENU_POSITION);
        mainMenuFragment.replaceFragment(ReportPerformerFragment.newInstance(String.valueOf(code)));
    }

    /**
     * knv added
     */
    public void gotoPurchasePointScreen() {
        if (mViewPager == null || mListFragment == null)
            return;
        mViewPager.setCurrentItem(TAB_MENU_POSITION);
        MainMenuContainer mainMenuFragment = (MainMenuContainer) mListFragment.get(TAB_MENU_POSITION);
        mainMenuFragment.replaceFragment(WebFragment.newInstance(Define.WebUrl.URL_PURCHASE, true));
    }

    private void gotoFaqScreen() {
        if (mViewPager == null || mListFragment == null)
            return;
        mViewPager.setCurrentItem(TAB_MENU_POSITION);
        MainMenuContainer mainMenuFragment = (MainMenuContainer) mListFragment.get(TAB_MENU_POSITION);
        mainMenuFragment.replaceFragment(WebFragment.newInstance(Define.WebUrl.URL_INQUIRY_HISTORY, true));
    }

    public void gotoProfile() {
        if (SettingManager.getInstance(this).hasMember()) {
        }
        Helpers.showCircleProgressDialog(this);
        String id = SettingManager.getInstance(this).getID();
        String password = SettingManager.getInstance(this).getPassword();

        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);

        RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
        //create call object
        Call<MemberInfoChange> call = apiService.getMemberInfoChange(id, password);

        // send call object
        call.enqueue(new Callback<MemberInfoChange>() {
            @Override
            public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                Helpers.dismissCircleProgressDialog();

                if (response == null || response.body() == null) {
                    Log.d(TAG, "response=null");
                    return;
                }

                // goto edit profile screen
                if (null != response.body().getMember()) {
                    RkLogger.d(TAG, "Member: " + response.body().getMember().toString());
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE, response.body());
                MenuFragment.newInstance().openNewFragment(ProfileFragment.newInstance(bundle));
                mViewPager.setCurrentItem(TAB_MENU_POSITION);
                MainMenuContainer mainMenuFragment = (MainMenuContainer) mListFragment.get(TAB_MENU_POSITION);
                mainMenuFragment.replaceFragment(ProfileFragment.newInstance(bundle));
            }

            @Override
            public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                // Log error here since request failed
                RkLogger.e(TAG, "MemberInfoChange", t);
                Helpers.dismissCircleProgressDialog();
            }
        });
    }

    /**
     * @author knv
     * Navigate to message detail screen
     */
    private void navigateToChatScreen(int performerCode, String name, int age, String image) {
        RkLogger.e(TAG, "got ID:" + performerCode);
        try {
            mViewPager.setCurrentItem(TAB_MESSAGE_POSITION);
            if (Integer.MIN_VALUE != performerCode && !TextUtils.isEmpty(name)) {
                FragmentMailContainer mailContainer = (FragmentMailContainer) mAdapter.getItem(TAB_MESSAGE_POSITION);
                mailContainer.gotoFragment(FragmentMailLine.newInstance(performerCode, name, age, image));
            }
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    /**
     * knv added
     */
    private void navigateToFreePointScreen() {
        RkLogger.d(TAG, "goto [free-point]");
        try {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            MainMenuContainer menuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
            menuContainer.replaceFragment(SMSAuthenticatePhoneFragment.newInstance());
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    private void navigateToPurchasePointScreen() {
        RkLogger.d(TAG, "goto [purchase-point]");
        try {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            MainMenuContainer menuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
            menuContainer.replaceFragment(WebFragment.newInstance(Define.WebUrl.URL_PURCHASE, true));
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    public void openMissionScreen() {
        try {
            mViewPager.setCurrentItem(TAB_MENU_POSITION);
            MainMenuContainer menuContainer = (MainMenuContainer) mAdapter.getItem(TAB_MENU_POSITION);
            menuContainer.replaceFragment(MissionFragmentContainer.newInstance());
        } catch (Exception e) {
            RkLogger.w(TAG, "Warning: ", e);
        }
    }

    public void moveScreenToTab(int position) {
        if (position >= 0 && position < mViewPager.getAdapter().getCount()) {
            mViewPager.setCurrentItem(position);
        }
    }

    public void updateBlockPerformerList() {
        if (mViewPager != null) {

            mViewPager.setCurrentItem(TAB_PERFORMERS_POSITION);
        }
//        if (mAdapter == null)
//            return;
//        FragmentPerformerContainer performerContainer =
//                (FragmentPerformerContainer) mAdapter.getItem(TAB_PERFORMERS_POSITION);
//        BaseFragment currentDisplayed = performerContainer.getCurrentFragment();
//        if (currentDisplayed != null) {
//            if (currentDisplayed instanceof PerformersListFragment) {
//                // reuse above
//                PerformersListFragment performersListFragment = (PerformersListFragment) currentDisplayed;
//                performersListFragment.removePerformer(performerCode);
//                mViewPager.setCurrentItem(TAB_PERFORMERS_POSITION);
//            }
//        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_search:
                actionSearch();
                break;

            case R.id.btn_add_point:
                navigateToPurchasePointScreen();
                break;
        }
    }

    private void actionSearch() {
        FragmentPerformerContainer performerContainer =
                (FragmentPerformerContainer) mAdapter.getItem(TAB_PERFORMERS_POSITION);
        BaseFragment currentDisplayed = performerContainer.getCurrentFragment();
        if (currentDisplayed != null) {
            if (currentDisplayed instanceof PerformersListFragment) {
                PerformersListFragment performersListFragment = (PerformersListFragment) currentDisplayed;
                performersListFragment.showSearchDialog();
            }
        }
    }

    /**
     * register PN setting with new token, for example
     */
    private void doRegisterPNSetting() {

        if (SettingManager.getInstance(this).hasMember()) {
            final MemberInformation member = SettingManager.getInstance(this).getMemberInformation();

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d("IDK-MainActivity", "registering PN setting");
            //create api object
            Call<ApiMemberWithNoticeResponse> call = apiService.registerPNSetting(member.getId(),
                    member.getPass(),
                    SettingManager.getInstance(this).getFireBaseIIDToken(),
                    0);

            //send api object
            call.enqueue(new Callback<ApiMemberWithNoticeResponse>() {

                @Override
                public void onResponse(Call<ApiMemberWithNoticeResponse> call, Response<ApiMemberWithNoticeResponse> response) {
                    handleRegisterPNSettingSuccess(response);
                }

                @Override
                public void onFailure(Call<ApiMemberWithNoticeResponse> call, Throwable t) {
                    RkLogger.e("IDK-MainActivity", "registerPNSetting failed:", t);
                }
            });
        }
    }

    /**
     * This method is called when registering PN token finished (no matter it's success or not)
     * We confirm that this method is called only when user has correct id and password, and
     * should register new token for PN setting
     */
    private void handleRegisterPNSettingSuccess(Response<ApiMemberWithNoticeResponse> response) {
        if (response != null) {
            RkLogger.d("IDK-MainActivity", "registerPNSetting response=" + response.isSuccessful());
        }
    }

    public void setScreenTitle(String screenTitle) {
        if (!TextUtils.isEmpty(screenTitle)) {
            mScreenTitle.setText(screenTitle);
            mScreenTitle.setTextSize(Helpers.pxToSp(this, getResources().getDimension(R.dimen.text_toolbar_size)));
            mAgeView.setVisibility(View.GONE);
        }
    }

    public void setAgeViewValue(int age) {
        if (age != Define.REQUEST_FAILED) {
            mScreenTitle.setTextSize(Helpers.pxToSp(this, getResources().getDimension(R.dimen.text_toolbar_size_small)));
            mAgeView.setVisibility(View.VISIBLE);
            mAgeView.setText(getString(R.string.screen_title_str_age, age));
        }
    }

    public void setScreenUpActionDisplay(boolean visible) {
        btnBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) setBtnSearchVisible(false);
    }

    public void setBtnSearchVisible(boolean visible) {
        btnSearch.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) setScreenUpActionDisplay(false);
    }

    public void showRetryDialog(DialogBuilder.OnClickListener onClickListener) {
        if (mRetryDialog != null && !isShowingRetryDialog) {
            isShowingRetryDialog = true;
            mRetryDialog.setOnClickListener(onClickListener);
//            mRetryDialog.show(getSupportFragmentManager(), RETRY_DIALOG_TAG);
            Helpers.showDialogFragment(getSupportFragmentManager(), mRetryDialog);
        }
    }

    public void dismissRetryDialog() {
        if (mRetryDialog != null && isShowingRetryDialog) {
            isShowingRetryDialog = false;
            mRetryDialog.dismiss();
        }
    }

    public void showCampaignDialog(ArrayList<Campaign> campaigns, int position) {
        CampaignDialog campaignDialog = CampaignDialog.newInstance(campaigns, position);
//        campaignDialog.show(getSupportFragmentManager(), TAG);
        Helpers.showDialogFragment(getSupportFragmentManager(), campaignDialog);
    }

    public boolean isHasChangedTab() {
        return hasChangedTab;
    }

    /**
     * knv added
     */
    @Override
    public void receivedBroadcast() {
        InAppBillingManager.getInstance().receivedBroadcast();
    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    /**
     * Back action in last screen
     */
    private void doLastBack() {
        if (doBackToQuitApp) {
            finish();
        } else {
            doBackToQuitApp = true;
            Toast.makeText(this, getString(R.string.back_again_to_quit_app), Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doBackToQuitApp = false;
                }
            }, TOAST_LENGTH_SHORT_TIME);
        }
    }

    public void showAppBarLayout() {
//        if (mMainAppBarLayout != null) {
//            mMainAppBarLayout.setExpanded(true, true);
//        }
    }

    public void updateMemberMissionInfo() {
        final MemberInformation member = SettingManager.getInstance(this).getMemberInformation();

        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);

        RkLogger.d("MainActivity", "updateMemberMissionInfo");
        //create api object
        Call<ApiMemberMissionInfoResponse> call = apiService.getMemberMissionInfo(member.getId(),
                member.getPass());

        //send api object
        call.enqueue(new Callback<ApiMemberMissionInfoResponse>() {

            @Override
            public void onResponse(Call<ApiMemberMissionInfoResponse> call, Response<ApiMemberMissionInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int point = response.body().getMember().getPoint();
                    String joinDate = response.body().getMember().getJoinDate();
                    EventBus.getDefault().post(new PropertyChangedEvent(point, PropertyChangedEvent.TYPE_POINT_CHANGED));
                    // Update point to local
                    MemberInformation memberInformation = SettingManager.getInstance(MainActivity.this).getMemberInformation();
                    memberInformation.setPoint(point);
                    memberInformation.setmJoinDate(joinDate);
                    SettingManager.getInstance(MainActivity.this).save(memberInformation);


//                    int remainMission = response.body().getMember().getMission().getRemain();
//                    EventBus.getDefault().post(new PropertyChangedEvent(remainMission, PropertyChangedEvent.TYPE_MISSION_CHANGE));
                }
            }

            @Override
            public void onFailure(Call<ApiMemberMissionInfoResponse> call, Throwable t) {
                RkLogger.e("IDK-MainActivity", "registerPNSetting failed:", t);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }
}
