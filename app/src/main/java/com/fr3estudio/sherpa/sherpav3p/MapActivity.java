package com.fr3estudio.sherpa.sherpav3p;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fr3estudio.sherpa.sherpav3p.model.DirectionResults;
import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.Destination;
import com.fr3estudio.sherpa.sherpav3p.utils.ObjectSerializer;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MapActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    LinearLayout additional_address_ll;
    private Intent overlayIntent;
    private int id_cliente;
    private boolean service_canceled;

    private int is_overlay_shown = 0;

    static final int STARTED = 0;
    static final int FILLING_FROM = 1;
    static final int FADDRESS_LOCATED = 2;
    static final int FILLING_ADDI = 3;
    static final int SEARCHING_DIR = 4;
    static final int DONE = 4;

    ArrayList<LatLng> positions;
    private ArrayList<Marker> servicesMarkers;
    int counter;
    private ArrayList<View> addressForms;
    private ArrayList<Double> distances;
    private ArrayList<Destination> destinations;

    private ArrayList<Integer> tax_limit = new ArrayList<>();
    private ArrayList<String> tax_range = new ArrayList<>();
    private ArrayList<Integer> tax_charge = new ArrayList<>();
    private ArrayList<Integer> tax_comision = new ArrayList<>();
    private ArrayList<Double> tax_bound = new ArrayList<>();


    private int status = STARTED;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleClient;

    private Handler h;
    private Runnable r;

    public static final String TAG = MapActivity.class.getSimpleName();

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == CONSTANTS.FAILURE_RESULT) {
                System.out.println("no recibió dirección ");
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_noResult),
                        false, false);
                return;
            } else if (resultCode == CONSTANTS.SUCCESS_RESULT && resultData != null) {
                LatLng position = resultData.getParcelable(CONSTANTS.RESULT_POS_KEY);
                System.out.println("oRR>" + position.toString());
                onMapRequestCompleted(position,
                        resultData.getString(CONSTANTS.CALLER), resultData.getInt(CONSTANTS.CALLER_INDEX));
                //showAddress(mAddress);
            } else {
                Log.i(TAG, "No Result Data");
            }

        }
    }

    ;

    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result " + requestCode);
        if (requestCode == CONSTANTS.MENU_OPTION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("menu_option")) {
                String menu = data.getStringExtra("menu_option");
                if (menu.equals("about") || menu.equals("terms")) {
                    showInfo(menu);
                } else if (menu.equals("profile")) {
                    getInfoToShow(menu);
                }

            }
        } else if (requestCode == CONSTANTS.CANCEL_CONFIRMATION_OVARLAY) {
            if (data.getExtras() != null
                    && data.getExtras().containsKey("confirm")
                    && data.getExtras().getBoolean("confirm")) {
                service_canceled = true;
                //cancelUpdates();
                /*final String query = "UPDATE diligencia SET id_estado_diligencia='7' WHERE id_diligencia='"
                        + service_id + "'";

                StringRequest postReq = new StringRequest(Request.Method.POST,
                        CONSTANTS.queryServer, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("[Response]--->" + response);
                        initFrames(false);
                        ((LinearLayout) findViewById(R.id.mainMenuFrame))
                                .setVisibility(View.VISIBLE);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error [" + error + "]");

                        hideOverlay();
                        showDialog(
                                getResources().getString(
                                        R.string.msg_serverError_title),
                                getResources().getString(
                                        R.string.msg_serverError),
                                false, false);
                    }
                })

                {
                    @Override
                    protected Map<String, String> getParams()
                            throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("data", query);

                        return params;
                    }

                };

                postReq.setRetryPolicy(new DefaultRetryPolicy(
                        CONSTANTS.time_out, CONSTANTS.retry_count,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                getRequestQueue(this).add(postReq);*/

            }
        } else if (requestCode == CONSTANTS.MENU_OVARLAY) {
            if (data != null
                    && data.getExtras() != null
                    && data.getExtras().containsKey("update")
                    && data.getExtras().getBoolean("update")) {
                System.out.println("menuOvarlay");
                SharedPreferences settings = this.getSharedPreferences(
                        "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

                // id_cliente = settings.getInt("id_cliente", 0);
                String nombre = settings.getString("nombre", "");
                String apellido = settings.getString("apellido", "");
                String email = settings.getString("email", "");
                String telefono = settings.getString("telefono", "");

                ArrayList<String> param = new ArrayList<>();
                param.add(nombre);
                param.add(apellido);
                param.add(email);
                param.add(telefono);
                param.add(id_cliente + "");

                RestClient.get().executeCommand("6", param.toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {
                        System.out.println("[UpdateQuery]-->" + response.getStatus());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideOverlay();
                        showDialog(
                                getResources().getString(
                                        R.string.msg_serverError_title),
                                getResources().getString(
                                        R.string.msg_serverError), false, false);
                    }
                });

            }
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("close_session")
                    && data.getExtras().getBoolean("close_session")) {
                onBackPressed();
                return;
            }
        } else if (requestCode == CONSTANTS.CLOSE_SESSION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("close_session")
                    && data.getExtras().getBoolean("close_session")) {
                onBackPressed();
                return;
            }
        } else if (requestCode == CONSTANTS.CONFIRMATION_OVARLAY) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("confirm")
                    && data.getExtras().getBoolean("confirm")) {
                onCloseSession();
            }
        }
    }


    public void getInfoToShow(String type) {

        RestClient.get().executeCommand("5", "[" + id_cliente + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (response.getClientes() != null && response.getClientes().size() > 0) {
                    String nombre = response.getClientes().get(0).getNombre_cliente();
                    String apellido = response.getClientes().get(0).getApellido_cliente();
                    String email = response.getClientes().get(0).getEmail_cliente();
                    String telefono = response.getClientes().get(0).getTelefono_cliente();

                    SharedPreferences settings = MapActivity.this
                            .getSharedPreferences(
                                    "com.fr3estudio.sherpaV3P.UsersData",
                                    Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    // editor.putInt("id_cliente", id_cliente);
                    editor.putString("nombre", nombre);
                    editor.putString("apellido", apellido);
                    editor.putString("email", email);
                    editor.putString("telefono", telefono);
                    System.out.println("metiendo " + nombre
                            + apellido + email + telefono);
                    editor.commit();

                    hideOverlay();
                    showInfo("profile");
                } else {

                    hideOverlay();
                    showDialog(
                            getResources().getString(
                                    R.string.msg_serverError_title),
                            getResources().getString(
                                    R.string.msg_serverError), false,
                            false);
                    return;

                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });


    }

    public void showInfo(String type) {

        System.out.println("show info");
        Intent i = new Intent(this, MenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("code", type);

        startActivityForResult(i, CONSTANTS.MENU_OVARLAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        addressForms = new ArrayList<>();
        additional_address_ll = (LinearLayout) findViewById(R.id.additional_destinations);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustumView = mInflater.inflate(R.layout.lblue_action_bar, null);
        mActionBar.setCustomView(mCustumView);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Button) mCustumView.findViewById(R.id.show_menu_mp))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        is_overlay_shown++;
                        hideSoftKeyboard();
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        overlayIntent = new Intent(v.getContext(),
                                OverlayActivity.class);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        overlayIntent.putExtra("code",
                                OverlayActivity.SHOW_MENU);

                        startActivityForResult(overlayIntent,
                                CONSTANTS.MENU_OPTION);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                    }
                });

        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(
                R.id.map);

        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

        id_cliente = settings.getInt(CONSTANTS.ID_CLIENT, 0);

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
       /* mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

        h = new Handler();

        positions = new ArrayList<>();
        servicesMarkers = new ArrayList<>();

        status = FILLING_FROM;
        final int index = -1;
        consultCosts();
        ((TextView) findViewById(R.id.usaAddress))
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // System.out.println("keyPressed " + s);
                        addressEdited(index);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        String addr_query = CONSTANTS.initialZone;


        Intent intent = new Intent(this, FetchAddressIntentService.class );
        intent.putExtra(CONSTANTS.RECEIVER, mResultReceiver);
        intent.putExtra(CONSTANTS.ADDRESS_DATA_EXTRA, addr_query);
        intent.setAction(CONSTANTS.ACTION_RGEOCODE);
        intent.putExtra(CONSTANTS.CALLER, "initialMap");
        startService(intent);
    }

    private void consultCosts() {
        tax_range.add("0-2.5");
        tax_bound.add(2.5d);
        tax_range.add("2.6-4.5");
        tax_bound.add(4.5d);
        tax_range.add("4.6-7");
        tax_bound.add(7d);
        tax_range.add("7.1-9.5");
        tax_bound.add(9.5d);
        tax_range.add("9.6-11");
        tax_bound.add(11d);
        tax_range.add("11.1-13.5");
        tax_bound.add(13.5d);
        tax_range.add("13.6-15");
        tax_bound.add(15d);
        tax_range.add("15.1-Max");
        tax_bound.add(Double.POSITIVE_INFINITY);
        for (int i = 0; i < tax_range.size(); i++) {
            tax_charge.add(0);
            tax_comision.add(0);
        }


        RestClient.get().executeCommand("7", "[]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                for (int i = 0; i < response.getTarfias().size(); i++) {
                    String min = response.getTarfias().get(i).getMinimo();
                    String max = response.getTarfias().get(i).getMaximo();
                    int value = response.getTarfias().get(i).getValor();
                    int com = response.getTarfias().get(i).getComision();
                    if (tax_range.contains(min + "-" + max)) {
                        // System.out.println(tax_range.indexOf(min+"-"+max)+" c: "+value+" "+com);
                        tax_charge.set(
                                tax_range.indexOf(min + "-" + max),
                                value);
                        tax_comision.set(
                                tax_range.indexOf(min + "-" + max),
                                com);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setUpMapIfNeeded();

        //mGoogleClient.connect();
    }

    public void hideSoftKeyboard() {

        View view = findViewById(R.id.mapHolder);

        InputMethodManager imm = (InputMethodManager) MapActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    public void showDialog(String title, String msg, boolean cancel,
                           boolean listen) {
        if (!listen) {
            SherpaDialog.showDialog(title, msg, cancel, this, getResources()
                    .getString(R.string.st_accept));
        } else {
            SherpaDialog.showDialogRes(title, msg, cancel, this, this,
                    getResources().getString(R.string.st_accept));
        }
    }

    public void showLoadingOverlay(String text) {
        is_overlay_shown++;
        overlayIntent = new Intent(this, OverlayActivity.class);
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        overlayIntent.putExtra("code", OverlayActivity.LOADING);
        overlayIntent.putExtra("text2show", text);
        startActivity(overlayIntent);
    }

    public void hideOverlay() {
        if (is_overlay_shown > 0) {
            overlayIntent = new Intent(this, OverlayActivity.class);
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overlayIntent.putExtra("code", OverlayActivity.EXIT);
            startActivity(overlayIntent);
            is_overlay_shown--;
        }
    }

    public void onCloseSession() {

        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3D.UsersData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id_sherpa", "");
        editor.putString("id_cliente", "");
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CONSTANTS.CLOSE_SESSION_ST, true);
        setResult(CONSTANTS.CLOSE_SESSION, result);
        MapActivity.this.finish();

    }

    public void fromNormalAccepted(View v) {
        if (status == FILLING_FROM) {
            // hideSoftKeyboard();
            moveToDirectionFrom();
            status = SEARCHING_DIR;
            return;
        }
        ((LinearLayout) findViewById(R.id.normal_address_from))
                .setVisibility(View.GONE);
        hideSoftKeyboard();
        status = FILLING_ADDI;

        ((Button) findViewById(R.id.addi_dest))
                .setBackgroundResource(R.drawable.bg_from_to_s_on);

        // showAddDestination();
        addNewDestination(v);
    }

    public void moveToDirectionFrom() {
        Log.i(TAG, "searching ...");
        String addr_query = ((EditText) findViewById(R.id.usaAddress))
                .getText().toString() + ", " + CONSTANTS.initialZone;


        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(CONSTANTS.RECEIVER, mResultReceiver);
        intent.putExtra(CONSTANTS.ADDRESS_DATA_EXTRA, addr_query);
        intent.setAction(CONSTANTS.ACTION_RGEOCODE);
        intent.putExtra(CONSTANTS.CALLER, "fromPoint");
        intent.putExtra(CONSTANTS.CALLER_INDEX, 0);
        startService(intent);

        hideOverlay();


    }

    public void onMapRequestCompleted(LatLng pos, String callerName, int index) {

        System.out.println("=========>Map Req completed " + callerName + " " + index);
        int zoomDist = 15;

        if (callerName.equals("initialMap")) {
            zoomDist = 10;
        }
        final int zoom = zoomDist;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            }
        }, 1000);
        if (callerName.equals("fromPoint")) {

            if (positions.size() == 0) {
                positions.add(pos);
            } else if (positions.size() > 0) {
                positions.set(0, pos);
            }
            fromAddressLocated(pos);

        }
        if (callerName.indexOf("addiPoint") >= 0) {

            if (positions.size() == index) {
                positions.add(pos);
            } else if (positions.size() > index) {
                positions.set(index, pos);
            }
            addiAddressLocated(pos, index);

        }
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        Bitmap bitmap = null;
        DisplayMetrics dsplMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dsplMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(dsplMetrics.widthPixels, dsplMetrics.heightPixels);
        view.layout(0, 0, dsplMetrics.widthPixels, dsplMetrics.heightPixels);
        view.buildDrawingCache();
        bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public void fromAddressLocated(LatLng markerPos) {
        String Origin = getResources().getString(R.string.lb_from);
        if (servicesMarkers.size() == 0) {
            mMap.clear();

            View marker_asset = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.marker_text, null);
            ((TextView) marker_asset.findViewById(R.id.marker_text))
                    .setText(Origin);

            Marker tmp = mMap.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(createDrawableFromView(this,
                                    marker_asset))).anchor(0.22f, 1.0f));
            // googleMap.addMarker(new MarkerOptions().position(markerPos));
            servicesMarkers.add(tmp);
        } else {
            servicesMarkers.get(0).setPosition(markerPos);
        }
        ((Button) findViewById(R.id.accept_additional))
                .setBackgroundResource(R.drawable.bt_accept);
        hideSoftKeyboard();
    }

    public void addiAddressLocated(LatLng markerPos, int pos) {
        System.out.println("AddiAddressLocated");
        String dest_lb = getResources().getString(R.string.lb_to);
        int counter = 1;
        for (int j = 0; j < pos; j++) {
            if (addressForms.get(j).getVisibility() == View.VISIBLE) {
                counter++;
            }
        }
		/*
		 * System.out.println("pos: " + counter + "\n # m: " +
		 * servicesMarkers.size());
		 */
        if (servicesMarkers.size() <= counter) {
            View marker_asset = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.marker_text, null);
            ((TextView) marker_asset.findViewById(R.id.marker_text))
                    .setText(dest_lb + " " + (counter));
            Marker tmp = mMap.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(createDrawableFromView(this,
                                    marker_asset))).anchor(0.0f, 1.0f));
            servicesMarkers.add(tmp);
        } else {
            servicesMarkers.get(counter).setPosition(markerPos);
        }
        ((Button) addressForms.get((pos)).findViewById(R.id.accept_additional))
                .setBackgroundResource(R.drawable.bt_accept);

        status = DONE;
        // status = ADDRESS_LOCATED;

    }


    public void addNewDestination(View view) {

        status = FILLING_ADDI;

        ((Button) findViewById(R.id.addi_bt)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.next)).setAlpha(0.5f);
        ((Button) findViewById(R.id.next)).setEnabled(false);
        LayoutInflater li = LayoutInflater.from(this);
        View addressForm = li.inflate(R.layout.additional_destination, null,
                false);

        addressForms.add(addressForm);
        additional_address_ll.addView(addressForm);

        ((TextView) addressForm.findViewById(R.id.add_dest_id)).setText(""
                + counter);

        counter++;
        enumerateDestinations();

        ((Button) addressForm.findViewById(R.id.accept_additional))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (status == FILLING_ADDI) {
                            // ubicar la direccion
                            hideSoftKeyboard();
                            moveToDirectionAddi((View) v.getParent()
                                    .getParent());
                            status = SEARCHING_DIR;
                            return;
                        }

                        ((Button) findViewById(R.id.next)).setEnabled(true);

                        ((Button) findViewById(R.id.addi_bt))
                                .setVisibility(View.VISIBLE);

                        ((View) v.getParent().getParent())
                                .setVisibility(View.GONE);
                        ((TextView) ((View) v.getParent().getParent()
                                .getParent()).findViewById(R.id.isFilled))
                                .setText("1");
                        ((View) v.getParent().getParent().getParent())
                                .findViewById(R.id.addi_dest)
                                .setBackgroundResource(
                                        R.drawable.bg_from_to_del_s_on);
                        ((Button) findViewById(R.id.next)).setAlpha(1.0f);

                        ((Button) findViewById(R.id.next))
                                .setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        /************************************************************* NEXT ************************************************/
										/*
										 * num_dist_received = 0;
										 * expected_dist_receive =
										 * servicesMarkers .size() - 1;
										 */
                                        calculateRoute();
                                    }
                                });
                        hideSoftKeyboard();
                    }
                });

        ((Button) addressForm.findViewById(R.id.addi_dest))
                .setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            // System.out.println("x: " + event.getX());
                            String index_var = ((TextView) ((View) v
                                    .getParent())
                                    .findViewById(R.id.add_dest_id)).getText()
                                    .toString();
                            // System.out.println("->" + index_var);
                            int index = Integer.parseInt(index_var);
                            if (event.getX() < 150) {
                                delDestination(index);
                            } else {
                                showDestination(index);
                            }
                        }
                        return true;
                    }
                });

        final int index_var = counter - 1;
        final View viewHolder = (View) addressForm;
        ((EditText) addressForm.findViewById(R.id.usaAddress))
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        destAddressEdited(viewHolder, index_var);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

