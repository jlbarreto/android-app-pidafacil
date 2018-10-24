package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 07-29-15.
 */
public class MunicipalityBean {
    private Integer id;
    private String municipality;
    private List<ZoneBean> zones;

    public MunicipalityBean() {
        this.zones = new ArrayList<>();
    }

    public MunicipalityBean(Integer id, String municipality) {
        this.id = id;
        this.municipality = municipality;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public List<ZoneBean> getZones() {
        return zones;
    }

    public void setZones(List<ZoneBean> zones) {
        this.zones = zones;
    }

    public boolean add(ZoneBean zone) {
        if(this.zones == null)
            this.zones = new ArrayList<>();
        return zones.add(zone);
    }
}
