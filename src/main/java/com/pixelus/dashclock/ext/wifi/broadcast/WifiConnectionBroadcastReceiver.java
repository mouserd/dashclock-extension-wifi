package com.pixelus.dashclock.ext.wifi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;
import com.pixelus.dashclock.ext.wifi.WifiExtension;

import static android.net.NetworkInfo.DetailedState.CONNECTED;
import static android.net.NetworkInfo.DetailedState.DISCONNECTED;
import static android.net.wifi.WifiManager.EXTRA_NETWORK_INFO;
import static android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION;
import static com.pixelus.dashclock.ext.wifi.WifiExtension.UPDATE_REASON_FORCED;

public class WifiConnectionBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = WifiConnectionBroadcastReceiver.class.getSimpleName();

  private WifiExtension extension;

  public WifiConnectionBroadcastReceiver(final WifiExtension extension) {
    this.extension = extension;
  }

  @Override
  public synchronized void onReceive(final Context context, final Intent intent) {

    NetworkInfo.DetailedState detailedState = null;
    // If the intent is anything other than connected or disconnect then just ignore it.
    if (intent.getAction().equals(NETWORK_STATE_CHANGED_ACTION)) {
      final NetworkInfo networkInfo = intent.getParcelableExtra(EXTRA_NETWORK_INFO);
      detailedState = networkInfo.getDetailedState();

      if (!detailedState.equals(CONNECTED) && !detailedState.equals(DISCONNECTED)) {
        return;
      }
    }

    Log.d(TAG, "Processing network change intent..." + intent.getAction() + " (state = " + detailedState + ")");

    extension.onUpdateData(UPDATE_REASON_FORCED);
  }
}