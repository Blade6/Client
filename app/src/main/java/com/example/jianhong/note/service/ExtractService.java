package com.example.jianhong.note.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.utils.CommonUtils;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PreferencesUtils;

/**
 * Created by jianhong on 2018/3/17.
 */

public class ExtractService extends Service {

    private static final String TAG = ExtractService.class.getSimpleName();

    public static final int NOTHING = 0;
    public static final int START_EXTRACT = 1;
    public static final int STOP_EXTRACT = 2;

    private Context mContext;

    private boolean isExtract = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        isExtract = PreferencesUtils.getBoolean(PreferencesUtils.LIGHTNING_EXTRACT);

        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (isExtract) {
                    ClipData data = cm.getPrimaryClip();
                    ClipData.Item item = data.getItemAt(0);
                    int extractLocation = PreferencesUtils.getInt(PreferencesUtils.LIGHTNING_EXTRACT_SAVE_LOCATION);

                    String extractGroup = CommonUtils.extractNote(item.getText().toString(), extractLocation, mContext);
                    if (extractGroup != null) {
                        Toast.makeText(mContext, getString(R.string.already_extract_to) + extractGroup, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.extract_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra("command", NOTHING);
        if (START_EXTRACT == command) {
            startExtract();
        } else if (STOP_EXTRACT == command) {
            stopExtract();
        } else if (NOTHING == command) {
            LogUtils.i(TAG, "nothing");
        } else {
            LogUtils.i(TAG, "command error");
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void startExtractTask(Context context) {
        Intent intent = new Intent(context, ExtractService.class);
        intent.putExtra("command", ExtractService.START_EXTRACT);
        context.startService(intent);
    }

    public static void stopExtractTask(Context context) {
        Intent intent = new Intent(context, ExtractService.class);
        intent.putExtra("command", ExtractService.STOP_EXTRACT);
        context.startService(intent);
    }

    private void startExtract() {
        isExtract = true;
        Toast.makeText(mContext, R.string.switch_lightning_extract_on, Toast.LENGTH_SHORT).show();
    }

    private void stopExtract() {
        isExtract = false;
        Toast.makeText(mContext, R.string.switch_lightning_extract_off, Toast.LENGTH_SHORT).show();

        stopSelf(); // 关闭服务
    }

}
