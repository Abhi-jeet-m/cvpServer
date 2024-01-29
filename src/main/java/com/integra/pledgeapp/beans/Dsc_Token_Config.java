package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Dsc_Token_Config")
public class Dsc_Token_Config {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SL_NO")
	private int SL_NO;
	
	@Column(name="DSC_TOKEN_ID")
	private String DSC_TOKEN_ID;
	
	@Column(name="DSC_TOKEN_INFO")
	private String DSC_TOKEN_INFO;
	
	@Column(name="EXTERNAL_IP")
	private String EXTERNAL_IP;
	
	@Column(name="EXTERNAL_PORT")
	private String EXTERNAL_PORT;
	
	@Column(name="PRESENCE_STATUS")
	private String PRESENCE_STATUS;
	
	@Column(name="USER")
	private String USER;
	
	@Column(name="SIGN_DISPLAY_INFO")
	private String SIGN_DISPLAY_INFO;

	public int getSL_NO() {
		return SL_NO;
	}

	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}

	public String getDSC_TOKEN_ID() {
		return DSC_TOKEN_ID;
	}

	public void setDSC_TOKEN_ID(String dSC_TOKEN_ID) {
		DSC_TOKEN_ID = dSC_TOKEN_ID;
	}

	public String getDSC_TOKEN_INFO() {
		return DSC_TOKEN_INFO;
	}

	public void setDSC_TOKEN_INFO(String dSC_TOKEN_INFO) {
		DSC_TOKEN_INFO = dSC_TOKEN_INFO;
	}

	public String getEXTERNAL_IP() {
		return EXTERNAL_IP;
	}

	public void setEXTERNAL_IP(String eXTERNAL_IP) {
		EXTERNAL_IP = eXTERNAL_IP;
	}

	public String getEXTERNAL_PORT() {
		return EXTERNAL_PORT;
	}

	public void setEXTERNAL_PORT(String eXTERNAL_PORT) {
		EXTERNAL_PORT = eXTERNAL_PORT;
	}

	public String getPRESENCE_STATUS() {
		return PRESENCE_STATUS;
	}

	public void setPRESENCE_STATUS(String pRESENCE_STATUS) {
		PRESENCE_STATUS = pRESENCE_STATUS;
	}

	public String getUSER() {
		return USER;
	}

	public void setUSER(String uSER) {
		USER = uSER;
	}

	public String getSIGN_DISPLAY_INFO() {
		return SIGN_DISPLAY_INFO;
	}

	public void setSIGN_DISPLAY_INFO(String sIGN_DISPLAY_INFO) {
		SIGN_DISPLAY_INFO = sIGN_DISPLAY_INFO;
	}
	
}
