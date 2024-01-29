package com.integra.pledgeapp.utilities;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import com.integra.pledgeapp.beans.Employee_Master;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class OtpHandler {
	private static Client client;
	private static WebResource webResource;
	public static JSONObject otpStore = new JSONObject();

	public static void addOtp(String otp, String empid) {
		try {
			otpStore.put(empid, otp);
			OtpCleaner OTC = new OtpCleaner();
			OTC.empid = empid;
			OTC.start();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static void removeOtp(String empid) {
		if (otpStore.has(empid)) {
			otpStore.remove(empid);
		}
	}

	public static boolean validateOtp(String otp, String empid) {
		boolean resp = false;
		if (otpStore.has(empid)) {
			try {
				if (otpStore.getString(empid).equalsIgnoreCase(otp)) {
					resp = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return resp;
	}

	public String sendOTPphone(String otp, String inputData) {
		String phoneno=null;
		String eventType="";
		// only in profile update upadteType key will be present.
		if(inputData.contains("updateType"))
		{
			JSONObject inputs;
			try {
				
				inputs = new JSONObject(inputData);
				phoneno=inputs.getString("mobAadhar");
				eventType=inputs.getString("updateType");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			phoneno=inputData;
		}
		initilizeWebResource(otp, phoneno,eventType);
		String resp = null;
		try {
			// This clientResponse is for UAT SMSFresh
//			ClientResponse response = webResource.path("").accept(MediaType.TEXT_PLAIN).type(MediaType.TEXT_PLAIN)
//					.get(ClientResponse.class);

			// This clientResponse is for Production AquaSMS
			ClientResponse response = webResource.path("").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			resp = response.getEntity(String.class);
//			System.out.println(resp);
		} catch (Exception ex) {
			System.out.println("SMS-SERVER  Calling  Exception for :" + phoneno);
			System.out.println("SMS-SERVER  Calling  Exception :" + ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("SMS-SERVER  Calling  Exception for :" + phoneno);
			System.out.println("SMS-SERVER Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	public static void initilizeWebResource(String otp, String phoneno,String eventType) {

		try {

//			 username = Properties_Loader.SMSUSERNAME;
//			 password = Properties_Loader.SMSPASSWORD;
			 String username = new String(Base64.getDecoder().decode(Properties_Loader.SMSUSERNAME.getBytes()));
			// "username=INTEGRADSB"; OR username=Integradoc

			String password = new String(Base64.getDecoder().decode( Properties_Loader.SMSPASSWORD.getBytes()));
			// "&apikey=55b4cf79-837a-4e4e-99d5-f1ed28596201"; OR &password=Jtpl@123
//			 
			String  smsproviderurl = Properties_Loader.SMSPROVIDERURL;
			// "http://login.aquasms.com/sendSMS?"; OR
			// http://sms6.rmlconnect.net:8080/bulksms/bulksms?

			String smssender = Properties_Loader.SMSSENDER;
			// "&sendername=INTJSN&smstype=TRANS&numbers="; OR &type=0&dlr=1&destination=
			String smssource = Properties_Loader.SMSSOURCE;
			// &source=DOCUEX&message=
			String smsprovider = Properties_Loader.SMSPROVIDER; 

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// do nothing and blindly accept the certificate
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// do nothing and blindly accept the server
				}
			} };
			
			
			String url="";
			if(smsprovider.equalsIgnoreCase("AQUA")) {
				
			
				String msg ="Your%20OTP%20for%20Verification%20with%20Mobile%20No.%20ref%20%20%20is%20" + otp
						+ "%20Integra";
			// This is for Production AquaSMS
//				System.out.println("inside aqua");
				url = smsproviderurl + username + password + smssender + phoneno + "&message=" + msg;
			//System.out.println(msgs);
			}else if(smsprovider.equalsIgnoreCase("ROUTEMOBILES")) {
//				System.out.println("outside aqua");
				String msg="";
	             if(eventType.equalsIgnoreCase("01")||eventType.equalsIgnoreCase("11")) {
	             msg="Your%20OTP%20for%20mobile%20number%20change%20is%20"+otp+".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20BC%20Cell%0AIntegra";

				}else {
				 msg="Your%20OTP%20for%20login%20is%20"+otp+".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20Admin%0AIntegra";
				}
				 
				 url=smsproviderurl + username +password + smssender + phoneno + smssource + msg;
			//	System.out.println("route mobiles "+msgs);
			}
			// This is for UAT testing SMSFresh
//			String msg = Properties_Loader.SMSPROVIDER + username + "&pass=" + password + Properties_Loader.SMSSENDER
//					+ phoneno + "&text=OTP%20for%20proceeding%20with%20login%20is%20" + otp
//					+ ".%20Do%20not%20share%20with%20anyone.";

			// This is for Production AquaSMS
			// for otp based login
//			String msg= Properties_Loader.SMSPROVIDER + username + password + Properties_Loader.SMSSENDER + phoneno
//					+ "&message=OTP%20for%20proceeding%20with%20login%20is%20" + otp
//					+ ".%20Do%20not%20share%20with%20anyone.%20Integra";
			// for verifiying mobile number
			

			// System.out.println(msg);
			URIBuilder ub = new URIBuilder(url);
			String uri = ub.toString();

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			ClientConfig config = new DefaultClientConfig();
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(new HostnameVerifier() {
						@Override
						public boolean verify(String s, SSLSession sslSession) {
							return true;
						}
					}, context));
			client = Client.create(config);
			client = Client.create();
			client.setFollowRedirects(true);
			webResource = client.resource(uri);
//			System.out.println(webResource);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("OTP Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (KeyManagementException ex) {
			System.out.println("OTP Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("OTP Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("OTP Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void emailOtp(String email, String otp, Employee_Master empData) {
//		System.out.println("In emailOtp Service ");
		try {
			String content = "Dear  " + empData.getEMP_NAME() + ",\n\r\n"
					+ "The One Time Password (OTP) for your login is " + otp + "." + "\r\n" + "\r\n"
					+ "This OTP is valid for 30 minutes or 1 successful attempt whichever is earlier. Please do not share this One Time Password with anyone.\n"
					+ "In case you have not requested for OTP, please contact office to report the issue, and remove this mail.\n"
					+ "\nRegards,\r\n" + "Admin";

			MailHandler.sendTextMail(content, "OTP for Login", empData.getEMP_EMAIL());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
