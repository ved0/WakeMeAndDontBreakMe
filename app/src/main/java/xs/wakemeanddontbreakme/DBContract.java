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
        public static final String TABLE_NAME = "alarms";
        public static final String ALARM_NAME = "name";
        public static final String ALARM_TIME = "time";
        public static final String ALARM_DAYS ="days";
        public static final String ALARM_POSITION = "position";
        public static final String ALARM_VIBRATION = "vibration";
        public static final String ALARM_DIFFICULTY = "difficulty";
    }
}
