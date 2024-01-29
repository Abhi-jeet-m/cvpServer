package com.integra.pledgeapp.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Emp_Master_CompositeKey implements Serializable {

	@Column(name = "EMP_ID", length = 10)
	private String EMP_ID;
	
	@Column(name = "EMP_COMPANY", length = 50)
	private String EMP_COMPANY;
	
	@Column(name = "EMP_GROUP", length = 50, columnDefinition = "varchar(255) default ''")
	private String EMP_GROUP;

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getEMP_COMPANY() {
		return EMP_COMPANY;
	}

	public void setEMP_COMPANY(String eMP_COMPANY) {
		EMP_COMPANY = eMP_COMPANY;
	}

	public String getEMP_GROUP() {
		return EMP_GROUP;
	}

	public void setEMP_GROUP(String eMP_GROUP) {
		EMP_GROUP = eMP_GROUP;
	}
	
	
}
