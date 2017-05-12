package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;


public class AlarmActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    TimePicker alarmTimePicker;
    DatabaseHandler db;
    PendingIntent pendingIntent;
    EditText nameText;
    Button deleteButton;
    Switch vibrationSwitch;
    Spinner difficultySpinner;
    Spinner daysSpinner;

    ArrayList<ToggleButton> toggleButtons;
    int lastPos, currentPos, switchValue, difficulty;
    String savedAlarmName, repeating;
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        db = new DatabaseHandler(this);         //Setup database and layout
        //Setup layout
        nameText = (EditText) findViewById(R.id.getAlarmName);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        vibrationSwitch = (Switch) findViewById(R.id.vibrationSwitch);
        setSwitchListener();
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        daysSpinner = (Spinner) findViewById(R.id.repeatingSpinner);
        initiateSpinner();
        initiateDaySpinner();
        Bundle extras = getIntent().getExtras();         //Determine if instance is an edit or a create
        isEdit = extras.getBoolean("IS_EDIT");
        if (isEdit) {
            fillInPreviousData();
            deleteButton.setVisibility(Button.VISIBLE);
        } else {
            nameText.setText("Alarm " + String.valueOf(db.getLastAlarmPosition() + 1));
            deleteButton.setVisibility(GONE);
        }
    }


    public void onApplyPress(View view) {
        //Use hours and minutes from TimePicker
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        //Get value from fields
        String newAlarmName = nameText.getText().toString();
        String newAlarmTime = getTime();
        String newAlarmDays = "";
        //Get position value of the most recently added alarm
        lastPos = db.getLastAlarmPosition();
        if (isEdit) {           //If user is editing
            if(db.getAlarm(newAlarmName) == "" || savedAlarmName.equals(newAlarmName)){
            String oldName = savedAlarmName;
            currentPos = db.getCurrentAlarmPosition(oldName);
            difficulty = difficultySpinner.getSelectedItemPosition();
            repeating = daysSpinner.getSelectedItem().toString();
            //TODO newAlarmDays
            //Set switch on!
            db.changeSwitchStatus(oldName, 1);
            //Change the alarm in the database
            db.changeRecord(oldName, newAlarmName, newAlarmTime, lastPos + 1, switchValue, difficulty, repeating);
            //Delete old alarm and create new one within alarmManager
            Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            receiverIntent.putExtra("vibration", switchValue);
            receiverIntent.putExtra("difficulty", difficulty);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currentPos, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), lastPos + 1, receiverIntent, 0);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis > System.currentTimeMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                timeInMillis += AlarmManager.INTERVAL_DAY;
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);}
                finish();
            } else {
                    Toast.makeText(this, "Name can not be the same as existing alarm",
                           Toast.LENGTH_LONG).show();
            }
        } else {             //If user is adding new alarm
            //Add to database, check if any conflict
            difficulty = difficultySpinner.getSelectedItemPosition();
            repeating = daysSpinner.getSelectedItem().toString();
            //TODO newAlarmDays
            long result = db.addAlarm(newAlarmName, newAlarmTime, lastPos + 1, switchValue, difficulty, repeating);
            if (result < 0) {
                Toast.makeText(this, "Name can not be the same as existing alarm",
                        Toast.LENGTH_LONG).show();
            } else {
                //Register the alarm it with alarmManager
                Intent receiverIntent = new Intent(this, AlarmReceiver.class);
                receiverIntent.putExtra("vibration", switchValue);
                receiverIntent.putExtra("difficulty", difficulty);
                pendingIntent = PendingIntent.getBroadcast(this, lastPos + 1, receiverIntent, 0);
                long timeInMillis = calendar.getTimeInMillis();
                if (timeInMillis > System.currentTimeMillis()) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    timeInMillis += AlarmManager.INTERVAL_DAY;
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
                finish();
            }
        }
    }

    public void onDeletePress(View view) {
        String alarmInfo = getIntent().getStringExtra("ALARM_INFO");
        String[] alarmDetails = alarmInfo.split("\\r?\\n");
        String alarmName = alarmDetails[0];
        currentPos = db.getCurrentAlarmPosition(alarmName);
        db.removeAlarm(alarmName);
        Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currentPos, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        finish();
    }

    public void onCancelPress(View view) {
        finish();
    }


    private String getTime() {
        String alarmTime;
        if (alarmTimePicker.getMinute() < 10) {
            alarmTime = alarmTimePicker.getHour() + ":0" + alarmTimePicker.getMinute();
        } else {
            alarmTime = alarmTimePicker.getHour() + ":" + alarmTimePicker.getMinute();
        }
        return alarmTime;
    }

    private void fillInPreviousData() {
        String alarmInfo[] = getIntent().getStringExtra("ALARM_INFO").split("\\r?\\n");
        nameText.setText(alarmInfo[0]);
        savedAlarmName = alarmInfo[0];
        String[] timeSplit = alarmInfo[1].split(":");
        alarmTimePicker.setHour(Integer.parseInt(timeSplit[0]));
        alarmTimePicker.setMinute(Integer.parseInt(timeSplit[1]));
        int vib = db.getAlarmVibration(savedAlarmName);
        if (vib == 0) {
            vibrationSwitch.setChecked(false);
        } else {
            vibrationSwitch.setChecked(true);
        }
        difficulty = db.getAlarmDifficulty(savedAlarmName);
        switch (difficulty) {
            case 0:
                difficultySpinner.setSelection(0);
                break;
            case 1:
                difficultySpinner.setSelection(1);
                break;
            case 2:
                difficultySpinner.setSelection(2);
                break;
        }
        repeating = db.getAlarmRepetition(savedAlarmName);
        switch(repeating){
            case "Once":
                daysSpinner.setSelection(0);
                break;
            case "Monday to Friday":
                daysSpinner.setSelection(1);
                break;
            case "Every day":
                daysSpinner.setSelection(2);
                break;
            case "Custom":
                daysSpinner.setSelection(3);
                break;
        }
    }

    private void setSwitchListener() {
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchValue = 1;
                } else {
                    switchValue = 0;
                }
            }
        });
    }

    private void initiateSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.difficulties, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
    }

    private void initiateDaySpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
