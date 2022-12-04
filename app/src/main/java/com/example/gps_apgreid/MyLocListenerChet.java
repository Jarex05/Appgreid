package com.example.gps_apgreid;

import android.location.GnssAntennaInfo;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.List;

public class MyLocListenerChet implements LocationListener {
    private LocListenerInterfaceChet locListenerInterfaceChet;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        locListenerInterfaceChet.OnLocationChangedChet(location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void setLocListenerInterfaceChet(LocListenerInterfaceChet locListenerInterfaceChet) {
        this.locListenerInterfaceChet = locListenerInterfaceChet;
    }
}
