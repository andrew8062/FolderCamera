package com.example.android.foldercamera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 8/14/2015.
 */
public class CustomAdapter extends BaseAdapter {

    private static final String TAG = "fc:custom_adapater";
    private FolderList mObject;
    private int mResource;
    private Context mContext;
    private Handler mHandler;

    public CustomAdapter(Context context, int resource, FolderList objects, Handler handler) {
        super();
        mContext = context;
        mResource = resource;
        mObject = objects;
        mHandler = handler;
    }


    @Override
    public int getCount() {
        return mObject.size();
    }

    @Override
    public FolderList.Folder getItem(int position) {
        return mObject.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FolderList.Folder folder = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_listview, parent, false);


        Button bt = (Button) convertView.findViewById(R.id.choose_folder_button);
        bt.setTag(position);
        bt.setOnClickListener(onClickListener);

        TextView tv = (TextView) convertView.findViewById(R.id.choose_folder_textview);
        tv.setTag(position);
        tv.setBackgroundColor(mContext.getResources().getColor((R.color.primary)));
        tv.setTextColor(mContext.getResources().getColor((R.color.text_primary)));
        tv.setOnTouchListener(onTouchListener);
        tv.setText(folder.getName());
        if (folder.getName().equals("default"))
            bt.setVisibility(View.GONE);
        return convertView;
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            final int action = event.getAction();

            Integer colorFrom = mContext.getResources().getColor((R.color.primary));
            Integer colorTo = mContext.getResources().getColor(R.color.primary_dark);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(300);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setBackgroundColor((Integer) animation.getAnimatedValue());
                }
            });
            if (action == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "finder down");
                colorAnimation.start();
            }
            if (action == MotionEvent.ACTION_UP) {
                Log.d(TAG, "finder up");
                int position = (Integer) v.getTag();
                v.setBackgroundColor(colorFrom);
                mHandler.obtainMessage(CustomDialog.DIALOG_LISTVIEW_SELECT, position, 0).sendToTarget();
            }
            if (action == MotionEvent.ACTION_CANCEL) {
                Log.d(TAG, "action outside");
                colorAnimation.reverse();
                v.setBackgroundColor(colorFrom);

            }
            return true;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            mHandler.obtainMessage(CustomDialog.DIALOG_DELETE_BUTTON, position, 0).sendToTarget();

        }
    };

    public void changeData(FolderList object) {
        mObject = object;
        notifyDataSetChanged();
    }

}