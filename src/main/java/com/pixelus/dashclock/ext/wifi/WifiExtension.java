package com.pixelus.dashclock.ext.wifi;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class WifiExtension extends DashClockExtension {

  public static final String TAG = WifiExtension.class.getName();
  public static final String WIFI_ENABLED = "com.pixelus.dashclock.ext.wifi.WIFI_ENABLED";

  private boolean crashlyticsStarted = false;
  private WifiToggledBroadcastReceiver wifiToggledBroadcastReceiver;

  @Override
  public void onCreate() {

    super.onCreate();

    // On create, register to receive any changes to the wifi settings.  This ensures that we can
    // update our extension status based on us toggling the state or something externally.
    wifiToggledBroadcastReceiver = new WifiToggledBroadcastReceiver(this);
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
  }

  @Override public void onDestroy() {
    unregisterReceiver(wifiToggledBroadcastReceiver);
  }

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = connManager.getNetworkInfo(TYPE_WIFI);
    final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    final WifiMessageBuilder builder = new WifiMessageBuilder()
        .withContext(this)
        .withNetworkInfo(networkInfo)
        .withWifiManager(wifiManager);

    final Intent toggleWifiIntent = new Intent(this, ToggleWifiDialogActivity.class);
    toggleWifiIntent.putExtra(WIFI_ENABLED, wifiManager.isWifiEnabled());

    final ExtensionData extensionData = new ExtensionData()
        .visible(true)
        .icon(R.drawable.ic_launcher)
        .status(builder.buildStatusMessage())
        .expandedTitle(builder.buildExpandedTitleMessage())
        .expandedBody(builder.buildExpandedBodyMessage())
        .clickIntent(toggleWifiIntent);

    publishUpdate(extensionData);
  }
}