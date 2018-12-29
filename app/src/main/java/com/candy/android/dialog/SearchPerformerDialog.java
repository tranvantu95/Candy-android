package com.candy.android.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.configs.Define;
import com.candy.android.fragment.PerformersListFragment;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Locale;

/**
 * Edit by TuTv on 09/08/2018.
 */

public class SearchPerformerDialog extends DialogFragment implements View.OnClickListener {

    private Context mContext;

    private TextView edArea;
    private EditText etName;
    private RangeSeekBar<Integer> seekbarAge;
    private TextView tvAge;
    private CheckBox cbIncludingSecret, cbRecommendedGirls, cbLiveDistributed;
    
//    private String area;
//    private boolean includingSecret, recommendedGirls, liveDistributed;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_search_perform_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //findView
        edArea = (EditText) view.findViewById(R.id.ed_area);
        etName = (EditText) view.findViewById(R.id.ed_name);
        seekbarAge = (RangeSeekBar<Integer>) view.findViewById(R.id.seekbar_age);
        tvAge = (TextView) view.findViewById(R.id.tv_age);
        cbIncludingSecret = (CheckBox) view.findViewById(R.id.cb_including_secret);
        cbRecommendedGirls = (CheckBox) view.findViewById(R.id.cb_recommended_girls);
        cbLiveDistributed = (CheckBox) view.findViewById(R.id.cb_live_distributed);

        //setup
        edArea.setEnabled(false);

        etName.clearFocus();
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etName.clearFocus();
                }
                return false;
            }
        });

        seekbarAge.setNotifyWhileDragging(true);
        seekbarAge.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Integer o, Integer t1) {
                updateTvAge(o, t1);
            }
        });

        //listener
        view.findViewById(R.id.btn_area).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);

        //restore
        restoreView();

    }

    private void updateTvAge(Integer from, Integer to) {
        if(from == null || to == null) return;
        String text = "" + from + (!from.equals(to) ? " - " + to + " " : "") + getString(R.string.dialog_search_age);
        tvAge.setText(text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_area:
                showAreaDialog();
                break;
                
            case R.id.btn_search:
                Fragment parentFragment = getTargetFragment();

                if (parentFragment instanceof PerformersListFragment) {
                    String name = etName.getText().toString();
                    String area = edArea.getText().toString();
//                    if(getString(R.string.area_undefined).equals(area)) area = null;
                    int pickup = cbRecommendedGirls.isChecked() ? 1 : 0;
                    ((PerformersListFragment) parentFragment).loadSearchData(null, name, area, null, null, null, pickup, cbLiveDistributed.isChecked());
                }
                
                dismiss();

                break;

            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private void showAreaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setItems(Define.PERFORMER_AREA, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String area = Define.PERFORMER_AREA[i];
                if(getString(R.string.area_undefined).equals(area)) area = null;
                edArea.setText(area);
            }
        });
        dialogBuilder.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onClose();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        onClose();
    }

    private void onClose() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).hideKeyboard();
        }

        cacheView();
    }

    private void restoreView() {
        tvAge.post(new Runnable() {
            @Override
            public void run() {
                updateTvAge(seekbarAge.getSelectedMinValue(), seekbarAge.getSelectedMaxValue());
            }
        });

//        if(!TextUtils.isEmpty(area)) edArea.setText(area);
//        cbIncludingSecret.setChecked(includingSecret);
//        cbRecommendedGirls.setChecked(recommendedGirls);
//        cbLiveDistributed.setChecked(liveDistributed);
    }

    private void cacheView() {
//        area = edArea.getText().toString();
//        includingSecret = cbIncludingSecret.isChecked();
//        recommendedGirls = cbRecommendedGirls.isChecked();
//        liveDistributed = cbLiveDistributed.isChecked();
    }

}
