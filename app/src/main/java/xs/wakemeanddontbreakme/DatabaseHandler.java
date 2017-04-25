package xs.wakemeanddontbreakme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static xs.wakemeanddontbreakme.DBContract.DBEntry.TABLE_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_TIME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DAYS;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_POSITION;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_VIBRATION;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DIFFICULTY;


/**
 * Created by Vedad on 2017-04-15.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "WakeMeDontBreakMeDB";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ALARM_NAME + " TEXT NOT NULL PRIMARY KEY," +
                    ALARM_TIME + " TEXT NOT NULL," +
                    ALARM_DAYS + " TEXT NOT NULL," +
                    ALARM_POSITION + " INTEGER NOT NULL," +
                    ALARM_VIBRATION + " INTEGER NOT NULL," +
                    ALARM_DIFFICULTY + " INTEGER NOT NULL" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public long addAlarm(String alarmName, String alarmTime, String alarmDays, int position, int vibration, int difficulty)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DAYS, alarmDays);
        values.put(ALARM_POSITION, position);
        values.put(ALARM_VIBRATION, vibration);
        values.put(ALARM_DIFFICULTY, difficulty);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public void removeAlarm(String alarmName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {alarmName};
        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void changeRecord(String oldName, String alarmName, String alarmTime, String alarmDays, int pos, int vibration, int difficulty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DAYS, alarmDays);
        values.put(ALARM_POSITION, pos);
        values.put(ALARM_VIBRATION, vibration);
        values.put(ALARM_DIFFICULTY, difficulty);
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {oldName};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public int getAlarmVibration(String alarmName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_VIBRATION};
        int vib = 0;
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            vib = cursor.getInt(cursor.getColumnIndex(ALARM_VIBRATION));
        }
        db.close();
        return vib;
    }

    public int getAlarmDifficulty(String alarmName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_DIFFICULTY};
        int dif = 0;
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            dif = cursor.getInt(cursor.getColumnIndex(ALARM_DIFFICULTY));
        }
        db.close();
        return dif;
    }

    public int getLastAlarmPosition() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ALARM_POSITION + " DESC";
        int pos = 0;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            pos = cursor.getInt(cursor.getColumnIndex(ALARM_POSITION));
        }
        db.close();
        return pos;
    }

    public int getCurrentAlarmPosition(String alarmName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_POSITION};
        int pos = 0;

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            pos = cursor.getInt(cursor.getColumnIndex(ALARM_POSITION));
        }
        db.close();
        return pos;
    }

    //Fetches all the alarms
    public ArrayList<String> getAllAlarms() {
        ArrayList<String> alarmList = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String alarm = cursor.getString(0) + "\n" + cursor.getString(1) + "\n" + cursor.getString(2);
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }
        db.close();
        return alarmList;
    }

//    //Will be used
//    public void removeAllAlarms() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(SQL_DELETE_ENTRIES);
//    }


}
