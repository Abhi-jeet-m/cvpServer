package com.integra.pledgeapp.token;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.support.StaticApplicationContext;
//import sun.security.*;
import java.security.Provider;
import java.security.Security;
import java.lang.reflect.Method;

public class PDFUSBTokenSign {

	static KeyStore ks = null;
	static PrivateKey pk = null;
	static Certificate[] chain = null;
	static BouncyCastleProvider bcp = null;
	// For windows
	//	static String pkcs11Config = "name=eToken\nlibrary=C:\\Windows\\System32\\eps2003csp11v2.dll";
	static String certName = null;
//	static String s = null;

	public static void presetup(String configPath, String password, String alias) {

		try {

//			ByteArrayInputStream pkcs11ConfigStream = new ByteArrayInputStream(configPath.getBytes());
//			@SuppressWarnings("restriction")
//			sun.security.pkcs11.SunPKCS11 providerPKCS11 = new sun.security.pkcs11.SunPKCS11(pkcs11ConfigStream);
//			java.security.Security.addProvider(providerPKCS11);
			//........................................................................
//			 Provider prototype = Security.getProvider("providerPKCS11");
//		    Class<?> providerPKCS11 = Class.forName("sun.security.pkcs11.SunPKCS11");
//		    Method configureMethod = providerPKCS11.getMethod("configure", String.class);
//		     configureMethod.invoke(prototype, pkcs11ConfigStream);
		     //......................................................................
		//	String configName = "/opt/bar/cfg/pkcs11.cfg";
			Provider providerPKCS11 = Security.getProvider("SunPKCS11");
			System.out.println("configgg before: "+providerPKCS11.isConfigured());
			providerPKCS11 = providerPKCS11.configure(configPath);
			System.out.println("configgg after: "+providerPKCS11.isConfigured());
			Security.addProvider(providerPKCS11);
			
		

			KeyStore keyStore = KeyStore.getInstance("PKCS11");

			keyStore.load(null, password.toCharArray());
			// Code to get the alias form token
			/*
			 * String alias = null; while (enumeration.hasMoreElements()) { alias =
			 * enumeration.nextElement(); System.out.println("alias name: " + alias); }
			 */

			Certificate certificate = keyStore.getCertificate(alias);
			// to get the subject from certificate and subject contents
			X500Name x500name = new JcaX509CertificateHolder((java.security.cert.X509Certificate) certificate)
					.getSubject();
			RDN cn = x500name.getRDNs(BCStyle.CN)[0];

			certName = IETFUtils.valueToString(cn.getFirst().getValue());

			KeyStore.PrivateKeyEntry entry = null;
			if (keyStore.isKeyEntry(alias)) {
				entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
			} else {
				throw new Exception(
						"Invalid alias name. No private key found with the given alias name in smart card keystore.");
			}

			pk = entry.getPrivateKey();
			chain = keyStore.getCertificateChain(alias);

			bcp = new BouncyCastleProvider();
			Security.insertProviderAt(bcp, 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getDisMSGFormat(JSONArray displayInfo) {
		String displayMsg = "";
		try {
			int length = displayInfo.length();
			Calendar currentDat = Calendar.getInstance();
			SimpleDateFormat apperenceDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date apperenceCurrentDate = currentDat.getTime();
			String apperenceStrCurrentDate = apperenceDateFormat.format(apperenceCurrentDate);
			
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject json = displayInfo.getJSONObject(i);
					String temp = json.getString("displayMsg");
					String res = "";
					if(temp.contains("$$cn$$")) {
						res = temp.replace("$$cn$$", certName);
						displayMsg += res;
					}
					else if(temp.contains("$$date$$")) {
						res = temp.replace("$$date$$", apperenceStrCurrentDate);
						displayMsg += res;
					}
					else {
						displayMsg += temp;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return displayMsg;
	}

	public static JSONObject addSignatureUsingUSBToken(String filePath, String inputImgPath,
			JSONObject signCoordinates, String configPath, String password, String alias, String displayInfo) throws JSONException {

		JSONObject jsRes = new JSONObject();
		
		presetup(configPath, password, alias);

		
		try{

			String signDisplayInfo = getDisMSGFormat(new JSONArray(displayInfo));
			
			PDDocument pdDocument = PDDocument.load(new File(filePath));
			String x = signCoordinates.getString("x").trim();
			String y = signCoordinates.getString("y").trim();
			String signPage = signCoordinates.getString("signPage").trim();
			String width = signCoordinates.getString("width").trim();
			String height = signCoordinates.getString("height").trim();
			PDSignature pds = null;
			int pageNum = 0;
			String[] signpages = null;

			File imgFile = new File(inputImgPath);

			PDAcroForm acroForm = pdDocument.getDocumentCatalog().getAcroForm();
			if (acroForm == null) {
				pdDocument.getDocumentCatalog().setAcroForm(acroForm = new PDAcroForm(pdDocument));
			}
			acroForm.setSignaturesExist(true);
			acroForm.setAppendOnly(true);
			acroForm.getCOSObject().setDirect(true);

			pds = new PDSignature();
			pds.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
			pds.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
			pds.setSignDate(Calendar.getInstance());

			PDPage pdpage = pdDocument.getPage(0);

			PDImageXObject pdImage = null;
			if (imgFile.exists()) {
				pdImage = PDImageXObject.createFromFileByContent(imgFile, pdDocument);
			}

			float totalheight = pdpage.getMediaBox().getHeight();
			float totalwidth = pdpage.getMediaBox().getWidth();
			PDRectangle rectangle = null;

			if ("L".equalsIgnoreCase(signPage)) {
				pageNum = pdDocument.getNumberOfPages() - 1;
			} else if ("F".equalsIgnoreCase(signPage)) {
				pageNum = 0;
			} else if ("P".equalsIgnoreCase(signPage)) {
				pageNum = -2; // -2 represents all mentioned pages in an array
				signPage = signCoordinates.getString("pages");
				signPage = signPage.substring(signPage.indexOf("[") + 1, signPage.indexOf("]"));
				signpages = signPage.split(",");
			} else if ("A".equalsIgnoreCase(signPage)) {
				pageNum = -1; // -1 represents all pages
			} else {
				pageNum = Integer.parseInt(signCoordinates.getString("signPage")) - 1;
			}

			y = (totalheight - (Float.parseFloat(y)) - Float.parseFloat(height)) + "";
			float xEnd = Float.parseFloat(x) + Float.parseFloat(width);
			if (xEnd > totalwidth) {
				float diff = xEnd - totalwidth;
				float newX = Float.parseFloat(x) - diff;
				x = newX + "";
			}

			rectangle = new PDRectangle(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(width),
					Float.parseFloat(height));

			List<PDField> acroFormFields = acroForm.getFields();
			PDSignatureField signatureField = new PDSignatureField(acroForm);
			acroForm.setSignaturesExist(true);
			acroForm.setAppendOnly(true);
			acroForm.getCOSObject().setDirect(true);
			signatureField.setValue(pds);
			acroFormFields.add(signatureField);

			pdDocument.addSignature(pds, new SignatureInterface() {

				@SuppressWarnings("rawtypes")
				@Override
				public byte[] sign(InputStream content) throws IOException {
					try {
						List<Certificate> certList = new ArrayList<>();
						certList.addAll(Arrays.asList(chain));

						Store certs = new JcaCertStore(certList);

						CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

						org.bouncycastle.asn1.x509.Certificate cert = org.bouncycastle.asn1.x509.Certificate
								.getInstance(chain[0].getEncoded());

						ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(pk);

						gen.addSignerInfoGenerator(
								new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build())
										.build(sha1Signer, new X509CertificateHolder(cert)));

						gen.addCertificates(certs);

						CMSProcessableInputStream msg = new CMSProcessableInputStream(content);

						CMSSignedData signedData = gen.generate(msg, false);

						return signedData.getEncoded();
					}

					catch (GeneralSecurityException | CMSException | OperatorCreationException e) {
						System.err.println("Error while creating pkcs7 signature.");
						e.printStackTrace();
					}
					throw new RuntimeException("Problem while preparing signature");
				}
			});

			if (pageNum == -1) {
				for (PDPage pdPage : pdDocument.getPages()) {
					AddVisibleSignature.createVisualSignatureTemplate(pdDocument, signatureField, pdPage, rectangle,
							pdImage, signDisplayInfo);
				}
			} else if (pageNum == -2) {
				for (String pageNo : signpages) {
					PDPage pdPage = pdDocument.getPage(Integer.parseInt(pageNo) - 1);
					AddVisibleSignature.createVisualSignatureTemplate(pdDocument, signatureField, pdPage, rectangle,
							pdImage, signDisplayInfo);
				}
			} else {
				PDPage pdPage = pdDocument.getPage(pageNum);
				AddVisibleSignature.createVisualSignatureTemplate(pdDocument, signatureField, pdPage, rectangle,
						pdImage, signDisplayInfo);
			}

			pdDocument.saveIncremental(new FileOutputStream(filePath));
			pdDocument.close();
			jsRes.put("status", "SUCCESS");
			jsRes.put("statusDetails", "Token pre sign success");
			jsRes.put("PDFPath", filePath);
			
		} catch (IOException e) {
			e.printStackTrace();
			jsRes.put("status", "FAILURE");
			jsRes.put("statusDetails", "Token pre sign failed "+e.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
			jsRes.put("status", "FAILURE");
			jsRes.put("statusDetails", "Token pre sign failed "+e.getMessage());
		}
		
		return jsRes;
	}

}
