package xs.wakemeanddontbreakme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

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
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task);
        //Run private method to setup ringtone and vibrator
        setUp();
        ringtone.play();
        vibrator.vibrate(vibrationPattern, 0);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                    finish();
                    ringtone.stop();
                    vibrator.cancel();

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
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
