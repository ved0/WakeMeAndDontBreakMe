package xs.wakemeanddontbreakme;

/**
 * Created by Vedad on 2017-04-14.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class RowAdapter extends BaseAdapter {
    Context context;
    ArrayList aL;
    DatabaseHandler db;
    Switch sw;
    int limit;
    private boolean isChecked;
    private LayoutInflater inflater = null;
    private static final int switchOff = 0;
    private static final int switchOn = 1;


    public RowAdapter(Context context, ArrayList aL, int size) {
        this.context = context;
        this.aL = aL;
        this.limit = size;
        DatabaseHandler db = new DatabaseHandler(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return limit;
    }

    @Override
    public Object getItem(int position) {
        return this.aL.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.alarm_row, null);
        }
        db = new DatabaseHandler(context);
        for (String s : db.getAllAlarms()) {
            final TextView alarmText = (TextView) vi.findViewById(R.id.alarmText);
            final String [] alarmInfo = db.getAllAlarms().get(position).split("\\r?\\n");
            if(alarmInfo[2].equals("Custom")){
                String customDays = db.getAlarmDays(alarmInfo[0]);
                alarmText.setText(alarmInfo[0]+"\n" +alarmInfo[1]+"\n"+customDays);
            } else {
                alarmText.setText(db.getAllAlarms().get(position));
            }
            sw = (Switch) vi.findViewById(R.id.switch1);
            if(db.getSwitchStatus(alarmInfo[0])){
                sw.setChecked(true);
            }else{
                sw.setChecked(false);
            }
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean on) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    if (on) {
                        // String[] alarmInfo = alarmText.getText().toString().split("\\r?\\n");
                        int lastPos = db.getLastAlarmPosition();
                        Calendar calendar = Calendar.getInstance();
                        String[] alarmTime = alarmInfo[1].split(":");
                        db.changeSwitchStatus(alarmInfo[0],switchOn);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(alarmTime[1]));
                        Intent receiverIntent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, lastPos + 1, receiverIntent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        long timeInMillis = calendar.getTimeInMillis();
                        if (timeInMillis > System.currentTimeMillis()) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                        } else {
                            timeInMillis += AlarmManager.INTERVAL_DAY;
                            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                        }
                        Toast.makeText(context, alarmInfo[0] + " has been enabled", Toast.LENGTH_LONG).show();
                    } else {
                        String[] alarmInfo = alarmText.getText().toString().split("\\r?\\n");
                        db.changeSwitchStatus(alarmInfo[0],switchOff);
                        int currentAlarmPosition = db.getCurrentAlarmPosition(alarmInfo[0]);
                        Intent receiverIntent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, currentAlarmPosition, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.cancel(pendingIntent);
                        Toast.makeText(context, alarmInfo[0] + " has been disabled", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return vi;

    }

}
