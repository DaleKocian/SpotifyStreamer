package dalekocian.github.io.spotifystreamer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Dale Kocian on 6/12/2015.
 */
public class Utils {
    public static String getCountryCodeFromSettings(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString("country_code", "US");
    }
}
