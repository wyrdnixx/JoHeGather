package de.ulewu.testfile;

import java.io.Serializable;

/**
 * Created by Ulewu on 29.08.2017.
 */

public class Item implements Serializable {


    private String id;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    private String text;

    public String[] getOptions() {
        return options;
    }
    private String[] options;

    public String getType() {
        return type;
    }

    private String type;
    private int field;

    // Konstruktor einfaches Text-Item
    public Item(String _id, String _text) {
        id = _id;
        text = _text;
        type = "text";
    }

    // Konstruktor Selector (DropDown)-Item
    public Item(String _id, String _text, String[] _options) {
        id = _id;
        text = _text;
        options = _options;
        type = "select";
    }

    // für NFC o.Ä. mit Feldkennzeichen
    public Item(String _id, String _text, String _type, int _field) {
        id = _id;
        text = _text;
        type = _type;
        field = _field;
    }
}
