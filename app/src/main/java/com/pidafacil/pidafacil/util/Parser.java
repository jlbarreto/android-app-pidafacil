package com.pidafacil.pidafacil.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by victor on 03-06-15.
 */
public class Parser {

    public static int toInt(Object o){
        Integer i = Integer.valueOf(o.toString());
        if(i == null){
            return -1;
        }else{
            return (int) i;
        }
    }

    @Deprecated
    public static double decimalFormat(double t){
        return (double) Math.round((t * 100) / 100);
    }

    public static String decimalFormatString(double t){
        Locale locale  = new Locale("en", "UK");
        String pattern = "###0.00";
        DecimalFormat decimalFormat = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(t);
    }

}
