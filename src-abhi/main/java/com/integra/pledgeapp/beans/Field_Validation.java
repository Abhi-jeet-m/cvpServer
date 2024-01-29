package com.integra.pledgeapp.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "field_validation")
public class Field_Validation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SI_NO")
	private int SI_NO;

	@Column(name = "FIELD_NAME", length = 50, columnDefinition = "varchar(100) default ''")
	private String FIELD_NAME;

	@Column(name = "FIELD_TYPE")
	private String FIELD_TYPE;

	@Column(name = "SIZE")
	private String SIZE;

	@Column(name = "NULLABLE")
	private String NULLABLE;

	@Column(name = "FIELD_VALIDATION_REQ", length = 10, columnDefinition = "varchar(10) default N")
	private String FIELD_VALIDATION_REQ;

	@Column(name = "FIELD_VALIDATION_RULE", length = 200, columnDefinition = "varchar(200) default ''")
	private String FIELD_VALIDATION_RULE;

	@Column(name = "IS_MANDATORY", length = 10, columnDefinition = "varchar(10) default N")
	private String IS_MANDATORY;

	@Column(name = "IS_ENABLED", length = 10, columnDefinition = "varchar(10) default N")
	private String IS_ENABLED;

	@Column(name = "CREATED_BY", length = 200, columnDefinition = "varchar(200) default ''")
	private String CREATED_BY;

	@Column(name = "MODIFIED_BY", length = 200, columnDefinition = "varchar(200) default ''")
	private String MODIFIED_BY;

	@Column(name = "UPDATED_ON")
	private Timestamp UPDATED_ON;

	@Column(name = "TEMP_ID" )
	private int TEMP_ID;
	
	
	public int getTEMP_ID() {
		return TEMP_ID;
	}

	public void setTEMP_ID(int tEMP_ID) {
		TEMP_ID = tEMP_ID;
	}

	public int getSI_NO() {
		return SI_NO;
	}

	public void setSI_NO(int SI_NO) {
		SI_NO = SI_NO;
	}

	public String getFIELD_NAME() {
		return FIELD_NAME;
	}

	public void setFIELD_NAME(String fIELD_NAME) {
		FIELD_NAME = fIELD_NAME;
	}

	public String getFIELD_TYPE() {
		return FIELD_TYPE;
	}

	public void setFIELD_TYPE(String fIELD_TYPE) {
		FIELD_TYPE = fIELD_TYPE;
	}

	public String getSIZE() {
		return SIZE;
	}

	public void setSIZE(String sIZE) {
		SIZE = sIZE;
	}

	public String getNULLABLE() {
		return NULLABLE;
	}

	public void setNULLABLE(String nULLABLE) {
		NULLABLE = nULLABLE;
	}

	public String getFIELD_VALIDATION_REQ() {
		return FIELD_VALIDATION_REQ;
	}

	public void setFIELD_VALIDATION_REQ(String fIELD_VALIDATION_REQ) {
		FIELD_VALIDATION_REQ = fIELD_VALIDATION_REQ;
	}

	public String getFIELD_VALIDATION_RULE() {
		return FIELD_VALIDATION_RULE;
	}

	public void setFIELD_VALIDATION_RULE(String fIELD_VALIDATION_RULE) {
		FIELD_VALIDATION_RULE = fIELD_VALIDATION_RULE;
	}

	public String getIS_MANDATORY() {
		return IS_MANDATORY;
	}

	public void setIS_MANDATORY(String iS_MANDATORY) {
		IS_MANDATORY = iS_MANDATORY;
	}

	public String getIS_ENABLED() {
		return IS_ENABLED;
	}

	public void setIS_ENABLED(String iS_ENABLED) {
		IS_ENABLED = iS_ENABLED;
	}

	public String getCREATED_BY() {
		return CREATED_BY;
	}

	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}

	public String getMODIFIED_BY() {
		return MODIFIED_BY;
	}

	public void setMODIFIED_BY(String mODIFIED_BY) {
		MODIFIED_BY = mODIFIED_BY;
	}

	public Timestamp getUPDATED_ON() {
		return UPDATED_ON;
	}

	public void setUPDATED_ON(Timestamp uPDATED_ON) {
		UPDATED_ON = uPDATED_ON;
	}

	@Override
	public String toString() {
		return "Field_Validation [SI_NO=" + SI_NO + ", FIELD_NAME=" + FIELD_NAME + ", FIELD_TYPE=" + FIELD_TYPE
				+ ", SIZE=" + SIZE + ", NULLABLE=" + NULLABLE + ", FIELD_VALIDATION_REQ=" + FIELD_VALIDATION_REQ
				+ ", FIELD_VALIDATION_RULE=" + FIELD_VALIDATION_RULE + ", IS_MANDATORY=" + IS_MANDATORY
				+ ", IS_ENABLED=" + IS_ENABLED + ", CREATED_BY=" + CREATED_BY + ", MODIFIED_BY=" + MODIFIED_BY
				+ ", UPDATED_ON=" + UPDATED_ON + ", TEMP_ID=" + TEMP_ID + "]";
	}
	

}
