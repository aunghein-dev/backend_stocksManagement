package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessRepo extends JpaRepository<Business, Long> {

    @Query(value = "SELECT * FROM business WHERE registered_by = :email", nativeQuery = true)
    Business findBizByEmail(@Param("email") String email);
}
