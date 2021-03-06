package com.example.android.foldercamera;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.List;

public class Settings extends PreferenceActivity {

    private final int CODE_DIRETORY_PICKER = 1;
    ListPreference listPreference;
    Preference directoryPicker;
    SharedPreferences preferences;
    Intent return_data = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        preferences =  getPreferenceManager().getDefaultSharedPreferences(this);
        listPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_resolution_key));
       // setupListPreference();
        setupDirectoryPickerPreference();
        setResult(Activity.RESULT_CANCELED);

    }

    private void setupDirectoryPickerPreference(){
        directoryPicker = findPreference(getResources().getString(R.string.pref_default_directory));

        String path = preferences.getString(directoryPicker.getKey(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        directoryPicker.setSummary(path);
        directoryPicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(i, CODE_DIRETORY_PICKER);
                return true;
            }
        });

    }
   SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
       @Override
       public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(listPreference.getKey())){
                setResult(Activity.RESULT_OK);
            }
       }
   };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DIRETORY_PICKER && resultCode == Activity.RESULT_OK) {

            SharedPreferences.Editor editor = directoryPicker.getEditor();
            String path = data.getData().getPath();
            directoryPicker.setSummary(path);
            editor.putString(directoryPicker.getKey(), path);
            editor.commit();
        }
    }
}
