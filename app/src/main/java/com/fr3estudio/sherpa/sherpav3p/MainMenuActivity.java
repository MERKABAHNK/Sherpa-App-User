package com.fr3estudio.sherpa.sherpav3p;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MainMenuActivity extends AppCompatActivity  implements DialogInterface.OnClickListener  {


    private int is_overlay_shown = 0;
    private Intent overlayIntent;
    private int id_cliente;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        } else if (requestCode == CONSTANTS.CLOSE_SESSION) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("close_session")
                    && data.getExtras().getBoolean("close_session")) {
                onCloseSession();
                return;
            }
        }else if (requestCode == CONSTANTS.MENU_OVARLAY ){
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

                        if (response.getStatus() == 0) {
                            hideOverlay();
                            showDialog(
                                    getResources().getString(
                                            R.string.msg_title_success),
                                    getResources().getString(
                                            R.string.msg_detail_infoSaved), false,
                                    false);

                        } else {
                            hideOverlay();
                            showDialog(
                                    getResources().getString(R.string.msg_serverError_title),
                                    getResources()
                                            .getString(R.string.msg_detail_info_not_Saved),
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
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("close_session")
                    && data.getExtras().getBoolean("close_session")) {
                onBackPressed();
                return;
            }
        }else if (requestCode == CONSTANTS.CONFIRMATION_OVARLAY){
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
                if (response != null && response.getClientes() != null && response.getClientes().size() > 0) {
                    String nombre = response.getClientes().get(0).getNombre_cliente();
                    String apellido = response.getClientes().get(0).getApellido_cliente();
                    String email = response.getClientes().get(0).getEmail_cliente();
                    String telefono = response.getClientes().get(0).getTelefono_cliente();

                    SharedPreferences settings = MainMenuActivity.this
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
        setContentView(R.layout.activity_main_menu);

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
                                OverlayActivity.SHOW_MENU);


                        startActivityForResult(overlayIntent,
                                CONSTANTS.MENU_OPTION);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);

                    }
                });
        mActionBar.setCustomView(mCustumView);
        mActionBar.setDisplayShowCustomEnabled(true);

        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

        id_cliente = settings.getInt(CONSTANTS.ID_CLIENT,0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        return super.onOptionsItemSelected(item);
    }
    public void newDeliver(View view) {
        Toast.makeText(this, R.string.st_loading_map, Toast.LENGTH_LONG).show();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMapActivity();
            }
        },1000);


    }

    public void startMapActivity() {
        //showLoadingOverlay(getResources().getString(R.string.st_loading_map));
        Intent i = new Intent(this, MapActivity.class);
        startActivityForResult(i, CONSTANTS.CLOSE_SESSION);
        //hideOverlay();
    }
    public void onCloseSession() {

        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3D.UsersData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id_sherpa", "");
        editor.putString(CONSTANTS.ID_CLIENT, "");
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CONSTANTS.CLOSE_SESSION_ST, true);
        setResult(CONSTANTS.CLOSE_SESSION, result);
        MainMenuActivity.this.finish();

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

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onBackPressed() {


        showConfirmation();

    }

    private void showConfirmation() {
        // System.out.println("Showing confirmation.");
        Intent oiIntent = new Intent(this, OverlayActivity.class);
        oiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        oiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        oiIntent.putExtra("code", OverlayActivity.CONFIRMATION);
        oiIntent.putExtra("title",
                getResources().getString(R.string.st_title_closeSessionQ));
        oiIntent.putExtra("subtitle",
                getResources().getString(R.string.st_subtitle_closeSessionQ));
        startActivityForResult(oiIntent, CONSTANTS.CONFIRMATION_OVARLAY);
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


}
