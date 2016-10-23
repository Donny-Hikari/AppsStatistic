package com.donny.appstatistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Donny on 8/18/2016.
 */
public class AppStatisticBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start ScreenUsageTrackService
        context.startService(new Intent(context, ScreenUsageTrackService.class));
        context.startService(new Intent(context, PushPromotionService.class));
        context.startService(new Intent(context, DataSyncService.class));
        CommonFunction.SetCoreServicesAlarm(context);
    }

}