/*        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/ArialRoundedMTStd.otf");

        ((Button) addressForm.findViewById(R.id.addi_dest)).setTypeface(tf);
        ((EditText) addressForm.findViewById(R.id.r_name)).setTypeface(tf);
        ((EditText) addressForm.findViewById(R.id.r_last_name)).setTypeface(tf);
        ((EditText) addressForm.findViewById(R.id.add_plat)).setTypeface(tf);
        ((EditText) addressForm.findViewById(R.id.addr_compl)).setTypeface(tf);
        ((EditText) addressForm.findViewById(R.id.r_comments_add_f))
                .setTypeface(tf);

                */
    }

    void enumerateDestinations() {
        String tmp = getResources().getString(R.string.lb_to);
        int counter = 1;
        boolean allSet = true;
        for (int j = 0; j < addressForms.size(); j++) {
            if (addressForms.get(j).getVisibility() == View.VISIBLE) {
                ((Button) addressForms.get(j).findViewById(R.id.addi_dest))
                        .setText(tmp + " " + (counter++));
                String filled = ((TextView) addressForms.get(j).findViewById(
                        R.id.isFilled)).getText().toString();
                if (filled.equals("0")) {
                    allSet = false;
                }
            }
        }
        if (counter == 1) {
            showAddDestination();
        }
        if (allSet) {
            ((Button) findViewById(R.id.next)).setAlpha(1.0f);
            ((Button) findViewById(R.id.next)).setEnabled(true);
        }
    }
    public void showAddDestination() {
        ((Button) findViewById(R.id.addi_bt)).setVisibility(View.VISIBLE);
    }

    public void hideAddDestination() {
        ((Button) findViewById(R.id.addi_bt)).setVisibility(View.GONE);
    }

    public void addressEdited(int index) {
        // System.out.println("cambio la dir: " + index);
        if (status != FILLING_FROM) {
            status = FILLING_FROM;
            ((Button) findViewById(R.id.accept_additional))
                    .setBackgroundResource(R.drawable.bt_search);
        }

    }

    public void moveToDirectionAddi(View v) {

        String addr_query = ((EditText) v.findViewById(R.id.usaAddress))
                .getText().toString() + ", " + CONSTANTS.initialZone;
        final int index = Integer.parseInt(((TextView) v
                .findViewById(R.id.add_dest_id)).getText().toString());

        Intent intent = new Intent(this, FetchAddressIntentService.class );
        intent.putExtra(CONSTANTS.RECEIVER, mResultReceiver);
        intent.putExtra(CONSTANTS.ADDRESS_DATA_EXTRA, addr_query);
        intent.putExtra(CONSTANTS.CALLER_INDEX, index);
        intent.setAction(CONSTANTS.ACTION_RGEOCODE);
        intent.putExtra(CONSTANTS.CALLER, "addiPoint");
        startService(intent);

        hideOverlay();

    }

    void showDestination(int i) {
        // System.out.println("mostrar el item " + i);
        hideAllDestinations();
        addressForms.get(i).findViewById(R.id.normal_address_add)
                .setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.addi_bt)).setVisibility(View.GONE);
    }
    void hideAllDestinations() {
        ((LinearLayout) findViewById(R.id.normal_address_from))
                .setVisibility(View.GONE);
        // isFromVisble = false;
        for (int j = 0; j < addressForms.size(); j++) {
            addressForms.get(j).findViewById(R.id.normal_address_add)
                    .setVisibility(View.GONE);
        }
    }

    void delDestination(int i) {
        // System.out.println("borrar el item " + i);

        int counter = 1;
        for (int j = 0; j < i; j++) {
            if (addressForms.get(j).getVisibility() == View.VISIBLE) {
                counter++;
            }
        }

        if (servicesMarkers.size() > counter) {
            servicesMarkers.get(counter).remove();
            servicesMarkers.remove(counter);
        }

        addressForms.get(i).setVisibility(View.GONE);

        enumerateDestinations();
        enumerateMarkers();
        hideAllDestinations();
        showAddDestination();
    }

    void enumerateMarkers() {
        String tmp = getResources().getString(R.string.lb_to);
        for (int j = 1; j < servicesMarkers.size(); j++) {
            View marker_asset = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.marker_text, null);
            ((TextView) marker_asset.findViewById(R.id.marker_text))
                    .setText(tmp + " " + (j));
            servicesMarkers.get(j).setIcon(
                    BitmapDescriptorFactory.fromBitmap(createDrawableFromView(
                            this, marker_asset)));
        }
    }

    void destAddressEdited(View v, int i) {
        ((Button) v.findViewById(R.id.accept_additional))
                .setBackgroundResource(R.drawable.bt_search);
        status = FILLING_ADDI;
    }
    public void calculateRoute() {
        showLoadingOverlay(getResources().getString(R.string.st_calculating));
        String responseString = "dist";
        distances = new ArrayList<>();
        for (int i = 0; i < servicesMarkers.size() - 1; i++) {
            distances.add(0d);
        }
        for (int i = 0; i < servicesMarkers.size() - 1; i++) {
            String str_origin =
                     servicesMarkers.get(i).getPosition().latitude + ","
                    + servicesMarkers.get(i).getPosition().longitude;

            // Destination of route
            String str_dest =
                     servicesMarkers.get(i + 1).getPosition().latitude + ","
                    + servicesMarkers.get(i + 1).getPosition().longitude;

            final int index = i;
            RestMapClient.get().getJson(str_origin, str_dest, new Callback<DirectionResults>() {
                @Override
                public void success(DirectionResults directionResults, retrofit.client.Response response) {
                    Log.i(TAG, "inside on success" + directionResults.getRoutes().size());
                    System.out.println("hay " + directionResults.getRoutes().size()
                            + " caminos posibles");
                    System.out.println("Distancia " + directionResults.getRoutes().get(0).getLegs().get(0).getDistance().getText());
                    System.out.println("Duración " + directionResults.getRoutes().get(0).getLegs().get(0).getDuration().getText());
                    addDistance(index,directionResults.getRoutes().get(0).getLegs().get(0).getDistance().getValue());
                }

                @Override
                public void failure(RetrofitError error) {
                    hideOverlay();
                    showDialog(
                            getResources().getString(
                                    R.string.msg_serverError_title),
                            getResources().getString(
                                    R.string.msg_serverError),
                            false, false);

                }
            });



                        //addDistance(index, lat);

        }

    }

    public void addDistance(int ind, double distance) {
        distances.add(distance);
        if (distances.size() >= servicesMarkers.size()) {
            // todas las distancias.
            ((View) findViewById(R.id.on_select_menu)).setVisibility(View.GONE);
            ((View) findViewById(R.id.addresses_info)).setVisibility(View.GONE);
            ((View) findViewById(R.id.request_menu_1))
                    .setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.summary_route))
                    .setVisibility(View.VISIBLE);

            double total_dist = 0;
            for (int i = 0; i < distances.size(); i++) {
                total_dist += distances.get(i);
            }

            ((TextView) findViewById(R.id.distanceLabel)).setText(String
                    .format("%.2f", total_dist));
            double price = 0;
            int index = 0;
            // System.out.println(tax_bound.get(index) +" <> "+
            // total_distance);
            while (tax_bound.get(index) < total_dist) {
                index++;
            }
            // System.out.println(index+" cargo: "+tax_charge.get(index)+" + "+tax_comision.get(index));
            price = tax_charge.get(index) + tax_comision.get(index);
            if (index >= tax_bound.size() - 1) {
                price = total_dist * 1000;
            }
            ((TextView) findViewById(R.id.priceLabel)).setText(String.format(
                    "%.2f", price));

            /** --------------CREATE RECEIP ---------------------- **/

            String addr = ((EditText) findViewById(R.id.usaAddress))
                    .getText().toString();
            String addr_query =  addr+ ", " + CONSTANTS.initialZone;
            if (destinations == null){
                destinations = new ArrayList<>();
            }
            if (!destinations.isEmpty()) {
                destinations.clear();
            }
            String service_detail = "O: "
                    + ((EditText) findViewById(R.id.r_service_deatail_f))
                    .getText().toString()
                    + " "
                    + ((EditText) findViewById(R.id.addr_compl)).getText()
                    .toString();
            destinations.add(new Destination(servicesMarkers.get(0)
                    .getPosition().latitude, servicesMarkers.get(0)
                    .getPosition().longitude, addr, true, total_dist,
                    price, service_detail));

            counter = 1;
            for (int j = 0; j < addressForms.size(); j++) {
                if (addressForms.get(j).getVisibility() == View.VISIBLE) {
                    String addr1 = ((EditText) addressForms.get(j)
                            .findViewById(R.id.usaAddress)).getText()
                            .toString();
                    String addr_query1 = addr1
                            + ", " + CONSTANTS.initialZone;
                    String service_detail1 = "D"
                            + (j + 1)
                            + ": "
                            + ((EditText) addressForms.get(j).findViewById(
                            R.id.r_comments_add_f)).getText()
                            .toString()
                            + " "
                            + ((EditText) addressForms.get(j).findViewById(
                            R.id.addr_compl)).getText().toString();
                    destinations.add(new Destination(servicesMarkers.get(
                            counter).getPosition().latitude, servicesMarkers
                            .get(counter).getPosition().longitude, addr1,
                            service_detail1, distances.get(counter - 1)));
                    counter++;
                }
            }
            hideOverlay();
        }
    }

    public void showDetail(View v) {
        ((LinearLayout) findViewById(R.id.mapInfo))
                .setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.detailFrame))
                .setVisibility(View.VISIBLE);
        int max = 0;
        String detail = "";
        for (int i = 0; i < destinations.size(); i++) {
            if (destinations.get(i).address.length() > max) {
                max = destinations.get(i).address.length();
            }
        }
        for (int i = 0; i < destinations.size(); i++) {
            LayoutInflater li = LayoutInflater.from(this);
            View des_pair = li.inflate(R.layout.destination_pair, null, false);
            if (destinations.get(i).isOrigin) {
                ((TextView) des_pair.findViewById(R.id.label))
                        .setText(getResources().getString(R.string.lb_from));
            } else {
                ((TextView) des_pair.findViewById(R.id.label))
                        .setText(getResources().getString(R.string.lb_to) + " "
                                + (i));
            }

            detail = detail + destinations.get(i).service_detail + "\n";

            ((TextView) des_pair.findViewById(R.id.tip_value))
                    .setText(normalizeSize(max, destinations.get(i).address));
            ((LinearLayout) findViewById(R.id.destinationsHolder))
                    .addView(des_pair);
        }

        ((TextView) findViewById(R.id.total_dst_rcp)).setText(destinations
                .get(0).total_length + "");
        ((TextView) findViewById(R.id.total_prc_rcp)).setText(destinations
                .get(0).total_price + "");

        ((TextView) findViewById(R.id.priceLabel_det)).setText(destinations
                .get(0).total_length + "");
        ((TextView) findViewById(R.id.distanceLabel_det)).setText(destinations
                .get(0).total_price + "");
        ((TextView) findViewById(R.id.service_details)).setText(detail);
    }

    public String normalizeSize(int len, String base) {
        String prefix = "";
        for (int i = 0; i < len - base.length(); i++) {
            prefix = " " + prefix;
        }
        return prefix + base;
    }

    public void hideDetail(View v) {

        ((LinearLayout) findViewById(R.id.detailFrame))
                .setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mapInfo))
                .setVisibility(View.VISIBLE);
    }

    public void requestButtonPressed(View v) {
        Intent i = new Intent(this, WaitingForSherpaActivity.class);
        i.putExtra(CONSTANTS.INSERT_SERVICE, true);
        i.putExtra(CONSTANTS.ID_CLIENT, id_cliente);
        String data = "";
        Log.i("Map","guardando "+destinations.size()+" destinos");
        try {
            data = ObjectSerializer.serialize(destinations);
        } catch
                (IOException e) {
            e.printStackTrace();
        }
        //i.putExtra(CONSTANTS.DESTINATIONS_NAME, destinations);
        SharedPreferences settings = this.getSharedPreferences("com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CONSTANTS.DESTINATIONS_NAME, data);
        editor.commit();
        startActivity(i);

        this.finish();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
