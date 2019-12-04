package com.openhome.data.manager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.annotation.Debug;
import com.openhome.data.Booking;
import com.openhome.data.Booking.BookingState;
import com.openhome.data.Transaction;
import com.openhome.data.Transaction.TransactionNature;
import com.openhome.data.Transaction.TransactionUser;
import com.openhome.mailer.Mailer;

@Component
public class BookingManager {

	private static final Double SERVICE_CHARGE = 1.00;
	private static final Long MS_24_HOURS = 24*60*60*1000l;
	private static final Long MS_12_HOURS = 12*60*60*1000l;

	private static final Long CHECK_IN_TIME = 15*60*60*1000l;//3 pm
	private static final Long CHECK_OUT_TIME = 11*60*60*1000l;//11 am

	@Autowired
	public Mailer mailer;
	
	public Booking booking;
	
	public BookingManager() {
		
	}

	public Booking getBooking() {
		return booking;
	}

	@Debug
	public void setBooking(Booking booking) {
		this.booking = booking;
	}
	
	@Debug
	public Transaction getTransactionByNatureUserAndDate(TransactionNature transactionNature, Transaction.TransactionUser transactionUser,Date dateToCharge) {
		for (Transaction transaction : getBooking().getTransactions()) {
			if(transaction.getTransactionNature().equals(transactionNature) && transaction.getTransactionUser().equals(transactionUser) && compareDMY(dateToCharge, transaction.getDayToChargeFor())) {
				return transaction;
			}
		}
		return null;
	}
	
		
	@Debug
	public void chargeGuestForDay(Date currentTime,Date dayToChargeFor) {
		Transaction t = getTransactionByNatureUserAndDate(TransactionNature.Charge,Transaction.TransactionUser.Guest,dayToChargeFor);
		
		if(t != null) {
			// guest already charged for today
			 return;
		}
		
		Double amount = getPriceForDay(dayToChargeFor);
		
		if(amount != 0) {
			Transaction transaction1 = new Transaction(amount*SERVICE_CHARGE,currentTime,dayToChargeFor,getBooking(),TransactionNature.Charge,Transaction.TransactionUser.Guest);
			getBooking().addTransaction(transaction1);
			
			Transaction transaction2 = new Transaction(amount,currentTime,dayToChargeFor,getBooking(),TransactionNature.Payment,Transaction.TransactionUser.Host);
			getBooking().addTransaction(transaction2);
		}
		
	}

	@Debug
	public void chargeGuestFeeForDay(Date currentTime,Date dayToChargeFor) {
		Transaction t = getTransactionByNatureUserAndDate(TransactionNature.Fee,TransactionUser.Guest,dayToChargeFor);
		
		if(t != null) {
			// guest already charged fee for today
			 return;
		}
		
		Double amount = getGuestCancellationFeeForDay(currentTime,dayToChargeFor);
		
		if(amount != 0) {
			Transaction transaction1 = new Transaction(amount*SERVICE_CHARGE,currentTime,dayToChargeFor,getBooking(),TransactionNature.Fee,Transaction.TransactionUser.Guest);
			getBooking().addTransaction(transaction1);
			
			Transaction transaction2 = new Transaction(amount,currentTime,dayToChargeFor,getBooking(),TransactionNature.Payment,Transaction.TransactionUser.Host);
			getBooking().addTransaction(transaction2);
		}
		
	}
	
	@Debug
	public void chargeHostFeeForDay(Date currentTime,Date dayToChargeFor) {
		Transaction t = getTransactionByNatureUserAndDate(TransactionNature.Fee,TransactionUser.Host,dayToChargeFor);
		
		if(t != null) {
			// host already charged fee for today
			 return;
		}
		
		Double amount = getHostCancellationFeeForDay(currentTime,dayToChargeFor);
		if(amount != 0) {
			Transaction transaction1 = new Transaction(amount*SERVICE_CHARGE,currentTime,dayToChargeFor,getBooking(),TransactionNature.Fee,Transaction.TransactionUser.Host);
			getBooking().addTransaction(transaction1);
			
			Transaction transaction2 = new Transaction(amount,currentTime,dayToChargeFor,getBooking(),TransactionNature.Payment,Transaction.TransactionUser.Guest);
			getBooking().addTransaction(transaction2);
		}
		
	}

