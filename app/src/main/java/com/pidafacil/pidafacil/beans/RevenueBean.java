package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 08-24-15.
 */
public class RevenueBean {
    private String restaurant;
    private String typeService;
    private String paymentType;
    private String subtotal;
    private String shippingPrice;
    private String id_categoria;
    private String seccion_nombre;
    private String id_seccion;
    private String tag_nombre;
    private String id_tag;


    private List<RevenueProductBean> prods;

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }


    public String getSeccion_nombre() {
        return seccion_nombre;
    }

    public void setSeccion_nombre(String seccion_nombre) {
        this.seccion_nombre = seccion_nombre;
    }

    public String getId_seccion() {
        return id_seccion;
    }

    public void setId_seccion(String id_seccion) {
        this.id_seccion = id_seccion;
    }

    public String getTag_nombre() {
        return tag_nombre;
    }

    public void setTag_nombre(String tag_nombre) {
        this.tag_nombre = tag_nombre;
    }

    public String getId_tag() {
        return id_tag;
    }

    public void setId_tag(String id_tag) {
        this.id_tag = id_tag;
    }

    public List<RevenueProductBean> getProds() {
        return prods;
    }

    public boolean add(RevenueProductBean object) {
        if(this.prods == null ){
            this.prods = new ArrayList<>();
        }
        return prods.add(object);
    }
}
