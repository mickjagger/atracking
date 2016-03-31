package google_api;

import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.tracking.TService;
import com.google.tracking.constants.TrackingConstants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ApiConnectionListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static String TAG = "ApiConnectionListener";
    private GoogleApiClient mGoogleApiClient;
    private Boolean _isConnected = false;
    private TService _controller;
    private Location _prevLocation;

    public ApiConnectionListener() {

    }

    public Boolean isConnected() {
        return _isConnected;
    }

    public void setController(TService s) {
        _controller = s;
    }

    public void setClient(GoogleApiClient api) {
        mGoogleApiClient = api;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        _isConnected = true;

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d(TAG, String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude()));
            saveLocation(mLastLocation);
            _controller.createLocationRequest();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended " + i);
        _isConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged " + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        saveLocation(location);
    }

    private void saveLocation(Location location) {
        if(_prevLocation != null)
        {
            if (_prevLocation.getLatitude() == location.getLatitude()
                    && _prevLocation.getLongitude() == location.getLongitude())
            {
                return;
            }
        }
        _prevLocation = location;
        String content = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())+";";

        File sampleDir = new File(Environment.getExternalStorageDirectory(), TrackingConstants.FILES_PATH);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }


        File file = new File(Environment.getExternalStorageDirectory(), TrackingConstants.FILES_PATH + "/" +
                TrackingConstants.LOCATIONS_FILE);

        OutputStream out = null;

        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            out = new BufferedOutputStream(new FileOutputStream(file, true));

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();
            out.write(contentInBytes);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

    }
}