package de.ulewu.testfile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class A_NFCActivity extends AppCompatActivity {


    private Util util;

    private String nfcTagSerialNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a__nfc);


        util = (Util) getIntent().getSerializableExtra("joheGather_util");
        nfcTagSerialNum = (String) getIntent().getSerializableExtra("nfcTagSerialNum");

        //message("Git-ID: " + nfcTagSerialNum);

        TextView tvNfcID =  (TextView) findViewById(R.id.tvNfcID);
        tvNfcID.setText(nfcTagSerialNum);

        EditText etNachname =  (EditText) findViewById(R.id.etNachname);
        EditText etVorname =  (EditText) findViewById(R.id.etVorname);
        EditText etGebDat =  (EditText) findViewById(R.id.etGebDat);


        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSaveListener();
            }
        });


    }

    private void btnSaveListener() {

        // message("Save geklickt");

        String dataString = "";


        TextView tvNfcID =  (TextView) findViewById(R.id.tvNfcID);
        EditText etNachname =  (EditText) findViewById(R.id.etNachname);
        EditText etVorname =  (EditText) findViewById(R.id.etVorname);
        EditText etGebDat =  (EditText) findViewById(R.id.etGebDat);

        String nfcId = tvNfcID.getText().toString();
        String Nachname = etNachname.getText().toString();
        String Vorname =  etVorname.getText().toString();
        String GebDat =  etGebDat.getText().toString();

        if (nfcId.isEmpty() ||Nachname.isEmpty() || Vorname.isEmpty() ||GebDat.isEmpty() )
        {
            message("Nicht alle Felder wurden ausgefüllt!");

        } else {

            // Convertiere Reversed Hex to Hex
            char[] chars = nfcId.toCharArray();
            String reversedHex = "";
            for (int i = 0 ; i <  chars.length -1 ; i = i +2) {
                reversedHex =  Character.toString(chars[i]) + Character.toString(chars[i+1]) +  reversedHex;
            }
            // Decodiere reversedHex nach integer -> dann String

            try {
                String toConvert = "#" + reversedHex;
                //Integer intNfcId = Integer.decode(toConvert);
                Long intNfcId = Long.decode(toConvert);


                        dataString = nfcId + ";" + reversedHex + ";" +intNfcId + ";" + Nachname + ";" +Vorname+";" + GebDat + System.getProperty("line.separator");
                util.writeToFile("NFCReader", dataString);
                finish();
            } catch (Exception e) {
                message("Fehler - Bitte erneut versuchen. Fehlercode: " + e.getMessage());
            }


        }


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
