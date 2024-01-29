package com.integra.pledgeapp.utilities;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.integra.pledgeapp.beans.Template_Master;
import com.integra.pledgeapp.core.PledgeAppDAOImpl;
import com.integra.pledgeapp.utilities.SMSThread;


public class Properties_Loader {

	public static String PLEDGE_FILE_PATH;
	public static String SIGNED_FILES_DIRECTORY;
	public static String UNSIGNED_FILES_DIRECTORY;
	public static String MYSIGN_URL;
	public static String MYSIGN_USERNAME;
	public static String MYSIGN_PASSWORD;
	public static String IMAGEPATH;
	public static String CONFIGCODE;
	public static String UPLOADPATH;
	public static String SMSUSERNAME;
	public static String SMSPASSWORD;
	public static String SMSPROVIDER;
	public static String SMSSENDER;
	public static String SMSPROVIDERURL;
	public static String SMSSOURCE;
	public static String OTPTIME;
	public static String NODEJSPDF_URL;
	public static String HTMLPDF_FILE_PATH;
	public static String CALLBACK_URL;
	public static String CLIENT_RES_URL;

	public static String MAILSMTPAUTH;
	public static String MAILSMTPSTARTTLSENABLE;
	public static String MAILSMTPHOST;
	public static String MAILSMTPPORT;
	public static String MAILPASSWORD;
	public static String MAILSENDER;
	public static String REJECT_SINGING;
	public static String LOGIN_MODE;

	

	
	public static String getLOGIN_MODE() {
		return LOGIN_MODE;
	}

	public static void setLOGIN_MODE(String lOGIN_MODE) {
		LOGIN_MODE = lOGIN_MODE;
	}

	public static String getREJECT_SINGING() {
		return REJECT_SINGING;
	}

	public static void setREJECT_SINGING(String rEJECT_SINGING) {
		REJECT_SINGING = rEJECT_SINGING;
	}

	public static String getMAILSENDER() {
		return MAILSENDER;
	}

	public static void setMAILSENDER(String mAILSENDER) {
		MAILSENDER = mAILSENDER;
	}

	public static String getMAILSMTPAUTH() {
		return MAILSMTPAUTH;
	}

	public static void setMAILSMTPAUTH(String mAILSMTPAUTH) {
		MAILSMTPAUTH = mAILSMTPAUTH;
	}

	public static String getMAILSMTPSTARTTLSENABLE() {
		return MAILSMTPSTARTTLSENABLE;
	}

	public static void setMAILSMTPSTARTTLSENABLE(String mAILSMTPSTARTTLSENABLE) {
		MAILSMTPSTARTTLSENABLE = mAILSMTPSTARTTLSENABLE;
	}

	public static String getMAILSMTPHOST() {
		return MAILSMTPHOST;
	}

	public static void setMAILSMTPHOST(String mAILSMTPHOST) {
		MAILSMTPHOST = mAILSMTPHOST;
	}

	public static String getMAILSMTPPORT() {
		return MAILSMTPPORT;
	}

	public static void setMAILSMTPPORT(String mAILSMTPPORT) {
		MAILSMTPPORT = mAILSMTPPORT;
	}

	public static String getMAILPASSWORD() {
		return MAILPASSWORD;
	}

	public static void setMAILPASSWORD(String mAILPASSWORD) {
		MAILPASSWORD = mAILPASSWORD;
	}

	public static String getOTPTIME() {
		return OTPTIME;
	}

	public static void setOTPTIME(String oTPTIME) {
		OTPTIME = oTPTIME;
	}

	public static String getSMSUSERNAME() {
		return SMSUSERNAME;
	}

	public static void setSMSUSERNAME(String sMSUSERNAME) {
		SMSUSERNAME = sMSUSERNAME;
	}

	public static String getSMSPASSWORD() {
		return SMSPASSWORD;
	}

	public static void setSMSPASSWORD(String sMSPASSWORD) {
		SMSPASSWORD = sMSPASSWORD;
	}

	public static String getUPLOADPATH() {
		return UPLOADPATH;
	}

