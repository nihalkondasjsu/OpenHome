package com.openhome.data.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.dao.RatingDAO;
import com.openhome.dao.ReservationDAO;
import com.openhome.dao.UserDetailsDAO;
import com.openhome.data.Rating;
import com.openhome.data.Reservation;
import com.openhome.exception.CustomException;

@Component
public class RatingManager {

	@Autowired(required=true)
	RatingDAO ratingDao;
	
	@Autowired(required=true)
	ReservationDAO reservationDao;
	
	@Autowired(required=true)
	PlaceDetailsDAO placeDetailsDao;

	@Autowired(required=true)
	UserDetailsDAO userDetailsDao;
	
	public Rating addRating(Date currentDate,Long reservationId,Rating rating,boolean userIsHost) throws CustomException {
		rating.setCreatedDate(currentDate);
		
		Reservation reservation = reservationDao.getOne(reservationId);
		
		rating.setReservation(reservation);
		
		Rating old = null ;
		
		if(rating.getTarget() == Rating.RatingTarget.GUEST) {
			
			if(userIsHost == false)
				throw new CustomException("Guest cannot give rating to guest.");
			
			if(reservation.getGuestRating() != null) {
				
				old = reservation.getGuestRating();
				
				reservation.getGuest().getUserDetails().removeRating(
						reservation.getGuestRating()
						);
				
				
				
			}
			reservation.getGuest().getUserDetails().addRating(rating);
			
			userDetailsDao.updateRating(
					reservation.getGuest().getUserDetails().getId(),
					reservation.getGuest().getUserDetails().getAverageRating(),
					reservation.getGuest().getUserDetails().getTotalReviewsCount()
					);
			
			reservation.setGuestRating(rating);
			
		}else if(rating.getTarget() == Rating.RatingTarget.HOST) {

			if(userIsHost)
				throw new CustomException("Host cannot give rating to host.");
			
			if(reservation.getHostRating() != null) {

				old = reservation.getHostRating();
				
				reservation.getPlace().getHost().getUserDetails().removeRating(
						reservation.getHostRating()
						);

				
				
			}
			
			reservation.getPlace().getHost().getUserDetails().addRating(rating);
			
			userDetailsDao.updateRating(
					reservation.getPlace().getHost().getUserDetails().getId(),
					reservation.getPlace().getHost().getUserDetails().getAverageRating(),
					reservation.getPlace().getHost().getUserDetails().getTotalReviewsCount()
					);
			
			reservation.setHostRating(rating);
			
		}else {

			if(userIsHost)
				throw new CustomException("Host cannot give rating to place.");
			
			rating.setTarget(Rating.RatingTarget.PLACE);
			
			if(reservation.getPlaceRating() != null) {
				
				old = reservation.getPlaceRating();
				
				reservation.getPlace().getPlaceDetails().removeRating(
						reservation.getPlaceRating()
						);
				
			}
			
			reservation.getPlace().getPlaceDetails().addRating(rating);
			
			placeDetailsDao.updateRating(
					reservation.getPlace().getPlaceDetails().getId(),
					reservation.getPlace().getPlaceDetails().getAverageRating(),
					reservation.getPlace().getPlaceDetails().getTotalReviewsCount()
					);
			
			reservation.setPlaceRating(rating);
			
		}
		
		if(old!= null)
			ratingDao.deleteById(old.getId());
		
		reservationDao.save(reservation);
		
		return ratingDao.save(rating);
	}

	public Rating readRating(Long ratingId) {
		return ratingDao.getOne(ratingId);
	}
	
	public void deleteRating(Long ratingId,boolean userIsHost) throws CustomException {
		Rating rating = readRating(ratingId);
		
		Reservation reservation = rating.getReservation();
		
		rating.setReservation(reservation);
		
		if(rating.getTarget() == Rating.RatingTarget.GUEST) {
			
			if(userIsHost == false)
				throw new CustomException("Guest cannot delete rating of guest.");
			
			if(reservation.getGuestRating() != null) {
				
				reservation.getGuest().getUserDetails().removeRating(
						reservation.getGuestRating()
						);
				
				userDetailsDao.updateRating(
						reservation.getGuest().getUserDetails().getId(),
						reservation.getGuest().getUserDetails().getAverageRating(),
						reservation.getGuest().getUserDetails().getTotalReviewsCount()
						);
				
			}
			
			reservation.setGuestRating(null);
			
		}else if(rating.getTarget() == Rating.RatingTarget.HOST) {

			if(userIsHost)
				throw new CustomException("Host cannot delete rating of host.");
			
			if(reservation.getHostRating() != null) {
				
				reservation.getPlace().getHost().getUserDetails().removeRating(
						reservation.getHostRating()
						);
				
				userDetailsDao.updateRating(
						reservation.getPlace().getHost().getUserDetails().getId(),
						reservation.getPlace().getHost().getUserDetails().getAverageRating(),
						reservation.getPlace().getHost().getUserDetails().getTotalReviewsCount()
						);
				
			}
			
			reservation.setHostRating(null);
			
		}else {

			if(userIsHost)
				throw new CustomException("Host cannot delete rating of place.");
			
			rating.setTarget(Rating.RatingTarget.PLACE);
			
			if(reservation.getPlaceRating() != null) {
				
				reservation.getPlace().getPlaceDetails().removeRating(
						reservation.getPlaceRating()
						);
				
				placeDetailsDao.updateRating(
						reservation.getPlace().getPlaceDetails().getId(),
						reservation.getPlace().getPlaceDetails().getAverageRating(),
						reservation.getPlace().getPlaceDetails().getTotalReviewsCount()
						);
				
			}
			
			reservation.setPlaceRating(null);
			
		}
		
		ratingDao.deleteById(ratingId);
		
		reservationDao.save(reservation);
		
	}
	
}
