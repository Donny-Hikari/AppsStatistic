package com.donny.appstatistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final String sTag = "MainActivity";
    private final int VERSION = 2; //3
    //private final String sSharedPreferenceName = "myUsageStat";
    //private final String sspScrOnTime = "ScrOnTime";
    //private final String sspScrOnLastTime = "ScrOnLastTime";
    //private final String sspScrPowerStatus = "ScrPowerStatus"; // true for On, false for Off

    //private Button btnAppStat;
    private TextView tvScreenOnTime_hour, tvScreenOnTime_minute;
    private Button btnSurvey;
    //private CommonFunction.MyTime screenOnTotalTime = new CommonFunction.MyTime();
    //private CommonFunction.MyTime screenOnLastTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            reLoadScreenOnTime();
        }
    };
    /*
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            reLoadScreenOnTime();
            handler.postDelayed(this, 1000);
        }
    };
    */

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(sTag, "Screen On Boradcast Received.");
                screenOnTotalTime = CommonFunction.LoadScreenUsage_TotalTime(context);
                screenOnLastTime = new CommonFunction.MyTime(Calendar.getInstance());
                handler.dispatchMessage(new Message());
            } else */
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                Log.d(sTag, "Time Tick Boradcast Received.");
                handler.dispatchMessage(new Message());
            }
        }
    };

    private void registerMyReceiver() {
        final IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (VERSION) {
            case 2:
                setContentView(R.layout.activity_main2);
                break;
            case 3:
                setContentView(R.layout.activity_main3);
                break;
        }

        CommonFunction.requestPermission(MainActivity.this);

        CommonFunction.EnsureServiceRunning(this, PushPromotionService.class);
        CommonFunction.EnsureServiceRunning(this, ScreenUsageTrackService.class);
        CommonFunction.EnsureServiceRunning(this, DataSyncService.class);
        CommonFunction.SetCoreServicesAlarm(this);
        //startService(new Intent(this, ScreenUsageTrackService.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        //startService(new Intent(this, PushPromotionService.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        //startService(new Intent(this, DataSyncService.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        CommonFunction.MyTime beginTime = new CommonFunction.MyTime(Calendar.getInstance());
        Log.d(sTag, beginTime.toString());

        /*
        btnAppStat = (Button) findViewById(R.id.btn_AppStatistic);
        btnAppStat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.UsageStatsActivity"));
                //intent.setClassName("com.android.settings", "UsageStats");
                startActivity(intent);
            }

        });
        */

        tvScreenOnTime_hour = (TextView) findViewById(R.id.id_ScreenOnTime_Hour);
        tvScreenOnTime_minute = (TextView) findViewById(R.id.id_ScreenOnTime_Minute);
        reLoadScreenOnTime();
        registerMyReceiver();
        //screenOnTotalTime = CommonFunction.LoadScreenUsage_TotalTime(getApplication());
        //screenOnLastTime = CommonFunction.LoadScreenUsage_BeginStatus(getApplication());
        //handler.postDelayed(runnable, 1000);

        if (VERSION == 2) {
            btnSurvey = (Button) findViewById(R.id.id_btnToSurvey);
            btnSurvey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SurveyActivity.class));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionmenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return true;
    }

    private void reLoadScreenOnTime() {
        CommonFunction.MyTime screenOnTime = CommonFunction.LoadScreenUsage_TotalTime(getApplication());
        //CommonFunction.MyTime screenOnTime = new CommonFunction.MyTime(Calendar.getInstance());
        //screenOnTime = screenOnTime.sub(screenOnLastTime);
        //screenOnTime = screenOnTime.add(screenOnTotalTime);
        //Log.d(sTag, "Screen On Last Time: " + screenOnLastTime.hour + "h " + screenOnLastTime.min + "m");
        //Log.d(sTag, "Screen On Total Time: " + screenOnTotalTime.hour + "h " + screenOnTotalTime.min + "m");
        Log.d(sTag, "Screen On Time: " + screenOnTime.hour + "h " + screenOnTime.min + "m");
        tvScreenOnTime_hour.setText(String.valueOf(screenOnTime.hour));//最终显示
        tvScreenOnTime_minute.setText(String.valueOf(screenOnTime.min));//最终显示
    }

    /*
    private void reLoadScreenOnTime() {
        SharedPreferences sp = getSharedPreferences(sSharedPreferenceName, Context.MODE_PRIVATE);
        boolean bScrOn = sp.getBoolean(sspScrPowerStatus, true);
        long timeEscape = 0;
        if (bScrOn) {
            long curTime = new Date().getTime();
            timeEscape = curTime - sp.getLong(sspScrOnLastTime, curTime);
        }
        long sum = sp.getLong(sspScrOnTime, 0L) / 1000 + timeEscape / 1000;
        int hour = (int) (sum / 3600);
        int min = (int) ((sum - hour * 3600) / 60);
        int sec = (int) (sum % 60);
        Log.d(sTag, "Updating \"tvScreenOnTime\" with timeEscape=" + timeEscape + "; bScrOn=" + bScrOn);
        tvScreenOnTime.setText(hour + "h " + min + "m " + sec + "s");//最终显示
    }
    */

}
