package com.example.android.foldercamera;

import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Andrew on 8/13/2015.
 */
public class FolderList {
    ArrayList<Folder> folderArrayList = new ArrayList<Folder>();

    public FolderList() {

    }

    public void add(String name, String path) {
        Folder folder = new Folder(name, path);
        folderArrayList.add(folder);
    }
    public void remove(int pos){
        folderArrayList.remove(pos);
    }


    public String getName(int index) {
        return folderArrayList.get(index).getName();
    }

    public String getPath(int index) {
        return folderArrayList.get(index).getPath();
    }

    public ArrayList<Folder> deserialize(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ArrayList<Folder>>() {}.getType());

    }

    public ArrayList<String> getFolderNameList() {
        ArrayList<String> folderNameList = new ArrayList<String>();
        for (Folder f : folderArrayList)
            folderNameList.add(f.getName());
        return folderNameList;
    }

    public void getFolderList(SharedPreferences settings, String key) {
        if (settings.contains(key)) {
            String json = settings.getString(key, "");
            folderArrayList = deserialize(json);
        } else {
            Folder folder = new Folder("default", Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString());
            folderArrayList.add(folder);
        }
    }

    public void saveToSharedPreference(SharedPreferences settings, String key) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, gson.toJson(folderArrayList, ArrayList.class));
        editor.commit();

    }

    public int size(){
        return folderArrayList.size();
    }

    public Folder get(int pos){
        return folderArrayList.get(pos);

    }

    public class Folder {
        private String name;
        private String path;

        public Folder(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
