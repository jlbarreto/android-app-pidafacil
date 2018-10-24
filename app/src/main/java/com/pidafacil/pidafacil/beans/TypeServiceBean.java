package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 08-10-15.
 */
public class TypeServiceBean {

    int type;
    String service;

    public TypeServiceBean() {
    }

    public TypeServiceBean(int type, String service) {
        this.type = type;
        this.service = service;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
