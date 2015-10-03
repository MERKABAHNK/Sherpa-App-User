package com.fr3estudio.sherpa.sherpav3p;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.Destination;
import com.fr3estudio.sherpa.sherpav3p.utils.ObjectSerializer;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;

public class LoginActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    boolean internet_access;
    private int is_overlay_shown = 0;
    private Intent overlayIntent;
    private boolean service_canceled;

    private Handler h;
    private Runnable r;

    private int id_cliente;
    private int service_id;
    private int sherpa_id;
    private String token;
    double total_price = 0;

    double total_len = 0;

    private ArrayList<Destination> destinations;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result " + requestCode);
        if (requestCode == CONSTANTS.MENU_OPTION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("menu_option")) {
                String menu = data.getStringExtra("menu_option");
                if (menu.equals("about") || menu.equals("terms")) {
                    showInfo(menu);
                }
            }
        }
    }


    public void showInfo(String type) {

        System.out.println("show info");
        Intent i = new Intent(this, MenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("code", type);

        startActivityForResult(i, CONSTANTS.MENU_OVARLAY);
    }

    private void cancelUpdates() {

        if (h != null) {
            h.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i(TAG, this.getSupportActionBar().toString());
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustumView = mInflater.inflate(R.layout.menu_action_bar, null);
        ((Button) mCustumView.findViewById(R.id.show_menu_ma))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("mostrar el creado desde el main.");
                        is_overlay_shown++;
                        overlayIntent = new Intent(v.getContext(),
                                OverlayActivity.class);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        overlayIntent.putExtra("code",
                                OverlayActivity.SHOW_MAIN_MENU);


                        startActivityForResult(overlayIntent,
                                CONSTANTS.MENU_OPTION);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);

                    }
                });
        mActionBar.setCustomView(mCustumView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private boolean isConnectedToWWW() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // continue
            internet_access = true;
        } else {
            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_serverError_title),
                    getResources().getString(R.string.msg_connectionError),
                    false, this, getResources().getString(R.string.st_accept));
            return false;
        }

        return true;
    }

    public void loginAccount(View view) {
        if (!isConnectedToWWW()) {
            return;
        }
        showLoadingOverlay(getResources().getString(R.string.st_connecting));
        EditText user = (EditText) findViewById(R.id.user_f);
        EditText passwd = (EditText) findViewById(R.id.passwd_f);
        if (user.getText().toString().trim().isEmpty()
                || passwd.getText().toString().trim().isEmpty()) {
            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_login_title),
                    getResources().getString(R.string.msg_detail_noUsrPswd),
                    false, this, getResources().getString(R.string.st_accept));
            hideOverlay();
            return;
        }

        ArrayList<String> param = new ArrayList<>();
        param.add(user.getText().toString());
        param.add(passwd.getText().toString());


        RestClient.get().executeCommand("1", param.toString(), new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                int credit = 0;
                if (response.getClientes() != null && response.getClientes().size() > 0) {
                    id_cliente = 0;

                    try {
                        id_cliente = Integer.parseInt(response.getClientes().get(0).getId_cliente());
                    } catch (NumberFormatException e) {
                        hideOverlay();
                        showDialog(
                                getResources().getString(
                                        R.string.msg_savingSession_title),
                                getResources().getString(
                                        R.string.msg_detail_internal), false,
                                false);
                        return;
                    }
                    saveClientId(id_cliente);
                    checkForPendingService();
                    ((EditText) findViewById(R.id.passwd_f)).setText("");
                } else {
                    hideOverlay();
                    showDialog(
                            getResources().getString(R.string.msg_login_title),
                            getResources()
                                    .getString(R.string.msg_userpasswrong),
                            false, false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Error [" + error + "]");

                hideOverlay();
                showDialog(
                        getResources().getString(
                                R.string.msg_serverError_title),
                        getResources().getString(
                                R.string.msg_serverError), false, false);
            }
        });


    }

    public void saveClientId(int id) {
        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CONSTANTS.ID_CLIENT, id);
        editor.commit();
    }

    private void checkForPendingService() {
        if (!isConnectedToWWW()) {
            return;
        }
        ArrayList<String> param = new ArrayList<>();
        param.add(id_cliente + "");
        RestClient.get().executeCommand("3", param.toString(), new Callback<Response>() {

            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (response.getDiligencias() != null && response.getDiligencias().size() > 0) {
                    // hay un resultado ....
                    service_id = Integer.parseInt(response.getDiligencias().get(0).getId_diligencia());
                    token = response.getDiligencias().get(0).getToken_actualizaciones();

                    total_price = Double.parseDouble(response.getDiligencias().get(0).getCosto());

                    String sherpa = response.getDiligencias().get(0).getId_sherpa();
                    try {
                        sherpa_id = Integer.parseInt(sherpa);
                    } catch (NumberFormatException e) {
                        // sherpa no asignado aun ... waiting for sherpa ...
                        sherpa_id = 0;
                    }
                    checkPointsForPendingService();


                } else {
                    hideOverlay();
                    showMainMenu();
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

    private void checkPointsForPendingService() {


        RestClient.get().executeCommand("4", "[" + service_id + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                destinations = new ArrayList<>();


                if (response.getPuntos() != null && response.getPuntos().size() > 0) {
                    total_len = 0;
                    for (int i = 0; i < response.getPuntos().size(); i++) {
                        double lat = Double.parseDouble(response.getPuntos().get(i).getLatitud());
                        double lon = Double.parseDouble(response.getPuntos().get(i).getLongitud());

                        String addr = response.getPuntos().get(i).getDireccion();
                        String detail = response.getPuntos().get(i).getObservaciones();
                        String distance = response.getPuntos().get(i).getDistancia();


                        double len;
                        try {
                            len = Double.parseDouble(distance);
                        } catch (NumberFormatException e) {
                            System.out.println("by passed " + e);
                            len = 0;
                        }
                        total_len += len;
                        destinations.add(new Destination(lat, lon, addr, true, 0,
                                len, detail));
                    }

                }

                String data = "";
                try {
                    data = ObjectSerializer.serialize(destinations);
                } catch
                        (IOException e) {
                    e.printStackTrace();
                }
                saveDestinations(data);


                hideOverlay();

                if (sherpa_id == 0) {
                    /*
                     * SherpaDialog.showDialog("Informacion",
					 * "Existe una diligencia no asiganada aon", false, this);
					 */
                    showWaitingForSherpa(true);

                } else {

                    showServiceInAction();
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

    private void showServiceInAction() {

        Intent i = new Intent(this, ServiceInActionActivity.class);
        i.putExtra(CONSTANTS.ID_CLIENT, id_cliente);
        i.putExtra(CONSTANTS.ID_SERVICE, service_id);
        i.putExtra(CONSTANTS.ID_SHERPA, sherpa_id);
        // i.putExtra(CONSTANTS.DESTINATIONS_NAME, destinations);
        String data = "";
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
    }
    private void showWaitingForSherpa(boolean ServiceInserted){

        Intent i = new Intent(this, WaitingForSherpaActivity.class);
        i.putExtra(CONSTANTS.INSERT_SERVICE, ServiceInserted);
        i.putExtra(CONSTANTS.ID_CLIENT, id_cliente);
        //i.putExtra(CONSTANTS.DESTINATIONS_NAME, destinations);
        String data = "";
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
    }


    private void saveDestinations(String data) {
        SharedPreferences settings = this.getSharedPreferences("com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("service", data);

    }

    private void showMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
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

    public void rememberPasswd(View v) {
        if (!isConnectedToWWW()) {

            return;
        }

        EditText user = (EditText) findViewById(R.id.user_f);

        if (user.getText().toString().trim().isEmpty()) {
            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_login_title),
                    getResources().getString(R.string.msg_detail_remNoUsr),
                    false, this, getResources().getString(R.string.st_accept));
            return;
        }

        showLoadingOverlay(getResources().getString(R.string.st_connecting));

        final String userF = user.getText().toString();

        RestClient.get().rememberPassword("userF", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                if (response != null && response.getStatus()!= 0) {
                    showDialog(
                            getResources().getString(
                                    R.string.msg_auth_title),
                            getResources().getString(
                                    R.string.msg_detail_notReg), false,
                            false);
                }else if (response != null && response.getStatus()!= 0){
                    showDialog(
                            getResources().getString(
                                    R.string.msg_title_rememberPswd),
                            getResources().getString(
                                    R.string.msg_detail_emailSent), false,
                            false);
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


    public void createAccount(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
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
