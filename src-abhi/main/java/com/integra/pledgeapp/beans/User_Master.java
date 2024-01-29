package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="User_master")
public class User_Master {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "USER_ID", length = 10)
    private int USER_ID;
    
    @Column(name = "USER_NAME", length = 50)
    private String USER_NAME;
    
    @Column(name = "PASSWORD", length = 50)
    private String PASSWORD;
    
    @Column(name = "ROLE_ID", length = 10)
    private int ROLE_ID;
    
    @Column(name = "PRIVILEGE_CODE", length = 50)
    private String PRIVILEGE_CODE;
    
    @Column(name="COMPANY",length=50)
    private String COMPANY;
    
    @Column(name="EMP_GROUP", length=50)
    private String EMP_GROUP;
            
	public int getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(int uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getUSER_NAME() {
		return USER_NAME;
	}
	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}
	public String getPASSWORD() {
		return PASSWORD;
	}
	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}
	public int getROLE_ID() {
		return ROLE_ID;
	}
	public void setROLE_ID(int rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}
	public String getPRIVILEGE_CODE() {
		return PRIVILEGE_CODE;
	}
	public void setPRIVILEGE_CODE(String pRIVILEGE_CODE) {
		PRIVILEGE_CODE = pRIVILEGE_CODE;
	}
	public String getCOMPANY() {
		return COMPANY;
	}
	public void setCOMPANY(String cOMPANY) {
		COMPANY = cOMPANY;
	}
	public String getEMP_GROUP() {
		return EMP_GROUP;
	}
	public void setEMP_GROUP(String eMP_GROUP) {
		EMP_GROUP = eMP_GROUP;
	}		
}
