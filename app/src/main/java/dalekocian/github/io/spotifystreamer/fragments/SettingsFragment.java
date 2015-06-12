package dalekocian.github.io.spotifystreamer.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.neovisionaries.i18n.CountryCode;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.Utils.Utils;

/**
 * Created by Dale Kocian on 6/12/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final ListPreference countryCode = (ListPreference) findPreference("country_code");
        countryCode.setEntries(getCountryEntriesValues().countryEntries);
        countryCode.setEntryValues(getCountryEntriesValues().countryValues);
        countryCode.setOnPreferenceChangeListener(getCountryCodePreferenceChangeListener());
        String currentCountryCode = Utils.getCountryCodeFromSettings(getActivity());
        countryCode.setValue(currentCountryCode);
        countryCode.setSummary(currentCountryCode);
    }

    private Preference.OnPreferenceChangeListener getCountryCodePreferenceChangeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        };
    }

    private static CountryEntriesValues getCountryEntriesValues() {
        return CountryEntriesValuesHolder.countryEntriesValues;
    }

    private static class CountryEntriesValuesHolder {
        private static final CountryEntriesValues countryEntriesValues = CountryEntriesValues.getNewInstance();
    }

    static class CountryEntriesValues {
        CharSequence[] countryEntries;
        CharSequence[] countryValues;
        public static CountryEntriesValues getNewInstance() {
            CountryEntriesValues countryEntriesValues = new CountryEntriesValues();
            CountryCode[] enumConstants = CountryCode.values();
            countryEntriesValues.countryEntries = new CharSequence[enumConstants.length-1];
            countryEntriesValues.countryValues = new CharSequence[enumConstants.length-1];
            for (int i = 1; i < enumConstants.length; ++i) {
                countryEntriesValues.countryEntries[i-1] = enumConstants[i].getName();
                countryEntriesValues.countryValues[i-1] = enumConstants[i].getAlpha2();
            }
            return countryEntriesValues;
        }
    }
}
