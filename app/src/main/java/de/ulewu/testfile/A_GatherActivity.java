package de.ulewu.testfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.util.List;
import java.util.ArrayList;
import android.nfc.NfcAdapter;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Ulewu on 30.08.2017.
 */

public class A_GatherActivity extends AppCompatActivity {

    private Param param;
    private Util util;
    private Gather gather;

    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_gather_actvity);

        // Hole das die Übergebenen Objekte aus dem Intent
//        param = (Param) getIntent().getSerializableExtra("joheGather_param");
        util = (Util) getIntent().getSerializableExtra("joheGather_util");
        gather = (Gather) getIntent().getSerializableExtra("joheGather_gather");

        //message("gather wurde übergeben: " + gather.getgName());

            addItem(gather);
    }


    private void addItem(Gather _gather){
        //Button bt = new Button(this);
        //bt.setText(g.getgName());
        //bt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
        //        LinearLayout.LayoutParams.WRAP_CONTENT));
        //linearLayout.addView(bt);
        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.aGatherActivity);
        for (Item _item: _gather.getGatherItems()) {

            String _type = _item.getType();
           // message("Debug: Add-Item: " + _type);

            // TextView -> Anzeigetext
            TextView tf = new TextView(this);
            tf.setText(_item.getText());
            tf.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(tf);


            // Datenfeld in abhängigkeit vom Typ
            switch (_type) {
                case "text":
                    // EditText
                    EditText et = new EditText(this);
                    et.setHint(_item.getId());
                    et.setTag(_item.getId());

                    et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(et);
                    break;

                case "select":
                    //Spinner
                    Spinner sp = new Spinner(this);
                    final List<String> list = new ArrayList<String>();

                    String[] _options = _item.getOptions();

                    // ein leeres Item dem Spinner hinzu fügen
                    list.add("");

                    for (int i = 0; i < _options.length; i++) {
                        list.add(_options[i]);
                   //     message(_options[i]);
                    }

                    ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_dropdown_item, list);
                    adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    sp.setAdapter(adp1);

                    linearLayout.addView(sp);
                    break;

                case "nfc":
                  //  message("NFC");
                 /*   mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

                    if (mNfcAdapter == null) {
                        // Stop here, we definitely need NFC
                        message("This device doesn't support NFC.");
                        return;

                    }

                    if (!mNfcAdapter.isEnabled()) {
                        message("NFC is disabled!");
                    } else {
                        message("NFC-Message: " + mNfcAdapter.toString());
                    }
*/
                    EditText nfcet = new EditText(this);
                    nfcet.setHint("NFC: " +_item.getId());
                    nfcet.setTag(_item.getId());

                    nfcet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(nfcet);

            }

        }

        Button btnSave = new Button(this);
        btnSave.setText("Speichern");
        btnSave.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSaveListener();
            }
        });
        
        linearLayout.addView(btnSave);


    }


    private void btnSaveListener(){
        String dataString = "";

        ViewGroup group = (ViewGroup)findViewById(R.id.aGatherActivity);
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                //((EditText)view).setText("");//here it will be clear all the EditText field

                String textString = ((EditText) view).getText().toString();

                if (!textString.equalsIgnoreCase("")) {
                   // message("->" + textString  +"<-");
                    dataString = dataString + textString+ ";";
                } else {
                    message("Fehler: Ein Feld ist nicht ausgefüllt!");
                    return;
                }


            }else if (view instanceof Spinner) {

                String spinnerString = ((Spinner) view).getSelectedItem().toString();

                if (spinnerString != "") {
                    dataString = dataString + ((Spinner) view).getSelectedItem().toString()  + ";";
                } else {
                    message("Fehler: Ein Auswahlfeld ist nicht ausgefüllt!");
                    return;
                }

            }
        }
        util.writeToFile(gather.getgName(), dataString);
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


