package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    private boolean doIchangeThisShit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String position = getIntent().getStringExtra("POSITION");
                        int i = Integer.parseInt(position) + 1;
                        new DatabaseHandler(getApplicationContext()).removeAlarm(i);
                        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(position), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                        // Toast.makeText(AlarmActivity.this, " "+i, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });


        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private ArrayList<ToggleButton> toggledButtons() {
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
        for (ToggleButton tb : temp) {
            if (tb.isChecked()) {
                toggledButtons.add(tb);
            }
        }
        return toggledButtons;
    }


    private String timeIfMinutesLessThanTen(String alarmTime) {
        if (alarmTimePicker.getMinute() < 10) {
            alarmTime = alarmTimePicker.getHour() + ":0" + alarmTimePicker.getMinute();
        } else {
            alarmTime = alarmTimePicker.getHour() + ":" + alarmTimePicker.getMinute();
        }
        return alarmTime;
    }

    public void onApplyPress(View view) {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        //Get selected time from time picker

        Calendar calendar = Calendar.getInstance();
        if (doIchangeThisShit == true) {
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            String position = getIntent().getStringExtra("POSITION");
            int i = Integer.parseInt(position) + 1;
            EditText et = (EditText) findViewById(R.id.getAlarmName);
            String alarmName = et.getText().toString();
            String alarmTime = timeIfMinutesLessThanTen(new String());
            String alarmDay = "";
            for (ToggleButton tb : toggledButtons()) {
                alarmDay += tb.getTextOn() + " ";
            }
            db.editAlarm(i, alarmName, alarmTime, alarmDay);
            doIchangeThisShit = false;
        } else {
            //Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            EditText et = (EditText) findViewById(R.id.getAlarmName);
            String alarmName = et.getText().toString();
            String alarmTime = timeIfMinutesLessThanTen(new String());
            String alarmDay = "";
            for (ToggleButton tb : toggledButtons()) {
                alarmDay += tb.getTextOn() + " ";

            }
            db.addAlarm(alarmName, alarmTime, alarmDay);
        }

        //Create Intent to trigger on alarm
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, db.getAllAlarms().size() - 1, receiverIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        String alarmInfo = getIntent().getStringExtra("ALARM_INFO");
        if (alarmInfo != null) {
            String[] alarmDetails = alarmInfo.split("\\r?\\n");
            String alarmName = alarmDetails[0];
            String alarmTime = alarmDetails[1];
            if (alarmDetails.length == 3) {
                String alarmDates = alarmDetails[2];
            }
            Button deleteButton = (Button) findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            String[] timeSplit = alarmTime.split(":");
            alarmTimePicker.setHour(Integer.parseInt(timeSplit[0]));
            alarmTimePicker.setMinute(Integer.parseInt(timeSplit[1]));
            doIchangeThisShit = true;

            // Add toogle functionality for the days clicked !!
            ToggleButton toggleMon = (ToggleButton) findViewById(R.id.Mon);
            toggleMon.setChecked(true);

        } else {

        }

    }

    public void onCancelPress(View view) {
        finish();
    }

}
