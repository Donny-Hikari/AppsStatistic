package com.donny.appstatistic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Donny on 8/24/2016.
 */
class CommonFunction {

    public static String GetDeviceIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String GetDeviceSerialNumber() {
        return Build.SERIAL;
    }

    /* ---- Time&Date: class ---- */

    public static class MyTime {
        public int hour = 0;
        public int min = 0;
        public int sec = 0;

        public MyTime() {
        }

        public MyTime(int _hour, int _min, int _sec) {
            Set(_hour, _min, _sec);
        }

        public MyTime(Calendar calendar) {
            Set(calendar);
        }

        public MyTime(final MyTime _myTime) {
            Set(_myTime);
        }

        public void Set(int _hour, int _min, int _sec) {
            hour = _hour;
            min = _min;
            sec = _sec;
        }

        public void Set(Calendar calendar) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
        }

        public void Set(final MyTime _myTime) {
            hour = _myTime.hour;
            min = _myTime.min;
            sec = _myTime.sec;
        }

        /*
        - return negative if obj2 is greater than this.
        - return positive if obj2 is smaller than this.
        */
        public int compare(MyTime obj2) {
            if (obj2.hour > hour) return -1;
            else if (obj2.hour < hour) return 1;
            if (obj2.min > min) return -1;
            else if (obj2.min < min) return 1;
            if (obj2.sec > sec) return -1;
            else if (obj2.sec < sec) return 1;
            return 0;
        }

        public MyTime sub(MyTime obj2) {
            MyTime tmp = new MyTime(this);
            if (obj2.sec > tmp.sec) {
                --tmp.min;
                tmp.sec += 60;
            }
            if (obj2.min > tmp.min) {
                --tmp.hour;
                tmp.min += 60;
            }
            if (obj2.hour > tmp.hour) tmp.hour += 24;
            return new MyTime(tmp.hour - obj2.hour, tmp.min - obj2.min, tmp.sec - obj2.sec);
        }

        public MyTime add(MyTime obj2) {
            MyTime tmp = new MyTime(this);
            tmp.sec += obj2.sec;
            while (tmp.sec >= 60) {
                tmp.sec -= 60;
                tmp.min += 1;
            }
            tmp.min += obj2.min;
            while (tmp.min >= 60) {
                tmp.min -= 60;
                tmp.hour += 1;
            }
            tmp.hour += obj2.hour;
            while (tmp.hour >= 24)
                tmp.hour -= 24;
            return tmp;
        }

        @Override
        public String toString() {
            return Time_StructToString(this);
        }

