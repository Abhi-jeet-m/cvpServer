package com.integra.pledgeapp.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Certificate {
	private static final String JCE_PROVIDER = "BC";
	private static final int SYMMETRIC_KEY_SIZE = 256;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	// fetching private key from private certificate
	public static PrivateKey getPrivetKey(String filename) throws Exception {

		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	// fetching public key from public certificate
	public static PublicKey getPublicKey(String filename) throws Exception {

		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public static String encrypt(String message) {
		try {
			return encrypt(message, "private_key_4096.der");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// used for encrypting the session key
	public static String encryptSessionKey(String message) {

		// Set plain message
		byte[] messageBytes = message.getBytes();
		// The source of randomness
		SecureRandom secureRandom = new SecureRandom();

		Cipher cipher;
		try {
			// Obtain a RSA Cipher Object
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// Initialize the cipher for encryption
			cipher.init(Cipher.ENCRYPT_MODE, getPrivetKey("private_key_4096.der"), secureRandom);

			// Encrypt the message
			byte[] ciphertextBytes = cipher.doFinal(messageBytes);

			return Base64.getEncoder().encodeToString(ciphertextBytes);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

//encrpting JSONdata and generating the session key
	public static String encrypt(String metaDATA, String keyFileLocation)
			throws NoSuchAlgorithmException, NoSuchProviderException, Exception {
		String encryptedData = null;

//		byte[] sessionkey = generateSessionKey();
//		String sessionKeyString = new String(sessionkey);
		String sessionKeyString="abcdef0123456789";
		
		
		
		
		// encrpting JSONdata by using the sessionkey
//		System.out.println("sessionkey beofre enc " + sessionKeyString);
		String encryptedMetaData = Certificate.encryptUsingSessionKey(metaDATA, sessionKeyString);
//		System.out.println("encryptedMetaData: " + encryptedMetaData);
		// encrypting the sessionKey
		String encryptedSessionKey = Certificate.encryptSessionKey(sessionKeyString);
//		System.out.println("encryptedSessionKey::::" + encryptedSessionKey);
		encryptedData = encryptedSessionKey + ":" + encryptedMetaData;
		return encryptedData;
	}

	// generating the session key
	private static byte[] generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES", JCE_PROVIDER);
		kgen.init(SYMMETRIC_KEY_SIZE);
		SecretKey key = kgen.generateKey();
		byte[] symmKey = key.getEncoded();
		return symmKey;
	}

	// encrypting the data using the sessionkey
	public static String encryptUsingSessionKey(String str, String sessionkey) {
		try {
//			System.out.println("plain metadata:" + str);
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(sessionkey.toCharArray(), salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] encryptedText = cipher.doFinal(str.getBytes("UTF-8"));

			// concatenate salt + iv + ciphertext
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(salt);
			outputStream.write(iv);
			outputStream.write(encryptedText);
			// properly encode the complete ciphertext
			return Base64.getEncoder()
					.encodeToString(DatatypeConverter.printBase64Binary(outputStream.toByteArray()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// decrypting the sessionKey
	public static String decrypt(String enccryptedMsg) {
		String encryptedSessionKey = enccryptedMsg.split(":")[0].toString();
		String encryptedMetaData = enccryptedMsg.split(":")[1].toString();
		byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedSessionKey);

		// The source of randomness
		SecureRandom secureRandom = new SecureRandom();

		Cipher cipher;
		try {
			// Obtain a RSA Cipher Object
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// Initialize the cipher for decryption
//			cipher.init(Cipher.DECRYPT_MODE, getPrivetKey("private_key_4096.der"), secureRandom);
//			cipher.init(Cipher.DECRYPT_MODE, getPublicKey("public_key_4096.der"), secureRandom);
			cipher.init(Cipher.DECRYPT_MODE, getPublicKey("001_public_key.der"), secureRandom);

			// Decrypt the message
			byte[] textBytes = cipher.doFinal(ciphertextBytes);

			String decryptedSessionKey = new String(textBytes);
			String decrypteddata = Certificate.decryptUsingSessionKey(encryptedMetaData,
					new String(decryptedSessionKey));
			return decrypteddata;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// decrypting the data from certificate using session Key
	public static String decryptUsingSessionKey(String str, String sessionKey) {
		// public static String decryptSessionKey(String str,String sessionKey) {
		try {
			byte[] ciphertext = DatatypeConverter.parseBase64Binary(new String(Base64.getDecoder().decode(str)));
			if (ciphertext.length < 48) {
				return null;
			}
			byte[] salt = Arrays.copyOfRange(ciphertext, 0, 16);
			byte[] iv = Arrays.copyOfRange(ciphertext, 16, 32);
			byte[] ct = Arrays.copyOfRange(ciphertext, 32, ciphertext.length);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(sessionKey.toCharArray(), salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] plaintext = cipher.doFinal(ct);

			return new String(plaintext, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// decrypt data using clint public key.
	// used for eStamping data decryption
	public static String decryptDataUsingPublicKey(String enccryptedMsg) {
		String encryptedSessionKey = enccryptedMsg.split(":")[0].toString();
		String encryptedMetaData = enccryptedMsg.split(":")[1].toString();
		byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedSessionKey);

		// The source of randomness
		SecureRandom secureRandom = new SecureRandom();

		Cipher cipher;
		try {
			// Obtain a RSA Cipher Object
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			// Initialize the cipher for decryption
//			cipher.init(Cipher.DECRYPT_MODE, getPublicKey("001_client_key.der"), secureRandom);
//			cipher.init(Cipher.DECRYPT_MODE, getPublicKey("public_key_4096.der"), secureRandom);
			

			// Decrypt the message
			byte[] textBytes = cipher.doFinal(ciphertextBytes);

			String decryptedSessionKey = new String(textBytes);
			String decrypteddata = Certificate.decryptUsingSessionKey(encryptedMetaData,
					new String(decryptedSessionKey));
			return decrypteddata;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public static String encrypt(String message) {
//		// Set plain message
//		byte[] messageBytes = message.getBytes();
//		// The source of randomness
//		SecureRandom secureRandom = new SecureRandom();
//
//		Cipher cipher;
//		try {
//			// Obtain a RSA Cipher Object
//			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//
//			// Initialize the cipher for encryption
//			cipher.init(Cipher.ENCRYPT_MODE, getPrivetKey("private_key_4096.der"), secureRandom);
//
//			// Encrypt the message
//			byte[] ciphertextBytes = cipher.doFinal(messageBytes);
//			
//			return Base64.getEncoder().encodeToString(ciphertextBytes);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "";
//	}

//	public static String decrypt(String enccryptedMsg) {
//
//		byte[] ciphertextBytes = Base64.getDecoder().decode(enccryptedMsg);
//
//		// The source of randomness
//		SecureRandom secureRandom = new SecureRandom();
//
//		Cipher cipher;
//		try {
//			// Obtain a RSA Cipher Object
//			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//
//			// Initialize the cipher for decryption
//			cipher.init(Cipher.DECRYPT_MODE, getPrivetKey("private_key_4096.der"), secureRandom);
//
//			// Decrypt the message
//			byte[] textBytes = cipher.doFinal(ciphertextBytes);
//
//			return new String(textBytes);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

}
