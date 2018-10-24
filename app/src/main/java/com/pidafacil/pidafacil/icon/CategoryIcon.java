package com.pidafacil.pidafacil.icon;

/**
 * Created by victor on 03-03-15.
 */
public class CategoryIcon {
    private int icon;
    private String name;
    private int id;
    private int position;

    public void CategoryIcon(){

    }

    public CategoryIcon(int icon, int id, String name) {
        this.icon = icon;
        this.id = id;
        this.name = name;
    }

    public CategoryIcon(int icon, int id) {
        this.icon = icon;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
