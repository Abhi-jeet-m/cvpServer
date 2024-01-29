package com.integra.pledgeapp.utilities;

import java.util.concurrent.TimeUnit;

public class TokenCleaner extends Thread{

	
	public  String token;
	public void run()
	{
		try {
			TimeUnit.MINUTES.sleep(2);
			TokenManager.removeToken(token);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
