package com.pidafacil.pidafacil.beans;

public class IngredientBean {
	private Integer id;
	private String description;
	private Integer restaurantId;
	private int removable = 1;
    private boolean checked = true;
	
	public IngredientBean() {
		// TODO Auto-generated constructor stub
	}

	public IngredientBean(Integer id, String description, Integer restaurantId,
			int removable) {
		this.id = id;
		this.description = description;
		this.restaurantId = restaurantId;
		this.removable = removable;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public int getRemovable() {
		return removable;
	}

	public void setRemovable(int removable) {
		this.removable = removable;
	}

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
	public String toString() {
		return "IngredientBean [id=" + id + ", description=" + description
				+ ", restaurantId=" + restaurantId + "]";
	}
	
}
