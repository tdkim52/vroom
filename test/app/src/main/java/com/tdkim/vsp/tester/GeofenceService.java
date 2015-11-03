package com.tdkim.vsp.tester;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import android.app.PendingIntent;
import android.util.Log;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.text.TextUtils;
import android.media.*;

import com.tdkim.vsp.tester.R;

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

        // get transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) ||
                (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) ||
                (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            // notification would go here
            Log.v(TAG, geofenceTransitionDetails);

            // replace hashmap with passed in hashmap
            String geoType;
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                for (Geofence geofence : triggeringGeofences) {
		            /* replace with getter function once singleton */
                    /* xyzzy - poor implementation, no need to run at O(N^2), currently uses unique hashmap for each key */
                    for (HashMap<String, String> hazard : GooglePlayServicesActivity.geoList) {
                        if ((geoType = hazard.get(geofence.getRequestId())) != null) {
                            char direction = geoType.charAt(geoType.length()-1);
                            geoType = geoType.substring(0, geoType.length()-2);
                            if (direction == 'N' && GooglePlayServicesActivity.lastKnownDirection.charAt(0) == 'N') {
                                playHazardAlert(geoType);
                            }
                            else if (direction == 'E' && GooglePlayServicesActivity.lastKnownDirection.charAt(1) == 'E') {
                                playHazardAlert(geoType);
                            }
                            else if (direction == 'S' && GooglePlayServicesActivity.lastKnownDirection.charAt(0) == 'S') {
                                playHazardAlert(geoType);
                            }
                            else if (direction == 'W' && GooglePlayServicesActivity.lastKnownDirection.charAt(1) == 'W') {
                                playHazardAlert(geoType);
                            }
                            else {
						    /* logical bug if direction is "null", change database to have X indicate no dir specified */
                                if (direction == 'X') {
                                    playHazardAlert(geoType);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            Log.v(TAG, "Error on geofence enter");
        }
    }

	/*
	 * playHazardAlert
	 *
	 * function plays the applicable sound clip notification
     */	
	/* xyzzy - eventually populate with audio library and allow functionality to output combination of clips */
	private void playHazardAlert(String type) {
        /* xyzzy - determine naming desired naming convention, for now do by type */
		//String filepath = "com.tdkim.vsp.tester.R.raw.";
		//filepath = filepath + type;

        int resID = this.getResources().getIdentifier(type, "raw", this.getPackageName());

        /* xyzzy - create cannot accept a string as its second argument
         * need to build it as some sort of resource? or uri?
         */
		try {
            MediaPlayer mp;
            if (resID == 0) {
                mp = MediaPlayer.create(this, R.raw.other);
            }
            else {
                mp = MediaPlayer.create(this, resID);
            }
			mp.start();
		} catch (Exception e) {
			Log.v(TAG, "Sound Playback Error");
		}
	}   

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList<String> triggerGeofencesIdsList =  new ArrayList<>();
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
