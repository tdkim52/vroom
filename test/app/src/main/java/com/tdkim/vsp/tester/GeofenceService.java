package com.tdkim.vsp.tester;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import android.app.PendingIntent;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceService extends IntentService {

    protected static final String TAG = "GeofenceService";

    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.v(TAG, "Error on geofence intent");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            // notification would go here
            Log.v(TAG, geofenceTransitionDetails);
        }
        else {
            Log.v(TAG, "Error on geofence enter");
        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList triggerGeofencesIdsList =  new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggerGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggerGeofencesIdsList);
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered Geofence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return  "Exited Geofence";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "Dwell";
            default:
                return "Unknown";
        }
    }

}