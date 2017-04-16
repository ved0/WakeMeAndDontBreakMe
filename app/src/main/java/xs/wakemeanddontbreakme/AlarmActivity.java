package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private ArrayList<ToggleButton> toggledButtons(){
        ArrayList<ToggleButton> temp = new ArrayList<>();
        ArrayList<ToggleButton> toggledButtons = new ArrayList<>();
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
                toggledButtons.add(tb);
            }
        }
        return toggledButtons;
    }

    public void onApplyPress(View view) {
        //Get selected time from time picker
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        int order = db.getAllAlarms().size() + 1;
        String alarmName = "Alarm "+ order;
        String alarmTime;
        if(alarmTimePicker.getMinute()<10){
            alarmTime = alarmTimePicker.getHour()+":0"+alarmTimePicker.getMinute();
        }else{
            alarmTime = alarmTimePicker.getHour()+":"+alarmTimePicker.getMinute();
        }
        String alarmDay = "";
        for(ToggleButton tb : toggledButtons()){
            alarmDay += tb.getTextOn()+" ";
        }

        db.addAlarm(alarmName, alarmTime, alarmDay);

        //Create Intent to trigger on alarm
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
       String alarmInfo = getIntent().getStringExtra("ALARM_INFO");
        if(alarmInfo != null) {
            String[] alarmDetails = alarmInfo.split("\\r?\\n");
            String alarmName = alarmDetails[0];
            String alarmTime = alarmDetails[1];
            if(alarmDetails.length == 3) {
                String alarmDates = alarmDetails[2];
            }
          String[] timeSplit = alarmTime.split(":");
            alarmTimePicker.setHour(Integer.parseInt(timeSplit[0]));
            alarmTimePicker.setMinute(Integer.parseInt(timeSplit[1]));

            //  String position = getIntent().getStringExtra("POSITION");
            //   int i = Integer.parseInt(position) + 1;
         //Toast.makeText(getApplicationContext(), "hello " + position+"|"+temp+"|", Toast.LENGTH_SHORT).show();
        } else {

        }

    }

    public void onCancelPress(View view) {
        finish();
    }

}
