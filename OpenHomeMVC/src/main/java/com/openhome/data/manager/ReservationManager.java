package com.openhome.data.manager;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.annotation.Debug;
import com.openhome.data.Reservation;
import com.openhome.data.Reservation.ReservationState;
import com.openhome.data.Transaction.TransactionNature;
import com.openhome.data.Transaction.TransactionUser;
import com.openhome.mailer.Mailer;

@Component
public class ReservationManager {

	private static final Double SERVICE_CHARGE = 1.00;
	private static final Long MS_24_HOURS = 24*60*60*1000l;
	private static final Long MS_12_HOURS = 12*60*60*1000l;
	private static final Long MS_11_HOURS = 11*60*60*1000l;
	private static final Long MS_15_HOURS = 15*60*60*1000l;
	private static final Long MS_4_HOURS = 4*60*60*1000l;

	private static final Long CHECK_IN_TIME = 15*60*60*1000l;//3 pm
	private static final Long CHECK_OUT_TIME = 11*60*60*1000l;//11 am

	@Autowired
	public Mailer mailer;
	
	TransactionManager transactionManager;
	
	public Reservation reservation;
	
	class MicroReservation{
		Date checkIn;
		Date checkOut;
		Double totalPrice;
		public MicroReservation(Date checkIn, Date checkOut, Double totalPrice) {
			super();
			this.checkIn = checkIn;
			this.checkOut = checkOut;
			this.totalPrice = totalPrice;
		}
		public Date getCheckIn() {
			return checkIn;
		}
		public void setCheckIn(Date checkIn) {
			this.checkIn = checkIn;
		}
		public Date getCheckOut() {
			return checkOut;
		}
		public void setCheckOut(Date checkOut) {
			this.checkOut = checkOut;
		}
		public boolean isDateUsed(Date date) {
			//System.out.println("Checking if "+this.checkIn+" to "+this.checkOut+" has been used by "+date);
			if(checkOut.getTime() > date.getTime())
				return checkOut.getTime() - date.getTime() <= MS_24_HOURS - MS_4_HOURS;
			return true;
		}
//		public boolean isDateBetween(Date date) {
//			if(checkOut.getTime() > date.getTime())
//				return checkOut.getTime() - date.getTime() <= MS_24_HOURS;
//			return false;
//		}
		public boolean isDateInComing24Hours(Date date) {
			if(checkOut.getTime() > date.getTime())
				return checkOut.getTime() - date.getTime() <= MS_24_HOURS;
			return false;
		}
		public boolean isDateInComing7Days(Date date) {
			if(checkOut.getTime() > date.getTime())
				return checkOut.getTime() - date.getTime() <= 7*MS_24_HOURS;
			return false;
		}
		public Double getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(Double totalPrice) {
			this.totalPrice = totalPrice;
		}
		@Override
		public String toString() {
			return String.format("{ checkIn : %s , checkOut : %s , totalPrice : %f }\n",checkIn,checkOut,totalPrice);
		}
	}
	
	private ArrayList<MicroReservation> microReservations ;
	
	public ReservationManager() {
		transactionManager = new TransactionManager();
	}

	public Reservation getReservation() {
		return reservation;
	}

