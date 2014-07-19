package com.pixelus.dashclock.ext.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.google.android.apps.dashclock.api.DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED;

public class WifiToggledBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiToggledBroadcastReceiver.class.getName();

    private WifiExtension extension;

    public WifiToggledBroadcastReceiver(final WifiExtension extension) {

        this.extension = extension;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received broadcast for " + intent.getAction());
        extension.onUpdateData(UPDATE_REASON_SETTINGS_CHANGED);
    }
}