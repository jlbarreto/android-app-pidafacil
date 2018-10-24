package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.util.ParseJsonArray;

public class RestaurantBean implements ParseJsonArray{
	private Integer restaurantId;
	private String name;
	private String imgUri;
	private String description;

	public RestaurantBean() {
		// TODO Auto-generated constructor stub
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUri() {
		return imgUri;
	}

	public void setImgUri(String imgUri) {
		this.imgUri = imgUri;
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
	public String toString() {
		return "RestaurantBean [restaurantId=" + restaurantId + ", name="
				+ name + ", imgUri=" + imgUri + "]";
	}

	public List<RestaurantBean> parseArray(JSONObject jsonStr) {
		// TODO Auto-generated method stub
		List<RestaurantBean> l = new ArrayList<RestaurantBean>();
		JSONArray arr;
		
		try {
			arr = jsonStr.getJSONArray("data");
			for (int i = 0 ; i < arr.length() ; i++) {
				RestaurantBean bean = new RestaurantBean();
				bean.setRestaurantId(Integer.parseInt(String.valueOf(((JSONObject) arr.get(i)).get("restaurant_id"))));
				bean.setName(((String) ((JSONObject) arr.get(i)).get("name")));
				try{
					JSONObject o = ((JSONObject) arr.get(i)).getJSONObject("landing_page");
                    bean.setDescription(o.getString("text_1"));
					bean.setImgUri(o.getString("logo"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("No se pudo obtener el logo para "+bean.getName());
				}
				l.add(bean);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return l;
	}
	
}
