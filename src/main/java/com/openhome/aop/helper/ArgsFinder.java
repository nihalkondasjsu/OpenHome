package com.openhome.aop.helper;

import javax.servlet.http.HttpSession;

import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import com.openhome.exception.CustomException;

public class ArgsFinder {

	public static <T extends Object> T findArg(Object[] args, Class<T> cls) throws CustomException {
		System.out.println("Looking for "+cls.toString());
		for (Object object : args) {
			if(object == null)
				continue;
			System.out.println(object.getClass().toString());
			if(object.getClass() == cls)
				return cls.cast(object);
		}
		throw new CustomException("No Argument of "+cls.getName()+" type");
	}

	public static Model getModel(Object[] args) {
		Model model=null;
		try {
			try {
				model = findArg(args, Model.class);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(model != null)
				return model;
			model = findArg(args, BindingAwareModelMap.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	
	public static HttpSession getHttpSession(Object[] args) {
		HttpSession httpSession=null;
		try {
			try {
				httpSession = findArg(args, HttpSession.class);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(httpSession != null) {
				System.out.println("HttpSession found");
				return httpSession;
			}
			System.out.println("HttpSession new try");
			httpSession = findArg(args, StandardSessionFacade.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpSession;
	}
	
}
