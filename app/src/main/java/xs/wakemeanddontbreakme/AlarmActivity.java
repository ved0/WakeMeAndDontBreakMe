package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Button;
import android.widget.ToggleButton;


import java.util.ArrayList;
import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    //private int[] daysToggled;
    //MainActivity mainActivity;
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
      //  mainActivity = new MainActivity();   behövs detta?
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        //daysToggled = new int[7];
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private ArrayList<ToggleButton> toogledButtons(){
        ArrayList<ToggleButton> temp = new ArrayList<ToggleButton>();
        ArrayList<ToggleButton> toogledButtons = new ArrayList<ToggleButton>();
        ToggleButton toggleMon = (ToggleButton) findViewById(R.id.Mon);
        temp.add(toggleMon);
        ToggleButton toggleTue = (ToggleButton) findViewById(R.id.Tue);
        temp.add(toggleTue);
        ToggleButton toggleWed = (ToggleButton) findViewById(R.id.Wed);
        temp.add(toggleWed);
        ToggleButton toggleThu = (ToggleButton) findViewById(R.id.Thu);
        temp.add(toggleThu);
        ToggleButton toggleFri = (ToggleButton) findViewById(R.id.Fri);
        temp.add(toggleFri);
        ToggleButton toggleSat = (ToggleButton) findViewById(R.id.Sat);
        temp.add(toggleSat);
        ToggleButton toggleSun = (ToggleButton) findViewById(R.id.Sun);
        temp.add(toggleSun);
        for(ToggleButton tb : temp){
            if(tb.isChecked()){
                toogledButtons.add(tb);
            }
        }
        return toogledButtons;
    }

    public void onApplyPress(View view) {
        //Get selected time from time picker
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        int ordning = db.getAllAlarms().size()+1;
        String alarmName = "Alarm "+ ordning;
        String alarmTime = alarmTimePicker.getHour()+":"+alarmTimePicker.getMinute();
        String alarmDay = "";
        for(ToggleButton tb : toogledButtons()){
        alarmDay += tb.getTextOn();
        }
        db.addAlarm(alarmName,alarmTime,alarmDay);
       // MainActivity.rw.notifyDataSetChanged();

        //Create Intent to trigger on alarm
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        finish();
    }

    public void onCancelPress(View view) {
        finish();
    }

}
