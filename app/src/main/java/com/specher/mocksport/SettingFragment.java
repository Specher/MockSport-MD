package com.specher.mocksport;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    EditTextPreference mEditTextPreference;
    public final String SETTING_CHANGED = "com.specher.mocksport.SETTING_CHANGED";

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(1);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditTextPreference = (EditTextPreference) findPreference("magnification");
        changeSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        changeSummary();
        getKey();
        return true;
    }

    private void changeSummary() {
        if (mEditTextPreference != null)
            mEditTextPreference.setSummary(getPreferenceManager().getSharedPreferences().getString("magnification", "10"));
    }
    public void getKey() {
        Intent intent = new Intent(SETTING_CHANGED);
        intent.putExtra("all", getPreferenceManager().getSharedPreferences().getBoolean("all", false));
        intent.putExtra("lock", getPreferenceManager().getSharedPreferences().getBoolean("lock", false));
        intent.putExtra("magnification", getPreferenceManager().getSharedPreferences().getString("magnification", "10"));
        intent.putExtra("chunyu", getPreferenceManager().getSharedPreferences().getBoolean("chunyu", true));
        intent.putExtra("yuedong", getPreferenceManager().getSharedPreferences().getBoolean("yuedong", true));
        intent.putExtra("ledong", getPreferenceManager().getSharedPreferences().getBoolean("ledong", true));
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        changeSummary();
        getKey();
    }
}
