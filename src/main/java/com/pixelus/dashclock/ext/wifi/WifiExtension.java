package com.pixelus.dashclock.ext.wifi;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class WifiExtension extends DashClockExtension {

  public static final String TAG = WifiExtension.class.getName();
  public static final String WIFI_ENABLED = "com.pixelus.dashclock.ext.wifi.WIFI_ENABLED";

  private boolean crashlyticsStarted = false;
  private WifiToggledBroadcastReceiver wifiToggledBroadcastReceiver;
  private int lastIcon = -1;
  private String lastStatus;

  @Override
  public void onCreate() {

    super.onCreate();

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean showSignalStrength = sp.getBoolean("show_signal_strength", true);

    // On create, register to receive any changes to the wifi settings.  This ensures that we can
    // update our extension status based on us toggling the state or something externally.
    wifiToggledBroadcastReceiver = new WifiToggledBroadcastReceiver(this);
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    if (showSignalStrength) {
      registerReceiver(wifiToggledBroadcastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(wifiToggledBroadcastReceiver);
  }

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

    final boolean showSignalStrength = sp.getBoolean("show_signal_strength", true);
    final boolean showOnlyWhenConnected = sp.getBoolean("show_only_when_connected", false);

    final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = connManager.getNetworkInfo(TYPE_WIFI);
    final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

    final WifiMessageBuilder builder = new WifiMessageBuilder()
        .withContext(this)
        .withNetworkInfo(networkInfo)
        .withWifiManager(wifiManager);

    final String status = builder.buildStatusMessage();
    final int icon = getIcon(showSignalStrength, wifiManager, networkInfo);
    // Minor optimisation - if the icon or status haven't changed since the last update, then
    // don't both trying to publish an update.  This is done because this extension receives a large
    // number of broadcast messages, particularly when the signal strength updates.  So we try not to unnecessarily
    // update the UI.
    if (icon == lastIcon && status.equals(lastStatus)) {
      return;
    }

    lastIcon = icon;
    lastStatus = status;
    // Set the the extension invisible if the user only wants to see when
    final boolean hideExtension = showOnlyWhenConnected && !networkInfo.isConnected();
    if (hideExtension) {
      lastIcon = -1;
    }

    final Intent toggleWifiIntent = new Intent(this, ToggleWifiDialogActivity.class);
    toggleWifiIntent.putExtra(WIFI_ENABLED, wifiManager.isWifiEnabled());

    final ExtensionData extensionData = new ExtensionData()
        .visible(!hideExtension)
        .icon(icon)
        .status(status)
        .expandedTitle(builder.buildExpandedTitleMessage())
//        .expandedBody(builder.buildExpandedBodyMessage())
        .clickIntent(toggleWifiIntent);

    publishUpdate(extensionData);

  }

  private int getIcon(boolean showSignalStrength, WifiManager wifiManager, NetworkInfo networkInfo) {

    // Is wifi disabled?
    if (!wifiManager.isWifiEnabled()) {
      return R.drawable.ic_signal_off;
    }

    // Does the user doesn't want to see signal strength updates?
    if (!showSignalStrength && networkInfo.isConnected()) {
      return R.drawable.ic_signal_4;
    }

    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    final int signalStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);

    switch (signalStrength) {
      case 1:
        return R.drawable.ic_signal_1;
      case 2:
        return R.drawable.ic_signal_2;
      case 3:
        return R.drawable.ic_signal_3;
      case 4:
        return R.drawable.ic_signal_4;
      default:
        return R.drawable.ic_signal_0;
    }
  }
}