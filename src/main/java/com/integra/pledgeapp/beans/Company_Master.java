package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company_master")
public class Company_Master {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SI_NO", length = 10)
	private int SI_NO;
	@Column(name="COMPANY_CODE", length = 50)
	private String COMPANY_CODE;
	@Column(name="COMPANY_NAME", length=200)
	private String COMPANY_NAME;
	@Column(name="GROUP_CODE", length=50)
	private String GROUP_CODE;
	@Column(name="GROUP_NAME")
	private String GROUP_NAME;
	public int getSI_NO() {
		return SI_NO;
	}
	public void setSI_NO(int sI_NO) {
		SI_NO = sI_NO;
	}
	public String getCOMPANY_CODE() {
		return COMPANY_CODE;
	}
	public void setCOMPANY_CODE(String cOMPANY_CODE) {
		COMPANY_CODE = cOMPANY_CODE;
	}
	public String getCOMPANY_NAME() {
		return COMPANY_NAME;
	}
	public void setCOMPANY_NAME(String cOMPANY_NAME) {
		COMPANY_NAME = cOMPANY_NAME;
	}
	public String getGROUP_CODE() {
		return GROUP_CODE;
	}
	public void setGROUP_CODE(String gROUP_CODE) {
		GROUP_CODE = gROUP_CODE;
	}
	public String getGROUP_NAME() {
		return GROUP_NAME;
	}
	public void setGROUP_NAME(String gROUP_NAME) {
		GROUP_NAME = gROUP_NAME;
	}
	@Override
	public String toString() {
		return "Company_Master [SI_NO=" + SI_NO + ", COMPANY_CODE=" + COMPANY_CODE + ", COMPANY_NAME=" + COMPANY_NAME
				+ ", GROUP_CODE=" + GROUP_CODE + ", GROUP_NAME=" + GROUP_NAME + "]";
	}
	
	
	
	
	
	
	
}