	@Debug
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
		microReservationsReset();
		System.out.println(microReservations);
	}
	
	private void microReservationsReset() {
		microReservations = new ArrayList<ReservationManager.MicroReservation>();
		Double amount = 0.0;
		try{
			Long checkIn = getReservation().getCheckIn();
			Long checkOut = getReservation().getCheckOut();

			Double weekendPrice = getReservation().getWeekendRentPrice();
			Double weekdayPrice = getReservation().getWeekdayRentPrice();
			Double dailyParkingFee = getReservation().getDailyParkingFee();

			checkOut = checkOut + MS_4_HOURS;
			System.out.println("========>"+new Date(checkIn));
			System.out.println("========>"+new Date(checkOut));
			
			if(checkOut - checkIn >= MS_24_HOURS){
				while(checkIn < checkOut){
					double temp = (new Date(checkIn).getDay() == 6 ? weekendPrice : weekdayPrice) + dailyParkingFee;
					microReservations.add(
							new MicroReservation(new Date(checkIn ), new Date(checkIn + MS_24_HOURS - MS_4_HOURS), temp)
							);
					System.out.printf("%f + %f = ", amount,temp);
					amount += temp ;
					System.out.printf("%f\n", amount);
					checkIn += MS_24_HOURS;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Debug
	public boolean reservationEnded() {
		return !(getReservation().getReservationState().equals(ReservationState.Booked) || getReservation().getReservationState().equals(ReservationState.CheckedIn));
	}
	
	public void showAllCalculations(Date currentTime) {
		System.out.println("showAllCalculations(Date currentTime) ====================== "+currentTime);
		System.out.printf("getTotalDays : %d\n", getTotalDays());
		System.out.printf("getTotalPrice : %f\n", getTotalPrice());
		System.out.printf("getGuestUsedDays : %d\n", getGuestUsedDays(currentTime));
		System.out.printf("getGuestUsagePrice : %f\n", getGuestUsagePrice(currentTime));
	}
	
	private Double getGuestUsagePrice(Date currentTime) {
		if(currentTime.before(new Date(getReservation().getCheckIn())))
			return 0.0;
		Double amount = 0.0;
		for (int i = 0; i < microReservations.size() ; i++) {
			if(microReservations.get(i).isDateUsed(currentTime)) {
				amount += microReservations.get(i).getTotalPrice();
			}else break;
		}
		return amount;
	}
	
	private Integer getGuestUsedDays(Date currentTime) {
		if(currentTime.before(new Date(getReservation().getCheckIn())))
			return 0;
		for (int i = 0; i < microReservations.size() ; i++) {
			if(microReservations.get(i).isDateUsed(currentTime)) {
				
			}else return i;
		}
		return microReservations.size();
	}
	
	private Double getGuestCancellationFee(Date currentTime) {
		Double amount = 0.0;
		
		try {
			Long checkIn = getReservation().getCheckIn();
			
			if(currentTime.before(new Date(checkIn))) {
				// prior cancellation
				if(checkIn - currentTime.getTime() > MS_24_HOURS) {
					// cancellation initiated 24 hours before the beginning of the reservation
					return 0.0;
				}else {
					// late cancellation for the first day of reservation
					return microReservations.get(0).getTotalPrice() * 0.30;
				}
				
			}else {
				
				Long checkOut = getReservation().getCheckOut();

				
				if(currentTime.after(new Date(checkOut))) {
					// cancelling a reservation after the time
					return getTotalPrice();
				}
				
				Integer guestUsedDays = getGuestUsedDays(currentTime);
				Integer guestTotalDays = getTotalDays();
				
				
				if(guestTotalDays > guestUsedDays) {
					// some days left
					return microReservations.get(guestUsedDays).getTotalPrice() * 0.30;
				}else {
					// cancelling on last day does not have any fee
					return 0.0;
				}
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return amount;
	}
	
	private Double getHostCancellationFee(Date currentTime) {
		Double amount = 0.0;
		try {
			Long checkIn = getReservation().getCheckIn();
			
			if(currentTime.getTime() < checkIn - (7*MS_24_HOURS)) {
				// host is cancelling the reservation 7 days ahead so no fee
				return 0.0;
			}
			
			Long checkOut = getReservation().getCheckOut();

			if(currentTime.getTime() > checkOut) {
				// reservation has ended so cancellation is not possible
				return 0.0;
			}
			
			if(currentTime.getTime() > checkIn) {
				// very late cancellation
				amount = 0.0;
				for (int i = 0; i < microReservations.size(); i++) {
					if(microReservations.get(i).isDateUsed(currentTime) == false) {
						for (int j = i; j < microReservations.size(); j++) {
							if(microReservations.get(j).isDateInComing7Days(currentTime)) {
								amount += microReservations.get(j).getTotalPrice() * 0.15;
							}else {
								break;
							}
						}
						break;
					}
				}
				return amount;
			}else {
				// some days of the reservation are within 7 days from now
				amount = 0.0;
				for (MicroReservation microReservation : microReservations) {
					if(microReservation.isDateInComing7Days(currentTime)) {
						amount += microReservation.getTotalPrice() * 0.15;
					}else {
						break;
					}
				}
				return amount;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return amount;
	}
	
	private Double getTotalPrice() {
		Double amount = 0.0;
		for (MicroReservation microReservation : microReservations) {
			amount += microReservation.getTotalPrice();
		}
		return amount;
	}
	
	private Integer getTotalDays() {
		return microReservations.size();
	}
	
	@Debug
	public void performGuestCheckIn(Date currentTime) throws IllegalAccessException {
		
		processReservation(currentTime);
		
		if(getReservation().getReservationState() != ReservationState.Booked) {
			throw new IllegalAccessException("Check In not Allowed");
		}
		
		if(currentTime.before(new Date(getReservation().getCheckIn()))) {
			throw new IllegalAccessException("It is too early to Check In");
		}
	
		getReservation().setActualCheckIn(currentTime.getTime());
		
		transactionManager.createTransaction(currentTime, new Date(getReservation().getCheckIn()), getTotalPrice(), reservation, TransactionNature.Charge, TransactionUser.Guest);
		
		getReservation().setReservationState(ReservationState.CheckedIn);
	}
	
	@Debug
	public void performGuestCheckOut(Date currentTime) throws IllegalAccessException {
		
		processReservation(currentTime);
		
		if(getReservation().getReservationState() != ReservationState.CheckedIn) {
			throw new IllegalAccessException("Check Out is not Allowed");
		}
		
		if(currentTime.after(new Date(getReservation().getCheckOut()))) {
			throw new IllegalAccessException("It is too late to Check Out");
		}

		//chargeGuestForDay(currentDate, currentDate);
	
		if(currentTime.before(new Date(getReservation().getCheckOut()-MS_24_HOURS))) {
			//early checkout
			//chargeGuestFeeForDay(currentDate, new Date(currentDate.getTime()+MS_24_HOURS));
			Double amount = getGuestCancellationFee(currentTime);
			transactionManager.createTransaction(currentTime, currentTime, amount, reservation, TransactionNature.Fee, TransactionUser.Guest);
			amount = getTotalPrice() - getGuestUsagePrice(currentTime);
			transactionManager.createTransaction(currentTime, currentTime, amount, reservation, TransactionNature.Charge, TransactionUser.Host);
		}
		
		getReservation().setActualCheckOut(currentTime.getTime());
		
		getReservation().setReservationState(ReservationState.CheckedOut);
		
	}
	
	@Debug
	public void performGuestCancel(Date currentTime) throws IllegalAccessException {
		
		processReservation(currentTime);
		
		if(getReservation().getReservationState()!=ReservationState.Booked) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		Double amount = getGuestCancellationFee(currentTime);
		transactionManager.createTransaction(currentTime, currentTime, amount, reservation, TransactionNature.Fee, TransactionUser.Guest);
		
		getReservation().setActualCheckOut(currentTime.getTime());

		getReservation().setReservationState(ReservationState.GuestCancelled);

	}
	
	@Debug
	public void performHostCancel(Date currentTime) throws IllegalAccessException {
		processReservation(currentTime);
		
		if(reservationEnded()) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		//hostCancelReservation(currentDate);

		Double amount = getHostCancellationFee(currentTime);
		
		if(currentTime.getTime() < getReservation().getCheckIn()) {
			transactionManager.createTransaction(currentTime, currentTime, amount, reservation, TransactionNature.Fee, TransactionUser.Host);
			getReservation().setActualCheckOut(getReservation().getCheckIn());
		}else {
			amount += getTotalPrice() - getGuestUsagePrice(currentTime);
			transactionManager.createTransaction(currentTime, currentTime, amount, reservation, TransactionNature.Fee, TransactionUser.Host);
			
			Date today11 = new Date(currentTime.getTime());
			Date tommorow11 = new Date(currentTime.getTime()+MS_24_HOURS);

			today11.setHours(11);today11.setMinutes(0);today11.setSeconds(0);
			tommorow11.setHours(11);tommorow11.setMinutes(0);tommorow11.setSeconds(0);
			
			if(today11.getTime() > currentTime.getTime()) {
				getReservation().setActualCheckOut(today11.getTime());
			}else {
				getReservation().setActualCheckOut(tommorow11.getTime());
			}
		}
		
		getReservation().setReservationState(ReservationState.HostCancelled);
	}

	@Debug
	public void processReservation(Date currentTime) {
		if(reservationEnded()) {
			// reservation is either checked-out or cancelled
			// in that case no need to process this reservation
			System.out.println("Reservation Ended");
			return;
		}
		
		// reservation is not finished
		
		Date checkInTime = new Date(getReservation().getCheckIn());
		Date checkOutTime = new Date(getReservation().getCheckOut());
		
		if(currentTime.before(checkInTime)) {
			// nothing to process before the actual check-in time
			System.out.println("Before check in");
			return;
		}
		
		// its time for the guest to check-in ( current time is after 3pm of first day )
		// checkInTime < currentTime < checkOutTime
		if(getReservation().getActualCheckIn() == null) {
			// no record of guest check-in found
			if(currentTime.before(new Date(getReservation().getCheckIn()+MS_12_HOURS))) {
				// guest still has 12 hours to check-in
				System.out.println("Check in day first 12 hours");
				return;
			}
			// guest has not checked in within the first 12 hours of their reservation 
			performGuestNoShowProtocol(currentTime);
			System.out.println("Guest no show");
			return;
		}
		
		if(currentTime.after(checkOutTime)) {
			// currentTime is after check-out time
			performGuestAutoCheckOut(currentTime);
			System.out.println("Guest Auto checkout");
			return;
		}else {
			// record of guest check-in found
			System.out.println("Guest charge (prepaid)");
			return;
		}
		
	}

	@Debug
	private void performGuestNoShowProtocol(Date currentTime) {
		Double amount = microReservations.get(0).getTotalPrice()*0.30;
		if(microReservations.get(1) != null) {
			amount += microReservations.get(1).getTotalPrice()*0.30;
		}
		Date dayToChargeFor = new Date(microReservations.get(0).getCheckIn().getTime() + MS_12_HOURS);
		transactionManager.createTransaction(currentTime, 
				dayToChargeFor
				, amount, reservation, TransactionNature.Fee, TransactionUser.Guest);
		
		getReservation().setActualCheckOut(dayToChargeFor.getTime());

		getReservation().setReservationState(ReservationState.GuestCancelled);
		
	}
	

	@Debug
	private void performGuestAutoCheckOut(Date currentTime) {
		getReservation().setActualCheckOut(getReservation().getCheckOut());

		getReservation().setReservationState(ReservationState.CheckedOut);
	}
}
