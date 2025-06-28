package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.StkGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StkRepo extends JpaRepository<StkGroup, Long> {

    @Query(value = "SELECT * FROM stk_group WHERE business_id = :businessId ORDER BY released_date DESC", nativeQuery = true)
    List<StkGroup> findStkGroupByBusinessId(@Param("businessId") Long businessId);


    @Query(value = "SELECT * FROM stk_group WHERE business_id = :businessId",
            countQuery = "SELECT count(*) FROM stk_group WHERE business_id = :businessId",
            nativeQuery = true)
    Page<StkGroup> findByBusinessId(@Param("businessId") Long businessId, Pageable pageable);


}
