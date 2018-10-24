package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 03-06-15.
 */
public class RequestProductBean {
    private int restaurant_id;
    private String name;
    public int getRestaurant_id() {
        return restaurant_id;
    }
    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
