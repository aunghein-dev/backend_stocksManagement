package com.aunghein.SpringTemplate.repository;


import com.aunghein.SpringTemplate.model.Users;
import com.aunghein.SpringTemplate.model.dto.TellerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(value = """
            select id,
            	   username,
            	   role,
            	   full_name,
            	   user_img_url
            from users
            where business_id = :bizId
           """, nativeQuery = true)
    List<TellerResponse> getAllTellersForBiz(@Param("bizId") Long bizId);
}
