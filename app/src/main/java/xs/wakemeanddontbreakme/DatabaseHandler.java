package xs.wakemeanddontbreakme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DATE;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_ID;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_NAME;
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
                    ALARM_DATE + " TEXT" +
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

    //Protected vs SQL-injections
    public void removeAlarm(int position) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ALARM_ID + " LIKE ?";
        String[] selectionArgs = {Integer.toString(position)};
        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    //Will be used
    public void removeAllAlarms() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    //Will be used
    public String getAlarm(int index) {
        String alarm = new String();
        String selectRowQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ALARM_ID + "=" + index;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectRowQuery, null);
        if (cursor.moveToFirst()) {
            do {
                alarm = cursor.getString(1) + "\n" + cursor.getString(2) + "\n" + cursor.getString(3);
            } while (cursor.moveToNext());
        }
        return alarm;
    }

    //Protected vs SQL-injections
    public void editAlarm(int position, String alarmName, String alarmTime, String alarmDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DATE, alarmDate);

        String selection = ALARM_ID + " LIKE ?";
        String[] selectionArgs = {Integer.toString(position)};
        db.update(TABLE_NAME, values, selection, selectionArgs);
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
