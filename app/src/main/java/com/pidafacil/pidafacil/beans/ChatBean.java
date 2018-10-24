package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 08-10-15.
 */
public class ChatBean {
    TypeChat type;
    String user;
    String message;
    String hour;

    public enum TypeChat{
        SERVER_UPDATE, SUPPORT_CHAT_USERSUPP, SUPPORT_CHAT_USERCL
    }

    public ChatBean() {
    }

    public TypeChat getType() {
        return type;
    }

    public void setType(TypeChat type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
