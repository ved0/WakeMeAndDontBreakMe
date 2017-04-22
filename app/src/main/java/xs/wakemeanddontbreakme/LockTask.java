package xs.wakemeanddontbreakme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class LockTask extends AppCompatActivity implements SensorEventListener {
    Ringtone ringtone;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};


    private ImageView lock;
    private TextView xText, pass;
    private SensorManager mSensorManager;
    private Sensor mOrientation;

    private float mCurrentDegree = 0f;
    private double fakeVal;
    private String realPassword, password;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_task);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        lock = (ImageView) findViewById(R.id.lock_image);
        xText = (TextView) findViewById(R.id.x_text);
        xText.setCursorVisible(false);
        pass = (TextView) findViewById(R.id.passcode_text);
        pass.setCursorVisible(false);
        password = "";

        //Run private method to setup ringtone and vibrator
//        setUpRingtoneAndVibration()
//        ringtone.play();
//        vibrator.vibrate(vibrationPattern, 0);

        //Run password randomizer
        randomizePass();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Rotate the lock using "real" values
        float x = event.values[0];
        RotateAnimation ra = new RotateAnimation(mCurrentDegree, x,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(0);
        ra.setFillAfter(true);
        lock.startAnimation(ra);
        mCurrentDegree = x;

        //Display "fake" value to match lock image
        fakeVal = (360 - x) * 2.77;
        xText.setText("Value : " + (int) fakeVal + "");
    }

    public void onEnterPress(View view) {
        if (counter < 3) {
            password = password.concat(" ");
            password = password.concat(Integer.toString((int)fakeVal));
            pass.setText("PASS: " + password);
            counter++;
        } else {
            enterPassword(password);
            password = "";
        }
    }

    public void onDismissPress(View view) {
        finish();
        ringtone.stop();
        vibrator.cancel();
    }

    private void enterPassword(String pw) {

    }

    private void randomizePass() {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int rNum = r.nextInt(999);
            realPassword.concat(" ");
            realPassword.concat(Integer.toString(rNum));
        }
        AlertDialog passDialogue = new AlertDialog();
    }

    private void setUpRingtoneAndVibration() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), alarmUri);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        super.onStop();
        mSensorManager.unregisterListener(this);
    }
}
