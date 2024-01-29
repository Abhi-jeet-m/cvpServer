package com.integra.pledgeapp.utilities;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class FileCleaner extends Thread {

	public  String filepath;
	public void run()
	{
		try {
			TimeUnit.MINUTES.sleep(1);
			System.out.println("Deleted file : "+filepath);
			new File(filepath).delete();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
