package com.pidafacil.pidafacil.beans;

/**
 * Created by victor on 03-06-15.
 */
public class ReqOptionBean {
    private int condition_id;
    private int condition_option_id;

    public ReqOptionBean(int condition_id, int condition_option_id) {
        this.condition_id = condition_id;
        this.condition_option_id = condition_option_id;
    }

    public int getCondition_id() {
        return condition_id;
    }

    public void setCondition_id(int condition_id) {
        this.condition_id = condition_id;
    }

    public int getCondition_option_id() {
        return condition_option_id;
    }

    public void setCondition_option_id(int condition_option_id) {
        this.condition_option_id = condition_option_id;
    }
}
