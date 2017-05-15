package xs.wakemeanddontbreakme;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.id.list;
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
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Custom"))
                {
                    showPopup();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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


    private void showPopup() {
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(AlarmActivity.this);
        builderDialog.setTitle("Choose days");
        int count = days.length;
        boolean[] is_checked = new boolean[count];
        if(isEdit){
            String dayId = db.getAlarmDaysId(savedAlarmName);
            for(int i = 0; i < dayId.length(); i++){
                int temp = Character.getNumericValue(dayId.charAt(i));
                is_checked[temp]= true;
            }
        }
        builderDialog.setMultiChoiceItems(days, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });
        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        String dayId = "";
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);
                            if (checked) {
                                dayId += i;
                                stringBuilder.append(list.getItemAtPosition(i) + " ");
                            }
                        }
                        String alarmDays = stringBuilder.toString();
                        String alarmName = nameText.getText().toString();
                        if(isEdit){
                          //  Toast.makeText(AlarmActivity.this, "Changing with name"+alarmName+"\n to days "+ alarmDays+"\n id "+dayId, Toast.LENGTH_LONG).show();
                            if(db.getAlarmDays(alarmName).equals("")){
                                db.addAlarmDays(alarmName, alarmDays, dayId);
                            } else {
                                db.changeAlarmDays(alarmName, alarmDays, dayId);
                            }
                        } else {
                          // Toast.makeText(AlarmActivity.this, "Adding with name"+alarmName+"\n days "+ alarmDays+"\n id "+dayId, Toast.LENGTH_LONG).show();
                            db.addAlarmDays(alarmName, alarmDays, dayId);
                        }
                    }
                });
        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builderDialog.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ff8c00"));
            }
        });
        dialog.show();
    }

    public void onApplyPress(View view) {
        //Use hours and minutes from TimePicker
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        //Get value from fields
        String newAlarmName = nameText.getText().toString();
        String newAlarmTime = getTime();
        //Get position value of the most recently added alarm
        lastPos = db.getLastAlarmPosition();
        if (isEdit) {           //If user is editing
            if(db.getAlarm(newAlarmName) == "" || savedAlarmName.equals(newAlarmName)){
                String oldName = savedAlarmName;
                currentPos = db.getCurrentAlarmPosition(oldName);
                difficulty = difficultySpinner.getSelectedItemPosition();
                repeating = daysSpinner.getSelectedItem().toString();
                //TODO newAlarmDays (DONE)
                //Set switch on!
                db.changeSwitchStatus(oldName, 1);
                //Change the alarm in the database
                db.changeRecord(oldName, newAlarmName, newAlarmTime, currentPos, switchValue, difficulty, repeating);
                //Delete old alarm and create new one within alarmManager
                Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                receiverIntent.putExtra("vibration", switchValue);
                receiverIntent.putExtra("difficulty", difficulty);
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currentPos, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), currentPos, receiverIntent, 0);
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
            //TODO newAlarmDays (DONE)
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
        if(daysSpinner.getSelectedItem().toString().equals("Custom")){
            db.deleteAlarmDays(alarmName);
        }
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
