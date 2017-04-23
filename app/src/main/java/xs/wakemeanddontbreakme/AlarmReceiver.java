package xs.wakemeanddontbreakme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Random;

/**
 * Created by James on 11/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null){
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int rand = randInt(1, 2);
        if (rand == 1) {
            Intent i = new Intent(context, LockTask.class);
            context.startActivity(i);
        }else if (rand == 2){
            Intent i = new Intent(context, ShakeEventListener.class);
            context.startActivity(i);
        }
    }
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}

