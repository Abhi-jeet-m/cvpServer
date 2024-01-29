package com.integra.pledgeapp.utilities;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.MultipartConfigElement;
import javax.net.ssl.HostnameVerifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class MySignClient {

	private static Client client;
	private static WebResource webResource;

	public static String fileDownload(String filename, String docCode) {

		initilizeWebResource();
		String path = null;
		try {
			ClientResponse response = webResource.path("/download/" + filename).accept(MediaType.TEXT_PLAIN)
					.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			InputStream is = response.getEntity(InputStream.class);
//			File fi = new File(Properties_Loader.SIGNED_FILES_DIRECTORY + File.separator + filename);

			String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			File file = new File(
					Properties_Loader.SIGNED_FILES_DIRECTORY + "/SIGNED/BCAA/" + timeStamp + "/" + filename);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			int read = 0;
			byte[] bytes = new byte[1024];
			OutputStream out = new FileOutputStream(file);
			while ((read = is.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			path = file.getAbsolutePath();
		} catch (Exception ex) {
			System.out.println("MYSIGN DOWNLOAD Calling  Exception :" + ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("MYSIGN DOWNLOAD Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}
		return path;
	}

     //callBackURL-true ,so that jSign will call to bcportal callbackurl after signing
	// Bcaadhar sign - callbaclURL-true
	// BCwitness sign(OTP Based Signing)- callbaclURL-true
	//filePath,authtoken,signDisplayInfo:Witness signing stamp,imgName,docCode,signInfo(JSONObject),userIP,callbackURL(boolean),signMode(int)
	public static String sendFile(String filepath, String authtoken, String signDisplayInfo, String imgName,
			String docCode, JSONObject signInfo, String userIP, boolean callBackURL, String signMode, String corpUser) {
		JSONObject resp = new JSONObject();
		
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		try {
			JSONObject JS = new JSONObject();
			JSONObject Sign = new JSONObject();
			Sign.put("totWidth", "595");
			Sign.put("totHeight", "842");
			Sign.put("width", signInfo.getString("width"));
			Sign.put("height", signInfo.getString("height"));
			Sign.put("x", signInfo.getString("x"));
			Sign.put("y", signInfo.getString("y"));
			JS.put("signPage", signInfo.getString("signPage"));
			JS.put("pages", signInfo.getString("pages"));
			JS.put("sc", "Y");
			JS.put("docType", "PDF");
			JS.put("authToken", authtoken);
			JS.put("signCoordinates", Sign);
			JS.put("signInfo", signDisplayInfo);
			JS.put("signUser", "Corp");
			JS.put("CORP_ID", "INT01");
			JS.put("CORP_USER",corpUser );
			JS.put("imgpath",  "/getLogoImage/" + imgName + ".jpg");
//			JS.put("imgpath",  imgName + ".jpg");
			JS.put("userIP", userIP);
			JS.put("signMode", signMode);
			if (signMode.equals("4")) {
				JSONObject inputs = new JSONObject(filepath);
				JS.put("docId", inputs.getString("docID"));
				JS.put("mobilenumotp", inputs.getString("mobileOTP"));
				JS.put("handSignImg", inputs.getString("handSignImg"));
				JS.put("mobileNo",inputs.getString("mobileNo"));
				JS.put("signerName",inputs.getString("signerName"));
			}
			if (callBackURL) {
				JS.put("callBackURL", Properties_Loader.CALLBACK_URL + "/jsignResponse");
			}
			if (docCode.equalsIgnoreCase("CVP")) {
				JS.put("imageLogo", getBase64LogoImage(Properties_Loader.IMAGEPATH + imgName + ".jpg"));
			}
//			Resource file=null;
			if (signMode.equals("4")) {
				JSONObject inputs = new JSONObject(filepath);
				bodyMap.add("file", getEmpBasedPledgeFile(inputs.getString("filePath")));
			} else {

				bodyMap.add("file", getEmpBasedPledgeFile(filepath));
			}
			bodyMap.add("inputDetails", JS.toString());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

			RestTemplate restTemplate = new RestTemplate();

			// to disable ssl hostname verifier
			restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
				@Override
				protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
					if (connection instanceof HttpsURLConnection) {
						((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
					}
					super.prepareConnection(connection, httpMethod);
				}
			});
			ResponseEntity<String> response = restTemplate.exchange(Properties_Loader.MYSIGN_URL + "/getSignedDoc",
					HttpMethod.POST, requestEntity, String.class);
			resp = new JSONObject(response.getBody());
//			System.out.println("resp.toStringValueeeeeee() :"+resp.toString());
		} catch (IOException | JSONException e) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
			e.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}
//System.out.println("resp.toStringValuee() :"+resp.toString());
		return resp.toString();
	}

	public static String getBase64LogoImage(String filepath) {
		File file = new File(filepath);
		String encodedBase64 = null;
		try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			encodedBase64 = new String(Base64.encodeBase64(bytes));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encodedBase64;
	}

	public static Resource getEmpBasedPledgeFile(String filepath) throws IOException {
		File file = new File(filepath);
		return new FileSystemResource(file);
	}

	// public static String validateLogin(String userIP) {
	public static JSONObject validateLogin(String userIP) {
		initilizeWebResource();
//		String authtoken = null;
		JSONObject validateLoginResp = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", Properties_Loader.MYSIGN_USERNAME);
			jsonObject.put("password", Properties_Loader.MYSIGN_PASSWORD);
			jsonObject.put("userIP", userIP);
			System.out.println("inputs::: "+jsonObject);
			ClientResponse response = webResource.path("/loginValidate").accept(MediaType.TEXT_PLAIN)
					.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonObject.toString());
			String output = response.getEntity(String.class);
			JSONObject JS = new JSONObject(output);
			System.out.println(JS);
			// authtoken = JS.getString("authToken");
			validateLoginResp.put("authToken", JS.getString("authToken"));
			validateLoginResp.put("roleId", JS.getString("roleId"));
			validateLoginResp.put("loginname", JS.getString("name"));

		} catch (JSONException ex) {
			System.out.println("MYSIGN LOGIN Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("MYSIGN LOGIN Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("MYSIGN LOGIN Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}

		return validateLoginResp;
	}

	public static void initilizeWebResource() {

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
			client.setFollowRedirects(true);
			webResource = client.resource(Properties_Loader.MYSIGN_URL);
			// System.out.println("wbres" + webResource);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (KeyManagementException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Throwable ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
// sending request for jSign to generate OTP for witness Signing
	public static String reqOTPforSigning(String filepath, JSONObject validateLoginResp, String imgName, String docCode,
			JSONObject signInfo, String userIP, boolean callBackURL, String signMode) {
		JSONObject resp = new JSONObject();
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		try {
			JSONObject JS = new JSONObject();
			JSONObject signerInfo = new JSONObject();
			JSONObject signerInfodata = signInfo;
			signerInfo.put("signerName", signerInfodata.getString("signerName"));
			signerInfo.put("signerMobileNo", signerInfodata.getString("signerMobile"));
			signerInfo.put("signingPurpose", "");
			JS.put("roleId", validateLoginResp.getString("roleId"));
			JS.put("authToken", validateLoginResp.getString("authToken"));
			JS.put("loginname", validateLoginResp.getString("loginname"));
			JS.put("signerInfo", signerInfo);
			JS.put("signUser", "Corp");
			JS.put("selectedMode", signMode);
			if (callBackURL) {
				JS.put("callBackURL", Properties_Loader.CALLBACK_URL + "/jsignResponse");
			}
			if (docCode.equalsIgnoreCase("CVP")) {
				JS.put("imageLogo", getBase64LogoImage(Properties_Loader.IMAGEPATH + imgName + ".jpg"));
			}
			bodyMap.add("file", getEmpBasedPledgeFile(filepath));
			bodyMap.add("inputDetails", JS.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
			RestTemplate restTemplate = new RestTemplate();
			// to disable ssl hostname verifier
			restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
				@Override
				protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
					if (connection instanceof HttpsURLConnection) {
						((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
					}
					super.prepareConnection(connection, httpMethod);
				}
			});
			ResponseEntity<String> response = restTemplate.exchange(Properties_Loader.MYSIGN_URL + "/generateOTP",
					HttpMethod.POST, requestEntity, String.class);
			resp = new JSONObject(response.getBody());
		} catch (IOException | JSONException e) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
			e.printStackTrace();
		} catch (Throwable ex) {
			System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + ex.getMessage());
			ex.printStackTrace();
		}

		return resp.toString();
	}
	
	// sending request for jSign to generate OTP for witness Signing
		public static String resendOTPforSigning(String filepath, JSONObject validateLoginResp, String imgName, String docCode,
				JSONObject signInfo, String userIP, boolean callBackURL, String signMode,String docID) {
			JSONObject resp = new JSONObject();
			MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
			try {
				JSONObject JS = new JSONObject();
				JSONObject signerInfo = new JSONObject();
				JSONObject signerInfodata = signInfo;
				signerInfo.put("signerName", signerInfodata.getString("signerName"));
				signerInfo.put("signerMobileNo", signerInfodata.getString("signerMobile"));
				signerInfo.put("signingPurpose", "");
				JS.put("roleId", validateLoginResp.getString("roleId"));
				JS.put("authToken", validateLoginResp.getString("authToken"));
				JS.put("loginname", validateLoginResp.getString("loginname"));
				JS.put("signerInfo", signerInfo);
				JS.put("docID", docID);
				JS.put("signUser", "Corp");
				JS.put("selectedMode", signMode);
				if (callBackURL) {
					JS.put("callBackURL", Properties_Loader.CALLBACK_URL + "/jsignResponse");
				}
				if (docCode.equalsIgnoreCase("CVP")) {
					JS.put("imageLogo", getBase64LogoImage(Properties_Loader.IMAGEPATH + imgName + ".jpg"));
				}
				
				ClientResponse response = webResource.path("/resendEsignOtp").accept(MediaType.APPLICATION_JSON)
						.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, JS.toString());
				String output = response.getEntity(String.class);
		        resp = new JSONObject(output);
			} catch ( JSONException e) {
				System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + e.getMessage());
				e.printStackTrace();
			} catch (Throwable ex) {
				System.out.println("MYSIGN FILEUPLOAD Calling Exception :" + ex.getMessage());
				ex.printStackTrace();
			}

			return resp.toString();
		}

	

		
	
}
