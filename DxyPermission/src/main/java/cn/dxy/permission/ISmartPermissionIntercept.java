package cn.dxy.permission;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * 权限拦截器 实现用户侧的业务定制
 * 例如:用户侧申请权限时都会 弹框提醒啊 权限被禁止时 会弹框提醒用户去设置界面处理 等等
 */
public interface ISmartPermissionIntercept {

    /**
     * 业务方处理
     *
     * @param activity
     * @param permission
     * @param callback
     * @return true 拦截了 false 不拦截
     */
    boolean process(@NonNull FragmentActivity activity, @NonNull SmartPermission permission, @NonNull ISmartPermissionInterceptCallback callback);

    /**
     * 是否支持权限被禁止时的提醒 如果返回true 权限申请未通过 会回掉 showDeniedHint 函数  让业务方处理
     *
     * @return
     */
    boolean isSupportDeniedHint();

    void showDeniedHint(@NonNull FragmentActivity activity, @NonNull SmartPermission permission, @NonNull ISmartPermissionInterceptCallback callback);
}
