package com.fr3estudio.sherpa.sherpav3p;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {


    public static final String TAG = FetchAddressIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;
    public String caller = "";
    public int caller_index = 0;


    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String errorMessage = "";
            String action = intent.getAction();
            caller = intent.getStringExtra(CONSTANTS.CALLER);
            caller_index = intent.getIntExtra(CONSTANTS.CALLER_INDEX,0);
            if (CONSTANTS.ACTION_GEOCODE.equals(action)) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                Location location = intent.getParcelableExtra(CONSTANTS.LOCATION_DATA_EXTRA);
                mReceiver = intent.getParcelableExtra(CONSTANTS.RECEIVER);
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException ioException) {
                    Log.e(TAG, "Service not available");
                } catch (IllegalArgumentException illegalArgumentException) {
                    Log.e(TAG, "ilegal argument");
                }


                if (addresses != null && addresses.size() == 0) {
                    Log.e(TAG, "no addresses found");
                    deliverResultToReceiver(CONSTANTS.FAILURE_RESULT, errorMessage);
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();
                    System.out.println("dirección " + address.toString());
                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                        break;
                    }
                    Log.i(TAG, "address found");
                    deliverResultToReceiver(CONSTANTS.SUCCESS_RESULT,
                            TextUtils.join(System.getProperty("line.separator"),
                                    addressFragments));

                }
            }else if (CONSTANTS.ACTION_DMATRIX.equals(action)){

            }else if (CONSTANTS.ACTION_RGEOCODE.equals(action)){
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                String addressSt = intent.getStringExtra(CONSTANTS.ADDRESS_DATA_EXTRA);
                mReceiver = intent.getParcelableExtra(CONSTANTS.RECEIVER);
                List<Address> addresses = null;
                Log.d(TAG, "calling geocoder ...");
                try {
                    addresses = geocoder.getFromLocationName(addressSt,1);
                } catch (IOException ioException) {
                    Log.e(TAG, "Service not available");
                } catch (IllegalArgumentException illegalArgumentException) {
                    Log.e(TAG, "ilegal argument");
                }

                Log.d(TAG, "done!!!.");
                if (addresses == null || (addresses != null && addresses.size() == 0)) {
                    Log.e(TAG, "no addresses found");
                    deliverResultToReceiver(CONSTANTS.FAILURE_RESULT, errorMessage);
                } else {
                    Address address = addresses.get(0);

                    LatLng result = new LatLng(address.getLatitude(),address.getLongitude());
                    Log.i(TAG, "address found");
                    deliverResultToReceiver(CONSTANTS.SUCCESS_RESULT,result
                            );

                }

            }
        }
    }
    private void deliverResultToReceiver(int resultCode, LatLng position){
        Bundle bundle = new Bundle();
        bundle.putParcelable(CONSTANTS.RESULT_POS_KEY, position);
        bundle.putString(CONSTANTS.CALLER, caller);
        bundle.putInt(CONSTANTS.CALLER_INDEX, caller_index);

        mReceiver.send(resultCode, bundle);

    }
    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(CONSTANTS.RESULT_DATA_KEY, message);
        bundle.putString(CONSTANTS.CALLER, caller);
        bundle.putInt(CONSTANTS.CALLER_INDEX, caller_index);

        mReceiver.send(resultCode,bundle);
    }

}
