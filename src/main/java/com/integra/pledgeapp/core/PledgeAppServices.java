package com.integra.pledgeapp.core;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;

import com.integra.pledgeapp.beans.ApproveNameList;
import com.integra.pledgeapp.beans.User_Requests;

public interface PledgeAppServices {

	
	public String validateLogin(String userInfo,String ip);
	
	public String getPledgeInfo(String empInfo);
	
	public String submitConsent(String empInfo,String ip);
	
	public String downloadAndSaveSignedPledge(String filename,String txnid, String docCode);
	
	public String getSignedPledgePath(String empInfo);
	
	public JSONObject getemailInfo(String empInfo);
	
	public JSONArray getEmpList(String reportInputs);
	
	public boolean updatePhysicalSignedrecord(String empid,String refno,String ip,String outfilepath);
	
	public String validateotp(String userinfo,String ip);
	
	public void insertauditinfo(JSONObject input);
	
	public JSONObject checkSignstatus(String txnid);
	
	public JSONObject getWidget(String reportInputs);
	
	public JSONObject getDocList(JSONObject inputs);
	
	public JSONObject getPDFPath(JSONObject inputs);
	
	public String witnessEsign(String empInfo, String ip);
	
	public JSONObject getSignedDocList(JSONObject inputs);
	
	public String signExistingDocument(String empInfo, String ip); 
	
	public String sendNotification(String userInfo, String ip);
	
	public JSONObject getEmailSignedPledge(String userInfo);
	
	public JSONObject userUpdateRequest(JSONObject input);

	public JSONObject updateProfileOnOtp(JSONObject input);

	public JSONObject bcNamesapprovedList(JSONObject approveUpdateNameRequest);

	public List<Object> bcRequestsforUpdatingNames();

	public JSONObject bcNamesRejectionList(JSONObject bcNamesRejectionList);

	 public JSONObject getLangList(JSONObject inputs);

	public JSONObject getLangFilePath(JSONObject inputs);

	public JSONObject bcstatewisesigningstatus(JSONObject inputs);

	public JSONObject bcsignrejectiondetails(JSONObject inputs);

	public String generateOTPforWitnessEsign(String witnessInfo, String ip);

	public String updateBcCellIP(JSONObject input);

	public String getBcCellIP();

	public String resendOTPforWitnessEsign(String witnessInfo, String ip);

	public JSONObject getEmplList(JSONObject input);

	public JSONObject updateUserStatus(JSONObject input, JSONObject userdetail);

	public boolean validateUserRolePermission(String userName);

	public JSONObject readBulkInputs(MultipartFile file, String userName);

	public JSONObject getTemplateBasedonUserId(String empid);

	public JSONObject removeBC(String empID,JSONObject userDetails, Boolean isSigned) throws JSONException;

	public JSONObject checkSignDoc(String inputs, JSONObject userdetail);

	public JSONObject getBank() ;


}
