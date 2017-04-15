package xs.wakemeanddontbreakme;

/**
 * Created by Vedad on 2017-04-14.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class RowAdapter extends BaseAdapter {
    Context context;
    ArrayList aL;
    int limit;
    private LayoutInflater inflater = null;


    public RowAdapter(Context context, ArrayList aL, int size) {
        this.context = context;
        this.aL = aL;
        this.limit = size;
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
        DatabaseHandler db = new DatabaseHandler(context);
        TextView alarmText = (TextView) vi.findViewById(R.id.alarmText);
        ImageView alarmImage = (ImageView) vi.findViewById(R.id.alarmImage);
        for(String s: db.getAllAlarms()){
            alarmText.setText(s);
            alarmImage.setImageResource(R.drawable.alarm_icon);

        }
        return vi;

    }
}
