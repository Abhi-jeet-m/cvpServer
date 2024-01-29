package com.integra.pledgeapp.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mailconfig")
public class Mail_Config {

	@Id
	@Column(name = "confid", length = 10)
	private String id;

	@Column(name = "configcode", length = 10)
	private String configcode;

	@Column(name = "mailid", length = 50)
	private String mailid;

	public String getConfigcode() {
		return configcode;
	}

	public void setConfigcode(String configcode) {
		this.configcode = configcode;
	}

	public String getMailid() {
		return mailid;
	}

	public void setMailid(String mailid) {
		this.mailid = mailid;
	}

}
