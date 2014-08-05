package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

import static com.pixelus.dashclock.ext.wifi.WifiExtension.UPDATE_REASON_FORCED;

public class WifiStateBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = WifiStateBroadcastReceiver.class.getSimpleName();

  private WifiExtension extension;

  public WifiStateBroadcastReceiver(final WifiExtension extension) {

    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(final Context context, final Intent intent) {

    extension.onUpdateData(UPDATE_REASON_FORCED);
  }
}