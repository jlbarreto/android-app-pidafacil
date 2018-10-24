package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 07-29-15.
 * Bean de Estados(departamentos)
 * Su funcionalidad es obtener valores despues
 * del parseo del JSON.
 *
 * Posee Municipios y Zonas relacionadas en un
 * esquema de objetos dependientes.
 */
public class StateBean {
    private Integer id;
    private String state;
    private Integer countryId;
    private List<MunicipalityBean> municipalities;

    public StateBean() {
        municipalities = new ArrayList<>();
    }

    public StateBean(Integer id, String state, Integer countryId) {
        this.id = id;
        this.state = state;
        this.countryId = countryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public List<MunicipalityBean> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<MunicipalityBean> municipalities) {
        this.municipalities = municipalities;
    }

    public boolean add(MunicipalityBean municipality) {
        if(this.municipalities == null)
            this.municipalities = new ArrayList<>();
        return getMunicipalities().add(municipality);
    }
}
