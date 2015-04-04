package com.example.helloms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moodstocks.android.Scanner;
import com.moodstocks.android.MoodstocksError;

public class MainActivity extends Activity implements Scanner.SyncListener {

  // Moodstocks API key/secret pair
  private static final String API_KEY    = "9lq0ixbxxgsokj9kezw5";
  private static final String API_SECRET = "nLLlvWvmYdEsfwSp";

  private boolean compatible = false;
  private Scanner scanner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    compatible = Scanner.isCompatible();
    if (compatible) {
      try {
        scanner = Scanner.get();
        String path = Scanner.pathFromFilesDir(this, "scanner.db");
        scanner.open(path, API_KEY, API_SECRET);
        scanner.setSyncListener(this);
        scanner.sync();
      } catch (MoodstocksError e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (compatible) {
      try {
        scanner.close();
        scanner.destroy();
        scanner = null;
      } catch (MoodstocksError e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onSyncStart() {
    Log.d("Moodstocks SDK", "Sync will start.");
  }

  @Override
  public void onSyncComplete() {
    try {
      Log.d("Moodstocks SDK", "Sync succeeded ("+scanner.count()+" images)");
    } catch (MoodstocksError e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSyncFailed(MoodstocksError e) {
    Log.d("Moodstocks SDK", "Sync error #"+e.getErrorCode()+": "+e.getMessage());
  }

  @Override
  public void onSyncProgress(int total, int current) {
    int percent = (int) ((float) current / (float) total * 100);
    Log.d("Moodstocks SDK", "Sync progressing: "+percent+"%");
  }

  public void onScanButtonClicked(View view) {
    if (compatible) {
      startActivity(new Intent(this, ScanActivity.class));
    }
  }

}