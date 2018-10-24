package com.pidafacil.pidafacil.beans;

public class SimpleProductBean {
	private String detail;
    private String price;

    public SimpleProductBean(String detail, String price) {
        this.detail = detail;
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
