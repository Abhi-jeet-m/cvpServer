package com.integra.pledgeapp.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "template_master")
public class Template_Master {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int ID;

	@Column(name = "TEMPLATE_DESCRIPTION", length = 100, columnDefinition = "varchar(100) default ''")
	private String TEMPLATE_DESCRIPTION;

	@Column(name = "NO_OF_FIELDS")
	private int NO_OF_FIELDS;

	@Column(name = "STATUS", length = 5, columnDefinition = "int default 0")
	private String STATUS;

	@Column(name = "CREATED_ON")
	private Timestamp CREATED_ON;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTEMPLATE_DESCRIPTION() {
		return TEMPLATE_DESCRIPTION;
	}

	public void setTEMPLATE_DESCRIPTION(String tEMPLATE_DESCRIPTION) {
		TEMPLATE_DESCRIPTION = tEMPLATE_DESCRIPTION;
	}

	public int getNO_OF_FIELDS() {
		return NO_OF_FIELDS;
	}

	public void setNO_OF_FIELDS(int nO_OF_FIELDS) {
		NO_OF_FIELDS = nO_OF_FIELDS;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public Timestamp getCREATED_ON() {
		return CREATED_ON;
	}

	public void setCREATED_ON(Timestamp cREATED_ON) {
		CREATED_ON = cREATED_ON;
	}

	@Override
	public String toString() {
		return "Template_Master [ID=" + ID + ", TEMPLATE_DESCRIPTION=" + TEMPLATE_DESCRIPTION + ", NO_OF_FIELDS="
				+ NO_OF_FIELDS + ", STATUS=" + STATUS + ", CREATED_ON=" + CREATED_ON + "]";
	}

	
}
