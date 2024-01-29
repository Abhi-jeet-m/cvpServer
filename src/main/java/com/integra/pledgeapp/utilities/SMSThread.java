package com.integra.pledgeapp.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.core.PledgeAppDAOImpl;
import com.integra.pledgeapp.core.PledgeAppDAOServices;
import com.integra.pledgeapp.notification.NotificationContent;
import com.integra.pledgeapp.notification.NotificationSMS;

public class SMSThread extends Thread {
	JSONArray datalist = new JSONArray();
	String type = null;
	JSONObject input = new JSONObject();
	String smsResp = null;
	PledgeAppDAOServices PDAOS = new PledgeAppDAOImpl();

	public SMSThread(JSONArray list, String type, JSONObject inputs) {
		this.datalist = list;
		this.type = type;
		this.input = inputs;
		
	}

	@Override
	public void run() {
		try {
			for (int j = 0; j < datalist.length(); j++) {
					String phoneno = datalist.getJSONObject(j).getString("mobileNo");
					String smsContent = NotificationContent.getSMSContent(type, input);
					smsResp = NotificationSMS.sendSMS(smsContent, phoneno);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}