package com.pidafacil.pidafacil.rest;

public class RestParser {
	
	public RestParser() {
		// TODO Auto-generated constructor stub
	}
	
	private String result;

	public void setResult(String result) {
		this.result = result;
	}
	
	public boolean isOk(){
		try{
			return result.substring(10, 14).equals("true");
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public static boolean isOk(String str){
		try{
			if(str.substring(10, 14).equals("true"))
				return true;
			else
				return false;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public String resultStr(String str){
		return str.substring(22,str.length()-1);
	}
	
	
}
