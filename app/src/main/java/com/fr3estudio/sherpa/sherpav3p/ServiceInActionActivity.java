package com.fr3estudio.sherpa.sherpav3p;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.CONSTANTS;
import com.fr3estudio.sherpa.sherpav3p.utils.Destination;
import com.fr3estudio.sherpa.sherpav3p.utils.ObjectSerializer;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;

public class ServiceInActionActivity extends AppCompatActivity   implements DialogInterface.OnClickListener {

    public static final String TAG = MapActivity.class.getSimpleName();

    private int service_id = 0;
    private int id_cliente = 0;
    private int is_overlay_shown = 0;
    private Intent overlayIntent;
    private String sherpa_id = "";

    private ArrayList<Destination> destinations;

    private Handler h;
    private Runnable r;

    private int number_stars = 0;

    ArrayList<LinearLayout> destinationsGUI = null;

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

                    SharedPreferences settings = ServiceInActionActivity.this
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
        setContentView(R.layout.activity_service_in_action);
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

        h = new Handler();
        r = new Runnable() {

            @Override
            public void run() {

            }
        };

        //destinations = this.getIntent().getParcelableExtra(CONSTANTS.DESTINATIONS_NAME);
        SharedPreferences settings = this.getSharedPreferences(
                "com.fr3estudio.sherpaV3P.UsersData", Context.MODE_PRIVATE);

        String data = settings.getString(CONSTANTS.DESTINATIONS_NAME, "");


        try {
            destinations = (ArrayList<Destination>) ObjectSerializer.deserialize(data);
        } catch (IOException e) {
            e.printStackTrace();
            showDialog(getResources().getString(
                            R.string.msg_savingData_title),
                    getResources().getString(
                            R.string.msg_detail_internal), false, false);
            destinations = new ArrayList<>();
        }
        id_cliente = this.getIntent().getIntExtra(CONSTANTS.ID_CLIENT, 0);
        service_id = this.getIntent().getIntExtra(CONSTANTS.ID_SERVICE, 0);
        sherpa_id = this.getIntent().getStringExtra(CONSTANTS.ID_SHERPA);


