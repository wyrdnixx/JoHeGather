package de.ulewu.testfile;

/**
 * Created by Ulewu on 18.08.2017.
 */


        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.Toast;


        import org.w3c.dom.Document;
        import org.w3c.dom.Element;
        import org.w3c.dom.Node;
        import org.w3c.dom.NodeList;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.FilenameFilter;
        import java.nio.channels.FileChannel;
        import java.security.MessageDigest;

        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Date;

        import java.io.Serializable;
        import java.util.List;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;


public class Util implements Serializable {

    public static File logFile;
    public  static Context context;
    public static  Param param;
    // Konstruktor
    public  Util(File BaseAppDir, Param _param, Context _context){

        // Logfile definieren und neu erstellen.
        // logFile = new File(BaseAppDir + "/_logFile.txt");


        this.param = _param;
        context = _context;

        this.param.setLogFile(new File(BaseAppDir + "/_logFile.txt"));
        logFile = this.param.getLogFile();



        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        } else {
            try
            {
                // löschen und neue erstellen.
                logFile.delete();
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /// Logfile


    }


public List<Gather> updateProfileData(File _xmlFile){


/*        Beispiel default_Profile.xml
            <JoHeGather>
                <masterserver server="192.168.1.20"></masterserver>
                <gather id="test">
	                <item type="text" text="hallo"/>
	                <item type="select" text="select-text" options="1;2;3"/>
                </gather>
            </JoHeGather>

            */


    File xmlfile = new File(param.getBaseAppDir() + "/" + "default_Profile.xml");

    //File xmlfile = _xmlFile;

    List<Gather> _gatherList = new ArrayList<>();


    try {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        //Document dom = db.parse("file.xml");
        Document dom = db.parse(xmlfile);
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getChildNodes();
        if (nl != null) {
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);

                    // Katalogdaten
                    if (el.getNodeName().contains("masterserver")) {
                        if (el.getAttribute("server") != null) {
            //               message(el.getAttribute("server") );
                            param.setMasterServer(el.getAttribute("server"));
                        } else {
             //               message("Server in Profil nicht definiert");
                        }

                    }

             // Gather einträge
                    else if (el.getNodeName().contains("gather")) {
                        // String name = el.getElementsByTagName("name").item(0).getTextContent();

//                        final String masterServer =el.getAttribute("server");
//                        param.setMasterServer(masterServer);

                        final String nodeID =el.getAttribute("id");

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        // Gather Objekt erzeugen
                        final Gather g = new Gather(nodeID);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

             /**           // Button erstellen und Lisener hinzu fügen.
                        Button bt = new Button(this);
                        bt.setText(g.getgName());
                        bt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        linearLayout.addView(bt);
                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gatherButtonListener(nodeID,g);
                            }
                        });
            **/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        NodeList items = el.getChildNodes();
                        if (items != null) {
                            int lenchilds = items.getLength();
                            for (int x = 0; x < lenchilds; x++) {
                                if (items.item(x).getNodeType() == Node.ELEMENT_NODE) {
                                    Element item = (Element) items.item(x);
                                    if (item.getNodeName().contains("item")) {


                                        // Item Typen diferenzieren
                                        //message("Itemtype: " + item.getAttribute("type"));
                                        switch (item.getAttribute("type")) {
                                            // Textitem dem Gather hinzu fügen
                                            case  "text":
                                                g.addItem(item.getAttribute("id"),item.getAttribute("text"));
                                                break;
                                            case "select":
                                                g.addItem(item.getAttribute("id"),item.getAttribute("text"),item.getAttribute("options") );
                                                break;
                                            case "nfc":
                                                g.addItem(item.getAttribute("id"),item.getAttribute("text"));
                                                break;
                                        }



                                        ///////////////////////////////////////

                                    }
                                }
                            }
                        }


                        _gatherList.add(g);
                        //  message("text: " + el.getElementsByTagName("text").item(0).getTextContent());
                    }
                }
            }
        }
    } catch (Exception e) {
        log(3,"Open XML File: " + e.getMessage());
        message("Error open XML File: " + e.getMessage());
    }

        return _gatherList;
}

    /*
    public File[] getLocalFiles(String baseDir) {

        String[] localFiles;
        File directory = new File(baseDir);
        File[] files = directory.listFiles();
        for     (File f : files) {
        ////    f.toString();
        }
        return files;
    }
*/

    public List<File> getCsvFiles(){

//        File dir = new File(".");
        File dir = new File(param.getBaseAppDir().toString());

        List<File> list = Arrays.asList(dir.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv"); // or something else
            }}));

        /* Debug
        for (File f : list) {
            log(1,"File found: " + f.getPath().toString());
        }
        */
        log(1,"CSV Dateiliste aktualisisert..."  );
        return list;
    }


    public static void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }


    // Seriennummer auslesen
    public String getDeviceSN() {
        String deviceUniqueIdentifier = null;
        deviceUniqueIdentifier = android.os.Build.SERIAL;
        log(1,"Device SN: " + deviceUniqueIdentifier );
        return deviceUniqueIdentifier;
    }

    // Benachrichtigung an den User
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

    // Logfile schreiben.

    public static void log(int severity , String msg) {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(date);



        String level = null;

        switch (severity) {
            case 1 : level = " - Info: ";
                break;
            case 2 : level = " - Warning: ";
                break;
            case 3 : level = " - Error: ";
                break;
            default: level = " - Info: ";
                break;
        } // Switch


        String LogString = timestamp + level + msg;

        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(LogString);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    } // public static void log


    public String writeToFile(String _gather, String _data) {

        // Dateiname setzen
        // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        String currentDateTimeString = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

        String FileName = getDeviceSN() + "_" + _gather + "_" + currentDateTimeString + ".csv";

        if (_gather =="NFCReader") {
            FileName = "NFCReader_" + currentDateTimeString + ".csv";
        }

        param.setDatafile(new File(param.getBaseAppDir(), FileName ));


        File file = param.getDatafile();
        log(1,"Datafile: " + file.getPath());

        try {
            FileOutputStream stream = new FileOutputStream(file,true);

            stream.write(_data.getBytes());
            stream.close();
        } catch (Exception e) {
            log(3,"ERROR while File Write: " + e.getMessage());
            return e.getMessage();
        }
        log(1,"File written: "+ file.getPath());
        return "File Written: " + file.getPath();
    } // Public WriteFile


    public String readFile(String _fileName){

        File fileToRead = new File(param.getBaseAppDir().toString() + "/" + _fileName );

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToRead));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            log(3, "Datei konnte nicht gelesen werden: " + _fileName);
        }
        return text.toString();
    }

public void deleteFile(String _fileName) {

    File fileToDelete = new File(param.getBaseAppDir().toString() + "/" + _fileName );

    try {
        fileToDelete.delete();
    } catch (Exception e) {
        log(3, "Datei konnte nicht gelöscht werden: " + _fileName);
    }
}


} // Public Class Util



