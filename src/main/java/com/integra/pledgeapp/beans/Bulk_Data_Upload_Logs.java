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
@Table(name = "bulk_data_upload_logs")
public class Bulk_Data_Upload_Logs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SL_NO")
	private int SL_NO;

	@Column(name = "EMP_ID", length = 10)
	private String EMP_ID;

	@Column(name = "EMP_GROUP", length = 50, columnDefinition = "varchar(255) default ''")
	private String EMP_GROUP;
	@Column(name = "EMP_DOB", length = 50)
	private Date EMP_DOB;

	@Column(name = "EMP_PHONE", length = 12, columnDefinition = "varchar(50) default ''")
	private String EMP_PHONE;

	@Column(name = "EMP_NAME", length = 255, columnDefinition = "varchar(255) default ''")
	private String EMP_NAME;

	@Column(name = "REG_BY", length = 100, columnDefinition = "varchar(100) default '' ")
	private String REG_BY;

	@Column(name = "REG_ON")
	private Timestamp REG_ON;

	@Column(name = "STATUS", length = 1, columnDefinition = "int(1) default 0")
	private int STATUS;

	@Column(name = "FAILURE_REASON", length = 255, columnDefinition = "varchar(255) default '' ")
	private String FAILURE_REASON;

	@Column(name = "BATCH_NO", length = 20, columnDefinition = "varchar(255) default '' ")
	private String BATCH_NO;

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

	public Date getEMP_DOB() {
		return EMP_DOB;
	}

	public void setEMP_DOB(Date eMP_DOB) {
		EMP_DOB = eMP_DOB;
	}

	public String getEMP_PHONE() {
		return EMP_PHONE;
	}

	public void setEMP_PHONE(String eMP_PHONE) {
		EMP_PHONE = eMP_PHONE;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}

	public String getREG_BY() {
		return REG_BY;
	}

	public void setREG_BY(String rEG_BY) {
		REG_BY = rEG_BY;
	}

	public Timestamp getREG_ON() {
		return REG_ON;
	}

	public void setREG_ON(Timestamp rEG_ON) {
		REG_ON = rEG_ON;
	}

	public int getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(int sTATUS) {
		STATUS = sTATUS;
	}

	public String getFAILURE_REASON() {
		return FAILURE_REASON;
	}

	public void setFAILURE_REASON(String fAILURE_REASON) {
		FAILURE_REASON = fAILURE_REASON;
	}

	public String getBATCH_NO() {
		return BATCH_NO;
	}

	public void setBATCH_NO(String bATCH_NO) {
		BATCH_NO = bATCH_NO;
	}

	@Override
	public String toString() {
		return "Bulk_Data_Upload_Logs [SL_NO=" + SL_NO + ", EMP_ID=" + EMP_ID + ", EMP_GROUP=" + EMP_GROUP
				+ ", EMP_DOB=" + EMP_DOB + ", EMP_PHONE=" + EMP_PHONE + ", EMP_NAME=" + EMP_NAME + ", REG_BY=" + REG_BY
				+ ", REG_ON=" + REG_ON + ", STATUS=" + STATUS + ", FAILURE_REASON=" + FAILURE_REASON + ", BATCH_NO="
				+ BATCH_NO + "]";
	}

	
	
	
}
