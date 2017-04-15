package xs.wakemeanddontbreakme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by Vedad on 2017-04-15.
 */

public class DatabaseHandler extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "alarmInfo";
    // Contacts table name
    private static final String ALARMS = "alarms";
    // Shops Table Columns names
    private static final String ALARM_ID = "id";
    private static final String ALARM_NAME = "name";
    private static final String ALARM_TIME = "time";
    private static final String ALARM_DAY = "day";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + ALARMS + "("
        + ALARM_ID + " INTEGER PRIMARY KEY," + ALARM_NAME + " TEXT,"
        + ALARM_TIME + " TEXT," + ALARM_DAY +" TEXT)";
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ALARMS);
// Creating tables again
        onCreate(db);
    }

    public void addAlarm(String alarmName, String alarmTime, String alarmDay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmName);
        values.put(ALARM_TIME, alarmTime);
        values.put(ALARM_DAY, alarmDay);
        db.insert(ALARMS, null, values);
        db.close();
    }

    //Will be used
    public void removeAlarm(String alarmName, String alarmTime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ALARMS, ALARM_NAME + " = ? AND "+ ALARM_TIME + " = ?",new String[]{alarmName,alarmTime});
        db.close();
    }

    //Will be used
    public void removeAllAlarms(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ALARMS,null,null);
        db.close();
    }

    //Will be used
    public String getAlarm(String alarmName, String alarmTime){
        String alarm = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ALARMS,new String[]{ALARM_NAME, ALARM_TIME, ALARM_DAY}, ALARM_NAME + "=? AND "+ ALARM_TIME +" = ?", new String[]{alarmName, alarmTime},null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            alarm = " Name "+ cursor.getString(1)+"\n Time "+cursor.getString(2)+"\n Day "+cursor.getString(3);
        }
        return alarm;
    }

    public ArrayList<String> getAllAlarms(){
        ArrayList<String> alarmList = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM "+ALARMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);
        if(cursor.moveToFirst()){
            do{
                String alarm = ""+ cursor.getString(1)+"\n"+cursor.getString(2)+"\n"+cursor.getString(3);
                alarmList.add(alarm);
            }while(cursor.moveToNext());
        }
        db.close();
        return alarmList;
    }

}
