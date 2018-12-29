package com.candy.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.custom.views.TabItemView;
import com.candy.android.fragment.tutorial.MainTutorialFragment;
import com.candy.android.model.BasePerformer;
import com.candy.android.utils.Helpers;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainTutorialActivity extends BaseActivity {

    public static final int RESULT_BACK = 2018;

    public static final String TT_STEP = "TT_STEP";
    public static final String TAB_ITEM_LEFT = "TAB_ITEM_LEFT";
    public static final String TAB_ITEM_TOP = "TAB_ITEM_TOP";
    public static final String TAB_ITEM_WIDTH = "TAB_ITEM_WIDTH";
    public static final String TAB_ITEM_HEIGHT = "TAB_ITEM_HEIGHT";
    public static final String UN_READ_MESSAGE = "UN_READ_MESSAGE";
    public static final String PERFORMER = "performer";

    public static final int FINAL_STEP_INDEX = 5;
    public static final int STEP_PERFORMER = 0;
    public static final int STEP_MESSAGE = 1;
    public static final int STEP_BLOG = 2;
    public static final int STEP_RANKING = 3;
    public static final int STEP_MENU = 4;

    public static final int PERFORMER_TUTORIAL_FIRST = 201;
    public static final int PERFORMER_TUTORIAL_BUTTON_CHAT_MESSAGE = 202;
    public static final int PERFORMER_TUTORIAL_BUTTON_CALL_VIDEO = 203;
    public static final int PERFORMER_TUTORIAL_BUTTON_PEEP_VIDEO = 204;
    public static final int PERFORMER_TUTORIAL_BUTTON_LIKE = 205;
    public static final int PERFORMER_TUTORIAL_LAST = 206;

    @BindView(R.id.main_container)
    FrameLayout mainContainer;

    @BindView(R.id.bottom_container)
    LinearLayout bottomContainer;

    int m_tab_item_left;
    int tab_item_top;
    int tab_item_width;
    int tab_item_height;
    int step;

    MainTutorialFragment startTutorialFragment;

    String unReadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tutorial);
        ButterKnife.bind(this);
        parseIntent();

        startTutorialFragment = MainTutorialFragment.newInstance();
        Helpers.replaceFragment(getSupportFragmentManager(), R.id.main_container, startTutorialFragment);
    }

    @Override
    public void onBackPressed() {

    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            step = intent.getIntExtra(TT_STEP, 0);
            int tab_item_left = intent.getIntExtra(TAB_ITEM_LEFT, 0);
            tab_item_top = intent.getIntExtra(TAB_ITEM_TOP, 0);
            tab_item_width = intent.getIntExtra(TAB_ITEM_WIDTH, 0);
            tab_item_height = intent.getIntExtra(TAB_ITEM_HEIGHT, 0);
            unReadMessage = intent.getStringExtra(UN_READ_MESSAGE);
            m_tab_item_left = step * tab_item_left;
            step = STEP_PERFORMER;
        }
    }

    public void removeTabTutorial() {
        bottomContainer.removeAllViews();
    }

    public void createTabTutorial(int pos) {
        LinearLayout.LayoutParams layoutParams;
        layoutParams = new LinearLayout.LayoutParams(tab_item_width, tab_item_height);
        layoutParams.leftMargin = pos * tab_item_width;
        layoutParams.topMargin = 0;
        layoutParams.width = tab_item_width;

        bottomContainer.removeAllViews();

        View tabItemView = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        tabItemView.setBackgroundColor(getResources().getColor(R.color.white));
        TabItemView itemView = (TabItemView) tabItemView.findViewById(R.id.icon);

        switch (pos) {
            case STEP_PERFORMER:
                tabItemView.setId(pos);
                bottomContainer.addView(tabItemView, layoutParams);
                ((ImageView) tabItemView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_performers);
                ((TextView) tabItemView.findViewById(R.id.title)).setText(R.string.title_performer_tab);
                break;

            case STEP_MESSAGE:
                ((ImageView) tabItemView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_message);
                ((TextView) tabItemView.findViewById(R.id.title)).setText(R.string.title_message_tab);

                tabItemView.setId(pos);
                bottomContainer.addView(tabItemView, layoutParams);

                TextView mTvUnreadMessage = ((TextView) tabItemView.findViewById(R.id.unread_message_count));
                if (unReadMessage != null && !unReadMessage.isEmpty()) {

                    mTvUnreadMessage.setVisibility(View.VISIBLE);
                    if (unReadMessage.length() >= 3) {
                        mTvUnreadMessage.setBackgroundResource(R.drawable.bg_count_msg_large);
                    } else {
                        mTvUnreadMessage.setBackgroundResource(R.drawable.bg_count_msg);
                    }
                    mTvUnreadMessage.setText(unReadMessage);

                } else {
                    mTvUnreadMessage.setVisibility(View.GONE);
                }
                break;

            case STEP_BLOG:
                ((ImageView) tabItemView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_blog);
                ((TextView) tabItemView.findViewById(R.id.title)).setText(R.string.title_blog_tab);
                tabItemView.setId(pos);
                bottomContainer.addView(tabItemView, layoutParams);
                break;

            case STEP_RANKING:
                ((ImageView) tabItemView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_ranking);
                ((TextView) tabItemView.findViewById(R.id.title)).setText(R.string.title_ranking_tab);
                tabItemView.setId(pos);
                bottomContainer.addView(tabItemView, layoutParams);
                break;

            case STEP_MENU:
                ((ImageView) tabItemView.findViewById(R.id.icon)).setImageResource(R.drawable.icon_menu);
                ((TextView) tabItemView.findViewById(R.id.title)).setText(R.string.title_menu_tab);
                tabItemView.setId(pos);
                bottomContainer.addView(tabItemView, layoutParams);
                break;

            default:
                break;
        }

        tabItemView.setSelected(true);
    }

    public void makePerformerTutorial(BasePerformer performer, int step) {
        Log.d("makePerformerTutorial", "step " + step);
        View view = findViewById(R.id.performer_profile_tutorial);
        if(view == null) view = getLayoutInflater().inflate(R.layout.performer_profile_tutorial, mainContainer, true);

        View btnChatMessage = view.findViewById(R.id.iv_chat_message);
        View tvChatMessage = view.findViewById(R.id.tv_chat_message);
        View btnCallVideo = view.findViewById(R.id.iv_call_video);
        View btnPeepVideo = view.findViewById(R.id.iv_peep_video);
        View btnLike = view.findViewById(R.id.btn_like);

        btnChatMessage.setVisibility(View.INVISIBLE);
        btnChatMessage.setEnabled(false);
        tvChatMessage.setEnabled(false);
        btnCallVideo.setVisibility(View.GONE);
        btnPeepVideo.setVisibility(View.GONE);
        btnLike.setVisibility(View.GONE);

        if(!performer.isNoPublic()) {
            btnChatMessage.setEnabled(true);
            tvChatMessage.setEnabled(true);
            if(performer.isLive()) btnCallVideo.setVisibility(View.INVISIBLE);
            if(performer.canPeep()) btnPeepVideo.setVisibility(View.INVISIBLE);
        }

        switch (step) {
            case PERFORMER_TUTORIAL_FIRST:
                break;

            case PERFORMER_TUTORIAL_BUTTON_CHAT_MESSAGE:
                btnChatMessage.setVisibility(View.VISIBLE);
                break;

            case PERFORMER_TUTORIAL_BUTTON_CALL_VIDEO:
                btnCallVideo.setVisibility(View.VISIBLE);
                break;

            case PERFORMER_TUTORIAL_BUTTON_PEEP_VIDEO:
                btnPeepVideo.setVisibility(View.VISIBLE);
                break;

            case PERFORMER_TUTORIAL_BUTTON_LIKE:
                btnLike.setVisibility(View.VISIBLE);
                break;

            case PERFORMER_TUTORIAL_LAST:
                mainContainer.removeView(view);
                break;

        }
    }

}
