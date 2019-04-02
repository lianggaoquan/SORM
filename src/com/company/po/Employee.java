package com.company.po;

import java.sql.*;
public class Employee {
	private String name;
	private Integer id;
	private java.sql.Date hiredate;
	private String email;


	public String getName(){
		return name;
	}
	public Integer getId(){
		return id;
	}
	public java.sql.Date getHiredate(){
		return hiredate;
	}
	public String getEmail(){
		return email;
	}


	public void setName(String name){
		this.name=name;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public void setHiredate(java.sql.Date hiredate){
		this.hiredate=hiredate;
	}
	public void setEmail(String email){
		this.email=email;
	}
}
