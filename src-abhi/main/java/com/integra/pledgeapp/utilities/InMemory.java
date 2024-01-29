package com.integra.pledgeapp.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONException;
import org.json.JSONObject;

public class InMemory {

	public static HashMap<String, Object> templateData = new HashMap<String, Object>();


	

	public static HashMap<String, Object> gettemplateData() {
		return templateData;
	}

	public static void settemplateData(String key, Object value) {
		try {
			templateData.put(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	

	

	
}
