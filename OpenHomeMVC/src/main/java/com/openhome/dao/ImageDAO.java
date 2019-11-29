package com.openhome.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.openhome.data.Image;

public interface ImageDAO extends JpaRepository<Image, Long>{

}
