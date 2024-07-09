package com.example.locationdemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class LocationReceiver extends BroadcastReceiver {
    private final PendingIntent pendingIntent;
    private final ArrayList<Boolean> providerEnableds = new ArrayList<>();
    private final ArrayList<Location> locations = new ArrayList<>();
    private final ArrayList<Integer> flushes = new ArrayList<>();

    public LocationReceiver(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setAction("ACTION_LOCATION");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context,requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        context.registerReceiver(this, new IntentFilter(intent.getAction()));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
            locations.add(intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED));
        }

        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            providerEnableds.add(intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false));
        }

        if (intent.hasExtra(LocationManager.KEY_FLUSH_COMPLETE)) {
            flushes.add(intent.getIntExtra(LocationManager.KEY_FLUSH_COMPLETE, -1));
        }
    }

    public PendingIntent getPendingIntent() {
        return this.pendingIntent;
    }
}