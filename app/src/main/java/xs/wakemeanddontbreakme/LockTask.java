package xs.wakemeanddontbreakme;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
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
    private MediaPlayer mediaPlayer;
    private float mCurrentDegree = 0f;
    private double fakeVal;
    private String realPassword, password;
    private int counter = 0;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_task);
        mediaPlayer = new MediaPlayer();
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
        Bundle extras = getIntent().getExtras();
        setUpRingtoneAndVibration(extras.getInt("vibration"));          //Run private method to setup ringtone and vibrator
        setUpDifficulty(extras.getInt("difficulty"));                   //Run private method to setup difficulty. NOTE: must be done before randomizePass();
        //Run password randomise
        randomizePass();
        //Run private method to setup ringtone and vibrator

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

    public void onMutePress(View view) {
        mediaPlayer.stop();
    }

    private void dismissAlarm() {
        mediaPlayer.stop();
        if (vibrator != null) {
            vibrator.cancel();
        }
        onStop();
        finish();
    }

    private void enterPassword(String pw) {
        if (pw.equals(realPassword)) {
            MediaPlayer temp = MediaPlayer.create(this, R.raw.success);
            temp.start();
            dismissAlarm();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The entered passcode was incorrect, please try again");
            builder.setCancelable(false);
            builder.setTitle("I'm sorry friend");
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    counter = 0;
                    passText.setText("");
                    randomizePass();
                }
            });
            builder.setIcon(android.R.drawable.ic_lock_idle_alarm);
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
        builder.setMessage("To deactivate the alarm, enter the passcode by rotating the phone and pressing enter.");
        builder.setCancelable(false);
        builder.setTitle("Good morning! The passcode is: " + sb.toString());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_lock_idle_alarm);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpDifficulty(int dif) {
        switch (dif) {
            case 0: difficulty = 100;
                break;
            case 1: difficulty = 10;
                break;
            case 2: difficulty = 1;
                break;
        }
    }

    private void setUpRingtoneAndVibration(int vibration) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try{
            mediaPlayer.setDataSource(this, alarmUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (vibration == 1) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibrationPattern, 0);
            }
        } catch (Exception e) {

        }
    }

    private int roundDown(double val) {
        int roundedVal = (int) val / difficulty;
        roundedVal *= difficulty;
        return roundedVal;
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
