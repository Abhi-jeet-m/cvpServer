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
@Table(name="user_requests")
public class User_Requests {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SL_NO", length = 10)
	private int SL_NO;
	
	@Column(name = "EMP_ID", length = 10)
    private String EMP_ID;
	
	@Column(name="EMP_GROUP", length=50)
    private String EMP_GROUP;
	
	@Column(name = "EMP_NAME", length = 100, columnDefinition = "varchar(100) default ''")
    private String EMP_NAME;
	
	@Column(name = "EMP_DOB", length = 50)
	//private Date EMP_DOB;
    private String EMP_DOB;
	@Column(name = "MOBILE_AADHAAR", length = 12, columnDefinition = "varchar(12) default ''")
	private String MOBILE_AADHAAR; 
	
	@Column(name =  "MOBILE_MESSAGING" , length = 12, columnDefinition = "varchar(12) default ''")
	private String MOBILE_MESSAGING; 
	
	@Column(name = "USER_EMAIL", length = 50, columnDefinition = "varchar(50) default ''")
	private String USER_EMAIL;
	
	@Column(name="IP_ADDRESS")
	private String IP_ADDRESS;
	
	@Column(name="REQUESTED_TIME")
	private Timestamp REQUESTED_TIME;
	
	@Column(name = " VERIFIED_STATUS",length=5,  columnDefinition = "varchar(5) default ''")
	private String VERIFIED_STATUS;
	
	@Column(name="VERIFIED_TIME", columnDefinition = "timestamp default 0000-00-00 00:00:00")
	private Timestamp VERIFIED_TIME;
	
	@Column(name="VERIFIED_BY")
	private String VERIFIED_BY;
	
	@Column(name = "OTHER_DATA", length = 500,columnDefinition = "varchar(500) default ''")
	private String OTHER_DATA;
	

	//01-only mobile
	//10-only name
	//11-both name and mobile
	@Column(name = "UPDATE_REQ_TYPE", length = 5,columnDefinition = "varchar(5) default ''")
	private String UPDATE_REQ_TYPE;
	

	

	public String getUPDATE_REQ_TYPE() {
		return UPDATE_REQ_TYPE;
	}

	public void setUPDATE_REQ_TYPE(String uPDATE_REQ_TYPE) {
		UPDATE_REQ_TYPE = uPDATE_REQ_TYPE;
	}

	public String getEMP_DOB() {
		return EMP_DOB;
	}

	public void setEMP_DOB(String eMP_DOB) {
		EMP_DOB = eMP_DOB;
	}

	

	
	

	public int getSL_NO() {
		return SL_NO;
	}

	public void setSL_NO(int sL_NO) {
		SL_NO = sL_NO;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getEMP_GROUP() {
		return EMP_GROUP;
	}

	public void setEMP_GROUP(String eMP_GROUP) {
		EMP_GROUP = eMP_GROUP;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public String getMOBILE_AADHAAR() {
		return MOBILE_AADHAAR;
	}

	public void setMOBILE_AADHAAR(String mOBILE_AADHAAR) {
		MOBILE_AADHAAR = mOBILE_AADHAAR;
	}

	
	
	public String getMOBILE_MESSAGING() {
		return MOBILE_MESSAGING;
	}

	public void setMOBILE_MESSAGING(String mOBILE_MESSAGING) {
		MOBILE_MESSAGING = mOBILE_MESSAGING;
	}

	public String getUSER_EMAIL() {
		return USER_EMAIL;
	}

	public void setUSER_EMAIL(String uSER_EMAIL) {
		USER_EMAIL = uSER_EMAIL;
	}

	

	public String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public void setIP_ADDRESS(String iP_ADDRESS) {
		IP_ADDRESS = iP_ADDRESS;
	}

	public Timestamp getREQUESTED_TIME() {
		return REQUESTED_TIME;
	}

	public void setREQUESTED_TIME(Timestamp rEQUESTED_TIME) {
		REQUESTED_TIME = rEQUESTED_TIME;
	}

	public String getOTHER_DATA() {
		return OTHER_DATA;
	}

	public void setOTHER_DATA(String oTHER_DATA) {
		OTHER_DATA = oTHER_DATA;
	}

	public String getVERIFIED_STATUS() {
		return VERIFIED_STATUS;
	}

	public void setVERIFIED_STATUS(String vERIFIED_STATUS) {
		VERIFIED_STATUS = vERIFIED_STATUS;
	}

	public Timestamp getVERIFIED_TIME() {
		return VERIFIED_TIME;
	}

	public void setVERIFIED_TIME(Timestamp vERIFIED_TIME) {
		VERIFIED_TIME = vERIFIED_TIME;
	}

	public String getVERIFIED_BY() {
		return VERIFIED_BY;
	}

	public void setVERIFIED_BY(String vERIFIED_BY) {
		VERIFIED_BY = vERIFIED_BY;
	}

	

	public User_Requests() {
		super();
	}

	@Override
	public String toString() {
		return "User_Requests [SL_NO=" + SL_NO + ", EMP_ID=" + EMP_ID + ", EMP_GROUP=" + EMP_GROUP + ", EMP_NAME="
				+ EMP_NAME + ", EMP_DOB=" + EMP_DOB + ", MOBILE_AADHAAR=" + MOBILE_AADHAAR + ", MOBILE_MESSAGING="
				+ MOBILE_MESSAGING + ", USER_EMAIL=" + USER_EMAIL + ", IP_ADDRESS=" + IP_ADDRESS + ", REQUESTED_TIME="
				+ REQUESTED_TIME + ", VERIFIED_STATUS=" + VERIFIED_STATUS + ", VERIFIED_TIME=" + VERIFIED_TIME
				+ ", VERIFIED_BY=" + VERIFIED_BY + ", OTHER_DATA=" + OTHER_DATA + ", UPDATE_REQ_TYPE=" + UPDATE_REQ_TYPE
				+ "]";
	}
	

}
