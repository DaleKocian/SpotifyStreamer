package dalekocian.github.io.spotifystreamer.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

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
}
