package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.openhome.data.Image;

public interface ImageDAO extends JpaRepository<Image, Long>{

	@Transactional
	@Modifying
	@Query("update Image i set i.publicId = :publicId , i.url = :url where i.id = :id")
	public void updateImage(@Param("id") Long id, @Param("publicId") String publicId, @Param("url") String url);

}
