package com.fsiq.android.usinggooglelocation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Marina on 08/11/17.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    public GeofenceTransitionsIntentService(){
        super("GeofenceTransitionsIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "errorrrrrr";
            Log.e(TAG, errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.


            // Send notification and log the transition details.
            Toast.makeText(this, "Geofence funcionou!!!!", Toast.LENGTH_LONG);
            Log.i(TAG, "geofence intent");
        } else {
            // Log the error.
            Toast.makeText(this, "Geofence funcionou 2!!!!", Toast.LENGTH_LONG);
        }

    }
}
