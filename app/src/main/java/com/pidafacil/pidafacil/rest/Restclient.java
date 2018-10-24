package com.pidafacil.pidafacil.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cliente del WS de pidafacil.
 * @author victor
 */
public class Restclient {
	//public static final String URI = "http://api.pidafacil.com";//Servidor Publico
	//public static final String URI = "http://127.0.0.1:8080/pidafacil-api/public";
	//public static final String URI = "http://198.74.52.173";
	public static final String URI = "http://pf.cocodeteam.com";
	//public static final String URI = "http://192.168.0.13/pidafacil/pidafacil-api/public";// Server Virtual Box
	public static final String USER = "api@pidafacil.com";
	public static final String PASS = "IadfogwfoOnHH0nnS3tmeE0XlwK46m";
	private String credentials = Restclient.USER.concat(":").concat(Restclient.PASS);
	private byte[] credBase64 = (Base64.encodeBase64(credentials.getBytes()));
	private JSONObject params = new JSONObject();
	private HttpClient client;
	private HttpPost post;
	private String mapping;
	private HttpResponse response;
	
	/**
	 * Constructor del cliente del WS. Recibe el mappeo de la ruta que accede.
	 * www.host.com/mapping
	 * @param mapping
	 */
	public Restclient(String mapping) {
		this.mapping = mapping;
		prepareClient();
	}
	
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	
	/***
	 * Limpia los parametros
	 */
	public void clearParams(){
		this.params = new JSONObject();
	}
	
	/**
	 * Agrega un parametro tipo String para enviarlo al host.
	 * @param name
	 * @param value``0
	 */
	public void addParam(String name, String value){
		try {
			params.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("El parametro "+name+" no pudo ser agregado ("+e.getMessage()+")");
		}
	}
	
	/**
	 * Antes de agregar los parametros, debe preparar el cliente seteando los valores de las cabeceras.
	 */
	public void prepareClient(){
		params = new JSONObject();
		client = new DefaultHttpClient();
		post = new HttpPost(Restclient.URI.concat(mapping));
		post.addHeader("Authorization", "Basic ".concat(new String(credBase64)));
		post.addHeader("Content-Type","application/json");
	}
	
	/**
	 * Agrega los parametros a la cabecera httpPost
	 */
	public void prepareParams(){
        try {
            StringEntity entity = new StringEntity(params.toString(), "UTF-8");
            post.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/**
	 * Ejecuta la peticion solicitada.
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public StringBuilder execute() throws ClientProtocolException, IOException, JSONException{		
		response = client.execute(post);
		BufferedReader rd = new BufferedReader(	new InputStreamReader(
				response.getEntity().getContent()));
		
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return (result);
	}

	/**
	 * Ejecuta una peticion enviando contenido tipo StringBuffer directamente en lugar de parametros.
	 * @param jsonStr
	 * @return
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public StringBuilder execute(String jsonStr) throws ClientProtocolException, IOException, JSONException{
		try {
			StringEntity entity = new StringEntity(jsonStr.toString(), "UTF-8");
			post.setEntity(entity);		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execute();
	}
	
}
