package com.openhome;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {

	public static void printObject(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("JSON.printObject");
			System.out.println(mapper.writeValueAsString(o));
			System.out.println("JSON.printObject");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
	}
	
	public static String base64(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Base64.getEncoder().encodeToString(mapper.writeValueAsString(o).getBytes());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			return null;
		}
	}
	
}
