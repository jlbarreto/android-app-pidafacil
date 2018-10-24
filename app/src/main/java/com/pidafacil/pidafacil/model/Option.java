package com.pidafacil.pidafacil.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by victor on 03-11-15.
 */
public class Option extends RealmObject{
    private String id;
    private int conditionId;
    private int conditionOptionId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public int getConditionOptionId() {
        return conditionOptionId;
    }

    public void setConditionOptionId(int conditionOptionId) {
        this.conditionOptionId = conditionOptionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
