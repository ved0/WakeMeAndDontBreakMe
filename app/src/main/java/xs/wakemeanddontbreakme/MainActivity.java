package xs.wakemeanddontbreakme;

import android.app.AlarmManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList alarms;
    AlarmManager alarmManager;
    ListView lv;
    DatabaseHandler db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DatabaseHandler(getApplicationContext());

        //rensa larmlistan
        //db.removeAllAlarms();
        alarms = db.getAllAlarms();
        lv = (ListView) findViewById(R.id.alarm_list);
        lv.setAdapter(new RowAdapter(this,alarms,alarms.size()));
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
