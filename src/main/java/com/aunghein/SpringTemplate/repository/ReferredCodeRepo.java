package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.ReferredCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReferredCodeRepo extends JpaRepository<ReferredCode, Long> {

    // Corrected: Changed 'isUsed' to 'is_used' in the SELECT statement
    @Query(value = "SELECT is_used FROM referred_code WHERE code = :secret_code", nativeQuery = true)
    boolean isAlreadyUsedByCode(@Param("secret_code") String secretCode);

    @Query(value = "SELECT count(*) FROM referred_code WHERE code = :secret_code", nativeQuery = true)
    Integer hasValueCode(@Param("secret_code") String secretCode);

    @Query(value = "SELECT * FROM referred_code WHERE code = :secret_code", nativeQuery = true)
    ReferredCode findByCode(@Param("secret_code") String secretCode);
}