package cn.dxy.permission;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.Map;

final class SmartPermissionUtils {

    private static final int MODE_ASK = 4;
    private static final int MODE_COMPAT = 5;

    public static boolean hasPermissions(Map<String, Boolean> result) {
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isShowRationalePermissions(Activity context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        for (String permission : permissions) {
            if (!isShowRationalePermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShowRationalePermission(Activity context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        return context.shouldShowRequestPermissionRationale(permission);
    }

    public static boolean isShowRationalePermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        for (String permission : permissions) {
            if (!isShowRationalePermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShowRationalePermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        PackageManager packageManager = context.getPackageManager();
        Class<?> pkManagerClass = packageManager.getClass();
        try {
            Method method = pkManagerClass.getMethod("shouldShowRequestPermissionRationale", String.class);
            if (!method.isAccessible()) method.setAccessible(true);
            return (boolean) method.invoke(packageManager, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasPermissions(@NonNull Context context, @PermissionDef String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        AppOpsManager opsManager = null;
        for (String permission : permissions) {
            int result = context.checkPermission(permission, Process.myPid(), Process.myUid());
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String op = AppOpsManager.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                continue;
            }
            if (opsManager == null)
                opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                result = opsManager.unsafeCheckOpNoThrow(op, Process.myUid(), context.getPackageName());
            } else {
                result = opsManager.checkOpNoThrow(op, Process.myUid(), context.getPackageName());
            }
            if (result != AppOpsManager.MODE_ALLOWED && result != MODE_ASK && result != MODE_COMPAT) {
                return false;
            }
        }
        return true;
    }
}
