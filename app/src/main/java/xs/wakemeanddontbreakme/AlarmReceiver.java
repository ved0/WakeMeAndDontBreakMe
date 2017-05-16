package xs.wakemeanddontbreakme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by James on 11/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int vibration = extras.getInt("vibration");
        int difficulty = extras.getInt("difficulty");
        int rand = randInt(1, 3);
        switch (rand) {
            case 1:
                Intent i1 = new Intent(context, LockTask.class);
                i1.putExtra("vibration", vibration);
                i1.putExtra("difficulty", difficulty);
                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i1);
                break;
            case 2:
                Intent i2 = new Intent(context, ShakeTask.class);
                i2.putExtra("vibration", vibration);
                i2.putExtra("difficulty", difficulty);
                i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i2);
                break;
            case 3:
                Intent i3 = new Intent(context, ShoutTask.class);
                i3.putExtra("vibration", vibration);
                i3.putExtra("difficulty", difficulty);
                i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i3);
                break;
            case 4:
                Intent i4 = new Intent(context, NfcTask.class);
                i4.putExtra("vibration", vibration);
                i4.putExtra("difficulty", difficulty);
                i4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i4);
                break;
        }
    }

    private static int randInt(int min, int max) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }
}

