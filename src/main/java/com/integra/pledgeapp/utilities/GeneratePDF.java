package com.integra.pledgeapp.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import java.util.ListIterator;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Doc_Sign_Config;
import com.integra.pledgeapp.beans.Dsc_Token_Config;
import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.core.PledgeAppDAOImpl;
import com.integra.pledgeapp.core.PledgeAppDAOServices;
import com.integra.pledgeapp.token.PDFUSBTokenSign;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GeneratePDF {

	public static JSONObject generatePDF(Employee_Master employeeDetails, String refno, String FILE, String rectFlag,
			String type, String docCode,JSONObject familyInfo) {

	
		StringBuilder contentBuilder = new StringBuilder();
		String filePath = null;
		JSONObject jsRes = new JSONObject();
		try {
			JSONArray familyDetails=null;
			PledgeAppDAOServices PDS = new PledgeAppDAOImpl();
			JSONObject docInfo = PDS.getDocInfoInEAM(docCode); 
			System.out.println("docINfo+ "+docInfo);
			BufferedReader in = new BufferedReader(
					new FileReader(Properties_Loader.getHTMLPDF_FILE_PATH() + "/" + docInfo.getString("fileName")));
			if (docInfo.getInt("generatePdf") == 1) {
				filePath = FILE + File.separator + docCode + refno + ".pdf";
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
				String htmldata = contentBuilder.toString();
				String temp="";
				//
				
				
				//changes
				
				
				if(familyInfo.has("isFamilyPresent")) {
				//Consent rendering into pdf based on presence of family details 
				if(familyInfo.getString("isFamilyPresent").equalsIgnoreCase("true")) {
					 familyDetails=familyInfo.getJSONArray("familyDetails");
					for(int i=0;i<familyDetails.length();i++) {
						JSONObject jsonFamily=familyDetails.getJSONObject(i);
						if(familyInfo.has("afterConsent")) {
							temp+=""+" <tr><td><Input type=checkbox checked> </Input></td> <td>I, "+jsonFamily.getString("name")+" "+jsonFamily.getString("relationship")+" of "+employeeDetails.getEMP_NAME()+" read the instructions and agree to proceed with signing. <td></tr>" ;
						}
						else {
						temp+=""+" <tr><td><Input type=checkbox> </Input></td> <td>I, "+jsonFamily.getString("name")+" "+jsonFamily.getString("relationship")+" of "+employeeDetails.getEMP_NAME()+" read the instructions and agree to proceed with signing. <td></tr>" ;			
						}
					}
					
				}
				else {
					if(familyInfo.has("afterConsent")) {
						temp+=""+" <tr><td><Input type=checkbox checked> </Input></td> <td>I,"+employeeDetails.getEMP_NAME()+" have no family, I am the single person of the family <td></tr>" ;
					}
					else {
						temp+=""+" <tr><td><Input type=checkbox > </Input></td> <td>I, "+employeeDetails.getEMP_NAME()+" have no family, I am the single person of the family <td></tr>" ;
					}
					
				}		
				//changes
				}
				else {
					temp="";
					
				}
//				appending family details to pdf 
				htmldata = htmldata.replace("$$Familydetails$$", temp);
				
				
				
				
				
				//
				
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();

				JSONObject empData = new JSONObject();
				empData.put("name", employeeDetails.getEMP_NAME());
				empData.put("mobile", employeeDetails.getEMP_PHONE());
				empData.put("month", new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date.getTime()));
				empData.put("date", formatter.format(date));
				empData.put("day", new SimpleDateFormat("dd").format(date));
				empData.put("year", new SimpleDateFormat("yyyy").format(date));
				empData.put("dob", employeeDetails.getEMP_DOB());
				if (employeeDetails.getADDITIONAL_DATA() != null) {
					JSONObject additionalData = new JSONObject(employeeDetails.getADDITIONAL_DATA());
					if (additionalData.length() > 0) {
						if (additionalData.has("ward")) {
							empData.put("ward", additionalData.getString("ward"));
						}
						if (additionalData.has("taluk")) {
							empData.put("taluk", additionalData.getString("taluk"));
						}
						if (additionalData.has("district")) {
							empData.put("district", additionalData.getString("district"));
						}
						if (additionalData.has("state")) {
							empData.put("state", additionalData.getString("state"));
						}
						if (additionalData.has("pinCode")) {
							empData.put("pinCode", additionalData.getString("pinCode"));
						}
						if (additionalData.has("effectiveDate")) {
							empData.put("effectiveDate", additionalData.getString("effectiveDate"));
						}
						if (additionalData.has("pan")) {
							empData.put("pan", additionalData.getString("pan"));
						}
						if (additionalData.has("bcId")) {
							empData.put("bcId", additionalData.getString("bcId"));
						}
						if (additionalData.has("bank")) {
							empData.put("bank", additionalData.getString("bank"));
						}
						if (additionalData.has("branch")) {
							empData.put("branch", additionalData.getString("branch"));
						}
						if (additionalData.has("bcLocation")) {
							empData.put("bcLocation", additionalData.getString("bcLocation"));
						}
						if (additionalData.has("IFSC1")) {
							empData.put("IFSC1", additionalData.getString("IFSC1"));
						}
						if (additionalData.has("accountNo1")) {
							empData.put("accountNo1", additionalData.getString("accountNo1"));
						}
						if (additionalData.has("IFSC2")) {
							empData.put("IFSC2", additionalData.getString("IFSC2"));
						}
						if (additionalData.has("accountNo2")) {
							empData.put("accountNo2", additionalData.getString("accountNo2"));
						}
						if (additionalData.has("commissionAC")) {
							empData.put("commissionAC", additionalData.getString("commissionAC"));
						}
						if (additionalData.has("settlementAC")) {
							empData.put("settlementAC", additionalData.getString("settlementAC"));
						}
						if (additionalData.has("age")) {
							empData.put("age", additionalData.getString("age"));
						}
						if (additionalData.has("relationship")) {
							empData.put("relationship", additionalData.getString("relationship"));
						}
						if (additionalData.has("address")) {
							empData.put("address", additionalData.getString("address"));
						}
						if (additionalData.has("act")) {
							empData.put("act", additionalData.getString("act"));
						}
						if (additionalData.has("bankAddress")) {
							empData.put("bankAddress", additionalData.getString("bankAddress"));
						}
						if (additionalData.has("bankFullName")) {
							empData.put("bankFullName", additionalData.getString("bankFullName"));
						}
						// later need to put a generic variable
						if (additionalData.has("companyFullName")) {
							empData.put("companyFullName", additionalData.getString("companyFullName"));
							empData.put("empcompany", employeeDetails.getCompositeKey().getEMP_COMPANY());
							empData.put("empid", employeeDetails.getCompositeKey().getEMP_ID());
						}
					}
				} else {
					empData.put("empcompany", employeeDetails.getCompositeKey().getEMP_COMPANY());
					empData.put("empid", employeeDetails.getCompositeKey().getEMP_ID());
				}
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("HTMLTEMPLATE", htmldata);
				jsonObject.put("INPUTJSON", empData);
				jsonObject.put("TXNID", refno);
				// only passing the footer value for executing the CVP Agreement
				if (employeeDetails.getCompositeKey().getEMP_COMPANY().equalsIgnoreCase("i25BCA")
						&& employeeDetails.getCompositeKey().getEMP_GROUP().equalsIgnoreCase("BOBUPSRLM")) {
					jsonObject.put("FOOTERVALUE", "BANK SAKHI Agreement");
				} else if (employeeDetails.getCompositeKey().getEMP_COMPANY().equalsIgnoreCase("i25BCA")) {
					jsonObject.put("FOOTERVALUE", "BCA Agreement");
				} else {
					jsonObject.put("FOOTERVALUE", "Core Value Pledge Agreement");
				}
				JSONObject pdf = getPDFDoc(jsonObject);
				if (pdf.getString("status").equalsIgnoreCase("SUCCESS")) {
					// decode base64 data from nodejs module and write to file
					byte[] pdfByte = org.apache.commons.codec.binary.Base64.decodeBase64(pdf.getString("PDFDATA"));

					PDDocument pddoc = PDDocument.load(pdfByte);
					PDDocumentInformation pddocinfo = pddoc.getDocumentInformation();
					pddocinfo.setTitle(docInfo.getString("docName"));
					pddoc.save(new FileOutputStream(filePath));
					pddoc.close();
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "PDF Creation success");
					jsRes.put("PDFPath", filePath);

				} else {
					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", "PDF Node Js Failed " + pdf.getString("statusDetails"));
					return jsRes;
				}

			} else {
				filePath = Properties_Loader.getHTMLPDF_FILE_PATH() + "/" + docInfo.getString("fileName");
				jsRes.put("status", "SUCCESS");
				jsRes.put("statusDetails", "PDF already exixts");
				jsRes.put("PDFPath", filePath);
			}

			JSONObject preSignRes = new JSONObject();
			boolean isPreSign = false;
			// 0-Pre Sign, 1-Aadhaar Sign, 2-Post Sign
			java.util.List<Doc_Sign_Config> docSignConfig = PDS.getDocSignConfig(docCode, "0", 3);
			ListIterator<Doc_Sign_Config> iterator = docSignConfig.listIterator();
			while (iterator.hasNext()) {
				isPreSign = true;
				Doc_Sign_Config doc_Sign_Config = (Doc_Sign_Config) iterator.next();

				// making pre sign the document if specified
				if (doc_Sign_Config.getDSC_TOKEN_ID() != null) {
					preSignRes = makePreSign(doc_Sign_Config, filePath, employeeDetails);
				}
			}
			if (preSignRes.has("status")) {
				if (preSignRes.getString("status").equalsIgnoreCase("SUCCESS") && isPreSign) {
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "PDF generation success");
					jsRes.put("PDFPath", filePath);
				} else {
					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", "Pre Sign failed " + preSignRes.getString("statusDetails"));
				}
			}

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return jsRes;

	}

	@SuppressWarnings("null")
	private static JSONObject getPDFDoc(JSONObject pdfData) {
//		System.out.println("pdfData:"+pdfData);
		JSONObject jsRes = null;
		try {

			Client client = Client.create();
			WebResource resource = client.resource(Properties_Loader.NODEJSPDF_URL);

			ClientResponse clientResponse = resource.path("/integra/generatePdf").type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, pdfData.toString());

			if (clientResponse.getStatus() == 200) {
				jsRes = new JSONObject(clientResponse.getEntity(String.class));
				jsRes.put("status", "SUCCESS");
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "PDF creator Node Js Failed " + clientResponse.getStatus());
			}

		} catch (Exception e) {
			try {
				jsRes = new JSONObject();
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return jsRes;
	}

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

				JSONObject tokenStatus = checkTokenStatus(dsc_Token_Config);
				if (tokenStatus.getString("status").equalsIgnoreCase("SUCCESS")) {
					tokenResponse = getDSCSignExternalToken(dsc_Token_Config, filePath, docSignConfig, employeeDetails);
				} else {
					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", tokenStatus.getString("statusDetails"));
					return jsRes;
				}
			}

			if (tokenResponse.getString("status").equalsIgnoreCase("SUCCESS")) {
				jsRes.put("status", "SUCCESS");
				jsRes.put("statusDetails", "Token Pre Sign Success");
				jsRes.put("PDFPath", filePath);
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

	public static JSONObject checkTokenStatus(Dsc_Token_Config dscTokenConfig) {
//		System.out.println("Inside checkTokenStatus... ");
		JSONObject jsRes = new JSONObject();
		try {
			JSONObject dscToken = new JSONObject(dscTokenConfig.getDSC_TOKEN_INFO());
			JSONObject tokenInputs = new JSONObject();
//			tokenInputs.put("password", dscToken.getString("password"));
			tokenInputs.put("configPath", dscToken.getString("configPath"));
			tokenInputs.put("alias", dscToken.getString("alias"));

			Client client = Client.create();
			WebResource resource = client
					.resource(dscTokenConfig.getEXTERNAL_IP() + ":" + dscTokenConfig.getEXTERNAL_PORT() + "/");

			ClientResponse clientResponse = resource.path("TOKENSIGN/checkTokenExistence")
					.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, tokenInputs.toString());

			if (clientResponse.getStatus() == 200) {
				JSONObject tokenRes = new JSONObject(clientResponse.getEntity(String.class));
				if (("SUCCESS").equalsIgnoreCase(tokenRes.getString("status"))) {
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "E-Token present");
				} else {
					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", tokenRes.getString("statusDetails"));
				}
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "E-Token status" + clientResponse.getStatus());
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				jsRes = new JSONObject();
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return jsRes;
	}

	public static JSONObject getDSCSignExternalToken(Dsc_Token_Config dscTokenConfig, String filePath,
			Doc_Sign_Config doc_Sign_Config, Employee_Master employeeDetails) {

//		System.out.println("Inside getDSCSignExternalToken...");
		String path = null;
		String renamedSignedFilePath = null;
		JSONObject jsRes = new JSONObject();
		ExternalSigningSupport externalSigning = null;
		PDDocument pddoc = null;

		try {
			JSONObject dscToken = new JSONObject(dscTokenConfig.getDSC_TOKEN_INFO());
			JSONObject tokenInputs = new JSONObject();
//			tokenInputs.put("password", dscToken.getString("password"));
			tokenInputs.put("configPath", dscToken.getString("configPath"));
			tokenInputs.put("alias", dscToken.getString("alias"));
			if (doc_Sign_Config.getSIGN_DISPLAY_INFO() != null) {
				tokenInputs.put("signDisplayInfo", doc_Sign_Config.getSIGN_DISPLAY_INFO());
			} else {
				tokenInputs.put("signDisplayInfo", dscTokenConfig.getSIGN_DISPLAY_INFO());
			}
			tokenInputs.put("signCoordinates", doc_Sign_Config.getSIGN_INFO());
			tokenInputs.put("mid", employeeDetails.getCompositeKey().getEMP_ID());
			tokenInputs.put("company", employeeDetails.getCompositeKey().getEMP_COMPANY());
			tokenInputs.put("group", employeeDetails.getCompositeKey().getEMP_GROUP());
			tokenInputs.put("name", employeeDetails.getEMP_NAME());
//			tokenInputs.put("imgpath", ""); // Enable this for background stamp image

//			File originalFile = new File(filePath);
//			String encodedBase64 = null;
//			try {
//				FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
//				byte[] bytes = new byte[(int) originalFile.length()];
//				fileInputStreamReader.read(bytes);
//				fileInputStreamReader.close();
//				encodedBase64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(bytes));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

			// Formatting the sign display info
			String displayInfo = PDFUtility.getDisMSGFormat(new JSONArray(doc_Sign_Config.getSIGN_DISPLAY_INFO()));

			//
			JSONObject hash = PDFUtility.getHash(filePath, displayInfo, tokenInputs, employeeDetails);
			tokenInputs.put("docHash", hash.getString("docHash"));

			Client client = Client.create();
			WebResource resource = client
					.resource(dscTokenConfig.getEXTERNAL_IP() + ":" + dscTokenConfig.getEXTERNAL_PORT() + "/");
			ClientResponse clientResponse = resource.path("TOKENSIGN/getDSCTokenSign").type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, tokenInputs.toString());

			if (clientResponse.getStatus() == 200) {
				String res = clientResponse.getEntity(String.class);
				JSONObject tokenRes = new JSONObject(res);
				if (("SUCCESS").equalsIgnoreCase(tokenRes.getString("status"))) {
					byte[] pdfByte = org.apache.commons.codec.binary.Base64.decodeBase64(tokenRes.getString("pdfdata"));
					String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					File file = new File(Properties_Loader.SIGNED_FILES_DIRECTORY + "/SIGNED/BCAA/" + timeStamp + "/"
							+ employeeDetails.getCompositeKey().getEMP_ID() + ".pdf");
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					externalSigning = (ExternalSigningSupport) hash.get("sap");
					pddoc = (PDDocument) hash.get("pddoc");
					externalSigning.setSignature(pdfByte);
					pddoc.close();
//					OutputStream out = new FileOutputStream(file);
//					out.write(pdfByte);
//					out.flush();
//					out.close();

					path = file.getAbsolutePath();
					File prevSignedFile = new File(filePath);
					File curSignedFile = new File(path);
					// checking for corrupted files
					if (curSignedFile.length() > prevSignedFile.length()) {
						// deleting the old signed file
						prevSignedFile.delete();
						File RenameSignedFile = new File(
								curSignedFile.getParent() + File.separator + prevSignedFile.getName());
						curSignedFile.renameTo(RenameSignedFile);
						renamedSignedFilePath = RenameSignedFile.getAbsolutePath();
						jsRes.put("status", "SUCCESS");
						jsRes.put("statusDetails", "Signing Existing file from external Token is success");
						jsRes.put("PDFPath", renamedSignedFilePath);
						jsRes.put("signerName", tokenRes.getString("signerName"));
					} else {
						jsRes.put("status", "FAILURE");
						jsRes.put("statusDetails", "File Corrupted Please try again");
					}
				} else {

					jsRes.put("status", "FAILURE");
					jsRes.put("statusDetails", "Pre Sign from external Token is Success failed " + tokenRes);
				}

			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails",
						"Pre Sign from external Token is Success failed " + clientResponse.getStatus());
			}

		} catch (Exception e) {
			try {
				e.printStackTrace();
				jsRes = new JSONObject();
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "Inside getDSCSignExternalToken : " + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return jsRes;
	}

	public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

		File file = new File(fileName);
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}
}
