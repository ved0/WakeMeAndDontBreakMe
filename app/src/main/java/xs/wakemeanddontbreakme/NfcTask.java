package xs.wakemeanddontbreakme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;

/**
 * Created by Vedad on 2017-05-15.
 */

public class NfcTask extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    TextView txtTagContent;

    private MediaPlayer mediaPlayer;
    Vibrator vibrator;
    long[] vibrationPattern = {0, 1000, 1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_task);

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

        mediaPlayer = new MediaPlayer();
        Bundle extras = getIntent().getExtras();
        setUpRingtoneAndVibration(extras.getInt("vibration"));          //Run private method to setup ringtone and vibrator
       //TODO setUpDifficulty(extras.getInt("difficulty"));
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        txtTagContent = (TextView) findViewById(R.id.whichTag);
        txtTagContent.setText(whichTag());
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


    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, NfcTask.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private String whichTag(){
        int random = 1;
        String temp = "Scan tag ";
        switch(random){
            case 1:
                temp += 1+"!";
                break;
        }
        return temp;
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


    private String getTagValue(String s){
        return s.substring(s.length()-2,s.length()-1);
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length>0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            String temp = txtTagContent.getText().toString();
            if(tagContent.equals(getTagValue(temp))){
                MediaPlayer success = MediaPlayer.create(this, R.raw.success);
                success.start();
                dismissAlarm();
            }
        }else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
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

    private void dismissAlarm() {
        mediaPlayer.stop();
        if (vibrator != null) {
            vibrator.cancel();
        }
        onStop();
        finish();
    }
}
