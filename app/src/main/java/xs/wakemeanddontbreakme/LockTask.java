package xs.wakemeanddontbreakme;

import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LockTask extends AppCompatActivity implements SensorEventListener {
    Ringtone ringtone;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};


    private ImageView lock;
    private TextView xText, passText;
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
        passText = (TextView) findViewById(R.id.passcode_text);
        passText.setCursorVisible(false);
        password = "";
        //Run password randomise
        randomizePass();
        //Run private method to setup ringtone and vibrator
        Bundle extras = getIntent().getExtras();
        setUpRingtoneAndVibration(extras.getInt("vibration"));
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
        fakeVal = roundDown((360 - x) * 2.77);
        xText.setText("Value : " + (int) fakeVal + "");
    }

    public void onEnterPress(View view) {
        counter++;
        password = password.concat(" ");
        password = password.concat(Integer.toString((int) fakeVal));
        passText.setText("PASS: " + password);
        if (counter == 3) {
            enterPassword(password);
            password = "";
        }
    }

    public void onDismissPress(View view) {
        dismissAlarm();
    }

    private void dismissAlarm() {
        ringtone.stop();
        if(vibrator!=null)
        vibrator.cancel();
        super.onStop();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        finish();
    }

    private void enterPassword(String pw) {
        if (pw.equals(realPassword)) {
            dismissAlarm();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The entered passcode was incorrect");
            builder.setCancelable(false);
            builder.setTitle("Sorry");
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    counter = 0;
                    passText.setText("");
                    randomizePass();
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void randomizePass() {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 3; i++) {
            int rNum = ThreadLocalRandom.current().nextInt(0, 999 + 1);
            rNum = roundDown(rNum);
            sb.append(" ").append(Integer.toString(rNum));
        }
        realPassword = sb.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The passcode is: " + sb.toString());
        builder.setCancelable(false);
        builder.setTitle("ATTENTION");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpRingtoneAndVibration(int vibration) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), alarmUri);
        ringtone.play();
        if (vibration == 1) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(vibrationPattern, 0);
        }
    }

    private int roundDown(double val) {
        int roundedVal = (int) val/10;
        roundedVal *= 10;
        return roundedVal;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
