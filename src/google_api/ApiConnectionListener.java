package google_api;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class ApiConnectionListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = "ApiConnectionListener";
    private GoogleApiClient mGoogleApiClient;
    private Boolean _isConnected = false;

    public ApiConnectionListener() {

    }

    public Boolean isConnected(){
        return _isConnected;
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
}
