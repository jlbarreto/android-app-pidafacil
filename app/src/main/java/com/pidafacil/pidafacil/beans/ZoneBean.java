package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 07-29-15.
 */
public class ZoneBean {
    private Integer id;
    private String zone;
    private Float shippingCharge;

    public ZoneBean() { }

    public ZoneBean(Integer id, String zone, Float shippingCharge) {
        this.id = id;
        this.zone = zone;
        this.shippingCharge = shippingCharge;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Float getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(Float shippingCharge) {
        this.shippingCharge = shippingCharge;
    }
}
