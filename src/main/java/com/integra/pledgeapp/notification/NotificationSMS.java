package com.integra.pledgeapp.notification;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.utils.URIBuilder;

import com.integra.pledgeapp.utilities.Properties_Loader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class NotificationSMS {
	private static Client client;
	private static WebResource webResource;
	static String username = null;
	static String password = null;
	static String smsprovider = null;
	static String smssender = null;
	static String smssource=null;
	static String smsproviderurl=null;
	
	static {
		SMSConfig();
	}
	
	private static void SMSConfig()
	{
//		 username = Properties_Loader.SMSUSERNAME;
//		 password = Properties_Loader.SMSPASSWORD;
//		 smsprovider = Properties_Loader.SMSPROVIDER;
//		 smssender = Properties_Loader.SMSSENDER;
		 
		  username = new String(Base64.getDecoder().decode(Properties_Loader.SMSUSERNAME.getBytes()));
			// "username=INTEGRADSB"; OR username=Integradoc

			 password = new String(Base64.getDecoder().decode( Properties_Loader.SMSPASSWORD.getBytes()));
			// "&apikey=55b4cf79-837a-4e4e-99d5-f1ed28596201"; OR &password=Jtpl@123
//			 
			  smsproviderurl = Properties_Loader.SMSPROVIDERURL;
			// "http://login.aquasms.com/sendSMS?"; OR
			// http://sms6.rmlconnect.net:8080/bulksms/bulksms?

			 smssender = Properties_Loader.SMSSENDER;
			// "&sendername=INTJSN&smstype=TRANS&numbers="; OR &type=0&dlr=1&destination=
			 smssource = Properties_Loader.SMSSOURCE;
			// &source=DOCUEX&message=
			 smsprovider = Properties_Loader.SMSPROVIDER; 
		 
	}

	public static String sendSMS(String msg, String phoneno) {
		SMSWebResource(msg, phoneno);
		String resp = null;
		try {
			// This clientResponse is for Production AquaSMS
			ClientResponse response = webResource.path("").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			resp = response.getEntity(String.class);
		} catch (Exception ex) {
			System.out.println("SMS-SERVER  Calling  Exception :" + ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("SMS-SERVER Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	public static void SMSWebResource(String msg, String phoneno) {

		try {
			

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
			String msgs="";
			if(smsprovider.equalsIgnoreCase("AQUA")) {
			// This is for Production AquaSMS
			 msgs = smsproviderurl + username + password + smssender + phoneno + "&message=" + msg;

			}
			else if(smsprovider.equalsIgnoreCase("ROUTEMOBILES")) {
				 msgs=smsproviderurl + username +password + smssender + phoneno + smssource + msg;

			}
			System.out.println(msgs);
			URIBuilder ub = new URIBuilder(msgs);
			String url = ub.toString();

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
			webResource = client.resource(url);
//			System.out.println(webResource);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("SMS Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (KeyManagementException ex) {
			System.out.println("SMS Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("SMS Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("SMS Sending failed for " + phoneno);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
