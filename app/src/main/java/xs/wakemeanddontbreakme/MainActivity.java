package xs.wakemeanddontbreakme;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String [] alarms = {"Alarm 1", "Alarm 2", "Alarm 3"};
        ListAdapter la = new RowAdapter(this, alarms);
        ListView lv = (ListView) findViewById(R.id.alarm_list);
        lv.setAdapter(la);
    }

    //Called when button pressed
    public void addAlarm(View view) {
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
    }

    //Called by AlarmActivity when done adding an alarm
    public void updateInterface() {
        alarmManager.getNextAlarmClock();
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }
}