        public void parseString(String strTime) {
            MyTime _myTime = Time_StringToStruct(strTime);
            hour = _myTime.hour;
            min = _myTime.min;
            sec = _myTime.sec;
        }

    }

    public static String Time_StructToString(MyTime myTime) {
        return String.format("%02d:%02d:%02d", myTime.hour, myTime.min, myTime.sec);
    }

    public static MyTime Time_StringToStruct(String strTime) {
        String[] results = strTime.split(":");
        try {
            return new MyTime(Integer.parseInt(results[0]), Integer.parseInt(results[1]), Integer.parseInt(results[2]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new MyTime(Calendar.getInstance());
        }
    }

    public static class MyDate {
        public int year = 0;
        public int month = 0;
        public int day = 0;

        public MyDate() {
        }

        public MyDate(int _year, int _month, int _day) {
            Set(_year, _month, _day);
        }

        public MyDate(Calendar calendar) {
            Set(calendar);
        }

        public MyDate(final MyDate _date) {
            Set(_date);
        }

        public void Set(int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
        }

        public void Set(Calendar calendar) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public void Set(final MyDate _date) {
            year = _date.year;
            month = _date.month;
            day = _date.day;
        }

        /*
        - return negative if obj2 is greater than this.
        - return positive if obj2 is smaller than this.
        */
        public int compare(MyDate obj2) {
            if (obj2.year > year) return -1;
            else if (obj2.year < year) return 1;
            if (obj2.month > month) return -1;
            else if (obj2.month < month) return 1;
            if (obj2.day > day) return -1;
            else if (obj2.day < day) return 1;
            return 0;
        }

        // focus two digitals
        public String yearInStr() {
            return String.format("%02d", year);
        }

        // focus two digitals
        public String monthInStr() {
            return String.format("%02d", month);
        }

        // focus two digitals
        public String dayInStr() {
            return String.format("%02d", day);
        }

        // return 20160826
        public String getDateWithoutSymbol() {
            return String.format("%02d%02d%02d", year, month, day);
        }

        @Override
        // return 2016-08-26
        public String toString() {
            return String.format("%02d-%02d-%02d", year, month, day);
        }

        public static MyDate parseDate(String sDateWithSymbol) {
            String[] results = sDateWithSymbol.split("-");
            try {
                return new MyDate(Integer.parseInt(results[0]), Integer.parseInt(results[1]), Integer.parseInt(results[2]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new MyDate(Calendar.getInstance());
        }

        public static MyDate parseDateWithoutSymbol(String sDateWithoutSymbol) {
            try {
                String sYear = sDateWithoutSymbol.substring(0, 2);
                String sMonth = sDateWithoutSymbol.substring(2, 4);
                String sDay = sDateWithoutSymbol.substring(4, 6);
                return new MyDate(Integer.parseInt(sYear), Integer.parseInt(sMonth), Integer.parseInt(sDay));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new MyDate(Calendar.getInstance());
        }

    }

    /*
    * Common Functions for this application.
    */

    /* ---- UploadFile ---- */

    public static boolean UploadFile(String uploadUrl, String userinfo, String srcFilename) {
        final String sLoacalTag = "UploadFile";
        Log.d(sLoacalTag, "Uploading file " + srcFilename);

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "************";
        //String boundary = "******_BOUNDARY_******";
        try {
            // Establish http connection
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            httpURLConnection.connect();

            DataOutputStream outdata = new DataOutputStream(httpURLConnection.getOutputStream());

            // Transmit user info
            outdata.writeBytes(twoHyphens + boundary + end);
            outdata.writeBytes("Content-Disposition: form-data; name=\"userinfo\"; filename=\""
                    + userinfo + "\"; " + end);
            outdata.writeBytes(end);

            outdata.writeBytes(end);

            // Transmit file
            outdata.writeBytes(twoHyphens + boundary + end);
            outdata.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + srcFilename.substring(srcFilename.lastIndexOf("/") + 1) + "\"; " + end);
            outdata.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcFilename);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                outdata.write(buffer, 0, count);
            }
            fis.close();

            outdata.writeBytes(end);

            outdata.writeBytes(twoHyphens + boundary + twoHyphens + end);
            outdata.flush();
            outdata.close();

            // Obtain reply
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            String detail = br.readLine();
            is.close();

            httpURLConnection.disconnect();

            Log.d(sLoacalTag, result);
            Log.d(sLoacalTag, detail);

            if (result.equals("Succeed"))
                return true;
            else // result == "Fail"
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.print(e.getMessage());
            return false;
        }
    }

    /* ---- DataPath: path in string ---- */

    private static final String sDataPath = "/Data/Usages/";

    public static String getDataPath(Context context) {
        String sFullDataPath = context.getFilesDir() + sDataPath;
        File destDir = new File(sFullDataPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return sFullDataPath;
    }

    public static String getDataPathToday(Context context) {
        MyDate myDate = new MyDate(Calendar.getInstance());
        String sDataPathToday = getDataPath(context) +
                myDate.yearInStr() + '/' + myDate.monthInStr() + '/' + myDate.dayInStr() + '/';
        File destDir = new File(sDataPathToday);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return sDataPathToday;
    }

    private static final String sScreenUsageBeginTimeFilename = "BeginTime.tmp";
    private static final String sScreenUsageFilename = "ScreenUsage.csv";
    private static final String sScreenUsageAppname = "Screen";
    //private static String sAppsUsageFilename = "AppsUsage.csv";

    public static String getScreenUsageAppname() {
        return sScreenUsageAppname;
    }

    /* ---- AppsUsage: Database ---- */

    public static String getDatabasePath(Context context) {
        return getDataPath(context);
    }

    public static String getAppsUsageTableName(MyDate myDate) {
        return "AppUsage_" + myDate.getDateWithoutSymbol();
    }

    public static String getAppsUsageTableNameToday() {
        MyDate myDate = new MyDate(Calendar.getInstance());
        return "AppUsage_" + myDate.getDateWithoutSymbol();
    }

    public static class DBAppUsage extends SQLiteOpenHelper {
        public static final String DB_NAME = "AppsUsage.db";
        private static final int DB_VERSION = 1;
        //public static final String TBL_NAME = "AppsUsageTable";
        public static final String COL_APPNAME = "Appname";
        public static final String COL_USAGE = "Usage";

        public DBAppUsage(Context context) {
            super(context, getDatabasePath(context) + DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        public void createTable(SQLiteDatabase db, String tableName) {
            final String CREATE_TBL = "create table if not exists " + tableName
                    + "(id integer primary key autoincrement," + COL_APPNAME + " text unique," + COL_USAGE + " text)";
            db.execSQL(CREATE_TBL);
        }

        public void insert(String tableName, ContentValues values) {
            SQLiteDatabase db = getWritableDatabase();
            db.insert(tableName, null, values);
            db.close();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public static boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public static boolean SaveScreenUsage_TotalTime(Context context, MyTime pass) {
        Log.d("Database", "SaveScreenUsage_TotalTime.");
        return SaveAppUsage(context, sScreenUsageAppname, pass);
    }

    public static MyTime LoadScreenUsage_TotalTime(Context context) {
        Log.d("Database", "LoadScreenUsage_TotalTime.");
        return LoadAppUsage(context, sScreenUsageAppname);
    }

    public static boolean SaveAppUsage(Context context, String sAppname, MyTime pass) {
        Log.d("Database", "SaveAppUsage of " + sAppname + ".");

        final String TABLENAME = getAppsUsageTableNameToday();
        DBAppUsage dbFile;
        SQLiteDatabase db;
        try {
            dbFile = new DBAppUsage(context);
            db = dbFile.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!isTableExists(db, TABLENAME)) {
            Log.d("Database", "createTable.");
            dbFile.createTable(db, TABLENAME);
        }
        Cursor cs = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE " + DBAppUsage.COL_APPNAME + "=?", new String[]{sAppname});
        //Cursor cs = db.query(DBAppUsage.TBL_NAME, null, "Appname=?", new String[]{sAppname}, null, null, null);
        Log.d("Database", sAppname + ": " + "Query result: count=" + String.valueOf(cs.getCount()));

        MyTime total;
        if (cs.getCount() == 1) { // cs.getCount() == 1
            cs.moveToNext();
            String ognTime = cs.getString(cs.getColumnIndex(DBAppUsage.COL_USAGE));
            Log.d("Database", sAppname + ": " + "Load originalTime as " + ognTime);
            total = Time_StringToStruct(ognTime).add(pass);
        } else { // cs.getCount() == 0
            total = pass;
        }

        ContentValues value = new ContentValues();
        value.put(DBAppUsage.COL_APPNAME, sAppname);
        value.put(DBAppUsage.COL_USAGE, Time_StructToString(total));

        if (cs.getCount() == 1) { // cs.getCount() == 1
            db.update(TABLENAME, value, "Appname=?", new String[]{sAppname});
            Log.d("Database", sAppname + ": " + "Usage Updated with value=" + Time_StructToString(total) + ".");
        } else { // cs.getCount() == 0
            db.insert(TABLENAME, null, value);
            Log.d("Database", sAppname + ": " + "Usage Created with value=" + Time_StructToString(total) + ".");
        }

        cs.close();
        db.close();
        dbFile.close();
        return true;
    }

    public static MyTime LoadAppUsage(Context context, String sAppname) {
        Log.d("Database", "LoadAppUsage of " + sAppname + ".");

        final String TABLENAME = getAppsUsageTableNameToday();
        MyTime total = new MyTime(0, 0, 0);
        DBAppUsage dbFile;
        SQLiteDatabase db;
        try {
            dbFile = new DBAppUsage(context);
            db = dbFile.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return total;
        }
        if (!isTableExists(db, TABLENAME)) {
            db.close();
            dbFile.close();
            return total;
        }
        Cursor cs = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE " + DBAppUsage.COL_APPNAME + "=?", new String[]{sAppname});
        //Cursor cs = db.query(DBAppUsage.TBL_NAME, null, "Appname=?", new String[]{sAppname}, null, null, null);
        Log.d("Database", sAppname + ": " + "Query result: count=" + String.valueOf(cs.getCount()));

        if (cs.getCount() == 1) { // cs.getCount() == 1
            cs.moveToNext();
            String ognTime = cs.getString(cs.getColumnIndex(DBAppUsage.COL_USAGE));
            total = Time_StringToStruct(ognTime);
        } else { // cs.getCount() == 0
            // total = new MyTime(0, 0, 0);
        }

        cs.close();
        db.close();
        dbFile.close();
        Log.d("Database", sAppname + ": " + "Load totalTime as " + Time_StructToString(total));
        return total;
    }

    public static Cursor LoadAllAppsUsage(Context context) {
        Log.d("Database", "LoadAllAppsUsage.");

        final String TABLENAME = getAppsUsageTableNameToday();
        DBAppUsage dbFile;
        SQLiteDatabase db;
        try {
            dbFile = new DBAppUsage(context);
            db = dbFile.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (!isTableExists(db, TABLENAME)) {
            db.close();
            dbFile.close();
            return null;
        }
        Cursor cs = db.rawQuery("SELECT * FROM " + TABLENAME, null);
        //Cursor cs = db.query(DBAppUsage.TBL_NAME, null, "Appname=?", new String[]{sAppname}, null, null, null);
        Log.d("Database", "AllApps: " + "Query result: count=" + String.valueOf(cs.getCount()));
        db.close();
        dbFile.close();
        return cs;
    }

    /* ---- ScreenUsage: csv file ---- */

    // Won't create folder if not exist.
    public static String getScreenUsageFilename(Context context, MyDate myDate) {
        String sScreenUsagePathToday = getDataPath(context) + myDate.yearInStr() + '/' + myDate.monthInStr() + '/';
        return sScreenUsagePathToday + "ScreenUsage_" + myDate.getDateWithoutSymbol() + ".csv";
    }

    // Will create folder if not exist.
    public static String getScreenUsageFilenameToday(Context context) {
        MyDate myDate = new MyDate(Calendar.getInstance());
        String sScreenUsagePathToday = getDataPath(context) + myDate.yearInStr() + '/' + myDate.monthInStr() + '/';
        File destDir = new File(sScreenUsagePathToday);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return sScreenUsagePathToday + "ScreenUsage_" + myDate.getDateWithoutSymbol() + ".csv";
    }

    // Will leave out if end is two day after begin.
    public static boolean SaveScreenUsage(Context context, MyTime begin, MyTime end) {
        MyTime pass = end.sub(begin);
        String beginTime = Time_StructToString(begin);
        String endTime = Time_StructToString(end);
        String passTime = Time_StructToString(pass);

        FileOutputStream outfile = null;
        DataOutputStream outdata = null;
        try {
            outfile = new FileOutputStream(getScreenUsageFilenameToday(context), true); // append mode
            outdata = new DataOutputStream(outfile);
            outdata.writeChars("\r\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                outfile = new FileOutputStream(getScreenUsageFilenameToday(context)); // create mode
                outdata = new DataOutputStream(outfile);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (outfile != null) outfile.close();
                if (outdata != null) outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        try {
            outdata.writeChars(beginTime);
            outdata.writeChar(',');
            outdata.writeChars(endTime);
            outdata.writeChar(',');
            outdata.writeChars(passTime);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outfile.close();
                outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        try {
            outfile.close();
            outdata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return true;
        //return SaveScreenUsage_TotalTime(context, pass);
    }

    /* ---- ScreenUsage BeginTime: tmp file ---- */

    public static String getScreenUsageBeginTimeFilename(Context context) {
        return getDataPath(context) + sScreenUsageBeginTimeFilename;
    }

    public static String readChars(DataInputStream indata, int count) throws IOException {
        if (indata == null) throw new IOException("indata == null");
        char[] str = new char[count];
        for (int i = 0; i < count; ++i) {
            str[i] = indata.readChar();
        }
        return String.valueOf(str);
    }

    //public static final String sScreenUsageRecordFinishReceiver = "com.donny.AppStatistic.ScreenUsageRecordFinish";

    private static boolean SaveScreenUsage_BeginTime(Context context, MyTime begin) {
        String beginTime = Time_StructToString(begin);
        Log.d("SaveBeginTime", "SaveScreenUsage_BeginTime with beginTime=" + beginTime);

        FileOutputStream outfile = null;
        DataOutputStream outdata = null;
        try {
            outfile = new FileOutputStream(getScreenUsageBeginTimeFilename(context)); // create mode
            outdata = new DataOutputStream(outfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (outfile != null) outfile.close();
                if (outdata != null) outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        try {
            outdata.writeChars(beginTime);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outfile.close();
                outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        try {
            outfile.close();
            outdata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return true;
    }

    private static MyTime LoadScreenUsage_BeginTime(Context context) {
        Log.d("LoadBeginTime", "LoadScreenUsage_BeginTime.");
        String beginTime;

        FileInputStream infile = null;
        DataInputStream indata = null;
        try {
            infile = new FileInputStream(getScreenUsageBeginTimeFilename(context)); // open mode
            indata = new DataInputStream(infile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new MyTime(Calendar.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (infile != null) infile.close();
                if (indata != null) indata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new MyTime(Calendar.getInstance());
        }

        try {
            beginTime = readChars(indata, 8);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                infile.close();
                indata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new MyTime(Calendar.getInstance());
        }
        try {
            infile.close();
            indata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Log.d("LoadBeginTime", "LoadScreenUsage_BeginTime with beginTime=" + beginTime);
        return Time_StringToStruct(beginTime);
    }

    private static boolean SaveScreenUsage_BeginDate(Context context, MyDate begin) {
        String beginDate = begin.toString();
        Log.d("SaveBeginDate", "SaveScreenUsage_BeginDate with beginDate=" + beginDate);

        FileOutputStream outfile = null;
        DataOutputStream outdata = null;
        try {
            outfile = new FileOutputStream(getScreenUsageBeginTimeFilename(context), true); // append mode
            outdata = new DataOutputStream(outfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (outfile != null) outfile.close();
                if (outdata != null) outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        try {
            outdata.writeChar(' ');
            outdata.writeChars(beginDate);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outfile.close();
                outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        try {
            outfile.close();
            outdata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return true;
    }

    private static MyDate LoadScreenUsage_BeginDate(Context context) {
        Log.d("LoadBeginDate", "LoadScreenUsage_BeginDate.");
        String beginDate;

        FileInputStream infile = null;
        DataInputStream indata = null;
        try {
            infile = new FileInputStream(getScreenUsageBeginTimeFilename(context)); // open mode
            indata = new DataInputStream(infile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new MyDate(Calendar.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (infile != null) infile.close();
                if (indata != null) indata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new MyDate(Calendar.getInstance());
        }

        try {
            indata.skip((8 + 1) * 2);
            beginDate = readChars(indata, 10);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                infile.close();
                indata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new MyDate(Calendar.getInstance());
        }
        try {
            infile.close();
            indata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Log.d("LoadBeginDate", "LoadScreenUsage_BeginDate with beginDate=" + beginDate);
        return MyDate.parseDate(beginDate);
    }

    public static boolean SaveScreenUsage_BeginStatus(Context context, MyTime time) {
        if (!SaveScreenUsage_BeginTime(context, time)) return false;
        if (!SaveScreenUsage_BeginDate(context, new MyDate(Calendar.getInstance()))) return false;
        return true;
    }

    public static MyTime LoadScreenUsage_BeginStatus(Context context) {
        MyTime time = LoadScreenUsage_BeginTime(context);
        MyDate date = LoadScreenUsage_BeginDate(context);
        if (date.compare(new MyDate(Calendar.getInstance())) == 0)
            return time;
        else
            return new MyTime(Calendar.getInstance());
    }

    /* ---- AppsUsage: csv file ---- */

    public static String getAppsUsageFilename(Context context, MyDate myDate) {
        String sScreenUsagePathToday = getDataPath(context) + myDate.yearInStr() + '/' + myDate.monthInStr() + '/';
        return sScreenUsagePathToday + "AppsUsage_" + myDate.getDateWithoutSymbol() + ".csv";
    }

    public static boolean ExportAppsUsageToCSVFile(Context context, MyDate _date, String outputFilename) {
        Log.d("Database", "ExtractAppsUsageToCSVFile.");

        final String TABLENAME = getAppsUsageTableName(_date);
        MyTime total = new MyTime(0, 0, 0);
        DBAppUsage dbFile;
        SQLiteDatabase db;
        try {
            dbFile = new DBAppUsage(context);
            db = dbFile.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!isTableExists(db, TABLENAME)) {
            db.close();
            dbFile.close();
            return false;
        }
        Cursor cs = db.rawQuery("SELECT * FROM " + TABLENAME, null);
        Log.d("Database", "Query result: count=" + String.valueOf(cs.getCount()));

        FileOutputStream outFile;
        DataOutputStream outData;
        try {
            outFile = new FileOutputStream(outputFilename);
            outData = new DataOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cs.close();
            db.close();
            dbFile.close();
            return false;
        }

        // Write Headers
        try {
            outData.writeShort(0xFEFF);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sAppname;
        String sUsageTime;
        while (cs.moveToNext()) {
            sAppname = cs.getString(cs.getColumnIndex(DBAppUsage.COL_APPNAME));
            sUsageTime = cs.getString(cs.getColumnIndex(DBAppUsage.COL_USAGE));

            try {
                outData.writeChars(sAppname);
                outData.writeChars(",");
                outData.writeChars(sUsageTime);
                outData.writeChars("\r\n");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    outData.close();
                    outFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                cs.close();
                db.close();
                dbFile.close();
                return false;
            }

        }

        try {
            outData.close();
            outFile.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            cs.close();
            db.close();
            dbFile.close();
            return false;
        }

        cs.close();
        db.close();
        dbFile.close();
        return true;
    }

    /* ---- DataSync: uploaded file ---- */

    public static String getUploadedFilename(Context context, MyDate myDate) {
        String sScreenUsagePathToday = getDataPath(context) + myDate.yearInStr() + '/' + myDate.monthInStr() + '/';
        return sScreenUsagePathToday + "Usage_" + myDate.getDateWithoutSymbol() + ".uploaded";
    }

    public static boolean isDataUploaded(Context context, MyDate myDate) {
        String sFilename = getUploadedFilename(context, myDate);
        FileInputStream infile = null;
        try {
            infile = new FileInputStream(sFilename); // open mode
            infile.close();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            Log.d("isDataUploaded", "Not uploaded. Date: " + myDate.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("isDataUploaded", "Uploaded. Date: " + myDate.toString());
        return true;
    }

    public static boolean setDataUploadStatus_Uploaded(Context context, MyDate myDate) {
        FileOutputStream outfile = null;
        try {
            outfile = new FileOutputStream(getUploadedFilename(context, myDate)); // create mode
            outfile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    * User info.
    */

    private static final String sUserPath = "/Data/User/";
    private static final String sUserInfoFile = "UserID.id";
    private static final String sTagUserInfo = "UserInfo";

    public static String getUserPath(Context context) {
        String sFullDataPath = context.getFilesDir() + sUserPath;
        File destDir = new File(sFullDataPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return sFullDataPath;
    }

    public static String getUserInfoFile(Context context) {
        return getUserPath(context) + sUserInfoFile;
    }

    public static boolean IsUserAlreadyLogin(Context context) {
        File userfile = new File(getUserInfoFile(context));
        if (userfile.exists()) return true;
        return false;
    }

    public static boolean setUserInfo(Context context, String sID) {
        FileOutputStream outfile = null;
        DataOutputStream outdata = null;
        try {
            outfile = new FileOutputStream(getUserInfoFile(context)); // create mode
            outdata = new DataOutputStream(outfile); // create mode

            outdata.writeUTF(sID);

            outdata.close();
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Log.d(sTagUserInfo, "ID set as " + sID);
        return true;
    }

    public static String getUserInfo(Context context) {
        String sID;
        FileInputStream infile = null;
        DataInputStream indata = null;
        try {
            infile = new FileInputStream(getUserInfoFile(context)); // create mode
            indata = new DataInputStream(infile); // create mode

            sID = indata.readUTF();

            indata.close();
            infile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Log.d(sTagUserInfo, "ID loaded as " + sID);
        return sID;
    }


    /*
    * Survey Activity Interface for Fragment
    */

    public abstract interface FragmentListener {

        void movetoFirstQuestion();

        void movetoNextQuestion(int selectedIndex);

        String getFirstQuestion();

    }

    /*
    * Survey - Questionnaire files.
    */

    private static final String sSurveyPath = "/Data/Survey/";

    public static String getSurveyPath(Context context) {
        String sFullDataPath = context.getFilesDir() + sSurveyPath;
        File destDir = new File(sFullDataPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return sFullDataPath;
    }

    public static String getSurveyFilename(Context context, MyDate myDate) {
        return getSurveyPath(context) + "Survey_" + myDate.getDateWithoutSymbol() + ".csv";
    }

    public static String getSurveyFilenameToday(Context context) {
        MyDate myDate = new MyDate(Calendar.getInstance());
        return getSurveyFilename(context, myDate);
    }

    public static boolean SaveSurveyData(Context context, int[] selections) {
        FileOutputStream outfile = null;
        DataOutputStream outdata = null;

        try {
            outfile = new FileOutputStream(getSurveyFilenameToday(context)); // create mode
            outdata = new DataOutputStream(outfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (outfile != null) outfile.close();
                if (outdata != null) outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        try {
            for (int i = 0; i < selections.length; ++i) {
                outdata.writeChars(String.format("Q%d,%d\r\n", i + 1, selections[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outfile.close();
                outdata.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        try {
            outfile.close();
            outdata.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return true;
    }

}
