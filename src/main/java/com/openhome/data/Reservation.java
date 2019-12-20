package com.openhome.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

import com.openhome.dao.helper.StringListConverter;
import com.openhome.exception.CustomException;

@Entity
@Component
public class Reservation {
	
	public enum ReservationState{
		Booked,CheckedIn,CheckedOut,GuestCancelled,HostCancelled,HostBlock
	}

	public static String CHECK_IN_TIME = "15:00";
	public static String CHECK_OUT_TIME = "11:00";
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	@Transient
	private String checkInDateString;

	@Transient
	private String checkOutDateString;
	
	@Transient
	private String requiredDays;
	
	private Long checkIn;
	
	private Long checkOut;
	
	private Long actualCheckIn;
	
	private Long actualCheckOut;
	
	@Enumerated(EnumType.STRING)
	private ReservationState reservationState;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Place place;
	
	@OneToMany(fetch=FetchType.LAZY,orphanRemoval=true,mappedBy = "reservation")
	private List<Transaction> transactions;

	@OneToOne(orphanRemoval=true,cascade=CascadeType.ALL)
	private Rating guestRating;
	
	@OneToOne(orphanRemoval=true,cascade=CascadeType.ALL)
	private Rating hostRating;
	
	@OneToOne(orphanRemoval=true,cascade=CascadeType.ALL)
	private Rating placeRating;
	
	@ManyToOne
	private Guest guest;

	@Column(nullable = false)
	private Double weekdayRentPrice = 100.0;

	@Column(nullable = false)
	private Double weekendRentPrice = 140.0;
	
	@Column(nullable = false)
	private Double dailyParkingFee = 14.0;
	
	@Transient
	private String pcf;
	
