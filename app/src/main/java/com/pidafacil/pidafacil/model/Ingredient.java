package com.pidafacil.pidafacil.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by victor on 03-11-15.
 */
public class Ingredient extends RealmObject{
    private int id;
    private int selected;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
