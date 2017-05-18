package xs.wakemeanddontbreakme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Vedad on 2017-05-15.
 */

public class NfcTask extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    TextView textView;
    ProgressBar pb;
    ImageView iv;
    private Stack<Integer> randOrderMembers;
    private int difficulty, order;
    private MediaPlayer mediaPlayer, success;
    private boolean blocked1, blocked2, blocked3;
    private boolean tag1, tag2, tag3;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_task);
        iv = (ImageView) findViewById(R.id.nfc_background);
        showInstructions();
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
        pb = (ProgressBar)findViewById(R.id.progressBarLock);
        mediaPlayer = new MediaPlayer();
        success = MediaPlayer.create(this, R.raw.success);
        success.setVolume(1.0f,1.0f);
        Bundle extras = getIntent().getExtras();
        randOrderMembers = new Stack<>();
        difficulty = extras.getInt("difficulty");
        fixProgressBar(difficulty);
        setUpRingtoneAndVibration(extras.getInt("vibration"));          //Run private method to setup ringtone and vibrator
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        textView = (TextView) findViewById(R.id.whichTag);
        textView.setText("Find this guy!");
        randomOrder();
        order = getRandomGuy();
        iv.setImageResource(order); //random sen
    }


    private String rightTagToRightGuy(int drawableId){
        String temp = "";
        switch(drawableId){
            case R.drawable.vedad:
                temp = "1";
             break;
            case R.drawable.carl:
                temp = "2";
             break;
            case R.drawable.james:
                temp = "3";
                break;
            case R.drawable.erik:
                temp = "4";
                break;
        }
    return temp;
    }

    private void fixProgressBar(int difficulty){
        switch(difficulty){
            case 0:
                pb.setVisibility(View.INVISIBLE);
                break;
            case 1:
                pb.setVisibility(View.VISIBLE);
                pb.setMax(2);
                break;
            case 2:
                pb.setVisibility(View.VISIBLE);
                pb.setMax(4);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private static int randInt(int min, int max) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }

    private int getRandomGuy(){
        return randOrderMembers.pop();
    }

    private void randomOrder(){
        int[] theTeam = {R.drawable.vedad, R.drawable.carl, R.drawable.erik,R.drawable.james};
        int size = 0;
        boolean inserted = false;
        while(!inserted){
            int random = randInt(0,3);
            if(!randOrderMembers.contains(theTeam[random])){
            randOrderMembers.push(theTeam[random]);
                size++;
            }
            if(size == 4){
            inserted = true;
            }
            }
    }

    public void showInstructions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To deactivate the alarm, go to the guy on the picture!");
        builder.setCancelable(false);
        builder.setTitle("Scan the tag!");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setIcon(android.R.drawable.ic_lock_idle_alarm);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, NfcTask.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
        } else {
            Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length>0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String toCompare = rightTagToRightGuy(order);
            String tagContent = getTextFromNdefRecord(ndefRecord);
            if (tagContent.equals(toCompare) && difficulty == 0) {
                dismissAlarm();
            }
            if(!blocked1) {
            if (tagContent.equals(toCompare) && difficulty != 0) {
                    tag1 = true;
                    order = getRandomGuy();
                    iv.setImageResource(order);
                    toCompare = rightTagToRightGuy(order);
                    textView.setText("Now this guy!");
                    mediaPlayer.pause();
                    delayTheSound();
                    success.start();
                    pb.setProgress(1);
                    blocked1 = true;
                }
            }
            if (difficulty == 1 && tag1 == true) {
                if (tagContent.equals(toCompare)) {
                    dismissAlarm();
                }
            }
            if (!blocked2) {
                if (difficulty == 2 && tag1 == true) {
                    if (tagContent.equals(toCompare)) {
                        tag2 = true;
                        order = getRandomGuy();
                        toCompare = rightTagToRightGuy(order);
                        iv.setImageResource(order);
                        textView.setText("And now this one!");
                        mediaPlayer.pause();
                        delayTheSound();
                        success.start();
                        pb.setProgress(2);
                        blocked2 = true;
                    }
                }
            }
            if (!blocked3) {
                if (tag1 == true && tag2 == true) {
                    if (tagContent.equals(toCompare)) {
                        tag3 = true;
                        order = getRandomGuy();
                        toCompare = rightTagToRightGuy(order);
                        iv.setImageResource(order);
                        textView.setText("And last, but not least!");
                        mediaPlayer.pause();
                        delayTheSound();
                        success.start();
                        pb.setProgress(3);
                        blocked3 = true;
                    }
                }
            }
            if(tag3==true){
                if(tagContent.equals(toCompare)){
                    dismissAlarm();
                }
            }
        }else
        {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void delayTheSound(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }, 200);
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) { }
        return tagContent;
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
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


    private void dismissAlarm() {
        mediaPlayer.stop();
        success.start();
        if (vibrator != null) {
            vibrator.cancel();
        }
        Toast.makeText(this, "Good work! Now enjoy your day!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
