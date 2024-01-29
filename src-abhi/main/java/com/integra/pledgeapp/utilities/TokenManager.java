package com.integra.pledgeapp.utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenManager {

	public static JSONObject tokenContainer = new JSONObject();

	public static void addToken(String token) {
		try {
			tokenContainer.put(token,"");
			TokenCleaner OTC = new TokenCleaner();
			OTC.token = token;
			OTC.start();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static void removeToken(String token) {

		if (tokenContainer.has(token)) {
			tokenContainer.remove(token);
		}

	}

	public  static boolean  validateToken(String token) {
		boolean resp = false;
		if (tokenContainer.has(token)) {
			resp = true;
			removeToken(token);
		}
		return resp;
	}
}
