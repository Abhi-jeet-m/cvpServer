package com.integra.pledgeapp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;

import com.integra.pledgeapp.utilities.Certificate;
import com.integra.pledgeapp.beans.Action_Master_Lang;
import com.integra.pledgeapp.beans.Audit_Info;
import com.integra.pledgeapp.beans.Bulk_Data_Upload_Logs;
import com.integra.pledgeapp.beans.Company_Master;
import com.integra.pledgeapp.beans.Doc_Sign_Config;
import com.integra.pledgeapp.beans.DroolBank;
import com.integra.pledgeapp.beans.Dsc_Token_Config;
import com.integra.pledgeapp.beans.Emp_Master_CompositeKey;
import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.beans.Employee_action_status;
import com.integra.pledgeapp.beans.Field_Validation;
import com.integra.pledgeapp.beans.Sign_Doc_Details;
import com.integra.pledgeapp.beans.Sign_Rejected_Info;
import com.integra.pledgeapp.beans.User_Requests;
import com.integra.pledgeapp.notification.NotificationContent;
import com.integra.pledgeapp.notification.NotificationEmail;
import com.integra.pledgeapp.notification.NotificationSMS;
import com.integra.pledgeapp.token.PDFUSBTokenSign;
import com.integra.pledgeapp.utilities.ConfigListner;
import com.integra.pledgeapp.utilities.ContextObjects;
import com.integra.pledgeapp.utilities.EmailThread;
import com.integra.pledgeapp.utilities.FileCleaner;
import com.integra.pledgeapp.utilities.GeneratePDF;
import com.integra.pledgeapp.utilities.InMemory;
import com.integra.pledgeapp.utilities.MailHandler;
import com.integra.pledgeapp.utilities.MySignClient;
import com.integra.pledgeapp.utilities.OtpHandler;
import com.integra.pledgeapp.utilities.Properties_Loader;
import com.integra.pledgeapp.utilities.SMSThread;
import com.integra.pledgeapp.utilities.TokenManager;
import com.mysql.cj.xdevapi.JsonArray;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class PledgeAppServicesImpl implements PledgeAppServices {

	private static final String Field_Validation = null;
	PledgeAppDAOServices PDAOS = new PledgeAppDAOImpl();
	PledgeAppDAOImpl PADL = new PledgeAppDAOImpl();
	SMSThread smsth = null;
	EmailThread emailth = null;
	String mergedFilePath = "";

	@Override
	public String validateLogin(String userInfo, String ip) {
		JSONObject out = new JSONObject();
		try {
//			System.out.println("inside Main:"+userInfo);
			JSONObject inputs = new JSONObject(userInfo);
			// Validating BC Login
			JSONObject js1 = PDAOS.validateEmployeeDetails(userInfo);
			if (js1.getString("status").equalsIgnoreCase("SUCCESS")) {
				if ("emp".equalsIgnoreCase(inputs.getString("usertype"))) {
					if (js1.getString("empStatus").equals("1")) {
						JSONObject auditInput1 = new JSONObject();
						auditInput1.put("empid", inputs.getString("empid"));
						auditInput1.put("operationtype", "EMP Login");
						auditInput1.put("operationremarks", "Success");
						auditInput1.put("ip", ip);
						auditInput1.put("userid", "");
						PDAOS.insertauditinfo(auditInput1);
//					String loginmode = Properties_Loader.getLOGIN_MODE();
						String fullName = js1.getString("empname");
						// getNameMasked(js1.getString("empname"));
						String mobile = js1.getString("mobilenum").replaceAll("\\d(?=\\d{3})", "*");

						if (inputs.getString("loginmode").equalsIgnoreCase("OTP")) {
//						System.out.println("inside Login mode");

							JSONObject inputJS = new JSONObject();
							JSONObject typeJSON = new JSONObject();
							JSONObject typeJSON1 = new JSONObject();
							JSONObject userData = new JSONObject();
							typeJSON.put("type", "SMS");
							typeJSON1.put("type", "EMAIL");
							JSONArray type = new JSONArray();
							type.put(typeJSON);
							type.put(typeJSON1);
							inputs.put("type", type);
							JSONArray list = new JSONArray();
							userData.put("empid", inputs.getString("empid"));
							userData.put("emailID", js1.getString("emailID"));
							userData.put("mobileNo", js1.getString("mobilenum"));
							list.put(userData);
							inputs.put("list", list);
							inputs.put("fullName", fullName);
//						System.out.println(inputs);
							String otp = generateOnlyOtp(inputs.getString("empid"));
							inputs.put("otp", otp);
							// If login mode is otp or both(otp,pan) den this code is used
							String response = sendNotification(inputs.toString(), ip);
//						System.out.println(response);
							JSONObject resJS = new JSONObject(response);

							if (resJS.getString("status").equalsIgnoreCase("SUCCESS")) {
								out.put("statuscode", "00");
								out.put("statusmsg", "SUCCESS");
								out.put("mobilenum", mobile);
								out.put("bcMobileNum", js1.getString("mobilenum"));
								out.put("empName", fullName);
								// out.put("loginmode", loginmode);
							} else {
								out.put("statuscode", "01");
								out.put("statusmsg", "OTP GENERATION FAILED");
								JSONObject auditInput = new JSONObject();
								auditInput.put("empid", inputs.getString("empid"));
								auditInput.put("operationtype", "Login");
								auditInput.put("operationremarks", "Failure (Otp Generation Failed)");
								auditInput.put("userid", "");
								auditInput.put("ip", ip);
								PDAOS.insertauditinfo(auditInput);
							}
						}
						// If login mode is only PAN then otp generation is not requiredelse
						else {
							out.put("statuscode", "00");
							out.put("statusmsg", "SUCCESS");
							out.put("mobilenum", mobile);
							out.put("empName", fullName);
							out.put("bcMobileNum", js1.getString("mobilenum"));
							// out.put("loginmode", loginmode);
						}
					} else {
						out.put("statuscode", "01");
						out.put("statusmsg", "Account Disabled. Please Contact BC Cell");
						JSONObject auditInput = new JSONObject();
						auditInput.put("empid", inputs.getString("empid"));
						auditInput.put("operationtype", "Login");
						auditInput.put("operationremarks", "Failure");
						auditInput.put("userid", "");
						auditInput.put("ip", ip);
						PDAOS.insertauditinfo(auditInput);
					}

				} else {
					out.put("statuscode", "01");
					out.put("statusmsg", "USER NOT FOUND");
					JSONObject auditInput = new JSONObject();
					auditInput.put("empid", inputs.getString("empid"));
					auditInput.put("operationtype", "Login");
					auditInput.put("operationremarks", "Failure");
					auditInput.put("userid", "");
					auditInput.put("ip", ip);
					PDAOS.insertauditinfo(auditInput);
				}
			} else {
				// Validating HR Login
				JSONObject js = PDAOS.validateUserDetails(userInfo);
				JSONObject dropDownforCompany = null;
				if (js.getString("status").equalsIgnoreCase("SUCCESS")) {
					String empcompany = js.getString("company");
					String empgroup = "";
					if (js.has("empgroup")) {
						empgroup = js.getString("empgroup");
					}

//					if(js.getString("privilegeCode").equalsIgnoreCase("ALL")) {
//						dropDownforCompany=	PADL.getDropDownDataforCompany();
//						out.put("dropDownforCompany", dropDownforCompany);
//						
//					}
					JSONObject dropDown = PADL.getDropDownData(empcompany, empgroup);

					String authToken = UUID.randomUUID().toString();
					TokenManager.addToken(authToken);
					out.put("statuscode", "00");
					out.put("statusmsg", "SUCCESS");
					out.put("authToken", authToken);
					out.put("roleId", js.getString("roleId"));
					out.put("privilegeCode", js.getString("privilegeCode"));

					out.put("company", js.getString("company"));
					if (js.has("empgroup")) {
						out.put("empGroup", js.getString("empgroup"));
					}

					out.put("dropDown", dropDown);
					JSONObject userdetails = new JSONObject();
					userdetails.put("username", inputs.getString("username"));
					userdetails.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authToken, userdetails);
					JSONObject auditInput = new JSONObject();
					auditInput.put("empid", "");
					auditInput.put("operationtype", "Login");
					auditInput.put("operationremarks", "Success");
					auditInput.put("userid", inputs.getString("username"));
					auditInput.put("ip", ip);
					PDAOS.insertauditinfo(auditInput);
				} else {
					out.put("statuscode", "01");
					out.put("statusmsg", "USER NOT FOUND");
					JSONObject auditInput = new JSONObject();
					auditInput.put("empid", "");
					auditInput.put("operationtype", "Login");
					auditInput.put("operationremarks", "Failure");
					auditInput.put("userid", inputs.getString("username"));
					auditInput.put("ip", ip);
					PDAOS.insertauditinfo(auditInput);
				}
			}

		} catch (JSONException e) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Error :" + e.getMessage());
				e.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Error :" + e.getMessage());
				e.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return out.toString();
	}

	@Override
	public String getPledgeInfo(String empInfo) {
		JSONObject output = new JSONObject();
		try {
			JSONObject input = new JSONObject(empInfo);
			if (input.has("empid")) {
				String empid = input.getString("empid");
				if (input.has("authToken")) {
					JSONObject token = ContextObjects.getAuthTokens();
					if (token.has(input.getString("authToken"))) {
						output.put("statuscode", "00");
						output.put("statusmsg", "SUCCESS");
						output.put("filepath", "");
						output.put("referenceno", "INT" + empid + System.currentTimeMillis());
					} else {
						output.put("statuscode", "01");
						output.put("statusmsg", "Error :Invalid Authentication key or key Expired!!");
					}
				} else {
					output.put("statuscode", "01");
					output.put("statusmsg", "Error :Authentication Failed!!");
				}
			} else {
				output.put("statuscode", "01");
				output.put("statusmsg", "Error :Employee ID Not Found!!");
			}
		} catch (JSONException e) {
			try {
				output.put("statuscode", "01");
				output.put("statusmsg", "Error :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		} catch (Exception e) {
			try {
				output.put("statuscode", "01");
				output.put("statusmsg", "Error :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

		return output.toString();
	}

	@Override
	public JSONObject getPDFPath(JSONObject inputs) {

		JSONObject pdfRes = new JSONObject();
		JSONObject pdfRes1 = new JSONObject();

		try {
			JSONObject input = new JSONObject(inputs);

			// family details Info

			JSONObject familyInfo = new JSONObject();
			if (inputs.has("familyInfo")) {
				familyInfo = inputs.getJSONObject("familyInfo");
			}
			System.out.println("familyInfo " + familyInfo);

			Employee_Master empDetails = PDAOS.getEmployeeDetails(inputs.getString("empid"),
					inputs.getString("empGroup"));

			if (empDetails.getSTATUS() == 1) {
				pdfRes = GeneratePDF.generatePDF(empDetails,
						"VIEW" + inputs.getString("empid") + System.currentTimeMillis(),
						Properties_Loader.UNSIGNED_FILES_DIRECTORY, "N", inputs.getString("type"),
						inputs.getString("docCode"), familyInfo);
				System.out.println("output from generate pdf method :" + pdfRes);
				String generatedpdfpath = pdfRes.getString("PDFPath");
				input.put("generatedpdfpath", generatedpdfpath);
				input.put("docCode", inputs.getString("docCode"));
				if (inputs.has("langselected")) {
					input.put("langselected", inputs.getString("langselected"));
					input.put("refno", "VIEW" + inputs.getString("empid") + System.currentTimeMillis());
					if (inputs.getString("langselected").equalsIgnoreCase("Show agreement in my language")
							|| inputs.getString("langselected").equalsIgnoreCase("")) {
						pdfRes1 = pdfRes;
					} else {

						JSONObject filepathRes = mergeingwithLocalLang(input);
						pdfRes1 = filepathRes;

					}
				} else {
					pdfRes1 = pdfRes;
				}

				// System.out.println("pdfRes1:"+pdfRes1);
				FileCleaner fc = new FileCleaner();
				fc.filepath = pdfRes1.getString("PDFPath");
				fc.start();
			} else {
				pdfRes1.put("status", "Failure");
				pdfRes1.put("statuscode", "01");
				pdfRes1.put("statusDetails", "Account Disabled. Please Contact BC Cell");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfRes1;
	}

	@Override
	public String submitConsent(String empInfo, String ip) {
		String response = null;
		String signMode = "1";
		String signType = "ESIGN";
		try {
			JSONObject pdfRes = new JSONObject();
			JSONObject emp = new JSONObject(empInfo);
			// String refno = emp.getString("referenceno");
			// generating refno inside submit consent method because there are chances of
			// refno being null when it fecthed from client (i.e txndIdnull issue was rised)
			String referencenoResp = getRefnum(emp.getString("empid"));
			String refno = referencenoResp;
			String empid = emp.getString("empid");
			String docCode = emp.getString("docCode");
			String userIP = emp.getString("userIP");
			Doc_Sign_Config docSignConfig = null;
			Employee_Master employeeDetails = PDAOS.getEmployeeDetails(empid, emp.getString("empGroup"));
			// for inserting intp log purpose
			String corpUser = employeeDetails.getCompositeKey().getEMP_COMPANY() + ":" + emp.getString("empGroup") + ":"
					+ empid;
			List<Doc_Sign_Config> doc_Sign_Config = PDAOS.getDocSignConfig(docCode, "1", 1);
			ListIterator<Doc_Sign_Config> iterator = doc_Sign_Config.listIterator();

			while (iterator.hasNext()) {
				docSignConfig = (Doc_Sign_Config) iterator.next();
			}
			// family info
			JSONObject familyInfo = new JSONObject();
			if (emp.has("familyInfo")) {
				familyInfo = emp.getJSONObject("familyInfo");
			}
			familyInfo.put("afterConsent", "true");

			if (!PDAOS.getDocStatusInSDD(empid, docCode)) {
				JSONObject pdfRes1 = GeneratePDF.generatePDF(employeeDetails, refno,
						Properties_Loader.UNSIGNED_FILES_DIRECTORY, "N", emp.getString("type"), docCode, familyInfo);
				String generatedpdfpath = pdfRes1.getString("PDFPath");
				JSONObject input = new JSONObject();
				input.put("generatedpdfpath", generatedpdfpath);
				input.put("docCode", docCode);
				input.put("refno", refno);

				if (emp.getString("langselected").equalsIgnoreCase("null")) {

					pdfRes = pdfRes1;
				} else {
					input.put("langselected", emp.getString("langselected"));
					if (emp.getString("langselected").equalsIgnoreCase("")
							|| emp.getString("langselected").equalsIgnoreCase("Show agreement in my language")) {
						pdfRes = pdfRes1;
					} else {
						JSONObject filepathRes = mergeingwithLocalLang(input);
						pdfRes = filepathRes;
					}
				}

//				JSONObject pdfRes= GeneratePDF.generatePDF(employeeDetails, refno,
//						Properties_Loader.UNSIGNED_FILES_DIRECTORY, "N", emp.getString("type"), docCode);
//				System.out.println("pdfRes:"+pdfRes);
				if (pdfRes.getString("status").equalsIgnoreCase("SUCCESS")) {

					// String authtoken = MySignClient.validateLogin(userIP);
					JSONObject validateLoginResp = MySignClient.validateLogin(userIP);

					String authtoken = validateLoginResp.getString("authToken");

					System.out.println("getSIGN_DISPLAY_INFO():::" + docSignConfig.getSIGN_DISPLAY_INFO());
					String signDisplayInfo = getSignDisplayInfoFormate(employeeDetails,
							docSignConfig.getSIGN_DISPLAY_INFO());
					System.out.println("signDisplayInfo:" + signDisplayInfo);
					JSONObject signInfo = new JSONObject(docSignConfig.getSIGN_INFO());
					// callBackURL-true ,so that jSign will call to bcportal callbackurl after
					// signing
					// Bcaadhar sign - callbaclURL-true
					// filePath,authtoken,signDisplayInfo:Witness signing
					// stamp,imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
					JSONObject out = new JSONObject(MySignClient.sendFile(pdfRes.getString("PDFPath"), authtoken,
							signDisplayInfo, employeeDetails.getCompositeKey().getEMP_COMPANY(), docCode, signInfo,
							userIP, false, signMode, corpUser));
					// System.out.println("out:"+out);
					out.put("statuscode", "00");
					out.put("statusmsg", "Success");
					FileCleaner fc = new FileCleaner();
					fc.filepath = pdfRes.getString("PDFPath");
					fc.start();
					response = out.toString();
					JSONObject resp = new JSONObject(response);
					if ("SUCCESS".equalsIgnoreCase(resp.getString("status"))) {
						String referenceToken = resp.getString("token");
						JSONObject empdet = new JSONObject();
						empdet.put("empid", empid);
						empdet.put("refno", docCode + refno);// CVP refn0
						empdet.put("token", referenceToken);// jsign token
						empdet.put("ip", ip);
						empdet.put("empGroup", emp.getString("empGroup"));
						empdet.put("docCode", emp.getString("docCode"));
						empdet.put("empCompany", employeeDetails.getCompositeKey().getEMP_COMPANY());
						empdet.put("empname", employeeDetails.getEMP_NAME());
						empdet.put("signType", signType);

						PDAOS.insertSign_log(empdet);

//						if(!signMode.equals("2")) {
						// preparing signerData for BC
						JSONObject signerData = new JSONObject();
						signerData.put("signerName", employeeDetails.getEMP_NAME());
						signerData.put("signerMobile", employeeDetails.getEMP_PHONE());
						// pushing signerData to memory for checking signer name after signing
						// if signer name is not matching signed document will be rejected
						// key will be jsign response token
						ContextObjects.setContextObjects(signerData, referenceToken);
						// }

					}
					// else {
//						JSONObject out = new JSONObject();
//						out.put("statuscode", "01");
//						out.put("statusmsg", pdfRes.getString("statusDetails"));
//						response = out.toString();
					// return response;
					// }

				} else {
					JSONObject out = new JSONObject();
					out.put("statuscode", "01");
					System.out.println("pdfRes.getString(\"statusDetails\"):" + pdfRes.getString("statusDetails"));
					out.put("statusmsg", pdfRes.getString("statusDetails"));
					response = out.toString();
					return response;
				}
			} else {
				JSONObject out = new JSONObject();
				out.put("statuscode", "01");
				out.put("statusmsg", "Document Already Signed !!");
				response = out.toString();
			}

		} catch (JSONException e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		} catch (Exception e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}

		System.out.println("responmse is:" + response);
		return response;
	}

	public String getSignDisplayInfoFormate(Employee_Master empDetails, String signDisplayInfo) {

		java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = simpleDateFormat.format(ts.getTime());

		String res = "";
		String temp = "";

		try {
			JSONArray jsarray = new JSONArray(signDisplayInfo);
			for (int i = 0; i < jsarray.length(); i++) {
				JSONObject js = jsarray.getJSONObject(i);
				temp = js.getString("displayMsg");
				if (temp.contains("$$name$$")) {
					res += temp.replace("$$name$$", empDetails.getEMP_NAME());
				} else if (temp.contains("$$empid$$")) {
					res += temp.replace("$$empid$$", empDetails.getCompositeKey().getEMP_ID());
				} else if (temp.contains("$$date$$")) {
					res += temp.replace("$$date$$", time);
				} else if (temp.contains("$$company$$")) {
					res += temp.replace("$$company$$", empDetails.getCompositeKey().getEMP_COMPANY());
				} else if (temp.contains("$$bank$$")) {
					JSONObject additionalData = new JSONObject(empDetails.getADDITIONAL_DATA());
					res += temp.replace("$$bank$$", additionalData.getString("bank"));
				} else {
					res += temp;
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public String downloadAndSaveSignedPledge(String filename, String txnid, String docCode) {
		JSONObject out = new JSONObject();
		String response = out.toString();
		String filepath = MySignClient.fileDownload(filename, docCode);
		response = PDAOS.updateEmployeeSignStatus(filepath, txnid, filename);
		return response;
	}

	@Override
	public String getSignedPledgePath(String empInfo) {
		JSONObject resp = PDAOS.getSignedDownloadPath(empInfo);
		String path = null;
		try {
			path = resp.getString("path");
		} catch (JSONException e) {
			System.out.println("getSignedPledgePath path not found");
		} catch (NullPointerException e) {
			System.out.println("getSignedPledgePath not found");
		}
		// PDAOS.updateDownloadStatus(empInfo);
		return path;
	}

	@Override
	public JSONObject getemailInfo(String empInfo) {
		JSONObject response = new JSONObject();
		try {
			JSONObject input = new JSONObject(empInfo);
			String usertype = input.getString("usertype");
			String value = input.getString("value");
			response = PDAOS.getMailInfo(usertype, value);
			String toMail = PDAOS.getHRMail();
			response.put("hr", toMail);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public JSONArray getEmpList(String report) {
//		System.out.println("Inside...getEmpList");
		JSONArray response = new JSONArray();
		// JSONObject dropDownList=null;
		try {
			JSONObject input = new JSONObject(report);
			String authToken = input.getString("authToken");
			JSONObject tokens = ContextObjects.getAuthTokens();
			if (tokens.has(authToken)) {
				JSONObject authTokenDetails = new JSONObject(tokens.getString(authToken));
				if (authTokenDetails.getString("username").equals(input.getString("username"))) {
					// System.out.println("Validation Success");
					if (input.has("value") && input.has("document") && input.has("group") || input.has("company")) {
						String value = input.getString("value");
						String document = input.getString("document");
						String group = null;
						String company = null;

						if (input.has("group")) {
							group = input.getString("group");
						}
						if (input.has("company")) {
							company = input.getString("company");
						}
						response = PDAOS.getEmployeeList(value, group, company, document);
					} else {
						System.out.println("Validation Failed");
					}
				}
			} else {
				System.out.println("Auth Token is not present");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public boolean updatePhysicalSignedrecord(String empid, String refno, String ip, String outfilepath) {
		return PDAOS.updatePhysicalSignedRecord(empid, refno, ip, outfilepath);
	}

	@Override
	public String validateotp(String userinfo, String ip) {
		JSONObject out = new JSONObject();
		String empGroup = "";
		JSONObject additionalData = null;
		try {
			JSONObject input = new JSONObject(userinfo);
			boolean isvalid = false;
			if (input.has("empgroup")) {
				empGroup = input.getString("empgroup");
			} else {
				empGroup = "";
			}
			Employee_Master empData = PDAOS.getEmployeeDetails(input.getString("empid"), empGroup);
			// System.out.println("empData:"+empData.getADDITIONAL_DATA());
			String loginmode = Properties_Loader.getLOGIN_MODE();
			if (empData.getADDITIONAL_DATA() != null) {
				// if(!input.getString("empGroup").equals("")) {
				additionalData = new JSONObject(empData.getADDITIONAL_DATA());

				if (input.has("emppan")) {
					String empPanNumber = additionalData.getString("pan");

					if (input.getString("emppan").equalsIgnoreCase(empPanNumber)
							&& empGroup.equalsIgnoreCase(empData.getCompositeKey().getEMP_GROUP())) {
						isvalid = true;
						JSONObject auditInput = new JSONObject();
						auditInput.put("empid", input.getString("empid"));
						auditInput.put("operationtype", "PAN Validation");
						auditInput.put("operationremarks", "Success");
						auditInput.put("userid", "");
						auditInput.put("ip", ip);
						PDAOS.insertauditinfo(auditInput);
					} else {
						out.put("statuscode", "01");
						out.put("statusmsg", "PAN validation failed.");
						JSONObject auditInput = new JSONObject();
						auditInput.put("empid", input.getString("empid"));
						auditInput.put("operationtype", "PAN Validation");
						auditInput.put("operationremarks", "PAN Validation Failed");
						auditInput.put("userid", "");
						auditInput.put("ip", ip);
						PDAOS.insertauditinfo(auditInput);
						return out.toString();
					}

				}
			}
			if (input.has("otp")) {
				if (OtpHandler.validateOtp(input.getString("otp"), input.getString("empid"))) {
					isvalid = true;
					OtpHandler.removeOtp(input.getString("empid"));
					JSONObject auditInput = new JSONObject();
					auditInput.put("empid", input.getString("empid"));
					auditInput.put("operationtype", "Otp Validation");
					auditInput.put("operationremarks", "Success");
					auditInput.put("userid", "");
					auditInput.put("ip", ip);
					PDAOS.insertauditinfo(auditInput);
				} else {
					out.put("statuscode", "01");
					out.put("statusmsg", "OTP validation failed.");
					JSONObject auditInput = new JSONObject();
					auditInput.put("empid", input.getString("empid"));
					auditInput.put("operationtype", "OTP Validation");
					auditInput.put("operationremarks", "OTP Validation Failed");
					auditInput.put("userid", "");
					auditInput.put("ip", ip);
					PDAOS.insertauditinfo(auditInput);
					return out.toString();
				}
			}
			if (isvalid) {
				String authToken = UUID.randomUUID().toString(); // Generating authToken
				JSONObject employeeDetail = new JSONObject();
				employeeDetail.put("empname", empData.getEMP_NAME());
				employeeDetail.put("empid", empData.getCompositeKey().getEMP_ID());
				employeeDetail.put("reqtime", new Date());
				ContextObjects.setAuthTokens(authToken, employeeDetail);
				Date date = new Date();
				if (empData.getADDITIONAL_DATA() != null) {
					JSONObject additionalDataValue = new JSONObject(empData.getADDITIONAL_DATA());

					if (additionalDataValue.length() > 0) {
						if (additionalDataValue.has("companyFullName")) {
							out.put("companyFullName", additionalDataValue.getString("companyFullName"));
						}
					}
					additionalData.put("day", new SimpleDateFormat("dd").format(date));
					additionalData.put("month", new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date.getTime()));
					additionalData.put("year", new SimpleDateFormat("yyyy").format(date));
					additionalData.put("mobile", empData.getEMP_PHONE());
					additionalData.put("dob", empData.getEMP_DOB());
					out.put("additionalData", additionalData.toString());
				}
				out.put("name", empData.getEMP_NAME());
				out.put("company", empData.getCompositeKey().getEMP_COMPANY());
				out.put("empid", empData.getCompositeKey().getEMP_ID());
				out.put("statuscode", "00");
				out.put("statusmsg", "SUCCESS");
				out.put("authToken", authToken);
				out.put("empGroup", empData.getCompositeKey().getEMP_GROUP());

			}
		} catch (NullPointerException e) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "FAILURE");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (JSONException e) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "FAILURE");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (Exception e) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "FAILURE");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return out.toString();
	}

	public boolean generateOTP(String empid, String ip, String empGroup) {
		boolean resp = false;
		Employee_Master empData = PDAOS.getEmployeeDetails(empid, "");
		String phoneNo = empData.getEMP_PHONE();
		String email = empData.getEMP_EMAIL();
		String response = null;
		if (OtpHandler.otpStore.has(empid)) {
			try {
				String otp = OtpHandler.otpStore.getString(empid);
//				System.out.println("OTP : " + otp);
				// response = null;
				response = new OtpHandler().sendOTPphone(otp, phoneNo);
				if (email != null) {
					new OtpHandler().emailOtp(email, otp, empData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			Random rr = new Random(System.currentTimeMillis());
			String otp = 100000 + rr.nextInt(900000) + "";
			// response = null;
			response = new OtpHandler().sendOTPphone(otp, phoneNo);
			if (email != null) {
				new OtpHandler().emailOtp(email, otp, empData);
			}
			OtpHandler.addOtp(otp, empid);
		}

		try {
			JSONObject auditInput = new JSONObject();
			auditInput.put("empid", empData.getCompositeKey().getEMP_ID());
			auditInput.put("operationtype", "Otp Generation");
			auditInput.put("operationremarks", response);
			auditInput.put("userid", "");
			auditInput.put("ip", ip);
			PDAOS.insertauditinfo(auditInput);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		resp = true;

		return resp;
	}

	@Override
	public void insertauditinfo(JSONObject input) {
		PDAOS.insertauditinfo(input);
	}

	@Override
	public JSONObject checkSignstatus(String txnid) {
		return PDAOS.checksignstatus(txnid);
	}

	@Override
	public JSONObject getWidget(String reportInputs) {
		JSONObject response = new JSONObject();
		try {
			JSONObject input = new JSONObject(reportInputs);
			String listType = input.getString("listType");
			String company = input.getString("company");
			response = PDAOS.getWidgetData(listType, company);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public JSONObject getDocList(JSONObject inputs) {
		JSONObject response = new JSONObject();
		JSONArray resp = new JSONArray();
		JSONArray langCodearr = new JSONArray();
		try {
			inputs.getString("empCode");
			int fee = checkingForPentanlyFee(inputs.getString("empCode"));
			int records = fee / 50;
			JSONObject res = PDAOS.getDocListInEAT(inputs);
			JSONArray docList = new JSONArray(res.getString("docList"));
			if (docList.length() > 0) {
				for (int i = 0; i < docList.length(); i++) {
					JSONObject docInfo = PDAOS.getDocInfoInEAM(docList.getString(i));
					System.out.println("docInfo  :" + docInfo);
					JSONObject respUI = new JSONObject();
					respUI.put("docName", docInfo.getString("docName"));
					respUI.put("docActionType", docInfo.getString("docActionType"));
					respUI.put("docCode", docList.getString(i));

					Sign_Doc_Details signDocDetails = PDAOS.checkSignDocDetails(inputs.getString("empCode"),
							inputs.getString("companyCode"), inputs.getString("empGroup"), docList.getString(i));
					if (signDocDetails == null) {
						respUI.put("docSignStatus", 0);
						respUI.put("signOrder", 0);
						respUI.put("txnID", "");
					} else {
						respUI.put("docSignStatus", 1);
						respUI.put("signOrder", signDocDetails.getSIGN_ORDER());
						respUI.put("txnID", signDocDetails.getTXNID());
					}
					resp.put(respUI);
				}
				response.put("status", "SUCCESS");
				response.put("Records", records);
				response.put("penatlyFee", fee);
				response.put("statusDetails", "List of Documents");
				response.put("docList", resp);

			} else {
				response.put("status", "");
				response.put("statusDetails", "NO Documents on ur account");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	private int checkingForPentanlyFee(String empId) {
//		inputs
		int penatlyFee = 0;
		List<Sign_Rejected_Info> sign_rejected_info = PDAOS.getRejectedList(empId);
		int records = sign_rejected_info.size();
		if (records > 0) {
			penatlyFee = records * 50;
			return penatlyFee;
		} else {
			return penatlyFee;
		}

	}

	@Override
	public String witnessEsign(String empInfo, String ip) {
		String response = null;
		// for OTP Based signing signMode is kept as 4
		String signMode = "4";
		String signType = "ESIGN";
		try {
			JSONObject emp = new JSONObject(empInfo);
			String empCompany = emp.getString("empCompany");
			String empid = emp.getString("empid");
			String empGroup = emp.getString("empGroup");
			String docCode = emp.getString("docCode");
			String userIP = emp.getString("userIP");
			int signOrder = Integer.parseInt(emp.getString("signOrder"));
			Doc_Sign_Config docSignConfig = null;
			String signDisplayInfo = "";
			String filePath = "";
			Employee_Master employeeDetails = PDAOS.getEmployeeDetails(empid, empGroup);
			// for inserting intp log purpose
			String corpUser = employeeDetails.getCompositeKey().getEMP_COMPANY() + ":" + empGroup + ":" + empid;
			Sign_Doc_Details signDocInfo = PDAOS.checkSignDocDetails(empid, empCompany, empGroup, docCode);
			List<Doc_Sign_Config> doc_Sign_Config = PDAOS.getDocSignConfig(docCode, "1", signOrder + 1);
			ListIterator<Doc_Sign_Config> iterator = doc_Sign_Config.listIterator();

			while (iterator.hasNext()) {
				docSignConfig = (Doc_Sign_Config) iterator.next();
			}
			// checking whether the document is for witness signing by using signOrder
			// signOrder:1-BC sign,2-Witness sign,3-BC Cell sign.
			if (!(signOrder >= 2)) {
				// Checking whether signMode is 4 because for OTP Based signing all the below
				// inputs must be prepared and sent
				if (signMode.equals("4")) {
					JSONObject signerData = emp.getJSONObject("signerData");
					JSONObject otpSinginginputs = new JSONObject();
					otpSinginginputs.put("docID", emp.getString("docID"));
					otpSinginginputs.put("mobileOTP", emp.getString("mobileOTP"));
					otpSinginginputs.put("filePath", signDocInfo.getDOC_PATH());
					otpSinginginputs.put("mobileNo", signerData.getString("signerMobile"));
					otpSinginginputs.put("signerName", signerData.getString("signerName"));
					otpSinginginputs.put("handSignImg", emp.getString("handSignImg"));
					filePath = otpSinginginputs.toString();
				} else {
					// for aAdhaar Based signing only filePath is sent
					filePath = signDocInfo.getDOC_PATH();
				}
				JSONObject validateLoginResp = MySignClient.validateLogin(userIP);
				String authtoken = validateLoginResp.getString("authToken");
				if (docSignConfig.getSIGN_DISPLAY_INFO().trim().length() > 0) {
					signDisplayInfo = getSignDisplayInfoFormate(employeeDetails, docSignConfig.getSIGN_DISPLAY_INFO());
				} else {
					java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = simpleDateFormat.format(ts.getTime());
					signDisplayInfo = emp.getString("signDisplayInfo");
					signDisplayInfo = signDisplayInfo + "Date : " + time + " IST";
				}
				JSONObject signInfo = new JSONObject(docSignConfig.getSIGN_INFO());
				// callBackURL-true ,so that jSign will call to bcportal callbackurl after
				// signing
				// Bcaadhar sign - callbaclURL-true
				// filePath:JSON object is converted to toString format and sent for OTP Based
				// Signing else only the actual filepath is sent
				// authtoken,signDisplayInfo:Witness signing
				// stamp,imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
				JSONObject out = new JSONObject(MySignClient.sendFile(filePath, authtoken, signDisplayInfo,
						employeeDetails.getCompositeKey().getEMP_COMPANY(), emp.getString("docCode"), signInfo,
						emp.getString("userIP"), false, signMode, corpUser));
//				out.put("statuscode", "00");
//				out.put("statusmsg", "Success");
				response = out.toString();
				JSONObject resp = new JSONObject(response);
				if ("SUCCESS".equalsIgnoreCase(resp.getString("status"))) {
					String referenceToken = resp.getString("token");
					JSONObject empdet = new JSONObject();
					empdet.put("empid", empid);
					empdet.put("refno", signDocInfo.getTXNID());
					empdet.put("token", referenceToken);
					empdet.put("ip", ip);
					empdet.put("empGroup", empGroup);
					empdet.put("docCode", docCode);
					empdet.put("empCompany", employeeDetails.getCompositeKey().getEMP_COMPANY());
					empdet.put("empname", employeeDetails.getEMP_NAME());
					if (signMode.equals("4")) {
						signType = "OTP Singing";
						empdet.put("signType", signType);
					}
					PDAOS.insertSign_log(empdet);

					// pushing signerData to memory for checking signer name after signing
					// if signer name is not matching signed document will be rejected
					// key will be jsign response token
					ContextObjects.setContextObjects(emp.getJSONObject("signerData"), referenceToken);

				}
			} else {
				JSONObject out = new JSONObject();
				out.put("statuscode", "01");
				out.put("statusmsg", "Document Already Signed !!");
				response = out.toString();
			}

		} catch (JSONException e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		} catch (Exception e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Purpose : to get signed documents from table Input :
	 * {"username":"abc","authToken":"rtytrhgh","group":"OBC","company":"i25BCA",
	 * "signOrder":"2"} Status : Success/Failure, Output : {"status" :
	 * "SUCCESS","statusDetails" : "List of documents", "list" :
	 * [{"empid":"1234567","docCode":"BCAA","signDate":"2020-04-22 12:25:00","txnID"
	 * :"BCAAINT17100000051586495",
	 * "signOrder":2,"empname":"BC name","signername":"signer name from esign"},...]
	 * }
	 */
	@Override
	public JSONObject getSignedDocList(JSONObject inputs) {
		JSONObject response = new JSONObject();
		try {
			JSONObject res = PDAOS.getSignedDocListInSDD(inputs);
			JSONArray resp = new JSONArray(res.getString("list"));
//			System.out.println("resp=>" + resp);
			if (resp.length() > 0) {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "List of Documents");
				response.put("list", resp);
			} else {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "No Documents on your account");
				response.put("list", resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	/*
	 * Purpose : To sign to existing document Input : {"username" :
	 * "xyz","authToken" : "agfsdgfsdf","mode":"esign", "list" :
	 * [{"empid":"1234567","docCode":"BCAA","txnID":"BCAAINT17100000051586495",
	 * "signOrder": 2},...]} Options : 1. mode- esign:- for aadhar based signing, 2.
	 * mode- token:- for token based signing Status : Success/Failure, Output : In
	 * case of success (eSign) {"status" : "SUCCESS","statusDetails" :
	 * "NSDL page is displayed"} In case of success (token){"status" :
	 * "SUCCESS","statusDetails" : "DSC Token signing completed"}
	 */
	@Override
	public String signExistingDocument(String empInfo, String ip) {
		String response = null;
		try {
			JSONObject emp = new JSONObject(empInfo);
			String mode = emp.getString("mode");
			JSONArray resp1 = new JSONArray(emp.getString("list"));
			for (int i = 0; i < resp1.length(); i++) {
				String empid = resp1.getJSONObject(i).getString("empid");
				String docCode = resp1.getJSONObject(i).getString("docCode");
				String txnID = resp1.getJSONObject(i).getString("txnID");
				int signOrder = Integer.parseInt(resp1.getJSONObject(i).getString("signOrder")) + 1;

				response = signExistingDocumentList(empid, docCode, txnID, signOrder, mode, emp);
			}
		} catch (JSONException e) {
			JSONObject out = new JSONObject();
			try {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}
		return response;
	}

	// same inputs as signExistingDocument
	public String signExistingDocumentList(String empid, String docCode, String txnID, int signOrder, String mode,
			JSONObject emp) {
		String response = null;
		String filePath = null;
		JSONObject js = null;
		String signMode = "1";
		String signType = "ESIGN";

		try {
			JSONObject preSignRes = new JSONObject();
			JSONObject out = new JSONObject();
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

			Sign_Doc_Details signDocInfo = PDAOS.getSignedDocListTxnid(txnID);
			Employee_Master employeeDetails = PDAOS.getEmployeeDetails(empid, signDocInfo.getEMP_GROUP());
			// for inserting intp log purpose
			String corpUser = employeeDetails.getCompositeKey().getEMP_COMPANY() + ":"
					+ employeeDetails.getCompositeKey().getEMP_GROUP() + ":" + empid;
			filePath = signDocInfo.getDOC_PATH();
			List<Doc_Sign_Config> doc_Sign_Config = PDAOS.getDocSignConfig(docCode, "1", signOrder);
			ListIterator<Doc_Sign_Config> iterator = doc_Sign_Config.listIterator();
			while (iterator.hasNext()) {
				Doc_Sign_Config docSignConfig = (Doc_Sign_Config) iterator.next();

				// making pre sign the document if specified using eToken
				String DSC_token_id = docSignConfig.getDSC_TOKEN_ID();
				if (mode.equalsIgnoreCase("token")) {
					if (DSC_token_id != null && DSC_token_id.trim() != "" && DSC_token_id.trim().length() > 1) {
						// checking whether the sign order is 2 and not greater than 2 i.e if it is
						// Witness signed only so that BC Cell cannot sign the Agreement once again

						if (signDocInfo.getSIGN_ORDER() == 2) {
							preSignRes = makePreSign(docSignConfig, filePath, employeeDetails);
							// preSignRes is success update db
							if (preSignRes.getString("status").equalsIgnoreCase("SUCCESS")) {
								signDocInfo.setDOC_PATH(preSignRes.getString("PDFPath"));
								signDocInfo.setSIGN_DATE(currentTimestamp);
								signDocInfo.setSIGN_ORDER(signDocInfo.getSIGN_ORDER() + 1);
								signDocInfo.setSIGNER_NAME(
										signDocInfo.getSIGNER_NAME() + "," + preSignRes.getString("signerName"));
								PDAOS.updateSignDocDetails(signDocInfo);

								Employee_action_status statusInfo = new Employee_action_status();
								statusInfo.setCOMPANY_CODE(employeeDetails.getCompositeKey().getEMP_COMPANY());
								statusInfo.setDOCUMENT_CODE(docCode);
								statusInfo.setEMP_CODE(empid);
								statusInfo.setEMP_GROUP(employeeDetails.getCompositeKey().getEMP_GROUP());
								statusInfo.setSIGN_TYPE("DSC Token");
								statusInfo.setEMP_NAME(employeeDetails.getEMP_NAME());
								statusInfo.setSIGN_DATE(currentTimestamp);
								statusInfo.setSIGN_STATUS(1);
								statusInfo.setSIGNER_NAME(preSignRes.getString("signerName"));
								PDAOS.insertStatusInEAS(statusInfo);
								// mailing signed pdf After DSC Token Signature
								JSONObject input = new JSONObject();
								input.put("usertype", "auto");
								input.put("value", signDocInfo.getTXNID());
								js = getEmailSignedPledge(input.toString());

								out.put("status", "SUCCESS");
								out.put("statusDetails", "Digital signing with DSC Token completed");
								return out.toString();
							} else {

								out.put("status", "FAILURE");
								out.put("statusDetails", preSignRes.getString("statusDetails"));
								return out.toString();
							}
						} else {
							out.put("status", "FAILURE");
							if (signDocInfo.getSIGN_ORDER() >= 3) {
								out.put("statusDetails",
										"BC Cell has already signed the document. MID:" + signDocInfo.getEMPID());
							} else {
								out.put("statusDetails", "BC and witness sign is not completed");
							}
							return out.toString();
						}
					} else {
						out.put("status", "FAILURE");
						out.put("statusDetails", "DSC Token id not found");
						response = out.toString();
					}
				} else {

					// for Aadhaar esign based signing implementation
					if (DSC_token_id.trim() == "" || DSC_token_id == null || DSC_token_id.trim().length() == 0) {
						// String authtoken = MySignClient.validateLogin(emp.getString("userIP"));
						JSONObject validateLoginResp = MySignClient.validateLogin(emp.getString("userIP"));

						String authtoken = validateLoginResp.getString("authToken");
						String signDisplayInfo = getSignDisplayInfoFormate(employeeDetails,
								docSignConfig.getSIGN_DISPLAY_INFO());
						JSONObject signInfo = new JSONObject(docSignConfig.getSIGN_INFO());
						// callBackURL-true ,so that jSign will call to bcportal callbackurl after
						// signing
						// Bcaadhar sign - callbaclURL-true
						// filePath,authtoken,signDisplayInfo:Witness signing
						// stamp,imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
						out = new JSONObject(MySignClient.sendFile(filePath, authtoken, signDisplayInfo,
								employeeDetails.getCompositeKey().getEMP_COMPANY(), docCode, signInfo,
								emp.getString("userIP"), true, signMode, corpUser));

						response = out.toString();

						JSONObject resp = new JSONObject(response);
						System.out.println("resp.getString(\"status\")::::" + resp.getString("status"));
						if ("SUCCESS".equalsIgnoreCase(resp.getString("status"))) {
							String referenceToken = resp.getString("token");
							JSONObject empdet = new JSONObject();
							empdet.put("empid", empid);
							empdet.put("refno", signDocInfo.getTXNID());
							empdet.put("token", referenceToken);
							empdet.put("ip", emp.getString("userIP"));
							empdet.put("empGroup", employeeDetails.getCompositeKey().getEMP_GROUP());
							empdet.put("docCode", docCode);
							empdet.put("empCompany", employeeDetails.getCompositeKey().getEMP_COMPANY());
							empdet.put("empname", employeeDetails.getEMP_NAME());
							empdet.put("signType", signType);
							if (signMode.equals("2")) {
								signType = "Electronic";
								empdet.put("signType", signType);
							}
							System.out.println("service impl empdet" + empdet);
							PDAOS.insertSign_log(empdet);
							// preparing signerData for BC Cell signing
							// if signer name is not matching signed document will be rejected
							// key will be jsign response token
							String bccellName = null;

							JSONObject tokens = ContextObjects.getAuthTokens();

							if (tokens.has(emp.getString("authToken"))) {
								JSONObject userDetails = tokens.getJSONObject(emp.getString("authToken"));
								// JSONObject userDetails =emp.getString("authToken");
								bccellName = userDetails.getString("username");
//								bccellName = "Hema N";
//								bccellName = "Elango S";

							}
							JSONObject signerData = new JSONObject();
							signerData.put("signerName", bccellName);
							signerData.put("signerType", "BC Cell");
							ContextObjects.setContextObjects(signerData, referenceToken);

							return out.toString();
						} else {
							out.put("status", "FAILURE");
							out.put("statusDetails", resp.getString("statusDetails"));
							return out.toString();
						}
					} else {

						out.put("status", "FAILURE");
						out.put("statusDetails", "No Records Found");
						response = out.toString();
					}
				}
			} // while
		} catch (Exception e) {
			JSONObject out = new JSONObject();
			try {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Purpose : To sign the documents based on the Token Input : {filepath,
	 * employeeDetails, DSCtokenID } Status : Success/Failure, Output : {"status" :
	 * "SUCCESS","statusDetails" : "Token Pre Sign Success"} {"status" :
	 * "FAILURE","statusDetails" : "Related error message"}
	 */
	private static JSONObject makePreSign(Doc_Sign_Config docSignConfig, String filePath,
			Employee_Master employeeDetails) {
		JSONObject jsRes = new JSONObject();
		PledgeAppDAOServices PDS = new PledgeAppDAOImpl();

		Dsc_Token_Config dsc_Token_Config = PDS.getDSCTokenConfig(docSignConfig.getDSC_TOKEN_ID());
		try {
			JSONObject dscTokenInfo = new JSONObject(dsc_Token_Config.getDSC_TOKEN_INFO());

			String configPath = new String(
					org.apache.commons.codec.binary.Base64.decodeBase64(dscTokenInfo.getString("configPath")));
			String password = new String(
					org.apache.commons.codec.binary.Base64.decodeBase64(dscTokenInfo.getString("password")));
			String alias = new String(
					org.apache.commons.codec.binary.Base64.decodeBase64(dscTokenInfo.getString("alias")));
			String signDisplayInfo = null;
			if (docSignConfig.getSIGN_DISPLAY_INFO().length() > 0) {
				signDisplayInfo = docSignConfig.getSIGN_DISPLAY_INFO();
			} else {
				signDisplayInfo = dsc_Token_Config.getSIGN_DISPLAY_INFO();
			}

			JSONObject tokenResponse = new JSONObject();

			if (dsc_Token_Config.getPRESENCE_STATUS().equalsIgnoreCase("I")) {

				tokenResponse = PDFUSBTokenSign.addSignatureUsingUSBToken(filePath, "",
						new JSONObject(docSignConfig.getSIGN_INFO()), configPath, password, alias, signDisplayInfo);

			} else if (dsc_Token_Config.getPRESENCE_STATUS().equalsIgnoreCase("E")) {
				// external token related code

				JSONObject tokenStatus = GeneratePDF.checkTokenStatus(dsc_Token_Config);
				if (tokenStatus.getString("status").equalsIgnoreCase("SUCCESS")) {
					tokenResponse = GeneratePDF.getDSCSignExternalToken(dsc_Token_Config, filePath, docSignConfig,
							employeeDetails);
				} else {
					System.out.println("E-Token status : " + tokenStatus);
					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", tokenStatus.getString("statusDetails"));
					return jsRes;
				}
			}

			if (tokenResponse.getString("status").equalsIgnoreCase("SUCCESS")) {
				jsRes.put("status", "SUCCESS");
				jsRes.put("statusDetails", "Token Pre Sign Success");
				jsRes.put("PDFPath", tokenResponse.getString("PDFPath"));
				jsRes.put("signerName", tokenResponse.getString("signerName"));
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", tokenResponse.getString("statusDetails"));

			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsRes;
	}

	public JSONObject getEmailSignedPledge(String userInfo) {
		// JSONObject response = new JSONObject();
		String mailid = "";
		JSONObject mailInfo = getemailInfo(userInfo);
		JSONObject output = new JSONObject();
		String content = "";
		String subject = "";

		if (mailInfo.has("path")) {
			try {
				// For core value pledge
//			String content = "Dear " + mailInfo.getString("name") + " (" + mailInfo.getString("empid") + "),"
//					+ "\r\n" + "\r\n" + "Please Find Attached the Signed Core Value Pledge document. \r\n" + "\r\n"
//					+ "Regards, \r\n" + "HR Desk";
//			String subject = "Core Value Pledge - " + mailInfo.getString("company") + ", "
//					+ mailInfo.getString("empid") + "," + mailInfo.getString("name");

				if (mailInfo.has("company") && mailInfo.getString("company").equalsIgnoreCase("i25BCA")) {

					content = "Dear " + mailInfo.getString("name") + " (" + mailInfo.getString("empid") + ")," + "\r\n"
							+ "\r\n" + "Please find the attached Signed BCA Agreement document. \r\n" + "\r\n"
							+ "Regards, \r\n" + "BC Cell";
					subject = "Signed BCA Agreement - " + mailInfo.getString("company") + ", "

							+ mailInfo.getString("empid") + "," + mailInfo.getString("name");

				} else {
					content = "Dear " + mailInfo.getString("name") + " (" + mailInfo.getString("empid") + ")," + "\r\n"
							+ "\r\n" + "Please find the attached Signed Core Value document. \r\n" + "\r\n"
							+ "Regards, \r\n" + "HRD";
					subject = "Signed Core Value document - " + mailInfo.getString("company") + ", "
							+ mailInfo.getString("empid") + "," + mailInfo.getString("name");
				}

				if (mailInfo.has("mail") && mailInfo.getString("mail") != null) {
					mailid = mailInfo.getString("mail");
				}

				MailHandler.sendAttachmentMail(content, subject, mailid, mailInfo.getString("hr"),
						mailInfo.getString("path"), mailInfo);
				output.put("statuscode", "00");
				output.put("statusmsg", "SUCCESS");
			} catch (JSONException e) {
				e.printStackTrace();
				try {
					output.put("statuscode", "01");
					output.put("statusmsg", "FAILURE");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					output.put("statuscode", "00");
					output.put("statusmsg", "FAILURE");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}

		} else {
			try {
				output.put("statuscode", "01");
				output.put("statusmsg", "FAILURE File Not Found");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// return output.toString();
		return output;
	}

	/*
	 * Purpose : to send notification to BCs Input :
	 * {"username":"abc","authToken":"rtytrhgh","type": "SMS/EMAIL",
	 * "signOrder":"0/1","list":[{"mobileNo":"1234567890","emailID":"abcd@gmail.com"
	 * },...]} Status: Success/Failure, Output : {"status" :
	 * "SUCCESS","statusDetails" : "Notification Initiated", "status" :
	 * "FAILURE","statusDetails" :"Related error message"}
	 */
	@Override
	public String sendNotification(String userInfo, String ip) {
		String response = null;
		String hrConfig = "";
		JSONObject out = new JSONObject();
		JSONArray types = null;
		JSONArray contentList = null;
		try {
			JSONObject input = new JSONObject(userInfo);

			types = new JSONArray(input.getString("type"));
			contentList = new JSONArray(input.getString("list"));

			JSONObject myJsonObject = new JSONObject();
			for (int i = 0; i < types.length(); i++) {
				myJsonObject = types.getJSONObject(i);
				String type = myJsonObject.getString("type");
				System.out.println("type:"+type);
				if (type.equalsIgnoreCase("SMS")) {
					smsth = new SMSThread(contentList, type, input);
					if (!smsth.isAlive()) {
						smsth.start();
					}
				} else {
					emailth = new EmailThread(contentList, type, input, hrConfig);
					if (!emailth.isAlive()) {
						emailth.start();
					}
				}
			}
//			}
			out.put("status", "SUCCESS");
			out.put("statusDetails", "Notification Initiated");
			response = out.toString();
		} catch (JSONException e) {
			try {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}
		return response;
	}

	// Inserting the BC details in user request table who ask for updating
	// Phone.No/Name Change
	@Override
	public JSONObject userUpdateRequest(JSONObject inputs) {
		boolean response = false;
		JSONObject out = new JSONObject();
		try {
			List<User_Requests> userReqsList = PDAOS.getUserRequestList(inputs.getString("empId"));
			// System.out.println(userReqsList);
			JSONObject userRequestRes = checkUserRequests(userReqsList, inputs);

			if (userRequestRes.getString("status").equalsIgnoreCase("SUCCESS")) {
				// System.out.println(userRequestRes.getString("statusDetails"));

				String otp = null;
				Employee_Master empData = PDAOS.getEmployeeDetails(inputs.getString("empId"),
						inputs.getString("empGroup"));
				if (empData != null) {
					// Validating PanNumber/AccNo1 and DOB before inserting the record in user
					// request table
					JSONObject additionalData = new JSONObject(empData.getADDITIONAL_DATA());
					String empPanNumber = additionalData.getString("pan");
					String empAccNo1 = additionalData.getString("accountNo1");
					if (empPanNumber.equalsIgnoreCase(inputs.getString("empPanNumber"))
							|| empAccNo1.equalsIgnoreCase(inputs.getString("empPanNumber"))) {
						if (empData.getEMP_DOB().equals(java.sql.Date.valueOf(inputs.getString("empDOB")))) {

							response = PDAOS.insertintoUserRequests(inputs);

							if (response == true) {

								if (inputs.getString("updateType").equals("01")) {
									otp = generateOnlyOtp(inputs.getString("empId"));
									JSONObject typeJSON = new JSONObject();
									JSONObject userData = new JSONObject();
									JSONArray list = new JSONArray();

									// System.out.println("InsidePhoneUpdate");
									typeJSON.put("type", "SMS");

									JSONArray type = new JSONArray();
									type.put(typeJSON);
									inputs.put("type", type);

									userData.put("empid", inputs.getString("empId"));

									userData.put("mobileNo", inputs.getString("mobAadhar"));
									list.put(userData);
									inputs.put("list", list);
									inputs.put("otp", otp);

									String responseData = sendNotification(inputs.toString(),
											inputs.getString("ipAdd"));
//									System.out.println(responseData);
									JSONObject resJS = new JSONObject(responseData);
									if (resJS.getString("status").equalsIgnoreCase("SUCCESS")) {
										out.put("status", "SUCCESS");
										out.put("statusDetails",
												"Request for phone number update recieved. Otp will be sent for verification.");
									} else {
										out.put("status", "FAILURE");
										out.put("statusDetails", "Updating phone details operation failed");
									}

								} else {
									out.put("status", "FAILURE");
									out.put("statusDetails", "Updating phone details operation failed");
								}

								if (inputs.getString("updateType").equals("10")) {
									// System.out.println("InsideNameUpdate");
									out.put("status", "SUCCESS");
									out.put("statusDetails",
											"Request for name change received. Name will be updated after verification.");
									return out;
								}

								if (inputs.getString("updateType").equals("11")) {
									// System.out.println("InsidePhoneandNameUpdate");
									otp = generateOnlyOtp(inputs.getString("empId"));

									JSONObject typeJSON = new JSONObject();
									JSONObject userData = new JSONObject();
									JSONArray list = new JSONArray();

									// System.out.println("InsidePhoneUpdate");
									typeJSON.put("type", "SMS");

									JSONArray type = new JSONArray();
									type.put(typeJSON);
									inputs.put("type", type);

									userData.put("empid", inputs.getString("empId"));

									userData.put("mobileNo", inputs.getString("mobAadhar"));
									list.put(userData);
									inputs.put("list", list);
									inputs.put("otp", otp);

									String responseData = sendNotification(inputs.toString(),
											inputs.getString("ipAdd"));
//									System.out.println(responseData);
									JSONObject resJS = new JSONObject(responseData);
									if (resJS.getString("status").equalsIgnoreCase("SUCCESS")) {
//									new OtpHandler().sendOTPphone(otp, inputs.toString());
										out.put("status", "SUCCESS");
										out.put("statusDetails",
												"Request for name and mobile updation received. Otp will be sent for verifing phone number.");
										return out;
									} else {
										out.put("status", "FAILURE");
										out.put("statusDetails",
												"Failed to receive the update request of BC details,Try after sometime");
									}
								}
							} else {
								out.put("status", "FAILURE");
								out.put("statusDetails",
										"Failed to receive the update request of BC details,Try after sometime");
							}
						} else {
							out.put("status", "FAILURE");
							out.put("statusDetails", "Request for updation failed,DOB is incorrect");
						}
					} else {
						out.put("status", "FAILURE");
						out.put("statusDetails", "Request for updation failed AccountNo/PAN is incorrect.");
						return out;
					}

				} else {
					out.put("status", "FAILURE");
					out.put("statusDetails", "User not found! Cannot update the details");
				}
			} // User has already requeste for one of the above update request so rejecting
				// the request
			else {
				out.put("status", userRequestRes.getString("status"));
				out.put("statusDetails", userRequestRes.getString("statusDetails"));
			}
		}

		catch (JSONException e1) {
			e1.printStackTrace();
		}
		return out;
	}

	// Validating whether the Bc has already requested for Name Change refering the
	// user request table
	private JSONObject checkUserRequests(List<User_Requests> userReqsList, JSONObject userReqData) {
		JSONObject jsresp = new JSONObject();
		try {
			if (!userReqsList.isEmpty()) {
//				for (User_Requests user_Requests : userReqsList) {
//					if((!user_Requests.getVERIFIED_STATUS().equals("11")||user_Requests.getUPDATE_REQ_TYPE().equals("10"))
//							&& ((userReqData.getString("updateType").equals("10")
//							|| userReqData.getString("updateType").equals("11")))){

				jsresp.put("status", "FAILURE");
				jsresp.put("statusDetails",
						"You have already updated/requested for name change. Contact BC Cell to update/request name change once again");
				return jsresp;

//					}
//				}

				// System.out.println("Inside");
//				for (User_Requests user_Requests : userReqsList) {
//					// Already requested for Phone No Change
////					if (userReqData.getString("updateType").equals("01")
////							&& user_Requests.getUPDATE_REQ_TYPE().equals("01")) {
////						jsresp.put("status", "FAILURE");
////						jsresp.put("statusDetails", "You have already updated mobile number. Cannot update once again");
////						return jsresp;
////					}
//					// Already Requested for Name and Phone No Change
//					// Again Can't request for (Name and Phone No Change )or Name Change only
//					  if (user_Requests.getUPDATE_REQ_TYPE().equals("11")
//							&& ((userReqData.getString("updateType").equals("10")
//									|| userReqData.getString("updateType").equals("11")))) {
//						// || user_Requests.getUPDATE_REQ_TYPE().equals("11")) {
//						// System.out.println("PhoneNameRequestFirst");
//						jsresp.put("status", "FAILURE");
//						jsresp.put("statusDetails",
//								"You have already updated/requested for name change. Cannot update/request once again");
//						return jsresp;
//					}
//					// Already requested for Name Change only
//					// Can't Requested for (Name and Phone No Change) Or Name Change
//					else if (user_Requests.getUPDATE_REQ_TYPE().equals("10")
//							&& ((userReqData.getString("updateType").equals("11")
//									|| userReqData.getString("updateType").equals("10")))) {
//						// System.out.println("NameRequestFirst");
//						jsresp.put("status", "FAILURE");
//						jsresp.put("statusDetails",
//								"You have already updated/requested for name change. Cannot update/request once again");
//						return jsresp;
//					}
//					// Already Requested (Name and Phone No Change) Or Name Change
//					// Cant request for phone change again
////					else if (user_Requests.getUPDATE_REQ_TYPE().equals("11")
////							&& (userReqData.getString("updateType").equals("01"))) {
////						jsresp.put("status", "FAILURE");
////						jsresp.put("statusDetails",
////								"You have already updated/requested for name and phone change. Cannot update/request once again");
////						return jsresp;
////
////					}
//					// Updated Phone No
//					// allows to request for Name change only message
////					else if ((user_Requests.getUPDATE_REQ_TYPE().equals("01"))
////							&& (userReqData.getString("updateType").equals("11"))) {
////						jsresp.put("status", "FAILURE");
////						jsresp.put("statusDetails", "You have already updated phone number. Cannot request once again");
////						return jsresp;
////					}
//					// Requested for Name Change
//					// allows to request for phone change only
////					else if ((user_Requests.getUPDATE_REQ_TYPE().equals("10"))
////							&& (userReqData.getString("updateType").equals("01"))) {
////						jsresp.put("status", "SUCCESS");
////						// return jsresp;
////					}
//					// Requested for Phone Change
//					// allows to request for Name change or(Name and Phone No Change)
////					else if ((user_Requests.getUPDATE_REQ_TYPE().equals("01"))
////							&& (userReqData.getString("updateType").equals("10"))) {
////						jsresp.put("status", "SUCCESS");
////						// return jsresp;
////					}
//
//				}
			} else {
				jsresp.put("status", "SUCCESS");
				return jsresp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsresp;
	}

	// Otp will be validated and Phone No. will be updated
	@Override
	public JSONObject updateProfileOnOtp(JSONObject inputs) {

		JSONObject out = new JSONObject();
		boolean isUpdated = false;
		try {

			Employee_Master empData = PDAOS.getEmployeeDetails(inputs.getString("empId"), inputs.getString("empGroup"));
			User_Requests userData = PDAOS.getUserRequestDetails(inputs.getString("empId"),
					inputs.getString("updateType"));
			boolean otpResp = OtpHandler.validateOtp(inputs.getString("otp"), inputs.getString("empId"));
			if (otpResp == true) {
				OtpHandler.removeOtp(inputs.getString("empId"));

				empData.setEMP_PHONE(inputs.getString("mobAadhar"));
				isUpdated = PDAOS.updateUserDetails(empData);
				if (inputs.getString("updateType").equals("01")) {
					userData.setVERIFIED_STATUS("01");
					PDAOS.updateUserRequests(userData);
					if (isUpdated == true) {
						out.put("status", "SUCCESS");
						out.put("statusDetails", "User phone details updated");
					}
				}
				// else {
//					out.put("status", "FAILURE");
//					out.put("statusDetails", "Otp validation Failed");
//				}

				if (inputs.getString("updateType").equals("11")) {
					if (isUpdated == true) {
						userData.setVERIFIED_STATUS("01");
						PDAOS.updateUserRequests(userData);
						out.put("status", "SUCCESS");
						out.put("statusDetails",
								"User phone details updated. Name will be updated after verification.");
					}
					// else {
//						out.put("status", "FAILURE");
//						out.put("statusDetails", "Otp validation failed");
//					}
				}
			} else {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Otp validation failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	// Generating OTP for verifying Phone No.
	public String generateOnlyOtp(String empId) {
		String otp = null;
		if (OtpHandler.otpStore.has(empId)) {
			try {
				otp = OtpHandler.otpStore.getString(empId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Random rr = new Random(System.currentTimeMillis());
			otp = 100000 + rr.nextInt(900000) + "";
			OtpHandler.addOtp(otp, empId);
		}
		return otp;
	}

	// Masking the BC names
	private String getNameMasked(String empName) {

		String UserFullName = empName;
		String maskedWord = "";
		String maskedName = "";

		String[] userNameArr = UserFullName.split(" ");

		for (int i = 0; i < userNameArr.length; i++) {
			if (userNameArr[i].length() > 1) {
				maskedWord = maskingName(userNameArr[i]);
			} else {
				maskedWord = userNameArr[i];
			}

			maskedName = maskedName + maskedWord + " ";
		}
		return maskedName;
	}

	// The List of BCs who has requested for name updation
	@Override
	public List<Object> bcRequestsforUpdatingNames() {
		String onlyNameChange = "10";
		String onlyNameandPhoneChange = "11";
		String empCompany = "i25BCA";
		List<Object> userList = PDAOS.userNameUpdationList(onlyNameChange, onlyNameandPhoneChange, empCompany);
		// List<ApproveNameList> nameList = new ArrayList<ApproveNameList>();
		return userList;
	}

	// regex Expression for masking
	public String maskingName(String empName) {
		final Pattern hidePattern = Pattern.compile("(.{1,1})(.*)(.{1,2})");
		Matcher m = hidePattern.matcher(empName);
		String output = null;
		int l = 0;
		if (m.find()) {
			l = m.group(2).length();
			output = m.group(1) + new String(new char[l]).replace("\0", "*") + m.group(3);
		}

		return output;
	}

	// updating the BC names Approved by BC cell in the master table and setting
	// status,login details in user request table
	public JSONObject bcNamesapprovedList(JSONObject inputs) {
		JSONObject out = new JSONObject();
		Boolean isUpdated = false;
		try {

			JSONArray approvalArray = inputs.getJSONArray("approvalList");
			for (int i = 0; i < approvalArray.length(); i++) {
				JSONObject approveSingleUser = approvalArray.getJSONObject(i);

				Employee_Master empData = PDAOS.getEmployeeDetails(approveSingleUser.getString("empid"),
						approveSingleUser.getString("group"));

				empData.setEMP_NAME(approveSingleUser.getString("name"));

				isUpdated = PDAOS.updateUserDetails(empData);
				User_Requests userData = PDAOS.getUserRequestDetails(approveSingleUser.getString("empid"),
						approveSingleUser.getString("updateType"));
				if (approveSingleUser.getString("updateType").equals("10")
						|| approveSingleUser.getString("updateType").equals("11")) {
					userData.setVERIFIED_STATUS(approveSingleUser.getString("updateType"));
				}
				// userData.getUPDATE_REQ_TYPE()
				userData.setVERIFIED_BY(inputs.getString("username"));
				userData.setIP_ADDRESS(inputs.getString("ipAdd"));

				userData.setVERIFIED_TIME(new Timestamp(System.currentTimeMillis()));
				PDAOS.updateUserRequests(userData);
				String Msg = "BC name updated successfully";
				out.put("status", "SUCCESS");
				out.put("statusDetails", Msg);

			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Name update failed");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return out;
	}

	// rejecting the BC names rejected by BC cell in user request table
	@Override
	public JSONObject bcNamesRejectionList(JSONObject inputs) {
		JSONObject out = new JSONObject();
		Boolean isUpdated = false;
		try {

			JSONArray rejectionArray = inputs.getJSONArray("rejectionList");
			for (int i = 0; i < rejectionArray.length(); i++) {
				JSONObject rejectionSingleUser = rejectionArray.getJSONObject(i);
				User_Requests userData = PDAOS.getUserRequestDetails(rejectionSingleUser.getString("empid"),
						rejectionSingleUser.getString("updateType"));
				if (rejectionSingleUser.getString("updateType").equals("10")
						|| rejectionSingleUser.getString("updateType").equals("11")) {
					userData.setVERIFIED_STATUS("21");

				}
				userData.setVERIFIED_BY(inputs.getString("username"));
				userData.setIP_ADDRESS(inputs.getString("ipAdd"));

				userData.setVERIFIED_TIME(new Timestamp(System.currentTimeMillis()));
				PDAOS.updateUserRequests(userData);
				String Msg = "BC name change request rejected.";
				out.put("status", "SUCCESS");
				out.put("statusDetails", Msg);

			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				out.put("status", "FAILURE");
				out.put("statusDetails", "Name rejection failed");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return out;

	}

	// to get the local lang lists in which the agreement is avaiable
	@Override
	public JSONObject getLangList(JSONObject inputs) {
		JSONObject response = new JSONObject();
		JSONArray resp = new JSONArray();
		try {

			List<Action_Master_Lang> lanlist = PDAOS.getlangInfo(inputs.getString("docCode"));
			List<String> langName = new ArrayList<String>();
			langName.add("Show agreement in my language");
			for (Action_Master_Lang si : lanlist) {
				langName.add(si.getLANG_NAME());
			}
			// System.out.println("col:"+langName);
			if (!lanlist.isEmpty()) {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "List of Languages.");
				response.put("lanList", langName);
			} else {
				response.put("status", "FALIURE");
				response.put("statusDetails", "No language list available on ur account");
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// to get the local language pdf file from the location in which it is present
	@Override
	public JSONObject getLangFilePath(JSONObject inputs) {
		JSONObject filepathRes = new JSONObject();
		// System.out.println("getLangFilePath");
		try {
			// to get the filename from the table according to the specific language
			// selected.
			if (inputs.getString("document").equalsIgnoreCase("Show agreement in my language")) {
				filepathRes.put("status", "SUCCESS");
				filepathRes.put("fileName", "ENGLISH");
			} else {
				List<Action_Master_Lang> selectedlangrecord = PDAOS.getlangFilePath(inputs.getString("document"),
						inputs.getString("docCode"));
				String fileName = selectedlangrecord.get(0).getFILE_NAME();
				String filePath = Properties_Loader.getHTMLPDF_FILE_PATH() + "/" + fileName;
				filepathRes.put("status", "SUCCESS");
				filepathRes.put("langFilePath", filePath);
				filepathRes.put("fileName", fileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				filepathRes.put("status", "FAILURE");
				filepathRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return filepathRes;
	}

	// to merge the generated pdf with local language pdf.
	private JSONObject mergeingwithLocalLang(JSONObject inputs) {
		JSONObject filepathRes = new JSONObject();
		// System.out.println("mergeingwithLocalLang");
		try {
			// to get the filename from the table according to the specific language
			// selected.
			List<Action_Master_Lang> selectedlangrecord = PDAOS.getlangFilePath(inputs.getString("langselected"),
					inputs.getString("docCode"));
			String fileName = selectedlangrecord.get(0).getFILE_NAME();
			String filePath = Properties_Loader.getHTMLPDF_FILE_PATH() + "/" + fileName;
			String FILE = Properties_Loader.UNSIGNED_FILES_DIRECTORY;
			// System.out.println("generatedpdf:"+inputs.getString("generatedpdfpath"));
			File file1 = new File(inputs.getString("generatedpdfpath"));
			File file2 = new File(filePath);

			// Instantiating PDFMergerUtility class
			// MemoryUsageSetting PDFmerger= new MemoryUsageSetting();
			PDFMergerUtility PDFmerger = new PDFMergerUtility();

			// Setting the destination file
			String mergedfilePath = FILE + File.separator + inputs.getString("docCode") + inputs.getString("refno")
					+ ".pdf";

			// PDFmerger.setDestinationFileName("C:\\Examples\\merged3.pdf");
			PDFmerger.setDestinationFileName(mergedfilePath);
			mergedFilePath = mergedfilePath;
			// PDAOS.updateActionMasterLang(lanlist);
			// adding the source files
			PDFmerger.addSource(file1);
			PDFmerger.addSource(file2);

			// Merging the two documents
			PDFmerger.mergeDocuments();

			filepathRes.put("status", "SUCCESS");
			filepathRes.put("statusDetails", "PDF Merging success");
			filepathRes.put("PDFPath", mergedfilePath);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				filepathRes.put("status", "FAILURE");
				filepathRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return filepathRes;

	}

	// The signing status List of BCs statewise
	@Override
	public JSONObject bcstatewisesigningstatus() {
		JSONObject signingstatusList = null;
		String FILE = Properties_Loader.UNSIGNED_FILES_DIRECTORY;
		String reportpath = FILE + File.separator + "bcsigningstatusreport.csv";
		JSONObject response = new JSONObject();
		try {
			signingstatusList = PDAOS.bcstatewisesigningstatus();
			JSONArray resp = new JSONArray(signingstatusList.getString("list"));
			File file = new File(reportpath);
			String csv = CDL.toString(resp);
			FileUtils.writeStringToFile(file, csv);
			if (resp.length() > 0) {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "Singing status report found");
				response.put("filepath", reportpath);
			} else {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "Singing status report found");
				response.put("filepath", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return response;

	}

	// The signing rejection list of bcs statewise
	@Override
	public JSONObject bcsignrejectiondetails() {
		JSONObject signrejectionList = null;
		JSONObject response = new JSONObject();
		String FILE = Properties_Loader.UNSIGNED_FILES_DIRECTORY;
		String reportpath = FILE + File.separator + "bcnamerejectionreport.csv";
		try {
			signrejectionList = PDAOS.bcsignrejectiondetails();
			JSONArray resp = new JSONArray(signrejectionList.getString("list"));
			File file = new File(reportpath);
			String csv = CDL.toString(resp);
			FileUtils.writeStringToFile(file, csv);
			if (resp.length() > 0) {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "Signrejection Report Found");
				response.put("filepath", reportpath);
			} else {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "Signrejection Report Not Found");
				response.put("filepath", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public String generateOTPforWitnessEsign(String witnessInfo, String ip) {
		String response = null;
		String signMode = "4";
		String signType = "OTP Based";
		try {
			JSONObject emp = new JSONObject(witnessInfo);
			String empCompany = emp.getString("empCompany");
			String empid = emp.getString("empid");
			String empGroup = emp.getString("empGroup");
			String docCode = emp.getString("docCode");
			String userIP = emp.getString("userIP");
			int signOrder = Integer.parseInt(emp.getString("signOrder"));
			Employee_Master employeeDetails = PDAOS.getEmployeeDetails(empid, empGroup);
			Sign_Doc_Details signDocInfo = PDAOS.checkSignDocDetails(empid, empCompany, empGroup, docCode);
			JSONObject signerInfo = emp.getJSONObject("signerData");
			if (!(signOrder >= 2)) {
				JSONObject validateLoginResp = MySignClient.validateLogin(userIP);
				// callBackURL-true ,so that jSign will call to bcportal callbackurl after
				// signing
				// Bcaadhar sign - callbaclURL-true
				// filePath,jSignLoginResp(JsonObject),imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
				JSONObject out = new JSONObject(MySignClient.reqOTPforSigning(signDocInfo.getDOC_PATH(),
						validateLoginResp, employeeDetails.getCompositeKey().getEMP_COMPANY(), docCode, signerInfo,
						emp.getString("userIP"), false, signMode));
				response = out.toString();
			} else {
				JSONObject out = new JSONObject();
				out.put("statuscode", "01");
				out.put("statusmsg", "Document Already Signed !!");
				response = out.toString();
			}
		} catch (JSONException e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		} catch (Exception e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}

		return response;

	}

	@Override
	public String resendOTPforWitnessEsign(String witnessInfo, String ip) {
		String response = null;
		String signMode = "4";
		String signType = "OTP Based";
		try {
			JSONObject emp = new JSONObject(witnessInfo);
			String empCompany = emp.getString("empCompany");
			String empid = emp.getString("empid");
			String empGroup = emp.getString("empGroup");
			String docCode = emp.getString("docCode");
			String userIP = emp.getString("userIP");
			String docID = emp.getString("docID");
			// System.out.println("docID:"+docID);
			int signOrder = Integer.parseInt(emp.getString("signOrder"));
			Employee_Master employeeDetails = PDAOS.getEmployeeDetails(empid, empGroup);
			Sign_Doc_Details signDocInfo = PDAOS.checkSignDocDetails(empid, empCompany, empGroup, docCode);
			JSONObject signerInfo = emp.getJSONObject("signerData");
			if (!(signOrder >= 2)) {
				JSONObject validateLoginResp = MySignClient.validateLogin(userIP);
				// callBackURL-true ,so that jSign will call to bcportal callbackurl after
				// signing
				// Bcaadhar sign - callbaclURL-true
				// filePath,jSignLoginResp(JsonObject),imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
				JSONObject out = new JSONObject(MySignClient.resendOTPforSigning(signDocInfo.getDOC_PATH(),
						validateLoginResp, employeeDetails.getCompositeKey().getEMP_COMPANY(), docCode, signerInfo,
						emp.getString("userIP"), false, signMode, docID));
				response = out.toString();
			} else {
				JSONObject out = new JSONObject();
				out.put("statuscode", "01");
				out.put("statusmsg", "Document Already Signed !!");
				response = out.toString();
			}
		} catch (JSONException e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		} catch (Exception e) {
			JSONObject out = new JSONObject();
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Exception :" + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			response = out.toString();
			e.printStackTrace();
		}

		return response;

	}

	// to update the BC Cell Ip by taking the input
	@Override
	public String updateBcCellIP(JSONObject input) {
		JSONObject js = new JSONObject();
		try {

			// System.out.println();
			// System.out.println(" Inside registerationforDSCSelfToken");
			Dsc_Token_Config dsctokenconfigresp = PDAOS.getDSCTokenConfig("INTBCAA");
			if (dsctokenconfigresp != null) {
				// System.out.println("IP:" +" "+ip + input.getString("ipAddress"));
				dsctokenconfigresp.setEXTERNAL_IP("http://" + input.getString("ipAddress").trim());
				boolean updateBcCellIpResp = PDAOS.updateBcCellIp(dsctokenconfigresp);
				if (updateBcCellIpResp) {
					js.put("status", "SUCCESS");
					js.put("statusDetails", "Ip Updated sucessfully.");
				} else {
					js.put("status", "SUCCESS");
					js.put("statusDetails", "Updation  failed. Try again.");
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Registration failed. Try again.");
				return js.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return js.toString();
	}

	// to get the present IP of the BCCellToken
	@Override
	public String getBcCellIP() {
		JSONObject js = new JSONObject();
		try {

			// System.out.println(" Inside getBcCellIP");
			Dsc_Token_Config dsctokenconfigresp = PDAOS.getDSCTokenConfig("INTBCAA");
			if (dsctokenconfigresp != null) {
				String[] onlyip = dsctokenconfigresp.getEXTERNAL_IP().split("//");
				js.put("existingIP", onlyip[1]);
				js.put("status", "SUCCESS");
				js.put("statusDetails", "IP Found");
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "No Ip found. Try again.");
				return js.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return js.toString();
	}

	// to generate refno to pass for submitConstent from the server side
	public String getRefnum(String empid) {
		String referenceno = "";
		try {
			referenceno = "INT" + empid + System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return referenceno;
	}

// get empl list from emp_master table
	@Override
	public JSONObject getEmplList(JSONObject input) {
		// TODO Auto-generated method stub

		JSONObject response = new JSONObject();
		try {
			JSONObject res = PDAOS.getEmplList(input);
			JSONArray resp = new JSONArray(res.getString("list"));
//			System.out.println("resp=>" + resp);
			if (resp.length() > 0) {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "List of Documents");
				response.put("list", resp);
			} else {
				response.put("status", "SUCCESS");
				response.put("statusDetails", "No Documents on your account");
				response.put("list", resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;

	}

// update user status 
	@Override
	public JSONObject updateUserStatus(JSONObject input, JSONObject userDetails) {
		// TODO Auto-generated method stub

		JSONObject response = new JSONObject();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String optnType = "";
		try {
			response = PDAOS.updateUserStatus(input);
			if (Integer.parseInt(input.getString("empStatus")) == 1) {
				optnType = "Emp enabled";
			} else {
				optnType = "Emp disabled";
			}
			JSONObject additionalInfo = new JSONObject();
			additionalInfo.put("EmpID", input.getString("empID"));
			Audit_Info userLogInfo = new Audit_Info();
			userLogInfo.setEMP_ID("");
			userLogInfo.setUSER(userDetails.getString("username"));
			userLogInfo.setOPERATION_TIME(timestamp);
			userLogInfo.setOPERATION_TYPE(optnType);
			userLogInfo.setOPERATIONREMARKS(response.getString("status"));
			userLogInfo.setIPADDRESS(input.getString("userIp"));
			userLogInfo.setADDITIONAL_INFO(additionalInfo.toString());
			boolean isSuccess = PDAOS.updateLogdetails(userLogInfo);
//			System.out.println("isSuccess"+isSuccess);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;

	}

	// bulk data upload user validation
	@Override
	public boolean validateUserRolePermission(String userName) {
		// TODO Auto-generated method stub

		JSONObject isValidUser = PDAOS.getLoginUserDetails(userName);
		try {
			if (isValidUser.getInt("roleId") == 1 || isValidUser.getInt("roleId") == 2
					|| isValidUser.getInt("roleId") == 3) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}

	}

	// bulk data upload
	@Override
	public JSONObject readBulkInputs(MultipartFile file, String inputDetails) {

		// TODO Auto-generated method stub
		// dupliacte check response
		JSONObject duplicateCheck = new JSONObject();
		// additional data response
		JSONObject additionalData = new JSONObject();
		// input for bulk data upload
		JSONObject inputsData = new JSONObject();
		// Employee master entity for data upload
		Employee_Master empMasterData = new Employee_Master();
		// Employee master composite key entity for data upload
		Emp_Master_CompositeKey empCompositeKeyData = new Emp_Master_CompositeKey();
		// bulk data upload logs entity
		Bulk_Data_Upload_Logs res = new Bulk_Data_Upload_Logs();
		// summary data response
		JSONObject summaryJs = new JSONObject();
		// all record response array in all stages
		JSONArray finalResJs = new JSONArray();

		// data inserted or not, default not inserted
		boolean isDataUploadedSuccess = false;

		String tempId = "1";
		int successCount = 0;
		// failure count
		int fcount = 0;
		// total no of records
		int count = 0;

		boolean isPrescanned = false;
		JSONObject responseData = new JSONObject();
		JSONObject jsres = new JSONObject();
		JSONObject uploadDataRes = new JSONObject();
		boolean isDupliacteFound = false;
		BufferedReader br;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// output file response
		JSONObject response = new JSONObject();
		String FILE = Properties_Loader.UNSIGNED_FILES_DIRECTORY;
		String fileName = new Date().getTime() + "_" + "BULKDATAUPLOADSTATUS.csv";
		String templatePath = FILE + File.separator + "" + fileName;

		// batch no generation for bulk registration.
		String batch_No = "" + new Date().getTime();

		try {
			JSONObject input = new JSONObject(inputDetails);
			String userName = input.getString("userName");
			InputStream inputStream = file.getInputStream();
			File file1 = new File(file.getOriginalFilename());
			file1.getAbsoluteFile();

			br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			// csv header from uploaded file
			String sr = br.readLine();
			if (userName.equalsIgnoreCase("cbmahesh")) {
				tempId = "2";
			}

			// CSV header from db based on template id
			String HeaderData = InMemory.gettemplateData().get("templates").toString();
			JSONObject jsData = new JSONObject(HeaderData);
			String Header = jsData.getString(tempId);
			sr = sr.concat(",");
			sr = sr.replace("\"", "");
			if (Header.equalsIgnoreCase(sr)) {

				CsvParserSettings setting = new CsvParserSettings();
				setting.setHeaderExtractionEnabled(true);
				CsvParser parser = new CsvParser(setting);
				List<Record> parseAllInfo = parser.parseAllRecords(inputStream);
				// no.of records present in file
				count = parseAllInfo.size();
//				System.out.println(count);
				if (count > 0) {
					// prescanning the csv file
					JSONObject preScanning = bulkDataUploadPrescanning(parseAllInfo, Header);
//					System.out.println("data :-------" + preScanning);

					if (preScanning.getString("status").equalsIgnoreCase("SUCCESS")) {
						isPrescanned = true;
						// timeStamp for data upload and bulk data upload logs
						Timestamp timeStamps = new Timestamp(System.currentTimeMillis());

						// bulk data upload

						for (Record record : parseAllInfo) {

							// ------------------- duplication check with master table based on groupcode
							// and EMP_ID-------------------
							duplicateCheck = duplicateCheck(record, input.getString("groupCode"), Header);

							if (duplicateCheck.getString("status").equalsIgnoreCase("SUCCESS")) {

								// data preparation for Emp_master and bulk data upload log table
								String EMP_ID = null;
								String EMP_COMPANY = null;
								String EMP_EMAIL = "";
								String EMP_NAME = null;
								String EMP_PHONE = null;
								String EMP_GROUP = "";
								String ADDITIONAL_DATA = "";
								int STATUS = 1;
								int SIGN_STATUS = 1;
								Timestamp CREATED_ON = timeStamps;
								// based on template preparing additional data
								if (tempId == "2") {
									// calling getAdditional data method
									additionalData = getAdditionalData(record);

								} else {
									// for EMPloyees only company name is additional data
									additionalData.put("companyFullName:", input.getString("companyName"));
								}

								ADDITIONAL_DATA = additionalData.toString().replace("\"", "\"");
								EMP_ID = record.getString("MERCHANT ID");
								java.sql.Date EMP_DOB = java.sql.Date.valueOf(record.getString("DOB (YYYY-MM-DD)"));
								EMP_NAME = record.getString("BC/BANK MITR");
								EMP_PHONE = record.getString("BC CONTACT NO.");
								EMP_GROUP = input.getString("groupCode");
								EMP_COMPANY = input.getString("company");

								// compositkey data
								empCompositeKeyData.setEMP_ID(EMP_ID);
								empCompositeKeyData.setEMP_GROUP(EMP_GROUP);
								empCompositeKeyData.setEMP_COMPANY(EMP_COMPANY);

								// emp master data
								empMasterData.setCompositeKey(empCompositeKeyData);
								empMasterData.setEMP_DOB(EMP_DOB);
								empMasterData.setEMP_EMAIL(EMP_EMAIL);
								empMasterData.setEMP_NAME(EMP_NAME);
								empMasterData.setEMP_PHONE(EMP_PHONE);
								empMasterData.setSIGN_STATUS(SIGN_STATUS);
								empMasterData.setSTATUS(STATUS);
								empMasterData.setADDITIONAL_DATA(ADDITIONAL_DATA);
								empMasterData.setCREATED_ON(CREATED_ON);

								// bulk data upload data
								res.setBATCH_NO(batch_No);
								res.setEMP_DOB(EMP_DOB);
								res.setEMP_GROUP(EMP_GROUP);
								res.setEMP_ID(EMP_ID);
								res.setEMP_NAME(EMP_NAME);
								res.setEMP_PHONE(EMP_PHONE);
								res.setREG_BY(userName);
								res.setREG_ON(CREATED_ON);
								res.setSTATUS(STATUS);

								// input for bulk data upload
								inputsData.put("empData", empMasterData);
								inputsData.put("batchNo", batch_No);
								inputsData.put("status", STATUS);
								inputsData.put("regBy", userName);
//								System.out.println(inputsData);

								// save data to employee master table
								uploadDataRes = PDAOS.SaveEmployeeData(inputsData);
								// adding data save failure reason after data insertion
								res.setFAILURE_REASON(uploadDataRes.getString("failureReason"));

								// bulk data upload logs call
								PDAOS.bulkDataUploadLogs(res);

								// successfully data uploaded
								if (uploadDataRes.getString("status").equalsIgnoreCase("SUCCESS")) {
									isDataUploadedSuccess = true;
									JSONObject Successjsres = new JSONObject();
									Successjsres.put("status", "SUCCESS");
									Successjsres.put("isPrescanned", isPrescanned);
									Successjsres.put("userInfo", record);
									Successjsres.put("statusDetails", "data uploaded successfully");
									finalResJs.put(Successjsres);
								} else {
									JSONObject Failurejsres = new JSONObject();

									Failurejsres.put("status", "FAILURE");
									Failurejsres.put("isPrescanned", isPrescanned);
									Failurejsres.put("userInfo", record);
									Failurejsres.put("statusDetails", "data upload failed,try after sometime");

									finalResJs.put(Failurejsres);
								}
								successCount = successCount + 1;
							} else {
								fcount = fcount + 1;
								JSONObject fjsRes = new JSONObject();
								isDupliacteFound = true;
								fjsRes.put("status", "FAILURE");
								fjsRes.put("isPrescanned", isPrescanned);
								fjsRes.put("userInfo", record);
								fjsRes.put("statusDetails", "Validation Failed, duplicate records found");

								finalResJs.put(fjsRes);

							}

						}
						// summary data for overall response
						summaryJs.put("currentTimestamp", timeStamps);
						summaryJs.put("fileName", file.getOriginalFilename());
						summaryJs.put("failureCount", fcount);
						summaryJs.put("successCount", successCount);
						responseData.put("summary", summaryJs);
						responseData.put("userInfo", finalResJs);

						JSONObject fileOutput = new JSONObject();
						JSONObject fileInputs = new JSONObject();
						fileInputs.put("header", Header);
						fileInputs.put("content", finalResJs);
						fileInputs.put("batchNo", batch_No);
						fileInputs.put("uploadedOn", timeStamps);
						fileOutput = getOutputCSVFile(fileInputs, templatePath);
						responseData.put("filePath", fileName);

						// some data dupliacte found
						if ((isDupliacteFound && isDataUploadedSuccess)
								|| (isDupliacteFound && !isDataUploadedSuccess)) {
							responseData.put("status", "FAILURE");
							responseData.put("statusDetails", "duplicate data found");
						} else {
							// All data uploaded successfully
							responseData.put("status", "SUCCESS");
							responseData.put("statusDetails", "Data Uploaded Successfully");
						}

					} else if (preScanning.getString("status").equalsIgnoreCase("FAILURE")) {
						JSONObject fileOutput = new JSONObject();
						JSONObject fileInputs = new JSONObject();
						fileInputs.put("header", Header);
						fileInputs.put("content", preScanning.get("userInfo"));
						fileInputs.put("prescanning", "failed");
						fileOutput = getOutputCSVFile(fileInputs, templatePath);
						responseData.put("filePath", fileName);

						summaryJs.put("currentTimestamp", preScanning.get("timeStamps"));
						summaryJs.put("fileName", file.getOriginalFilename());
						summaryJs.put("failureCount", preScanning.get("fcount"));
						summaryJs.put("successCount", preScanning.get("successCount"));
						responseData.put("summary", summaryJs);
						responseData.put("status", preScanning.getString("status"));
						responseData.put("userInfo", preScanning.get("userInfo"));
						responseData.put("statusDetails", preScanning.getString("statusDetails"));

					} else {
						responseData.put("status", "FAILURE");
						responseData.put("statusDetails", "Validation Failed, kindly try after sometime.");
					}
				} else {
					responseData.put("status", "FAILURE");
					responseData.put("statusDetails", "No data in the file, kindly update data and upload agin.");
					responseData.put("invalidColumn", sr);
					responseData.put("header", Header);

				}
				// prescanning input file

			} else {
				responseData.put("status", "FAILURE");
				responseData.put("statusDetails",
						"Invalid Column Headers, Kindly download the template and then retry.");
				responseData.put("invalidColumn", sr);
				responseData.put("header", Header);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseData;
	}

	private JSONObject getAdditionalData(Record record) {
		JSONObject additionalData = new JSONObject();
		try {
			additionalData.put("ward", record.getString("VILLAGE NAME"));
			additionalData.put("taluk", record.getString("TALUK/BLOCK/MANDAL"));
			additionalData.put("district", record.getString("DISTRICT"));
			additionalData.put("state", record.getString("STATE"));
			additionalData.put("pinCode", record.getString("AADHAAR PINCODE"));
			additionalData.put("effectiveDate", record.getString("BC ENGAGEMENT DATE(DD/MM/YYYY)"));
			additionalData.put("mobileNumber", record.getString("BC CONTACT NO."));
			additionalData.put("pan", record.getString("PAN CARD NO"));
			additionalData.put("bcId", record.getString("MERCHANT ID"));
			additionalData.put("bank", record.getString("BANK"));
			additionalData.put("bankFullName", record.getString("BANK FULL NAME"));
			additionalData.put("branch", record.getString("BRANCH NAME"));
			additionalData.put("bcLocation", record.getString("BCA LOCATION"));
			additionalData.put("IFSC1", record.getString("IFSC1"));
			additionalData.put("IFSC2", record.getString("IFSC2"));
			additionalData.put("accountNo1", record.getString("SB AC NO"));

			additionalData.put("accountNo2", record.getString("SETTLEMENT AC NO"));
			additionalData.put("age", record.getString("AGE"));
			additionalData.put("relationship", record.getString("NAME OF FATHER/HUSBAND / WO"));
			additionalData.put("address", record.getString("RESIDENTIAL ADDRESS"));
			additionalData.put("act", record.getString("ACT"));

			additionalData.put("bankAddress", record.getString("BANK ADDRESS"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return additionalData;
	}

	private JSONObject duplicateCheck(Record record, String groupCode, String Header) {
		JSONObject jsRes = new JSONObject();
		JSONObject dupliacteData = new JSONObject();

		String ID = null;
		boolean isDuplicateRecord = false;
		try {
			ID = record.getString("MERCHANT ID");
			if (ID != null && !ID.isEmpty()) {
				JSONObject empInfo = new JSONObject();
				empInfo.put("empid", ID);
				empInfo.put("empGroup", groupCode);
				jsRes = PADL.validateEmployeeDetails(empInfo.toString());
//				System.out.println(jsRes);
				// to check whether the input file has duplicate records

				if (jsRes.getString("status").equalsIgnoreCase("SUCCESS")) {
					isDuplicateRecord = true;

				}

			}

			if (isDuplicateRecord) {
				dupliacteData.put("status", "FAILURE");
				dupliacteData.put("statusDetails", "Dupliacte Record Found");
				dupliacteData.put("userInfo", jsRes);

			} else {
				dupliacteData.put("status", "SUCCESS");
				dupliacteData.put("statusDetails", "New record");
				dupliacteData.put("userInfo", jsRes);

			}

		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

		// TODO Auto-generated method stub
		return dupliacteData;
	}

//output of bulk data upload 
	public JSONObject getOutputCSVFile(JSONObject object, String Path) {
		JSONObject response = new JSONObject();
		JSONObject inputs = new JSONObject();
		boolean isBatchNoReq = false;
		String batchNo = "";
		String createdOn = "";

		try {
			inputs = object;
			String Header = inputs.getString("header");

			Header = Header + "REMARKS";
			if (!inputs.has("prescanning")) {
				isBatchNoReq = true;
			}
			if (isBatchNoReq) {
				Header = Header + "," + "BATCH NUMBER" + "," + "UPLOADED ON";
				batchNo = inputs.getString("batchNo");
				createdOn = inputs.getString("uploadedOn");

			}
			String[] strArray = Header.split(",");

			List<String[]> headerData = new ArrayList<String[]>();
			headerData.add(strArray);

			File file = new File(Path);
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			writer.writeAll(headerData);

			JSONArray dataContent = new JSONArray();
			dataContent = (JSONArray) inputs.get("content");
//			System.out.println(dataContent);
			List<String[]> content = new ArrayList<String[]>();

			for (int i = 0; i < dataContent.length(); i++) {

				JSONObject record = dataContent.getJSONObject(i);

				Record filedsData = (Record) record.get("userInfo");
//				System.out.println(filedsData);
				String[] dataArray = null;
				String[] stHeaders = inputs.getString("header").split(",");
				JSONObject inputJs = new JSONObject();

				for (String st : stHeaders) {
					String Filedsdata = "";
					Filedsdata = filedsData.getString(st);
					inputJs.put(st, Filedsdata + ",");
				}
				inputJs.put("REMARKS", record.getString("statusDetails") + ",");
				if (isBatchNoReq) {
					inputJs.put("UPLOADED ON", createdOn + ",");
					inputJs.put("BATCH NUMBER", batchNo + ",");
				}

//				System.out.println(inputJs);
//				int arraylen = dataArray.length;
//				 dataArray[arraylen]=(String) record.get("statusDetails");
				String stsSt = "";
				for (String str : strArray) {
					stsSt = stsSt + inputJs.getString(str) + "@";

				}
//				System.out.println(stsSt);

				String[] dataArrays = stsSt.split(",@");
				content.add(dataArrays);

				writer.writeAll(content);
				content.clear();
			}

			writer.close();
			response.put("filePath", file);
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

		return response;
	}

	// prescanning of csv file
	private JSONObject bulkDataUploadPrescanning(List<Record> parseAllInfo, String header) {
		// return resposne json from bulkDataUploadPrescanning.
		JSONObject resData = new JSONObject();
		// collect the all record prescanning status
		JSONArray finalResJs = new JSONArray();
		// collect individual record info
		JSONObject jsres = new JSONObject();

		JSONObject finalResponse = new JSONObject();

		// collect all records status
		JSONObject ValidatedRes = new JSONObject();
		// check any of the feild is empty or null
		int preScanCount = 0;

		// check null
		boolean isNullCheck = false;

		// record failure count
		int fcount = 0;

		// record success sount
		int successCount = 0;
		boolean isValidated = false;
		boolean isPrescanningSuccess = true;
		int tcount = parseAllInfo.size();
		String failMgs = "";

		try {
			// for summary data timeStamp
			Timestamp timeStamps = new Timestamp(System.currentTimeMillis());

			// header string to array of string
			String[] headers = header.split(",");
			char quotes = '"';
			// taking fields data from DB based on field name dynamically.
			JSONObject validations = (JSONObject) InMemory.gettemplateData().get("fields");
			// check all record
			for (Record record : parseAllInfo) {
				// for selected record check all the fileds presence
				for (String field : headers) {
					boolean isDataLengthChecked = true;
					// not null and empty check
					if ((record.getString(field) == null || record.getString(field).isEmpty())) {
						// check any of the feild is empty or null.
						JSONObject nullJsres = new JSONObject();
						preScanCount = preScanCount + 1;

						nullJsres.put("status", "FAILURE");
						nullJsres.put("statusDetails", "Invalid data at column " + quotes + field + quotes);
						nullJsres.put("userInfo", record);
						finalResJs.put(nullJsres);
						// not null check
						isNullCheck = true;
						// data validated for null check and empty check
						isValidated = true;

						break;
					} else {
						isNullCheck = false;
						// data is paased null check
						JSONObject validationFailRes = new JSONObject();
						// validation based field type
						preScanCount = 0;

						// taking field record based on inpiut field
						String FVData = validations.getString(field);
						// headers json
						JSONObject js = new JSONObject(FVData);
						// taking datatype of header
						String dataType = js.getString("type");
						int reqLength = Integer.parseInt(js.getString("size"));

						// if data type is AL - alphabets and space
						if (dataType.equalsIgnoreCase("AL") && !record.getString(field).matches("[A-Za-z ]*")) {
							isPrescanningSuccess = false;
							isDataLengthChecked = false;
							preScanCount = preScanCount + 1;

							failMgs = "Special characters and numbers are not allowed at column" + quotes + field
									+ quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}
						// if data type is AN -alphabets and numbers
						if (dataType.equalsIgnoreCase("AN") && !record.getString(field).matches("[A-Za-z0-9]*")) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;
							failMgs = "Special characters are not allowed at column " + quotes + field + quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;

						}
						// if data type is N -only numbers without space
						if (dataType.equalsIgnoreCase("N") && !record.getString(field).matches("^[0-9]+")) {
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							isPrescanningSuccess = false;

							failMgs = "Alphabets and special characters are not allowed at column " + quotes + field
									+ quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}
						// if data type is MN mobile number it should be 10 digit
						if (dataType.equalsIgnoreCase("MN")
								&& !record.getString(field).matches("^(?=(?:[6-9]){1})(?=[0-9]{8}).*")) {
							isDataLengthChecked = false;
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;

							failMgs = "Invalid data at column " + quotes + field + quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}
						//

						// pan number validation
						if (dataType.equalsIgnoreCase("PN") && !record.getString(field).matches("^[A-Za-z0-9]+")
//								&& record.getString(field).length() != 10
						) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							failMgs = "Alphabets and special characters are not allowed at column " + quotes + field
									+ quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}

						// DOB validation for Date format -DD/MM/YYYY
						if (dataType.equalsIgnoreCase("EDT")
								&& !record.getString(field).matches("^\\d{2}/\\d{2}/\\d{4}$")) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							failMgs = "Invalid date format at column " + quotes + field + quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}
						// datae validation for YYYY-MM-DD
						if (dataType.equalsIgnoreCase("DT") && !record.getString(field)
								.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$")) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							failMgs = "Invalid date format at column " + quotes + field + quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;

						}
						// validate AS -alphabets and space as accepted characters
						if (dataType.equalsIgnoreCase("AS") && !record.getString(field).matches("^[A-Za-z/. ]+")
//								&& 
//								record.getString(field).length() != 10
						) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							failMgs = "special characters and numbers are not allowed at column " + quotes + field
									+ quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}

						// validate AGE data type and max and min length
						if (dataType.equalsIgnoreCase("AG")) {
							if (!(Integer.parseInt(record.getString(field)) >= 18)
									|| !(Integer.parseInt(record.getString(field)) < 70)) {
								isPrescanningSuccess = false;
								preScanCount = preScanCount + 1;
								failMgs = "Invalid data for found at column " + quotes + field + quotes;
								validationFailRes.put("status", "FAILURE");
								validationFailRes.put("statusDetails", failMgs);
								validationFailRes.put("userInfo", record);
								finalResJs.put(validationFailRes);
								break;
							}
						}
						// Validate IFSC dataType and length
//						if (dataType.equalsIgnoreCase("IF")
//								&& record.getString(field).length() != 11
//						) {
//							isPrescanningSuccess = false;
//							preScanCount = preScanCount + 1;
//							isDataLengthChecked = false;
//
//							failMgs = "Invalid data for found at column" + quotes + field + quotes;
//							validationFailRes.put("status", "FAILURE");
//							validationFailRes.put("statusDetails", failMgs);
//							validationFailRes.put("userInfo", record);
//							finalResJs.put(validationFailRes);
//							break;
//						}
						// validate ANS -alphabets numbers and special characters
						if (dataType.equalsIgnoreCase("ANS")
								&& !record.getString(field).matches("^[a-zA-Z0-9 '!@#$&()\\-`.+,/\"]*$")) {
							isPrescanningSuccess = false;
							preScanCount = preScanCount + 1;
							isDataLengthChecked = false;

							failMgs = "Invalid data for found at column" + quotes + field + quotes;
							validationFailRes.put("status", "FAILURE");
							validationFailRes.put("statusDetails", failMgs);
							validationFailRes.put("userInfo", record);
							finalResJs.put(validationFailRes);
							break;
						}
						// formate of data is correct check the length of the data.
						if (isDataLengthChecked && reqLength > 0) {
							boolean lengthValid = isValidDatalength(record.getString(field).length(), reqLength);
							if (!lengthValid) {
								isPrescanningSuccess = false;
								preScanCount = preScanCount + 1;
								failMgs = "data length should be " + quotes + reqLength + quotes + " for the column "
										+ quotes + field + quotes;
								validationFailRes.put("status", "FAILURE");
								validationFailRes.put("statusDetails", failMgs);
								validationFailRes.put("userInfo", record);
								finalResJs.put(validationFailRes);
								break;
							}
						}

					}

				}

				if (preScanCount != 0) {
					fcount = fcount + 1;

				} else {
					successCount = successCount + 1;
				}
				if (!isNullCheck && preScanCount == 0) {
					JSONObject validationFailRes = new JSONObject();
					validationFailRes.put("status", "SUCCESS");
					validationFailRes.put("statusDetails", "Validation Success");
					validationFailRes.put("userInfo", record);
					finalResJs.put(validationFailRes);
				}

			}

			if (!isValidated && isPrescanningSuccess && !isNullCheck && preScanCount == 0) {
				resData.put("userInfo", finalResJs);
				resData.put("statusDetails", "All are valid data");
				resData.put("status", "SUCCESS");
				resData.put("fcount", fcount);
				resData.put("succsessCount", successCount);
				resData.put("timeStamps", timeStamps);

			} else {
				resData.put("userInfo", finalResJs);
				resData.put("statusDetails", "Few data are invalid");
				resData.put("status", "FAILURE");
				resData.put("fcount", fcount);
				resData.put("successCount", successCount);
				resData.put("timeStamps", timeStamps);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resData;
	}

// check the field length is correct or not
	private boolean isValidDatalength(int dataSize, int reqSize) {
		boolean validaLength = false;
		if (dataSize == reqSize) {
			validaLength = true;
		}

		return validaLength;

	}

	@Override
	public JSONObject getTemplateBasedonUserId(String tempID) {
		JSONObject response = new JSONObject();
		String FILE = Properties_Loader.UNSIGNED_FILES_DIRECTORY;
		String templatePath = FILE + File.separator + "BULKDATAUPLOAD.csv";
		try {

			String HeaderData = InMemory.gettemplateData().get("templates").toString();
			JSONObject jsData = new JSONObject(HeaderData);
			String Header = jsData.getString(tempID);
			String[] strArray = Header.split(",");
			List<String[]> headerData = new ArrayList<String[]>();
			headerData.add(strArray);
			File file = new File(templatePath);
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			writer.writeAll(headerData);
			writer.close();
			response.put("filePath", templatePath);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.put("status", "FAILURE");
				response.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public JSONObject checkSignDoc(String inputs, JSONObject userdetail) {
		JSONObject res = new JSONObject();
		JSONObject js = null;
		String empID = null;
		ArrayList<Sign_Doc_Details> responseFromDao = new ArrayList<Sign_Doc_Details>();
		try {
			js = new JSONObject(inputs);
			empID = js.getString("empID");
			String empGrp = js.getString("empGrp");
			responseFromDao = PADL.getSignedDocListEmpidAndEmpGrp(empID, empGrp);
			if (responseFromDao.isEmpty()) {
				res = removeBC(inputs, userdetail, false);
			} else {
				res.put("status", "success");
				res.put("statusDetails", "signing status:1");
				res.put("inputs", Certificate.encrypt(inputs.toString()));
				res.put("userdetail", Certificate.encrypt(userdetail.toString()));

			}
		} catch (Exception e) {

		}

		return res;
	}

	@Override
	public JSONObject removeBC(String inputs, JSONObject userDetails, Boolean isSigned) throws JSONException {
		JSONObject res = new JSONObject();
		JSONObject updateSignDocDetails = new JSONObject();
		JSONObject js = null;
		String empID = null;
		int numberToAppend;
		try {
			js = new JSONObject(inputs);
			empID = js.getString("empID");
			String empGrp = js.getString("empGrp");

			// to fetch the ids if it is used multiple times(deleted multiple times with
			// inclusion of numeric value)
			JSONObject jres = PDAOS.getEmployeeRepeatedID(empID, empGrp);

			if (jres.getString("status").equalsIgnoreCase("success")) {
				// get the last used number +1 to append to the id to be deleted if not
				// appending 1
				if (jres.getString("lastOccurance").contains("-")) {
					String[] temp = jres.getString("lastOccurance").split("-");
					numberToAppend = Integer.parseInt(temp[temp.length - 1]) + 1;
				}

				else {
					numberToAppend = 1;
				}

				// updating in signDoc details table
				if (isSigned == true) {
					updateSignDocDetails = PDAOS.removeBCInSignDocDetails(numberToAppend, empID, empGrp);
					if (updateSignDocDetails.getString("status").equalsIgnoreCase("failure")) {
						res.put("status", "failure");
						res.put("statusDetails", updateSignDocDetails.getString("statusDetails"));
						return res;
					}

//					res.put("status", updateSignDocDetails.getString("status"));

				}

//				JSONObject updateSignDocDetails=PDAOS.removeBCInSignDocDetails(numberToAppend,empID,empGrp);
//				if(updateSignDocDetails.getString("status").equalsIgnoreCase("success")) {

				res = PDAOS.removeBC(numberToAppend, empID, empGrp);
				if (res.getString("status").equalsIgnoreCase("success")) {

					res.put("status", res.getString("status"));
					res.put("statusDetails", "Account removed successfully");
				} else {
					res.put("status", res.getString("status"));
				}
//			}	
//				else {
//					res.put("status", updateSignDocDetails.getString("status"));
//				}

			} else {
				res.put("status", jres.getString("status"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			JSONObject additionalInfo = new JSONObject();
			additionalInfo.put("EmpID", empID);
			additionalInfo.put("Updated EmpID", res.getString("newEmpId"));
			additionalInfo.put("Reason", js.getString("reason"));
			Audit_Info userLogInfo = new Audit_Info();
			userLogInfo.setEMP_ID("");
			userLogInfo.setUSER(userDetails.getString("username"));
			userLogInfo.setOPERATION_TIME(timestamp);
			userLogInfo.setOPERATION_TYPE("Emp removed");
			userLogInfo.setOPERATIONREMARKS(res.getString("status"));
			userLogInfo.setIPADDRESS(js.getString("userIp"));
			userLogInfo.setADDITIONAL_INFO(additionalInfo.toString());
			// inserting into audit_info table
			boolean isSuccess = PDAOS.updateLogdetails(userLogInfo);
		}
		return res;
	}

	@Override
	public JSONObject getBank() throws JSONException {
		JSONObject outputToController = new JSONObject();
		List<JSONObject> listOfBanks=new ArrayList();
		
		try {
			List outputFromDb = PDAOS.getBankInfo();
			if (outputFromDb != null) {
				outputToController.put("status", "Success");
				outputToController.put("statusDetails", "operation successfull");
				for (int i = 0; i < outputFromDb.size(); i++) {
					Company_Master companyMaster = (Company_Master) outputFromDb.get(i);
					String BankName = companyMaster.getGROUP_NAME();
					String grpCode = companyMaster.getGROUP_CODE();
					String companyName = companyMaster.getCOMPANY_NAME();
					String companyCode = companyMaster.getCOMPANY_CODE();
					JSONObject banksInfo = new JSONObject();
					banksInfo.put("BankName", BankName);
					banksInfo.put("grpCode", grpCode);
					banksInfo.put("companyName", companyName);
					banksInfo.put("companyCode", companyCode);
					listOfBanks.add(banksInfo);
				}
			} else {
				outputToController.put("status", "Success");
				outputToController.put("statusDetails", "No bank record is registered");
			}
			outputToController.put("bankInfo",listOfBanks );
			return outputToController;
			
		} catch (Exception e) {
			e.printStackTrace();
			outputToController.put("status", "Failure");
			outputToController.put("statusDetails", e.getMessage());
		}
		return outputToController;
		

	}

}
