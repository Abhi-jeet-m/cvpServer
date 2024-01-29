package com.integra.pledgeapp.utilities;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class ContextObjects {

	static JSONObject contextObjects = new JSONObject();
    public static JSONObject authTokens = new JSONObject();

    public static JSONObject getAuthTokens() {
            return authTokens;

    }

    public static void setAuthTokens(String authtoken, JSONObject values) {
        try {
                authTokens.put(authtoken, values);
        } catch (JSONException ex) {
            System.out.println("[ContextObjects][setAuthTokens] JSONException " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("[ContextObjects][setAuthTokens] Exception " + ex.getMessage());
        }
    }

    public static void clearAuthTokens(String key) {
        try {
                authTokens.remove(key);

        } catch (Exception ex) {
            System.out.println("[ContextObjects][clearAuthTokens] Exception " + ex.getMessage());
        }
    }

    public static JSONObject getContextObjects() {
            return contextObjects;

    }

    public static void setContextObjects(JSONObject inputs, String key) {
        try {
                contextObjects.put(key, inputs);
        } catch (JSONException ex) {
            System.out.println("[ContextObjects][setContextObjects] JSONException " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("[ContextObjects][setContextObjects] Exception " + ex.getMessage());
        }
    }

    public static void clearContextObjects(String key) {
        try {
             contextObjects.remove(key);
        } catch (Exception ex) {
            System.out.println("[ContextObjects][clearContextObjects] Exception " + ex.getMessage());
        }
    }
    
    public static boolean validateAuth(JSONObject empDetails) {
    	boolean isValid = false;
    	try {
	    	String authToken = empDetails.getString("authToken");
			JSONObject tokens = ContextObjects.getAuthTokens();
			if(tokens.has(authToken)) {
				JSONObject authTokenDetails = new JSONObject(tokens.getString(authToken));
				if(authTokenDetails.getString("empname").equals(empDetails.getString("empname")) &&
	     		   authTokenDetails.getString("empid").equals(empDetails.getString("empid")) &&
	       		   authTokenDetails.getString("empcompany").equals(empDetails.getString("empcompany"))) {
					JSONObject empAuthDetail = new JSONObject();
					empAuthDetail.put("empname", empDetails.getString("empname"));
					empAuthDetail.put("empid", empDetails.getString("empid"));
					empAuthDetail.put("empcompany", empDetails.getString("empcompany"));
					empAuthDetail.put("otpValidateTime", new Date()); // overriding otpValidateTime while validating
					ContextObjects.setAuthTokens(authToken, empAuthDetail);
					System.out.println("Validation Success");
					isValid = true;
				}
			} else {
				System.out.println("Auth Token is not present");
			}
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    	} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return isValid;
    }
}
