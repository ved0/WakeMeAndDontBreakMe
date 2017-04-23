package xs.wakemeanddontbreakme;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
        Log.d("TAG", "BEFORE DB CREATE");
        db = new DatabaseHandler(getApplicationContext());
        Log.d("TAG2", "AFTER DB CREATE");

        lv = (ListView) findViewById(R.id.alarm_list);

        //makes the list clickable
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        TextView tv = (TextView) view.findViewById(R.id.alarmText);
                        String alarm = tv.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                        intent.putExtra("POSITION", Integer.toString(position));
                        //Toast.makeText(getApplicationContext(), "hello " + position + "|"+alarmDetails+"|"+alarm+"|", Toast.LENGTH_SHORT).show();
                        intent.putExtra("ALARM_INFO", alarm);
                        startActivity(intent);
                    }
                }
        );

        //rensa larmlistan
        //db.removeAllAlarms();
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
        startActivity(intent);
    }

    //Called when done adding/editing/removing an alarm
    public void updateInterface() {
        alarms = db.getAllAlarms();
        lv.setAdapter(new RowAdapter(this, alarms, alarms.size()));
    }


    public void getMeBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    public void notImplemented(View view){
//        setContentView(R.layout.activity_need_implementation);
//    }
}
