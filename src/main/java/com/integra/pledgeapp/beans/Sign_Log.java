package com.integra.pledgeapp.beans;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "sign_log")
public class Sign_Log {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SIGN_ID", length = 50)
	private int SIGN_ID;
	@Column(name = "EMP_ID", length = 10)
	private String EMP_ID;
	@Column(name = "EMP_GROUP", length = 50)
	private String EMP_GROUP;
	@Column(name = "EMP_COMPANY", length = 50)
	private String EMP_COMPANY;
	@Column(name = "EMP_NAME", length = 100)
	private String EMP_NAME;
	@Column(name = "DOC_CODE", length = 50)
	private String DOC_CODE;
	

	@Column(name = "SIGN_STATUS", length = 1)
	private int SIGN_STATUS;
	
	@Column(name = "SIGNED_ON", length = 50)
	@Temporal(TemporalType.TIMESTAMP)
	private Date SIGNED_ON;
	
	@Column(name = "SIGN_REMARKS", length = 100)
	private String SIGN_REMARKS;
	
	@Column(name = "FILE_PATH", length = 255)
	private String FILE_PATH;
	
	@Column(name = "REFERENCE_NO", length = 50)
	private String REFERENCE_NO;
	
	@Column(name = "CLIENT_IP", length = 15)
	private String CLIENT_IP;
	
	@Column(name = "MYSIGN_REF_TOKEN", length = 50)
	private String MYSIGN_REF_TOKEN;
	
	@Column(name ="SIGN_TYPE", length = 12)
	private String SIGN_TYPE;

	
	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	
	public Date getSIGNED_ON() {
		return SIGNED_ON;
	}

	public void setSIGNED_ON(Date sIGNED_ON) {
		SIGNED_ON = sIGNED_ON;
	}

	public String getSIGN_TYPE() {
		return SIGN_TYPE;
	}

	public void setSIGN_TYPE(String sIGN_TYPE) {
		SIGN_TYPE = sIGN_TYPE;
	}

	public String getCLIENT_IP() {
		return CLIENT_IP;
	}

	public void setCLIENT_IP(String cLIENT_IP) {
		CLIENT_IP = cLIENT_IP;
	}

	public String getMYSIGN_REF_TOKEN() {
		return MYSIGN_REF_TOKEN;
	}

	public void setMYSIGN_REF_TOKEN(String mYSIGN_REF_TOKEN) {
		MYSIGN_REF_TOKEN = mYSIGN_REF_TOKEN;
	}

	public int getSIGN_ID() {
		return SIGN_ID;
	}

	public void setSIGN_ID(int sIGN_ID) {
		SIGN_ID = sIGN_ID;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public int getSIGN_STATUS() {
		return SIGN_STATUS;
	}

	public void setSIGN_STATUS(int sIGN_STATUS) {
		SIGN_STATUS = sIGN_STATUS;
	}

	public String getSIGN_REMARKS() {
		return SIGN_REMARKS;
	}

	public void setSIGN_REMARKS(String sIGN_REMARKS) {
		SIGN_REMARKS = sIGN_REMARKS;
	}

	public String getFILE_PATH() {
		return FILE_PATH;
	}

	public void setFILE_PATH(String fILE_PATH) {
		FILE_PATH = fILE_PATH;
	}

	public String getREFERENCE_NO() {
		return REFERENCE_NO;
	}

	public void setREFERENCE_NO(String rEFERENCE_NO) {
		REFERENCE_NO = rEFERENCE_NO;
	}

	public String getEMP_GROUP() {
		return EMP_GROUP;
	}

	public void setEMP_GROUP(String eMP_GROUP) {
		EMP_GROUP = eMP_GROUP;
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

}
