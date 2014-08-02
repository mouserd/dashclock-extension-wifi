package com.pixelus.dashclock.ext.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.pixelus.dashclock.ext.wifi.WifiExtension.WIFI_ENABLED;

public class ToggleWifiFragment extends DialogFragment {

  private static final String TAG = ToggleWifiFragment.class.getSimpleName();

  private boolean wifiEnabled;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    final Activity activity = getActivity();
    final Intent intent = getActivity().getIntent();
    final Bundle bundle = intent.getExtras();
    wifiEnabled = bundle.getBoolean(WIFI_ENABLED);

    int message = R.string.toggle_wifi_to_enabled_message;
    if (wifiEnabled) {
      message = R.string.toggle_wifi_to_disabled_message;
    }

    return new AlertDialog.Builder(activity)
        .setTitle(message)
        .setPositiveButton(R.string.toggle_wifi_affirmative_button, new DialogClickListener())
        .setNegativeButton(R.string.toggle_wifi_negative_button, new DialogClickListener())
        .create();

  }

  private class DialogClickListener implements DialogInterface.OnClickListener {

//    private Activity activity;

    private DialogClickListener() {
//      this.activity = context;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int id) {

      Log.d(TAG, "Handling " + (BUTTON_POSITIVE == id ? "affirmative" : "negative") + " click event");

      if (BUTTON_POSITIVE == id) {

        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(!wifiEnabled);
      }

      getActivity().finish();
    }
  }
}