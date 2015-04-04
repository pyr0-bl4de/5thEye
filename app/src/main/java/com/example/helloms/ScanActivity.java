package com.example.helloms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.SurfaceView;

import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;

public class ScanActivity extends Activity implements AutoScannerSession.Listener {

  private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE | Result.Type.EAN13;

  private AutoScannerSession session = null;
    //AlertMessage
    private final int CHECK_CODE = 0;
    private Speaker speaker;

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan);

    SurfaceView preview = (SurfaceView)findViewById(R.id.preview);

    try {
      session = new AutoScannerSession(this, Scanner.get(), this, preview);
      session.setResultTypes(TYPES);
    } catch (MoodstocksError e) {
      e.printStackTrace();
    }
  }
  @Override
  protected void onResume() {
    super.onResume();
    session.start();
  }

  @Override
  protected void onPause() {
    super.onPause();
    session.stop();
  }

  @Override
  public void onCameraOpenFailed(Exception e) {
    // You should inform the user if this occurs!
  }

  @Override
  public void onWarning(String debugMessage) {
    // Useful for debugging!
  }

  @Override
  public void onResult(Result result) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(false);
    builder.setNeutralButton("OK", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        session.resume();
      }
    });
    builder.setTitle(result.getType() == Result.Type.IMAGE ? "Image:" : "Barcode:");
    builder.setMessage(result.getValue());
    builder.show();

      String text = result.getValue();
      speaker.speak("There is a "+ text + "in front of you");

  }

}
