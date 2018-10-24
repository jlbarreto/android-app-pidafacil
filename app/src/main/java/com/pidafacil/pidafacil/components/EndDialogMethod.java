package com.pidafacil.pidafacil.components;

/**
 * Created by victor on 03-24-15.
 * Al finalizar un cuadro de dialogo Custom, se ejecutara este metodo
 * que probablemente cambie valores en la actividad del fragmento o activity.
 */
public interface EndDialogMethod {

    public void endDialog(Object value, int operation);

}
