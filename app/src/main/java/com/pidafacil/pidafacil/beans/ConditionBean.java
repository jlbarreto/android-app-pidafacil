package com.pidafacil.pidafacil.beans;

import java.util.ArrayList;
import java.util.List;

public class ConditionBean {
	private Integer id;
	private String description;
	private List<OptionBean> options;
	
	public ConditionBean() {
		// TODO Auto-generated constructor stub
		options = new ArrayList<OptionBean>();
	}

	public ConditionBean(Integer id,
			String description) {
		this.id = id;
		this.description = description;
		options = new ArrayList<OptionBean>();
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

	public void addOption(OptionBean optionBean){
		options.add(optionBean);
	}
	
	public List<OptionBean> getOptions() {
		return options;
	}

	@Override
	public String toString() {
		return "OptionBean [id=" + id + ", description=" + description + "]";
	}
	
	
}
