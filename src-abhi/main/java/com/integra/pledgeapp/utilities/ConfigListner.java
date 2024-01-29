package com.integra.pledgeapp.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;

import com.integra.pledgeapp.beans.Company_Master;
import com.integra.pledgeapp.beans.Field_Validation;
import com.integra.pledgeapp.beans.Template_Master;
import com.integra.pledgeapp.core.PledgeAppDAOImpl;

public class ConfigListner {

	public List<Template_Master> getTemplates() {
		List<Template_Master> list = null;
		List<Field_Validation> fieldList = new ArrayList<Field_Validation>();
		JSONObject templateJs = new JSONObject();
		JSONObject listofTemplates = new JSONObject();
		JSONObject filedsList = new JSONObject();
		JSONObject masterTemplates = new JSONObject();
		
		List<Company_Master> companyMaster = new ArrayList<Company_Master>();

		

		try {
			companyMaster=PledgeAppDAOImpl.getCompanyInfo();
			list = PledgeAppDAOImpl.getListofTemplates();
			JSONObject js=new JSONObject();
			js=PledgeAppDAOImpl.getDropDownDataFromCompanyMaster();
//			System.out.println("jsresponse :"+js);
			
			fieldList = PledgeAppDAOImpl.getFieldList();

			ListIterator<Field_Validation> iterator = fieldList.listIterator();
			while (iterator.hasNext()) {
				Field_Validation fieldValidation = iterator.next();
				JSONObject fieldValidationData=new JSONObject();
				fieldValidationData.put("name",fieldValidation.getFIELD_NAME());
				fieldValidationData.put("type",fieldValidation.getFIELD_TYPE());
				fieldValidationData.put("size",fieldValidation.getSIZE());
				fieldValidationData.put("nullable",fieldValidation.getNULLABLE());
				fieldValidationData.put("FiledValidationRequired",fieldValidation.getFIELD_VALIDATION_REQ());
				fieldValidationData.put("isEnabeld",fieldValidation.getIS_ENABLED());
				fieldValidationData.put("isMandatory",fieldValidation.getIS_MANDATORY());





				
				templateJs.put(fieldValidation.getFIELD_NAME(), fieldValidationData);

			}
//			System.out.println(templateJs);

			ListIterator<Template_Master> lisItr = list.listIterator();
//			System.out.println("Constructing ASP Server");
			while (lisItr.hasNext()) {
				Template_Master tMaster = lisItr.next();

				if (tMaster.getSTATUS().equals("1")) {
					String CSVHeader="";
					ListIterator<Field_Validation> iterator1 = fieldList.listIterator();
					while (iterator1.hasNext()) {
					
						Field_Validation fieldValidation = iterator1.next();
						if(tMaster.getID()==fieldValidation.getTEMP_ID()) {
							CSVHeader=CSVHeader.concat(fieldValidation.getFIELD_NAME()+",");
						}
						
//						System.out.println(fieldValidation);
//						System.out.println(CSVHeader);

					}
					listofTemplates.put(""+tMaster.getID(), CSVHeader);
					
				}
				
				
				

				
			}
			JSONObject compInfo=new JSONObject();
			JSONObject masterData=new JSONObject();
			
			
//			for (Company_Master company_Master : companyMaster) {
//				
//			
//				if(company_Master.getCOMPANY_CODE().equalsIgnoreCase("i25BCA")) {
//					compInfo.put(company_Master.getGROUP_NAME(),company_Master.getGROUP_CODE());
//					
//				}else {
//					masterData.put(company_Master.getGROUP_NAME(),company_Master.getGROUP_CODE());
//				}
//				
////				compInfo.put("COMPANY_CODE",company_Master.getCOMPANY_CODE());
////				compInfo.put("COMPANY_NAME", company_Master.getCOMPANY_NAME());
////				compInfo.put("GROUP_CODE", company_Master.getGROUP_CODE());
////				compInfo.pu
//				
//			}
			
			// get comapany details from company_master.
			
//				InMemory.settemplateData("i25BCA", compInfo);
//				InMemory.settemplateData("iMFAST", masterData);
			InMemory.settemplateData("dropDownList", js);

				InMemory.settemplateData("templates", listofTemplates);
				InMemory.settemplateData("fields", templateJs);
//			System.out.println(listofTemplates);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;

	}
}
