package cn.dxy.permission;

/**
 * 权限拦截回调监听器
 */
public interface ISmartPermissionInterceptCallback {
    /**
     * 继续申请权限
     */
    void onContinue();

    /**
     * 去设置界面手动打开权限
     */
    void onGoToSetting();

    /**
     * 拦截
     */
    void onIntercept();
}
