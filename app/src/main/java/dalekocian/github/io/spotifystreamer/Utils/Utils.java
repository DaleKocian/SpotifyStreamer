package dalekocian.github.io.spotifystreamer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by k557782 on 6/12/2015.
 */
public class Utils {
    public static String getCountryCode(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String countryCode = sharedPref.getString("country_code", "US");
        return countryCode;
    }
}
