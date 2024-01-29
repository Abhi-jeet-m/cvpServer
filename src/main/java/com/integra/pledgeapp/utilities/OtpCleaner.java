package com.integra.pledgeapp.utilities;


import java.util.concurrent.TimeUnit;

public class OtpCleaner extends Thread{

	public  String empid;
	public void run()
	{
		try {
			int minutes=Integer.parseInt(Properties_Loader.getOTPTIME());
			TimeUnit.MINUTES.sleep(minutes);
			OtpHandler.removeOtp(empid);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

