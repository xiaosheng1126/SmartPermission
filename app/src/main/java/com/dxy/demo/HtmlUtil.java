package com.dxy.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

/**
 * #类的用途说明#
 *
 * @author maxl
 * @date 2015/12/03 15:43
 */
public class HtmlUtil {


    public static Spanned changeHighLightTextColor(String origin) {
        if (!TextUtils.isEmpty(origin)) {
            if (origin.contains("<em")) {
                origin = origin.replaceAll("<em[^>]*>", "<font color=\"#FEA220\">");
            }
            if (origin.contains("</em>")) {
                origin = origin.replaceAll("</em>", "</font>");
            }

            if (origin.contains("\n")) {
                origin = origin.replaceAll("\n", "<br/>");
            }
        }
        return fromHtml(origin);
    }


    /**
     * 显示带图片的HTml文本
     *
     * @return
     */
    public static Spanned changeHighLightTextColor2(String origin, Context context) {
        if (!TextUtils.isEmpty(origin)) {
            if (origin.contains("<em")) {
                origin = origin.replaceAll("<em[^>]*>", "<font color=\"#FEA220\">");
            }
            if (origin.contains("</em>")) {
                origin = origin.replaceAll("</em>", "</font>");
            }

            if (origin.contains("\n")) {
                origin = origin.replaceAll("\n", "<br/>");
            }
        }
        return Html.fromHtml(origin, getImageGetter(context), null);
    }


    private static Html.ImageGetter getImageGetter(Context context) {
        return source -> {
            Drawable drawable = ContextCompat.getDrawable(context, Integer.parseInt(source));
            drawable.setBounds(6, 0, 70 + 12, 70);
            return drawable;
        };
    }


    public static Spanned fromHtml(String html) {
        html = html == null ? "" : html;
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }



}