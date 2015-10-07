package com.example.android.foldercamera;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * Created by Andrew on 8/12/2015.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    public static final int DIALOG_LISTVIEW_SELECT = 1;
    public static final int DIALOG_DELETE_BUTTON = 2;
    private static final int CODE_SETTING_INTENT=3;

    private final String SHARED_PREFERENCE_FOLDER_LIST = "fc:folderdialog";
    private final String DCIM_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM).toString();


    private Activity mActivity;
    private ListView listview;
    private ImageButton button;
    private EditText editText;

    private FolderList folderList;
    private CustomAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> folderNameList;
    private PictureSave mPictureSave;
    private ImageButton btn_setting;

    private View.OnClickListener btnSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, Settings.class);

            mActivity.startActivityForResult(intent, CODE_SETTING_INTENT);
        }
    };


    public CustomDialog(Activity activity, PictureSave pictureSave) {
        super(activity);
        mActivity = activity;
        mPictureSave = pictureSave;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_dialog);
        btn_setting = (ImageButton)findViewById(R.id.button_settings);

        btn_setting.setOnClickListener(btnSettingsClickListener);

        listview = (ListView) findViewById(R.id.dialog_listview);
        editText = (EditText) findViewById(R.id.dialog_edittext);
        button = (ImageButton) findViewById(R.id.dialog_button);

        button.setOnClickListener(this);

        folderList = new FolderList(getContext());
        folderNameList = new ArrayList<String>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        setTitle("選擇項目");
        setupListView();
    }

    private void setupListView() {
        folderList.getFolderList(sharedPreferences, mActivity.getResources().getString(R.string.pref_folder_list));
        folderNameList = folderList.getFolderNameList();
        mAdapter = new CustomAdapter(mActivity, android.R.layout.select_dialog_singlechoice, folderList, handler);
        listview.setAdapter(mAdapter);
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mPictureSave.setFolderNameAndPath(folderList.getName(position), folderList.getPath(position));
//                Toast.makeText(mActivity, "設定照片儲存位置為: " + folderList.getName(position), Toast.LENGTH_SHORT).show();
//                dismiss();
//
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_button:
                //if editorText is not empty
                if (!editText.getText().toString().equals("")) {
                    String folderName = editText.getText().toString();
                    String folderPath = DCIM_PATH + "/" + folderName;
                    folderList.add(folderName, folderPath);
                    folderList.saveToSharedPreference(sharedPreferences, mActivity.getResources().getString(R.string.pref_folder_list));
                    folderNameList = folderList.getFolderNameList();
                    mPictureSave.setFolderNameAndPath(folderName, folderPath);
                    mAdapter.changeData(folderList);
                    Toast.makeText(mActivity, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = -1;
            switch (msg.what) {
                case DIALOG_LISTVIEW_SELECT:
                    position = msg.arg1;
                    mPictureSave.setFolderNameAndPath(folderList.getName(position), folderList.getPath(position));
                    Toast.makeText(mActivity, "設定照片儲存位置為: " + folderList.getName(position), Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case DIALOG_DELETE_BUTTON:
                    position = msg.arg1;
                    folderList.remove(position);
                    mAdapter.changeData(folderList);
                    break;
            }
        }

    };

}
