package com.pixelus.dashclock.ext.wifi;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/*
 * @author David Mouser
 */
public class WifiMessageBuilder {

  private static final String TAG = WifiMessageBuilder.class.getName();
  private WifiManager wifiManager;
  private Context context;
  private NetworkInfo networkInfo;

  public WifiMessageBuilder withContext(final Context context) {
    this.context = context;
    return this;
  }

  public WifiMessageBuilder withWifiManager(final WifiManager wifiManager) {
    this.wifiManager = wifiManager;
    return this;
  }

  public WifiMessageBuilder withNetworkInfo(NetworkInfo networkInfo) {
    this.networkInfo = networkInfo;
    return this;
  }

  public String buildStatusMessage() {
    return getWifiStatus();
  }

  public String buildExpandedTitleMessage() {

    if (networkInfo == null || wifiManager == null) {

      return context.getString(R.string.extension_expanded_title_not_connected,
          context.getString(R.string.wifi_status_unknown));
    }

    if (!networkInfo.isConnected()) {
      return context.getString(R.string.extension_expanded_title_not_connected, getWifiStatus());
    }

    return context.getString(R.string.extension_expanded_title_connected, getWifiStatus(),
        wifiManager.getConnectionInfo().getSSID().replaceAll("\"", ""));
  }

  public String buildExpandedBodyMessage() {

    if (wifiManager == null) {
      return "";
    }

    final boolean wifiEnabled = wifiManager.isWifiEnabled();
    if (!wifiEnabled) {
      return "";
    }

    return context.getString(R.string.extension_expanded_body);
  }

  private String getWifiStatus() {

    if (networkInfo == null || wifiManager == null) {
      return context.getString(R.string.wifi_status_unknown);
    }

    if (networkInfo.isConnected()) {
      return context.getString(R.string.wifi_status_connected);
    }
    if (wifiManager.isWifiEnabled()) {
      return context.getString(R.string.wifi_status_enabled);
    }

    return context.getString(R.string.wifi_status_disabled);

  }
}
