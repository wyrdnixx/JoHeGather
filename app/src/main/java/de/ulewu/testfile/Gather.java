package de.ulewu.testfile;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ulewu on 29.08.2017.
 */

//public class Gather implements Parcelable {
public class Gather implements Serializable {



    // Objektvariablen
    private String gName;



    private List<Item> gatherItems;


    // Getter und Setter
    public String getgName() {
        return gName;
    }

    public List<Item> getGatherItems() {
        return gatherItems;
    }

    // Konstruktor
    public Gather(String _name) {
        gName = _name;
        gatherItems = new ArrayList<Item>();

    }

    // Ein Text-Item Objekt dem Gather hinzu fügen.
    public void addItem(String _id, String _text) {

        Item i = new Item(_id,_text);
        gatherItems.add(i);

    }

    //andere typen mit Datenfeldnummern
    public void addItem(String _id, String _text, String _type, int _field) {

        Item i = new Item(_id,_text, _type, _field);
        gatherItems.add(i);

    }

    // Ein Selector-Item Objekt (DropDown) dem Gather hinzu fügen.
    public void addItem(String _id, String _text, String _options) {

        String[] options = _options.split(";");

        Item i = new Item(_id,_text,options);
        gatherItems.add(i);

    }

}
