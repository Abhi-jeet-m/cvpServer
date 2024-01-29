
package com.integra.pledgeapp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.integra.pledgeapp.beans.DroolBank;
import com.integra.pledgeapp.beans.Employee_action_status;
import com.integra.pledgeapp.beans.Sign_Doc_Details;
import com.integra.pledgeapp.beans.Sign_Rejected_Info;
import com.integra.pledgeapp.core.PledgeAppDAOImpl;
import com.integra.pledgeapp.core.PledgeAppDAOServices;
import com.integra.pledgeapp.core.PledgeAppServices;
import com.integra.pledgeapp.core.PledgeAppServicesImpl;
import com.integra.pledgeapp.utilities.Certificate;
import com.integra.pledgeapp.utilities.ContextObjects;
import com.integra.pledgeapp.utilities.InMemory;
import com.integra.pledgeapp.utilities.MySignClient;
import com.integra.pledgeapp.utilities.Properties_Loader;
import com.integra.pledgeapp.utilities.TokenManager;

@RestController
@CrossOrigin(origins = "*")
public class WSController {

	public static final String BaseURL = "/CORE_VALUE_PLEDGE_APP";
	PledgeAppServices PAS = new PledgeAppServicesImpl();
	PledgeAppDAOServices PDS = new PledgeAppDAOImpl();

	@PostMapping(value = BaseURL + "/validatelogin", consumes = { "application/JSON" })
	public String validateLoginDetails(@RequestBody String userInfo, HttpServletRequest requestContext) {
		// System.out.println("In validateLoginDetails Service: ");
		String ip = requestContext.getRemoteAddr();
		System.out.println("ip:"+ip);
		String response = PAS.validateLogin(userInfo, ip);
		return response;
	}

	@PostMapping(value = BaseURL + "/validateotp", consumes = { "application/JSON" })
	public String validateOtp(@RequestBody String userInfo, HttpServletRequest requestContext) {
//		System.out.println("In validateOTP Service ");
		String ip = requestContext.getRemoteAddr();
		System.out.println("ip:"+ip);
		String response = PAS.validateotp(userInfo, ip);
		return response;
	}

