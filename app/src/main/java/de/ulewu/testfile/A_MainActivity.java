package de.ulewu.testfile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.NetworkInfo;
import android.content.BroadcastReceiver;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;




public class A_MainActivity extends AppCompatActivity  implements AsyncTaskListener {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;

// NFC
    //public static final String MIME_TEXT_PLAIN = "text/plain";
    //public static final String TAG = "NfcDemo";
    //private NfcAdapter mNfcAdapter;

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;

    // public File BaseAppDir;
    public Util util;
    public String DeviceSN;
    public File datafile;
    public Param param;

    // Netzwerkstatus
    // ConnectivityManager
    private ConnectivityManager mConnMgr;
    // Breadcast Receiver
    public NetworkReceiver  mReceiver;
    public boolean NetworkAvailable;

    // Liste mit Gahter Objekten
    public List<Gather> gatherList = null;

    // Arrayadapter für FileListView
    private List<String> file_list;
    private ArrayAdapter<String> arrayAdapter;
    private ListView lv;

    private TextView tv_InfoNo;

////////////////////////////////////////////////////
//////////////////////////// Menueleiste

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            // Erstelle ein Intent für die neue Activity
            Intent settingsintent = new Intent(this, A_settings.class);

            // Packe das Objekt param in ein Bundle und übergebe es an das Intent (die neue Activity)
            // wird in der onCreacte Methode der Activity entgegen genommen.
            Bundle b = new Bundle();
            b.putSerializable("joheGather_param",param);
//        intent.putExtras(b);
            b.putSerializable("joheGather_util", util);
            settingsintent.putExtras(b);
            // Starte die neue Activity
            startActivity(settingsintent);

            return true;
        }

        //View LogFile
        if (id == R.id.LogView) {
            LogView();
        }

        //Sync Profile File
        if (id == R.id.updProfile) {
            new UpdateProfile(this,param).execute();
        }


        return super.onOptionsItemSelected(item);
    }

////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadFileView();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

*/
        tv_InfoNo = (TextView)findViewById(R.id.tv_InfoNotSync);


        // Get reference of widgets from XML layout
         lv = (ListView) findViewById(R.id.FileListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                FileListViewClicked(position);
            }
        });



        // Allgemeine Variable und Objekte
        Context context = getApplicationContext();

        // Basis Programmverzeichniss ermitteln
        //BaseAppDir = context.getExternalFilesDir(null);

        // Param Objekt initialisiseren
        param = new Param();
        // Basis Programmverzeichniss ermitteln
        param.setBaseAppDir(context.getExternalFilesDir(null));

        // Util Objekt initialisiseren
        util = new Util(param.getBaseAppDir(),param , context);


        util.readSettings(param);


        // Programmstart inLogfile schreiben
        util.log(1,"Programm start");
        util.log(1,"BaseAppDir: " + param.getBaseAppDir());
        //datafile = new File(param.getBaseAppDir(), "Datafile.txt");
        //param.setDatafile(new File(param.getBaseAppDir(), "Datafile.txt"));
        //util.log(1,"Datafile: " + param.getDatafile().getPath());


        // Seriennummer auslesen
        DeviceSN = util.getDeviceSN();
        ////////////////////////////////////////


        // App Berechtigungen prüfen bevor es los geht
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        //////////////////////////////

    /////////////////////////////////////

        // Check Network Connection
        // Store the Connectivity Manager in der member Variable
        mConnMgr =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // instantioate Network Event Bradcast Receiver
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // Register the Bradcast Receiver
        registerReceiver(mReceiver,filter);
        /////////////////////////////////// /////



            // Button upload
            Button btn_upload = (Button) findViewById(R.id.btn_upload);
            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnUploadClicked();
                }
            });
            ///////////////////////////////////////////////


        // Button Start Gather
        Button btn_startGather = (Button) findViewById(R.id.btn_StartGather);
        btn_startGather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_startGather_Clicked();
            }
        });
        /////////////////////////////////////////


/// --> übergangsweise, bis der Server im Programm auhc kontorliert und gesetzt werden kann.
        // param.setMasterServer("omega.chaos.local");

