package dalekocian.github.io.spotifystreamer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import dalekocian.github.io.spotifystreamer.R;

/**
 * Created by Dale Kocian on 6/12/2015.
 */
public class Utils {
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss", Locale.US);
    public static final String ZERO_TIME_REGEX = "^00:(?:0){0,1}";

    static {
        TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone(Constants.UTC_TIME_ZONE));
    }
    public static String getCountryCodeFromSettings(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
        return sharedPref.getString(resources.getString(R.string.country_code_list_pref_key),
                resources.getString(R.string.default_country_code));
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNullOrEmpty(@Nullable Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static String emptyToNull(@Nullable String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    public static Byte getByteFromBoolean(Boolean bool) {
        return (byte) ((bool != null && bool) ? 1 : 0);
    }

    public static int getIntFromBoolean(Boolean bool) {
        if (bool == null) {
            return -1;
        } else if (bool) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Boolean getBooleanFromInt(int bool) {
        switch (bool) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                return null;
        }
    }

    public static Bundle createBundleFromMap(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    public static Map<String, String> createMapFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>(bundle.keySet().size());
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }

    public static String millisecondsToTimeString(long timeInMilliseconds) {
        String format = TIME_FORMATTER.format(new Date(timeInMilliseconds));
        return format.replaceFirst(ZERO_TIME_REGEX, "");
    }

    public static int getProgressPercentage(long currentTimeInMilliseconds, long totalDurationInMilliseconds) {
        Double percentage = (double) currentTimeInMilliseconds / totalDurationInMilliseconds * Constants.MAX_PROGRESS;
        return percentage.intValue();
    }

    public static int progressToMilliseconds(int progress, int totalDurationInMilli) {
        return (int) ((double) progress / Constants.MAX_PROGRESS * totalDurationInMilli);
    }
}
