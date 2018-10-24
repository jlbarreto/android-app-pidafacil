package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 03-27-15.
 */
public class CardBean {

    private String name;
    private String number;
    private String code;
    private String monthStr;
    private String year;
    private String month;
    private String bin;

    public CardBean(String name, String number, String code, String monthStr, String year, String month) {
        this.name = name;
        this.number = number;
        this.code = code;
        this.monthStr = monthStr;
        this.year = year;
        this.month = month;
    }

    public CardBean(String name, String number, String code, String monthStr, String year, String month, String bin) {
        this.name = name;
        this.number = number;
        this.code = code;
        this.monthStr = monthStr;
        this.year = year;
        this.month = month;
        this.bin = bin;
        }


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", code='" + code + '\'' +
                ", monthStr='" + monthStr + '\'' +
                ", year='" + year + '\'' +
                ", bin='" + bin + '\'' +
                '}';
    }
}
