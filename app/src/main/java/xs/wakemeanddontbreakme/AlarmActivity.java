package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    private boolean amIchanged;
    DatabaseHandler db;
    PendingIntent pendingIntent;
    private boolean doIchangeThisShit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        amIchanged = false;
        db = new DatabaseHandler(this);
        int temp = db.lastAlarmPosition();
        EditText et = (EditText) findViewById(R.id.getAlarmName);
        if (temp == 0) {
            et.setText("Alarm " + 1);
        } else {
            et.setText("Alarm " + (temp));
        }
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String alarmInfo = getIntent().getStringExtra("ALARM_INFO");
                        String[] alarmDetails = alarmInfo.split("\\r?\\n");
                        String alarmName = alarmDetails[0];
                        String alarmTime = alarmDetails[1];
                        int i = db.getAlarmPosition(alarmName, alarmTime);
                        db.removeAlarm(alarmName, alarmTime);
                        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
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

    private ArrayList<Integer> toggleOrderId() {
        int i = 1;
        ArrayList<ToggleButton> temp = new ArrayList<>();
        ArrayList<Integer> toggledButtons = new ArrayList<>();
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
                toggledButtons.add(i);
            }
            i++;
        }
        return toggledButtons;
    }

    private void toggleButtons(int i) {
        switch (i) {
            case 1:
                ToggleButton toggleMon = (ToggleButton) findViewById(R.id.Mon);
                toggleMon.setChecked(true);
                break;
            case 2:
                ToggleButton toggleTue = (ToggleButton) findViewById(R.id.Tue);
                toggleTue.setChecked(true);
                break;
            case 3:
                ToggleButton toggleWed = (ToggleButton) findViewById(R.id.Wed);
                toggleWed.setChecked(true);
                break;
            case 4:
                ToggleButton toggleThu = (ToggleButton) findViewById(R.id.Thu);
                toggleThu.setChecked(true);
                break;
            case 5:
                ToggleButton toggleFri = (ToggleButton) findViewById(R.id.Fri);
                toggleFri.setChecked(true);
                break;
            case 6:
                ToggleButton toggleSat = (ToggleButton) findViewById(R.id.Sat);
                toggleSat.setChecked(true);
                break;
            case 7:
                ToggleButton toggleSun = (ToggleButton) findViewById(R.id.Sun);
                toggleSun.setChecked(true);
                break;
        }
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
        //Get selected time from time picker
        String alarmName;
        String alarmTime;
        Calendar calendar = Calendar.getInstance();
        if (doIchangeThisShit == true) {
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            EditText et = (EditText) findViewById(R.id.getAlarmName);
            alarmName = et.getText().toString();
            alarmTime = timeIfMinutesLessThanTen(new String());
            String alarmDay = "";
            for (ToggleButton tb : toggledButtons()) {
                alarmDay += tb.getTextOn() + " ";
            }
            String alarmInfo = getIntent().getStringExtra("ALARM_INFO");
            String[] alarmDetails = alarmInfo.split("\\r?\\n");
            String oldName = alarmDetails[0];
            String oldTime = alarmDetails[1];
            db.changeRecord(oldName, oldTime, alarmName, alarmTime, alarmDay);
            if(oldName==alarmName && oldTime == alarmTime){
                amIchanged = false;
            }else{
                amIchanged = true;
            }
            String alarmId = "";
            for (int i : toggleOrderId()) {
                alarmId += i + ":";
            }
            db.addAlarmDateID(alarmName, alarmTime, alarmId);
            doIchangeThisShit = false;
        } else {
            //Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            EditText et = (EditText) findViewById(R.id.getAlarmName);
            alarmName = et.getText().toString();
            alarmTime = timeIfMinutesLessThanTen(new String());
            String alarmDay = "";
            for (ToggleButton tb : toggledButtons()) {
                alarmDay += tb.getTextOn() + " ";
            }
            db.addAlarm(alarmName, alarmTime, alarmDay);
            //kommer ihåg vilka dagar man ställt in
            String alarmId = "";
            for (int i : toggleOrderId()) {
                alarmId += i + ":";
            }
            db.addAlarmDateID(alarmName, alarmTime, alarmId);
        }
        if (amIchanged) {
            int i = db.lastAlarmPosition();
            //lägger till referensen för att kunna stänga av rätt larm i alarmManagern
            db.addAlarmPosition(alarmName, alarmTime, i);
            // Toast.makeText(this, "|" + i + "|", Toast.LENGTH_SHORT).show();
            //Create Intent to trigger on alarm
            Intent receiverIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, i, receiverIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
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

            EditText et = (EditText) findViewById(R.id.getAlarmName);
            et.setText(alarmName);

            // Add toogle functionality for the days clicked !!
            String temp = db.getAlarmDateID(alarmName, alarmTime);
            String[] alarmIds = temp.split(":");
            for (int i = 0; i < alarmIds.length; i++) {
                if (alarmIds[i] != "") {
                    toggleButtons(Integer.parseInt(alarmIds[i]));
                }
            }

            doIchangeThisShit = true;

        } else {

        }

    }


    public void onCancelPress(View view) {
        finish();
    }

}
