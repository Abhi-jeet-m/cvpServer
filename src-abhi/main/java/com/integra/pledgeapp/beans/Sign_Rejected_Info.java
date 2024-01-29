package com.integra.pledgeapp.beans;

import java.sql.Timestamp;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

@Entity
@Table(name="sign_rejected_info")
public class Sign_Rejected_Info {
	//Sign_Rejected_Info
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SL_NO", length = 10)
	private int SL_NO;
	
	@Column(name = "EMP_ID", length = 10)
    private String EMP_ID;
	
	@Column(name = "EMP_NAME", length = 100, columnDefinition = "varchar(100) default ''")
    private String EMP_NAME;
       
	@Column(name="LOG_TIME")
	private Timestamp LOG_TIME;
                      

	@Column(name = " SIGN_MODE",length=5,  columnDefinition = "varchar(5) default ''")
	private String SIGN_MODE;
               
	@Column(name = "SIGNER_NAME", length = 100, columnDefinition = "varchar(100) default ''")
    private String SIGNER_NAME;
	
	@Column(name = "SIGNED_BY", length = 100, columnDefinition = "varchar(100) default ''")
    private String SIGNED_BY;
	
	

	@Column(name = "REJECTED_REASON", length = 255, columnDefinition = "varchar(255) default ''")
    private String REJECTED_REASON;
	
//	@Column(name = "SIGNER_DATA", length = 1000, columnDefinition = "varchar(1000) default ''")
//    private JSONObject SIGNER_DATA;
	
	@Column(name = "PENALTY_FEE", nullable = false, columnDefinition = "int default 0")
	private int PENALTY_FEE;
    
	@Column(name = "PENALTY_STATUS", nullable = false, columnDefinition = "int default 0")
	private int PENALTY_STATUS ;

	@Column(name = "REJECTED_BY", length=100, columnDefinition = "varchar(100) default ''")
	private String REJECTED_BY;
	
	@Column(name = "SIGN_ORDER", nullable = false, columnDefinition = "int default 0")
	private int SIGN_ORDER;

	@Column(name="PENALTY_CLOSED_TIME")
	private Timestamp PENALTY_CLOSED_TIME;
	
	@Column(name = "EMP_COMPANY", length = 50,columnDefinition = "varchar(50) default ''")
	private String EMP_COMPANY;
	
	@Column(name = "EMP_GROUP", length = 10, columnDefinition = "varchar(10) default ''")
	private String EMP_GROUP;
	
	@Column(name = "DOC_CODE", length = 255 ,columnDefinition = "varchar(255) default ''")
	private String DOC_CODE;

	@Column(name = "SIGNER_DATA", length = 2500, columnDefinition = "varchar(2500) default ''")
	private String SIGNER_DATA;
	
	public Timestamp getPENALTY_CLOSED_TIME() {
		return PENALTY_CLOSED_TIME;
	}

	public void setPENALTY_CLOSED_TIME(Timestamp pENALTY_CLOSED_TIME) {
		PENALTY_CLOSED_TIME = pENALTY_CLOSED_TIME;
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

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public Timestamp getLOG_TIME() {
		return LOG_TIME;
	}

	public void setLOG_TIME(Timestamp lOG_TIME) {
		LOG_TIME = lOG_TIME;
	}

	public String getSIGN_MODE() {
		return SIGN_MODE;
	}

	public void setSIGN_MODE(String sIGN_MODE) {
		SIGN_MODE = sIGN_MODE;
	}

	public String getSIGNER_NAME() {
		return SIGNER_NAME;
	}

	public void setSIGNER_NAME(String sIGNER_NAME) {
		SIGNER_NAME = sIGNER_NAME;
	}

	public String getREJECTED_REASON() {
		return REJECTED_REASON;
	}

	public void setREJECTED_REASON(String rEJECTED_REASON) {
		REJECTED_REASON = rEJECTED_REASON;
	}



	

	

	public int getPENALTY_FEE() {
		return PENALTY_FEE;
	}

	public void setPENALTY_FEE(int pENALTY_FEE) {
		PENALTY_FEE = pENALTY_FEE;
	}

	public int getPENALTY_STATUS() {
		return PENALTY_STATUS;
	}

	public void setPENALTY_STATUS(int pENALTY_STATUS) {
		PENALTY_STATUS = pENALTY_STATUS;
	}

	public String getREJECTED_BY() {
		return REJECTED_BY;
	}

	public void setREJECTED_BY(String string) {
		REJECTED_BY = string;
	}

	public int getSIGN_ORDER() {
		return SIGN_ORDER;
	}

	public void setSIGN_ORDER(int sIGN_ORDER) {
		SIGN_ORDER = sIGN_ORDER;
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

	public String getDOC_CODE() {
		return DOC_CODE;
	}

	public void setDOC_CODE(String dOC_CODE) {
		DOC_CODE = dOC_CODE;
	}

	public String getSIGNED_BY() {
		return SIGNED_BY;
	}

	public void setSIGNED_BY(String sIGNER_BY) {
		SIGNED_BY = sIGNER_BY;
	}

	public String getSIGNER_DATA() {
		return SIGNER_DATA;
	}

	public void setSIGNER_DATA(String sIGNER_DATA) {
		SIGNER_DATA = sIGNER_DATA;
	}

	@Override
	public String toString() {
		return "Sign_Rejected_Info [SL_NO=" + SL_NO + ", EMP_ID=" + EMP_ID + ", EMP_NAME=" + EMP_NAME + ", LOG_TIME="
				+ LOG_TIME + ", SIGN_MODE=" + SIGN_MODE + ", SIGNER_NAME=" + SIGNER_NAME + ", SIGNED_BY=" + SIGNED_BY
				+ ", REJECTED_REASON=" + REJECTED_REASON + ", PENALTY_FEE=" + PENALTY_FEE + ", PENALTY_STATUS="
				+ PENALTY_STATUS + ", REJECTED_BY=" + REJECTED_BY + ", SIGN_ORDER=" + SIGN_ORDER
				+ ", PENALTY_CLOSED_TIME=" + PENALTY_CLOSED_TIME + ", EMP_COMPANY=" + EMP_COMPANY + ", EMP_GROUP="
				+ EMP_GROUP + ", DOC_CODE=" + DOC_CODE + ", SIGNER_DATA=" + SIGNER_DATA + "]";
	}

	
	
	
	





	
	
	
	
	

}
