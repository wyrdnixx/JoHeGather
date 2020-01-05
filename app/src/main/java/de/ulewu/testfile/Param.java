package de.ulewu.testfile;

import java.io.Serializable;
import java.io.File;

/**
 * Created by Ulewu on 27.08.2017.
 */

public class Param implements Serializable{

    // Konstruktor
    public Param() {


        //setMasterServer("192.168.1.20");
        setMasterServerPassword("password");
        setMasterServerUser("tester");
    }

    public File getBaseAppDir() {
        return BaseAppDir;
    }

    public void setBaseAppDir(File baseAppDir) {
        BaseAppDir = baseAppDir;
    }

    private File BaseAppDir;

    private String masterServerPassword;

    public String getMasterServerPassword() {
        return masterServerPassword;
    }

    public void setMasterServerPassword(String masterServerPassword) {
        this.masterServerPassword = masterServerPassword;
    }

    private String masterServerUser;

    public String getMasterServerUser() {
        return masterServerUser;
    }

    public void setMasterServerUser(String masterServerUser) {
        this.masterServerUser = masterServerUser;
    }

    private String masterServer;

    public String getMasterServer() {
        return masterServer;
    }

    public void setMasterServer(String _masterServer) {

      this.masterServer = _masterServer;
    }

    private File datafile;

    public File getDatafile() {
        return datafile;
    }

    public void setDatafile(File datafile) {
        this.datafile = datafile;
    }

    public File getLogFile() {
        return LogFile;
    }

    public void setLogFile(File logFile) {
        LogFile = logFile;
    }

    private File LogFile;


   /** private String server;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
    **/
}
