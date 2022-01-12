package com.dxy.demo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import cn.dxy.permission.ISmartPermissionInterceptCallback;

public class AspirinPermissionRequestTipFragment extends DialogFragment {

    public static AspirinPermissionRequestTipFragment newInstance(String title, String htmlSubTitle) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("htmlSubTitle", htmlSubTitle);
        AspirinPermissionRequestTipFragment fragment = new AspirinPermissionRequestTipFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ISmartPermissionInterceptCallback callback;


    public void setCallback(ISmartPermissionInterceptCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_permission_tip_fragment, container, false);
        TextView titleView = view.findViewById(R.id.title);
        TextView contentView = view.findViewById(R.id.content);
        View hasKnownButton = view.findViewById(R.id.btn_has_known);
        hasKnownButton.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (callback != null) {
                callback.onContinue();
            }
        });
        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String subTitle = getArguments().getString("htmlSubTitle");
            titleView.setText(title);
            contentView.setText(HtmlUtil.fromHtml(subTitle));
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        //把 dialog 背景设置为透明，因为有些图片会设置为圆角，不设置的话，圆角图片就会显示为白色
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        //dialog宽度为屏幕的 75%，在各种屏幕上会显示的好一些
        if (getContext() != null) {
            lp.width = (int) (DisplayUtil.getScreenWidth(getContext()) * 0.75f);
        }
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}
