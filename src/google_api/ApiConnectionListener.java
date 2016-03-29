package google_api;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.tracking.TService;

public class ApiConnectionListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private static String TAG = "ApiConnectionListener";
    private GoogleApiClient mGoogleApiClient;
    private Boolean _isConnected = false;
    private TService _controller;

    public ApiConnectionListener() {

    }

    public Boolean isConnected(){
        return _isConnected;
    }
    public void setController(TService s){
        _controller = s;
    }
    public void setClient(GoogleApiClient api){
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
    }
}
