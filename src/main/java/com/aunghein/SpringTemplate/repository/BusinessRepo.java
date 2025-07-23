package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessRepo extends JpaRepository<Business, Long> {

    @Query(
            value = "SELECT * FROM business WHERE business_id = (" +
                    "SELECT business_id FROM users WHERE username = :username)",
            nativeQuery = true
    )
    Business findBusinessByUsername(@Param("username") String username);

}
