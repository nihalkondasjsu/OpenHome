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
import com.openhome.dao.ReservationDAO;
import com.openhome.data.Reservation;

@Aspect
@Component
@Order(1)
public class ReservationIdValidateAspect {

	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidReservationId)")
	public Object rightReservationId(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.toLongString());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		try {
			Long reservationId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			if(reservationId == null) {
				return ControllerHelper.popupMessageAndRedirect("No ReservationId provided.", "");
			}
			Reservation b = reservationDao.getOne(reservationId);
			if(b == null) {
				return ControllerHelper.popupMessageAndRedirect("Invalid Reservation Id.", "");
			}
			System.out.println("============>Reservation Id is Valid");
			return joinPoint.proceed();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return "index";
		
	 }
	
}
