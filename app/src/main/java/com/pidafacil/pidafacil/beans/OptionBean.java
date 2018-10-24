package com.pidafacil.pidafacil.beans;

public class OptionBean {
	
	private Integer id;
	private Integer conditionOptionId;
	private String description;
	
	public OptionBean(Integer id, Integer conditionOptionId, String description) {
		this.id = id;
		this.conditionOptionId = conditionOptionId;
		this.description = description;
	}
	
	public OptionBean() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getConditionOptionId() {
		return conditionOptionId;
	}

	public void setConditionOptionId(Integer conditionOptionId) {
		this.conditionOptionId = conditionOptionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "OptionBean [id=" + id + ", conditionOptionId="
				+ conditionOptionId + ", description=" + description + "]";
	}
	
}