	public Reservation() {
		// TODO Auto-generated constructor stub
		createdDate = new Date();
		this.transactions = new ArrayList<Transaction>();
		reservationState = ReservationState.Booked;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCheckIn() {
		return checkIn;
	}
	
	public Date dateOfCheckIn() {
		return new Date(checkIn);
	}

	public void setCheckIn(Long checkIn) {
		this.checkIn = checkIn;
	}

	public Long getCheckOut() {
		return checkOut;
	}

	public Date dateOfCheckOut() {
		return new Date(checkOut);
	}
	
	public void setCheckOut(Long checkOut) {
		this.checkOut = checkOut;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public List<Transaction> getTransactions() {
		if(transactions != null)
		Collections.sort(transactions,new Comparator<Transaction>() {

			@Override
			public int compare(Transaction o1, Transaction o2) {
				return o1.getCreatedDate().compareTo(o2.getCreatedDate());
			}
		});
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}

	public Rating getGuestRating() {
		return guestRating;
	}

	public void setGuestRating(Rating guestRating) {
		this.guestRating = guestRating;
	}

	public Rating getHostRating() {
		return hostRating;
	}

	public void setHostRating(Rating hostRating) {
		this.hostRating = hostRating;
	}

	public Rating getPlaceRating() {
		return placeRating;
	}

	public void setPlaceRating(Rating placeRating) {
		this.placeRating = placeRating;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}
	
	public Double getWeekdayRentPrice() {
		return weekdayRentPrice;
	}
	
	public void setWeekdayRentPrice(Double weekdayRentPrice) {
		this.weekdayRentPrice = weekdayRentPrice;
	}
	
	public Double getWeekendRentPrice() {
		return weekendRentPrice;
	}
	
	public void setWeekendRentPrice(Double weekendRentPrice) {
		this.weekendRentPrice = weekendRentPrice;
	}
	
	public Long getActualCheckIn() {
		return actualCheckIn;
	}

	public void setActualCheckIn(Long actualCheckIn) {
		this.actualCheckIn = actualCheckIn;
	}

	public Long getActualCheckOut() {
		return actualCheckOut;
	}

	public void setActualCheckOut(Long actualCheckOut) {
		this.actualCheckOut = actualCheckOut;
	}
	
	public ReservationState getReservationState() {
		return reservationState;
	}

	public void setReservationState(ReservationState reservationState) {
		this.reservationState = reservationState;
	}
	
	public String getRequiredDays() {
		return requiredDays;
	}

	public void setRequiredDays(String requiredDays) {
		this.requiredDays = requiredDays;
	}
	
	public String getCheckInDateString() {
		return checkInDateString;
	}

	public void setCheckInDateString(String checkInDateString) {
		this.checkInDateString = checkInDateString;
	}

	public String getCheckOutDateString() {
		return checkOutDateString;
	}

	public void setCheckOutDateString(String checkOutDateString) {
		this.checkOutDateString = checkOutDateString;
	}
	
	public Double getDailyParkingFee() {
		return dailyParkingFee;
	}

	public void setDailyParkingFee(Double dailyParkingFee) {
		this.dailyParkingFee = dailyParkingFee;
	}

	public void prepareForRegistration(Date createdDate,Place place,Guest guest) throws ParseException, CustomException {
		
		this.createdDate = createdDate;
		
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		this.checkIn = simpleDateFormat.parse(checkInDateString+" "+CHECK_IN_TIME).getTime();
		this.checkOut = simpleDateFormat.parse(checkOutDateString+" "+CHECK_OUT_TIME).getTime();
		
		if(this.checkIn<createdDate.getTime()) {
			throw new CustomException("Invalid Reservation. Predate.");
		}
		
		if(this.checkOut - this.checkIn < 20 * 60 * 60 * 1000l) {
			throw new CustomException("Invalid Reservation. Less than 20hrs.");
		}
		
		if(this.checkOut - this.checkIn > 14 * 24 * 60 * 60 * 1000l) {
			throw new CustomException("Invalid Reservation. Greater than 14days");
		}
		
		if(this.checkOut - createdDate.getTime() > (365 * 24 * 60 * 60 * 1000l) ) {
			System.out.println("");
			
			throw new CustomException("Invalid Reservation. Checkout is 365 days away.");
		}
		
		this.requiredDays = weekDays();
		
		this.actualCheckIn = null;
		this.actualCheckOut = checkOut;
		
		this.reservationState = ReservationState.Booked;
		
		this.place = place;
		
		this.transactions = new ArrayList<Transaction>();
		
		this.guestRating = null;
		this.hostRating = null;
		this.placeRating = null;
		
		this.guest = guest;
		
		this.weekdayRentPrice = this.place.getPlaceDetails().getWeekdayRentPrice();
		this.weekendRentPrice = this.place.getPlaceDetails().getWeekendRentPrice();
		this.dailyParkingFee = this.place.getPlaceDetails().getDailyParkingFee();
		
	}
	
	public void prepareForHostBlock(Date createdDate,Place place) throws ParseException, CustomException {
		
		this.createdDate = createdDate;
		
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		this.checkIn = simpleDateFormat.parse(checkInDateString+" "+"00:00").getTime();
		this.checkOut = simpleDateFormat.parse(checkOutDateString+" "+"23:59").getTime();
		
		if(this.checkIn<createdDate.getTime()) {
			throw new CustomException("Invalid Reservation. Predate.");
		}
		
		if(this.checkOut - this.checkIn < 20 * 60 * 60 * 1000l) {
			throw new CustomException("Invalid Reservation. Less than 20hrs.");
		}
		
		if(this.checkOut > createdDate.getTime() + 365 * 24 * 60 * 60 * 1000l ) {
			throw new CustomException("Invalid Reservation");
		}
		
		this.reservationState = ReservationState.HostBlock;
		
		this.place = place;
		
		this.transactions = new ArrayList<Transaction>();
		
		this.guestRating = null;
		this.hostRating = null;
		this.placeRating = null;
		
	}
	
	public String weekDays() throws ParseException {
		
		List<String> weekdays = new ArrayList<String>();
		
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		long start = simpleDateFormat.parse(checkInDateString+" 00:00").getTime();
		long end = simpleDateFormat.parse(checkOutDateString+" 00:00").getTime();
		
		String[] week = "Sunday;Monday;Tuesday;Wednesday;Thursday;Friday;Saturday".split(";");
		
		for (long i = start; i <= end; i+= 24*60*60*1000l) {
			String weekS = week[new Date(i).getDay()];
			if(weekdays.contains(weekS) == false)
				weekdays.add(weekS);
			else break;
		}
		
		String res = "%"+StringListConverter.listToString(weekdays).replace(";;",";%;")+"%";
		
		System.out.println(res);
		
		return res;
	}

	@Override
	public String toString() {
		try {
			return String.format(
					"{ createdDate : %s , checkIn : %s , checkOut : %s , actualCheckIn : %s , actualCheckOut : %s , reservationState : %s , transactions: %s , weekdayRentPrice : %f , weekendRentPrice : %f , dailyParkingFee : %f}",
					createdDate,
					new Date(checkIn),
					new Date(checkOut),
					actualCheckIn == null ? "No Record" : new Date(actualCheckIn),
					new Date(actualCheckOut),
					reservationState,
					transactions,
					weekdayRentPrice,
					weekendRentPrice,
					dailyParkingFee
					);
		} catch (Exception e) {
			// TODO: handle exception
			return super.toString();
		}
	}

	public String getPcf() {
		return pcf;
	}

	public void setPcf(String pcf) {
		this.pcf = pcf;
	}

	public void calculatePcf(Date date) {
		if(date.getTime() > this.actualCheckOut ) {
			this.pcf = "Past";
		}else if(date.getTime() < this.checkIn ) {
			this.pcf = "Future";
		}else {
			this.pcf = "Current";
		}
	}
}
