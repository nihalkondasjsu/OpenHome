package com.openhome.dao.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
    	return listToString(stringList);
    }

    public static String listToString(List<String> stringList) {
    	//System.out.println(">>>>convertToDatabaseColumn>>>>>"+stringList);
    	if(stringList == null)
    		return "";
    	
    	Collections.sort(stringList);
        
    	return ";"+String.join(";;", stringList)+";";
	}

	@Override
    public List<String> convertToEntityAttribute(String string) {
    	return stringToList(string);
    }

	public static List<String> stringToList(String string) {
		//System.out.println(">>>>convertToEntityAttribute>>>>>"+string);
        try {
        	if(string.equals(""))
        		return new ArrayList<String>();
        	string = string.replace(";;", ";");
        	ArrayList<String> list = new ArrayList<>(Arrays.asList(string.split(";")));
        	while(list.contains("")) {
        		list.remove("");
        	}
        	//System.out.println(">>>>convertToEntityAttribute>>output>>>"+list);
            return list;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
}