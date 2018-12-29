package com.candy.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candy.android.R;
import com.candy.android.dialog.DialogTermConfirm;
import com.candy.android.utils.Helpers;

/**
 * @author Favo
 * Created on 13/10/2016.
 */

public class StartPageActivity extends BaseActivity implements View.OnClickListener, DialogTermConfirm.OnClickListener {
    private static final String TAG = "IDK-StartPageActivity";
    private View mStartBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_startpage);

        initView();
    }

    private void initView() {
//        ImageView ivBackGround = (ImageView) findViewById(R.id.iv_background);
//        Glide.with(this).load(R.drawable.start).into(ivBackGround);

        TextView login = (TextView) findViewById(R.id.btn_login);
        mStartBtn = findViewById(R.id.btn_start);

        login.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
    }

    private void showTermOfService() {
        DialogTermConfirm dialogTermConfirm = new DialogTermConfirm();
        dialogTermConfirm.setOnClickListener(this);
        Helpers.showDialogFragment(getSupportFragmentManager(), dialogTermConfirm);
    }

    @Override
    public void onClick(View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.btn_login:
                it = new Intent(this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.btn_start:
                showTermOfService();
                break;
        }
    }

    @Override
    public void onOkClick() {
        gotoProfileRegistrationActivity();
    }
}
