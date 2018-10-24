package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.util.ParseJsonObject;
import com.pidafacil.pidafacil.util.ParseJsonArray;

public class ProductBean implements ParseJsonArray, ParseJsonObject{
	private Integer id;
	private String name;
	private String description;
	private Float value;
	private Integer section_id;
	private String slug;
	private int activate;
	private String imageUri;
	private byte[] img;
	
	private List<ConditionBean> conditions;
	private List<IngredientBean> ingredients;
	
	public ProductBean() {
		// TODO Auto-generated constructor stub
		this.conditions = new ArrayList<ConditionBean>();
		this.ingredients = new ArrayList<IngredientBean>();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
	public Integer getSection_id() {
		return section_id;
	}
	public void setSection_id(Integer section_id) {
		this.section_id = section_id;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public int getActivate() {
		return activate;
	}
	public void setActivate(int activate) {
		this.activate = activate;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}
	
	public List<ConditionBean> getConditions() {
		return conditions;
	}
	
	public List<IngredientBean> getIngredients() {
		return ingredients;
	}
	
	public void addCondition(ConditionBean bean){
		conditions.add(bean);
	}
	
	public void addIngredient(IngredientBean bean){
		ingredients.add(bean);
	}
	
	@Override
	public String toString() {
		return "ProductBean [id=" + id + ", name=" + name + ", description="
				+ description + ", value=" + value + ", section_id="
				+ section_id + ", slug=" + slug + ", activate=" + activate
				+ ", imageUri=" + imageUri + ", img=" + Arrays.toString(img)
				+ "]";
	}
	
	public List<ProductBean> parseArray(JSONObject jsonStr) {
		// TODO Auto-generated method stub
		List<ProductBean> l = new ArrayList<ProductBean>();
		try {
			JSONArray arr = jsonStr.getJSONArray("data");			
			JSONObject obj = new JSONObject();
			
			if(arr != null){
				for(int i = 0 ; i < arr.length() ; i++){
					obj = arr.getJSONObject(i);
					ProductBean bean = new ProductBean();
					
					bean.setId(obj.getInt("product_id"));
					bean.setName(obj.getString("product"));
					bean.setDescription(obj.getString("description"));
					bean.setValue((float) obj.getDouble("value"));
					bean.setSection_id(obj.getInt("section_id"));
					bean.setSlug(obj.getString("slug"));
										
					try{
						bean.setImageUri(obj.getString("image_web"));					
						l.add(bean);
					}catch(JSONException e){
						l.add(bean); continue;	
					}
					
				}
			} 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error Json "+e.getMessage());
		}
		
		return l;
	}

	public ProductBean parseObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		ProductBean bean = new ProductBean();
		
		try {
			JSONObject obj = jsonObject.getJSONObject("data");
			
			bean.setId(obj.getInt("product_id"));
			bean.setName(obj.getString("product"));
			bean.setDescription(obj.getString("description"));
			bean.setValue((float) obj.getDouble("value"));
			bean.setSection_id(obj.getInt("section_id"));
			bean.setImageUri(obj.getString("image_web"));
			bean.setSlug(obj.getString("slug"));
			
			JSONArray cond_arr = obj.getJSONArray("conditions");
			JSONArray ingr_arr = obj.getJSONArray("ingredients");
			
			for (int x1 = 0; x1 < cond_arr.length(); x1++) {
				JSONObject object = (JSONObject) cond_arr.get(x1);
				ConditionBean bean0 = new ConditionBean(object.getInt("condition_id"), 
						object.getString("condition"));
				
				JSONArray opt_arr = object.getJSONArray("options");
				for (int x2 = 0; x2 < opt_arr.length(); x2++) {
					JSONObject object0 = (JSONObject) opt_arr.get(x2);
					bean0.addOption(new OptionBean(null, object0.getInt("condition_option_id"), 
							object0.getString("condition_option")));
				}
				
				bean.addCondition(bean0);				
			}
			
			for (int x1 = 0; x1 < ingr_arr.length(); x1++) {
				JSONObject object0 = (JSONObject) ingr_arr.get(x1);
				bean.addIngredient(new IngredientBean(object0.getInt("ingredient_id"), 
						object0.getString("ingredient"), object0.getInt("restaurant_id"), 
						((JSONObject) object0.getJSONObject("pivot")).getInt("removable") ));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error Json "+e.getMessage());
		}		
		return bean;
	}
	
	
	
}
