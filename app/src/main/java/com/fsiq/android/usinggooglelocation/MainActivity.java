package com.fsiq.android.usinggooglelocation;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PendingIntent mGeofencePendingIntent;
    GeofencingRequest mGeofencingRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if(!checkFinePermissions())
            requestFinePermissions();
        else {
            prepareLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        mGoogleApiClient.connect();



        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private boolean checkFinePermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        boolean per = (permissionState == PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "Has Permission: " + per);
        return per;
    }

    private void requestFinePermissions() {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            Log.d(TAG, "right request code");
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Log.d(TAG, "Permission granted");
                prepareLocation();
            }
            else
                Log.d(TAG, "Permission NOT granted");
        }else
            Log.d(TAG, "did not receive right answer code");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void prepareLocation(){
        Log.d(TAG, "Prepare location");
        getLastLocation();
        setLocationRequest();
        getLocationUpdates();
        createGeofence();
    }

    private void createGeofence(){
        Geofence geofence = new Geofence.Builder()
                .setRequestId("oi")
                .setCircularRegion((double)-23.6236, (double)-46.7001, 5)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration((long)10)
                .build();
        ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
        geofenceList.add(geofence);

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        mGeofencingRequest = builder.build();

        getGeofencePendingIntent();

    }
    @SuppressWarnings("MissingPermission")
    private void getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        //if (mGeofencePendingIntent != null) {
        //    return mGeofencePendingIntent;
        //}
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                mGeofencingRequest,
                mGeofencePendingIntent
        ).setResultCallback(MainActivity.this);


    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        Log.d(TAG, "Get Last Location");
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(loc != null)
            printLocation(loc);
    }

    private void setLocationRequest(){
        Log.d(TAG, "set location request");
        mLocationRequest = new LocationRequest()
                .setInterval(10 * 1000)
                .setFastestInterval(5 * 1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @SuppressWarnings("MissingPermission")
    private void getLocationUpdates(){
        Log.d(TAG, "get location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override

            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged");
                printLocation(location);

            }
        });
    }

    private void printLocation(Location location){
        Log.d(TAG, "print location");
        TextView tv = (TextView)findViewById(R.id.tv_unico);
        String str =
                "Provider: " + location.getProvider() + "  \n" +
                "Accuracy: " + location.getAccuracy() + "  \n" +
                "Latitude: " + location.getLatitude() + "  \n" +
                "Longitud: " + location.getLongitude() + " \n" ;

        tv.setText(str);

    }


    @Override
    public void onResult(@NonNull Result result) {
        Toast.makeText(this, "onResult!!!!", Toast.LENGTH_LONG);
        Log.d(TAG, "onResult");
    }
}