	public static void setUPLOADPATH(String uPLOADPATH) {
		UPLOADPATH = uPLOADPATH;
	}

	public static String getCONFIGCODE() {
		return CONFIGCODE;
	}

	public static void setCONFIGCODE(String cONFIGCODE) {
		CONFIGCODE = cONFIGCODE;
	}

	public static String getIMAGEPATH() {
		return IMAGEPATH;
	}

	public static void setIMAGEPATH(String iMAGEPATH) {
		IMAGEPATH = iMAGEPATH;
	}

	public static String getMYSIGN_USERNAME() {
		return MYSIGN_USERNAME;
	}

	public static void setMYSIGN_USERNAME(String mYSIGN_USERNAME) {
		MYSIGN_USERNAME = mYSIGN_USERNAME;
	}

	public static String getMYSIGN_PASSWORD() {
		return MYSIGN_PASSWORD;
	}

	public static void setMYSIGN_PASSWORD(String mYSIGN_PASSWORD) {
		MYSIGN_PASSWORD = mYSIGN_PASSWORD;
	}

	public static String getMYSIGN_URL() {
		return MYSIGN_URL;
	}

	public static void setMYSIGN_URL(String mYSIGN_URL) {
		MYSIGN_URL = mYSIGN_URL;
	}

	public static String getUNSIGNED_FILES_DIRECTORY() {
		return UNSIGNED_FILES_DIRECTORY;
	}

	public static void setUNSIGNED_FILES_DIRECTORY(String uNSIGNED_FILES_DIRECTORY) {
		UNSIGNED_FILES_DIRECTORY = uNSIGNED_FILES_DIRECTORY;
	}

	public static String getPLEDGE_FILE_PATH() {
		return PLEDGE_FILE_PATH;
	}

	public static void setPLEDGE_FILE_PATH(String pLEDGE_FILE_PATH) {
		PLEDGE_FILE_PATH = pLEDGE_FILE_PATH;
	}

	public static String getSIGNED_FILES_DIRECTORY() {
		return SIGNED_FILES_DIRECTORY;
	}

	public static void setSIGNED_FILES_DIRECTORY(String sIGNED_FILES_DIRECTORY) {
		SIGNED_FILES_DIRECTORY = sIGNED_FILES_DIRECTORY;
	}
	
	public static String getCALLBACK_URL() {
		return CALLBACK_URL;
	}

	public static void setCALLBACK_URL(String cALLBACK_URL) {
		CALLBACK_URL = cALLBACK_URL;
	}
	

	
	public static String getSMSPROVIDERURL() {
		return SMSPROVIDERURL;
	}

	public static void setSMSPROVIDERURL(String sMSPROVIDERURL) {
		SMSPROVIDERURL = sMSPROVIDERURL;
	}
	

	public static String getSMSSOURCE() {
		return SMSSOURCE;
	}

	public static void setSMSSOURCE(String sMSSOURCE) {
		SMSSOURCE = sMSSOURCE;
	}

