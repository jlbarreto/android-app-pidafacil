package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 03-06-15.
 */
public class UserRequestBean {
    private String user;
    private String restaurantName;
    private int user_id;
    private int restaurant_id;
    private String data;

    /*
    * @param restaurant id del restaurante
    * @param user Nombre del usuario de esta solicitud.
    * */
    public UserRequestBean(int restaurant_id, String user) {
        this.restaurant_id = restaurant_id;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    @Override
    public String toString() {
        return "UserRequestBean{" +
                "user='" + user + '\'' +
                ", user_id=" + user_id +
                ", restaurant_id=" + restaurant_id +
                ", data='" + data + '\'' +
                '}';
    }
}
