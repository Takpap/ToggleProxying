package com.takpap.toggleproxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class UtilShardPreference {
    public static void setShardPreference(Context context, String key, String value){
        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit();

    }

}
