package com.candy.android.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.NetworkUtils;

/**
 * @author NamHV
 * Created on 01/11/2016.
 */

public class DialogTermConfirm extends DialogFragment implements View.OnClickListener {

    private OnClickListener onClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_term_of_service, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button btnStart = (Button) view.findViewById(R.id.btn_dialog_start);
        btnStart.setOnClickListener(this);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_dialog_confirm);
        RadioButton radioButton = view.findViewById(R.id.rb_not_accept);
        radioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_accept:
                        btnStart.setEnabled(true);
                        break;
                    case R.id.rb_not_accept:
                        btnStart.setEnabled(false);
                        break;
                }
            }
        });
        ImageView termsCancelButton = (ImageView) view.findViewById(R.id.terms_cancel);
        termsCancelButton.setOnClickListener(this);

        if(SettingManager.getInstance(getActivity()).getConfig()!=null){
            String webBaseUrl = SettingManager.getInstance(getActivity()).getConfig().getWebviewBaseUrl();
            String termUrl = webBaseUrl + Define.WebUrl.TERM;

            if (!NetworkUtils.hasConnection(getActivity())) {
                termUrl = Define.WebUrl.OFFLINE_TERM;
            }
            // load terms content
            WebView termWebView = (WebView) view.findViewById(R.id.s_terms);
            termWebView.loadUrl(termUrl);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_start:
                if (onClickListener != null) {
                    dismiss();
                    onClickListener.onOkClick();
                }
                break;
            case R.id.terms_cancel:
                dismiss();
                break;
        }
    }

    public interface OnClickListener {
        void onOkClick();
    }
}
