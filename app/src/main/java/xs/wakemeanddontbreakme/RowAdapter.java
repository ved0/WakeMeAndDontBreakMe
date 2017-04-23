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
    int limit;
    private LayoutInflater inflater = null;


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
            ImageView alarmImage = (ImageView) vi.findViewById(R.id.alarmImage);
            alarmText.setText(db.getAllAlarms().get(position));
            alarmImage.setImageResource(R.drawable.alarm_icon);
            final int pos = position;
            Switch sw = (Switch) vi.findViewById(R.id.switch1);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean on){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    if(on)
                    {
                        String [] alarmInfo = alarmText.getText().toString().split("\\r?\\n");
                        int i = db.getAlarmPosition(alarmInfo[0],alarmInfo[1]);
                        Calendar calendar = Calendar.getInstance();
                        String [] alarmTime = alarmInfo[1].split(":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(alarmTime[1]));
                        Intent receiverIntent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, receiverIntent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                        // add functionality
                       // Toast.makeText(context, " I am on "+ alarmText.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String [] alarmInfo = alarmText.getText().toString().split("\\r?\\n");
                        int i = db.getAlarmPosition(alarmInfo[0],alarmInfo[1]);
                        Intent receiverIntent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                        //add functionality
                       // Toast.makeText(context, " I am off "+ pos, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return vi;

    }

}
