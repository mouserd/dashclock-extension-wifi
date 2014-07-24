package com.pixelus.dashclock.ext.wifi;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.pixelus.dashclock.ext.wifi.broadcast.SettingsUpdatedBroadcastReceiver;
import com.pixelus.dashclock.ext.wifi.broadcast.WifiConnectionBroadcastReceiver;
import com.pixelus.dashclock.ext.wifi.broadcast.WifiSignalStateBroadcastReceiver;
import com.pixelus.dashclock.ext.wifi.broadcast.WifiStateBroadcastReceiver;
import com.pixelus.dashclock.ext.wifi.builder.WifiMessageBuilder;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class WifiExtension extends DashClockExtension {

  public static final String TAG = WifiExtension.class.getName();
  public static final String WIFI_ENABLED = "com.pixelus.dashclock.ext.wifi.WIFI_ENABLED";
  public static final String EXTENSION_SETTINGS_CHANGED = "com.pixelus.dashclock.ext.wifi.SETTINGS_CHANGED";
  public static final String PREF_SHOW_SIGNAL_STRENGTH = "show_signal_strength";
  public static final String PREF_SHOW_ONLY_WHEN_CONNECTED = "show_only_when_connected";

  public static final int UPDATE_REASON_FORCED = 99;

  private boolean crashlyticsStarted = false;
  private boolean signalStrengthReceiverRegistered = false;
  private boolean wifiStateReceiverRegistered = false;

  private WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
  private WifiSignalStateBroadcastReceiver signalStrengthBroadcastReceiver;
  private SettingsUpdatedBroadcastReceiver settingsUpdatedBroadcastReceiver;
  private int currentIcon = -1;
  private String currentStatus;

  @Override
  protected void onInitialize(boolean isReconnect) {
    super.onInitialize(isReconnect);

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    setUpdateWhenScreenOn(true);

    // On create, register to receive any changes to the wifi settings.  This ensures that we can
    // update our extension status based on us toggling the state or something externally.
    wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver(this);
    signalStrengthBroadcastReceiver = new WifiSignalStateBroadcastReceiver(this);

    final WifiConnectionBroadcastReceiver wifiConnectionBroadcastReceiver = new WifiConnectionBroadcastReceiver(this);
    registerReceiver(wifiConnectionBroadcastReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
    registerReceiver(wifiConnectionBroadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

    settingsUpdatedBroadcastReceiver = new SettingsUpdatedBroadcastReceiver(this);
    registerReceiver(settingsUpdatedBroadcastReceiver, new IntentFilter(EXTENSION_SETTINGS_CHANGED));

    onWifiStatusChanged();
    onSettingsChanged();
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(wifiStateBroadcastReceiver);
    unregisterReceiver(settingsUpdatedBroadcastReceiver);
  }

  public void onUpdateData() {
    onUpdateData(UPDATE_REASON_MANUAL);
  }

  @Override
  public void onUpdateData(int updateReason) {

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean showSignalStrength = sp.getBoolean(PREF_SHOW_SIGNAL_STRENGTH, false);
    final boolean showOnlyWhenConnected = sp.getBoolean(PREF_SHOW_ONLY_WHEN_CONNECTED, false);

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
    // don't bother trying to publish an update.  This is done because this extension receives a large
    // number of broadcast messages, particularly when the signal strength updates.  So we try not to unnecessarily
    // update the UI.
    if (updateReason == UPDATE_REASON_FORCED && icon == currentIcon && status.equals(currentStatus)) {
      return;
    }

    currentIcon = icon;
    currentStatus = status;

    // Set the the extension invisible if the user only wants to see when a connection is active.
    final boolean hideExtension = showOnlyWhenConnected && !networkInfo.isConnected();
    if (hideExtension) {
      currentIcon = -1; // Ensure the current icon will be different should a connection be established!
    }

    final ExtensionData extensionData = new ExtensionData()
        .visible(!hideExtension)
        .icon(icon)
        .status(status)
        .expandedTitle(builder.buildExpandedTitleMessage())
        .clickIntent(new Intent(this, ToggleWifiDialogActivity.class)
            .putExtra(WIFI_ENABLED, wifiManager.isWifiEnabled()));

    publishUpdate(extensionData);
  }

  public void onWifiStatusChanged() {

    final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    final boolean wifiEnabled = wifiManager.isWifiEnabled();

    updateWifiStatusBroadcastReceiver(wifiEnabled);
  }

  private void updateWifiStatusBroadcastReceiver(boolean wifiEnabled) {

    if (wifiEnabled) {
      registerReceiver(wifiStateBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    } else {
      if (wifiStateReceiverRegistered) {
        unregisterReceiver(wifiStateBroadcastReceiver);
      }
    }

    wifiStateReceiverRegistered = wifiEnabled;
  }

  public void onSettingsChanged() {

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean showSignalStrength = sp.getBoolean(PREF_SHOW_SIGNAL_STRENGTH, true);

    Log.d(TAG, "Settings changed, register signal strength receiver = " + showSignalStrength);

    registerSignalStrengthReceiver(showSignalStrength);
  }

  private void registerSignalStrengthReceiver(boolean showSignalStrength) {
    if (showSignalStrength) {
      registerReceiver(signalStrengthBroadcastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    } else {
      if (signalStrengthReceiverRegistered) {
        unregisterReceiver(signalStrengthBroadcastReceiver);
      }
    }

    signalStrengthReceiverRegistered = showSignalStrength;
  }

  private int getIcon(boolean showSignalStrength, WifiManager wifiManager, NetworkInfo networkInfo) {

    // Is wifi disabled?
    if (!wifiManager.isWifiEnabled()) {
      return R.drawable.ic_signal_off;
    }

    // Does the user not want to see signal strength updates?
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