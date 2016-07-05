package com.tranxuanloc.emobits.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tranxuanloc on 4/16/2016.
 */
public class UserInfoPref {
    private final String NAME_PREF = "user_info";
    private final String DEVICE = "device";
    private final String NAME = "name";
    private final String PARAM = "param";
    private SharedPreferences preferences;

    public void putInfo(Context context, String device, String name, String param) {
        if (preferences == null)
            preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICE, device);
        editor.putString(NAME, name);
        editor.putString(PARAM, param);
        editor.apply();
    }

    public String getDevice(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE);
        return preferences.getString(DEVICE, "");
    }

    public String getName(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE);
        return preferences.getString(NAME, "");
    }

    public String getParam(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE);
        return preferences.getString(PARAM, "");
    }
}
