package com.pixelus.dashclock.ext.wifi;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ToggleWifiDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogFragment dialog = new ToggleWifiFragment();
        dialog.show(getSupportFragmentManager(), "Toggle Wifi");
    }
}