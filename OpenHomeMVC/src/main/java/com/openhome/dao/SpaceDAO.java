package com.openhome.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.openhome.data.Space;

public interface SpaceDAO extends JpaRepository<Space,Long>{

//	@Query("select s from Space s,Booking b,Address a where s.id = b.space_id and a.id = s.address_id and ( a.latitude between ?1 and ?2 ) and ( a.longitude between ?3 and ?4 ) ")
//	public List<Space> findAllSpaces(Long latitude1,Long latitude2,Long longitude1,Long longitude2,Date checkIn,Date checkOut);
	
	@Query("select count(*) from Space s left join s.host h where h.id = ?1")
	//@Query("select h from Host h left join h.userDetails ud where ud.email = ?1")
	public long getSpaceCountOfHost(Long hostId);
	
//	@Query("select s from Space s left join s.spaceDetails sd left join s.host h where h.id = ?1 and sd.name = ?2")
//	public Space getSpaceBySpaceNameAndHost(Long hostId,String spaceName);
	
	@Query("select s from Space s left join s.spaceDetails sd where sd.id = ?1")
	public Space getSpaceBySpaceDetailsId(Long spaceDetailsId);
	
	@Query(value="select s.id from Space s , Space_Details sd where s.host_id = ?1 and s.space_details_id = sd.id and sd.name = ?2",nativeQuery=true)
	public Long getSpaceByHostAndName(Long hostId,String spaceName);
	
//	select s_name, score, status, address_city, email_id,
//	accomplishments from student s inner join marks m on
//	s.s_id = m.s_id inner join details d on 
//	d.school_id = m.school_id;
	
	@Query(value="select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where sd.address in (select a from Address a where (a.latitude between ?1 and ?2) and (a.longitude between ?3 and ?4))) and 0 = (select count(*) from Booking b where b.space = s and (( ?5 between b.checkIn and b.checkOut ) or ( ?6 between b.checkIn and b.checkOut ) or ( b.checkIn between ?5 and ?6 ) or ( b.checkOut between ?5 and ?6 )))")
	public List<Space> getSpacesByLocationAndDates(Double minLatitude,Double maxLatitude,Double minLongitude,Double maxLongitude,Long startDate,Long endTime);
	
	@Query(value="select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where sd.address in (select a from Address a where a.zip = ?1)) and 0 = (select count(*) from Booking b where b.space = s and (( ?2 between b.checkIn and b.checkOut ) or ( ?3 between b.checkIn and b.checkOut ) or ( b.checkIn between ?2 and ?3 ) or ( b.checkOut between ?2 and ?3 )))")
	public List<Space> getSpacesByZipAndDates(String zip,Long startDate,Long endTime);
	
}
