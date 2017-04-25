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

        int rand = randInt(1, 2);
        switch (rand) {
            case 1:
                //TODO If clause for difficulty
                Intent i1 = new Intent(context, LockTask.class);
                i1.putExtra("vibration", vibration);
                context.startActivity(i1);
                break;
            case 2:
                //TODO if clause for difficulty
                Intent i2 = new Intent(context, ShakeEventListener.class);
                i2.putExtra("vibration", vibration);
                context.startActivity(i2);
                break;
        }
    }

    private static int randInt(int min, int max) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }
}

