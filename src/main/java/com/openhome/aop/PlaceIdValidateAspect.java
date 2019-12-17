package com.openhome.aop;

import java.util.Arrays;

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
import com.openhome.data.Place;

@Aspect
@Component
@Order(1)
public class PlaceIdValidateAspect {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidPlaceId)")
	public Object rightPlaceId(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.toLongString());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		try {
			Long placeId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			if(placeId == null) {
				return ControllerHelper.popupMessageAndRedirect("No PlaceId provided.", "");
			}
			Place s = placeDao.getOne(placeId);
			if(s == null) {
				return ControllerHelper.popupMessageAndRedirect("Invalid Place Id.", "");
			}
			System.out.println("============>Place Id is Valid");
			return joinPoint.proceed();
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidAlivePlaceId)")
	public Object rightAlivePlaceId(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.toLongString());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		try {
			Long placeId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			if(placeId == null) {
				return ControllerHelper.popupMessageAndRedirect("No PlaceId provided.", "");
			}
			Place s = placeDao.getOne(placeId);
			if(s == null || s.getPermanentlyUnavailable()) {
				return ControllerHelper.popupMessageAndRedirect("Invalid Place Id.", "");
			}
			System.out.println("============>Place Id is Valid");
			return joinPoint.proceed();
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
}
