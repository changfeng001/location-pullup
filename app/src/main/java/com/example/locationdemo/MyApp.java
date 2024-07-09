package com.example.locationdemo;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MyApp extends Application {
    private static final String TAG = "MyApp";

    int refCount = 0;

    // 将在 onCreate 中被初始化
    private LocationManager locationManager;
    // 接受位置更新的监听器
    protected final LocationListener locationListener =
            new LocationListener() {

                // 当位置发生变化时，输出位置信息
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Location changed to: " + getLocationInfo(location));
                }

                public void onProviderDisabled(String provider) {
                    Log.d(TAG, provider + " disabled.");
                }

                public void onProviderEnabled(String provider) {
                    Log.d(TAG, provider + " enabled.");
                }

                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                    Log.d(TAG, provider + " status changed.");
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                refCount++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                refCount--;
                if (refCount == 0) {
                    startIntent();
                }

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                    @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    private void startIntent() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 获取 LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = null;

        List<String> providerList = locationManager.getProviders(true);
        if (providerList != null && providerList.size() > 0) {
            if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                provider = LocationManager.GPS_PROVIDER;
            }
        }

        Log.v(TAG, "provider = " + provider);

        long minTimeMs = 1000;
        float minDistanceM = 0.0f;
        int requestCode = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocationReceiver locationReceiver = new LocationReceiver(this, requestCode);
            try {
                locationManager.requestLocationUpdates(provider, minTimeMs, minDistanceM,
                        locationReceiver.getPendingIntent());
                locationManager.requestFlush(provider, locationReceiver.getPendingIntent(),
                        requestCode);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "CurrentProvider: " + provider);
        // 获取 Provider 最后一个记录的地址信息
        Location lastKnownLocation = locationManager
                .getLastKnownLocation(provider);
        if (lastKnownLocation != null) {
            Log.d(TAG, "LastKnownLocation: "
                    + getLocationInfo(lastKnownLocation));
        } else {
            Log.d(TAG, "Last Location Unkown!");
        }
    }

    /**
     * 将 Location 对象转换成字符串形式方便显示
     *
     * @param location
     *            Location 对象
     * @return 字符串形式的表示
     */
    private String getLocationInfo(Location location) {
        String info = "";
        info += "Longitude:" + location.getLongitude();
        info += ", Latitude:" + location.getLatitude();
        if (location.hasAltitude()) {
            info += ", Altitude:" + location.getAltitude();
        }
        if (location.hasBearing()) {
            info += ", Bearing:" + location.getBearing();
        }
        return info;
    }
}
