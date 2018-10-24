package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 05-20-15.
 */
public class Suggestion {
    private int type; //categoria, tag
    private int value; //id de la base de datos
    private String label; // expresion que identifica la sugerencia

    public Suggestion(int type, int value, String label) {
        this.type = type;
        this.value = value;
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "type=" + type +
                ", value=" + value +
                ", label='" + label + '\'' +
                '}';
    }
}
