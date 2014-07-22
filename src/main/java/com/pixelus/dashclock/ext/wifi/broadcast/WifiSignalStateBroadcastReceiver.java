package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

public class WifiSignalStateBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiSignalStateBroadcastReceiver.class.getName();

    private WifiExtension extension;

    public WifiSignalStateBroadcastReceiver(final WifiExtension extension) {

        this.extension = extension;
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {

//      Log.d(TAG, "Received broadcast " + intent.getAction());
      //try {
        extension.onUpdateData();
//      } catch (NullPointerException e) {
//        // Every so often an exception seems to be thrown by the DashClock api.
//        // It seems that this exception is timing related.  Catch and log it for now!
//        Crashlytics.log("NullPointerException caught when updating dashclock following receiving broadcast: "
//          + intent.toString());
//        Crashlytics.logException(e);
//      }
    }
}