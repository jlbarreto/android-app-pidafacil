package com.pidafacil.pidafacil.util;

import android.util.Log;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by victor on 03-27-15.
 */
public class Validator {

    public static boolean isNull(Object object){
        return (object == null);
    }

    public static boolean isEmpty(String str){
        if(!isNull(str))
            return str.trim().equals("");
        else
            return false;
    }

    public static final boolean isEmptyInt(Integer x) {
        return (x == null || x == 0);
    }
    public static final boolean isEmptyLong(Long x) {
        return (x == null || x == 0);
    }
    public static final boolean isEmptyCollection(Collection<?> x) {
        return (x == null || x.size() == 0);
    }
    public static final boolean isEmptyMap(Map<?,?> x) {
        return (x == null || x.size() == 0);
    }
    public static final boolean isEmptyObject(Object[] x) {
        return (x == null || x.length == 0);
    }


    public static boolean isEmail(String emailAddress){
        if(emailAddress == null)
            return false;
        if(emailAddress.isEmpty())
            return false;

        if(emailAddress.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+" +
                "(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isValid(String creditCardNum) throws NumberFormatException {

        if(creditCardNum!=null){

            if(creditCardNum.length()<13){
                return false;
            }

            if(creditCardNum.trim().equals(""))
                return false;

            String str = creditCardNum.toString();
            int length = str.length();

            String[] sub = new String[]{str.substring(0,1),
                    str.substring(0,5),str.substring(6,length-1)};
            Integer number = Integer.valueOf(sub[0]);
            Integer n = Integer.valueOf(sub[1]);

            if( number != 0 && n!=null && sub[2].length()>=9 ){

                char[] chars = str.toCharArray();
                int[] pars = new int[length % 2 == 0 ? length / 2: (length -1) /2];
                int[] impars = new int[length % 2 == 0 ? length / 2: (length +1) /2];

                int position = 0;
                int p = 0;
                int i= 0;

                for(int x=0; x < length; x++){
                    position = (x+1);
                    if(position % 2 == 0){
                        pars[p]= (Character.getNumericValue(chars[x]));
                        p++;
                    }else{
                        impars[i]= (Character.getNumericValue(chars[x]));
                        i++;
                    }
                }

                for(int x=0; x<impars.length; x++)
                    impars[x]*=2;

                StringBuilder builder= new StringBuilder("");

                for(int num: pars)
                    builder.append(num);

                for(int num: impars)
                    builder.append(num);

                char[] digits = builder.toString().toCharArray();
                int result = 0;

                for(char e: digits)
                    result+=Character.getNumericValue(e);

                if( result % 10 == 0 ){
                    return true;
                }

            }
        }

        return false;
    }


    public static boolean validMonth(String month) {
        try{
            Integer m = Integer.valueOf(month);
            Log.d("INFO: ","month "+m);
            if(m!=null){
                if(m>0 && m <=12){
                    return true;
                }
            }
        }catch(NumberFormatException e){
        }
        return false;
    }

    public static boolean validYear(String year){
        try{
            int y = Integer.parseInt(year);

            int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
            String cyy = String.valueOf(currentYear).substring(3);
            currentYear = Integer.parseInt(cyy);

            if(y != 0){
                if(y > currentYear && y <=(currentYear+30)){
                    return true;
                }
            }
        }catch(NumberFormatException e){
        }
        return false;
    }

    public static boolean validSecureCode(String code){
        if(code != null){
            if(code.length()<=4 && code.length() >= 3){
                int code0 = Integer.parseInt(code);
                if(code0 != 0){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean validPassword(String password){
        if(password== null)
            return false;

        if(password.length() < 4)
            return false;

        return true;
    }

    public static boolean validHour(String hour, String min){
        try{
            int h = Integer.parseInt(hour);
            int m = Integer.parseInt(min);
            
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