	@Debug
	public void chargeGuestForPreviousDay(Date currentTime) {
		
		Date dayToChargeFor = new Date(currentTime.getTime() - MS_24_HOURS );
		
		//was the guest charged before on the same day
		
		chargeGuestForDay(currentTime, dayToChargeFor);
		
	}
	
	@Debug
	public void guestCancelBooking(Date currentTime) {
		for (int i = 0 ; i < 2 ; i++) {
			chargeGuestFeeForDay(currentTime, new Date(currentTime.getTime()+(i*MS_24_HOURS)));
		}
	}
	
	@Debug
	public void hostCancelBooking(Date currentTime) {
		for (int i = 1 ; i < 7 ; i++) {
			chargeHostFeeForDay(currentTime, new Date(currentTime.getTime()+(i*MS_24_HOURS)));
		}
	}
	
	@Debug
	public boolean compareDMY(Date a, Date b) {
		return a.getDate() == b.getDate() && a.getMonth() == b.getMonth() && a.getYear() == b.getYear();
	}
	
	@Debug
	public boolean bookingEnded() {
		return !(getBooking().getBookingState().equals(BookingState.Booked) || getBooking().getBookingState().equals(BookingState.CheckedIn));
	}
	
	@Debug
	public Double getGuestCancellationFeeForDay(Date currentDate,Date date) {
		System.out.println(date.getTime() - currentDate.getTime() +" | " + MS_24_HOURS);
		if(date.getTime() - currentDate.getTime() >= MS_24_HOURS) {
			System.out.println("cancelling 24 hours ahead");
			return 0.0;
		}
		return getPriceForDay(date) * 0.30 ;
	}
	
	@Debug
	public Double getHostCancellationFeeForDay(Date currentDate,Date date) {
		if(date.getTime() - currentDate.getTime() >= 7*MS_24_HOURS) {
			return 0.0;
		}
		return getPriceForDay(date) * 0.15 ;
	}
	
	@Debug
	public Double getPriceForDay(Date date) {
		if(date.before(new Date(getBooking().getCheckIn())) || date.after(new Date(getBooking().getCheckOut()))) {
			System.out.println("date.before(new Date(getBooking().getCheckIn()))");
			return 0.0;
		}
		return date.getDay() == 6 ? getBooking().getWeekendRentPrice() : getBooking().getWeekdayRentPrice();
	}
	
	@Debug
	public Double priceForDays() {
		Double price = 0.0 ;
		
		Date start,end;
		
		start = new Date(getBooking().getCheckIn());
		end = new Date(getBooking().getCheckOut());
		
		start.setHours(0);end.setHours(0);
		
		start.setMinutes(0);end.setMinutes(0);
		
		start.setSeconds(0);end.setSeconds(0);
		
		long Start = start.getTime() , End = end.getTime();
		
		for (long i = Start; i < End; i += 24*60*60*1000 ) {
			price += getPriceForDay(new Date(i));
		}
		
		return price;
	}
	
	@Debug
	public void performGuestCheckIn(Date currentDate) throws IllegalAccessException {
		
		processBooking(currentDate);
		
		if(getBooking().getBookingState() != BookingState.Booked) {
			throw new IllegalAccessException("Check In not Allowed");
		}
		
		if(currentDate.before(new Date(getBooking().getCheckIn()))) {
			throw new IllegalAccessException("It is too early to Check In");
		}
	
		getBooking().setActualCheckIn(currentDate.getTime());
		
		getBooking().setBookingState(BookingState.CheckedIn);
	}
	
