package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

public class WifiConnectionBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = WifiConnectionBroadcastReceiver.class.getName();

  private WifiExtension extension;

  public WifiConnectionBroadcastReceiver(final WifiExtension extension) {

    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(Context context, Intent intent) {

    extension.onUpdateData(WifiExtension.UPDATE_REASON_FORCED);
  }
}