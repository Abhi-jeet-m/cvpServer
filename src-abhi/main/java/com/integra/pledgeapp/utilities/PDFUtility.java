package com.integra.pledgeapp.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.token.AddVisibleSignature;


public class PDFUtility {
	
		static String certName = "";
	// getHash() : Used to get document hash from pdf using PDFBox
	public static JSONObject getHash(String SRC, String signDisplayInfo, JSONObject inputdata, Employee_Master employeeDetails) {
		File file = null;
		JSONObject responseObj = new JSONObject();
		String srcFileName;
		PDDocument pddoc;
		PDSignature pds;
		byte[] hashdocument;
		String totalHeight;
		String totalWidth;
		float totalheight;
		float totalwidth;
		ExternalSigningSupport externalSigning = null;
		try {
//			System.out.println("inputdata : "+inputdata);
			file = new File(SRC);
			int pageNum = 0;
			JSONObject signCoordinates = new JSONObject(inputdata.getString("signCoordinates"));
			String x = signCoordinates.getString("x").trim();
			String y = signCoordinates.getString("y").trim();
			String signPage = signCoordinates.getString("signPage").trim();
			String width = signCoordinates.getString("width").trim();
			String height = signCoordinates.getString("height").trim();
			srcFileName = new File(SRC).getName();
			String strTmpExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1, srcFileName.length());

			if (strTmpExt.equalsIgnoreCase("pdf") || strTmpExt.equalsIgnoreCase("jpg")
					|| strTmpExt.equalsIgnoreCase("png") || strTmpExt.equalsIgnoreCase("bmp")
					|| strTmpExt.equalsIgnoreCase("jpeg")) {
				if (!strTmpExt.equalsIgnoreCase("pdf")) {
					srcFileName = srcFileName.split("\\.")[0] + ".pdf";
				}
			} else {
				throw new Exception("Invalid PDF");
			}
			// Reading PDF file 
			pddoc = PDDocument.load(file);
			
			// Rearranging the coordinates to top left
				totalheight = Float.parseFloat("842");
				totalwidth = Float.parseFloat("595");
				if (!x.equalsIgnoreCase("") && !y.equalsIgnoreCase("")) {
					y = (totalheight - (Float.parseFloat(y)) - Float.parseFloat(height)) + "";
					float xEnd = Float.parseFloat(x) + Float.parseFloat(width);
					if (xEnd > totalwidth) {
						float diff = xEnd - totalwidth;
						float newX = Float.parseFloat(x) - diff;
						x = newX + "";
					}
				}
			
			// Identifying the page to sign
			String[] signpages = null;
			if ("L".equalsIgnoreCase(signPage)) {
				pageNum = pddoc.getNumberOfPages() - 1;
			} else if ("F".equalsIgnoreCase(signPage)) {
				pageNum = 0;
			} else if ("P".equalsIgnoreCase(signPage)) {
				pageNum = -2;	//-2 represents all mentioned pages in an array
				signPage = signCoordinates.getString("pages");
				signPage = signPage.substring(signPage.indexOf("[") + 1, signPage.indexOf("]"));
				signpages=signPage.split(",");
			} else if ("A".equalsIgnoreCase(signPage)) {
				pageNum = -1;	//-1 represents all pages
			} else {
				pageNum = Integer.parseInt(inputdata.getString("signPage"))-1;
			}
			File imgFile = null ;
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String DEST = Properties_Loader.SIGNED_FILES_DIRECTORY + "/SIGNED/BCAA/" + timeStamp + "/" + employeeDetails.getCompositeKey().getEMP_ID() + ".pdf";
			
			// Creating the file in destination path
			File destFile = new File(DEST);
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(DEST);
			
			String signImage = ""; 
			
			// uncomment it for image as a stamp
			
			String strCompanyCode = null;
			String logoPath = null;
			logoPath = Properties_Loader.IMAGEPATH;
			if(inputdata.has("imgpath")) {
				// we dont put any images for BC
				strCompanyCode = inputdata.getString("imgpath").split("/")[2];
				signImage = logoPath+strCompanyCode;
			}else {
				signImage = "";
			}
			
			imgFile = new File(signImage);

			pds = new PDSignature();
			pds.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
			pds.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
			pds.setSignDate(Calendar.getInstance());
			PDAcroForm acroForm = pddoc.getDocumentCatalog().getAcroForm();
			PDImageXObject pdImage = null;
			if (acroForm == null) {
				pddoc.getDocumentCatalog().setAcroForm(acroForm = new PDAcroForm(pddoc));
			}
			if(imgFile.exists()) {
				pdImage = PDImageXObject.createFromFileByContent(imgFile, pddoc);
			}

			PDRectangle rectangle = new PDRectangle(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(width),
					Float.parseFloat(height));
			List<PDField> acroFormFields = acroForm.getFields();
			PDSignatureField signatureField = new PDSignatureField(acroForm);
			acroForm.setSignaturesExist(true);
			acroForm.setAppendOnly(true);
			acroForm.getCOSObject().setDirect(true);
			signatureField.setValue(pds);
			acroFormFields.add(signatureField);
			// Used for Multi page signing
			pddoc.addSignature(pds);
			if(pageNum==-1) {
				for (PDPage pdPage : pddoc.getPages()) {
					AddVisibleSignature.createVisualSignatureTemplate(pddoc, signatureField, pdPage, rectangle, pdImage,
							signDisplayInfo);
				}
			} else if(pageNum==-2) {
				for (String pageNo : signpages) {
					PDPage pdPage = pddoc.getPage(Integer.parseInt(pageNo)-1);
					AddVisibleSignature.createVisualSignatureTemplate(pddoc, signatureField, pdPage, rectangle, pdImage, signDisplayInfo);
				}
			} else {
				PDPage pdPage = pddoc.getPage(pageNum);
				AddVisibleSignature.createVisualSignatureTemplate(pddoc, signatureField, pdPage, rectangle, pdImage, signDisplayInfo);
			}
			externalSigning = pddoc.saveIncrementalForExternalSigning(fos);
			InputStream dataToSign = externalSigning.getContent();
			byte[] hashByte = DigestUtils.sha256(dataToSign); // 64 bit length doc hash
			responseObj.put("sap", externalSigning);
			responseObj.put("pddoc", pddoc);
			responseObj.put("docHash", Base64.getEncoder().encodeToString(hashByte));
			return responseObj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDisMSGFormat(JSONArray displayInfo) {
		String displayMsg = "";
		try {
			int length = displayInfo.length();
			Calendar currentDat = Calendar.getInstance();
			SimpleDateFormat apperenceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//SimpleDateFormat apperenceDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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
	
}
