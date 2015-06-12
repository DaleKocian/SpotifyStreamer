package dalekocian.github.io.spotifystreamer.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.neovisionaries.i18n.CountryCode;

import dalekocian.github.io.spotifystreamer.R;

/**
 * Created by k557782 on 6/12/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference countryCode = (ListPreference) findPreference("country_code");
        CountryCode[] enumConstants = CountryCode.values();
        CharSequence[] countryEntries = new CharSequence[enumConstants.length-1];
        CharSequence[] countryValues = new CharSequence[enumConstants.length-1];
        for (int i = 1; i < enumConstants.length; ++i) {
            countryEntries[i-1] = enumConstants[i].getName();
            countryValues[i-1] = enumConstants[i].getAlpha2();
        }
        countryCode.setEntries(countryEntries);
        countryCode.setEntryValues(countryValues);
    }
}
