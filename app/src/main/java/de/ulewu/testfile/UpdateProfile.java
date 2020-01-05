package de.ulewu.testfile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;

import static de.ulewu.testfile.Util.log;

/**
 * Created by Ulewu on 26.08.2017.
 */

public class UpdateProfile extends AsyncTask<Void,Void,String> {

    Context context;

    ProgressDialog progressDialog;
    File file;
    Param param ;

    // Konstruktor
    // UpdateProfile(Context _context, File _file) {
    UpdateProfile(Context _context, Param _param) {
        context = _context;
        param = _param;
    }


    // AsyncTask Methods
    @Override
    protected String doInBackground(Void... params) {


        JSch jsch = new JSch();

        String host =param.getMasterServer(), username=param.getMasterServerUser(), password=param.getMasterServerPassword();


        //String localFilePath = file.getAbsolutePath();
        //String fileName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);

        //String remoteFilePath = "/" + fileName;

        Session session = null;

        synchronized (this) {
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

                //sftpChannel.put(localFilePath, remoteFilePath);
                String localProfileFilePath = param.getBaseAppDir().toString() + "/";
                sftpChannel.get("default_Profile.xml",localProfileFilePath);
                log(1, "Update default_Profile.xml successfully to: " + localProfileFilePath);


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

            return "Profile Update completed...";
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
        progressDialog.setProgress(10);
        //progressDialog.setTitle(_result);
        progressDialog.dismiss();
        message(_result);

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
