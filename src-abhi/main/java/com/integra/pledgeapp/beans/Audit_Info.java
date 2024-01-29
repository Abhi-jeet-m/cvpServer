package com.integra.pledgeapp.beans;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "audit_info")
public class Audit_Info {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "audit_id", length = 10)
	private int AUDIT_ID;
	@Column(name = "user", length = 50)
	private String USER;
	@Column(name = "empid", length = 10)
	private String EMP_ID;
	@Column(name = "operationtime", length = 50)
	private Timestamp OPERATION_TIME;
	@Column(name = "operationtype", length = 50)
	private String OPERATION_TYPE;
	@Column(name = "operationremarks", length = 250)
	private String OPERATIONREMARKS;
	@Column(name = "ipaddress", length = 250)
	private String IPADDRESS;
	@Column(name = "ADDITIONAL_INFO", length = 1000, columnDefinition = "varchar(1000) default ''")
	private String ADDITIONAL_INFO;
	public String getIPADDRESS() {
		return IPADDRESS;
	}
	public String getADDITIONAL_INFO() {
		return ADDITIONAL_INFO;
	}
	public void setADDITIONAL_INFO(String aDDITIONAL_INFO) {
		ADDITIONAL_INFO = aDDITIONAL_INFO;
	}
	public void setIPADDRESS(String iPADDRESS) {
		IPADDRESS = iPADDRESS;
	}
	public int getAUDIT_ID() {
		return AUDIT_ID;
	}
	public void setAUDIT_ID(int aUDIT_ID) {
		AUDIT_ID = aUDIT_ID;
	}
	public String getUSER() {
		return USER;
	}
	public void setUSER(String uSER) {
		USER = uSER;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public Timestamp getOPERATION_TIME() {
		return OPERATION_TIME;
	}
	public void setOPERATION_TIME(Timestamp oPERATION_TIME) {
		OPERATION_TIME = oPERATION_TIME;
	}
	public String getOPERATION_TYPE() {
		return OPERATION_TYPE;
	}
	public void setOPERATION_TYPE(String oPERATION_TYPE) {
		OPERATION_TYPE = oPERATION_TYPE;
	}
	public String getOPERATIONREMARKS() {
		return OPERATIONREMARKS;
	}
	public void setOPERATIONREMARKS(String oPERATIONREMARKS) {
		OPERATIONREMARKS = oPERATIONREMARKS;
	}
	
}
