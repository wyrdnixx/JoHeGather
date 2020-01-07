package de.ulewu.testfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class A_settings extends AppCompatActivity {

    private Param param;
    private Util util;

    // Interface
    private AsyncTaskListener listener;

    EditText etServer;
    EditText etUser;
    EditText etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        util = (Util) getIntent().getSerializableExtra("joheGather_util");
        param = (Param) getIntent().getSerializableExtra("joheGather_param");

        util.readSettings(param);

        String host=param.getMasterServer(),
                username=param.getMasterServerUser(),
                password=param.getMasterServerPassword();

         etServer =  (EditText) findViewById(R.id.etServer);
         etUser =  (EditText) findViewById(R.id.etUser);
         etPassword =  (EditText) findViewById(R.id.etPassword);


        etServer.setText(host);
        etUser.setText(username);
        etPassword.setText(password);


        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSaveListener();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnAbbrechen);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCancelListener();
            }
        });


    }


    private void btnSaveListener() {
        try {

            String Server = etServer.getText().toString();
            String User =  etUser.getText().toString();
            String Password = etPassword.getText().toString();

            Boolean res = util.writeSettings(Server,User,Password);

            finish();
        } catch (Exception e) {
            message("Error: " + e.getMessage());
            util.log(3,"Error writing settingsfile: " + e.getMessage());
        }
    }

    private void btnCancelListener() {
        finish();
    }


    public void message(String _message) {

        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setCancelable(false); // This blocks the 'BACK' button
        ad.setMessage(_message);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }


}
