package com.integra.pledgeapp;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.integra.pledgeapp.utilities.ConfigListner;
import com.integra.pledgeapp.utilities.ConnectionCheck;
import com.integra.pledgeapp.utilities.Properties_Loader;

@SpringBootApplication
public class CoeValuePledgeAppApplication {

	
	public static void main(String[] args) {
		
		System.out.println("===========================================================");
        System.out.println("=                 Core Value Pledge App                   =");
        System.out.println("=                 Version 00_00_56                     =");
        System.out.println("=                 App Version 1.4.6		                 =");
        System.out.println("===========================================================");
		SpringApplication.run(CoeValuePledgeAppApplication.class, args);
		Properties_Loader.loadProperties();
		new ConfigListner().getTemplates();
		ConnectionCheck cc = new ConnectionCheck();
		cc.start();
		
	}

}
