package xs.wakemeanddontbreakme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
     * Minimum times in a shake gesture that the direction of movement needs to
     * change.
     */
    private int MIN_DIRECTION_CHANGE = 100;

    /** Maximum pause between movements. */
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

    /** Maximum allowed time for shake gesture. */
    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 30000;

    /** Time when the gesture started. */
    private long mFirstDirectionChangeTime = 0;

    /** Time when the last movement started. */
    private long mLastDirectionChangeTime;

    /** How many movements are considered so far. */
    private int mDirectionChangeCount = 0;

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

        /**
         * Called when shake gesture is detected.
         */
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private final int[] imageArray = { R.drawable.shakephone2left, R.drawable.shakephone2,
            R.drawable.shakephone2right, R.drawable.shakephone2};;
    private ImageView image;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_event_task);
        Bundle extras = getIntent().getExtras();
        setUpRingtoneAndVibration(extras.getInt("vibration"));
        setUpDifficulty(extras.getInt("difficulty"));
        mediaPlayer = MediaPlayer.create(this,R.raw.success);
        image = (ImageView)findViewById(shake_phone);
        pb = (ProgressBar)findViewById(R.id.firstBar3);
        pb.setVisibility(View.VISIBLE);
        pb.setMax(MIN_DIRECTION_CHANGE);
        pb.setBackgroundColor(Color.argb(255,20,20,20));

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
                        finish();

                        mediaPlayer.start();
                        if(vibrator!=null)
                        vibrator.cancel();

            }
        });

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
        mFirstDirectionChangeTime = 0;
        mDirectionChangeCount = 0;
        mLastDirectionChangeTime = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    private void setUpRingtoneAndVibration(int vibration) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
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
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
    public void onDismissPress(View view) {
        finish();
        mediaPlayer.stop();
        if(vibrator!=null)
        vibrator.cancel();
    }
    @Override
    protected void onStop(){
        mSensorManager.unregisterListener(mSensorListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
