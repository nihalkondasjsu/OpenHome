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
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Space;

@Aspect
@Component
@Order(1)
public class SpaceIdValidateAspect {

	@Autowired
	SpaceDAO spaceDao;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidSpaceId)")
	public Object rightSpaceId(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.toLongString());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		try {
			Long spaceId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			if(spaceId == null) {
				return ControllerHelper.popupMessageAndRedirect("No SpaceId provided.", "");
			}
			Space s = spaceDao.getOne(spaceId);
			if(s == null) {
				return ControllerHelper.popupMessageAndRedirect("Invalid Space Id.", "");
			}
			System.out.println("============>Space Id is Valid");
			return joinPoint.proceed();
		} catch (Exception e) {
			System.out.println(e.toString());
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
}
