package com.example.missitchat;

import android.content.Context;

public class ColorManager {

    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;

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
                    case 0 : return context.getResources().getColor(R.color.primaryColor1);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor1);
                    default : return context.getResources().getColor(R.color.primaryColor1);
                }
            }
            case 1 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor2);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor2);
                    default: return context.getResources().getColor(R.color.primaryColor2);
                }
            }
            case 2 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor3);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor3);
                    default : return context.getResources().getColor(R.color.primaryColor3);
                }
            }
            case 3 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor4);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor4);
                    default : return context.getResources().getColor(R.color.primaryColor4);
                }
            }
            case 4 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor5);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor5);
                    default : return context.getResources().getColor(R.color.primaryColor5);
                }
            }
            case 5 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor6);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor6);
                    default : return context.getResources().getColor(R.color.primaryColor6);
                }
            }
            case 6 : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor7);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor7);
                    default : return context.getResources().getColor(R.color.primaryColor7);
                }
            }
            default : {
                switch (type) {
                    case 0 : return context.getResources().getColor(R.color.primaryColor);
                    case 1 : return context.getResources().getColor(R.color.secondaryColor);
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
}
