package de.ulewu.testfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static de.ulewu.testfile.Util.log;

public class A_ChooseGatherActivity extends AppCompatActivity {

    private Param param;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_choose_gather_activity);

        // Hole das die Übergebenen Objekte aus dem Intent
        param = (Param) getIntent().getSerializableExtra("joheGather_param");
        util = (Util) getIntent().getSerializableExtra("joheGather_util");



        // testCreateButton();
        // message(util.getDeviceSN());
        readXML();
    }


// --> Muss angepasst werden, damit nicht merh direkt aus der XML gelesen wird, sondern aus
    // der Liste, die aus util.updateProfileData gelesen wird
public void readXML(){

    ViewGroup linearLayout = (ViewGroup) findViewById(R.id.A_ChooseGatherActivity);


    File file = new File(param.getBaseAppDir() + "/" + "default_Profile.xml");

    try {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        //Document dom = db.parse("file.xml");
        Document dom = db.parse(file);
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getChildNodes();
        if (nl != null) {
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);
                    if (el.getNodeName().contains("gather")) {
                       // String name = el.getElementsByTagName("name").item(0).getTextContent();

                        final String nodeID =el.getAttribute("id");
                        param.setMasterServer(el.getAttribute("server"));
                        // message("Node ID: " + nodeID);
                        // message("Node Name: " + el.getNodeName());       // NodeName ist nur der Teil im <..> in diesem Fall immer gleich "gather"

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        // Gather Objekt erzeugen
                        final Gather g = new Gather(nodeID);

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            // Button erstellen und Lisener hinzu fügen.
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        NodeList nodes = el.getChildNodes();
                        if (nodes != null) {
                            int lenchilds = nodes.getLength();
                            for (int x = 0; x < lenchilds; x++) {
                                if (nodes.item(x).getNodeType() == Node.ELEMENT_NODE) {
                                    Element nodeItem = (Element) nodes.item(x);
                                    if (nodeItem.getNodeName().contains("item")) {
                                   //     message("SubNode Name: " + nodeItem.getNodeName());
                                   //     message("Item ID: " + nodeItem.getAttribute("id"));
                                   //     message("Item Text: " + nodeItem.getAttribute("text"));
                                        String _type = nodeItem.getAttribute("type");

                                        ///////////////////////////////////////
                                        // dem Gather das Item hinzu fügen, je nach dem, ob es ein Type "text" oder "select" ist.
                                        if (_type.toString().equalsIgnoreCase("select")) {
                                            g.addItem(nodeItem.getAttribute("id"),nodeItem.getAttribute("text"),nodeItem.getAttribute("options"));
                                        } else if (_type.toString().equalsIgnoreCase("text")) {
                                            //message("textitem");
                                            g.addItem(nodeItem.getAttribute("id"),nodeItem.getAttribute("text"));
                                        }
                                     else if (_type.toString().equalsIgnoreCase("nfc")) {
                                            //message("nfcitem");
                                            try {
                                                String _fieldIDString = nodeItem.getAttribute("fieldId");
                                               // message("fieldIdString:" + _fieldIDString);
                                                Integer fieldId = Integer.parseInt(_fieldIDString);
                                                g.addItem(nodeItem.getAttribute("id"),nodeItem.getAttribute("text"), "nfc" ,fieldId);
                                            } catch(NumberFormatException nfe) {
                                                message("ERROR: FieldID not a number in Profile-XML-File");
                                            }

                                    }

                                    }
                                }
                            }
                        }



                      //  message("text: " + el.getElementsByTagName("text").item(0).getTextContent());
                    }
                }
            }
        }
    } catch (Exception e) {
        log(3,"Open XML File: " + e.getMessage());
        message("Error open XML File: " + e.getMessage());
    }

}


/**
    public void testCreateButton() {
        ViewGroup linearLayout = (ViewGroup) findViewById(R.id.a_choose_gather_activity);


        for (int i = 1; i < 3 ; i++) {

            Button bt = new Button(this);
            final String btntext = "Button " + i;
            bt.setText(btntext);

            bt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(bt);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    test(btntext);
                }
            });
        }
    }

    */

public void gatherButtonListener(String text,Gather _g)
{
   /** for (Item item: _g.getGatherItems()) {
        message(item.getId() + " : " + item.getText());
    } */
    startGathering(_g);
    //message(text);
}


public void startGathering(Gather _gather){
    // Erstelle ein Intent für die neue Activity
    Intent intent = new Intent(this, A_GatherActivity.class);

    // Packe das Objekt param in ein Bundle und übergebe es an das Intent (die neue Activity)
    // wird in der onCreacte Methode der Activity entgegen genommen.
//    Bundle b = new Bundle();
//    b.putSerializable("joheGather_param",param);
    //intent.putExtras(b);
    //b.putSerializable("joheGather_util", util);
    //intent.putExtras(b);
  //  b.putSerializable("joheGather_gather", _gather);
//    intent.putExtras(b);
        intent.putExtra("joheGather_util", util);
        intent.putExtra("joheGather_gather",_gather);
    // Starte die neue Activity
    startActivity(intent);
}

///////////////////////////////////////////////////
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
