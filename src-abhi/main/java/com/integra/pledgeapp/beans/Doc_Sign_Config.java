package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Doc_Sign_Config")
public class Doc_Sign_Config {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SL_NO")
	private int SL_NO;
	
	@Column(name="DOC_CODE", length = 50, columnDefinition = "varchar(50) default ''")
	private String DOC_CODE;
	
	@Column(name="DSC_TOKEN_ID", length = 50, columnDefinition = "varchar(50) default ''")
	private String DSC_TOKEN_ID;
	
	@Column(name="ENABLE_STATUS")
	private int ENABLE_STATUS;
	
	@Column(name="SIGN_INFO", length = 1024, columnDefinition = "varchar(1024) default ''")
	private String SIGN_INFO;
	
	@Column(name="SIGN_STATUS", length = 50, columnDefinition = "varchar(50) default ''")
	private String SIGN_STATUS;
	
	@Column(name="SIGN_DISPLAY_INFO", length = 1024, columnDefinition = "varchar(1024) default ''")
	private String SIGN_DISPLAY_INFO;
	
	@Column(name="SIGN_ORDER", length = 50, columnDefinition = "varchar(50) default ''")
	private String SIGN_ORDER;

	public int getSL_NO() {
		return SL_NO;
	}

	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}

	public String getDOC_CODE() {
		return DOC_CODE;
	}

	public void setDOC_CODE(String dOC_CODE) {
		DOC_CODE = dOC_CODE;
	}

	public String getDSC_TOKEN_ID() {
		return DSC_TOKEN_ID;
	}

	public void setDSC_TOKEN_ID(String dSC_TOKEN_ID) {
		DSC_TOKEN_ID = dSC_TOKEN_ID;
	}

	public int getENABLE_STATUS() {
		return ENABLE_STATUS;
	}

	public void setENABLE_STATUS(int eNABLE_STATUS) {
		ENABLE_STATUS = eNABLE_STATUS;
	}

	public String getSIGN_INFO() {
		return SIGN_INFO;
	}

	public void setSIGN_INFO(String sIGN_INFO) {
		SIGN_INFO = sIGN_INFO;
	}

	public String getSIGN_DISPLAY_INFO() {
		return SIGN_DISPLAY_INFO;
	}

	public void setSIGN_DISPLAY_INFO(String sIGN_DISPLAY_INFO) {
		SIGN_DISPLAY_INFO = sIGN_DISPLAY_INFO;
	}

	public String getSIGN_STATUS() {
		return SIGN_STATUS;
	}

	public void setSIGN_STATUS(String sIGN_STATUS) {
		SIGN_STATUS = sIGN_STATUS;
	}

	public String getSIGN_ORDER() {
		return SIGN_ORDER;
	}

	public void setSIGN_ORDER(String sIGN_ORDER) {
		SIGN_ORDER = sIGN_ORDER;
	}
	

}
