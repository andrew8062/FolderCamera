package com.example.android.foldercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Andrew on 8/11/2015.
 */
public  class PictureSave {
    private static final String TAG = "fc:pictureSave";
    private String path;
    private String folderName;
    public PictureSave(Context mContext){
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"\\"+mContext.getResources().getString(R.string.default_picture_direcory);
    }
    public PictureSave(String path){
        this.path = path;
    }
    public void  save (byte[] data){
//        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        String timeStamp = new SimpleDateFormat("/yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        //byte數组轉換成Bitmap
        //拍下圖片顯示在下面的ImageView裡
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdirs();
        FileOutputStream fop;
        try {
            File file = new File(path,timeStamp+".jpg");
            fop = new FileOutputStream(file);
            fop.write(data);
            Log.d(TAG, "picture location: "+path+timeStamp+".jpg");
            //實例化FileOutputStream，參數是生成路徑
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
            //壓缩bitmap寫進outputStream 參數：輸出格式  輸出質量  目標OutputStream
            //格式可以為jpg,png,jpg不能存儲透明
            fop.close();
            System.out.println("拍照成功");
            //關閉流
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            System.out.println("FileNotFoundException");

        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("IOException");
        }
//        return bmp;
    }
    public void setFolderNameAndPath(String name, String path){
        folderName = name;
        this.path = path;
    }
}
