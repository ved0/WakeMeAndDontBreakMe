package xs.wakemeanddontbreakme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DAYS;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DAYS_ID;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_REPEATING;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.DAY_TABLE_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.SWITCH_INFO;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.TABLE_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_NAME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_TIME;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_POSITION;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_VIBRATION;
import static xs.wakemeanddontbreakme.DBContract.DBEntry.ALARM_DIFFICULTY;


/**
 * Created by Vedad on 2017-04-15.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int switchOn = 1;
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "WakeMeDontBreakMeDB";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ALARM_NAME + " TEXT NOT NULL PRIMARY KEY," +
                    ALARM_TIME + " TEXT NOT NULL," +
                    ALARM_POSITION + " INTEGER NOT NULL," +
                    ALARM_VIBRATION + " INTEGER NOT NULL," +
                    ALARM_DIFFICULTY + " INTEGER NOT NULL," +
                    ALARM_REPEATING + " TEXT NOT NULL," +
                    SWITCH_INFO + " INTEGER NOT NULL)";

    private static final String SQL_CREATE_DAY_TABLE = "CREATE TABLE " + DAY_TABLE_NAME + " (" +
            ALARM_NAME + " TEXT NOT NULL PRIMARY KEY," +
            ALARM_DAYS + " TEXT NOT NULL,"+
            ALARM_DAYS_ID + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_DAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void changeAlarmDays(String alarmName, String alarmDays, String daysId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_DAYS, alarmDays);
        values.put(ALARM_DAYS_ID, daysId);
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {alarmName};
        db.update(DAY_TABLE_NAME,values,selection,selectionArgs);
        db.close();
    }

    public void deleteAlarmDays(String alarmName){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {alarmName};
        db.delete(DAY_TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public String getAlarmDaysId(String alarmName){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_DAYS_ID};
        String daysId = "";
        Cursor cursor = db.query(DAY_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            daysId = cursor.getString(cursor.getColumnIndex(ALARM_DAYS_ID));
        }
        db.close();
        return daysId;
    }

    public String getAlarmDays(String alarmName){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_DAYS};
        String alarmDays = "";
        Cursor cursor = db.query(DAY_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            alarmDays = cursor.getString(cursor.getColumnIndex(ALARM_DAYS));
        }
        db.close();
        return alarmDays;
    }

    public long addAlarmDays(String alarmName, String alarmDays, String daysId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_DAYS, alarmDays);
        values.put(ALARM_DAYS_ID, daysId);
        long result = db.insert(DAY_TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public long addAlarm(String alarmName, String alarmTime, int position, int vibration, int difficulty, String repeating)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_POSITION, position);
        values.put(ALARM_VIBRATION, vibration);
        values.put(ALARM_DIFFICULTY, difficulty);
        values.put(ALARM_REPEATING, repeating);
        values.put(SWITCH_INFO, switchOn);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    public void changeSwitchStatus(String alarmName, int status){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SWITCH_INFO, status);
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {alarmName};
        db.update(TABLE_NAME,values,selection,selectionArgs);
        db.close();
    }

    public boolean getSwitchStatus(String alarmName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {SWITCH_INFO};
        int switchStatus = 0;
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            switchStatus = cursor.getInt(cursor.getColumnIndex(SWITCH_INFO));
        }
        db.close();
        if(switchStatus == 0){
            return false;
        } else {
            return true;
        }
    }

    public void removeAlarm(String alarmName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ALARM_NAME + " LIKE ?";
        String[] selectionArgs = {alarmName};
        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void changeRecord(String oldName, String alarmName, String alarmTime, int pos, int vibration, int difficulty, String repeating) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_POSITION, pos);
        values.put(ALARM_VIBRATION, vibration);
        values.put(ALARM_DIFFICULTY, difficulty);
        values.put(ALARM_REPEATING, repeating);
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

    public String getAlarmRepetition(String alarmName){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_REPEATING};
        String repeating = "";
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            repeating = cursor.getString(cursor.getColumnIndex(ALARM_REPEATING));
        }
        db.close();
        return repeating;
    }

    public String getAlarm(String alarmName){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ALARM_NAME + " = ?";
        String[] selectionArgs = {alarmName};
        String[] projection = {ALARM_NAME};
        String alarm = "";
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            alarm = cursor.getString(cursor.getColumnIndex(ALARM_NAME));
        }
        db.close();
        return alarm;
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
                String alarm = cursor.getString(0) + "\n" + cursor.getString(1) + "\n" + cursor.getString(5);
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
