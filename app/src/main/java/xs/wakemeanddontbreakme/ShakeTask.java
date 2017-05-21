package xs.wakemeanddontbreakme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import static xs.wakemeanddontbreakme.R.id.shake_phone;


/**
 * Created by carltidelius on 2017-04-15.
 */

public class ShakeTask extends AppCompatActivity implements SensorEventListener {

    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};
    private SensorManager mSensorManager;
    private ShakeTask mSensorListener;
    private MediaPlayer mediaPlayer;
    /** Minimum movement force to consider. */
    private static final int MIN_FORCE = 10;
    /**
     * Minimum times in a shake gesture that the direction of movement needs to change.
     */
    private int MIN_DIRECTION_CHANGE = 100;

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

    /** OnShakeListener that is called when shake is detected. */
    private OnShakeListener mShakeListener;

    ProgressBar pb;

    /**
     * Interface for shake gesture.
     */
    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
    private final int[] imageArray = { R.drawable.shakedaphone2, R.drawable.shakedaphone,
            R.drawable.shakedaphone3, R.drawable.shakedaphone};
    private ImageView image;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_task);
        mediaPlayer = new MediaPlayer();
        Bundle extras = getIntent().getExtras();
        setUpRingtoneAndVibration(extras.getInt("vibration"));
        setUpDifficulty(extras.getInt("difficulty"));
        image = (ImageView)findViewById(shake_phone);
        pb = (ProgressBar)findViewById(R.id.firstBar3);
        pb.setVisibility(View.VISIBLE);
        pb.setMax(MIN_DIRECTION_CHANGE);
        pb.setBackgroundColor(Color.argb(255,20,20,20));
        showInstructions();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {
                image.setImageResource(imageArray[i]);
                i++;
                if (i > imageArray.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 500);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = this;
        mSensorListener.setOnShakeListener(new ShakeTask.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(mSensorListener, "Good work! Time to get up!", Toast.LENGTH_LONG).show();
                MediaPlayer temp = MediaPlayer.create(mSensorListener, R.raw.success);
                temp.start();
                dismissAlarm();
                if(vibrator!=null)
                    vibrator.cancel();

            }
        });

    }

    public void showInstructions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To deactivate the alarm, shake your phone until the bar is filled.");
        builder.setCancelable(false);
        builder.setTitle("Shake away!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_lock_idle_alarm);
        AlertDialog alert = builder.create();
        alert.show();
    }
    float totalMovement = 0;
    @Override
    public void onSensorChanged(SensorEvent se) {
        // get sensor data
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];

        // calculate movement
        totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

        if (totalMovement > MIN_FORCE) {
            lastX = x;
            lastY = y;
            lastZ = z;

            Runnable updateColor = new Runnable() {

                public void run() {
                    //set the progressbars position to a new value based on shakyness done
                    pb.setProgress(pb.getProgress() + (int)totalMovement/20);
                    //set the progressbars color to match the position of the progressbar
                    float temp = pb.getProgress();
                    pb.setProgressTintList(ColorStateList.valueOf(Color.rgb(
                            (180-(int)(temp/MIN_DIRECTION_CHANGE*150)),
                            (int)(temp/MIN_DIRECTION_CHANGE*150),0)));
                    //end the process if progress is equal to max
                    if (pb.getProgress() >= pb.getMax()) {
                        mShakeListener.onShake();
                        resetShakeParameters();
                    }
                }
            };
            handler.post(updateColor);

        } else{

            Runnable updateColorBackwards = new Runnable() {

                public void run() {
                    pb.setProgress(pb.getProgress() - 1);
                    float temp = pb.getProgress();
                    pb.setProgressTintList(ColorStateList.valueOf(Color.rgb(
                            (180 -  (int)(temp / MIN_DIRECTION_CHANGE * 150)),
                            (int)(temp / MIN_DIRECTION_CHANGE * 150), 0)));
                }
            };
            handler.post(updateColorBackwards);
        }
    }

    /**
     * Resets the shake parameters to their default values.
     */
    private void resetShakeParameters() {
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    private void setUpRingtoneAndVibration(int vibration) {
        //Remember user's volume before we change it
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        try {
            mediaPlayer.setDataSource(this, alarmUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (vibration == 1) {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibrationPattern, 0);
            }
        } catch (Exception e) {

        }
    }
    private void setUpDifficulty(int dif) {
        switch (dif) {
            case 0:
                MIN_DIRECTION_CHANGE = 100;
                break;
            case 1:
                MIN_DIRECTION_CHANGE = 500;
                break;
            case 2:
                MIN_DIRECTION_CHANGE = 1000;
                break;
        }
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
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }
    public void onDismissPress(View view) {
        dismissAlarm();
    }
    @Override
    protected void onStop(){
        super.onStop();
        mSensorManager.unregisterListener(mSensorListener);
    }
    private void dismissAlarm() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        if (vibrator != null) {
            vibrator.cancel();
        }
        onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
