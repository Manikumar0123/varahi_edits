package com.varahiedits.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varahiedits.model.Images;
@Repository
public interface ImageRepository extends JpaRepository<Images, Integer>{

}
