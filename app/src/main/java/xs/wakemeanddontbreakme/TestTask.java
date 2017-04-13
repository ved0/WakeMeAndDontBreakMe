package xs.wakemeanddontbreakme;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestTask extends AppCompatActivity {
    Ringtone ringtone;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task);
        //Run private method to setup ringtone and vibrator
        setUp();

        ringtone.play();
        vibrator.vibrate(vibrationPattern, 0);

    }

    public void onDismissPress(View view) {
        finish();
        ringtone.stop();
        vibrator.cancel();
    }

    private void setUp() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), alarmUri);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
}
