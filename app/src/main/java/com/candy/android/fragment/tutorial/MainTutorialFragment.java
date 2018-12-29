package com.candy.android.fragment.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.MainTutorialActivity;
import com.candy.android.model.BasePerformer;
import com.candy.android.model.PerformerDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhTriu on 3/29/2018.
 */

public class MainTutorialFragment extends Fragment implements View.OnClickListener {

    public static final int STEP_0 = 0;
    public static final int STEP_1 = 1;
    public static final int STEP_2 = 2; //tab top
    public static final int STEP_3 = 3; //message list
    public static final int STEP_4 = 4; //tab blog
    public static final int STEP_5 = 5; //tab ranking
    public static final int STEP_6 = 6; //tab menu
    public static final int STEP_7 = 7; //final

    public static final String GO_SMS_AUTHEN = "GOTO_POINT";

    View mButtonBack;
    View mButtonNext;
    View mSmsAuthen;
    View mGoMainPage;
    RelativeLayout mLnTutorialContainer;

    int[] listStep = new int[]{STEP_0, STEP_1, STEP_2, STEP_3, STEP_4, STEP_5, STEP_6, STEP_7};
    private int currentStepIndex = 0;

    private BasePerformer performer;

    public MainTutorialFragment() {
    }

    public static MainTutorialFragment newInstance() {
        MainTutorialFragment fragment = new MainTutorialFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity() != null && getActivity().getIntent() != null)
            performer = getActivity().getIntent().getParcelableExtra(MainTutorialActivity.PERFORMER);

        if(performer != null) {
            List<Integer> steps = new ArrayList<>();
            steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_FIRST);
            steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_CHAT_MESSAGE);
            if(!performer.isNoPublic()) {
                if(performer.isLive()) steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_CALL_VIDEO);
//                if(performer.canPeep()) steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_PEEP_VIDEO);
                steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_LIKE);
            }
            steps.add(MainTutorialActivity.PERFORMER_TUTORIAL_LAST);

            listStep = new int[steps.size()];
            for(int i = 0; i < steps.size(); i++) listStep[i] = steps.get(i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_tutorial, null);
        mLnTutorialContainer = (RelativeLayout) view.findViewById(R.id.rl_tutorial_container);
        goStep(currentStepIndex);
        return view;
    }

    private void goStep(int stepIndex) {
        if (stepIndex < 0) {
            currentStepIndex = 0;
        } else if (stepIndex >= listStep.length) {
            currentStepIndex = listStep.length - 1;
        } else {
            currentStepIndex = stepIndex;
        }

        View view = null;
        ((MainTutorialActivity) getActivity()).removeTabTutorial();

        switch (listStep[currentStepIndex]) {
            case STEP_0:
                view = LayoutInflater.from(getActivity()).inflate(R.layout.main_tutorial_first, mLnTutorialContainer, false);
                break;

            case STEP_7:
                view = LayoutInflater.from(getActivity()).inflate(R.layout.main_tutorial_final, mLnTutorialContainer, false);
                break;

            case STEP_1:
            case STEP_2:
            case STEP_3:
            case STEP_4:
            case STEP_5:
            case STEP_6:
                view = LayoutInflater.from(getActivity()).inflate(R.layout.main_tutorial, mLnTutorialContainer, false);
                TextView tutorialContent = (TextView) view.findViewById(R.id.tv_content);

                switch (listStep[stepIndex]) {
                    case STEP_1:
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step1)));
                        break;

                    case STEP_2:
                        ((MainTutorialActivity) getActivity()).createTabTutorial(0);
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step2)));
                        break;

                    case STEP_3:
                        ((MainTutorialActivity) getActivity()).createTabTutorial(1);
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step3)));
                        break;

                    case STEP_4:
                        ((MainTutorialActivity) getActivity()).createTabTutorial(2);
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step4)));
                        break;

                    case STEP_5:
                        ((MainTutorialActivity) getActivity()).createTabTutorial(3);
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step5)));
                        break;

                    case STEP_6:
                        ((MainTutorialActivity) getActivity()).createTabTutorial(4);
                        tutorialContent.setText(Html.fromHtml(getResources().getString(R.string.fragment_step6)));
                        break;
                }
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_FIRST:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_first)));
                view.findViewById(R.id.btn_back).setVisibility(View.GONE);
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_CHAT_MESSAGE:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_button_chat_message)));
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_CALL_VIDEO:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_button_call_video)));
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_PEEP_VIDEO:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_button_peep_video)));
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_BUTTON_LIKE:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_button_like)));
                break;

            case MainTutorialActivity.PERFORMER_TUTORIAL_LAST:
                ((MainTutorialActivity) getActivity()).makePerformerTutorial(performer, listStep[currentStepIndex]);
                view = LayoutInflater.from(getActivity()).inflate(R.layout.performer_profile_tutorial_2, mLnTutorialContainer, false);
                ((TextView) view.findViewById(R.id.message)).setText(Html.fromHtml(getResources().getString(R.string.performer_tutorial_last)));
                ((TextView) view.findViewById(R.id.btn_next)).setText(getString(R.string.performer_tutorial_last_text_button));
                view.findViewById(R.id.btn_back).setVisibility(View.GONE);
                break;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLnTutorialContainer.removeAllViews();
        mLnTutorialContainer.addView(view, layoutParams);

        mButtonBack = view.findViewById(R.id.btn_back);
        mButtonNext = view.findViewById(R.id.btn_next);
        mSmsAuthen = view.findViewById(R.id.btn_sms_authen);
        mGoMainPage = view.findViewById(R.id.btn_goto_main);

        if (mButtonBack != null) {
            mButtonBack.setOnClickListener(this);
        }
        if (mButtonNext != null) {
            mButtonNext.setOnClickListener(this);
        }
        if (mSmsAuthen != null) {
            mSmsAuthen.setOnClickListener(this);
        }
        if (mGoMainPage != null) {
            mGoMainPage.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                currentStepIndex--;
                goStep(currentStepIndex);
                break;

            case R.id.btn_next:
                if (performer != null && currentStepIndex == listStep.length - 1) {
                    getActivity().finish();
                } else {
                    currentStepIndex++;
                    goStep(currentStepIndex);
                }
                break;

            case R.id.btn_sms_authen:
                Intent intent = new Intent();
                intent.putExtra(GO_SMS_AUTHEN, true);
                getActivity().setResult(MainTutorialActivity.RESULT_BACK, intent);
                getActivity().finish();
                break;

            case R.id.btn_goto_main:
                getActivity().finish();
                break;
        }
    }

}
