package com.example.model;

public class Author {
	int id;
	String name;
	String phone;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "Author [id=" + id + ", name=" + name + ", phone=" + phone + "]";
	}
	
	
	

}
