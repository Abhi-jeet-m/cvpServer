package com.integra.pledgeapp.beans;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Employee_action_master")
public class Employee_action_master {

	@Id
	@Column(name="SL_NO")
	private int SL_NO;
	@Column(name="DOCUMENT_SCOPE", length = 50)
	private String DOCUMENT_SCOPE;
	@Column(name="DOCUMENT_NAME", length = 50)
	private String DOCUMENT_NAME;
	@Column(name="DOCUMENT_CODE", length = 50)
	private String DOCUMENT_CODE;
	@Column(name="DOCUMENT_DATE", length = 50)
	private Date DOCUMENT_DATE;
	@Column(name="ACTION_TYPE", length = 50)
	private String ACTION_TYPE;
	@Column(name="ENABLE_STATUS", nullable = false, columnDefinition = "int default 0")
	private int ENABLE_STATUS;
	@Column(name="GENERATE_PDF", nullable = false, columnDefinition = "int default 1")
	private int GENERATE_PDF;
	@Column(name="FILE_NAME", length = 50)
	private String FILE_NAME;
	public int getSL_NO() {
		return SL_NO;
	}
	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}
	public String getDOCUMENT_SCOPE() {
		return DOCUMENT_SCOPE;
	}
	public void setDOCUMENT_SCOPE(String dOCUMENT_SCOPE) {
		DOCUMENT_SCOPE = dOCUMENT_SCOPE;
	}
	public String getDOCUMENT_NAME() {
		return DOCUMENT_NAME;
	}
	public void setDOCUMENT_NAME(String dOCUMENT_NAME) {
		DOCUMENT_NAME = dOCUMENT_NAME;
	}
	public String getDOCUMENT_CODE() {
		return DOCUMENT_CODE;
	}
	public void setDOCUMENT_CODE(String dOCUMENT_CODE) {
		DOCUMENT_CODE = dOCUMENT_CODE;
	}
	public Date getDOCUMENT_DATE() {
		return DOCUMENT_DATE;
	}
	public void setDOCUMENT_DATE(Date dOCUMENT_DATE) {
		DOCUMENT_DATE = dOCUMENT_DATE;
	}
	public String getACTION_TYPE() {
		return ACTION_TYPE;
	}
	public void setACTION_TYPE(String aCTION_TYPE) {
		ACTION_TYPE = aCTION_TYPE;
	}
	@Override
	public String toString() {
		return "Employee_action_master [SL_NO=" + SL_NO + ", DOCUMENT_SCOPE=" + DOCUMENT_SCOPE + ", DOCUMENT_NAME="
				+ DOCUMENT_NAME + ", DOCUMENT_CODE=" + DOCUMENT_CODE + ", DOCUMENT_DATE=" + DOCUMENT_DATE
				+ ", ACTION_TYPE=" + ACTION_TYPE + ", ENABLE_STATUS=" + ENABLE_STATUS + "]";
	}
	public int getENABLE_STATUS() {
		return ENABLE_STATUS;
	}
	public void setENABLE_STATUS(int eNABLE_STATUS) {
		ENABLE_STATUS = eNABLE_STATUS;
	}
	
	public String getFILE_NAME() {
		return FILE_NAME;
	}
	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}
	public int getGENERATE_PDF() {
		return GENERATE_PDF;
	}
	public void setGENERATE_PDF(int gENERATE_PDF) {
		GENERATE_PDF = gENERATE_PDF;
	}
}