// NFC
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        } else {
            Util.log(1,"NFCAdapter found: : " + mAdapter.toString());
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


    }

    // Beim neuladen der Activity soll die File List aktualisert werden
    @Override
    public void onResume() {
        super.onResume();

        reloadFileView();


        //setupForegroundDispatch(this, mNfcAdapter);
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

    }

    public interface AsyncResponse {
        void processFinish(String output);
    }



    public  void reloadFileView() {


        List<File> fl = util.getCsvFiles();

        // ListView für Anzeige der vorhandenen Dateien
        //ListVie Reverenz in onCreate()
        // Initializing a new String Array
        String[] FileListItems = new String[] {};
        List<String> file_list = new ArrayList<String>(Arrays.asList(FileListItems));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, file_list);
        lv.setAdapter(arrayAdapter);


        if (fl.size() > 0) {
            //   message("Sie haben nicht syncronisierte Daten... \r\n Bitte Upload durchführen.");

            for (File f : fl) {
                //file_list.add(f.getName());
                tv_InfoNo.setText("Es sind Daten zum upload vorhanden...");
            }
            tv_InfoNo.setVisibility(View.VISIBLE);
        } else {
            tv_InfoNo.setVisibility(View.INVISIBLE);

        }


    }


    private void btn_startGather_Clicked(){


        // Erstelle ein Intent für die neue Activity
        Intent intent = new Intent(this, A_ChooseGatherActivity.class);

        // Packe das Objekt param in ein Bundle und übergebe es an das Intent (die neue Activity)
        // wird in der onCreacte Methode der Activity entgegen genommen.
        Bundle b = new Bundle();
        b.putSerializable("joheGather_param",param);
//        intent.putExtras(b);
        b.putSerializable("joheGather_util", util);
        intent.putExtras(b);
        // Starte die neue Activity
        startActivity(intent);


    }

    private void btnUploadClicked() {


        //param.setMasterServer("192.168.1.200");
      //  util.testMasterServerUpdate();

        // Test um Serverdaten aus Profil zu aktualisieren
  //      File profileFile = new File(param.getBaseAppDir() + "/" + "default_Profile.xml");
//        gatherList = util.updateProfileData(profileFile);
        ///////

        List<File> fl = util.getCsvFiles();

        if (fl.size() >=1){
            // new UploadDatafile(this,param).execute();
            util.readSettings(param);
           new UploadDatafile(this,param,util, fl).execute();
            //AsyncTask UploadTask = new UploadDatafile(this,param,util, fl);


        } else {
            message("Keine gespeicherten Daten gefunden.");
            util.log(2,"Keine gespeicherten Daten gefunden.");
        }


        // Die Dateiliste aktualisieren...
        // vorher 500ms warten, da sonnst die hoch geladenen Dateien evtl. noch nicht verschoben wurden...
        // ist derzeit ein Workaround
        //try {
        //    Thread.sleep(1000);
       // } catch (Exception e) {
        //}
      //  reloadFileView();

    }

    // Callback Methode über Interface - Empfängt UploadStatus
    public void giveUploadStatus(String _result){

        if (_result == "Upload completed...") {
            message("Upload erfolgreich...");
            reloadFileView();
        } else {
            message("Fehler beim Upload der Daten: " + _result);
        }

    }



    private void FileListViewClicked(int _pos) {
        //message(lv.getSelectedItem().toString());
        final String file = lv.getItemAtPosition(_pos).toString();
     //   message(file);


        // Dialog zum Anzeigen der File und der Option zum Löschen

// 1. Instantiate an AlertDialog.Builder with its constructor
       // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(util.readFile(file))
                .setTitle(file);

        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                util.deleteFile(file);
                reloadFileView();
            }
        });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
dialog.show();

    }


    private void LogView() {
        String File = param.getLogFile().getName().toString();
        String log = util.readFile(File);

        message(log);
    }


    // Beenden der App, wenn der User den Back Button in der MainActivity drückt
    @Override
    public void onBackPressed() {
        //finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        Util.log(1,"NewIntent Event " );

        getTagInfo(intent);
        //handleIntent(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Util.log(1,"getTagInfo: " + tag.toString() );
        Util.log(1,"TAG-ID found: " + tag.getId() );
        //message(tag.getId().toString());



        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] extraID = tagFromIntent.getId();

        StringBuilder sb = new StringBuilder();
        for (byte b : extraID) {
            sb.append(String.format("%02X", b));
        };

        String nfcTagSerialNum = sb.toString();
        // message("nfc ID: "+ nfcTagSerialNum);
        util.log(1,"RFID / NFC TAG found with ID: " + nfcTagSerialNum);


        Intent intentNfc = new Intent(this, A_NFCActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("joheGather_param",param);
//        intent.putExtras(b);
        b.putSerializable("joheGather_util", util);
        b.putString("nfcTagSerialNum",nfcTagSerialNum );
        intentNfc.putExtras(b);
        // Starte die neue Activity
        startActivity(intentNfc);
    }





    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
             if (networkInfo!= null) {
                 boolean isWifiAv = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                 boolean isGSMAv = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

                 if (isWifiAv) {
                     util.log(1,"WIFI has become available...");
                   //  Toast.makeText(getApplicationContext(), "Network Connection available", Toast.LENGTH_SHORT).show();
                     NetworkAvailable = true;
                 } else if (isGSMAv) {
                     util.log(1,"GSM has become available...");
                  //   Toast.makeText(getApplicationContext(), "Network Connection available", Toast.LENGTH_SHORT).show();
                     NetworkAvailable = true;
                 } else {
                     util.log(2,"Network Connection lost...");
                     Toast.makeText(getApplicationContext(), "Network Connection NOT available", Toast.LENGTH_SHORT).show();
                     NetworkAvailable = false;
                 }

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
