package com.donny.appstatistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.donny.appstatistic.dummy.DummyContent;
import com.donny.appstatistic.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppsUsageFragment extends Fragment {

    private final String sTag = "AppsUsageFragment";

    private static final String sAppsUsageUpdated = "com.donny.appstatistic.appsUsageUpdated";

    private GridView usageList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateAppsList();
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(sAppsUsageUpdated)) {
                Log.d(sTag, "Time Tick Boradcast Received.");
                handler.dispatchMessage(new Message());
            }
        }
    };

    private void registerMyReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(sAppsUsageUpdated);
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private List<Map<String, Object>> queryAppsUsage() {
        List<Map<String, Object>> list = new ArrayList<>();
        Cursor cs = CommonFunction.LoadAllAppsUsage(getContext());
        if (cs == null) return list;
        Map<String, Object> map;
        String appname, usage;
        while (cs.moveToNext()) {
            map = new HashMap<>();
            appname = cs.getString(cs.getColumnIndex(CommonFunction.DBAppUsage.COL_APPNAME));
            usage = cs.getString(cs.getColumnIndex(CommonFunction.DBAppUsage.COL_USAGE));
            map.put("Appname", appname);
            map.put("TotalTime", usage);
            list.add(map);
        }
        cs.close();
        return list;
    }

    public void updateAppsList() {
        SimpleAdapter adapter = new SimpleAdapter(getContext(), queryAppsUsage(), R.layout.list_appsusage,
                new String[]{"Appname", "TotalTime"}, new int[]{R.id.id_name, R.id.id_totaltime});
        usageList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(sTag, "AppsList updated.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appsusage, container, false);
        usageList = (GridView) view.findViewById(R.id.id_appsusage_list);
        updateAppsList();
        registerMyReceiver();
        return view;
    }

}
