package com.openhome.aop;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.PlaceDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.session.SessionManager;

@Aspect
@Component
@Order(0)
public class GuestAuthorizationAspect {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.GuestLoginRequired)")
	public Object hostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			Guest guest = sessionManager.getGuest(httpSession);
			if(guest == null) {
				return ControllerHelper.popupMessageAndRedirect("No Guest Login found in session.", "");
			}
			if(guest.getUserDetails().verifiedEmail() == false) {
				return ControllerHelper.popupMessageAndRedirect("Guest is unverified.", "");
			}
			System.out.println("============>Has Guest Login");
			return joinPoint.proceed();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
}
