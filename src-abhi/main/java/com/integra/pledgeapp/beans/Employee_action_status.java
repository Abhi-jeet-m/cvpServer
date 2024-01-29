package com.integra.pledgeapp.beans;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Employee_action_status")
public class Employee_action_status {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SL_NO")
	private int SL_NO;
	@Column(name="EMP_CODE", length = 50)
	private String EMP_CODE;
	@Column(name="EMP_GROUP", length = 50)
	private String EMP_GROUP;
	@Column(name="EMP_NAME", length = 100)
	private String EMP_NAME;
	@Column(name="COMPANY_CODE", length = 50)
	private String COMPANY_CODE;
	@Column(name="DOCUMENT_CODE", length = 50)
	private String DOCUMENT_CODE;
	@Column(name="ACTION_STATUS", nullable = false, columnDefinition = "int default 0")
	private int ACTION_STATUS;
	@Column(name="ACTION_DATE", length = 50)
	private Timestamp ACTION_DATE;
	@Column(name="ACTION_TYPE", nullable = false, columnDefinition = "int default 0")
	private int ACTION_TYPE;
	@Column(name="SIGN_STATUS", nullable = false, columnDefinition = "int default 0")
	private int SIGN_STATUS;
	@Column(name="SIGN_DATE", length = 50)
	private Timestamp SIGN_DATE;
	@Column(name="SIGN_TYPE", length = 50)
	private String SIGN_TYPE;
	@Column(name="SIGNER_NAME", columnDefinition = "varchar(255) default ''")
	private String SIGNER_NAME;
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	public String getSIGN_TYPE() {
		return SIGN_TYPE;
	}
	public void setSIGN_TYPE(String sIGN_TYPE) {
		SIGN_TYPE = sIGN_TYPE;
	}
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
	
	@Override
	public String toString() {
		return "Employee_action_status [SL_NO=" + SL_NO + ", EMP_CODE=" + EMP_CODE + ", COMPANY_CODE=" + COMPANY_CODE
				+ ", DOCUMENT_CODE=" + DOCUMENT_CODE + ", ACTION_STATUS=" + ACTION_STATUS + ", ACTION_DATE="
				+ ACTION_DATE + ", SIGN_STATUS=" + SIGN_STATUS + ", SIGN_DATE=" + SIGN_DATE + "]";
	}
	public int getACTION_STATUS() {
		return ACTION_STATUS;
	}
	public void setACTION_STATUS(int aCTION_STATUS) {
		ACTION_STATUS = aCTION_STATUS;
	}
	public int getSIGN_STATUS() {
		return SIGN_STATUS;
	}
	public void setSIGN_STATUS(int sIGN_STATUS) {
		SIGN_STATUS = sIGN_STATUS;
	}
	public Timestamp getACTION_DATE() {
		return ACTION_DATE;
	}
	public void setACTION_DATE(Timestamp aCTION_DATE) {
		ACTION_DATE = aCTION_DATE;
	}
	public Timestamp getSIGN_DATE() {
		return SIGN_DATE;
	}
	public void setSIGN_DATE(Timestamp sIGN_DATE) {
		SIGN_DATE = sIGN_DATE;
	}
	public int getACTION_TYPE() {
		return ACTION_TYPE;
	}
	public void setACTION_TYPE(int aCTION_TYPE) {
		ACTION_TYPE = aCTION_TYPE;
	}
	public String getEMP_GROUP() {
		return EMP_GROUP;
	}
	public void setEMP_GROUP(String eMP_GROUP) {
		EMP_GROUP = eMP_GROUP;
	}
	public String getSIGNER_NAME() {
		return SIGNER_NAME;
	}
	public void setSIGNER_NAME(String sIGNER_NAME) {
		SIGNER_NAME = sIGNER_NAME;
	}
	
}
