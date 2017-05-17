package xs.wakemeanddontbreakme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ShoutTask extends AppCompatActivity {
    private MediaPlayer mp;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private static final int sampleRate = 44100;
    private AudioRecord audio;
    private int bufferSize;
    private EditText text;
    double timeStartLevel1 = 0;
    double timeStartLevel2 = 0;
    double timeStartLevel3 = 0;
    double timeSpentLevel1 = 0;
    double timeSpentLevel2 = 0;
    double timeSpentLevel3 = 0;
    private ProgressBar pb;
    private final Handler handler = new Handler();
    int maxProgressNumber;
    int fightBack;



    private double lastLevel = 0;
    private int difficulty;
    private Thread thread;
    private static final int SAMPLE_DELAY = 75;
    private ImageView micImage;
    private static int[] sampleRates = new int[]{8000, 11025, 22050, 44100};
    long[] vibrationPattern = {0, 1000, 1000};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mp = MediaPlayer.create(this,R.raw.success);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout);
        text = (EditText) findViewById(R.id.instructionField);
        Bundle extras = getIntent().getExtras();
        difficulty = extras.getInt("difficulty");
        setUpDifficulty(difficulty);
        audio = findBestCompatibleAudioRecord();
        micImage = (ImageView) findViewById(R.id.microphone);
        micImage.setKeepScreenOn(true);
        pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
        pb.setMax(maxProgressNumber);
        pb.setBackgroundColor(Color.argb(255,20,20,20));
        setUpRingtoneAndVibration(extras.getInt("vibration"));
        showInstructions();

        try {
            bufferSize = AudioRecord
                    .getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            System.out.println(bufferSize);
        } catch (Exception e) {
            android.util.Log.e("TrackingFlow", "Exception", e);
        }

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


    protected void onResume() {
        super.onResume();
        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if(audio!=null) {
            audio.startRecording();
        }
        thread = new Thread(new Runnable() {
            public void run() {
                timeStartLevel1 = System.currentTimeMillis();
                timeStartLevel2 = System.currentTimeMillis();
                timeStartLevel3 = System.currentTimeMillis();
                while (thread != null && !thread.isInterrupted()) {
                    //Let's make the thread sleep for a the approximate sampling time
                    timeSpentLevel1 = System.currentTimeMillis() - timeStartLevel1;
                    timeSpentLevel2 = System.currentTimeMillis() - timeStartLevel2;
                    timeSpentLevel3 = System.currentTimeMillis() - timeStartLevel3;
                    try {
                        Thread.sleep(SAMPLE_DELAY);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    readAudioBuffer();//After this call we can get the last value assigned to the lastLevel variable
                    final Runnable updateColor = new Runnable() {

                        public void run() {
                            //set the progressbars position to a new value based on shakyness done
                            pb.setProgress(pb.getProgress() + (int) lastLevel);
                            //set the progressbars color to match the position of the progressbar
                            float temp = pb.getProgress();
                            pb.setProgressTintList(ColorStateList.valueOf(Color.rgb(
                                    (180-(int)(temp/maxProgressNumber*150)),
                                    (int)(temp/maxProgressNumber*150),0)));
                            //end the process if progress is equal to max
                            if (pb.getProgress() >= pb.getMax()) {
                                dismissAlarm();
                                Toast.makeText(ShoutTask.this, "Good work! Time to get out of bed!", Toast.LENGTH_LONG).show();
                                mp.start();
                            }
                        }
                    };
                    final Runnable updateColorBackwards = new Runnable() {

                        public void run() {
                            pb.setProgress(pb.getProgress() - fightBack);
                            float temp = pb.getProgress();
                            pb.setProgressTintList(ColorStateList.valueOf(Color.rgb(
                                    (180 -  (int)(temp / maxProgressNumber * 150)),
                                    (int)(temp / maxProgressNumber * 150), 0)));
                        }
                    };
                    handler.post(updateColorBackwards);





                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (lastLevel > 0 && lastLevel <= 50) {
                                micImage.setImageResource(R.drawable.microphone1);
                                timeStartLevel2 = System.currentTimeMillis();
                                timeStartLevel3 = System.currentTimeMillis();


                            } else if (lastLevel > 50 && lastLevel<100) {
                                micImage.setImageResource(R.drawable.microphone2);
                                timeStartLevel3 = System.currentTimeMillis();
                                handler.post(updateColor);

                            } else if (lastLevel > 100) {
                                timeStartLevel2 = System.currentTimeMillis();
                                micImage.setImageResource(R.drawable.microphone3);
                                handler.post(updateColor);




                            }


                        }
                    });
                }
            }
        });
        thread.start();
    }


    public AudioRecord findBestCompatibleAudioRecord() {
        for (int rate : sampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d("hej", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("hej", rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }
    public void showInstructions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To deactivate the alarm, shout into the microphone until the bar is filled.");
        builder.setCancelable(false);
        builder.setTitle("SHOUT LOUDLY!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_lock_idle_alarm);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Functionality that gets the sound level out of the sample
     */
    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {

                // Sense the voice...
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
                if (lastLevel > 300){
                    lastLevel = 250;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
        thread = null;
        try {
            if (audio != null) {
                audio.stop();
                audio.release();
                audio = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpDifficulty(int dif) {
        switch (dif) {
            case 0:
                maxProgressNumber = 1000;
                fightBack = 10;
                break;
            case 1:
                maxProgressNumber = 2500;
                fightBack = 30;
                break;
            case 2:
                maxProgressNumber = 5000;
                fightBack = 50;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    protected void onStop() {
        super.onStop();
    }

    private void dismissAlarm() {
        ringtone.stop();
        if (vibrator != null)
            vibrator.cancel();
        super.onStop();
        if (thread != null)
            thread.interrupt();
        finish();
    }

}




