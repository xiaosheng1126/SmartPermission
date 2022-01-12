package cn.dxy.permission;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class SmartPermission {

    private final Builder builder;

    private SmartPermission(@NonNull Builder builder) {
        this.builder = builder;
    }

    public @PermissionDef String[] permissions(){
        return builder.permissions;
    }

    public boolean hasAlwaysDeniedPermission(Activity activity){
        return SmartPermissionUtils.isShowRationalePermissions(activity, builder.permissions);
    }

    public boolean request() {
        Activity activity = SmartPermissionManager.get().getCurrentActivity();
        if (activity instanceof FragmentActivity) {
            return request((FragmentActivity) activity);
        }
        if (builder.denied != null) {
            builder.denied.onDenied();
        }
        return false;
    }

    public boolean request(@NonNull Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            return request((FragmentActivity) activity);
        }
        if (builder.denied != null) {
            builder.denied.onDenied();
        }
        return false;
    }

    public boolean request(@NonNull FragmentActivity activity) {
        //第一步先 检测一下 是否有权限 如果有直接结束 回调成功
        if (SmartPermissionUtils.hasPermissions(activity, builder.permissions)) {
            if (builder.granted != null) {
                builder.granted.onGranted();
            }
            return true;
        }
        //走到这里说明 此时app 无权限 但由于用户端需要进行一些弹框提醒操作 因为判断是否存在拦截器
        if (builder.intercept != null) {
             ISmartPermissionInterceptCallback callback = new ISmartPermissionInterceptCallback() {
                @Override
                public void onContinue() {
                    if (builder.intercept.isSupportDeniedHint()) {
                        SmartPermissionManager.get().request(activity, createRequest(() -> builder.intercept.showDeniedHint(activity, SmartPermission.this, this)));
                    } else {
                        SmartPermissionManager.get().request(activity, createRequest(null));
                    }
                }

                @Override
                public void onGoToSetting() {
                    SmartPermissionManager.get().setGoToSetting(activity, createRequest(null));
                }

                @Override
                public void onIntercept() {
                    if (builder.denied != null) {
                        builder.denied.onDenied();
                    }
                }
            };
            boolean intercept = builder.intercept.process(activity, this, callback);
            if (intercept) {
                return false;
            }
        }
        //走到这里 没有其他操作了 直接请求权限
        SmartPermissionManager.get().request(activity, createRequest(null));
        return false;
    }

    private SmartPermissionManager.Request createRequest(ISmartPermissionDenied denied){
        return new SmartPermissionManager.Request(builder.permissions, builder.granted, denied == null ? builder.denied : denied);
    }

    public static class Builder {
        private @Nullable
        ISmartPermissionDenied denied;
        private @Nullable
        ISmartPermissionGranted granted;
        private
        @PermissionDef
        String[] permissions;
        private @Nullable
        ISmartPermissionIntercept intercept;

        public Builder permissions(@PermissionDef String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder denied(@Nullable ISmartPermissionDenied denied) {
            this.denied = denied;
            return this;
        }

        public Builder granted(@Nullable ISmartPermissionGranted granted) {
            this.granted = granted;
            return this;
        }

        public Builder intercept(ISmartPermissionIntercept intercept) {
            this.intercept = intercept;
            return this;
        }

        public SmartPermission build() {
            if (permissions == null || permissions.length <= 0) {
                throw new IllegalArgumentException("permission must not empty");
            }
            return new SmartPermission(this);
        }
    }
}
