package com.integra.pledgeapp.core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Action_Master_Lang;
import com.integra.pledgeapp.beans.Audit_Info;
import com.integra.pledgeapp.beans.Bulk_Data_Upload_Logs;
import com.integra.pledgeapp.beans.Company_Master;
import com.integra.pledgeapp.beans.Doc_Sign_Config;
import com.integra.pledgeapp.beans.Dsc_Token_Config;
import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.beans.Employee_action_status;
import com.integra.pledgeapp.beans.Sign_Doc_Details;
import com.integra.pledgeapp.beans.Sign_Rejected_Info;
import com.integra.pledgeapp.beans.User_Requests;

public interface PledgeAppDAOServices {

	public JSONObject validateEmployeeDetails(String empInfo);

	public JSONObject validateUserDetails(String userInfo);

	public Employee_Master getEmployeeDetails(String empid, String empGroup);

	public boolean insertSign_log(JSONObject empInfo);

	public String updateEmployeeSignStatus(String empInfo, String txnid, String filename);

	public JSONObject getSignedDownloadPath(String empInfo);

	public String updateDownloadStatus(String empInfo);

	public JSONObject getMailInfo(String usertype, String empid);

	public JSONArray getEmployeeList(String empid, String branchName, String company, String document);

	public String getHRMail();

	public boolean updatePhysicalSignedRecord(String empid, String refno, String ip, String outfilepath);

	public void insertauditinfo(JSONObject input);

	public JSONObject checksignstatus(String txnid);

	public JSONObject getWidgetData(String listType, String companyName);

	public JSONObject getDocListInEAT(JSONObject inputs);

	public JSONObject getDocInfoInEAM(String docCode);

	public boolean getDocStatusInSDD(String empCode, String docCode);

	public boolean insertStatusInEAS(Employee_action_status statusInfo);

	public List<Doc_Sign_Config> getDocSignConfig(String docCode, String signStatus, int signOrder);

	public Dsc_Token_Config getDSCTokenConfig(String dscTokenID);

	public boolean insertSignDocDetails(Sign_Doc_Details signDocDetails);

	public Sign_Doc_Details checkSignDocDetails(String empid, String empCompany, String empGroup, String docCode);

	public boolean updateSignDocDetails(Sign_Doc_Details signDocDetails);

	public JSONObject getSignedDocListInSDD(JSONObject inputs);

	public Sign_Doc_Details getSignedDocListTxnid(String txnid);

	public JSONObject getEmailDetails(String usertype, JSONArray contentList);

	public boolean insertintoUserRequests(JSONObject inputs);

	public User_Requests getUserRequestDetails(String empid, String reqType);

	public boolean updateUserDetails(Employee_Master empData);

	public List<Object> userNameUpdationList(String reqType1, String reqType2, String empCompany);

	public boolean updateUserRequests(User_Requests userData);

	public List<User_Requests> getUserRequestList(String empid);

	public boolean insertintoRejectionList(Sign_Rejected_Info rejectionList);

	public List<Sign_Rejected_Info>  getRejectedList(String empid);

	public List<Action_Master_Lang> getlangInfo(String docCode);

	public List<Action_Master_Lang> getlangFilePath(String langName, String docCode);

	public JSONObject bcstatewisesigningstatus(JSONObject input);

	public	JSONObject bcsignrejectiondetails(JSONObject input);

	public boolean updateBcCellIp(Dsc_Token_Config dsctokenconfigresp);

	public JSONObject getEmplList(JSONObject input);

	public JSONObject updateUserStatus(JSONObject input);

	public boolean updateLogdetails(Audit_Info userLogInfo);

	JSONObject getLoginUserDetails(String username);

	public JSONObject SaveEmployeeData(JSONObject inputsData);

	public void bulkDataUploadLogs(Bulk_Data_Upload_Logs res);

	public JSONObject getEmployeeRepeatedID(String empID, String empGrp);

	public JSONObject removeBC(int numberToAppend, String empID, String empGrp);

	public ArrayList<Sign_Doc_Details> getSignedDocListEmpidAndEmpGrp(String empid, String Empgrp);

	public JSONObject removeBCInSignDocDetails(int numberToAppend, String empID, String empGrp);

	ArrayList<Company_Master> getBankInfo();
	
	//public boolean updateActionMasterLang(Action_Master_Lang langData);
	
	

}
