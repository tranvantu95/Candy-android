package com.candy.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;

/**
 * @author Favo
 * Created on 24/10/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private DialogBuilder.NoticeDialog mRetryDialog;
    protected boolean isAlive;

    /**
     * knv added, upon QuanNT agreement
     */
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
    }

    /**
     * Store the member's info to disk
     * Most of result from APIs doesn't give id and/or pwd, so we need to pass id and pwd also, to store
     * into disk, too
     *
     * @param member   The member to be stored
     * @param id       The member's ID to be stored
     * @param password The member;s Pwd to be stored
     */
    protected void doSaveMemberInfo(MemberInformation member, String id, String password, boolean is3Day) {
        doSaveMemberInfo(member, id, password, SettingManager.getInstance(this).isNameSet(), is3Day);
    }

    /**
     * Save the given member, id, pass, and whether it's a totally new member
     *
     * @param isNameSet if true, the member's mail is set
     */
    protected void doSaveMemberInfo(MemberInformation member, String id, String password, boolean isNameSet, boolean is3Day) {
        member.setId(id);
        member.setPass(password);
        member.setmIs3Day(is3Day);
        SettingManager.getInstance(this).save(member);
        SettingManager.getInstance(this).setIsNameSet(isNameSet);
    }

    protected void gotoMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void gotoMainScreenFirst() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void gotoProfileRegistrationActivity() {
        Intent intent = new Intent(this, ProfileRegistrationV2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void checkConnectivity() {
        if (!NetworkUtils.hasConnection(this)) {
            showRetryConnectionDialog();
        }
    }

    /**
     * knv added
     */
    protected void showRetryConnectionDialog() {
        boolean dialogShown = mRetryDialog != null && mRetryDialog.getDialog() != null
                && mRetryDialog.getDialog().isShowing();

        if (!dialogShown) {
            mRetryDialog = DialogBuilder.NoticeDialog.newInstance(
                    getString(R.string.can_not_connect_to_server), getString(R.string.retry), false);
            mRetryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    mRetryDialog.dismiss();
                    onRetryConnectionOk();
                }

                @Override
                public void onCancelClick() {

                }
            });
            mRetryDialog.setOnDialogBackPress(new DialogBuilder.OnDialogBackPress() {
                @Override
                public void onDialogBackPress() {
                    onRetryConnectionOk();
                }
            });

            Helpers.showDialogFragment(getSupportFragmentManager(), mRetryDialog);
        }
    }

    protected void onRetryConnectionOk() {
        //no-op
    }

    private DialogBuilder.NoticeTitleDialog mNoticeCheckCodeDialog;

    public void showDialogErrorMemberCode(Context context) {
        mNoticeCheckCodeDialog = DialogBuilder.NoticeTitleDialog.newInstance(context.getString(R.string.purchase_point_member_code_zero_title), context.getString(R.string.purchase_point_member_code_zero_message));
        mNoticeCheckCodeDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mNoticeCheckCodeDialog.dismiss();
            }

            @Override
            public void onCancelClick() {

            }
        });
        Helpers.showDialogFragment(((FragmentActivity) context).getSupportFragmentManager(), mNoticeCheckCodeDialog);
    }

}
