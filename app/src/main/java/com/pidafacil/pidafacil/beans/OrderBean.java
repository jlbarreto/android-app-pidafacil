package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 04-14-15.
 */
public class OrderBean {

    private Integer id;
    private String name;
    private String total;
    private String createAt;
    private String status;
    private String orderCode;

    public OrderBean(Integer id, String name, String total) {
        this.id = id;
        this.name = name;
        this.total = total;
    }

    public OrderBean(Integer id, String name, String total, String createAt) {
        this.id = id;
        this.name = name;
        this.total = total;
        this.createAt = createAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
