package com.integra.pledgeapp.notification;

import org.json.JSONException;
import org.json.JSONObject;

import com.integra.pledgeapp.beans.Employee_Master;
import com.integra.pledgeapp.utilities.MailHandler;
import com.integra.pledgeapp.utilities.Properties_Loader;

public class NotificationContent {
	public static String getSMSContent(String type, JSONObject inputs) {
		String str = null;
		String signOrder = null;
		String smsprovider = Properties_Loader.SMSPROVIDER;

		try {
			if (inputs.has("loginmode")) {
				if (smsprovider.equalsIgnoreCase("AQUA")) {

					String msg = "Your%20OTP%20for%20Verification%20with%20Mobile%20No.%20ref%20%20%20is%20"
							+ inputs.getString("otp") + "%20Integra";
					// This is for Production AquaSMS
//					System.out.println("inside aqua");
					// System.out.println(msgs);
				} else if (smsprovider.equalsIgnoreCase("ROUTEMOBILES")) {
//					System.out.println("outside aqua");
					if (inputs.has("eventType")) {
						if (inputs.getString("eventType").equalsIgnoreCase("01")
								|| inputs.getString("eventType").equalsIgnoreCase("11")) {
							str = "Your%20OTP%20for%20mobile%20number%20change%20is%20" + inputs.getString("otp")
									+ ".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20BC%20Cell%0AIntegra";

						}
					} else {
						str = "Your%20OTP%20for%20login%20is%20" + inputs.getString("otp")
								+ ".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20Admin%0AIntegra";

					}

				} else {
					str = "Your%20OTP%20for%20login%20is%20" + inputs.getString("otp")
							+ ".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20Admin%0AIntegra";
				}
			} else if (inputs.has("updateType")) {
				if (smsprovider.equalsIgnoreCase("AQUA")) {

					str = "Your%20OTP%20for%20Verification%20with%20Mobile%20No.%20ref%20%20%20is%20"
							+ inputs.getString("otp") + "%20Integra";
				} else if (smsprovider.equalsIgnoreCase("ROUTEMOBILES")) {
//					System.out.println("outside aqua");
					String msg = "";
					if (inputs.getString("updateType").equalsIgnoreCase("01")
							|| inputs.getString("updateType").equalsIgnoreCase("11")) {
						str = "Your%20OTP%20for%20mobile%20number%20change%20is%20" + inputs.getString("otp")
								+ ".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20BC%20Cell%0AIntegra";

					} else {
						str = "Your%20OTP%20for%20login%20is%20" + inputs.getString("otp")
								+ ".%20It%20is%20valid%20for%2010%20mins.%20Do%20not%20share%20with%20anyone.%20Admin%0AIntegra";
					}
				} else {

				}
			}
			else {
				signOrder = inputs.getString("signOrder");

				if (smsprovider.equalsIgnoreCase("AQUA")) {

					if (signOrder.equals("0")) {
						str = "Hi,%20Please%20complete%20the%20digital%20signing%20of%20BCA%20Agreement.%20Visit%20https://bcsign.docuexec.com/cvp/bclogin";
					} else {
						str = "Hi,%20Please%20complete%20the%20witness%20signing%20of%20BCA%20Agreement.%20Visit%20https://bcsign.docuexec.com/cvp/bclogin";
					}
//				String msg ="Your%20OTP%20for%20Verification%20with%20Mobile%20No.%20ref%20%20%20is%20" + otp
//						+ "%20Integra";
					// This is for Production AquaSMS
//				System.out.println("inside aqua");

					// System.out.println(msgs);
				} else if (smsprovider.equalsIgnoreCase("ROUTEMOBILES")) {
//				System.out.println("outside aqua");
					if (signOrder.equals("0")) {
						str = "Your%20BCA%20agreement%20for%20Digital%20Signing%20with%20Integra%20ref%20X%20is%20pending.%20Please%20complete%20soon.BC%20Cell%0AIntegra";
					} else {
						str = "Your%20BCA%20agreement%20for%20witness%20signing%20with%20Integra%20ref%20X%20is%20pending.%20Please%20complete%20soon.BC%20Cell%0AIntegra";

					}
				} else {
					// other sms service provider
				}
			}

		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getEmailContent(String type, JSONObject inputs) {
		String str = null;
		try {
			if (inputs.has("loginmode")) {
				String fullName = inputs.getString("fullName");

				str = "Dear  " + fullName + ",\n\r\n" + "The One Time Password (OTP) for your login is "
						+ inputs.getString("otp") + "." + "\r\n" + "\r\n"
						+ "This OTP is valid for 30 minutes or 1 successful attempt whichever is earlier. Please do not share this One Time Password with anyone.\n"
						+ "In case you have not requested for OTP, please contact office to report the issue, and remove this mail.\n"
						+ "\nRegards,\r\n" + "Admin";

			} else {
				String signOrder = inputs.getString("signOrder");
				if (signOrder.equals("0")) {
					str = "Hi," + "\r\n" + "\r\n" + "Please Complete the digital signing of BCA Agreement." + "\r\n"
							+ "Visit https://bcsign.docuexec.com/cvp/bclogin" + "\r\n" + "\r\n" + "Regards, \r\n"
							+ "BC Cell";
				} else {
					str = "Hi," + "\r\n" + "\r\n" + "Please Complete the witness signing of BCA Agreement." + "\r\n"
							+ "Visit https://bcsign.docuexec.com/cvp/bclogin" + "\r\n" + "\r\n" + "Regards, \r\n"
							+ "BC Cell";
				}
			}

		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// getEmailSubject()
	public static String getEmailSubject(String type, JSONObject inputs) {
		String str = null;
		try {
			if (inputs.has("loginmode")) {
				str = "OTP for Login";
			} else {
				str = "Notification to Digitally sign the BCA Agreement";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
