package com.donny.appstatistic;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;

import static com.donny.appstatistic.CommonFunction.*;

/**
 * Created by Donny on 10/13/2016.
 */
public class PushPromotionService extends Service {

    private static final String sTag = "PushPromotionService";
    private static final String queryURL = "http://123.207.62.26/push_file.php";
    private static boolean bRunning = false;

    public static void PushNotification(Context context, NotificationManager mNotificationManager) {
        final String sLocalTag = "PushNotification";
        final String sTitle = "Apps Statistic";
        final String sDetail = "我们邀请您填写一项在线的调查问卷";

        if (mNotificationManager == null) return;

        String sTargetUrl = ReceivePushData(context, queryURL);
        if (sTargetUrl.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sTargetUrl));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(sTitle).setContentText(sDetail).setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)//.setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // .setColor(0xFFFFFFFF)
                .setContentIntent(contentIntent).build();
        Log.v(sLocalTag, "Notify event. Id=" + LoadPushNumber(context) + "; Url=" + sTargetUrl + ".");
        mNotificationManager.notify(0, notification);
    }

    public static void NotifySurvey(Context context, NotificationManager mNotificationManager) {
        final String sLocalTag = "NotifySurvey";
        final String sTitle = "Apps Statistic";
        final String sDetail = "我们邀请您填写本周的问卷";

        if (mNotificationManager == null) return;
        if (IsSurveyFinished(context)) return;
        if (IsSurveyNotificationPushed(context)) return;

        Intent intent = new Intent(context, SurveyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(sTitle).setContentText(sDetail).setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)//.setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // .setColor(0xFFFFFFFF)
                .setContentIntent(contentIntent).build();
        Log.v(sLocalTag, "Notify Survey.");
        mNotificationManager.notify(1, notification);

        SetSurveyNotificationPushed(context);
    }

    public static void NotificationService(Context context) {
        final int nRepeatPeriod = 2 * 3600 * 1000; // 30 mins
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // new File(getSurveyNotificationRecordFilename(context)).delete();
        while (true) {
            NotifySurvey(context, mNotificationManager);
            PushNotification(context, mNotificationManager);
            try {
                Thread.sleep(nRepeatPeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(sTag, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(sTag, "onStartCommand");

        if (!bRunning) {

            bRunning = true;

            new Thread() {
                public void run() {
                    NotificationService(getApplication());
                }
            }.start();

            new Thread() {
                public void run() {
                    KeepServiceRunning(getApplication(), ScreenUsageTrackService.class);
                }
            }.start();

        }

        flags |= START_STICKY | START_CONTINUATION_MASK;
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
