package com.pixelus.dashclock.ext.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.apps.dashclock.api.DashClockExtension;

public class WifiToggledBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiToggledBroadcastReceiver.class.getName();

    private WifiExtension extension;

    public WifiToggledBroadcastReceiver(final WifiExtension extension) {

        this.extension = extension;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.d(TAG, "Received broadcast for " + intent.getAction());
        extension.onUpdateData(DashClockExtension.UPDATE_REASON_MANUAL);
    }
}