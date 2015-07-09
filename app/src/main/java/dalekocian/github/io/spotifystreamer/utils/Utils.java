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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dalekocian.github.io.spotifystreamer.R;

/**
 * Created by Dale Kocian on 6/12/2015.
 */
public class Utils {
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

    public static Byte getByteFromBoolean(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    public static Bundle createBundleFromMap(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    public static Map<String, String> getMapFromBundle(Bundle bundle) {
        Map<String, String> map = new HashMap<>(bundle.keySet().size());
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }
}
