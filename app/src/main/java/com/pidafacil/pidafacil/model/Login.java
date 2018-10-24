package com.pidafacil.pidafacil.model;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by victor on 03-23-15.
 */
public class Login extends RealmObject {
    private int id; //userid
    private int loginMethod; // 1 = normal, 2 = facebook, 3 = google plus
    private String userName; //userName
    private String userLastName;
    private String email; //email (Si se logueo normal)
    private String phoneNumber;
    private boolean photo = false;
    private String photoUri;
    private String sospecha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(int loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserLastName() { return userLastName; }

    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public boolean isPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getSospecha() {
        return sospecha;
    }

    public void setSospecha(String sospecha) {
        this.sospecha = sospecha;
    }
}
