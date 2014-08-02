package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.crashlytics.android.Crashlytics;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

public class WifiConnectionBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = WifiConnectionBroadcastReceiver.class.getSimpleName();

  private WifiExtension extension;

  public WifiConnectionBroadcastReceiver(final WifiExtension extension) {

    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(Context context, Intent intent) {

    try {
      extension.onUpdateData(WifiExtension.UPDATE_REASON_FORCED);
    } catch (NullPointerException e) {
      // Every so often an exception seems to be thrown by the DashClock api.
      // It seems that this exception is timing related.  Catch and log it for now!
      Crashlytics.log("NullPointerException caught when updating dashclock following receiving broadcast: "
          + intent.toString());
      Crashlytics.logException(e);
    }
  }
}