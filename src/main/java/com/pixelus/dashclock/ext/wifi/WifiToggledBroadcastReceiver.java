package com.pixelus.dashclock.ext.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;

public class WifiToggledBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiToggledBroadcastReceiver.class.getName();

    private WifiExtension extension;

    public WifiToggledBroadcastReceiver(final WifiExtension extension) {

        this.extension = extension;
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {

      try {
        extension.onUpdateData(DashClockExtension.UPDATE_REASON_MANUAL);
      } catch (NullPointerException e) {
        // Every so often an exception seems to be thrown by the DashClock api.
        // It seems that this exception is timing related.  Catch and log it for now!
        Crashlytics.log("NullPointerException caught when updating dashclock following receiving broadcast: "
          + intent.toString());
        Crashlytics.logException(e);
      }
    }
}