package com.integra.pledgeapp.beans;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "employee_master")
public class Employee_Master {

	@EmbeddedId
	Emp_Master_CompositeKey compositeKey;

	@Column(name = "EMP_EMAIL", length = 50, columnDefinition = "varchar(50) default ''")
	private String EMP_EMAIL;

	@Column(name = "EMP_DOB", length = 50)
	private Date EMP_DOB;

	@Column(name = "EMP_PHONE", length = 12, columnDefinition = "varchar(50) default ''")
	private String EMP_PHONE;

	@Column(name = "EMP_NAME", length = 255, columnDefinition = "varchar(255) default ''")
	private String EMP_NAME;

	@Column(name = "SIGN_STATUS", nullable = false, columnDefinition = "int default 0")
	private int SIGN_STATUS;

	@Column(name = "ADDITIONAL_DATA", length = 2500, columnDefinition = "varchar(2500) default ''")
	private String ADDITIONAL_DATA;

	@Column(name = "STATUS", length = 5, columnDefinition = "int default 0")
	private int STATUS;
	
	
	@Column(name="CREATED_ON")
	private Timestamp CREATED_ON;

	public int getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(int sTATUS) {
		STATUS = sTATUS;
	}

	public Emp_Master_CompositeKey getCompositeKey() {
		return compositeKey;
	}

	public void setCompositeKey(Emp_Master_CompositeKey compositeKey) {
		this.compositeKey = compositeKey;
	}

	public String getEMP_EMAIL() {
		return EMP_EMAIL;
	}

	public void setEMP_EMAIL(String EMP_EMAIL) {
		this.EMP_EMAIL = EMP_EMAIL;
	}

	public Date getEMP_DOB() {
		return EMP_DOB;
	}

	public void setEMP_DOB(Date EMP_DOB) {
		this.EMP_DOB = EMP_DOB;
	}

	public String getEMP_PHONE() {
		return EMP_PHONE;
	}

	public void setEMP_PHONE(String EMP_PHONE) {
		this.EMP_PHONE = EMP_PHONE;
	}

	public String getEMP_NAME() {
		return EMP_NAME;
	}

	public void setEMP_NAME(String EMP_NAME) {
		this.EMP_NAME = EMP_NAME;
	}

	public int getSIGN_STATUS() {
		return SIGN_STATUS;
	}

	public void setSIGN_STATUS(int SIGN_STATUS) {
		this.SIGN_STATUS = SIGN_STATUS;
	}

	public String getADDITIONAL_DATA() {
		return ADDITIONAL_DATA;
	}

	public void setADDITIONAL_DATA(String aDDITIONAL_DATA) {
		ADDITIONAL_DATA = aDDITIONAL_DATA;
	}
	

	public Timestamp getCREATED_ON() {
		return CREATED_ON;
	}

	public void setCREATED_ON(Timestamp cREATED_ON) {
		CREATED_ON = cREATED_ON;
	}

	@Override
	public String toString() {
		return "Employee_Master [compositeKey=" + compositeKey + ", EMP_EMAIL=" + EMP_EMAIL + ", EMP_DOB=" + EMP_DOB
				+ ", EMP_PHONE=" + EMP_PHONE + ", EMP_NAME=" + EMP_NAME + ", SIGN_STATUS=" + SIGN_STATUS
				+ ", ADDITIONAL_DATA=" + ADDITIONAL_DATA + ", STATUS=" + STATUS + ", CREATED_ON=" + CREATED_ON + "]";
	}

//	@Override
//	public String toString() {
//		return "Employee_Master [compositeKey=" + compositeKey + ", EMP_EMAIL=" + EMP_EMAIL + ", EMP_DOB=" + EMP_DOB
//				+ ", EMP_PHONE=" + EMP_PHONE + ", EMP_NAME=" + EMP_NAME + ", SIGN_STATUS=" + SIGN_STATUS
//				+ ", ADDITIONAL_DATA=" + ADDITIONAL_DATA + "]";
//	}
	

}
