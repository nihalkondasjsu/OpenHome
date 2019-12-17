package com.openhome.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.openhome.dao.helper.StringListConverter;
import com.openhome.exception.CustomException;

public class PlaceSearchQuery {

	private String keywords="";
	private String cityOrZip="";
	private String reservationStartDateTime="";
	private String reservationEndDateTime="";
	private List<String> sharingType=new ArrayList<String>();
	private List<String> propertyType=new ArrayList<String>();
	private Double minPrice=0.0;
	private Double maxPrice=10000.0;
	private Boolean internetAvailable=false;
	
	public String getKeywords() {
		if(keywords == null)
			return "";
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getReservationStartDateTime() throws CustomException {
		if(reservationStartDateTime == null)
			throw new CustomException("reservationStartDateTime not provided");
		return reservationStartDateTime;
	}
	public void setReservationStartDateTime(String reservationStartDateTime) {
		this.reservationStartDateTime = reservationStartDateTime;
	}
	public String getReservationEndDateTime() throws CustomException {
		if(reservationEndDateTime == null)
			throw new CustomException("reservationEndDateTime not provided");
		return reservationEndDateTime;
	}

	public Date getReservationStartDateTimeObj() throws ParseException, CustomException {
		return stringToDate(getReservationStartDateTime());
	}
	
	public Date getReservationEndDateTimeObj() throws ParseException, CustomException {
		return stringToDate(getReservationEndDateTime());
	}
	
	public String getWeekDays() throws ParseException, CustomException {
		List<String> weekdays = new ArrayList<String>();
		long start = getReservationStartDateTimeObj().getTime();
		long end = getReservationEndDateTimeObj().getTime();
		String[] week = "Sunday;Monday;Tuesday;Wednesday;Thursday;Friday;Saturday".split(";");
		for (long i = start; i <= end; i+= 24*60*60*1000) {
			String weekS = week[new Date(i).getDay()];
			if(weekdays.contains(weekS) == false)
				weekdays.add(weekS);
			else break;
		}
		String res = "%"+StringListConverter.listToString(weekdays).replace(";;",";%;")+"%";
		System.out.println(res);
		return res;
	}
	
	public void setReservationEndDateTime(String reservationEndDateTime) {
		this.reservationEndDateTime = reservationEndDateTime;
	}
	
	private Date stringToDate(String dateString) throws ParseException {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = simpleDateFormat.parse(dateString);
		return date;
	}
	
	public List<String> getSharingType() {
		if(sharingType == null)
			return new ArrayList<String>();
		return sharingType;
	}
	public void setSharingType(List<String> sharingType) {
		this.sharingType = sharingType;
	}
	public List<String> getPropertyType() {
		if(propertyType == null)
			return new ArrayList<String>();
		return propertyType;
	}
	public void setPropertyType(List<String> propertyType) {
		this.propertyType = propertyType;
	}
	public Double getMinPrice() {
		if(minPrice == null)
			return 0.0;
		return minPrice;
	}
	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}
	public Double getMaxPrice() {
		if(maxPrice == null)
			return 100000.0;
		return maxPrice;
	}
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Boolean getInternetAvailable() {
		if(internetAvailable == null)
			return false;
		return internetAvailable;
	}
	public void setInternetAvailable(Boolean internetAvailable) {
		this.internetAvailable = internetAvailable;
	}
	
	public boolean isCityQuery() throws CustomException {
		cityOrZip = cityOrZip.trim();
		if(cityOrZip.length() == 0)
			throw new CustomException("No City or Zip provided");
		if(cityOrZip.matches("[0-9]+")) {
			return false;
		}
		return true;
	}
	
	public String getCityOrZip() {
		return cityOrZip;
	}
	public void setCityOrZip(String cityOrZip) {
		this.cityOrZip = cityOrZip;
	}
	
	public boolean suitableMatch(PlaceDetails placeDetails) {
		if(getSharingType().size() > 0) {
			List<String> eliminateSharingType = new ArrayList<String>(Arrays.asList("Entire House","Private Room"));
			eliminateSharingType.removeAll(getSharingType());
			if(eliminateSharingType.contains(placeDetails.getRoomType())) {
				System.out.println(placeDetails.getName()+" fails at ST");
				return false;
			}
		}
		if(getPropertyType().size() > 0) {
			List<String> eliminatePropertyType = new ArrayList<String>(Arrays.asList("House","Apartment","Bed and breakfast"));
			eliminatePropertyType.removeAll(getPropertyType());
			if(eliminatePropertyType.contains(placeDetails.getPropertyType())) {
				System.out.println(placeDetails.getName()+" fails at PT");
				return false;
			}
		}
		String[] keywords = getKeywords().toLowerCase().split(" ");
		String description = placeDetails.getDescription().toLowerCase();
		boolean temp = true;
		for (String keyword : keywords) {
			if(description.contains(keyword) == false) {
				temp = false;
				break;
			}
		}
		if(temp == false) {
			System.out.println(placeDetails.getName()+" fails at KW");
			return false;
		}
		if(getInternetAvailable()) {
			if(placeDetails.getFreeWiFi() == false) {
				System.out.println(placeDetails.getName()+" fails at WF");
				return false;
			}
		}
		return true;
	}
	
}
