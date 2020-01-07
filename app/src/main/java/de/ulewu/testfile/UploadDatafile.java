package de.ulewu.testfile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.icu.util.ValueIterator;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.os.AsyncTask;

import static de.ulewu.testfile.Util.copyFile;
import static de.ulewu.testfile.Util.log;

/**
 * Created by Ulewu on 26.08.2017.
 */

public class UploadDatafile extends AsyncTask<Void,Void,String> {



    Context context;

    ProgressDialog progressDialog;
  //  File file;
    List<File> FileList;
    private Param param;
    private Util util;

    // Interface AsyncTaskListener für Callback an Main Activity
    private AsyncTaskListener listener;


    // Konstruktor
    UploadDatafile(Context _context,Param _param, Util _util, List<File> _fl) {

        // file  = param.getDatafile();
        param = _param;
        util = _util;
        context = _context;
        FileList = _fl;

        // Interface AsyncTaskListener für Callback an Main Activity
        listener= (AsyncTaskListener)_context;


        util.log(1, "Upload Files started... ");
    }

    // AsyncTask Methods
    @Override
    protected String doInBackground(Void... params) {


        util.log(1, "Background Upload process started... ");

        JSch jsch = null;
        try {
           jsch = new JSch();
            util.log(1, "JSch instance started...");
        } catch (Exception e) {
            util.log(2, e.getMessage());
        }


        // String host ="192.168.1.20", username="tester", password="password";

        String host=param.getMasterServer(), username=param.getMasterServerUser(), password=param.getMasterServerPassword();
        util.log(1, "Host parameter set to: " + host + " , Username: " + username);
/*
        String localFilePath = file.getAbsolutePath();
        String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);

        String remoteFilePath = "/" + fileName;
*/
        Session session = null;

        synchronized (this) {
            util.log(1, "synchronized process started... ");
            publishProgress();
            try {


                session = jsch.getSession(username, host, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.setTimeout(5000);
                session.connect();

                util.log(1, "SFTP: Session established to " + host);

                Channel channel = session.openChannel("sftp");
                channel.connect();
                util.log(1, "SFTP: Channel connected to " + host);
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                for (File f : FileList) {

                    String localFilePath = f.getAbsolutePath();
                    String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);

                    // Ausnahme für NFCReader.csv Datei
                    if (fileName.equals("NFCReader.csv")) {

                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                        String timestamp = dateFormat.format(date);

                        String[] ar = fileName.split(".csv");
                        fileName= ar[0] + "_" + timestamp + ".csv";
                    }


                    String remoteFilePath = "/" + fileName;

                    sftpChannel.put(localFilePath, remoteFilePath);
                    util.log(1, "SFTP: File put successfully " + host);

                    File targetDir = new File(param.getBaseAppDir().toString() + "/archive");

                    if (!targetDir.exists()) {
                        util.log(1, "Archive Dir does not exist.. creating: " + targetDir.toString());
                        targetDir.mkdir();
                    }

                    File dest = new File(targetDir.toString() + "/"+ fileName.toString());
                    try {
                        copyFile(f,dest);
                        util.log(1, "File archived to: " + dest.getPath());
                        f.delete();
                        util.log(1, "Orginal File deleted...");

                    } catch (Exception e) {
                        util.log(3, "ERROR: SFTP: File Copy failed... " + e.getMessage());
                    }



                }

                sftpChannel.exit();
                session.disconnect();
                util.log(1, "SFTP: Channel and Session closed to " + host);


                // wait(2000);
            }

            catch (JSchException e) {
            e.printStackTrace();
            util.log(3,"SftpExeption: " + e.getMessage());
            return "JSchExeption: " + e.getMessage();
            } catch (SftpException e) {
            e.printStackTrace();
            util.log(3,"SftpExeption: " + e.getMessage());
            return  "SftpExeption: " + e.getMessage();
            }
            util.log(1,"Sftp: Upload erfolgreich ");
            return "Upload completed...";
        }

    }
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading progress...");
        progressDialog.setMax(10);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }


//    public A_MainActivity.AsyncResponse delegate = null;




    @Override
    protected void onPostExecute(String _result) {
        // super.onPostExecute(_result);
        progressDialog.setProgress(100);
        //progressDialog.setTitle(_result);
        progressDialog.dismiss();


        super.onPostExecute(_result);

        // Upload Status über Interface an Hauptklasse übergeben.
        listener.giveUploadStatus(_result);

       // context.startActivity(new Intent(context, A_MainActivity.class));
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        //super.onProgressUpdate(values);
        progressDialog.setProgress(1);
    }






    public void message(String _message) {

        AlertDialog ad = new AlertDialog.Builder(context).create();
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
