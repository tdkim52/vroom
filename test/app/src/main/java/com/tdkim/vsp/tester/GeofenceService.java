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
                for (Geofence geofence : triggeringGeofences.size()) {
                    if ((geoType = GooglePlayServicesActivity.geoList.get(geofence.getRequestId())) != null) {
                        char direction = geoType.charAt(geoType.length()-1);
                        geoType = geoType.substring(0, geoType.length()-2);
                        if (direction == 'N') {

                        }
                        else if (direction == 'E') {

                        }
                        else if (direction == 'S') {

                        }
                        else if (direction == 'W') {

                        }
                        else {

                        }
                    }
                }
                try {
                    MediaPlayer mp = MediaPlayer.create(this, com.tdkim.vsp.tester.R.raw.harzard);
                    mp.start();
                } catch (Exception e) {
                    Log.v(TAG, "Sound Error");
                }
            }
//            else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
//                } catch (Exception e) {
//                    Log.v(TAG, "Sound Error");
//                }
//            }
//            else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
//                } catch (Exception e) {
//                    Log.v(TAG, "Sound Error");
//                }
//            }
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
