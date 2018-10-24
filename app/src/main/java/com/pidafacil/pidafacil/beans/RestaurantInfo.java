package com.pidafacil.pidafacil.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidafacil.pidafacil.util.ParseJsonObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantInfo implements ParseJsonObject{
	private Integer restaurantId;
	private String name;
	private String address;
	private String slogan;
	private byte[] img;
    private byte[] imgLogo;
	private String imgUri;
    private String imgUriLogo;
    List<Hours> hours = new ArrayList<>();
    private String description;
    private String minPrice;
    private String maxPrice;
    private String hubicacion;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean addHour(int day, String opening, String closing, int typeService) {
        return hours.add(new Hours(day, opening, closing, typeService));
    }

    public List<Hours> getHours() {
        return hours;
    }

    public void setHours(List<Hours> hours) {
        this.hours = hours;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSlogan() {
		return slogan;
	}
	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}
	
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getHubicacion() {
        return hubicacion;
    }

    public void setHubicacion(String hubicacion) {
        this.hubicacion = hubicacion;
    }

    public String getImgUri() {
		return imgUri;
	}
	public void setImgUri(String imgUri) {
		this.imgUri = imgUri;
	}
	@Override
	public String toString() {
		return "RestaurantInfo [restaurantId=" + restaurantId + ", name="
				+ name + ", address=" + address + ", slogan=" + slogan + "]";
	}

    public String getImgUriLogo() {
        return imgUriLogo;
    }

    public void setImgUriLogo(String imgUriLogo) {
        this.imgUriLogo = imgUriLogo;
    }

    public RestaurantInfo parseObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		RestaurantInfo bean = new RestaurantInfo();
		try {
			jsonObject = jsonObject.getJSONObject("data");
			bean.setRestaurantId(jsonObject.getInt("restaurant_id"));
			bean.setName(jsonObject.getString("name"));
			bean.setAddress(jsonObject.getString("address"));

            JSONObject subObj = jsonObject.getJSONObject("landing_page");
			bean.setSlogan(subObj.getString("slogan"));
			bean.setImgUri(subObj.getString("header"));
            bean.setImgUriLogo(subObj.getString("logo"));
            bean.setDescription(subObj.getString("text_1"));
            bean.setMinPrice(jsonObject.getString("min_price"));
            bean.setMaxPrice(jsonObject.getString("max_price"));
            JSONArray arr = jsonObject.getJSONArray("schedules");

            if(arr.length()>0){
                for(int i = 0; i<arr.length(); i++){
                    int d = arr.getJSONObject(i).getInt("day_id");
                    String op = arr.getJSONObject(i).getString("opening_time");
                    String cl = arr.getJSONObject(i).getString("closing_time");
                    int type = arr.getJSONObject(i).getInt("service_type_id");

                    bean.addHour(d, op, cl, type);
                }
            }

            if(jsonObject.getString("branches") != null){
                bean.setHubicacion(jsonObject.getString("branches"));
            }

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Json Parse Error "+e.getMessage());
		}
		return bean;
	}
	
	public class Hours{
        private int day;
        private String opening;
        private String closing;
        private int typeService;

        public Hours(int day, String opening, String closing,int typeService) {
            this.day = day;
            this.opening = opening;
            this.closing = closing;
            this.typeService = typeService;
        }

        public Hours(){  }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public String getOpening() {
            return opening;
        }

        public void setOpening(String opening) {
            this.opening = opening;
        }

        public String getClosing() {
            return closing;
        }

        public void setClosing(String closing) {
            this.closing = closing;
        }

        public int getTypeService() {
            return typeService;
        }

        public void setTypeService(int typeService) {
            this.typeService = typeService;
        }
    }
	
}
