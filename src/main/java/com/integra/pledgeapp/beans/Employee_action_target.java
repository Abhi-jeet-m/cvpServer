package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Employee_action_target")
public class Employee_action_target {

	@Id
	@Column(name="SL_NO")
	private int SL_NO;
	@Column(name="EMP_CODE", length = 50)
	private String EMP_CODE;
	@Column(name="COMPANY_CODE", length = 50)
	private String COMPANY_CODE;
	@Column(name="DOCUMENT_CODE", length = 50)
	private String DOCUMENT_CODE;
	@Column(name="GROUP_CODE", length = 50)
	private String GROUP_CODE;
	@Column(name="ENABLE_STATUS", nullable = false, columnDefinition = "int default 0")
	private int ENABLE_STATUS;
	
	@Column(name="DOCUMENT_FULL_NAME", length = 100, columnDefinition = "varchar default ''")
	private String DOCUMENT_FULL_NAME;
	public int getSL_NO() {
		return SL_NO;
	}
	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}
	public String getEMP_CODE() {
		return EMP_CODE;
	}
	public void setEMP_CODE(String eMP_CODE) {
		EMP_CODE = eMP_CODE;
	}
	public String getCOMPANY_CODE() {
		return COMPANY_CODE;
	}
	public void setCOMPANY_CODE(String cOMPANY_CODE) {
		COMPANY_CODE = cOMPANY_CODE;
	}
	public String getDOCUMENT_CODE() {
		return DOCUMENT_CODE;
	}
	public void setDOCUMENT_CODE(String dOCUMENT_CODE) {
		DOCUMENT_CODE = dOCUMENT_CODE;
	}
	public int getENABLE_STATUS() {
		return ENABLE_STATUS;
	}
	public void setENABLE_STATUS(int eNABLE_STATUS) {
		ENABLE_STATUS = eNABLE_STATUS;
	}
	public String getGROUP_CODE() {
		return GROUP_CODE;
	}
	public void setGROUP_CODE(String gROUP_CODE) {
		GROUP_CODE = gROUP_CODE;
	}
	
	@Override
	public String toString() {
		return "Employee_action_target [SL_NO=" + SL_NO + ", EMP_CODE=" + EMP_CODE + ", COMPANY_CODE=" + COMPANY_CODE
				+ ", DOCUMENT_CODE=" + DOCUMENT_CODE + ", GROUP_CODE=" + GROUP_CODE + ", ENABLE_STATUS=" + ENABLE_STATUS
				+ ", DOCUMENT_FULL_NAME=" + DOCUMENT_FULL_NAME + "]";
	}
	
	
}
