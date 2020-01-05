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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;


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

    // Konstruktor
    // UploadDatafile(Context _context,Param _param, File _file) {
    /*
    UploadDatafile(Context _context,Param _param) {
        param = _param;
        file  = param.getDatafile();
        context = _context;
    }
*/
    UploadDatafile(Context _context,Param _param, List<File> _fl) {

        // file  = param.getDatafile();
        param = _param;
        context = _context;
        FileList = _fl;
        log(1, "Upload Files started... ");
    }

    // AsyncTask Methods
    @Override
    protected String doInBackground(Void... params) {


        log(1, "Background Upload process started... ");

        JSch jsch = null;
        try {
           jsch = new JSch();
            log(1, "JSch instance started...");
        } catch (Exception e) {
            log(2, e.getMessage());
        }


        // String host ="192.168.1.20", username="tester", password="password";

        String host=param.getMasterServer(), username=param.getMasterServerUser(), password=param.getMasterServerPassword();
        log(1, "Host parameter set to: " + host + " , Username: " + username);
/*
        String localFilePath = file.getAbsolutePath();
        String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);

        String remoteFilePath = "/" + fileName;
*/
        Session session = null;

        synchronized (this) {
            log(1, "synchronized process started... ");
            publishProgress();
            try {


                session = jsch.getSession(username, host, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(password);
                session.setTimeout(5000);
                session.connect();

                log(1, "Session established to " + host);

                Channel channel = session.openChannel("sftp");
                channel.connect();
                log(1, "Channel connected to " + host);
                ChannelSftp sftpChannel = (ChannelSftp) channel;

                for (File f : FileList) {

                    String localFilePath = f.getAbsolutePath();
                    String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);

                    String remoteFilePath = "/" + fileName;


                    sftpChannel.put(localFilePath, remoteFilePath);
                    log(1, "File put successfully " + host);

                    File targetDir = new File(param.getBaseAppDir().toString() + "/archive");

                    if (!targetDir.exists()) {
                        log(1, "Archive Dir does not exist.. creating: " + targetDir.toString());
                        targetDir.mkdir();
                    }

                    File dest = new File(targetDir.toString() + "/"+ fileName.toString());
                    try {
                        copyFile(f,dest);
                        log(1, "File archived to: " + dest.getPath());
                        f.delete();
                        log(1, "Orginal File deleted...");

                    } catch (Exception e) {
                        log(3, "ERROR: File Copy failed... " + e.getMessage());
                    }



                }

                sftpChannel.exit();
                session.disconnect();
                log(1, "Channel and Session closed to " + host);


                // wait(2000);
            }

            catch (JSchException e) {
            e.printStackTrace();
            log(3,"JSchExeption: " + e.getMessage());
            return "JSchExeption: " + e.getMessage();
            } catch (SftpException e) {
            e.printStackTrace();
            log(3,"SftpExeption: " + e.getMessage());
            return  "SftpExeption: " + e.getMessage();
            }

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




    @Override
    protected void onPostExecute(String _result) {
        // super.onPostExecute(_result);
        progressDialog.setProgress(100);
        //progressDialog.setTitle(_result);
        progressDialog.dismiss();
       // message(_result);

        super.onPostExecute(_result);

        context.startActivity(new Intent(context, A_MainActivity.class));
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
