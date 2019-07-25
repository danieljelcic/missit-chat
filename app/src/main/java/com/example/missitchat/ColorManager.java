package com.example.missitchat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.TypedValue;
import android.view.View;

public class ColorManager {

    public static final int PRIMARY = 0;
    public static final int PRIMARY_DARK = 1;
    public static final int SECONDARY = 2;

    public static Integer parseUsername(String username) {

        Integer sum = 0;

        for (int i = 0; i < username.length(); i++) {
            sum += username.charAt(i);
        }

        return sum % 7;
    }

    public static Integer getColor(String username, int type, Context context) {
        
        switch (parseUsername(username)) {
            case 0 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor1);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor1);
                    default : return context.getResources().getColor(R.color.primaryColor1);
                }
            }
            case 1 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor2);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor2);
                    default: return context.getResources().getColor(R.color.primaryColor2);
                }
            }
            case 2 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor3);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor3);
                    default : return context.getResources().getColor(R.color.primaryColor3);
                }
            }
            case 3 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor4);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor4);
                    default : return context.getResources().getColor(R.color.primaryColor4);
                }
            }
            case 4 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor5);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor5);
                    default : return context.getResources().getColor(R.color.primaryColor5);
                }
            }
            case 5 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor6);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor6);
                    default : return context.getResources().getColor(R.color.primaryColor6);
                }
            }
            case 6 : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor7);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor7);
                    default : return context.getResources().getColor(R.color.primaryColor7);
                }
            }
            default : {
                switch (type) {
                    case PRIMARY : return context.getResources().getColor(R.color.primaryColor);
                    case SECONDARY : return context.getResources().getColor(R.color.secondaryColor);
                    default : return context.getResources().getColor(R.color.primaryColor);
                }
            }
        }
    }

    public static Integer getThemeId(String username, Context context) {
        switch (parseUsername(username)) {
            case 0 : return R.style.MissItTheme1;
            case 1 : return R.style.MissItTheme2;
            case 2 : return R.style.MissItTheme3;
            case 3 : return R.style.MissItTheme4;
            case 4 : return R.style.MissItTheme5;
            case 5 : return R.style.MissItTheme6;
            case 6 : return R.style.MissItTheme7;
            default : return R.style.AppTheme;
        }
    }

    public static int getThemeColor(int type, Context context) {
        Resources.Theme theme = context.getTheme();
        int themeAttrId;
        int defaultColor;

        switch (type) {
            case PRIMARY : {
                themeAttrId = R.attr.colorPrimary;
                defaultColor = context.getResources().getColor(R.color.primaryColor);
                break;
            }
            case PRIMARY_DARK : {
                themeAttrId = R.attr.colorPrimaryDark;
                defaultColor = context.getResources().getColor(R.color.primaryDarkColor);
                break;
            }
            case SECONDARY : {
                themeAttrId = R.attr.colorAccent;
                defaultColor = context.getResources().getColor(R.color.secondaryColor);
                break;
            }
            default: {
                themeAttrId = R.attr.colorAccent;
                defaultColor = context.getResources().getColor(R.color.primaryColor);
                break;
            }
        }

        TypedValue outValue = new TypedValue();

        if (theme.resolveAttribute(themeAttrId, outValue, true)) {
            return context.getResources().getColor(outValue.resourceId);
        } else {
            return defaultColor;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void setDrawableBackgroundColor(View view, int color) {

        GradientDrawable viewBg = (GradientDrawable) view.getBackground();
        viewBg.setColor(color);
        view.setBackground(viewBg);
    }
}
