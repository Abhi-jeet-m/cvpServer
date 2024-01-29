package com.integra.pledgeapp.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Sign_Doc_Details")
public class Sign_Doc_Details {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SL_NO")
	private int SL_NO;
	
	@Column(name="EMPID", length = 50)
	private String EMPID;
	
	@Column(name="DOC_CODE", length = 50)
	private String DOC_CODE;
	
	@Column(name="EMP_COMPANY", length = 50)
	private String EMP_COMPANY;
	
	@Column(name="EMP_GROUP", length = 50)
	private String EMP_GROUP;
	
	@Column(name="SIGN_ORDER", length = 5)
	private int SIGN_ORDER;
	
	@Column(name="DOC_PATH")
	private String DOC_PATH;
	
	@Column(name="TXNID", length = 50)
	private String TXNID;
	
	@Column(name="SIGN_DATE")
	private Timestamp SIGN_DATE;
	
	@Column(name="EMP_NAME", length = 50, columnDefinition = "varchar(50) default ''")
	private String EMP_NAME;
	
	@Column(name="SIGNER_NAME", length = 100)
	private String SIGNER_NAME;

	public int getSL_NO() {
		return SL_NO;
	}

	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}

	public String getEMPID() {
		return EMPID;
	}

	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}

	public String getDOC_CODE() {
		return DOC_CODE;
	}

	public void setDOC_CODE(String dOC_CODE) {
		DOC_CODE = dOC_CODE;
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

	public int getSIGN_ORDER() {
		return SIGN_ORDER;
	}

	public void setSIGN_ORDER(int sIGN_ORDER) {
		SIGN_ORDER = sIGN_ORDER;
	}

	public String getDOC_PATH() {
		return DOC_PATH;
	}

	public void setDOC_PATH(String dOC_PATH) {
		DOC_PATH = dOC_PATH;
	}

	public String getTXNID() {
		return TXNID;
	}

	public void setTXNID(String tXNID) {
		TXNID = tXNID;
	}

	public Timestamp getSIGN_DATE() {
		return SIGN_DATE;
	}

	public void setSIGN_DATE(Timestamp sIGN_DATE) {
		SIGN_DATE = sIGN_DATE;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public String getSIGNER_NAME() {
		return SIGNER_NAME;
	}

	public void setSIGNER_NAME(String sIGNER_NAME) {
		SIGNER_NAME = sIGNER_NAME;
	}
	
}
