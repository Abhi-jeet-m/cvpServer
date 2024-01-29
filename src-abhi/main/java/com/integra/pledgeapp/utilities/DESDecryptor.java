package com.integra.pledgeapp.utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
//import java.util.*;
//import java.util.Base64.Decoder;

public class DESDecryptor {

	private static Cipher dcipher;
	public static String decrypt(String str) {
		String decrypted = null;
		String input = str.trim();
		try {
			dcipher = Cipher.getInstance("DES");
			DESKeySpec dks = new DESKeySpec("cia135@$^".getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey key = skf.generateSecret(dks);
			dcipher.init(2, key);
//			 byte[] dataBytesDecrypted = (dcipher.doFinal(input.getBytes()));

//			byte[] decordedValue = new sun.misc.BASE64Decoder().decodeBuffer(input);
//			byte[] decordedValue = new Decoder().decode(input);
			
			byte[] utf8 = dcipher.doFinal(input.getBytes());

			decrypted = new String(utf8, "UTF8");
		} catch (Exception e) {
			System.out.println("Error while decrypting : "+e);
		}
		return decrypted;
	}

}
