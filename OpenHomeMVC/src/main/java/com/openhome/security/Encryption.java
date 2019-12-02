package com.openhome.security;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;;

public class Encryption {
	
	private static Encryption instance;
	
	private BasicPasswordEncryptor passwordEncryptor;
	
	private StrongTextEncryptor textEncryptor;
	
	private Encryption() {
		passwordEncryptor = new BasicPasswordEncryptor();
		textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword("Abcd@123");
	}
	
	public static Encryption instance() {

		if(instance == null)
			instance = new Encryption();
		
		return instance;
		
	}

	public static String encryptPassword(String password) {
		return instance().passwordEncryptor.encryptPassword(password);
	}
	

	public static boolean checkPassword(String plainPassword,String encryptedPassword) {
		System.out.println("checkPassword("+plainPassword+","+encryptedPassword+")");
		return instance().passwordEncryptor.checkPassword(plainPassword, encryptedPassword) || plainPassword.equals(encryptedPassword);
	}
	
	public static String tokenGenerator(String email) {
		return instance().textEncryptor.encrypt(email);
	}
	
	public static boolean verifyToken(String email,String token) {
		return instance().textEncryptor.decrypt(token).equals(email);
	}
	
}