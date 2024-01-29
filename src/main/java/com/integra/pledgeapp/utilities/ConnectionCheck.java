package com.integra.pledgeapp.utilities;

import java.util.concurrent.TimeUnit;

import com.integra.pledgeapp.core.PledgeAppDAOImpl;

public class ConnectionCheck  extends Thread{
	public void run() {

		while (true) {
			try {
				TimeUnit.HOURS.sleep(5);
				new PledgeAppDAOImpl().getEmployeeDetails("3090", "");
			} catch (Throwable e) {
				System.out.println(e.getMessage());
			}
		}

	}
}
