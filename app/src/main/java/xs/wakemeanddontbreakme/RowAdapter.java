package xs.wakemeanddontbreakme;

/**
 * Created by Vedad on 2017-04-14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class RowAdapter extends ArrayAdapter {

    public RowAdapter(Context context, String [] alarmDetails) {
        super(context, R.layout.alarm_row ,alarmDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater alarmInflater = LayoutInflater.from(getContext());
        View alarmView = alarmInflater.inflate(R.layout.alarm_row, parent, false);

        String oneAlarm = (String) getItem(position);
        TextView alarmText = (TextView) alarmView.findViewById(R.id.alarmText);
        ImageView alarmImage = (ImageView) alarmView.findViewById(R.id.alarmImage);

        alarmText.setText(oneAlarm);
        alarmImage.setImageResource(R.drawable.alarm_icon);
        return alarmView;
    }
}