	@PostMapping(value = BaseURL + "/getPledgeInfo", consumes = { "application/JSON" })
	public String getPledgeInfo(@RequestBody String empInfo) { // Used to create A unique reference number which will be
																// used by ASP
//		System.out.println("In getPledgeInfo Service ");
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(empInfo);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {

					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("userid", token.getString("userid"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					response = PAS.getPledgeInfo(empInfo);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// @RequestMapping(value = "/getPDFPath", method = RequestMethod.POST, consumes
	// = { "application/JSON" })
	// public ResponseEntity<String> getPDFPath(@RequestBody String inputs,
	// HttpServletRequest request) {
	@PostMapping(value = BaseURL + "/getPDFPath", consumes = { "application/JSON" })
	public String getPDFPath(@RequestBody String inputs) {
		System.out.println("In getPDFPath Service "+inputs);
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {

					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("userid", token.getString("userid"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);
					js = PAS.getPDFPath(input);
					js.put("username", token.getString("username"));

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
			}
		} catch (Exception e) {
			System.out.println("insie exception");
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("js.toString();::"+js.toString());
		return js.toString();
	}

	@GetMapping(value = BaseURL + "/downloadPledgeInfo/{empid}")
	public ResponseEntity<Resource> downloadPledgeInfo(@PathVariable("empid") String empid,
			HttpServletRequest requestContext) {
//		 System.out.println("In downloadPledgeInfo Service ");
		JSONObject json = null;
		Resource resource = null;
		String filepath = null;
		try {
			empid = new String(Base64.getDecoder().decode(empid));

			json = new JSONObject(empid);
			String path = json.getString("PDFPath");
			// for local
//			String pdfPath = path.replace("@", "\\");
			// for production
			String pdfPath = path.replace("@", "/");

			filepath = pdfPath;

			resource = MySignClient.getEmpBasedPledgeFile(filepath);

			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			Employee_action_status statusInfo = new Employee_action_status();
			statusInfo.setACTION_DATE(currentTimestamp);
			statusInfo.setACTION_STATUS(1);
			statusInfo.setACTION_TYPE(2);
			statusInfo.setCOMPANY_CODE(json.getString("empcompany"));
			statusInfo.setDOCUMENT_CODE(json.getString("docCode"));
			statusInfo.setEMP_CODE(json.getString("empid"));
			statusInfo.setEMP_GROUP(json.getString("empGroup"));
			PDS.insertStatusInEAS(statusInfo);

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String contentType = "application/pdf";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + "Document.pdf" + "\"").body(resource);
	}

	@PostMapping(value = BaseURL + "/submitConsent", consumes = { "application/JSON" })
	public String submitConsent(@RequestBody String empInfo, HttpServletRequest requestContext) {
		System.out.println("In submitConsent Service "+empInfo);
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(empInfo);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("userid", token.getString("userid"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					response = PAS.submitConsent(empInfo, ip);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			System.out.println("exception caused");
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
		System.out.println("response from cvp ::;;;;"+response);
		return response;
	}

	@GetMapping(value = BaseURL + "/downloadAndSaveSignedPledge/{filename}", produces = { "application/JSON" })
	public String downloadAndSaveSignedPledge(@PathVariable("filename") String filename) {
		 System.out.println("In downloadAndSaveSignedPledge Service "+filename);
		 
		String response = null;
		String fileName = filename.split("&")[0];
		String txnid = new String(Base64.getDecoder().decode(filename.split("&")[1].replace("txnid=", "")));
		String signername = new String(Base64.getDecoder().decode(filename.split("&")[2].replace("signerNames=", "")));

		String rejectSigningFlag = Properties_Loader.getREJECT_SINGING();
		JSONObject checkSignStatus = PAS.checkSignstatus(txnid);
		
		
		System.out.println("checkSignStatus::"+checkSignStatus);
		
		try {
			if (("SUCCESS").equalsIgnoreCase(checkSignStatus.getString("status"))) {
				String empid = checkSignStatus.getString("empid");
				String empCompany = checkSignStatus.getString("empCompany");
				String empGroup = checkSignStatus.getString("empGroup");
				String docCode = checkSignStatus.getString("docCode");
				String empname = checkSignStatus.getString("empname");

				Sign_Doc_Details signDocInfo1 = PDS.checkSignDocDetails(empid, empCompany, empGroup, docCode);
				int signOrder = 1;
				if (signDocInfo1 != null) {
					signOrder = signDocInfo1.getSIGN_ORDER() + 1;
				}
				// fetch the Signerdata from memory for signer name checking
				JSONObject signersData = ContextObjects.getContextObjects();
				// System.out.println(signersData);
				JSONObject signerData = signersData.getJSONObject(txnid);
				String existingName = signerData.getString("signerName").trim();
				// based on company enabling and disabling the rejection flag(bcx for CVP HR
				// application name rejection is not required)
				if (!(empCompany.trim().equalsIgnoreCase("i25BCA"))) {
					rejectSigningFlag = "false";
				}
				// System.out.println(existingName);
				// comparing the signer name with the Existing Name(BC/Witness/BC Cell)
				if (!rejectSigning(rejectSigningFlag, signername, existingName)) {
					// if (empname.equalsIgnoreCase(signername))
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					response = PAS.downloadAndSaveSignedPledge(fileName, txnid, checkSignStatus.getString("docCode"));
					if (!filename.contains("Error")) {
						if (new JSONObject(response).has("statuscode")) {
							if (new JSONObject(response).getString("statuscode").equalsIgnoreCase("00")) {
								String currentSignedFilePath = new JSONObject(response).getString("signedFilePath");
								String referenceno = new JSONObject(response).getString("referenceno");
								String renamedSignedFilePath = "";
//							Sign_Doc_Details signDocInfo1 = PDS.checkSignDocDetails(empid, empCompany, empGroup,
//									docCode);
//							int signOrder = 1;
//							if (signDocInfo1 != null) {
//								signOrder = signDocInfo1.getSIGN_ORDER() + 1;
//							
								JSONObject input = new JSONObject();
								input.put("usertype", "auto");
								input.put("value", referenceno);
//								Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
								Employee_action_status statusInfo = new Employee_action_status();
								statusInfo.setCOMPANY_CODE(empCompany);
								statusInfo.setDOCUMENT_CODE(docCode);
								statusInfo.setEMP_CODE(empid);
								statusInfo.setEMP_GROUP(empGroup);
								statusInfo.setSIGN_TYPE(checkSignStatus.getString("signType"));
								statusInfo.setEMP_NAME(checkSignStatus.getString("empname"));
								statusInfo.setSIGN_DATE(currentTimestamp);
								statusInfo.setSIGN_STATUS(1);
								statusInfo.setSIGNER_NAME(signername);
								PDS.insertStatusInEAS(statusInfo);

								String renamedSignedFile = currentSignedFilePath.split("@")[1];
								if (signDocInfo1 == null) {
									// renaming the current file name for second signing purpose
									File signedFile = new File(currentSignedFilePath);
									File RenameSignedFile = new File(
											signedFile.getParent() + File.separator + renamedSignedFile);
									signedFile.renameTo(RenameSignedFile);
									renamedSignedFilePath = RenameSignedFile.getAbsolutePath();

									Sign_Doc_Details signDocInfo = new Sign_Doc_Details();
									signDocInfo.setDOC_CODE(docCode);
									signDocInfo.setDOC_PATH(renamedSignedFilePath);
									signDocInfo.setEMP_COMPANY(empCompany);
									signDocInfo.setEMP_GROUP(empGroup);
									signDocInfo.setEMPID(empid);
									signDocInfo.setSIGN_DATE(currentTimestamp);
									signDocInfo.setSIGN_ORDER(1);
									signDocInfo.setTXNID(referenceno);
									signDocInfo.setEMP_NAME(empname);
									signDocInfo.setSIGNER_NAME(signername);
									PDS.insertSignDocDetails(signDocInfo);
								} else {
									File previousSignedFile = new File(signDocInfo1.getDOC_PATH());
									File currentSignedFile = new File(currentSignedFilePath);
									// checking for corrupted files
									if (currentSignedFile.length() > previousSignedFile.length()) {

										// deleting the old signed file
										previousSignedFile.delete();

										File RenameSignedFile = new File(
												currentSignedFile.getParent() + File.separator + renamedSignedFile);
										currentSignedFile.renameTo(RenameSignedFile);
										renamedSignedFilePath = RenameSignedFile.getAbsolutePath();

										signDocInfo1.setDOC_PATH(renamedSignedFilePath);
										signDocInfo1.setSIGN_DATE(currentTimestamp);
										signDocInfo1.setSIGN_ORDER(signDocInfo1.getSIGN_ORDER() + 1);
										signDocInfo1.setTXNID(referenceno);
										signDocInfo1.setSIGNER_NAME(signDocInfo1.getSIGNER_NAME() + "," + signername);
										PDS.updateSignDocDetails(signDocInfo1);

									} else {
										try {
											JSONObject output = new JSONObject();
											output.put("statusmsg", "File Corrupted Please try again");
											output.put("statuscode", "01");
											response = output.toString();
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}

								response = new JSONObject(emailSignedPledge(input.toString()))
										.put("referenceno", referenceno).put("docCode", docCode)
										.put("signOrder", signOrder).toString();
							}
						} else {
							try {
								JSONObject output = new JSONObject();
								output.put("statusmsg", "FAILURE");
								output.put("statuscode", "01");
								response = output.toString();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else {

						try {
							JSONObject output = new JSONObject();
							output.put("statusmsg", "FAILURE");
							output.put("statuscode", "01");
							response = output.toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					// Rejecting the signed document and inserting to Sign_Rejected_Info table
					// System.out.println("Signing Agreement Rejected");
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					Sign_Rejected_Info sign_rejected_info = new Sign_Rejected_Info();
					sign_rejected_info.setEMP_ID(empid);
					sign_rejected_info.setLOG_TIME(currentTimestamp);
					sign_rejected_info.setPENALTY_FEE(00);
					sign_rejected_info.setEMP_NAME(empname);
					sign_rejected_info.setSIGN_MODE("1");
					sign_rejected_info.setPENALTY_STATUS(0);
					sign_rejected_info.setREJECTED_REASON("Name Mismatch");
					sign_rejected_info.setREJECTED_BY("system");
					sign_rejected_info.setSIGNER_NAME(signerData.getString("signerName"));
					sign_rejected_info.setSIGNED_BY(signername);
					sign_rejected_info.setDOC_CODE(docCode);
					sign_rejected_info.setEMP_COMPANY(empCompany);
					sign_rejected_info.setEMP_GROUP(empGroup);
					sign_rejected_info.setSIGN_ORDER(signOrder);

					sign_rejected_info.setSIGNER_DATA(signerData.toString());
					boolean res = PDS.insertintoRejectionList(sign_rejected_info);
					ContextObjects.clearContextObjects(txnid);
					// System.out.println(res);

					String Msg = "Signing agreement rejected ! Aadhaar name does not match with the records";
					JSONObject output = new JSONObject();

					output.put("statusmsg", Msg);
					output.put("statuscode", "01");
					response = output.toString();

					return response;
				}
			} else {
				try {
					JSONObject output = new JSONObject();
					output.put("statusmsg", "ALREADY DOWNLOADED !!");
					output.put("statuscode", "01");
					response = output.toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

//checking whether the flag in property loaders for rejection  is true and signer name does not match with existing name 
	private boolean rejectSigning(String rejectionFlag, String signerName, String existingName) {
		boolean result = false;
		String existingname = existingName.trim();
		if (rejectionFlag.equalsIgnoreCase("false")) {

			result = false;
		} else if (!signerName.trim().equalsIgnoreCase(existingname) && rejectionFlag.equalsIgnoreCase("true")) {
			result = true;
		}

		return result;
	}

	@GetMapping(value = BaseURL + "/downloadSignedPledge/{userInfo}")
	public ResponseEntity<Resource> downloadSignedPledge(@PathVariable("userInfo") String userInfo) {
		// System.out.println("In downloadSignedPledge Service ");
		String signedfilepath = PAS.getSignedPledgePath(userInfo);
		Resource resource = null;
		try {
			Path filePath = Paths.get(signedfilepath).toAbsolutePath().normalize();
			resource = new UrlResource(filePath.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InvalidPathException e) {
			System.out.println("In downloadSignedPledge Service Signed file path not found");
		} catch (NullPointerException e) {
			System.out.println("In downloadSignedPledge Service Signed file path not found");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String contentType = "application/pdf";
//		FileCleaner fc = new FileCleaner();
//		fc.filepath = signedfilepath;
//		fc.start();
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + "SignedDocument.pdf" + "\"").body(resource);

	}

	// Sending emial notifications after sign Completion
	@PostMapping(value = BaseURL + "/emailSignedPledge", consumes = { "application/JSON" })
	public String emailSignedPledge(@RequestBody String userInfo) {
		// System.out.println("In emailSignedPledge Service ");
		JSONObject output = PAS.getEmailSignedPledge(userInfo);
		return output.toString();
	}

	@PostMapping(value = BaseURL + "/getEmployeeList", consumes = { "application/JSON" })
	public String getEmpList(@RequestBody String reportInputs) {
		// System.out.println("In getEmpList Service ");
		JSONArray response = null;
		response = PAS.getEmpList(reportInputs);
		if (response == null || response.length() == 0)
			return "null";
		else
			return response.toString();
	}

	@GetMapping(value = BaseURL + "/getLogoImage/{filename}")
	public ResponseEntity<Resource> getLogoImage(@PathVariable("filename") String filename) {
//		System.out.println("In getLogoImage Service ");

		Resource resource = null;
		try {
			// System.out.println("filename : " + filename);
			resource = MySignClient.getEmpBasedPledgeFile(Properties_Loader.IMAGEPATH + filename);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String contentType = "application/octet-stream";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + "logo.jpg" + "\"").body(resource);

	}

	/*
	 * @PostMapping(value = BaseURL + "/uploadSignedPledge") public String
	 * uploadSignedFile(@RequestParam("file") MultipartFile uploadedInputStream,
	 * 
	 * @RequestParam("inputDetails") String userInfo, HttpServletRequest
	 * requestContext) { System.out.println("In UploadSignedDoc Service");
	 * JSONObject output = new JSONObject(); try { String ip =
	 * requestContext.getRemoteAddr(); Employee_Master mailInfo =
	 * PDS.getEmployeeDetails(new JSONObject(userInfo).getString("empid"));
	 * 
	 * if (mailInfo.getSIGN_STATUS() != 1)
	 * 
	 * { String hrMail = PDS.getHRMail(); String refno = mailInfo.getEMP_ID() +
	 * System.currentTimeMillis(); String outfilepath = Properties_Loader.UPLOADPATH
	 * + "/" + refno; String absolutefilepath = new
	 * File(outfilepath).getAbsolutePath(); InputStream inputStream = new
	 * BufferedInputStream(uploadedInputStream.getInputStream()); int read = 0;
	 * byte[] bytes = new byte[1024]; OutputStream out = new FileOutputStream(new
	 * File(absolutefilepath)); while ((read = inputStream.read(bytes)) != -1) {
	 * out.write(bytes, 0, read); } out.flush(); out.close(); inputStream.close();
	 * MimeMessage message = sender.createMimeMessage();
	 * 
	 * try { MimeMessageHelper helper = new MimeMessageHelper(message, true);
	 * helper.setCc(mailInfo.getEMP_EMAIL()); helper.setTo(hrMail);
	 * helper.setText("Dear " + mailInfo.getEMP_NAME() + " (" + mailInfo.getEMP_ID()
	 * + ")," + "\r\n" + "\r\n" +
	 * "Please Find Attached the Signed Core Value Pledge document. \r\n" + "\r\n" +
	 * "Regards, \r\n" + "HR Desk"); helper.setSubject("Core Value Pledge - " +
	 * mailInfo.getEMP_COMPANY() + ", " + mailInfo.getEMP_ID() + "," +
	 * mailInfo.getEMP_NAME() + ""); helper.addAttachment("SignedPDF.pdf", new
	 * File(absolutefilepath)); sender.send(message); if
	 * (PAS.updatePhysicalSignedrecord(mailInfo.getEMP_ID(), refno, ip,
	 * absolutefilepath)) { FileCleaner fc = new FileCleaner(); fc.filepath =
	 * outfilepath; fc.start(); output.put("statuscode", "00");
	 * output.put("statusmsg", "SUCCESS"); JSONObject auditInput = new JSONObject();
	 * auditInput.put("empid", new JSONObject(userInfo).getString("empid"));
	 * auditInput.put("operationtype", "Upload Signed Pledge");
	 * auditInput.put("operationremarks", "Success"); auditInput.put("userid", "");
	 * auditInput.put("ip", ip); PAS.insertauditinfo(auditInput); } else {
	 * output.put("statuscode", "01"); output.put("statusmsg", "FAILED"); JSONObject
	 * auditInput = new JSONObject(); auditInput.put("empid", new
	 * JSONObject(userInfo).getString("empid")); auditInput.put("operationtype",
	 * "Upload Signed Pledge"); auditInput.put("operationremarks", "Failure");
	 * auditInput.put("userid", ""); auditInput.put("ip", ip);
	 * PAS.insertauditinfo(auditInput); }
	 * 
	 * } catch (MessagingException e) { e.printStackTrace(); try {
	 * output.put("statuscode", "01"); output.put("statusmsg", "FAILURE");
	 * JSONObject auditInput = new JSONObject(); auditInput.put("empid", new
	 * JSONObject(userInfo).getString("empid")); auditInput.put("operationtype",
	 * "Upload Signed Pledge"); auditInput.put("operationremarks", "Failure");
	 * auditInput.put("userid", ""); auditInput.put("ip", ip);
	 * PAS.insertauditinfo(auditInput); } catch (JSONException e1) {
	 * e1.printStackTrace(); } } catch (Exception e) { e.printStackTrace(); try {
	 * output.put("statuscode", "01"); output.put("statusmsg", "FAILURE");
	 * JSONObject auditInput = new JSONObject(); auditInput.put("empid", new
	 * JSONObject(userInfo).getString("empid")); auditInput.put("operationtype",
	 * "Upload Signed Pledge"); auditInput.put("operationremarks", "Failure");
	 * auditInput.put("userid", ""); auditInput.put("ip", ip);
	 * PAS.insertauditinfo(auditInput); } catch (JSONException e1) {
	 * e1.printStackTrace(); } }
	 * 
	 * } else { try { output.put("statuscode", "01"); output.put("statusmsg",
	 * "Employee Already Signed !!"); JSONObject auditInput = new JSONObject();
	 * auditInput.put("empid", new JSONObject(userInfo).getString("empid"));
	 * auditInput.put("operationtype", "Upload Signed Pledge");
	 * auditInput.put("operationremarks", "Failure : Already Signed the Pledge");
	 * auditInput.put("userid", ""); auditInput.put("ip", ip);
	 * PAS.insertauditinfo(auditInput); } catch (JSONException e1) {
	 * e1.printStackTrace(); } }
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } catch (JSONException e2) {
	 * e2.printStackTrace(); } return output.toString(); }
	 */

	@GetMapping(value = BaseURL + "/validateToken/{token}")
	public String validateToken(@PathVariable("token") String token, HttpServletRequest requestContext) {
		// System.out.println("In ValidateToken Service");
		JSONObject out = new JSONObject();
		try {
			if (TokenManager.validateToken(token)) {
				out.put("statuscode", "00");
				out.put("statusmsg", "SUCCESS");
			} else {
				out.put("statuscode", "01");
				out.put("statusmsg", "FAILURE");
			}
		} catch (Exception x) {
			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "FAILURE");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return out.toString();
	}

	@PostMapping(value = BaseURL + "/getWidgets")
	public String getWidgets(@RequestBody String reportInputs) {
		// System.out.println("In getWidgets Service");
		JSONObject out = PAS.getWidget(reportInputs);
		return out.toString();
	}

	@PostMapping(value = BaseURL + "/getDocList", consumes = { "application/JSON" }, produces = { "application/JSON" })
	public String getDocList(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("Inside getDocList...");
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					if (token.has("empname")) {
						userdetail.put("username", token.getString("empname"));
						userdetail.put("userid", token.getString("empid"));
					} else if (token.has("username")) {
						userdetail.put("username", token.getString("username"));
						userdetail.put("userid", token.getString("userid"));
					}
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.getDocList(input);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

			return js.toString();
		} catch (JSONException e) {
			return js.toString();

		} catch (Exception e) {
			return js.toString();

		}
	}

	@PostMapping(value = BaseURL + "/witnessEsign", consumes = { "application/JSON" })
	public String witnessEsign(@RequestBody String empInfo, HttpServletRequest requestContext) {
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(empInfo);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("userid", token.getString("userid"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					// ContextObjects.setContextObjects(inputs, authtoken);
					response = PAS.witnessEsign(empInfo, ip);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@PostMapping(value = BaseURL + "/getSignedDocList", consumes = { "application/JSON" }, produces = {
			"application/JSON" })
	public String getSignedDocList(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("In getSignedDocList Service ");
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.getSignedDocList(input);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

		} catch (JSONException e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return js.toString();
	}

	// This Service is used to sign the documents based on mode=esign/token.
	@PostMapping(value = BaseURL + "/signExistingDocument", consumes = { "application/JSON" })
	public String signExistingDocument(@RequestBody String empInfo, HttpServletRequest requestContext) {
		// System.out.println("In signExistingDocument Service ");
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(empInfo);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);
					response = PAS.signExistingDocument(empInfo, ip);
				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// callback service based on jsign response redirection
	@GetMapping(value = BaseURL + "/jsignResponse/{filename}")
	public ResponseEntity jsignResponse(@PathVariable("filename") String filename) {
		// System.out.println("In jsignResponse Service ");

		String response = null;
		String errorMsg = null;

		if (filename.contains("Error")) {
			errorMsg = filename.split("&")[1].replace("Error=", "");
		}
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!filename.contains("Error")) {
				filename = filename.replace("filename=", "");
				response = downloadAndSaveSignedPledge(filename);
				// Mode-1- to display esignedFailed response Msg based on mode in the client
				// side
				// Mode-2-esignedFailed(Name Miss Match)Rejected Msg based on mode in the client
				// side
				JSONObject resp = new JSONObject(response);
				int mode = 1;
				String Msg = "Signing Agreement Rejected!!Your Aadhaar Name Does not Match With the Records";
				// checking whether the error msg is of mode 1 or mode 2 by the Msg content and
				// setting the modes to send to client
				if (resp.getString("statusmsg").equalsIgnoreCase(Msg)) {
					mode = 2;
				}
				if (resp.getString("statuscode").equalsIgnoreCase("00")) {
					headers.setLocation(
							URI.create(Properties_Loader.CLIENT_RES_URL + "/dsctokenSign?bcas=SUCCESS&mode=1"));
				} else {
					if (mode == 2) {
						headers.setLocation(
								URI.create(Properties_Loader.CLIENT_RES_URL + "/dsctokenSign?bcas=FAILURE&mode=2"));
					} else {
						headers.setLocation(
								URI.create(Properties_Loader.CLIENT_RES_URL + "/dsctokenSign?bcas=FAILURE&mode=1"));
					}
				}
			} else {
				System.out.println("Error Msg :" + errorMsg);
				headers.setLocation(URI.create(Properties_Loader.CLIENT_RES_URL + "/dsctokenSign?bcas=FAILURE&mode=1"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			headers.setLocation(URI.create(Properties_Loader.CLIENT_RES_URL + "/dsctokenSign?bcas=FAILURE"));
		}
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	// This Service is used to download the signed document in UI
	@GetMapping(value = BaseURL + "/downloadDoc/{txnid}")
	public ResponseEntity downloadDoc(@PathVariable("txnid") String txnid) throws Exception {
		// System.out.println("In downloadDoc Service ");
		String contentType = "application/pdf"; 

		Sign_Doc_Details signDocInfo = PDS.getSignedDocListTxnid(txnid);
		String filepath = signDocInfo.getDOC_PATH();
		Path filePath = Paths.get(filepath).toAbsolutePath().normalize();
		Resource resource = new UrlResource(filePath.toUri());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + txnid + ".pdf\"").body(resource);
	}

	// This service is used to send notifications via SMS/Email.
	@PostMapping(value = BaseURL + "/sendNotification", consumes = { "application/JSON" })
	public String sendNotification(@RequestBody String userInfo, HttpServletRequest requestContext) {
		 System.out.println("In sendNotification Service ");
		String ip = requestContext.getRemoteAddr();
		String response = null;
		JSONObject js = new JSONObject();
		JSONObject output = new JSONObject();

		try {
			JSONObject input = new JSONObject(userInfo);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);
					response = PAS.sendNotification(userInfo, ip);
				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;

	}

	// This service is used to Insert the BC details in user request table who ask
	// for updating Phone.No/Name Change
	@PostMapping(value = BaseURL + "/userUpdateRequest", consumes = { "application/JSON" })
	public String userUpdateRequest(@RequestBody String inputs, HttpServletRequest httpServletRequest) {
		// System.out.println("In userUpdateRequest Service ");
		JSONObject updateRequestResp = new JSONObject();
		try {
			JSONObject userUpdateReqInfo = new JSONObject(inputs);
			String ipAdd = getClientIpAddress(httpServletRequest);
			userUpdateReqInfo.put("ipAdd", ipAdd);
			updateRequestResp = PAS.userUpdateRequest(userUpdateReqInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return updateRequestResp.toString();

	}

	// This service is used to verify the Phone.No When the BC user Updates his
	// Phone No.
	@PostMapping(value = BaseURL + "/updateProfileOnOtp", consumes = { "application/JSON" })
	public String updateProfileOnOtp(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("In updateProfileOnOtp Service ");
		JSONObject updateMobileNumber = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			updateMobileNumber = PAS.updateProfileOnOtp(input);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return updateMobileNumber.toString();

	}

	// This service is used to get the list of BC's who has asked for Name Updation
	@PostMapping(value = BaseURL + "/bcRequestsforUpdatingNames", consumes = { "application/JSON" })
	public String bcRequestsforUpdatingNames(@RequestBody String inputs) {
		String authtoken = null;
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(inputs);
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					List<Object> resp = PAS.bcRequestsforUpdatingNames();
					js.put("status", "SUCCESS");
					js.put("statusDetails", "List of Names ");
					js.put("list", resp);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		// System.out.println("In approveNameList Service ");
		return js.toString();
	}

	@PostMapping(value = BaseURL + "/bcNamesapprovedList", consumes = { "application/JSON" })
	public String bcNamesapprovedList(@RequestBody String inputs, HttpServletRequest httpServletRequest) {
		// System.out.println("In approvalList Service ");
		String authtoken = null;
		JSONObject approveUpdateNameResponse = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject approveUpdateNameRequest = new JSONObject(inputs);
					String ipAdd = getClientIpAddress(httpServletRequest);
					approveUpdateNameRequest.put("ipAdd", ipAdd);
					approveUpdateNameResponse = PAS.bcNamesapprovedList(approveUpdateNameRequest);
				} else {
					approveUpdateNameResponse.put("status", "FAILURE");
					approveUpdateNameResponse.put("statusDetails", "Session Expired!!");
				}
			} else {
				approveUpdateNameResponse.put("status", "FAILURE");
				approveUpdateNameResponse.put("statusDetails", "Authentication key not found !!");
			}
//			JSONObject approveUpdateNameRequest = new JSONObject(inputs);
//			String ipAdd = getClientIpAddress(httpServletRequest);
//			approveUpdateNameRequest.put("ipAdd", ipAdd);
//			approveUpdateNameResponse = PAS.bcNamesapprovedList(approveUpdateNameRequest);
		} catch (JSONException e) {
			try {
				e.printStackTrace();
				approveUpdateNameResponse.put("status", "FAILURE");
				approveUpdateNameResponse.put("statusDetails", e.getMessage());
				return approveUpdateNameResponse.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return approveUpdateNameResponse.toString();

	}

	@PostMapping(value = BaseURL + "/bcNamesRejectionList", consumes = { "application/JSON" })
	public String bcNamesRejectionList(@RequestBody String inputs, HttpServletRequest httpServletRequest) {
		// System.out.println("In approvalList Service ");
		String authtoken = null;
		JSONObject bcNamesRejectionListResponse = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject bcNamesRejectionList = new JSONObject(inputs);
					String ipAdd = getClientIpAddress(httpServletRequest);
					bcNamesRejectionList.put("ipAdd", ipAdd);
					bcNamesRejectionListResponse = PAS.bcNamesRejectionList(bcNamesRejectionList);
				} else {
					bcNamesRejectionListResponse.put("status", "FAILURE");
					bcNamesRejectionListResponse.put("statusDetails", "Session Expired!!");
				}
			} else {
				bcNamesRejectionListResponse.put("status", "FAILURE");
				bcNamesRejectionListResponse.put("statusDetails", "Authentication key not found !!");
			}
		} catch (JSONException e) {
			try {
				e.printStackTrace();
				bcNamesRejectionListResponse.put("status", "FAILURE");
				bcNamesRejectionListResponse.put("statusDetails", e.getMessage());
				return bcNamesRejectionListResponse.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return bcNamesRejectionListResponse.toString();

	}

	// to get the loacal lang lists in which the agreement is avaiable
	@PostMapping(value = BaseURL + "/getlangList", consumes = { "application/JSON" }, produces = { "application/JSON" })
	public String getLangList(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("Inside getLangList...");
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					if (token.has("empname")) {
						userdetail.put("username", token.getString("empname"));
						userdetail.put("userid", token.getString("empid"));
					} else if (token.has("username")) {
						userdetail.put("username", token.getString("username"));
						userdetail.put("userid", token.getString("userid"));
					}
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.getLangList(input);
					

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

			return js.toString();
		} catch (JSONException e) {
			return js.toString();

		} catch (Exception e) {
			return js.toString();

		}
	}

	// to get the local language pdf file from the location in which it is present
	@PostMapping(value = BaseURL + "/viewlangFile", consumes = { "application/JSON" }, produces = {
			"application/JSON" })
	public String viewlangFile(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("In viewlangFile Service ");
		JSONObject js = new JSONObject();
//		System.out.println("In getPDFPath Service ");
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {

					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("userid", token.getString("userid"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.getLangFilePath(input);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return js.toString();

	}

	// This service is used to get statewise BC's signing status
	@RequestMapping(value = BaseURL + "/bcstatewisesigningstatus{at}", method = RequestMethod.GET)
	public ResponseEntity<Object> bcstatewisesigningstatus(@RequestParam String at) throws Exception {
		String contentType = "application/pdf";
		JSONObject tokens = ContextObjects.getAuthTokens();
		try {
			at = new String(Base64.getDecoder().decode(at));
			if (tokens.has(at)) {
				// System.out.println("In bcstatewisesigningstatus Service ");
				JSONObject response = PAS.bcstatewisesigningstatus();
				Path filePath = Paths.get(response.getString("filepath")).toAbsolutePath().normalize();
				Resource resource = new UrlResource(filePath.toUri());
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bcsigningstatusreport.csv\"")
						.body(resource);
			} else {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
						.body("Failure Reason : Session Expired");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
					.body("Failure Reason :Session Expired");
		}
		// return null;
	}

	// This service is used to get deatils of BC's signing rejection
	// in UI
	@RequestMapping(value = BaseURL + "/bcsignrejectiondetails{at}", method = RequestMethod.GET)
	public ResponseEntity<Object> bcsignrejectiondetails(@RequestParam String at) throws Exception {
		String contentType = "application/pdf";
		JSONObject tokens = ContextObjects.getAuthTokens();
		try {
			at = new String(Base64.getDecoder().decode(at));
			if (tokens.has(at)) {
				JSONObject response = PAS.bcsignrejectiondetails();
				Path filePath = Paths.get(response.getString("filepath")).toAbsolutePath().normalize();
				Resource resource = new UrlResource(filePath.toUri());
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bcnamerejectionreport.csv\"")
						.body(resource);
			} else {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
						.body("Failure Reason : Session Expired");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
					.body("Failure Reason :Session Expired");
		}
		// return null;
	}

	// generate otp request to eSign for getting otp to perform OTP based signing
	// for witness
	@PostMapping(value = BaseURL + "/generateOTPforWitnessEsign", consumes = { "application/JSON" })
	public String generateOTPforWitnessEsign(@RequestBody String witnessInfo, HttpServletRequest requestContext) {
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(witnessInfo);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject token = tokens.getJSONObject(authtoken);
					token.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, token);
					response = PAS.generateOTPforWitnessEsign(witnessInfo, ip);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// generate otp request to eSign for getting otp to perform OTP based signing
	// for witness
	@PostMapping(value = BaseURL + "/resendOTPforWitnessEsign", consumes = { "application/JSON" })
	public String resendOTPforWitnessEsign(@RequestBody String witnessInfo, HttpServletRequest requestContext) {
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(witnessInfo);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject token = tokens.getJSONObject(authtoken);
					token.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, token);
					response = PAS.resendOTPforWitnessEsign(witnessInfo, ip);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// to get the present IP of the BCCellToken
	@PostMapping(value = BaseURL + "/getBcCellIP", consumes = { "application/JSON" })
	public String getBcCellIP(@RequestBody String inputs, HttpServletRequest requestContext) {
		String ip = requestContext.getRemoteAddr();
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject token = tokens.getJSONObject(authtoken);
					token.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, token);
					response = PAS.getBcCellIP();

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					response = js.toString();
				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");
				response = js.toString();
			}
		} catch (Exception e) {
			try {
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
				response = js.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	// to update the BC Cell Ip by taking the input
	@PostMapping(value = BaseURL + "/updateBcCellIP", consumes = { "application/JSON" }, produces = {
			"application/JSON" })
	public String updateBcCellIP(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("Inside getLangList...");
		JSONObject js = new JSONObject();
		String response = null;
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;

			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject token = tokens.getJSONObject(authtoken);
					token.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, token);
					response = PAS.updateBcCellIP(input);
					return response;
				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");
					js.toString();

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

			return js.toString();
		} catch (JSONException e) {
			return js.toString();

		} catch (Exception e) {
			return js.toString();

		}
	}

	// get Empl list from EMPL_master
	@PostMapping(value = BaseURL + "/getEmplList", consumes = { "application/JSON" }, produces = { "application/JSON" })
	public String getEmplList(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("In getSignedDocList Service ");
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.getEmplList(input);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

		} catch (JSONException e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return js.toString();
	}

	// update user status in empl master tbl
	@PostMapping(value = BaseURL + "/updateUserStatus", consumes = { "application/JSON" }, produces = {
			"application/JSON" })
	public String updateUserStatus(@RequestBody String inputs, HttpServletRequest requestContext) {
		// System.out.println("In getSignedDocList Service ");
		JSONObject js = new JSONObject();
		try {
			JSONObject input = new JSONObject(inputs);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					js = PAS.updateUserStatus(input, userdetail);

				} else {
					js.put("status", "FAILURE");
					js.put("statusDetails", "Session Expired!!");

				}
			} else {
				js.put("status", "FAILURE");
				js.put("statusDetails", "Authentication key not found !!");

			}

		} catch (JSONException e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (Exception e) {
			try {
				e.printStackTrace();
				js.put("status", "FAILURE");
				js.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return js.toString();
	}

	// bulk data upload
	@PostMapping(value = BaseURL + "/bulkDataUpload")
	public ResponseEntity<String> bulkUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("inputDetails") String inputDetails) throws Exception {
		JSONObject res = new JSONObject();
		JSONObject responseData = new JSONObject();
//			String res1 = "Hello";
//		System.out.println("inputs " + inputDetails);
		try {
			JSONObject input = new JSONObject(inputDetails);
			String authtoken = null;
			if (input.has("authToken")) {
				authtoken = input.getString("authToken");
			}
			if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
				JSONObject tokens = ContextObjects.getAuthTokens();
				if (tokens.has(authtoken)) {
					JSONObject userdetail = new JSONObject();
					JSONObject token = tokens.getJSONObject(authtoken);
//					System.out.println("token " + token);
					userdetail.put("username", token.getString("username"));
					userdetail.put("reqtime", new Date());
					ContextObjects.setAuthTokens(authtoken, userdetail);

					if (userdetail.get("username") != null) {

						// Fetch User_id Based on User_Name from User_master table.

						JSONObject rolePermissionValidation = new JSONObject();

						String userName = userdetail.getString("username");
						input.put("userName", userName);
//					        //System.out.println(userId);
						// user role validation yet to impl
						boolean resJs = PAS.validateUserRolePermission(userName);
						if (resJs) {
//							System.out.println("user validated successdully " + resJs);
							res = PAS.readBulkInputs(file, input.toString());
							responseData = res;
						} else {
							responseData.put("status", "FAILURE");
							responseData.put("statusDetails", "Permission Denied");
							responseData.put("bulkDataUploadPermission", "false");
						}

					}

				} else {
					responseData.put("status", "FAILURE");
					responseData.put("statusDetails", "Session Expired!");
				}
			} else {
				responseData.put("status", "FAILURE");
				responseData.put("statusDetails", "Authentication key not found !!");
			}

//							System.out.println("final Response="+ responseData);
			return new ResponseEntity<>(responseData.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(responseData.toString(), HttpStatus.BAD_REQUEST);
		}

	}

	// Data upload status check
	@SuppressWarnings({ "unused" })
	@GetMapping(value = BaseURL + "/getDataUploadStatus/{fileName}/{at}")
	public ResponseEntity<Object> getDataUploadStatus(@PathVariable("fileName") String fileName, String tempid,
			@PathVariable("at") String at, HttpServletRequest requestContext) {
//			 System.out.println("In downloadPledgeInfo Service ");
		String contentType = "application/pdf";
		JSONObject tokens = ContextObjects.getAuthTokens();
		try {

			fileName = new String(Base64.getDecoder().decode(fileName));
			if (tokens.has(at)) {

				Path filePath = Paths.get(Properties_Loader.UNSIGNED_FILES_DIRECTORY + File.separator + fileName)
						.toAbsolutePath().normalize();
				Resource resource = new UrlResource(filePath.toUri());
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
						.body(resource);

			} else {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
						.body("Failure Reason : Session Expired");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
					.body("Failure Reason :Session Expired");
		}
	}

	// download template based on login user

	@SuppressWarnings({ "unused" })
	@GetMapping(value = BaseURL + "/getTemplate/{username}/{tempid}/{at}")
	public ResponseEntity<Object> downloadTemplate(@PathVariable("username") String username,
			@PathVariable("tempid") String tempid, @PathVariable("at") String at, HttpServletRequest requestContext) {
//			 System.out.println("In downloadPledgeInfo Service ");
		String contentType = "application/pdf";
		JSONObject tokens = ContextObjects.getAuthTokens();
		try {

//			 String userNAme= new String(Base64.getDecoder().decode(username));
			username = new String(Base64.getDecoder().decode(username));
			if (tokens.has(at)) {
				// System.out.println("In bcstatewisesigningstatus Service ");

				// Fetch User_id Based on User_Name from User_master table.

				JSONObject rolePermissionValidation = new JSONObject();

//							String userName = userdetail.getString("username");
//					        //System.out.println(userId);
				// user role validation yet to impl
				boolean resJs = PAS.validateUserRolePermission(username);
				if (resJs) {
					JSONObject response = PAS.getTemplateBasedonUserId(tempid);
					Path filePath = Paths.get(response.getString("filePath")).toAbsolutePath().normalize();
					Resource resource = new UrlResource(filePath.toUri());
					return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulkDataUpload.csv\"")
							.body(resource);
				} else {
					return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
							.header(HttpHeaders.CONTENT_DISPOSITION,
									"attachment; filename=\"" + "Error_File.txt" + "\"")
							.body("Failure Reason : Permission Denied!");
				}

			} else {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
						.body("Failure Reason : Session Expired");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Error_File.txt" + "\"")
					.body("Failure Reason :Session Expired");
		}
		// return null;
	}

	// get companytInfo from company master
	@GetMapping(value = BaseURL + "/getCompanyInfo/{at}")
	public String getCompanyDetails(@PathVariable("at") String at, HttpServletRequest requestContext) {
		JSONObject tokens = ContextObjects.getAuthTokens();
		JSONObject companyDetails = new JSONObject();
		JSONObject js = new JSONObject();
		try {
			if (tokens.has(at)) {

//			 JSONObject compInfo = (JSONObject) InMemory.gettemplateData().get("i25BCA");
//			 JSONObject compInfo1=(JSONObject)InMemory.gettemplateData().get("iMFAST");
//			 System.out.println(compInfo);
//			 System.out.println(compInfo1);
//			 
//			 companyDetails.put("status", "SUCCESS");
//				companyDetails.put("statusDetails", "Data Received successfully");
//				companyDetails.put("compInfo", compInfo);		
//				companyDetails.put("compInfo1", compInfo1);
//				
				js = (JSONObject) InMemory.gettemplateData().get("dropDownList");
				companyDetails.put("status", "SUCCESS");
				companyDetails.put("data", js);

			} else {
				companyDetails.put("status", "FAILURE");
				companyDetails.put("statusDetails", "Session Expired!");
			}

		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
		return companyDetails.toString();
	}

	// to fetch client IP
	private String getClientIpAddress(HttpServletRequest request) {
		final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
				"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
	
	
	

	// Removing/deleting user 
	@PostMapping(value = BaseURL + "/removeBCDetails", consumes = { "application/JSON" }, produces = {
			"application/JSON" })
	public ResponseEntity<String> removeBCDetails(@RequestBody String inputs, HttpServletRequest requestContext) throws JSONException {
		
		JSONObject js = new JSONObject(inputs);
		JSONObject resp=new JSONObject();
		JSONObject jres=new JSONObject();
		String authtoken = null;
		if (js.has("authToken")) {
			authtoken = js.getString("authToken");
		}
		if (null != authtoken && !"".equalsIgnoreCase(authtoken)) {
			JSONObject tokens = ContextObjects.getAuthTokens();
			if (tokens.has(authtoken)) {
				JSONObject userdetail = new JSONObject();
				JSONObject token = tokens.getJSONObject(authtoken);
				userdetail.put("username", token.getString("username"));
				userdetail.put("reqtime", new Date());
				
				if(js.has("recussiveCall")) {
					String decryptedInput=Certificate.decrypt(js.getString("encypInputs"));
					
					JSONObject decryptedUserDetails=new JSONObject(Certificate.decrypt(js.getString("encypUserdetail")));
					 jres =  PAS.removeBC(decryptedInput,decryptedUserDetails,true);
					
				}
				else {
					jres =  PAS.checkSignDoc(inputs,userdetail);
					
				}
				
			if(jres.getString("status").equalsIgnoreCase("success")) {
				resp.put("status", "Success");
				if(jres.getString("statusDetails").equalsIgnoreCase("signing status:1")) {
					resp.put("statusDetails", "Siginng is performed");
					resp.put("inputs", jres.getString("inputs"));
					resp.put("userdetail",jres.getString("userdetail"));
				}
				else {
				resp.put("statusDetails", "BC account removed successfully");
				}
		}
		else {
			resp.put("status", "Failure");
			resp.put("statusDetails", "Try again after some time");
		}
		}
		else {
			resp.put("status", "FAILURE");
			resp.put("statusDetails", "Session Expired!!");

		}
	} else {
		resp.put("status", "FAILURE");
		resp.put("statusDetails", "Authentication key not found !!");

	}
		
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		
	}
	
	

	
	
	
	
	@PostMapping(value = BaseURL + "/registeredBankInfo", produces = {
	"application/JSON" })
public String getBankDetails() throws JSONException {
		JSONObject output =new JSONObject();
		try {
			 output =  PAS.getBank();
		}
		
		catch(Exception e) {
			output.put("status","failure");
			output.put("statusDetails",e.getMessage());
			
		}
		
		return output.toString(); 
		
	}


	
	
	
	
	
}