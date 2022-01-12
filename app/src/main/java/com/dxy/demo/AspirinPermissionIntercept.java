package com.dxy.demo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;
import java.util.Date;

import cn.dxy.permission.SmartPermission;
import cn.dxy.permission.ISmartPermissionIntercept;
import cn.dxy.permission.ISmartPermissionInterceptCallback;

public class AspirinPermissionIntercept implements ISmartPermissionIntercept {

    private String groupKey;
    private boolean showOncePerDay;

    private String calculateGroupKey(String... permissions) {
        StringBuilder allPermissionStr = new StringBuilder();
        for (String permission : permissions) {
            allPermissionStr.append(permission);
            allPermissionStr.append("|");
        }
        return "calculate_key_v1_" + allPermissionStr.toString().hashCode();
    }

    @Override
    public boolean process(@NonNull FragmentActivity activity, @NonNull SmartPermission permission, @NonNull ISmartPermissionInterceptCallback callback) {
        this.groupKey = calculateGroupKey(permission.permissions());
        long lastShowPermissionTime = getDxyPermission(activity, groupKey);
        // 如果从未弹出过对话框，则直接弹出第一个对话框
        if (lastShowPermissionTime == -1) {
            showFirstPermissionTipPage(activity, callback);
        } else { //如果已经弹出过对话框
            if (showOncePerDay && isSameDay(lastShowPermissionTime)) {
                //在同一天，且这一天只允许弹出一次权限请求框
                callback.onIntercept();
            } else {
                if (!permission.hasAlwaysDeniedPermission(activity)) {
                    showFirstPermissionTipPage(activity, callback);
                } else {
                    if (showOncePerDay) {
                        setDxyPermission(activity, groupKey);
                    }
                    //现实权限全部被禁止 去设置页面打开的提醒
                    showDeniedHint(activity, permission, callback);
                }
            }
        }
        return true;
    }

    @Override
    public boolean isSupportDeniedHint() {
        return true;
    }

    @Override
    public void showDeniedHint(@NonNull FragmentActivity activity, @NonNull SmartPermission permission, @NonNull ISmartPermissionInterceptCallback callback) {
        //现实权限全部被禁止 去设置页面打开的提醒
        AspirinPermissionDeniedTipFragment fragment = AspirinPermissionDeniedTipFragment.newInstance("相机或麦克风未授权", "授权相机和麦克风后才能正常开播哦。<br/><br/>如需开启相机和麦克风权限，请按如下操作路径：设置-应用-丁香医生-权限");
        fragment.setCallback(callback);
        fragment.show(activity.getSupportFragmentManager(), "AspirinPermissionDeniedTipFragment");
    }

    private void showFirstPermissionTipPage(FragmentActivity activity, ISmartPermissionInterceptCallback callback) {
        AspirinPermissionRequestTipFragment fragment = AspirinPermissionRequestTipFragment.newInstance("“丁香医生”想访问您的相机和麦克风“", "您还没有开启相机和麦克风，开启后才能正常进行直播");
        fragment.setCallback(callback);
        setDxyPermission(activity, groupKey);
        fragment.show(activity.getSupportFragmentManager(), "AspirinPermissionRequestTipFragment");
    }


    /**
     * 判断是否是今天
     */
    public boolean isSameDay(long lastShowPermissionTime) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(new Date(lastShowPermissionTime));
        //纪元 年 日
        return currentCalendar.get(Calendar.ERA) == lastCalendar.get(Calendar.ERA)
                && currentCalendar.get(Calendar.YEAR) == lastCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.DAY_OF_YEAR) == lastCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private long getDxyPermission(Context context, String key){
        return SPUtils.getLong(context, key, -1L);
    }

    private void setDxyPermission(Context context, String key){
        SPUtils.putLong(context, key, System.currentTimeMillis());
    }

}
