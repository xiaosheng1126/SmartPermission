package com.dxy.demo;

import android.content.Context;

import com.tencent.mmkv.MMKV;

/**
 * @author chenhui
 */
public class SPUtils {

    private static MMKV getMMKV(Context context) {
        return MMKV.mmkvWithID("share_data", MMKV.MULTI_PROCESS_MODE, "1111111");
    }

    /**
     * 保存 String
     */
    public static void putString(Context context, String key, String str) {
        getMMKV(context).putString(key, str);
    }

    /**
     * 保存 Int
     */
    public static void putInt(Context context, String key, int i) {
        getMMKV(context).putInt(key, i);
    }

    /**
     * 保存 long
     */
    public static void putLong(Context context, String key, long i) {
        getMMKV(context).putLong(key, i);
    }

    /**
     * 保存 boolean
     */
    public static void putBoolean(Context context, String key, boolean b) {
        getMMKV(context).putBoolean(key, b);
    }

    /**
     * 获取 String
     */
    public static String getString(Context context, String key, String str) {
        return getMMKV(context).getString(key, str);
    }

    /**
     * 获取 Int
     */
    public static int getInt(Context context, String key, int i) {
        return getMMKV(context).getInt(key, i);
    }

    /**
     * 获取 long
     */
    public static long getLong(Context context, String key, long i) {
        return getMMKV(context).getLong(key, i);
    }


    /**
     * 获取 boolean
     */
    public static boolean getBoolean(Context context, String key, boolean b) {
        return getMMKV(context).getBoolean(key, b);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        getMMKV(context).removeValueForKey(key);
    }
}