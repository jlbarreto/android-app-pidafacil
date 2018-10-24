package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.util.ParseJsonArray;

public class CategoryBean implements ParseJsonArray{
	
	private Integer tagId;
	private String tagName;
	private Integer tagType;
	private String tagUrl;
	
	public CategoryBean() {
		// TODO Auto-generated constructor stub
	}
	
	public CategoryBean(Integer tagId, String tagName, Integer tagType, String tagUrl) {
		this.tagId	 = tagId;
		this.tagName = tagName;
		this.tagType = tagType;
		this.tagUrl	 = tagUrl;
	}
	
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public Integer getTagType() {
		return tagType;
	}
	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}
//agregando manejo de Tagurl
	public String getTagUrl() {
		return tagUrl;
	}

	public void setTagUrl(String tagUrl) {
		this.tagUrl = tagUrl;
	}

	@Override
	public String toString() {
		return "CategoryBean [tagId=" + tagId + ", tagName=" + tagName
				+ ", tagType=" + tagType + ", tagUrl=" + tagUrl	+ "]";
	}

	public List<CategoryBean> parseArray(JSONObject jsonStr) {
		// TODO Auto-generated method stub
		List<CategoryBean> l = new ArrayList<CategoryBean>();
		try {
			JSONArray arr = jsonStr.getJSONArray("data");
			for (int i = 0 ; i < arr.length() ; i++) {
				CategoryBean bean = new CategoryBean();
				bean.setTagId(Integer.valueOf((String) ((JSONObject) arr.get(i)).get("tag_id")));
				bean.setTagName((String) ((JSONObject) arr.get(i)).get("tag_name"));
				bean.setTagType(Integer.valueOf((String) ((JSONObject) arr.get(i)).get("tag_type_id")));
				bean.setTagUrl((String) ((JSONObject) arr.get(i)).get("image"));
				l.add(bean);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	
}
