package com.donny.appstatistic;

import static com.donny.appstatistic.CommonFunction.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Donny on 8/27/2016.
 */
public class DataSyncService extends Service {

    private static final String sTag = "DataSyncService";
    private static final String uploadUrl = "http://123.207.62.26/receive_file.php";
    private static final int nRepeatPeriod = 3600 * 1000;

    public static void LogToFileAndConsole(String tag, String contain) {
        Log.d(tag, contain);
        LogErrorToFile(tag, contain);
    }

    @Override
    public void onCreate() {
        LogToFileAndConsole(sTag, "onCreate");

        Context context = getApplication();

        // Start DataSyncService
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        PendingIntent intent =
                PendingIntent.getService(context, 0, new Intent(context, DataSyncService.class), PendingIntent.FLAG_CANCEL_CURRENT);
        long nTimeNow = System.currentTimeMillis();
        am.set(AlarmManager.RTC_WAKEUP, nTimeNow + nRepeatPeriod, intent);

        super.onCreate();
    }

    public static void SyncData(Context context) {
        String sLocalTag = "SyncData";

        final String sUserInfo = getUserFullID(context);
        LogToFileAndConsole(sLocalTag, "UserInfo: " + sUserInfo);
        Calendar calendar = Calendar.getInstance();
        MyDate myDate = new MyDate();
        String sFileToUpload;

        for (int i = 0; i < 31; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            myDate.Set(calendar);

            if (CommonFunction.isDataUploaded(context, myDate))
                continue;

            // Upload ScreenUsage.csv
            sFileToUpload = CommonFunction.getScreenUsageFilename(context, myDate);
            if (!CommonFunction.UploadFile(uploadUrl, sUserInfo, sFileToUpload))
                continue;

            // Upload AppsUsage.csv ( a temp file from database )
            sFileToUpload = CommonFunction.getAppsUsageFilename(context, myDate);
            CommonFunction.ExportAppsUsageToCSVFile(context, myDate, sFileToUpload);
            if (!CommonFunction.UploadFile(uploadUrl, sUserInfo, sFileToUpload)) {
                File fileToUpload = new File(sFileToUpload);
                fileToUpload.delete();
                LogToFileAndConsole(sLocalTag, sFileToUpload + " has been deleted.");
                continue;
            }
            File fileToUpload = new File(sFileToUpload);
            fileToUpload.delete();
            LogToFileAndConsole(sLocalTag, sFileToUpload + " has been deleted.");

            // Upload Survey.csv
            sFileToUpload = CommonFunction.getSurveyFilename(context, myDate);
            if (!CommonFunction.UploadFile(uploadUrl, sUserInfo, sFileToUpload))
                continue;

            CommonFunction.setDataUploadStatus_Uploaded(context, myDate);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(sTag, "onStartCommand");

        new Thread() {
            public void run() {
                SyncData(getApplication());
                stopSelf();
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(sTag, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(sTag, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(sTag, "onUnbind");
        return super.onUnbind(intent);
    }


}
