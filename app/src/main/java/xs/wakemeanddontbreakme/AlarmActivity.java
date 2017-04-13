package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.Button;


import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    //private int[] daysToggled;
    MainActivity mainActivity;
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mainActivity = new MainActivity();
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        //daysToggled = new int[7];
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.d("t0", "ONCREATE SUCCESSFUL");
    }

    public void onApplyPress(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        Log.d("t1", String.valueOf(alarmTimePicker.getHour()));
        Log.d("t2", String.valueOf(alarmTimePicker.getMinute()));

        //Create Intent to trigger on alarm
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("t3", "Made it down");
    }

    public void onCancelPress(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
