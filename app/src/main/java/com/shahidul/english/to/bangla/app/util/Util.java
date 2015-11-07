package com.shahidul.english.to.bangla.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shahidul.english.to.bangla.app.config.Configuration;
import com.shahidul.english.to.bangla.app.dao.Database;

/**
 * @author Shahidul Islam
 * @since 7/4/2015.
 */
public class Util {
    public static String getCurrentTableName(){
        if (Configuration.isPrimaryLanguageEnglish){
            return Database.TABLE_FIRST_TO_SECOND;
        }
        else {
            return Database.TABLE_SECOND_TO_FIRST;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isTextEmpty(CharSequence charSequence){
        if (charSequence == null || charSequence.toString().trim().length() == 0){
            return true;
        }
        return false;
    }

    public static String getFirstWord(String commaSeparatedWord){
        if (commaSeparatedWord == null){
            return "";
        }
        String[] words = commaSeparatedWord.split(",");
        if (words == null || words.length == 0){
            return "";
        }
        return words[0].trim();
    }
}
