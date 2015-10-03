package com.fr3estudio.sherpa.sherpav3p;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.fr3estudio.sherpa.sherpav3p.model.Response;
import com.fr3estudio.sherpa.sherpav3p.utils.OverlayActivity;
import com.fr3estudio.sherpa.sherpav3p.utils.SherpaDialog;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;

public class RegisterActivity extends AppCompatActivity  implements DialogInterface.OnClickListener{

    private int is_overlay_shown = 0;
    private Intent overlayIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    public void onRegisterClicked(View v) {
        String name = ((EditText) findViewById(R.id.r_name_f)).getText()
                .toString().trim();
        String lastName = ((EditText) findViewById(R.id.r_last_name_f))
                .getText().toString().trim();
        String email = ((EditText) findViewById(R.id.r_email_f)).getText()
                .toString().trim();
		/*
		 * String address = ((EditText)
		 * findViewById(R.id.r_address_f)).getText() .toString().trim();
		 */
        String cellphone = ((EditText) findViewById(R.id.r_cellphone_f))
                .getText().toString().trim();
        String passwd = ((EditText) findViewById(R.id.r_passwd_f)).getText()
                .toString().trim();
        String confirm = ((EditText) findViewById(R.id.r_passwdr_f)).getText()
                .toString().trim();
        RadioButton terms = (RadioButton) findViewById(R.id.r_term_rb);

        String messageError = "";
        if (name.isEmpty()) {
            messageError += getResources().getString(R.string.fst_name) + ", ";
        }
        if (lastName.isEmpty()) {
            messageError += getResources().getString(R.string.lst_name) + ",";
        }
        if (email.isEmpty()) {
            messageError += getResources().getString(R.string.email) + ",";
        }
		/*
		 * if (address.isEmpty()) { messageError +=
		 * getResources().getString(R.string.st_address) + ","; }
		 */
        if (cellphone.isEmpty()) {
            messageError += getResources().getString(R.string.cellphone) + ",";
        }
        if (passwd.isEmpty()) {
            messageError += getResources().getString(R.string.passwd) + ",";
        }
        if (confirm.isEmpty()) {
            messageError += getResources().getString(R.string.conf_passwd)
                    + ",";
        }

        if (!messageError.isEmpty()) {
            messageError = getResources().getString(
                    R.string.st_missingFileds_part1)
                    + " "
                    + messageError
                    + " "
                    + getResources().getString(R.string.st_missingFileds_part2);
            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_title_register),
                    messageError, false, this,
                    getResources().getString(R.string.st_accept));
            return;
        }
        if (email.indexOf("@") == -1) {
            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_title_register),
                    getResources().getString(R.string.msg_detail_invalidEmail),
                    false, this, getResources().getString(R.string.st_accept));
            ((EditText) findViewById(R.id.r_email_f)).requestFocus();
            return;
        }

        if (!passwd.equals(confirm)) {
            SherpaDialog
                    .showDialog(
                            getResources().getString(
                                    R.string.msg_title_register),
                            getResources().getString(
                                    R.string.msg_detail_pswdMissmatch), false,
                            this, getResources().getString(R.string.st_accept));
            ((EditText) findViewById(R.id.r_passwd_f)).requestFocus();
            return;
        }

        if (!terms.isChecked()) {

            SherpaDialog.showDialog(
                    getResources().getString(R.string.msg_title_register),
                    getResources().getString(R.string.accept_term_cond), false,
                    this, getResources().getString(R.string.st_accept));
            return;
        }

        ArrayList<String> param = new ArrayList<>();
        param.add(name);
        param.add(lastName);
        param.add(email);
        param.add(cellphone);
        param.add(passwd);
        param.add("1");
        RestClient.get().executeCommand("16", param.toString(), new Callback<Response>() {

            @Override
            public void success(Response response, retrofit.client.Response response2) {
                hideOverlay();
                if (response != null && response.getStatus() == 0) {
                    // hay un resultado ....

                    showDialog(
                            getResources().getString(
                                    R.string.msg_title_register),
                            getResources().getString(
                                    R.string.msg_register_succsess), false,
                            true);
                    return;
                } else {
                    showDialog(
                            getResources().getString(
                                    R.string.msg_title_register),
                            getResources().getString(
                                    R.string.msg_detail_userExists), false,
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


    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.finish();
    }
}
