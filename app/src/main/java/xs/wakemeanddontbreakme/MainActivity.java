package xs.wakemeanddontbreakme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;


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

        lv = (ListView) findViewById(R.id.alarm_list);
        //makes the list clickable
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        TextView tv = (TextView) view.findViewById(R.id.alarmText);
                        String alarmInfo = tv.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                        intent.putExtra("ALARM_INFO", alarmInfo);
                        intent.putExtra("IS_EDIT", true);
                        startActivity(intent);
                    }
                }
        );
        updateInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInterface();
    }


    //Called when apply button pressed
    public void addAlarm(View view) {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("ISEDIT", false);
        startActivity(intent);
    }

//    public void removeAllAlarms(View view) {
//        ArrayList<String> allAlarms = db.getAllAlarms();
//        for (String alarmName : allAlarms) {
//            alarmName = alarmName.split("\\r?\\n")[0];
//            int pos = db.getCurrentAlarmPosition(alarmName);
//            Intent receiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), pos, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            alarmManager.cancel(pendingIntent);
//            db.removeAlarm(alarmName);
//            updateInterface();
//        }
//    }

    //Called when done adding/editing/removing an alarm
    public void updateInterface() {
        alarms = db.getAllAlarms();
        lv.setAdapter(new RowAdapter(this, alarms, alarms.size()));
    }

}
