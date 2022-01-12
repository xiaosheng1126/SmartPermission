package cn.dxy.permission;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限管理器
 * 实现权限的申请
 */
public class SmartPermissionManager {

    private static SmartPermissionManager sInstance = null;
    private final Map<String, Entry<String[]>> permissionMap;
    private final Map<String, Entry<Void>> goToSettingMap;
    private WeakReference<Activity> currentActivity;

    protected static synchronized void init(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new SmartPermissionManager(context);
        }
    }

    public static SmartPermissionManager get() {
        return sInstance;
    }

    public @Nullable
    Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    private SmartPermissionManager(@NonNull Context context) {
        final Application application = (Application) context.getApplicationContext();
        this.permissionMap = new HashMap<>(4);
        this.goToSettingMap = new HashMap<>(4);
        application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                currentActivity = new WeakReference<>(activity);
                if (activity instanceof FragmentActivity) {
                    registerRequestPermissionForActivityResult((FragmentActivity) activity);
                }
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                clearCurrent(activity);
                unRegisterRequestPermissionForActivityResult(activity);
            }
        });
    }

    public void request(@NonNull Activity activity, @NonNull Request request) {
        Entry<String[]> entry = permissionMap.get(activity.toString());
        if (entry != null && entry.launcher != null) {
            entry.request = request;
            entry.launcher.launch(request.permissions);
        }
    }

    public void setGoToSetting(@NonNull Activity activity, @NonNull Request request) {
        Entry<Void> entry = goToSettingMap.get(activity.toString());
        if (entry != null && entry.launcher != null) {
            entry.request = request;
            entry.launcher.launch(null);
        }
    }

    private void clearCurrent(@NonNull Activity activity) {
        if (getCurrentActivity() == activity) {
            currentActivity.clear();
            currentActivity = null;
        }
    }

    private void unRegisterRequestPermissionForActivityResult(@NonNull Activity activity) {
        permissionMap.remove(activity.toString());
        goToSettingMap.remove(activity.toString());
    }

    private void registerRequestPermissionForActivityResult(@NonNull FragmentActivity activity) {
        ActivityResultCallback<Map<String, Boolean>> callback = result -> {
            Entry<String[]> entry = permissionMap.get(activity.toString());
            if (entry == null || entry.request == null) {
                return;
            }
            //获得权限
            if (SmartPermissionUtils.hasPermissions(result)) {
                if (entry.request.granted != null) {
                    entry.request.granted.onGranted();
                }
            } else {
                //无权限
                if (entry.request.denied != null) {
                    entry.request.denied.onDenied();
                }
            }
        };
        ActivityResultLauncher<String[]> launcher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), callback);
        permissionMap.put(activity.toString(), new Entry<>(launcher));

        ActivityResultCallback<Void> callback2 = result -> {
            Entry<Void> entry = goToSettingMap.get(activity.toString());
            if (entry == null || entry.request == null) {
                return;
            }
            //获得权限
            if (SmartPermissionUtils.hasPermissions(activity, entry.request.permissions)) {
                if (entry.request.granted != null) {
                    entry.request.granted.onGranted();
                }
            } else {
                //无权限
                if (entry.request.denied != null) {
                    entry.request.denied.onDenied();
                }
            }
        };
        ActivityResultLauncher<Void> launcher2 = activity.registerForActivityResult(new GoToSettingResultContract(), callback2);
        goToSettingMap.put(activity.toString(), new Entry<>(launcher2));
    }


    private static class Entry<I> {
        private final ActivityResultLauncher<I> launcher;
        private Request request;

        public Entry(ActivityResultLauncher<I> launcher) {
            this.launcher = launcher;
        }
    }

    static class Request {
        private @PermissionDef
        final String[] permissions;
        private final ISmartPermissionGranted granted;
        private final ISmartPermissionDenied denied;

        public Request(@PermissionDef String[] permissions, ISmartPermissionGranted granted, ISmartPermissionDenied denied) {
            this.permissions = permissions;
            this.granted = granted;
            this.denied = denied;
        }
    }
}
