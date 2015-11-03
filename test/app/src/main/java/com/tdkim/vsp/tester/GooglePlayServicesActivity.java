package com.tdkim.vsp.tester;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.Geofence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooglePlayServicesActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "GPSActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    private LocationRequest mLocationRequest;

    private static final String PACKAGE_NAME = "com.tdk.vsp.tester";
    private static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    private static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000; // 12 hours
    public static final float GEOFENCE_RADIUS_IN_METERS = 50;

    public ArrayList<HashMap<String, LatLng>> hazardList = new ArrayList<>();
    
    /* make these available in the GeofenceService */
    /* encapsulate variables and create getters and setters */
    public static ArrayList<HashMap<String, String>> geoList = new ArrayList<>();
    public static LatLng lastKnownLocation;
    public static String lastKnownDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(5 * 1000);
        super.onCreate(savedInstanceState);

        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(GEOFENCES_ADDED_KEY, false);
        //String test = Integer.toString(hazardList.size());
        //Log.v(TAG, Integer.toString(hazardList.size()));
        try {
            new LoadAllHazards().execute().get();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        populateGeofenceList();
        //Log.v(TAG, Integer.toString(hazardList.size()));
        //Log.v(TAG, mGeofenceList.get(0).getRequestId());
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();


    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }*/
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        // TODO: Start making API requests.
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        handleNewLocation(mLastLocation);

        //geoHash = getNewGeofences(mLastLocation);
        //while !end of geoArray:
            //PopulateGeofenceList(geoHash) --rewrite to take in Hashmap, instead of builtin
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        );
        //startService(new Intent(this, GeofenceService.class));
    }


    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ProgressDialog pDialog;

    class LoadAllHazards extends AsyncTask<String, String, String> {
        //ArrayList<HashMap<String, LatLng>> hazardList = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GooglePlayServicesActivity.this);
            pDialog.setMessage("Loading hazards.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            //hazardList = null;
            //JSONParser jsonParser = new JSONParser();
            String url_AllHazards = "http://api.tdkim.com/hazards.php";
            final String TAG_SUCESS = "success";
            final String TAG_HAZARD = "hazard";
            final String TAG_ID = "id";
            final String TAG_TYPE = "type";
            final String TAG_LATITUDE = "latitude";
            final String TAG_LONGITUDE = "longitude";
            final String TAG_DIRECTION = "direction";
            final String TAG_MESSAGE = "message";

            JSONArray hazards = null;
            String result = null;
            HttpURLConnection con = null;
            //Log.v(TAG, "XXX");

            try {
               /* change lat long to use user location */
                URL url = new URL("http://api.tdkim.com/hazards.php?latitude=49&longitude=-122");
                con = (HttpURLConnection) url.openConnection();
                BufferedReader reader = null;
                InputStream is = con.getInputStream();
                //Log.v(TAG, "VVV");
                InputStreamReader isr = new InputStreamReader(is);
                //Log.v(TAG, "RRR");
                reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line = "";
                //Log.v(TAG, "OOO");
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                //Log.v(TAG, "ONE");
                JSONObject json = new JSONObject(result);
                int success = json.getInt(TAG_SUCESS);
                if (success == 1) {
                    //Log.v(TAG, "TWO");
                    hazards = json.getJSONArray(TAG_HAZARD);
                    for (int i = 0; i < hazards.length(); i++) {
                        //Log.v(TAG, "THREE");
                        JSONObject h = hazards.getJSONObject(i);
                        String id = h.getString(TAG_ID);
                        String type = h.getString(TAG_TYPE);
                        Double lat = h.getDouble(TAG_LATITUDE);
                        Double lon = h.getDouble(TAG_LONGITUDE);
                        /* xyzzy - make sure proper conversion is happening */
                        char dir = (char)h.getInt(TAG_DIRECTION);
                        type = type + dir;
                        HashMap<String, LatLng> map = new HashMap<>();
                        HashMap<String, String> geo = new HashMap<>();
                        map.put(id, new LatLng(lat,lon));
                        geo.put(id, type);
                        //Log.v(TAG, "FOUR");
                        hazardList.add(map);
                        geoList.add(geo);
                        Log.v(TAG, type);
                    }

                }
                else {
                    Log.v(TAG, "not successful");
                    return null;
                }
            }
            catch (Exception ex) {
                //Log.v(TAG, ex.getMessage());
                ex.printStackTrace();
            }
            finally {
                con.disconnect();
            }
            return null;
            //return hazardList;
        }

        protected void onPostExecute(String url) {
            pDialog.dismiss();
        }
    }

    public void populateGeofenceList() {
//        HashMap<String, LatLng> hazards = new HashMap<String, LatLng>();
//        hazards.put("Stairs", new LatLng(48.733343,-122.486056));
//        hazards.put("AW", new LatLng(48.732528, -122.486627));
//        hazards.put("Triangle", new LatLng(48.734944, -122.485932));

        //new LoadAllHazards().execute();

        //ArrayList<HashMap<String, LatLng>> hazardList = getHazards();
        if (hazardList.size() > 0) {
            for (int i = 0; i < hazardList.size(); i++){
                //HashMap<String, LatLng> hazard = hazardList.get(i);
                for (Map.Entry<String, LatLng> h : hazardList.get(i).entrySet()) {          
                  
                    mGeofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(h.getKey())

                                    // Set the circular region of this geofence.
                            .setCircularRegion(
                                    h.getValue().latitude,
                                    h.getValue().longitude,
                                    GEOFENCE_RADIUS_IN_METERS
                            )

                            .setLoiteringDelay(1000 * 5)

                                    // Set the expiration duration of the geofence. This geofence gets automatically
                                    // removed after this period of time.
                            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                                    // Set the transition types of interest. Alerts are only generated for these
                                    // transition. We track entry and exit transitions in this sample.
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)

                                    // Create the geofence.
                            .build());
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        double lat;
        double lon;
        String direction;

        Log.v(TAG, location.toString());

        lat = location.getLatitude();
        lon = location.getLongitude();

        if (lastKnownLocation.latitude < lat) {
            direction = "N";
        }
        else {
            direction = "S";
        }

        if (lastKnownLocation.longitude < lon) {
            direction = direction + "E";
        }
        else {
            direction = direction + "W";
        }

        lastKnownDirection = direction;
        lastKnownLocation = new LatLng(lat,lon);
    }
     
   /* private void getNewGeofences(Location location){
        //login
        //send lat/long
        //get hazard message
        //parse into array?
        //return array
    }*/
}