        ((Button) findViewById(R.id.contact_bt))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        contactSherpaAdmin();

                    }
                });

        h = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                queryUpdate();
            }
        };

        ((Button) findViewById(R.id.button6)).setEnabled(true);

        ((TextView) findViewById(R.id.service_code)).setText(getResources()
                .getString(R.string.st_service_code) + service_id);

        // cargar los puntos ...
        destinationsGUI = new ArrayList<>();
        LayoutInflater li = LayoutInflater.from(this);
        System.out.println("habian antes "
                + ((LinearLayout) findViewById(R.id.destinatios_layout))
                .getChildCount() + " Views");
        ((LinearLayout) findViewById(R.id.destinatios_layout)).removeAllViews();
        for (int i = 0; i < destinations.size(); i++) {
            LinearLayout tmpButton = (LinearLayout) li.inflate(
                    R.layout.destination_row, null, false);
            if (i == 0) {
                ((Button) tmpButton.findViewById(R.id.button1)).setText("O: "
                        + destinations.get(i).address);
				/*
				 * System.out.println("esta tiene " + ((Button)
				 * tmpButton.findViewById(R.id.button1)) .getBackground());
				 */
            } else {
                ((Button) tmpButton.findViewById(R.id.button1)).setText("D" + i
                        + ": " + destinations.get(i).address);
            }

            destinationsGUI.add(tmpButton);

            ((LinearLayout) findViewById(R.id.destinatios_layout))
                    .addView(tmpButton);

        }

        RestClient.get().executeCommand("13", "["+sherpa_id+"]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                if (response != null && response.getSherpas() != null && response.getSherpas().size() > 0){
                    String name = response.getSherpas().get(0).getNombre_sherpa();
                    String lastn = response.getSherpas().get(0).getApellido_sherpa();
                    ((TextView) findViewById(R.id.sherpa_name_tf))
                            .setText(name + " " + lastn);

                    String qual_prom = response.getSherpas().get(0).getCalificacion_promedio_sherpa();

                    if (qual_prom.trim().equals("0")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars0));
                    } else if (qual_prom.trim().equals("1")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars1));
                    } else if (qual_prom.trim().equals("2")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars2));
                    } else if (qual_prom.trim().equals("3")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars3));
                    } else if (qual_prom.trim().equals("4")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars4));
                    } else if (qual_prom.trim().equals("5")) {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars5));
                    } else {
                        ((ImageView) findViewById(R.id.qualification))
                                .setImageDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.stars0));
                    }

                    String plate = response.getSherpas().get(0).getPlaca_sherpa();
                    ((TextView) findViewById(R.id.vehicle_plate))
                            .setText(plate);

                    String cel = response.getSherpas().get(0).getTelefono_sherpa();
                    ((TextView) findViewById(R.id.cel_phone_sherpa))
                            .setText(cel);

                    ((Button) findViewById(R.id.call_sherpa_bt))
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    placeCallToSherpa(v);
                                }
                            });

                    loadAvatar(response.getSherpas().get(0).getAvatar_sherpa());
                    scheduleUpdateQuery(CONSTANTS.TIME_BETWEEN_SERVICEA_CALLS);
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

    public void scheduleUpdateQuery(int invertval) {
        System.out.println("SCHEDULE FOR " + invertval);
        h.postDelayed(r, invertval);
    }
    private void loadAvatar(String fileName) {

        System.out.println("Loading image " + fileName);
        if (fileName.indexOf(".png") < 0) {
            return;
        }
        ImageView img = ((ImageView) findViewById(R.id.sherpa_avatar));
        Picasso.with(this.getBaseContext()).load(CONSTANTS.avatarServer + fileName).into(img);

    }
    public void placeCallToSherpa(View v1) {
        // System.out.println("Llamar al sherpa ...");
        String cel = ((TextView) findViewById(R.id.cel_phone_sherpa)).getText()
                .toString();
        // System.out.println("hay que llamar al " + cel);

        if (cel != null && !cel.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + cel));
            try {
                startActivity(intent);
            }catch (SecurityException e){
                showDialog(
                        getResources().getString(
                                R.string.msg_Error_title),
                        getResources().getString(
                                R.string.msg_detail_noPermission), false, false);
            }
        }
    }

    public void queryUpdate() {


        RestClient.get().executeCommand("14", "[" + service_id + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (response != null && response.getPuntos() != null && response.getPuntos().size() > 0) {
                    for (int i = 0; i < response.getPuntos().size(); i++) {
                        String point = response.getPuntos().get(i).getPunto_Key();
                        int index = Integer.parseInt(point.split("to")[1]);
                        String state = response.getPuntos().get(i).getId_estado_punto().trim();
                        System.out.println("stado : " + state + " - "
                                + destinationsGUI.size());
                        if (state.trim().equals("2")) {
                            if (destinationsGUI.size() > index) {
                                System.out
                                        .println("cambiando el estado");
                                destinationsGUI
                                        .get(index)
                                        .findViewById(R.id.button1)
                                        .setBackgroundResource(
                                                R.drawable.bg_dest_check_on3);
                            }
                            if (index == destinationsGUI.size() - 1) {
                                System.out
                                        .println("todos los servicios ...");
                                showQualification();
                                updateServiceCompleted();
                                return;
                            }
                        }
                    }
                }
                scheduleUpdateQuery(CONSTANTS.TIME_BETWEEN_SERVICEA_CALLS);
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

    public void updateServiceCompleted() {
        RestClient.get().executeCommand("15", "[" + service_id + "]", new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {

                Log.i(TAG, "updateServiceCompleted!!!");
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

    public void onestar(View v) {
        ((Button) findViewById(R.id.one_star_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.two_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.three_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.four_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.five_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        number_stars = 1;
    }

    public void twostars(View v) {
        ((Button) findViewById(R.id.one_star_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.two_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.three_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.four_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.five_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        number_stars = 2;
    }

    public void threestars(View v) {
        ((Button) findViewById(R.id.one_star_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.two_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.three_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.four_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        ((Button) findViewById(R.id.five_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        number_stars = 3;
    }

    public void fourstars(View v) {
        ((Button) findViewById(R.id.one_star_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.two_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.three_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.four_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.five_stars_bt))
                .setBackgroundResource(R.drawable.as_star_off);
        number_stars = 4;
    }

    public void fivestars(View v) {
        ((Button) findViewById(R.id.one_star_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.two_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.three_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.four_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        ((Button) findViewById(R.id.five_stars_bt))
                .setBackgroundResource(R.drawable.as_star_on);
        number_stars = 5;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showQualification() {
        findViewById(R.id.service_completed_lo).setVisibility(View.VISIBLE);
        number_stars = 1;
    }

    public void qualifySherpa(View v) {

        ArrayList<String> param = new ArrayList<>();
        param.add(number_stars + "");
        param.add(id_cliente + "");
        param.add(sherpa_id);
        param.add(service_id + "");
        param.add(((TextView) findViewById(R.id.qual_comments)).getText().toString());
        RestClient.get().executeCommand("12", param.toString(), new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                showDialog(
                        getResources().getString(
                                R.string.msg_title_serviceCompleted),
                        getResources().getString(
                                R.string.msg_detail_thaksMsg), false,
                        true);

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
        editor.putString(CONSTANTS.ID_CLIENT, "");
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CONSTANTS.CLOSE_SESSION_ST, true);
        setResult(CONSTANTS.CLOSE_SESSION, result);
        ServiceInActionActivity.this.finish();

    }

    public void contactSherpaAdmin() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { CONSTANTS.contact_mail });
        i.putExtra(Intent.EXTRA_SUBJECT,
                getResources().getString(R.string.st_contact_subj));
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(i,
                    getResources().getString(R.string.st_title_conctact)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ServiceInActionActivity.this,
                    getResources().getString(R.string.st_error_noMsgSvc),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ServiceInActionActivity.this.finish();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
