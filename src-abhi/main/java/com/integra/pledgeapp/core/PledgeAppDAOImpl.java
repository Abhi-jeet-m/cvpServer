package com.integra.pledgeapp.core;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Action_Master_Lang;
import com.integra.pledgeapp.beans.Audit_Info;
import com.integra.pledgeapp.beans.Bulk_Data_Upload_Logs;
import com.integra.pledgeapp.beans.Company_Master;
import com.integra.pledgeapp.beans.Doc_Sign_Config;
import com.integra.pledgeapp.beans.Dsc_Token_Config;
import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.beans.Employee_action_master;
import com.integra.pledgeapp.beans.Employee_action_status;
import com.integra.pledgeapp.beans.Field_Validation;
import com.integra.pledgeapp.beans.Mail_Config;
import com.integra.pledgeapp.beans.Sign_Rejected_Info;
import com.integra.pledgeapp.beans.Template_Master;
import com.integra.pledgeapp.beans.Sign_Doc_Details;
import com.integra.pledgeapp.beans.Sign_Log;
import com.integra.pledgeapp.beans.User_Master;
import com.integra.pledgeapp.beans.User_Requests;
import com.integra.pledgeapp.utilities.HibernateUtil;
import com.integra.pledgeapp.utilities.Properties_Loader;

public class PledgeAppDAOImpl implements PledgeAppDAOServices {