	public static void loadProperties() {

		setPLEDGE_FILE_PATH(getValueFromPropetyFile("PLEDGE_FILE_PATH"));
		setSIGNED_FILES_DIRECTORY(getValueFromPropetyFile("SIGNED_FILES_DIRECTORY"));
		setUNSIGNED_FILES_DIRECTORY(getValueFromPropetyFile("UNSIGNED_FILES_DIRECTORY"));
		setMYSIGN_PASSWORD(getValueFromPropetyFile("MYSIGN_PASSWORD"));
		setMYSIGN_USERNAME(getValueFromPropetyFile("MYSIGN_USERNAME"));
		setMYSIGN_URL(getValueFromPropetyFile("MYSIGN_URL"));
		setIMAGEPATH(getValueFromPropetyFile("IMAGE_PATH"));
		setCONFIGCODE(getValueFromPropetyFile("CONFIGCODE"));
		setUPLOADPATH(getValueFromPropetyFile("UPLOADPATH"));
		setOTPTIME(getValueFromPropetyFile("OTPTIME"));
		setHTMLPDF_FILE_PATH(getValueFromPropetyFile("HTMLPDF_FILE_PATH"));
		setNODEJSPDF_URL(getValueFromPropetyFile("NODEJSPDF_URL"));
		setCALLBACK_URL(getValueFromPropetyFile("CALLBACK_URL"));
		setCLIENT_RES_URL(getValueFromPropetyFile("CLIENT_RES_URL"));
		setREJECT_SINGING(getValueFromPropetyFile("REJECT_SINGING"));
		setLOGIN_MODE(getValueFromPropetyFile("LOGIN_MODE"));
	
		
		//SMS
		setSMSUSERNAME(getValueFromPropetyFile("sms.username"));
		setSMSPASSWORD(getValueFromPropetyFile("sms.password"));
		setSMSPROVIDER(getValueFromPropetyFile("sms.provider"));
		setSMSSENDER(getValueFromPropetyFile("sms.sender"));
		setSMSPROVIDERURL(getValueFromPropetyFile("sms.provider.url"));
		setSMSSOURCE(getValueFromPropetyFile("sms.source"));
		
		//MAIL//
		setMAILPASSWORD(getValueFromPropetyFile("mail.password"));
		setMAILSMTPAUTH(getValueFromPropetyFile("mail.smtp.auth"));
		setMAILSMTPHOST(getValueFromPropetyFile("mail.smtp.host"));
		setMAILSMTPPORT(getValueFromPropetyFile("mail.smtp.port"));
		setMAILSMTPSTARTTLSENABLE(getValueFromPropetyFile("mail.smtp.starttls.enable"));
		setMAILSENDER(getValueFromPropetyFile("mail.sender"));
		
		Threads th = new Threads();
		Threads.sleepTime = Integer.parseInt(getOTPTIME());
		th.start();
	}

	public static String getValueFromPropetyFile(String StrLabel) {
		Properties props = null;
		String returnvalue = null;
		try {
			props = new Properties();

			// For Local Setup
			// props.load(Properties_Loader.class.getClassLoader().getResourceAsStream("pledgeValApp.properties"));

			// For Deployment
			props.load(new InputStreamReader(new FileInputStream("pledgeValApp.properties")));

			returnvalue = props.getProperty(StrLabel);
//			System.out.println(StrLabel + "::" + returnvalue);
		} catch (Exception ex) {
			System.out.println("[Properties_Loader][getValueFromPropetyFile][Exception]Key Missing: " + StrLabel + " "
					+ ex.getMessage());
		}
		System.out.println("return value from Properties_Loader"+returnvalue);
		return returnvalue;

	}
	
	public static List<Template_Master> getTemplate(){
		List<Template_Master> list=new ArrayList<Template_Master>();
		try {
			
			
			list=PledgeAppDAOImpl.getListofTemplates();
//			System.out.println(list);
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
		return list;
		
	}

	public static String getNODEJSPDF_URL() {
		return NODEJSPDF_URL;
	}

	public static void setNODEJSPDF_URL(String nODEJSPDF_URL) {
		NODEJSPDF_URL = nODEJSPDF_URL;
	}

	public static String getHTMLPDF_FILE_PATH() {
		return HTMLPDF_FILE_PATH;
	}

	public static void setHTMLPDF_FILE_PATH(String hTMLPDF_FILE_PATH) {
		HTMLPDF_FILE_PATH = hTMLPDF_FILE_PATH;
	}

	public static String getSMSPROVIDER() {
		return SMSPROVIDER;
	}

	public static void setSMSPROVIDER(String sMSPROVIDER) {
		SMSPROVIDER = sMSPROVIDER;
	}

	public static String getSMSSENDER() {
		return SMSSENDER;
	}

	public static void setSMSSENDER(String sMSSENDER) {
		SMSSENDER = sMSSENDER;
	}

	public static String getCLIENT_RES_URL() {
		return CLIENT_RES_URL;
	}

	public static void setCLIENT_RES_URL(String cLIENT_RES_URL) {
		CLIENT_RES_URL = cLIENT_RES_URL;
	}

}
