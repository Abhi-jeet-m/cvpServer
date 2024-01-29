package com.integra.pledgeapp.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.notification.NotificationContent;
import com.integra.pledgeapp.notification.NotificationEmail;

public class EmailThread extends Thread {
	JSONArray datalist = new JSONArray();
	String type = null;
	JSONObject input = new JSONObject();
	String cc = null;
	JSONObject emailResp = null;
	JSONArray fileData = null;

	public EmailThread(JSONArray list, String type, JSONObject inputs, String hrConfig) {
		this.datalist = list;
		this.type = type;
		this.input = inputs;
		this.cc = hrConfig;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < datalist.length(); i++) {
				String receiver = datalist.getJSONObject(i).getString("emailID");
				String emailContent = NotificationContent.getEmailContent(type, input);
				String subject = NotificationContent.getEmailSubject(type, input);
				emailResp = NotificationEmail.sendEmail(emailContent, subject, receiver, cc, fileData);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
