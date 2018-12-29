package com.candy.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.fragment.mail.registration.FragmentProfileRegister;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @author Favo
 * Created on 13/12/2016.
 */

public class ProfileRegistrationV2Activity extends BaseActivity {

    private static final String TAG = "IDK-ProfileRegistrationV2Activity";
    private List<String> mProbihitedWords;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration_v2);
        initToolbar();
        loadProhibitedWorlds();
        startProfileRegister();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.fragment_profile_registration_title);
            ab.setCustomView(R.layout.profile_registration_toolbar);
            ab.setDisplayShowCustomEnabled(true);
        }
    }

    private void startProfileRegister() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProfileRegistrationV2FragmentContainer fragment = new ProfileRegistrationV2FragmentContainer();
        fragmentTransaction.add(R.id.fm_profile_registration, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        RkLogger.e(TAG, "Warning: "+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProfileRegistrationV2FragmentContainer fragment = (ProfileRegistrationV2FragmentContainer) fragmentManager.findFragmentById(R.id.fm_profile_registration);
            fragment.doSelfBackPressed();
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }
    }

    public static final class ProfileRegistrationV2FragmentContainer extends BaseFragmentContainer {
        private ProfileRegistrationV2Activity mRegistrationV2Activity;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_profile_register_container, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (FragmentProfileRegister.RegistrationDataV2.hasData()) {
                FragmentProfileRegister.RegistrationDataV2.destroy();
            }
            // go to start point screen
            FragmentProfileRegister.goToStep(this, FragmentProfileRegister.SET_NAME_STEP);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof ProfileRegistrationV2Activity) {
                mRegistrationV2Activity = (ProfileRegistrationV2Activity) context;
            } else {
                throw new RuntimeException("Developer error: this fragment must attach to ProfileRegistrationV2Activity");
            }
        }

        /**
         * utility function for replacing fragment
         *
         * @param fragment the fragment to be replaced
         */
        @Override
        public void gotoFragment(BaseFragment fragment) {
            Helpers.replaceFragment(getChildFragmentManager(), R.id.fm_profile_register_container, fragment);
        }

        public void onProfileRegisterSuccess() {
            mRegistrationV2Activity.gotoMainScreenFirst();
        }

        public boolean doSelfBackPressed() {
            if (mChildFragmentMgr == null) {
                mChildFragmentMgr = getChildFragmentManager();
                // register callback
                getChildFragmentManager().addOnBackStackChangedListener(this);
            }
            if (null != mChildFragmentMgr && mChildFragmentMgr.getBackStackEntryCount() > 1) {
                RkLogger.d(TAG, "doSelfBackPressed");
                mChildFragmentMgr.popBackStack();
                return true;
            } else {
                return false;
            }
        }

    }

    private void loadProhibitedWorlds() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, Define.API.API_PROBIHITED_WORDS, new AsyncHttpResponseHandler() {
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
}
