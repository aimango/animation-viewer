package com.elisa.a5.view;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.elisa.a5.R;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}