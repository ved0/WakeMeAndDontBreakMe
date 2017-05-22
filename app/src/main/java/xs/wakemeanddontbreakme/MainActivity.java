package xs.wakemeanddontbreakme;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ArrayList alarms;
    AlarmManager alarmManager;
    ListView lv;
    DatabaseHandler db;
    Intent previewIntent;
    Button previewButton;

    private static final int PERMISION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DatabaseHandler(getApplicationContext());
        previewButton = (Button) findViewById(R.id.button_preview);
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

    public void onPreviewPress(View view) {
        PopupMenu popup1 = new PopupMenu(this, previewButton);
        popup1.getMenuInflater().inflate(R.menu.preview_popup, popup1.getMenu());
        popup1.show();
        popup1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String task = item.toString();
                previewTask(task);
                return true;
            }
        });
    }

    private void previewTask(String task) {
        switch (task) {
            case "Lock":
                previewIntent = new Intent(getApplicationContext(), LockTask.class);
                break;
            case "Shake":
                previewIntent = new Intent(getApplicationContext(), ShakeTask.class);
                break;
            case "Shout":
                previewIntent = new Intent(getApplicationContext(), ShoutTask.class);
                break;
            case "NFC":
                previewIntent = new Intent(getApplicationContext(), NfcTask.class);
                break;
        }
        PopupMenu popup2 = new PopupMenu(this, previewButton);
        popup2.getMenuInflater().inflate(R.menu.diff_select, popup2.getMenu());
        popup2.show();
        popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String difficulty = item.toString();
                switch (difficulty) {
                    case "Hard":
                        previewIntent.putExtra("difficulty", 0);
                        break;
                    case "Harder":
                        previewIntent.putExtra("difficulty", 1);
                        break;
                    case "NO FEAR":
                        previewIntent.putExtra("difficulty", 2);
                        break;
                }
                previewIntent.putExtra("vibration", 1);
                startActivity(previewIntent);
                previewIntent = null;
                return true;
            }
        });
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
