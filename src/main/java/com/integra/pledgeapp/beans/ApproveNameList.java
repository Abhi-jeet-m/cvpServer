package com.integra.pledgeapp.beans;

public class ApproveNameList {

	
	private String existingName;
	
	private String existingEmail;
	private String requestedName;
	private String id;
	private String requestedTime;
	private String phoneNo;
	private String group;
	private String UpdateType;
	
	
	
	public String getUpdateType() {
		return UpdateType;
	}
	public void setUpdateType(String updateType) {
		UpdateType = updateType;
	}
	public String getExistingName() {
		return existingName;
	}
	public void setExistingName(String existingName) {
		this.existingName = existingName;
	}
	public String getExistingEmail() {
		return existingEmail;
	}
	public void setExistingEmail(String existingEmail) {
		this.existingEmail = existingEmail;
	}
	public String getRequestedName() {
		return requestedName;
	}
	public void setRequestedName(String requestedName) {
		this.requestedName = requestedName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(String requestedTime) {
		this.requestedTime = requestedTime;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public ApproveNameList(String existingName, String existingEmail, String requestedName, String id,
			String requestedTime, String phoneNo, String group, String updateType) {
		super();
		this.existingName = existingName;
		this.existingEmail = existingEmail;
		this.requestedName = requestedName;
		this.id = id;
		this.requestedTime = requestedTime;
		this.phoneNo = phoneNo;
		this.group = group;
		UpdateType = updateType;
	}
	@Override
	public String toString() {
		return "ApproveNameList [existingName=" + existingName + ", existingEmail=" + existingEmail + ", requestedName="
				+ requestedName + ", id=" + id + ", requestedTime=" + requestedTime + ", phoneNo=" + phoneNo
				+ ", group=" + group + ", UpdateType=" + UpdateType + "]";
	}
	
}
