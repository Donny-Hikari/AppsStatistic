package com.donny.appstatistic;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.donny.appstatistic.CommonFunction.KeepServiceRunning;

/**
 * Created by Donny on 7/26/2016.
 */
public class ScreenUsageTrackService extends Service {

    private static final String sTag = "ScreenUsageTrackService";

    private static final String sAppsUsageUpdated = "com.donny.appstatistic.appsUsageUpdated";
    //private static final String sSharedPreferenceName = "myUsageStat";
    //private static final String sspScrOnTime = "ScrOnTime";
    //private static final String sspScrOnLastTime = "ScrOnLastTime";
    //private static final String sspScrPowerStatus = "ScrPowerStatus"; // true for On, false for Off
    // private static final int NOTIFY_ID = 1234;//通知的唯一标识符

    /*
    //主要功能，广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sp = getSharedPreferences(sSharedPreferenceName, Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sp.edit();

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //保存屏幕启动时的毫秒数
                Log.d(sTag, "Screen ON broadcast received.");
                editor.putLong(sspScrOnLastTime, new Date().getTime());
                editor.putBoolean(sspScrPowerStatus, true);
                editor.commit();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //保存屏幕总工作时间
                Log.d(sTag, "Screen OFF broadcast received.");
                long lasttime = sp.getLong(sspScrOnLastTime, new Date().getTime());
                long sum = sp.getLong(sspScrOnTime, 0L);
                sum += new Date().getTime() - lasttime;
                editor.putLong(sspScrOnTime, sum);
                editor.putBoolean(sspScrPowerStatus, false);
                editor.commit();
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                Log.d(sTag, "Time-tick broadcast received.");
                Calendar cal = Calendar.getInstance();
                if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                    //每天凌晨自动更新数据
                    editor.putLong(sspScrOnTime, 0L);
                    editor.commit();
                }
            }
        }
    };
    */

    // Deprecated
    /*
    private String getForegroundAppname(Context context) {
        final String sLocalTag = "getFrontgroundAppname";

        PackageManager pm = getPackageManager();
        ActivityManager manager = (ActivityManager) context .getSystemService(android.content.Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningTaskInfos = manager.getRunningAppProcesses();
        String sAppname = null;
        if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo info : runningTaskInfos) {
                Log.d(sLocalTag, info.processName);
                Log.d(sLocalTag, String.valueOf(info.importance));
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    try {
                        sAppname = pm.getApplicationInfo(info.processName, 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    return sAppname;
                }
            }
        }
        return null;
    }
    */

    public static String getAppnameByProcessName(Context context, String sProcessName) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getApplicationInfo(sProcessName, 0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getForegroundAppname(Context context) {
        String sCurrentApp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    sCurrentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            sCurrentApp = tasks.get(0).processName;
        }

        if (sCurrentApp == null)
            Log.d("getForegroundAppname", "Fail to detect current application.");
        else {
            sCurrentApp = getAppnameByProcessName(context, sCurrentApp);
            Log.d("getForegroundAppname", "Current application in foreground is: " + sCurrentApp);
        }
        return sCurrentApp;
    }

    public static boolean recordForegroundApp(Context context) {
        String sCurrentApp = getForegroundAppname(context);
        if (sCurrentApp == null) {
            Log.d(sTag, "Fail to detect current application.");
            return false;
        }
        return CommonFunction.SaveAppUsage(context, sCurrentApp, new CommonFunction.MyTime(0, 1, 0));
    }

    private boolean bScreenOn = false;

    public static boolean getScreenState(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = dm.getDisplays();
            for (Display display : displays) {
                if (display.getState() == Display.STATE_ON
                        || display.getState() == Display.STATE_UNKNOWN) {
                    return true;
                }
            }
            return false;
        } else { // If you use less than API20:
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (powerManager.isScreenOn()) {
                return true;
            }
            return false;
        }
    }

    public static void BroadcastAppsUsageUpdate(Context context) {
        Intent intent = new Intent(sAppsUsageUpdated);
        context.sendBroadcast(intent);
    }

    //主要功能，广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //保存屏幕启动时的毫秒数
                Log.d(sTag, "Screen ON broadcast received.");
                bScreenOn = true;
                CommonFunction.MyTime beginTime = new CommonFunction.MyTime(Calendar.getInstance());
                CommonFunction.SaveScreenUsage_BeginStatus(context, beginTime);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //保存屏幕总工作时间
                Log.d(sTag, "Screen OFF broadcast received.");
                bScreenOn = false;
                CommonFunction.MyTime endTime = new CommonFunction.MyTime(Calendar.getInstance());
                CommonFunction.MyTime beginTime = CommonFunction.LoadScreenUsage_BeginStatus(context);
                if (endTime.compare(beginTime) > 0)
                    CommonFunction.SaveScreenUsage(context, beginTime, endTime);
                CommonFunction.SaveScreenUsage_BeginStatus(context, endTime);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                Log.d(sTag, "Time-tick broadcast received.");
                if (bScreenOn) {
                    recordForegroundApp(context);
                    CommonFunction.SaveScreenUsage_TotalTime(context, new CommonFunction.MyTime(0, 1, 0));
                    BroadcastAppsUsageUpdate(context);
                } else {
                    Log.d(sTag, "Screen off. Apps Usage not recorded.");
                }
            }
        }
    };

    public void EnsureBeginTimeFileExist() {
        FileInputStream infile = null;
        try {
            infile = new FileInputStream(CommonFunction.getScreenUsageBeginTimeFilename(getApplication()));
            infile.close();
        } catch (FileNotFoundException e) {
            CommonFunction.SaveScreenUsage_BeginStatus(getApplication(), new CommonFunction.MyTime(Calendar.getInstance()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DeleteBeginTimeFile() {
        File beginTimeFile = new File(CommonFunction.getScreenUsageBeginTimeFilename(getApplication()));
        beginTimeFile.delete();
        Log.d(sTag, "BeginTime file has been deleted.");
    }

    @Override
    public void onCreate() {
        Log.d(sTag, "onCreate");

        bScreenOn = getScreenState(getApplication());
        if (bScreenOn)
            EnsureBeginTimeFileExist();
        else
            DeleteBeginTimeFile();

        //添加过滤器并注册
        //receiver.onReceive(this, new Intent().setAction(Intent.ACTION_SCREEN_ON));
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(sTag, "onStartCommand");

        new Thread() {
            public void run() {
                KeepServiceRunning(getApplication(), PushPromotionService.class);
            }
        }.start();

        flags |= START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(sTag, "onDestroy");
        DeleteBeginTimeFile();
        unregisterReceiver(receiver);
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
