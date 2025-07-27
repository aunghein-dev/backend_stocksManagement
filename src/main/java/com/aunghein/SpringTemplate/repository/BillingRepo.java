package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepo extends JpaRepository<CurrBilling, Long> {

    @Query(value = "select * from curr_billing where biz_id = :bizId", nativeQuery = true)
    CurrBilling getBillingOfCurrentPlanByBizId(@Param("bizId") Long bizId);
}