	@Debug
	public void performGuestCheckOut(Date currentDate) throws IllegalAccessException {
		
		processBooking(currentDate);
		
		if(getBooking().getBookingState() != BookingState.CheckedIn) {
			throw new IllegalAccessException("Check Out is not Allowed");
		}
		
		if(currentDate.after(new Date(getBooking().getCheckOut()))) {
			throw new IllegalAccessException("It is too late to Check Out");
		}

		chargeGuestForDay(currentDate, currentDate);
	
		if(currentDate.before(new Date(getBooking().getCheckOut()-MS_24_HOURS))) {
			//early checkout
			chargeGuestFeeForDay(currentDate, new Date(currentDate.getTime()+MS_24_HOURS));
		}
		
		getBooking().setActualCheckOut(currentDate.getTime());
		
		getBooking().setBookingState(BookingState.CheckedOut);
		
	}
	
	@Debug
	public void performGuestCancel(Date currentDate) throws IllegalAccessException {
		
		processBooking(currentDate);
		
		if(bookingEnded()) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		guestCancelBooking(currentDate);
		
		getBooking().setActualCheckOut(currentDate.getTime());

		getBooking().setBookingState(BookingState.GuestCancelled);

	}
	
	@Debug
	public void performHostCancel(Date currentDate) throws IllegalAccessException {
		processBooking(currentDate);
		
		if(bookingEnded()) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		hostCancelBooking(currentDate);
		
		//getBooking().setActualCheckOut(currentDate.getTime());
		getBooking().setBookingState(BookingState.HostCancelled);
	}
	
	@Debug
	public void processBooking(Date currentDate) {
		if(bookingEnded()) {
			// booking is either checked-out or cancelled
			// in that case no need to process this booking
			System.out.println("Booking Ended");
			return;
		}
		
		// booking is not finished
		
		Date checkInTime = new Date(getBooking().getCheckIn());
		Date checkOutTime = new Date(getBooking().getCheckOut());
		
		if(currentDate.before(checkInTime)) {
			// nothing to process before the actual check-in time
			System.out.println("Before check in");
			return;
		}
		
		// its time for the guest to check-in ( current time is after 3pm of first day )
		if(currentDate.before(checkOutTime)) {
			// checkInTime < currentTime < checkOutTime
			if(getBooking().getActualCheckIn() == null) {
				// no record of guest check-in found
				if(currentDate.before(new Date(getBooking().getCheckIn()+MS_12_HOURS))) {
					// guest still has 12 hours to check-in
					System.out.println("Check in day first 12 hours");
					return;
				}
				// guest has not checked in within the first 12 hours of their booking 
				performGuestNoShowProtocol(currentDate);
				System.out.println("Guest no show");
				return;
			}
			// record of guest check-in found
			chargeGuestForPreviousDay(currentDate);
			System.out.println("Guest charge");
			return;
		}
		// currentTime is after check-out time
		performGuestAutoCheckOut(currentDate);
		System.out.println("Guest Auto checkout");
		return;
	}

	@Debug
	private void performGuestNoShowProtocol(Date currentTime) {
		//overriding currentTime
		currentTime = new Date(getBooking().getCheckIn()+MS_12_HOURS+1);
		//currentTime overridden
		
		chargeGuestFeeForDay(currentTime, new Date(currentTime.getTime()-MS_12_HOURS));
		guestCancelBooking(currentTime);
		getBooking().setBookingState(Booking.BookingState.GuestCancelled);
	}
	

	@Debug
	private void performGuestAutoCheckOut(Date currentTime) {
		//overriding currentTime
		currentTime = new Date(getBooking().getCheckOut()+1);
		//currentTime overridden
				
		chargeGuestForPreviousDay(currentTime);
		chargeGuestForDay(currentTime, currentTime);
		getBooking().setBookingState(Booking.BookingState.CheckedOut);
	}
}
