package com.example.proyecto;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class odometro extends Service {
    public odometro() {
    }


    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;
    private double totalDistance = 0.0;

    private final IBinder binder = new OdometerBinder();


    public class OdometerBinder extends Binder {
        odometro getOdometer() {
            return odometro.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getDistanciaMetros(){
        return totalDistance;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient=null;
        lastLocation=null;
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); // Intervalo en milisegundos para obtener actualizaciones de ubicación
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location currentLocation = locationResult.getLastLocation();
                    if (lastLocation != null) {
                        // Calcular la distancia entre las ubicaciones anteriores y actuales
                        float[] distance = new float[1];
                        Location.distanceBetween(
                                lastLocation.getLatitude(), lastLocation.getLongitude(),
                                currentLocation.getLatitude(), currentLocation.getLongitude(),
                                distance);

                        // Actualizar la distancia total
                        totalDistance += distance[0] / 1000.0; // Convertir a kilómetros
                    }
                    lastLocation = currentLocation;
                }
            }
        }, null);
    }
}