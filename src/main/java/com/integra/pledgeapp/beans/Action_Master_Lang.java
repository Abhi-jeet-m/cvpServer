package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name="action_master_lang")
public class Action_Master_Lang {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SL_NO", length = 10)
	private int SL_NO;
	
	
	@Column(name="DOC_CODE", length = 50)
	private String DOC_CODE;	
	
	@Column(name="LANG_CODE", length = 10)
	private String LANG_CODE;
	
	@Column(name="LANG_NAME", length = 25)
	private String LANG_NAME;
	
	@Column(name="FILE_NAME", length = 50)
	private String FILE_NAME;
	
	
	@Column(name="ENABLE_STATUS", nullable = false, columnDefinition = "int default 0")
	private int ENABLE_STATUS;

	@Column(name="DOCUMENT_DATE")
	private Timestamp DOCUMENT_DATE;

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

	public String getLANG_CODE() {
		return LANG_CODE;
	}

	public void setLANG_CODE(String lANG_CODE) {
		LANG_CODE = lANG_CODE;
	}

	public String getLANG_NAME() {
		return LANG_NAME;
	}

	public void setLANG_NAME(String lANG_NAME) {
		LANG_NAME = lANG_NAME;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public int getENABLE_STATUS() {
		return ENABLE_STATUS;
	}

	public void setENABLE_STATUS(int eNABLE_STATUS) {
		ENABLE_STATUS = eNABLE_STATUS;
	}

	public Timestamp getDOCUMENT_DATE() {
		return DOCUMENT_DATE;
	}

	public void setDOCUMENT_DATE(Timestamp dOCUMENT_DATE) {
		DOCUMENT_DATE = dOCUMENT_DATE;
	}

	@Override
	public String toString() {
		return "Action_Master_Lang [SL_NO=" + SL_NO + ", DOC_CODE=" + DOC_CODE + ", LANG_CODE=" + LANG_CODE
				+ ", LANG_NAME=" + LANG_NAME + ", FILE_NAME=" + FILE_NAME + ", ENABLE_STATUS=" + ENABLE_STATUS
				+ ", DOCUMENT_DATE=" + DOCUMENT_DATE + "]";
	}
	

}
