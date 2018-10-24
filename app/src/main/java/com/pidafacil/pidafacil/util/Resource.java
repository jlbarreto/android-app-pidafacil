package com.pidafacil.pidafacil.util;

import android.util.Log;

import com.pidafacil.pidafacil.singleton.UD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Resource {
	
	public static final String RESOURCE_URI = "http://images.pf.techmov.co";
    public static final String APP_ICONS = RESOURCE_URI + "/app-icons/";
    //public static final String APP_ICONS = "http://192.168.0.13/app-icons/";
    public static byte[] getImageBytesFromURI(String URI){
        try {
            InputStream input = new URL(URI).openStream();
            return getImageBytes(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String mapping;

    public static byte[] getImageBytes(String mapping){
        if(mapping==null)
            return null;
        if(mapping.equals("null"))
            return null;
        byte[] cache = (byte[]) UD.getInstance()
                .getFromCache("cache_img_"+mapping);
        Resource.mapping = mapping;

        try {
            URL url = new URL(RESOURCE_URI.concat(mapping));
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            if(cache==null)
                return getImageBytes(connection.getInputStream());
            else
                return cache;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getImageBytes(InputStream input) throws  Exception{
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[(1024*2)];
            byte[] outBytes;
            int content = 0;

            while(-1 != (content = input.read(buffer))){
                output.write(buffer, 0, content);
            }

            input.close();
            outBytes = output.toByteArray();
            UD.getInstance().putInCache("cache_img_"+mapping, outBytes);
            return outBytes;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Exception: "+e.getMessage());
        }
        return null;
    }

}
