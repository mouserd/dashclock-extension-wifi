package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

public class WifiSignalStateBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = WifiSignalStateBroadcastReceiver.class.getSimpleName();

  private WifiExtension extension;

  public WifiSignalStateBroadcastReceiver(final WifiExtension extension) {

    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(final Context context, final Intent intent) {

    Log.d(TAG, "Received broadcast " + intent.getAction());
    extension.onUpdateData();
  }
}