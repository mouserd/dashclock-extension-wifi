package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

import static com.pixelus.dashclock.ext.wifi.WifiExtension.UPDATE_REASON_FORCED;

public class SettingsUpdatedBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = SettingsUpdatedBroadcastReceiver.class.getSimpleName();

  private WifiExtension extension;

  public SettingsUpdatedBroadcastReceiver(final WifiExtension extension) {
    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(final Context context, final Intent intent) {

    Log.d(TAG, "User updated extension settings...");
    extension.onUpdateData(UPDATE_REASON_FORCED);
  }
}