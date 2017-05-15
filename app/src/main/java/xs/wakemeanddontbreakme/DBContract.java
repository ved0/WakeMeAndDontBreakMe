package xs.wakemeanddontbreakme;

import android.provider.BaseColumns;

/**
 * Created by James on 17/04/2017.
 */

public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {}

    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {
        //AlarmDatabase
        public static final String TABLE_NAME = "alarms";
        public static final String ALARM_NAME = "name";
        public static final String ALARM_TIME = "time";
        public static final String ALARM_POSITION = "position";
        public static final String ALARM_VIBRATION = "vibration";
        public static final String ALARM_DIFFICULTY = "difficulty";
        public static final String ALARM_REPEATING = "repeating";
        public static final String SWITCH_INFO = "switch";
        //AlarmDaysDatabase
        public static final String DAY_TABLE_NAME = "alarmDays";
        public static final String ALARM_DAYS = "days";
        public static final String ALARM_DAYS_ID = "days_id";

    }
}