	@Override
	public JSONObject validateEmployeeDetails(String empInfo) {
		SessionFactory sf = null;
		Session session = null;
		// boolean res = false;
		JSONObject jsRes = new JSONObject();
		try {
			JSONObject input = new JSONObject(empInfo);
			String empID = input.getString("empid");
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = null;
			if (!input.get("empGroup").equals("")) {
				query = session.createQuery(
						"from Employee_Master M where M.compositeKey.EMP_ID=:empid and M.compositeKey.EMP_GROUP=:empGroup");
				query.setParameter("empid", empID);
				query.setParameter("empGroup", input.get("empGroup"));
			} else {
				query = session.createQuery("from Employee_Master M where M.compositeKey.EMP_ID=:empid");
				query.setParameter("empid", empID);
			}

			@SuppressWarnings("unchecked")
			List<Employee_Master> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (Employee_Master emp : result) {
					// res = true;
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "User details found");
					jsRes.put("mobilenum", emp.getEMP_PHONE());
					jsRes.put("emailID", emp.getEMP_EMAIL());
					jsRes.put("empname", emp.getEMP_NAME());
					jsRes.put("empStatus", emp.getSTATUS());

				}
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "User datails not found");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsRes;

	}

	@Override
	public JSONObject validateUserDetails(String userInfo) {
		SessionFactory sf = null;
		Session session = null;
		Boolean res = false;
		JSONObject jsRes = new JSONObject();
		try {
			JSONObject input = new JSONObject(userInfo);
			String username = input.getString("username");
			String password = input.getString("password");
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session
					.createQuery("from User_Master U where U.USER_NAME=:username and U.PASSWORD=:password");
			query.setParameter("username", username);
			query.setParameter("password", java.util.Base64.getEncoder().encodeToString(password.getBytes("utf-8")));
			@SuppressWarnings("unchecked")
			List<User_Master> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (User_Master user : result) {
					res = true;
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "user found");
					jsRes.put("roleId", user.getROLE_ID());
					jsRes.put("privilegeCode", user.getPRIVILEGE_CODE());
					jsRes.put("company", user.getCOMPANY());
					jsRes.put("empgroup", user.getEMP_GROUP());
				}
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "User not found");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsRes;
	}

	@Override
	public String updateEmployeeSignStatus(String filepath, String txnid, String filename) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		JSONObject output = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.beginTransaction();
			String empid = getEmpID(txnid, session);
			if (updateSign_Log(txnid, filepath, session)) {
				if (updateEmployee_Master(empid, filepath, session)) {
					tx.commit();
					output.put("statusmsg", "SUCCESS");
					output.put("statuscode", "00");
					output.put("referenceno", filename.split("@")[1].replace(".pdf", ""));
					output.put("signedFilePath", filepath);
				} else {
					tx.rollback();
					output.put("statusmsg", "FAILURE");
					output.put("statuscode", "01");
				}
			} else {
				tx.rollback();
				output.put("statusmsg", "FAILURE");
				output.put("statuscode", "01");
			}
		} catch (Throwable ex) {
			tx.rollback();
			try {
				output.put("statusmsg", "Error :" + ex.getMessage());
				output.put("statuscode", "01");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return output.toString();
	}

	public boolean updateSign_Log(String txnid, String filepath, Session session) {
		Boolean res = false;
		try {
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			Query query = session.createQuery(
					"update Sign_Log set file_path = :filepath, signed_on = :signedon ,sign_remarks = :signremarks, sign_status = :signstatus  where mysign_ref_token = :token");
			query.setParameter("signedon", currentTimestamp);

			if (filepath.contains("Error:")) {
				query.setParameter("signstatus", 2);
				query.setParameter("signremarks", "FAILURE");

			} else {
				query.setParameter("signstatus", 1);
				query.setParameter("signremarks", "SUCCESS");
			}
			query.setParameter("filepath", filepath);
			query.setParameter("token", txnid);
			int out = query.executeUpdate();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String getEmpID(String txnid, Session session) {
		String empid = null;
		try {
			Query query = session.createQuery("from Sign_Log S where S.MYSIGN_REF_TOKEN=:token");
			query.setParameter("token", txnid);
			@SuppressWarnings("unchecked")
			List<Sign_Log> result = query.list();
			if (!result.isEmpty()) {
				for (Sign_Log sign : result) {
					empid = sign.getEMP_ID();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return empid;
	}

	public boolean updateEmployee_Master(String empid, String filepath, Session session) {
		Boolean res = false;
		try {
			Query query = session
					.createQuery("UPDATE Employee_Master  as m set SIGN_STATUS = :signstatus where EMP_ID = :empid");

			if (filepath.contains("Error:")) {
				query.setParameter("signstatus", 2);
			} else {
				query.setParameter("signstatus", 1);
			}
			query.setParameter("empid", empid);
			int out = query.executeUpdate();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public JSONObject getSignedDownloadPath(String empInfo) {
		SessionFactory sf = null;
		Session session = null;
		String res = null;
		JSONObject out = new JSONObject();
		try {
//			String usertype = empInfo.split("-")[0];
			String value = empInfo.split("-")[1];

			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = null;
//			if (usertype.equalsIgnoreCase("emp")) {
//				query = session.createQuery("from Sign_Log S where S.REFERENCE_NO=:value and S.SIGN_STATUS =:status");
//			} else {
//				query = session.createQuery("from Sign_Log S where S.EMP_ID=:value and S.SIGN_STATUS =:status");
//
//			}
			query = session.createQuery("FROM Sign_Doc_Details where TXNID =:value");
			query.setParameter("value", value);
//			query.setParameter("status", 1);
			@SuppressWarnings("unchecked")
			List<Sign_Doc_Details> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (Sign_Doc_Details sign : result) {
					res = sign.getDOC_PATH();
					out.put("statuscode", "00");
					out.put("statusmsg", "SUCCESS");
					out.put("path", res + "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			try {
				out.put("statuscode", "01");
				out.put("statusmsg", "Error " + e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return out;
	}

	@Override
	public String updateDownloadStatus(String empInfo) {
		return null;
	}

//	@Override
//	public JSONArray getEmployeeList(String value, String group, String company, String document) {
//		SessionFactory sf = null;
//		Session session = null;
//		Query query = null;
//		Query query1 = null;
//		Query total = null;
//		JSONArray out = null;
//		JSONArray resp = new JSONArray();
//		JSONArray resp1 = new JSONArray();
//		try {
//			sf = HibernateUtil.getSessionFactory();
//			session = sf.openSession();
//			String listType = value;
//			total = session.createQuery(
//					"Select count(*) from Employee_Master e where e.compositeKey.EMP_GROUP =:group AND e.compositeKey.EMP_COMPANY=:company");
//
//			total.setParameter("group", group);
//			total.setParameter("company", company);
//
//			Long emptotal = (Long) total.uniqueResult();
//
//			Query query2 = session.createSQLQuery(
//					"SELECT EMP_CODE,COMPANY_CODE FROM employee_action_target WHERE DOCUMENT_CODE=:document AND (EMP_CODE IS NOT NULL OR COMPANY_CODE IS NOT NULL OR GROUP_CODE IS NOT NULL)");
//			query2.setParameter("document", document);
//			List<Object> result1 = query2.list();
//			Iterator it1 = result1.iterator();
//			String EMP_CODE = "";
//			String COMPANY_CODE = "";
//			while (it1.hasNext()) {
//				Object rows[] = (Object[]) it1.next();
//				JSONObject js = new JSONObject();
//				if (rows[0] != null) {
//					EMP_CODE = EMP_CODE + "'" + rows[0] + "',";
//				}
//				if (rows[1] != null) {
//					COMPANY_CODE = COMPANY_CODE + "'" + rows[1] + "',";
//				}
//			}
//
//			if (listType.equalsIgnoreCase("allList")) {
////
////				Query query2 = session.createSQLQuery(
////						"SELECT EMP_CODE,COMPANY_CODE FROM employee_action_target WHERE DOCUMENT_CODE=:document AND (EMP_CODE IS NOT NULL OR COMPANY_CODE IS NOT NULL OR GROUP_CODE IS NOT NULL)");
////				query2.setParameter("document", document);
////				List result1 = query2.list();
////				Iterator it1 = result1.iterator();
////				String EMP_CODE = "";
////				String COMPANY_CODE = "";
////				while (it1.hasNext()) {
////					Object rows[] = (Object[]) it1.next();
////					JSONObject js = new JSONObject();
////					if (rows[0] != null) {
////						EMP_CODE = EMP_CODE + "'" + rows[0] + "',";
////					}
////					if (rows[1] != null) {
////						COMPANY_CODE = COMPANY_CODE + "'" + rows[1] + "',";
////					}
////				}
//
//				if (COMPANY_CODE.length() != 0 && EMP_CODE.length() != 0) {
//					EMP_CODE = EMP_CODE.substring(0, EMP_CODE.length() - 1);
//					COMPANY_CODE = COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1);
//					query1 = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status,"
//									+ "(SELECT SIGN_DATE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'  AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS signed_on,"
//									+ "(SELECT SIGN_TYPE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'   AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS sign_type FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e "
//									+ " WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND (e.EMP_ID IN("
//									+ EMP_CODE + ")) " + "AND e.EMP_COMPANY IN (" + COMPANY_CODE
//									+ ")) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
//				} else if (COMPANY_CODE.length() != 0 && EMP_CODE.length() == 0) {
//					COMPANY_CODE = COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1);
//					query1 = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group ) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status,"
//									+ "(SELECT SIGN_DATE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'  AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS signed_on,"
//									+ "(SELECT SIGN_TYPE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'   AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS sign_type FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
//									+ "   WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_COMPANY IN ("
//									+ COMPANY_CODE + ")) AS e) AS e1 ; ");
//				} else {
//					EMP_CODE = EMP_CODE.substring(0, EMP_CODE.length() - 1);
//					query1 = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status,"
//									+ "(SELECT SIGN_DATE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'  AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS signed_on,"
//									+ "(SELECT SIGN_TYPE FROM EMPLOYEE_ACTION_STATUS WHERE  SIGN_STATUS='1'   AND DOCUMENT_CODE=:document AND EMP_CODE = eid AND EMP_GROUP=:group) AS sign_type  FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e "
//									+ "   WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_ID IN("
//									+ EMP_CODE + ")) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
//				}
//				query1.setParameter("group", group);
//				query1.setParameter("company", company);
//				query1.setParameter("document", document);
//
//				List list = query1.list();
//				Iterator it = list.iterator();
//				while (it.hasNext()) {
//					Object rows[] = (Object[]) it.next();
//					JSONObject js = new JSONObject();
//					js.put("empid", rows[0]);
//					js.put("empname", rows[1]);
//					js.put("empcompany", rows[2]);
//					if (null != rows[3]) {
//						String status;
//						if (rows[3].toString().equalsIgnoreCase("1"))
//							status = "Signed";
//						else {
//							status = "Failed";
//						}
//						js.put("empsignstatus", status);
//					} else {
//						js.put("empsignstatus", "Pending");
//					}
//					if (null != rows[5]) {
//						Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rows[5].toString());
//						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						String strDate = formatter.format(date1);
//						js.put("empsignedon", strDate);
//					} else {
//						js.put("empsignedon", "-");
//					}
//					if (null != rows[6]) {
//						js.put("empsigntype", rows[6]);
//					} else {
//						js.put("empsigntype", "-");
//					}
//					js.put("emptotal", emptotal);
//					resp1.put(js);
//				}
//				out = resp1;
//			}
//
//			else if (listType.equalsIgnoreCase("sigEmpList")) {
//				query1 = session.createQuery(
//						"from Employee_action_status E where E.SIGN_STATUS=1 and E.EMP_GROUP =:group and E.DOCUMENT_CODE=:document");
//				query1.setParameter("group", group);
//				query1.setParameter("document", document);
////				query1 = session.createSQLQuery(
////						"SELECT eid, ename, ephone, status, company ,(SELECT SIGNED_ON FROM SIGN_LOG WHERE emp_id = eid AND SIGN_STATUS='1') AS signed_on, "
////								+ "(SELECT SIGN_TYPE FROM SIGN_LOG WHERE emp_id = eid AND SIGN_STATUS='1') AS sign_type FROM "
////								+ "(SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_PHONE AS ephone, e.SIGN_STATUS AS status,  e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e where e.SIGN_STATUS=:status and e.EMP_COMPANY =:company) AS e");
////				query1.setParameter("status", 1);
//				List<Employee_action_status> list = query1.list();
//				Iterator<Employee_action_status> it = list.iterator();
//				while (it.hasNext()) {
//					Employee_action_status statusDetails = it.next();
//					JSONObject js = new JSONObject();
//					js.put("empid", statusDetails.getEMP_CODE());
//					js.put("empname", statusDetails.getEMP_NAME());
////					js.put("empphone", rows[2]);
//
//					if (statusDetails.getSIGN_STATUS() == 1) {
//						String status;
//						status = "Signed";
//						js.put("empsignstatus", status);
//					}
//
//					js.put("empcompany", statusDetails.getCOMPANY_CODE());
//					if (null != statusDetails.getSIGN_DATE()) {
////						Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(statusDetails.getSIGN_DATE());
//						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						String strDate = formatter.format(statusDetails.getSIGN_DATE());
//						js.put("empsignedon", strDate);
//					} else {
//						js.put("empsignedon", "-");
//					}
//					js.put("empsigntype", statusDetails.getSIGN_TYPE());
//					js.put("emptotal", emptotal);
//					resp1.put(js);
//				}
//				out = resp1;
//			}
//
//			else if (listType.equalsIgnoreCase("unSigEmpList")) {
//
////				query= session.createSQLQuery(
////						"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS WHERE EMP_CODE=eid AND `SIGN_STATUS`='1' AND DOCUMENT_CODE=:document) AS empstatus, "
////						+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS WHERE EMP_CODE=eid AND `ACTION_STATUS`='1' AND ACTION_TYPE='2' AND DOCUMENT_CODE=:document) AS actionstatus "
////						+ "FROM "+ "(SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e where  e.EMP_GROUP =:group OR e.EMP_COMPANY=:company) AS e) AS e1 WHERE e1.empstatus IS NULL");
////			
////				SELECT * FROM (SELECT eid, ename,ephone, company, empgroup, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS WHERE EMP_CODE=eid AND `SIGN_STATUS`='1') AS empstatus
////						FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_PHONE AS ephone, e.EMP_COMPANY AS company,e.EMP_GROUP AS empgroup  FROM EMPLOYEE_MASTER e WHERE e.EMP_GROUP ="ALLA" AND e.EMP_COMPANY="i25BCA") AS e ) AS e1 WHERE e1.empstatus IS NULL
//
//				if (COMPANY_CODE.length() != 0 && EMP_CODE.length() != 0) {
//					EMP_CODE = EMP_CODE.substring(0, EMP_CODE.length() - 1);
//					COMPANY_CODE = COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1);
//					query = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e "
//									+ "   WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND (e.EMP_ID IN("
//									+ EMP_CODE + ") " + "AND e.EMP_COMPANY IN (" + COMPANY_CODE
//									+ "))) AS e) AS e1 WHERE  e1.empstatus IS NULL ;" + "   ");
//				} else if (COMPANY_CODE.length() != 0 && EMP_CODE.length() == 0) {
//					COMPANY_CODE = COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1);
//					query = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
//									+ "   WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_COMPANY IN ("
//									+ COMPANY_CODE + ")) AS e) " + "AS e1 WHERE  e1.empstatus IS NULL ;" + "   ");
//				} else {
//					EMP_CODE = EMP_CODE.substring(0, EMP_CODE.length() - 1);
//					query = session.createSQLQuery(
//							"SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_STATUS FROM EMPLOYEE_ACTION_STATUS "
//									+ " WHERE EMP_CODE=eid AND SIGN_STATUS='1' AND EMP_GROUP=:group) AS empstatus,"
//									+ "(SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS"
//									+ " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status FROM"
//									+ " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e "
//									+ "   WHERE ( e.EMP_GROUP =:group OR e.EMP_COMPANY=:company)  AND  e.EMP_ID IN("
//									+ EMP_CODE + ")) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
//				}
//				query.setParameter("group", group);
//				query.setParameter("company", company);
//				@SuppressWarnings("unchecked")
//				List result = query.list();
//				Iterator it = result.iterator();
//
//				while (it.hasNext()) {
//					Object rows[] = (Object[]) it.next();
//					JSONObject js = new JSONObject();
//					js.put("empid", rows[0]);
//					js.put("empname", rows[1]);
//					js.put("empcompany", rows[2]);
//					if (null != rows[3]) {
//						String status;
//						if (rows[3].toString().equalsIgnoreCase("1"))
//							status = "Signed";
//						else {
//							status = "Failed";
//						}
//						js.put("empsignstatus", status);
//					} else {
//						js.put("empsignstatus", "Pending");
//					}
//					if (null != rows[4]) {
//						if (rows[4].toString().equalsIgnoreCase("1"))
//							js.put("empactionstatus", "Viewed/Downloaded");
//					} else {
//						js.put("empactionstatus", "Not Viewed");
//					}
//					js.put("emptotal", emptotal);
//					resp.put(js);
//				}
//				out = resp;
//			}
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			if (session != null && session.isOpen()) {
//				session.close();
//			}

//		}
//		return out;
//	}

	// purpose of logic and funtionality to be reviewd again
	/*
	 * @Override public JSONArray getEmployeeList(String value, String group, String
	 * company, String document) { SessionFactory sf = null; Session session = null;
	 * Query query = null; Query query1 = null; Query total = null; JSONArray out =
	 * null; JSONArray resp = new JSONArray(); JSONArray resp1 = new JSONArray();
	 * try { sf = HibernateUtil.getSessionFactory(); session = sf.openSession();
	 * String listType = value; total = session.createQuery(
	 * "Select count(*) from Employee_Master e where e.compositeKey.EMP_GROUP =:group AND e.compositeKey.EMP_COMPANY=:company"
	 * );
	 * 
	 * total.setParameter("group", group); total.setParameter("company", company);
	 * 
	 * Long emptotal = (Long) total.uniqueResult();
	 * 
	 * Query query2 = session.createSQLQuery(
	 * "SELECT EMP_CODE,COMPANY_CODE FROM employee_action_target WHERE DOCUMENT_CODE=:document AND (EMP_CODE IS NOT NULL OR COMPANY_CODE IS NOT NULL OR GROUP_CODE IS NOT NULL)"
	 * ); query2.setParameter("document", document); List<Object> result1 =
	 * query2.list(); Iterator it1 = result1.iterator(); String EMP_CODE = "";
	 * String COMPANY_CODE = ""; while (it1.hasNext()) { Object rows[] = (Object[])
	 * it1.next(); JSONObject js = new JSONObject(); if (rows[0] != null) { EMP_CODE
	 * = EMP_CODE + "'" + rows[0] + "',"; } if (rows[1] != null) { COMPANY_CODE =
	 * COMPANY_CODE + "'" + rows[1] + "',"; } }
	 * 
	 * if (listType.equalsIgnoreCase("allList")) { group = ""; if
	 * (COMPANY_CODE.length() != 0 && EMP_CODE.length() != 0) { EMP_CODE =
	 * EMP_CODE.substring(0, EMP_CODE.length() - 1); COMPANY_CODE =
	 * COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1); query1 =
	 * session.createSQLQuery(
	 * 
	 * // SELECT * FROM (SELECT eid,ename,company, // (SELECT SIGN_ORDER FROM
	 * Sign_Doc_Details WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP="" ) AS
	 * empstatus, // (SELECT SIGN_DATE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'
	 * AND DOC_CODE="CVP" AND EMPID = "IF0006" AND EMP_GROUP="") AS signed_on //
	 * FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company
	 * FROM EMPLOYEE_MASTER e WHERE // ( e.EMP_GROUP ="" AND
	 * e.EMP_COMPANY="IMFPL"))AS e) AS e1;
	 * 
	 * "SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_ORDER FROM Sign_Doc_Details "
	 * +
	 * " WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group ) AS empstatus, "
	 * +
	 * "(SELECT SIGN_DATE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'  AND DOC_CODE=:document AND EMPID = eid AND EMP_GROUP=:group) AS signed_on "
	 * + " FROM " +
	 * " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * + "  WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)) AS e) AS e1");
	 * System.out.println("query1:" + query1); } else if (COMPANY_CODE.length() != 0
	 * && EMP_CODE.length() == 0) { COMPANY_CODE = COMPANY_CODE.substring(0,
	 * COMPANY_CODE.length() - 1); query1 = session.createSQLQuery(
	 * "SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_ORDER FROM Sign_Doc_Details "
	 * +
	 * " WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group ) AS empstatus, "
	 * +
	 * "(SELECT SIGN_DATE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'  AND DOC_CODE=:document AND EMPID = eid AND EMP_GROUP=:group) AS signed_on "
	 * + " FROM " +
	 * " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * +
	 * "  WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_COMPANY IN ("
	 * + COMPANY_CODE + ")) AS e) AS e1"); System.out.println("query2:" + query1); }
	 * else { EMP_CODE = EMP_CODE.substring(0, EMP_CODE.length() - 1); query1 =
	 * session.createSQLQuery(
	 * "SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_ORDER FROM Sign_Doc_Details "
	 * +
	 * " WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group ) AS empstatus, "
	 * +
	 * "(SELECT SIGN_DATE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'  AND DOC_CODE=:document AND EMPID = eid AND EMP_GROUP=:group) AS signed_on "
	 * + " FROM " +
	 * " (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * +
	 * "  WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_ID IN ("
	 * + EMP_CODE + ")) AS e) AS e1"); System.out.println("query3:" + query1); }
	 * query1.setParameter("group", group); query1.setParameter("company", company);
	 * query1.setParameter("document", document);
	 * 
	 * List list = query1.list(); Iterator it = list.iterator(); while
	 * (it.hasNext()) { Object rows[] = (Object[]) it.next(); JSONObject js = new
	 * JSONObject(); js.put("empid", rows[0]); js.put("empname", rows[1]);
	 * js.put("empcompany", rows[2]); if (null != rows[3]) { String status; String
	 * signType; if (rows[3].toString().equalsIgnoreCase("1")) { status =
	 * "BC Signed"; signType = "Esign"; } else if
	 * (rows[3].toString().equalsIgnoreCase("2")) { status =
	 * "BC and Witness Signed"; signType = "Esign"; } else { status = "Pending";
	 * signType = "-"; } js.put("empsignstatus", status); js.put("empsigntype",
	 * signType); } else { js.put("empsignstatus", "pending"); js.put("empsigntype",
	 * "-"); }
	 * 
	 * if (null != rows[4]) { Date date1 = new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rows[4].toString());
	 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * String strDate = formatter.format(date1); js.put("empsignedon", strDate); }
	 * else { js.put("empsignedon", "-"); }
	 * 
	 * js.put("emptotal", emptotal); resp1.put(js); } out = resp1; } else if
	 * (listType.equalsIgnoreCase("sigEmpList")) { group = ""; //
	 * System.out.println("group:"+group); query1 =
	 * session.createSQLQuery("SELECT eid,company,signDate,signOrder," +
	 * "(SELECT EMP_NAME FROM Employee_Master E WHERE  E.EMP_ID= eid AND E.SIGN_STATUS=:status AND E.EMP_GROUP=:group AND E.EMP_COMPANY=company) AS ename FROM "
	 * +
	 * "(SELECT s.EMPID AS eid, s.EMP_COMPANY AS company, s.SIGN_DATE AS signDate,s.SIGN_ORDER as signOrder FROM Sign_Doc_Details s where s.SIGN_ORDER>=1 and s.DOC_CODE=:document and s.EMP_GROUP=:group) AS s"
	 * ); query1.setParameter("status", 1); query1.setParameter("group", group);
	 * query1.setParameter("document", document);
	 * 
	 * List list = query1.list(); Iterator it = list.iterator(); while
	 * (it.hasNext()) { Object rows[] = (Object[]) it.next(); JSONObject js = new
	 * JSONObject(); js.put("empid", rows[0]); js.put("empcompany", rows[1]); if
	 * (null != rows[2]) { SimpleDateFormat formatter = new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); String strDate =
	 * formatter.format(rows[2]); js.put("empsignedon", strDate); } else {
	 * js.put("empsignedon", "-"); } if (null != rows[3]) { String status = null; if
	 * (rows[3].toString().equalsIgnoreCase("1")) { status = "BC signed"; } else if
	 * (rows[3].toString().equalsIgnoreCase("2")) { status =
	 * "BC and Witness signed"; } js.put("empsignstatus", status); } else {
	 * js.put("empsignstatus", "Pending"); } js.put("empname", rows[4]);
	 * js.put("empsigntype", "Esign"); js.put("emptotal", emptotal); resp1.put(js);
	 * }
	 * 
	 * out = resp1; }
	 * 
	 * else if (listType.equalsIgnoreCase("unSigEmpList")) { group = ""; if
	 * (COMPANY_CODE.length() != 0 && EMP_CODE.length() != 0) { EMP_CODE =
	 * EMP_CODE.substring(0, EMP_CODE.length() - 1); COMPANY_CODE =
	 * COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1); query = session.
	 * createSQLQuery("SELECT EMP_ID AS eid,EMP_NAME AS ename,EMP_COMPANY AS company,"
	 * +
	 * "(SELECT SIGN_ORDER FROM Sign_Doc_Details  WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group ) AS empstatus,"
	 * +
	 * " (SELECT SIGN_DATE FROM Sign_Doc_Details WHERE EMPID=eid AND SIGN_ORDER>='1'   AND EMP_GROUP=:group) AS signed_on "
	 * +
	 * "FROM Employee_Master em WHERE EMP_COMPANY=:company AND EMP_GROUP=:group AND "
	 * +
	 * "em.EMP_ID NOT IN (SELECT sdd.EMPID FROM Sign_Doc_Details sdd WHERE sdd.EMP_COMPANY=:company "
	 * + "AND sdd.EMP_GROUP=:group AND sdd.DOC_CODE=:document)");
	 * 
	 * // + " (SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS " // +
	 * " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status"
	 * // +
	 * " FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * // +
	 * " WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  (e.EMP_ID IN ("
	 * // + EMP_CODE + ") " + "AND e.EMP_COMPANY IN (" + COMPANY_CODE // +
	 * "))) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
	 * System.out.println("query1:" + query); } else if (COMPANY_CODE.length() != 0
	 * && EMP_CODE.length() == 0) {
	 * 
	 * COMPANY_CODE = COMPANY_CODE.substring(0, COMPANY_CODE.length() - 1);
	 * 
	 * query = session.createSQLQuery(
	 * "SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_ORDER FROM Sign_Doc_Details  WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group) AS empstatus, "
	 * + " (SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS " +
	 * " WHERE EMP_CODE=eid AND ACTION_STATUS='1' AND ACTION_TYPE='2' ) AS action_status"
	 * +
	 * " FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * +
	 * " WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_COMPANY IN ("
	 * + COMPANY_CODE + ")) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
	 * System.out.println("query2:" + query); } else { EMP_CODE =
	 * EMP_CODE.substring(0, EMP_CODE.length() - 1); query = session.createSQLQuery(
	 * "SELECT * FROM (SELECT eid,ename,company, (SELECT SIGN_ORDER FROM Sign_Doc_Details  WHERE EMPID=eid AND SIGN_ORDER>='1' AND EMP_GROUP=:group) AS empstatus, "
	 * + " (SELECT DISTINCT ACTION_STATUS FROM EMPLOYEE_ACTION_STATUS " +
	 * " WHERE EMP_CODE=eid AND ACTION_STATUS=1' AND ACTION_TYPE='2' ) AS action_status"
	 * +
	 * " FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
	 * +
	 * " WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)  AND  e.EMP_ID IN ("
	 * + EMP_CODE + ")) AS e) AS e1 WHERE  e1.empstatus IS NULL ;");
	 * System.out.println("query3:" + query); } query.setParameter("group", group);
	 * query.setParameter("company", company); query.setParameter("document",
	 * document);
	 * 
	 * @SuppressWarnings("unchecked") List result = query.list(); Iterator it =
	 * result.iterator();
	 * 
	 * while (it.hasNext()) { Object rows[] = (Object[]) it.next(); JSONObject js =
	 * new JSONObject(); js.put("empid", rows[0]); js.put("empname", rows[1]);
	 * js.put("empcompany", rows[2]); if (null != rows[3]) { String status; if
	 * (rows[3].toString().equalsIgnoreCase("1")) status = "bc Signed"; else if
	 * (rows[3].toString().equalsIgnoreCase("2")) { status =
	 * "bc and witness signed"; } else { status = "pending"; }
	 * js.put("empsignstatus", status); } else { js.put("empsignstatus", "Pending");
	 * } if (null != rows[4]) { if (rows[4].toString().equalsIgnoreCase("1"))
	 * js.put("empactionstatus", "Viewed/Downloaded"); } else {
	 * js.put("empactionstatus", "Not Viewed"); } js.put("emptotal", emptotal);
	 * resp.put(js); } out = resp; }
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } finally { if (session !=
	 * null && session.isOpen()) { session.close(); }
	 * 
	 * } return out; }
	 */

	@Override
	public JSONArray getEmployeeList(String value, String group, String company, String document) {
		SessionFactory sf = null;
		Session session = null;
		String whereCondition = "";
		Query query = null;
		Query query1 = null;
		Query total = null;
		JSONArray out = null;
		JSONArray resp = new JSONArray();
		JSONArray resp1 = new JSONArray();
		try {
			group = "";
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String listType = value;
			total = session.createQuery(
					"Select count(*) from Employee_Master e where e.compositeKey.EMP_GROUP =:group AND e.compositeKey.EMP_COMPANY=:company");

			total.setParameter("group", group);
			total.setParameter("company", company);
			Long emptotal = (Long) total.uniqueResult();
			if (listType.equalsIgnoreCase("allList")) {
				whereCondition = "";
			} else if (listType.equalsIgnoreCase("sigEmpList")) {
				whereCondition = "WHERE  e.empstatus='1'";
			}

			else if (listType.equalsIgnoreCase("unSigEmpList")) {
				whereCondition = "WHERE e.empstatus='0'";

			}

			query1 = session.createSQLQuery(

//					"SELECT * FROM (SELECT eid,ename,company, (SELECT (CASE  WHEN COUNT(*)>0 THEN SIGN_ORDER ELSE 0  END ) FROM Sign_Doc_Details WHERE SIGN_ORDER>='1' AND EMPID=eid AND EMP_GROUP=:group)AS empstatus, "
//							+ "(SELECT SIGN_DATE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'  AND DOC_CODE=:document AND EMPID = eid AND EMP_GROUP=:group) AS signed_on,"
//							//+"(SELECT DOC_CODE FROM Sign_Doc_Details WHERE SIGN_ORDER>='1'  AND DOC_CODE=:document AND EMPID = eid AND EMP_GROUP=:group) AS doc_code"
//							+":document AS document,"
//							+"(SELECT (CASE WHEN (SELECT COUNT(*) FROM employee_action_target eat WHERE eat.DOCUMENT_CODE=:document AND  eat.COMPANY_CODE=:company)>0 THEN 'Y' ELSE 'N' END)) AS is_sign_this_doc"
//							+ " FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e"
//							+ " WHERE ( e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)) AS e) AS e1 "+ whereCondition);
					"SELECT * FROM (SELECT eid,ename,company,IFNULL(sdd.sign_order,0) AS empstatus,"
							+ "sdd.SIGN_DATE AS signed_on,:document AS document,"
							+ "(SELECT (CASE WHEN (SELECT COUNT(*) FROM employee_action_target eat WHERE eat.DOCUMENT_CODE=:document AND  eat.COMPANY_CODE=:company)>0 THEN 'Y' ELSE 'N' END)) AS signing_applicable "
							+ "FROM (SELECT e.EMP_ID AS eid, e.EMP_NAME AS ename, e.EMP_COMPANY AS company FROM EMPLOYEE_MASTER e WHERE (e.EMP_GROUP =:group AND e.EMP_COMPANY=:company)) a1 "
							+ "LEFT JOIN (SELECT * FROM sign_doc_details WHERE EMP_GROUP =:group AND EMP_COMPANY=:company AND DOC_CODE=:document) sdd "
							+ "ON a1.eid=sdd.empid)AS e " + whereCondition);
			query1.setParameter("group", group);
			query1.setParameter("company", company);
			query1.setParameter("document", document);

			List list = query1.list();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object rows[] = (Object[]) it.next();
				if (rows[6].toString().equalsIgnoreCase("Y")) {
					JSONObject js = new JSONObject();
					js.put("empid", rows[0]);
					js.put("empname", rows[1]);
					js.put("empcompany", rows[2]);
					if (null != rows[3]) {
						String status;
						String signType;
						if (rows[3].toString().equalsIgnoreCase("1")) {
							status = "Signed";
							signType = "Esign";
						} else {
							status = "Pending";
							signType = "-";
						}
						js.put("empsignstatus", status);
						js.put("empsigntype", signType);
					} else {
						js.put("empsignstatus", "pending");
						js.put("empsigntype", "-");
					}

					if (null != rows[4]) {
						Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rows[4].toString());
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String strDate = formatter.format(date1);
						js.put("empsignedon", strDate);
					} else {
						js.put("empsignedon", "-");
					}

					js.put("emptotal", emptotal);
					resp1.put(js);
				}

			}
			out = resp1;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return out;
	}

	@Override
	public Employee_Master getEmployeeDetails(String empid, String empGroup) {
		SessionFactory sf = null;
		Session session = null;
		Employee_Master empDet = null;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = null;
			if (!empGroup.equals("")) {
				query = session.createQuery(
						"from Employee_Master M where M.compositeKey.EMP_ID=:empid and M.compositeKey.EMP_GROUP=:empGroup");
				query.setParameter("empid", empid);
				query.setParameter("empGroup", empGroup);
			} else {
				query = session.createQuery("from Employee_Master M where M.compositeKey.EMP_ID=:empid");
				query.setParameter("empid", empid);
			}
			@SuppressWarnings("unchecked")
			List<Employee_Master> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (Employee_Master emp : result) {
					empDet = emp;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return empDet;
	}

	@Override
	public boolean insertSign_log(JSONObject empinfo) {
		System.out.println("empinfo insertSign_log:"+empinfo);
		Sign_Log sl = new Sign_Log();
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sl.setEMP_ID(empinfo.getString("empid"));
			sl.setMYSIGN_REF_TOKEN(empinfo.getString("token"));
			sl.setREFERENCE_NO(empinfo.getString("refno"));
			sl.setCLIENT_IP(empinfo.getString("ip"));
			sl.setSIGN_TYPE(empinfo.getString("signType"));
			sl.setEMP_GROUP(empinfo.getString("empGroup"));
			sl.setDOC_CODE(empinfo.getString("docCode"));
			sl.setEMP_COMPANY(empinfo.getString("empCompany"));
			sl.setEMP_NAME(empinfo.getString("empname"));
			try {
				sf = HibernateUtil.getSessionFactory();
				session = sf.openSession();
				tx = session.getTransaction();
				tx.begin();
				session.save(sl);
				tx.commit();
				isSuccess = true;
			} catch (Exception e) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public JSONObject getMailInfo(String usertype, String value) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject resp = new JSONObject();
		sf = HibernateUtil.getSessionFactory();
		session = sf.openSession();
		try {
			// if ("emp".equalsIgnoreCase(usertype)) {
////				Query query = session
////						.createQuery("from Sign_Log M where M.REFERENCE_NO=:refno and M.SIGN_STATUS=:status");
			Query query = session.createQuery("FROM Sign_Doc_Details where TXNID =:refno");
			query.setParameter("refno", value);
////				query.setParameter("status", 1);
			@SuppressWarnings("unchecked")
			List<Sign_Doc_Details> signlog_res = query.list();
			String empid = null;
			String path = null;
			for (Sign_Doc_Details sign : signlog_res) {
				empid = sign.getEMPID();
				path = sign.getDOC_PATH();
			}
			Query query2 = session
					.createQuery("from Employee_Master M where M.compositeKey.EMP_ID=:empid and M.SIGN_STATUS=:status");
			query2.setParameter("empid", empid);
			query2.setParameter("status", 1);
			@SuppressWarnings("unchecked")
			List<Employee_Master> empmaster_res = query2.list();
			if (!empmaster_res.isEmpty()) {
				for (Employee_Master emp : empmaster_res) {
					try {
						resp.put("mail", emp.getEMP_EMAIL());
						resp.put("path", path);
						resp.put("name", emp.getEMP_NAME());
						resp.put("empid", emp.getCompositeKey().getEMP_ID());
						resp.put("company", emp.getCompositeKey().getEMP_COMPANY());
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			session.close();

			// } else if ("auto".equalsIgnoreCase(usertype)) {

////				Query query = session
////						.createQuery("from Sign_Log M where M.MYSIGN_REF_TOKEN=:token and M.SIGN_STATUS=:status");
//				Query query = session.createQuery("FROM Sign_Doc_Details where TXNID =:refno");
//				query.setParameter("refno", value);
//////				query.setParameter("status", 1);
//				@SuppressWarnings("unchecked")
//				List<Sign_Doc_Details> signlog_res = query.list();
//				String empid = null;
//				String path = null;
//				for (Sign_Doc_Details sign : signlog_res) {
//					empid = sign.getEMPID();
//					path = sign.getDOC_PATH();
//				}
//				Query query2 = session.createQuery(
//						"from Employee_Master M where M.compositeKey.EMP_ID=:empid and M.SIGN_STATUS=:status");
//				query2.setParameter("empid", empid);
//				query2.setParameter("status", 1);
//				@SuppressWarnings("unchecked")
//				List<Employee_Master> empmaster_res = query2.list();
//				if (!empmaster_res.isEmpty()) {
//					for (Employee_Master emp : empmaster_res) {
//						try {
//							resp.put("mail", emp.getEMP_EMAIL());
//							resp.put("path", path);
//							resp.put("name", emp.getEMP_NAME());
//							resp.put("empid", emp.getCompositeKey().getEMP_ID());
//							resp.put("company", emp.getCompositeKey().getEMP_COMPANY());
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//				session.close();

			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return resp;
	}

	@Override
	public String getHRMail() {
		SessionFactory sf = null;
		Session session = null;
		String resp = null;
		sf = HibernateUtil.getSessionFactory();
		session = sf.openSession();
		try {
			Query query = session.createQuery("from Mail_Config M where M.configcode=:configcode");
			query.setParameter("configcode", Properties_Loader.CONFIGCODE);
			@SuppressWarnings("unchecked")
			List<Mail_Config> mailconf_res = query.list();
			for (Mail_Config mail : mailconf_res) {
				resp = mail.getMailid();
			}
			session.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return resp;
	}

	@Override
	public boolean updatePhysicalSignedRecord(String empid, String refno, String ip, String outfilepath) {
		JSONObject input = new JSONObject();
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean resp = false;
		try {
			input.put("empid", empid);
			input.put("refno", refno);
			input.put("ip", ip);
			input.put("filepath", outfilepath);
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			if (insertSignedLogPhysicalSigned(input, session)) {
				if (updatePhysicalSignedEmployee_Master(empid, session)) {
					tx.commit();
					resp = true;
				} else {
					tx.rollback();
				}
			} else {
				tx.rollback();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return resp;
	}

	public boolean insertSignedLogPhysicalSigned(JSONObject empinfo, Session session) {
		boolean isSuccess = false;
		Sign_Log sl = new Sign_Log();
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			sl.setEMP_ID(empinfo.getString("empid"));
			sl.setREFERENCE_NO(empinfo.getString("refno"));
			sl.setCLIENT_IP(empinfo.getString("ip"));
			sl.setSIGNED_ON(currentTimestamp);
			sl.setSIGN_REMARKS("Uploaded");
			sl.setSIGN_STATUS(1);
			sl.setFILE_PATH(empinfo.getString("filepath"));

			sl.setSIGN_TYPE("UPLOADED");
			session.save(sl);
			isSuccess = true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	public boolean updatePhysicalSignedEmployee_Master(String empid, Session session) {
		Boolean res = false;
		try {
			Query query = session
					.createQuery("UPDATE Employee_Master  as m set SIGN_STATUS = :signstatus where EMP_ID = :empid");
			query.setParameter("signstatus", 1);
			query.setParameter("empid", empid);
			int out = query.executeUpdate();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public void insertauditinfo(JSONObject input) {
		Audit_Info AI = new Audit_Info();
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		try {
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			AI.setEMP_ID(input.getString("empid"));
			AI.setOPERATION_TIME(currentTimestamp);
			AI.setOPERATION_TYPE(input.getString("operationtype"));
			AI.setOPERATIONREMARKS(input.getString("operationremarks"));
			AI.setUSER(input.getString("userid"));
			AI.setIPADDRESS(input.getString("ip"));
			try {
				sf = HibernateUtil.getSessionFactory();
				session = sf.openSession();
				tx = session.getTransaction();
				tx.begin();
				session.save(AI);
				tx.commit();
			} catch (Exception e) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

	}

	@Override
	public JSONObject checksignstatus(String txnid) {
		SessionFactory sf = null;
		Session session = null;
		Sign_Log slog = null;
		JSONObject res1 = new JSONObject();
//		boolean res = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("from Sign_Log S where S.MYSIGN_REF_TOKEN=:txnid");
			query.setParameter("txnid", txnid);
			@SuppressWarnings("unchecked")
			List<Sign_Log> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (Sign_Log emp : result) {
					slog = emp;
				}
			}
			res1.put("docCode", slog.getDOC_CODE());
			res1.put("empid", slog.getEMP_ID());
			res1.put("empGroup", slog.getEMP_GROUP());
			res1.put("empCompany", slog.getEMP_COMPANY());
			res1.put("empname", slog.getEMP_NAME());
			res1.put("signType", slog.getSIGN_TYPE());
			if (getDocStatusInSDD(slog.getEMP_ID(), slog.getDOC_CODE())) {
				res1.put("status", "FAILURE");
			} else {
				res1.put("status", "SUCCESS");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return res1;
	}

	@Override
	public JSONObject getWidgetData(String listType, String companyName) {
		SessionFactory sf = null;
		Session session = null;
		Query signed = null;
		Query total = null;
		JSONObject resp = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			total = session
					.createQuery("Select count(*) from Employee_Master e where e.compositeKey.EMP_COMPANY =:company");
			total.setParameter("company", companyName);
			Long emptotal = (Long) total.uniqueResult();

			signed = session.createQuery(
					"Select count(*) from Employee_Master e where e.compositeKey.EMP_COMPANY =:company and SIGN_STATUS=:status");
			signed.setParameter("company", companyName);
			signed.setParameter("status", 1);
			Long empsigned = (Long) signed.uniqueResult();

			Long empUnsigned = emptotal - empsigned;

			resp.put("totalcount", emptotal);
			resp.put("signedcount", empsigned);
			resp.put("unsignedcount", empUnsigned);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return resp;
	}

	@Override
	public JSONObject getDocListInEAT(JSONObject inputs) {
		System.out.println("getDocListInEAT::"+inputs);
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery(
					"SELECT DISTINCT DOCUMENT_CODE FROM Employee_action_target WHERE COMPANY_CODE =:companyCode and ENABLE_STATUS='1'");
			query.setParameter("companyCode", inputs.getString("companyCode"));
			@SuppressWarnings("unchecked")
			List res = query.list();
			java.util.ListIterator<String> iterator = res.listIterator();
			ArrayList<String> docArray = new ArrayList<String>();
			while (iterator.hasNext()) {
				docArray.add(iterator.next());
			}

			Query query2 = session.createQuery(
					"SELECT DISTINCT DOCUMENT_CODE FROM Employee_action_target WHERE EMP_CODE =:empCode AND ENABLE_STATUS='1'");
			query2.setParameter("empCode", inputs.getString("empCode"));
			@SuppressWarnings("unchecked")
			List res2 = query2.list();
			java.util.ListIterator<String> iterator2 = res2.listIterator();
			while (iterator2.hasNext()) {
				docArray.add(iterator2.next());
			}

			if (inputs.getString("empGroup").trim().length() != 0 && inputs.has("empGroup")) {
				Query query3 = session.createQuery(
						"SELECT DISTINCT DOCUMENT_CODE FROM Employee_action_target WHERE GROUP_CODE =:empGroup AND ENABLE_STATUS='1'");
				query3.setParameter("empGroup", inputs.getString("empGroup"));
				@SuppressWarnings("unchecked")
				List res3 = query3.list();
				java.util.ListIterator<String> iterator3 = res3.listIterator();
				while (iterator3.hasNext()) {
					docArray.add(iterator3.next());
				}
			}

			for (int i = 0; i < docArray.size() - 1; i++) {
				for (int j = i + 1; j < docArray.size(); j++) {
					if (docArray.get(i).equalsIgnoreCase(docArray.get(j))) {
						docArray.remove(j);
					}
				}
			}

			result.put("status", "");
			result.put("statusDetails", "");
			result.put("docList", docArray);
			session.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				result.put("status", "FAILURE");
				result.put("statusDetails", e.getMessage());
				result.put("docList", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}

		}
		return result;
	}

	@Override
	public JSONObject getDocInfoInEAM(String docCode) {
		System.out.println("docCode:"+docCode);
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery(
					"from Employee_action_master e where e.DOCUMENT_CODE=:varDocCode and e.ENABLE_STATUS=:varEnableStatus");
			query.setParameter("varDocCode", docCode);
			query.setParameter("varEnableStatus", 1);
			@SuppressWarnings("unchecked")
			List<Employee_action_master> res = query.getResultList();
			java.util.ListIterator<Employee_action_master> iterator = res.listIterator();
			while (iterator.hasNext()) {
				System.out.println("in while");
				Employee_action_master docList = iterator.next();
				result.put("status", "");
				result.put("statusDetails", "");
				result.put("docName", docList.getDOCUMENT_NAME());
				result.put("docScope", docList.getDOCUMENT_SCOPE());
				result.put("docDate", docList.getDOCUMENT_DATE());
				result.put("docActionType", docList.getACTION_TYPE());
				result.put("fileName", docList.getFILE_NAME());
				result.put("generatePdf", docList.getGENERATE_PDF());

			}
			session.close();

		} catch (Exception e) {
			try {
				result.put("status", "FAILURE");
				result.put("statusDetails", e.getMessage());
				result.put("docList", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}

		}
		System.out.println("result in PledgeAppDAOImpl:"+result);
		return result;
	}

	@Override
	public boolean getDocStatusInSDD(String empCode, String docCode) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session
					.createQuery("from Sign_Doc_Details where DOC_CODE=:varDocCode and e.EMPID=:varEmpCode");
			query.setParameter("varDocCode", docCode);
			query.setParameter("varEmpCode", empCode);
			@SuppressWarnings("unchecked")
			List<Sign_Doc_Details> res = query.list();
			java.util.ListIterator<Sign_Doc_Details> iterator = res.listIterator();
			while (iterator.hasNext()) {
				Sign_Doc_Details sign_Doc_Details = iterator.next();
				if (sign_Doc_Details.getSIGN_ORDER() >= 1) {
					isSuccess = true;
				}
			}
			session.close();

		} catch (Exception e) {
			try {
				result.put("status", "FAILURE");
				result.put("statusDetails", e.getMessage());
				result.put("docList", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public boolean insertStatusInEAS(Employee_action_status statusInfo) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.save(statusInfo);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	public JSONObject getDropDownData(String company, String group) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		List groupList = null;
		List docList = null;
		List stateList = null;
		List docNameList = null;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery(
					"SELECT DISTINCT e.compositeKey.EMP_GROUP FROM  Employee_Master e WHERE e.compositeKey.EMP_COMPANY=:company ");
			query.setParameter("company", company);
			groupList = query.list();

			ArrayList<String> groupList1 = new ArrayList<String>();
			ArrayList<String> docList1 = new ArrayList<String>();
			ArrayList<String> docList2 = new ArrayList<String>();

			Query query2 = null;
			String hql = "SELECT DISTINCT e.DOCUMENT_CODE FROM Employee_action_target e  WHERE e.COMPANY_CODE=:company OR e.GROUP_CODE=:group";

			Query query3 = null;
			String hqlquery3 = "SELECT DISTINCT e.DOCUMENT_FULL_NAME FROM Employee_action_target e  WHERE e.COMPANY_CODE=:company OR e.GROUP_CODE=:group";

//			e.DOCUMENT_FULL_NAME
			if (group.trim().length() == 0) {
				for (int i = 0; i < groupList.size(); i++) {
					query2 = session.createQuery(hql);
					query2.setParameter("group", groupList.get(i));
					query2.setParameter("company", company);

					query3 = session.createQuery(hqlquery3);
					query3.setParameter("group", groupList.get(i));
					query3.setParameter("company", company);

					docList = query2.list();
					docNameList = query3.list();
					docList1.addAll(docList);
					docList2.addAll(docNameList);
					groupList1.add((String) groupList.get(i));

				}
			} else {
				query2 = session.createQuery(hql);
				query2.setParameter("group", group);
				groupList1.add(group);
				// stateList1.add(state);
				query2.setParameter("company", company);
				docList = query2.list();
				docList1.addAll(docList);

				query3 = session.createQuery(hqlquery3);
				query3.setParameter("group", group);
				groupList1.add(group);
				// stateList1.add(state);
				query3.setParameter("company", company);
				docNameList = query3.list();
				docList2.addAll(docNameList);
			}

			jsonObject.put("groupList", groupList1);
			jsonObject.put("documentList", docList1);
			jsonObject.put("docNameList", docList2);
			jsonObject.put("empCompany", company);

		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsonObject;
	}

	public JSONObject getDropDownDataforCompany() {
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		List groupList = null;
		List docList = null;
		List stateList = null;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			ArrayList<String> groupList1 = new ArrayList<String>();
			ArrayList<String> companyList1 = new ArrayList<String>();
			String companyCode = "";
			Query query2 = null;
			String hql = "SELECT DISTINCT e.COMPANY_CODE FROM Employee_action_target e WHERE e.COMPANY_CODE!=''";
//					WHERE e.COMPANY_CODE!=companyCode";
//					+ ""; WHERE e.COMPANY_CODE=:company OR e.GROUP_CODE=:group";
//			e.DOCUMENT_FULL_NAME

			query2 = session.createQuery(hql);
			docList = query2.list();
			companyList1.addAll(docList);
			// groupList1.add((String) groupList.get(i));

			jsonObject.put("empCompanyList", companyList1);

		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return jsonObject;
	}

	@Override
	public List<Doc_Sign_Config> getDocSignConfig(String docCode, String signStatus, int signOrder) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsRes = new JSONObject();
		List<Doc_Sign_Config> dsc = null;

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery(
					"FROM Doc_Sign_Config where DOC_CODE =:docCode and SIGN_STATUS =:signStatus and SIGN_ORDER=:signOrder and ENABLE_STATUS='1'");
			query.setParameter("docCode", docCode);
			query.setParameter("signStatus", signStatus);
			query.setParameter("signOrder", String.valueOf(signOrder));
			dsc = query.list();

		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return dsc;
	}

	@Override
	public Dsc_Token_Config getDSCTokenConfig(String dscTokenID) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsRes = new JSONObject();
		Dsc_Token_Config dsc = new Dsc_Token_Config();

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery("FROM Dsc_Token_Config where DSC_TOKEN_ID =:dscTokenID");
			query.setParameter("dscTokenID", dscTokenID);
			List<Dsc_Token_Config> res = query.list();
			ListIterator<Dsc_Token_Config> iterator = res.listIterator();
			while (iterator.hasNext()) {
				dsc = (Dsc_Token_Config) iterator.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return dsc;
	}

	@Override
	public boolean insertSignDocDetails(Sign_Doc_Details signDocDetails) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.save(signDocDetails);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public Sign_Doc_Details checkSignDocDetails(String empid, String empCompany, String empGroup, String docCode) {
		SessionFactory sf = null;
		Session session = null;
		Sign_Doc_Details signDocInfo = null;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery(
					"FROM Sign_Doc_Details where EMPID=:empid and EMP_COMPANY=:empCompany and EMP_GROUP=:empGroup and DOC_CODE=:docCode");
			query.setParameter("empid", empid);
			query.setParameter("empCompany", empCompany);
			query.setParameter("empGroup", empGroup);
			query.setParameter("docCode", docCode);
			List<Sign_Doc_Details> res = query.list();
			ListIterator<Sign_Doc_Details> iterator = res.listIterator();
			while (iterator.hasNext()) {
				signDocInfo = (Sign_Doc_Details) iterator.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return signDocInfo;
	}

	@Override
	public boolean updateSignDocDetails(Sign_Doc_Details signDocDetails) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.update(signDocDetails);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public JSONObject getSignedDocListInSDD(JSONObject inputs) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		ArrayList<JSONObject> alist = new ArrayList<JSONObject>();

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String signOrder = inputs.getString("signOrder");
//			Query query = session.createQuery(
//					"FROM Sign_Doc_Details sd WHERE sd.EMP_COMPANY =:companyCode AND sd.EMP_GROUP =:empGroup AND sd.SIGN_ORDER =:signOdrer");
//			query.setParameter("companyCode", inputs.getString("company"));
//			query.setParameter("empGroup", inputs.getString("group"));
//			query.setParameter("signOdrer", Integer.valueOf(signOrder));
//
//			@SuppressWarnings("unchecked")
//			List<Sign_Doc_Details> res = query.list();
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			if (res.size() > 0) {
//				for (int i = 0; i < res.size(); i++) {
//					Sign_Doc_Details docList = res.get(i);
//					Timestamp date = docList.getSIGN_DATE();
//					String time = simpleDateFormat.format(date.getTime());
//					JSONObject jsobj = new JSONObject();
//					jsobj.put("empid", docList.getEMPID());
//					jsobj.put("docCode", docList.getDOC_CODE());
//					jsobj.put("signDate", time);
//					jsobj.put("txnID", docList.getTXNID());
//					jsobj.put("signOrder", docList.getSIGN_ORDER());
//					jsobj.put("signername", docList.getSIGNER_NAME());
//					jsobj.put("empname", docList.getEMP_NAME());
//					alist.add(jsobj);
//				}
//				result.put("list", alist);
//			} else {
//				result.put("list", alist);
//			}

			if (signOrder.equalsIgnoreCase("0")) {
				Query query1 = session
						.createQuery("SELECT em.EMP_PHONE,em.EMP_EMAIL,em.compositeKey.EMP_ID,em.EMP_NAME "
								+ "FROM Employee_Master em WHERE em.compositeKey.EMP_COMPANY=:companyCode AND em.compositeKey.EMP_GROUP=:empGroup AND "
								+ "em.compositeKey.EMP_ID NOT IN (SELECT sdd.EMPID FROM Sign_Doc_Details sdd WHERE sdd.EMP_COMPANY=:companyCode "
								+ "AND sdd.EMP_GROUP=:empGroup AND sdd.DOC_CODE=:docCode)");

				query1.setParameter("companyCode", inputs.getString("company"));
				query1.setParameter("empGroup", inputs.getString("group"));
//				query1.setParameter("signOdrer", Integer.valueOf(signOrder));
				query1.setParameter("docCode", inputs.getString("docCode"));

				@SuppressWarnings("unchecked")
				List<Object> resp = query1.list();
				Iterator itr = resp.iterator();
				while (itr.hasNext()) {
					Object[] obj = (Object[]) itr.next();
					String phone = String.valueOf(obj[0]);
					String email = String.valueOf(obj[1]);
					String empid = String.valueOf(obj[2]);
					String empname = String.valueOf(obj[3]);
					JSONObject jsobj = new JSONObject();
					jsobj.put("mobileNo", phone);
					jsobj.put("emailID", email);
					jsobj.put("empid", empid);
					jsobj.put("empname", empname);
					jsobj.put("docCode", inputs.getString("docCode"));
					alist.add(jsobj);
				}
				result.put("list", alist);
			} else {
				Query query2 = session.createQuery(
						"SELECT sdd.EMPID,sdd.DOC_CODE,sdd.EMP_NAME,sdd.SIGNER_NAME,sdd.SIGN_DATE,sdd.TXNID, "
								+ "(SELECT EMP_PHONE FROM Employee_Master WHERE EMP_ID=sdd.EMPID AND EMP_GROUP=:empGroup AND EMP_COMPANY=:companyCode) AS phoneNo, "
								+ "(SELECT EMP_EMAIL FROM Employee_Master WHERE EMP_ID=sdd.EMPID AND EMP_GROUP=:empGroup AND EMP_COMPANY=:companyCode) AS emailID "
								+ "FROM Sign_Doc_Details sdd WHERE sdd.EMP_COMPANY=:companyCode AND "
								+ "sdd.EMP_GROUP=:empGroup AND sdd.SIGN_ORDER=:signOdrer AND sdd.DOC_CODE=:docCode");
				query2.setParameter("companyCode", inputs.getString("company"));
				query2.setParameter("empGroup", inputs.getString("group"));
				query2.setParameter("signOdrer", Integer.valueOf(signOrder));
				query2.setParameter("docCode", inputs.getString("docCode"));

				@SuppressWarnings("unchecked")
				List<Object> resp = query2.list();

				Iterator itr = resp.iterator();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				while (itr.hasNext()) {
					Object[] obj = (Object[]) itr.next();
					Timestamp datetime = (Timestamp) obj[4];
					JSONObject jsobj = new JSONObject();
					String date = simpleDateFormat.format(datetime);
					jsobj.put("empid", String.valueOf(obj[0]));
					jsobj.put("docCode", String.valueOf(obj[1]));
					jsobj.put("empname", String.valueOf(obj[2]));
					jsobj.put("signername", String.valueOf(obj[3]));
					jsobj.put("signDate", date);
					jsobj.put("txnID", String.valueOf(obj[5]));
					jsobj.put("mobileNo", String.valueOf(obj[6]));
					jsobj.put("emailID", String.valueOf(obj[7]));
					alist.add(jsobj);
				}
				result.put("list", alist);
			}
			session.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				result.put("status", "FAILURE");
				result.put("statusDetails", e.getMessage());
				result.put("list", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}

		}
		return result;
	}

	@Override
	public Sign_Doc_Details getSignedDocListTxnid(String txnid) {
		SessionFactory sf = null;
		Session session = null;
		Sign_Doc_Details signDocInfo = null;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("FROM Sign_Doc_Details sd WHERE sd.TXNID =:txnid");
			query.setParameter("txnid", txnid);
			@SuppressWarnings("unchecked")
			List<Sign_Doc_Details> res = query.list();
			ListIterator<Sign_Doc_Details> iterator = res.listIterator();
			while (iterator.hasNext()) {
				signDocInfo = (Sign_Doc_Details) iterator.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return signDocInfo;
	}

	@Override
	public JSONObject getEmailDetails(String usertype, JSONArray contentList) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject resp = new JSONObject();
		sf = HibernateUtil.getSessionFactory();
		session = sf.openSession();
		try {
			for (int i = 0; i < contentList.length(); i++) {
				String employeeId = contentList.getJSONObject(i).getString("empid");
				Query query = session.createQuery("FROM Sign_Doc_Details where EMPID =:refno");
				query.setParameter("refno", employeeId);
				@SuppressWarnings("unchecked")
				List<Sign_Doc_Details> signlog_res = query.list();
				String empid = null;
				String path = null;
				for (Sign_Doc_Details sign : signlog_res) {
					empid = sign.getEMPID();
					path = sign.getDOC_PATH();
				}
				Query query2 = session.createQuery(
						"from Employee_Master M where M.compositeKey.EMP_ID=:empid and M.SIGN_STATUS=:status");
				query2.setParameter("empid", empid);
				query2.setParameter("status", 1);
				@SuppressWarnings("unchecked")
				List<Employee_Master> empmaster_res = query2.list();
				if (!empmaster_res.isEmpty()) {
					for (Employee_Master emp : empmaster_res) {
						try {
							resp.put("path", path);
							resp.put("name", emp.getEMP_NAME());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			session.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return resp;
	}

	@Override
	public boolean insertintoUserRequests(JSONObject inputs) {

		User_Requests userReq = new User_Requests();
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;

		try {
			// Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			// System.out.println(inputs.getString("empdob"));
			// java.sql.Date date=new
			// java.sql.Date(Long.valueOf(inputs.getString("empdob")));
			// System.out.println(date);
			userReq.setVERIFIED_STATUS("00");
			userReq.setEMP_DOB(inputs.getString("empDOB"));
			userReq.setEMP_ID(inputs.getString("empId"));
			userReq.setREQUESTED_TIME(new Timestamp(System.currentTimeMillis()));
			userReq.setEMP_GROUP(inputs.getString("empGroup"));
			userReq.setEMP_NAME(inputs.getString("empName"));
//			userReq.setUSER_EMAIL("");
			userReq.setMOBILE_AADHAAR(inputs.getString("mobAadhar"));
			userReq.setMOBILE_MESSAGING(inputs.getString("mobMessaging"));
			userReq.setUPDATE_REQ_TYPE(inputs.getString("updateType"));

			// userReq.setVERIFIED_TIME("0000-00-00 00:00:00");
			// UR.setVERIFIED_BY(inputs.getString("verifiedBy"));

			userReq.setIP_ADDRESS(inputs.getString("ipAdd"));

			try {
				sf = HibernateUtil.getSessionFactory();
				session = sf.openSession();
				tx = session.getTransaction();
				tx.begin();
				session.save(userReq);
				tx.commit();
				isSuccess = true;
			} catch (Exception e) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return isSuccess;

	}

	public boolean updateUserDetails(Employee_Master empData) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.update(empData);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public User_Requests getUserRequestDetails(String empid, String reqType) {
		// String reqType
		String reqType1 = "10";
		String reqType2 = "11";
		SessionFactory sf = null;
		Session session = null;
		User_Requests userInfo = null;

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("FROM User_Requests  WHERE  EMP_ID=:empid AND UPDATE_REQ_TYPE =:reqType");
			query.setParameter("empid", empid);
			query.setParameter("reqType", reqType);
//				query.setParameter("reqType2", reqType2);
			@SuppressWarnings("unchecked")
			List<User_Requests> res = query.list();
			// return res;
			ListIterator<User_Requests> iterator = res.listIterator();
			while (iterator.hasNext()) {
				userInfo = (User_Requests) iterator.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return userInfo;
	}

	public boolean updateUserRequests(User_Requests userData) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.update(userData);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	public List<Object> userNameUpdationList(String reqType1, String reqType2, String empCompany) {

		SessionFactory sf = null;
		Session session = null;

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

//					" SELECT userreq.*,(SELECT EMP_NAME from Employee_Master M where M.EMP_ID = userreq.EMP_ID) as EXISTING_NAME "
//							+ "from (SELECT * from User_Requests where UPDATE_REQ_TYPE IN ('10','11') AND VERIFIED_STATUS IN('00','01')) as userreq");
//			
			Query query = session.createSQLQuery(
					"SELECT userreq.*,(SELECT EMP_NAME FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP) AS EXISTING_NAME,\r\n"
							+ "(SELECT CASE WHEN JSON_VALID(M.ADDITIONAL_DATA) THEN JSON_UNQUOTE(M.ADDITIONAL_DATA->'$.state')ELSE NULL END  FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP)AS STATE,\r\n"
							+ "(SELECT CASE WHEN JSON_VALID(M.ADDITIONAL_DATA) THEN JSON_UNQUOTE(M.ADDITIONAL_DATA->'$.bank')ELSE NULL END  FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP)AS NAME_OF_BANK,\r\n"
							+ "(SELECT CASE WHEN JSON_VALID(M.ADDITIONAL_DATA) THEN JSON_UNQUOTE(M.ADDITIONAL_DATA->'$.district')ELSE NULL END  FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP)AS DISTRICT,\r\n"
							+ "(SELECT CASE WHEN JSON_VALID(M.ADDITIONAL_DATA) THEN JSON_UNQUOTE(M.ADDITIONAL_DATA->'$.branch')ELSE NULL END  FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP)AS BRANCH,\r\n"
							+ "(SELECT CASE WHEN JSON_VALID(M.ADDITIONAL_DATA) THEN JSON_UNQUOTE(M.ADDITIONAL_DATA->'$.bcLocation')ELSE NULL END  FROM Employee_Master M WHERE M.EMP_ID = userreq.EMP_ID AND EMP_COMPANY =:empCompany AND  EMP_GROUP=userreq.EMP_GROUP)AS BC_LOCATION "
							+ "FROM (SELECT * FROM User_Requests WHERE UPDATE_REQ_TYPE IN ('10','11') AND VERIFIED_STATUS IN('00','01')) as userreq");

			query.setParameter("empCompany", empCompany);
			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object> res = query.list();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return null;
//		
	}

	@Override
	public List<User_Requests> getUserRequestList(String empid) {

		SessionFactory sf = null;
		Session session = null;
		// User_Requests userInfo = null;

//		 reqType = ["11","10"];
		String reqType2 = "10";
//		String verifiedStatus1="00";
//		String verifiedStatus2="01";
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String sql = "FROM User_Requests WHERE EMP_ID=:empid AND UPDATE_REQ_TYPE IN ('11','10') AND VERIFIED_STATUS IN('00','01')";
			Query query = session.createQuery(sql);
//			Query query = session
//					.createQuery("FROM User_Requests  WHERE  EMP_ID=:empid AND VERIFIED_STATUS !=:reqType AND UPDATE_REQ_TYPE =: ");
			query.setParameter("empid", empid);
//			query.setParameter("reqType1", reqType1);
//			query.setParameter("reqType2", reqType2);

			// query.setParameter("reqType2", reqType2);
			@SuppressWarnings("unchecked")
			List<User_Requests> res = query.list();
			return res;
//						ListIterator<User_Requests> iterator = res.listIterator();
//						while (iterator.hasNext()) {
//							userInfo = (User_Requests) iterator.next();
//						}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return null;
	}

	@Override
	public boolean insertintoRejectionList(Sign_Rejected_Info sign_rejected_info) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.save(sign_rejected_info);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	@Override
	public List<Sign_Rejected_Info> getRejectedList(String empid) {

		SessionFactory sf = null;
		Session session = null;
		// User_Requests userInfo = null;
		int reqType = 0;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session
					.createQuery("FROM Sign_Rejected_Info  WHERE  EMP_ID=:empid AND PENALTY_STATUS =:reqType ");
			query.setParameter("empid", empid);
			query.setParameter("reqType", reqType);
			// query.setParameter("reqType2", reqType2);
			@SuppressWarnings("unchecked")
			List<Sign_Rejected_Info> res = query.list();
			return res;
//						ListIterator<User_Requests> iterator = res.listIterator();
//						while (iterator.hasNext()) {
//							userInfo = (User_Requests) iterator.next();
//						}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return null;
	}

	@Override
	public List<Action_Master_Lang> getlangInfo(String docCode) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			int reqType = 1;
			Query query = session
					.createQuery("FROM Action_Master_Lang WHERE DOC_CODE=:docCode AND ENABLE_STATUS=:reqType");
			query.setParameter("docCode", docCode);
			query.setParameter("reqType", reqType);
			@SuppressWarnings("unchecked")
			List<Action_Master_Lang> res = query.list();
			return res;
			// java.util.ListIterator<Action_Master_Lang> iterator = res.listIterator();

//			while (iterator.hasNext()) {
//				Action_Master_Lang docList = iterator.next();
//				result.put("status", "");
//				result.put("statusDetails", "");
//				result.put("langName", docList.getLANG_NAME());
//				result.put("langCode", docList.getLANG_CODE());
//				result.put("docDate", docList.getDOCUMENT_DATE());
//				result.put("fileName", docList.getFILE_NAME());
//
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return null;

	}

	@Override
	public List<Action_Master_Lang> getlangFilePath(String langName, String docCode) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session
					.createQuery("FROM Action_Master_Lang WHERE LANG_NAME=:langName AND DOC_CODE=:docCode");
			// SELECT FILE_NAME
			query.setParameter("langName", langName);
			query.setParameter("docCode", docCode);
			// query.setParameter("reqType", reqType);
			@SuppressWarnings("unchecked")
			List<Action_Master_Lang> res = query.list();
			return res;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return null;

	}

	@Override
	public JSONObject bcstatewisesigningstatus() {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		ArrayList<JSONObject> alist = new ArrayList<JSONObject>();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
//			String hql = "SELECT bank,state,district,bcid,bcname,bcmobile,signstatus,signdate,bcaDataUploadedOn,bcaActiveStatus,PAN FROM \n\r"
//					+ "(SELECT bank,state,district,bcid,bcname,bcmobile,CASE WHEN signstatus=0 THEN 'not signed'WHEN signstatus=1 THEN 'BC signed' WHEN signstatus=2 THEN 'BC & Witness signed'\r\n"
//					+ " WHEN signstatus=3 THEN 'completed' ELSE 'check' END AS signstatus,signdate,bcaDataUploadedOn,bcaActiveStatus,PAN\r\n"
//					+ " FROM \r\n"
//					+ "(SELECT em.EMP_GROUP as bank,em.EMP_ID as bcid,em.EMP_NAME as bcname,em.EMP_PHONE as bcmobile,\r\n"
//					+ "IFNULL(sdd.SIGN_ORDER,0) signstatus,\r\n"
//					+ "IFNULL(DATE_FORMAT(sign_date,'%Y-%m-%d'),'')signdate,\r\n"
//					+ "IFNULL(DATE_FORMAT(em.CREATED_ON,'%Y-%m-%d'),'')bcaDataUploadedOn,\r\n"
//					+ "(SELECT CASE WHEN em.STATUS=0 THEN 'Blocked' WHEN em.STATUS=1 THEN 'Active' ELSE 'check' END) AS bcaActiveStatus,\r\n"
//					+ "em.EMP_COMPANY,\r\n"
//					+ "(SELECT CASE WHEN JSON_VALID(em.ADDITIONAL_DATA) THEN JSON_UNQUOTE(em.ADDITIONAL_DATA->'$.state')ELSE NULL END )AS state,\r\n"
//					+ "(SELECT CASE WHEN JSON_VALID(em.ADDITIONAL_DATA) THEN JSON_UNQUOTE(em.ADDITIONAL_DATA->'$.district')ELSE NULL END )AS district,\r\n"
//					+ "(SELECT CASE WHEN JSON_VALID(em.ADDITIONAL_DATA) THEN JSON_UNQUOTE(em.ADDITIONAL_DATA->'$.pan')ELSE NULL END )AS PAN\r\n"
//					+ "FROM Employee_Master em LEFT JOIN Sign_Doc_Details sdd ON sdd.EMPID=em.EMP_ID WHERE em.`EMP_COMPANY` ='i25BCA' AND em.EMP_ID NOT LIKE '999999%')as t1)t2  ORDER BY bank,state,district,signstatus";

			
			
			Query query = session.createSQLQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object> res = query.list();
			Iterator itr = res.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				
				for(int i=0;i<obj.length;i++) {
					System.out.println(i+" - "+obj[i]);
				}
				
				
				JSONObject jsobj = new JSONObject();
				jsobj.put("bank", String.valueOf(obj[0]));
				
				jsobj.put("state", String.valueOf(obj[1]));
				jsobj.put("district", String.valueOf(obj[2]));
				jsobj.put("bcid", String.valueOf(obj[3]));
				jsobj.put("bcname", String.valueOf(obj[4]));
				jsobj.put("bcmobile", String.valueOf(obj[5]));
				jsobj.put("signstatus", String.valueOf(obj[6]));
				jsobj.put("signdate", String.valueOf(obj[7]));
				jsobj.put("bcaDataUploadedOn", String.valueOf(obj[8]));
				jsobj.put("bcaActiveStatus", String.valueOf(obj[9]));
				jsobj.put("PAN", String.valueOf(obj[10]));
				jsobj.put("docCode", String.valueOf(obj[11]));

				alist.add(jsobj);

			}

			result.put("list", alist);
			System.out.println("resultresultresult:::"+result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return null;
	}

	@Override
	public JSONObject bcsignrejectiondetails() {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		ArrayList<JSONObject> alist = new ArrayList<JSONObject>();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String hql = "SELECT bank,state,bcid,\r\n"
					+ "(SELECT emp_name FROM employee_master em WHERE em.emp_id = bcid AND emp_group=bank) AS empname,\r\n"
					+ "(SELECT `EMP_PHONE` FROM employee_master em WHERE em.emp_id  = bcid AND emp_group=bank) AS empmobile,\r\n"
					+ "COUNT( *) rejection_count,COUNT( *)*50 rejection_charges FROM (	\r\n"
					+ " SELECT bank,state,bcid FROM \r\n"
					+ "(SELECT  em.EMP_NAME AS bcname, em.EMP_ID AS bcid,em.EMP_COMPANY,em.EMP_GROUP AS bank,em.EMP_PHONE AS bcmobile,\r\n"
					+ "(SELECT CASE WHEN JSON_VALID(em.ADDITIONAL_DATA) THEN JSON_UNQUOTE(em.ADDITIONAL_DATA->'$.state')ELSE NULL END )AS state\r\n"
					+ "FROM Employee_Master em  INNER JOIN sign_rejected_info sdd ON em.EMP_ID=sdd.EMP_ID  WHERE  em.EMP_ID=sdd.EMP_ID AND em.emp_id NOT LIKE '9999999%'\r\n"
					+ ") AS t1)t2 GROUP BY bank,bcid,state";

			Query query = session.createSQLQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object> res = query.list();
			Iterator itr = res.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				JSONObject jsobj = new JSONObject();
				jsobj.put("bank", String.valueOf(obj[0]));
				jsobj.put("state", String.valueOf(obj[1]));
				jsobj.put("bcid", String.valueOf(obj[2]));
				jsobj.put("bcname", String.valueOf(obj[3]));
				jsobj.put("bcmobile", String.valueOf(obj[4]));
				jsobj.put("rejection_count", String.valueOf(obj[5]));
				jsobj.put(" rejection_charges", String.valueOf(obj[6]));
				alist.add(jsobj);
			}

			result.put("list", alist);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return null;
	}

	public boolean updateBcCellIp(Dsc_Token_Config dscTokenConfig) {
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.update(dscTokenConfig);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	// getting empl list from empl master table
	@Override
	public JSONObject getEmplList(JSONObject input) {
		SessionFactory sf = null;
		Session session = null;
		JSONObject result = new JSONObject();
		ArrayList<JSONObject> alist = new ArrayList<JSONObject>();

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String signOrder = input.getString("group");

			Query query1 = session
					.createQuery("SELECT em.EMP_PHONE,em.EMP_EMAIL, em.compositeKey.EMP_ID,em.EMP_NAME, em.STATUS "
							+ "FROM Employee_Master em WHERE em.compositeKey.EMP_GROUP=:empGroup");

			query1.setParameter("empGroup", input.getString("group"));

			@SuppressWarnings("unchecked")
			List<Object> resp = query1.list();
			Iterator itr = resp.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				String phone = String.valueOf(obj[0]);
				String email = String.valueOf(obj[1]);
				String empid = String.valueOf(obj[2]);
				String empname = String.valueOf(obj[3]);
				String empStatus = String.valueOf(obj[4]);

				JSONObject jsobj = new JSONObject();
				jsobj.put("mobileNo", phone);
				jsobj.put("emailID", email);
				jsobj.put("empid", empid);
				jsobj.put("empname", empname);
				jsobj.put("empStatus", empStatus);
				alist.add(jsobj);
			}
			result.put("list", alist);

			session.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				result.put("status", "FAILURE");
				result.put("statusDetails", e.getMessage());
				result.put("list", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != session && session.isOpen()) {
				session.close();
			}

		}
		return result;
	}

	// update user status
	@Override
	public JSONObject updateUserStatus(JSONObject input) {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		JSONObject jsres = new JSONObject();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
//			System.out.println(input);
//			System.out.println(input.get("empID"));
			Query query = session.createQuery("UPDATE Employee_Master SET STATUS =:empStatus WHERE EMP_ID = :empid");
			query.setParameter("empStatus", Integer.parseInt(input.getString("empStatus")));
			query.setParameter("empid", input.getString("empID"));
			int out = query.executeUpdate();

//			System.out.println(out);
			tx.commit();
			if (out == 1) {
				jsres.put("status", "SUCCESS");
				if (Integer.parseInt(input.getString("empStatus")) == 0) {
					jsres.put("statusDetails", "User account disabled successfully");
				} else {
					jsres.put("statusDetails", "User account enabled successfully");
				}
			} else {
				jsres.put("status", "Failure");
				if (Integer.parseInt(input.getString("empStatus")) == 0) {
					jsres.put("statusDetails", "Error at user account disable, try after sometime again! ");
				} else {
					jsres.put("statusDetails", "Error at user account enable, try after sometime again!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return jsres;
	}

	@Override
	public boolean updateLogdetails(Audit_Info userLogInfo) {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		boolean isSuccess = false;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			session.save(userLogInfo);
			tx.commit();
			isSuccess = true;
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
//	            e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return isSuccess;
	}

	// based on userid find user details from user_master table
	@Override
	public JSONObject getLoginUserDetails(String username) {
		SessionFactory sf = null;
		Session session = null;
		Boolean res = false;
		JSONObject jsRes = new JSONObject();
		try {
			String userName = username;
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("from User_Master U where U.USER_NAME=:userName");
			query.setParameter("userName", userName);

			List<User_Master> result = query.list();
			session.close();

			if (!result.isEmpty()) {
				for (User_Master user : result) {
					res = true;
					jsRes.put("status", "SUCCESS");
					jsRes.put("statusDetails", "user found");
					jsRes.put("roleId", user.getROLE_ID());
					jsRes.put("privilegeCode", user.getPRIVILEGE_CODE());
					jsRes.put("company", user.getCOMPANY());
					jsRes.put("empgroup", user.getEMP_GROUP());
				}
			} else {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", "User not found");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			try {
				jsRes.put("status", "FAILURE");
				jsRes.put("statusDetails", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsRes;
	}

	public static List<Template_Master> getListofTemplates() {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		List list = new ArrayList<>();

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery("From Template_Master");
			list = query.list();
//			System.out.println(list);
		} catch (Exception e) {

			e.printStackTrace();
			System.out.println(e);// TODO: handle exception
		}

		return list;
	}

	public static List<Field_Validation> getFieldList() {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		List<Field_Validation> list = new ArrayList<>();

		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery("From Field_Validation");
			list = query.list();
//			System.out.println(list);
		} catch (Exception e) {

			e.printStackTrace();
			System.out.println(e);// TODO: handle exception
		}

		return list;
	}

	public static List<Company_Master> getCompanyInfo() {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		List<Company_Master> companyMaster = new ArrayList<>();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();

			Query query = session.createQuery("From Company_Master");
			companyMaster = query.list();

		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

		return companyMaster;
	}

	// get DropDown Data From CompanyMaster
	public static JSONObject getDropDownDataFromCompanyMaster() {
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		List companyNameList = new ArrayList<String>();
		List companyCodeList = new ArrayList<String>();

		List groupCodeList = new ArrayList<String>();
		List groupNameList = new ArrayList<String>();
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			String hql = "SELECT DISTINCT GROUP_CODE FROM company_master";
			Query query = session.createSQLQuery(hql);
			groupCodeList = query.list();
			String hql1 = "SELECT DISTINCT COMPANY_NAME FROM company_master";
			Query query1 = session.createSQLQuery(hql1);
			companyNameList = query1.list();

			String hql2 = "SELECT DISTINCT COMPANY_CODE FROM company_master";
			Query query2 = session.createSQLQuery(hql2);
			companyCodeList = query2.list();

			String hql3 = "SELECT DISTINCT GROUP_NAME FROM company_master";
			Query query3 = session.createSQLQuery(hql3);
			groupNameList = query3.list();

			jsonObject.put("groupCodeList", groupCodeList);
			jsonObject.put("companyNameList", companyNameList);
			jsonObject.put("companyCodeList", companyCodeList);
			jsonObject.put("groupNameList", groupNameList);

		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsonObject;
	}

	@Override
	public JSONObject SaveEmployeeData(JSONObject inputsData) {
		// TODO Auto-generated method stub
		SessionFactory sf = null;
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		Transaction tx = null;
		try {

			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();

			Employee_Master dataEmp = new Employee_Master();

			dataEmp = (Employee_Master) inputsData.get("empData");
//			System.out.println(dataEmp);
			tx.begin();

			session.save(dataEmp);
			tx.commit();
			jsonObject.put("status", "SUCCESS");
			jsonObject.put("failureReason","");
		} catch (Exception e) {
			try {
				jsonObject.put("failureReason",e);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

		return jsonObject;
	}

	@Override
	public void bulkDataUploadLogs(Bulk_Data_Upload_Logs res) {
		SessionFactory sf = null;
		Session session = null;
	
		Transaction tx = null;
		try {

			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();

			Employee_Master dataEmp = new Employee_Master();

			
//			System.out.println(dataEmp);
			tx.begin();

			session.save(res);
			tx.commit();
		
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}

	
	}
	
	
	
	
	
	
	public JSONObject getEmployeeRepeatedID(String empID, String empGrp) {
		JSONObject jsRes = new JSONObject();
		SessionFactory sf = null;
		Session session = null;
		
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("SELECT  M.compositeKey.EMP_ID FROM Employee_Master M where M.compositeKey.EMP_ID LIKE :empidPattern AND M.compositeKey.EMP_GROUP = :empGroup");
			query.setParameter("empidPattern", "%"+empID+"%");
				query.setParameter("empGroup", empGrp);
				List<Employee_Master> result = query.list();
				session.close();
				jsRes.put("status", "success");
				jsRes.put("lastOccurance", result.get(result.size()-1));
}
		catch (Exception e) {
				try {
					jsRes.put("status", "Failure");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				
		}
		return jsRes;
	}
	
	
	//updating the newID to employeeMaster 
	@Override
	public JSONObject removeBC(int numberToAppend, String empID, String empGrp) throws JSONException {
		
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		JSONObject jsres = new JSONObject();
		String newEmpId=empID.concat("-").concat(String.valueOf(numberToAppend));
		jsres.put("newEmpId",newEmpId);
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("UPDATE Employee_Master SET EMP_ID =:newempid, STATUS =:empstatus WHERE EMP_ID =:empid");
			query.setParameter("newempid", newEmpId);
			query.setParameter("empstatus",2);
			query.setParameter("empid", empID);
			
			int out = query.executeUpdate();
			tx.commit();
			if (out == 1) {
				jsres.put("status", "SUCCESS");
					jsres.put("statusDetails", "User account removed successfully");
				
			} else {
				jsres.put("status", "Failure");
					jsres.put("statusDetails", "Error at user account deletion, try after sometime again! ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsres;
		
		
	}
	
	
	
	@Override
	public JSONObject removeBCInSignDocDetails(int numberToAppend, String empID, String empGrp) throws JSONException {
		
		SessionFactory sf = null;
		Session session = null;
		Transaction tx = null;
		JSONObject jsres = new JSONObject();
		String newEmpId=empID.concat("-").concat(String.valueOf(numberToAppend));
		jsres.put("newEmpId",newEmpId);
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("UPDATE  Sign_Doc_Details SET EMPID =:newempid WHERE EMPID =:empid and EMP_GROUP=:empgrp");
			query.setParameter("newempid", newEmpId);
			query.setParameter("empid", empID);
			query.setParameter("empgrp",empGrp);
			
			int out = query.executeUpdate();
			tx.commit();
			if (out == 1) {
				jsres.put("status", "SUCCESS");
					jsres.put("statusDetails", "User account removed successfully");
				
			} else {
				jsres.put("status", "Failure");
					jsres.put("statusDetails", "Error at user account deletion, try after sometime again! ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (tx.isActive()) {
				tx.rollback();
			}
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return jsres;
		
		
	}
	
	
	
	@Override
	public ArrayList<Sign_Doc_Details> getSignedDocListEmpidAndEmpGrp(String empid,String Empgrp) {
		SessionFactory sf = null;
		Session session = null;
		Sign_Doc_Details signDocInfo = null;
		ArrayList<Sign_Doc_Details> signDocDetails=new ArrayList<>();;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
			Query query = session.createQuery("FROM Sign_Doc_Details  WHERE EMPID =:empid and EMP_GROUP=:empgrp");
			query.setParameter("empid", empid);
			query.setParameter("empgrp", Empgrp);
			@SuppressWarnings("unchecked")
			List<Sign_Doc_Details> res = query.list();
			ListIterator<Sign_Doc_Details> iterator = res.listIterator();
			
			while (iterator.hasNext()) {
				signDocInfo = (Sign_Doc_Details) iterator.next();
				signDocDetails.add(signDocInfo);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return signDocDetails;
	}
	
	
	
	
	
	
	@Override
	public ArrayList<Company_Master> getBankInfo() {
		SessionFactory sf = null;
		Session session = null;
		Company_Master companyMaster = null;
		ArrayList<Company_Master> banksData=new ArrayList<>();;
		try {
			sf = HibernateUtil.getSessionFactory();
			session = sf.openSession();
//			Query query = session.createQuery("FROM Sign_Doc_Details ");
			Query query = session.createQuery("FROM Company_Master where COMPANY_CODE!=:companyCOde");
			query.setParameter("companyCOde", "iMFAST");
			
			@SuppressWarnings("unchecked")
			List<Company_Master> res = query.list();
			ListIterator<Company_Master> iterator = res.listIterator();
			
			while (iterator.hasNext()) {
				companyMaster = (Company_Master) iterator.next();
				banksData.add(companyMaster);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}

		}
		return banksData;
	}
	
	
	
	

}