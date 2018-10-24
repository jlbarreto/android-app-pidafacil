package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.util.ParseJsonArray;

public class SectionBean implements ParseJsonArray {
	
	private Integer sessionId;
	private Integer restaurantId;
	private String section;
	private Integer secciontOrderId;
	private String createAtStr;
	private String updatedAtStr;
	
	public SectionBean() {
		// TODO Auto-generated constructor stub
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Integer getSecciontOrderId() {
		return secciontOrderId;
	}

	public void setSecciontOrderId(Integer secciontOrderId) {
		this.secciontOrderId = secciontOrderId;
	}

	public String getCreateAtStr() {
		return createAtStr;
	}

	public void setCreateAtStr(String createAtStr) {
		this.createAtStr = createAtStr;
	}

	public String getUpdatedAtStr() {
		return updatedAtStr;
	}

	public void setUpdatedAtStr(String updatedAtStr) {
		this.updatedAtStr = updatedAtStr;
	}

	@Override
	public String toString() {
		return "SeccionBean [sessionId=" + sessionId + ", restaurantId="
				+ restaurantId + ", section=" + section + ", secciontOrderId="
				+ secciontOrderId + ", createAtStr=" + createAtStr
				+ ", updatedAtStr=" + updatedAtStr + "]";
	}

	public List<SectionBean> parseArray(JSONObject jsonStr) {
		// TODO Auto-generated method stub
		List<SectionBean> beans = new ArrayList<SectionBean>();
		try {
			JSONArray arr = jsonStr.getJSONArray("data");
			for (int i = 0 ; i < arr.length() ; i++) {
				JSONObject object = arr.getJSONObject(i);
				SectionBean bean = new SectionBean();
				bean.setSessionId(Integer.valueOf(object.getString("section_id")));
				bean.setRestaurantId(Integer.valueOf(object.getString("restaurant_id")));
				bean.setSection(object.getString("section"));
				bean.setSecciontOrderId(Integer.valueOf(object.getString("section_order_id")));
				beans.add(bean);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("No se pudo obtener el json "+e.getMessage());
		}
		return beans;
	}
	
}
