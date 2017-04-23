package xs.wakemeanddontbreakme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DATE;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DATE_ID;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_ID;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_POSITION;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_TIME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.TABLE_NAME;

/**
 * Created by Vedad on 2017-04-15.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WakeMeDontBreakMeDB";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ALARM_ID + " INTEGER PRIMARY KEY," +
                    ALARM_NAME + " TEXT," +
                    ALARM_TIME + " TEXT," +
                    ALARM_DATE + " TEXT," +
                    ALARM_POSITION + " INTEGER," +
                    ALARM_DATE_ID + " TEXT" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG3", SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void addAlarm(String alarmName, String alarmTime, String alarmDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DATE, alarmDate);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public void removeAlarm(String alarmName, String alarmTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        String[] selectionArgs = {alarmName, alarmTime};
        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void addAlarmPosition(String alarmName, String alarmTime, int position) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_POSITION, position);
        String selection = ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        String[] selectionArgs = {alarmName, alarmTime};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public void addAlarmDateID(String alarmName, String alarmTime, String alarmDateId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_DATE_ID, alarmDateId);
        String selection = ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        String[] selectionArgs = {alarmName, alarmTime};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public String getAlarmDateID(String alarmName, String alarmTime) {
        String selectRowQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        String s = "";
        String[] eii = {alarmName, alarmTime};
        Cursor cursor = db.rawQuery(selectRowQuery, eii);
        if (cursor.moveToFirst()) {
            do {
                s = cursor.getString(5);
            } while (cursor.moveToNext());
        }
        return s;
    }

    //Will be used
    public void removeAllAlarms() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public int getAlarmPosition(String alarmName, String alarmTime) {
        String selectRowQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        int i = 0;
        String[] eii = {alarmName, alarmTime};
        Cursor cursor = db.rawQuery(selectRowQuery, eii);
        if (cursor.moveToFirst()) {
            do {
                i = cursor.getInt(4);
            } while (cursor.moveToNext());
        }
        return i;
    }


    public void changeRecord(String oldName, String oldTime, String alarmName, String alarmTime, String alarmDay) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DATE, alarmDay);
        String selection = ALARM_NAME + " LIKE ? AND " + ALARM_TIME + " LIKE ?";
        String[] selectionArgs = {oldName, oldTime};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }


    public int lastAlarmPosition() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ALARM_POSITION + " DESC";
        int temp = 0;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            temp = cursor.getInt(4) + 1;
        }
        return temp;
    }
    //Fetches all the alarms

    public ArrayList<String> getAllAlarms() {
        ArrayList<String> alarmList = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String alarm = cursor.getString(1) + "\n" + cursor.getString(2) + "\n" + cursor.getString(3);
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }
        db.close();
        return alarmList;
    }

}
