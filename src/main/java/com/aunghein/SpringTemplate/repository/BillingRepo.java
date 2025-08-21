package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.billing.CurrBilling;
import com.aunghein.SpringTemplate.model.dto.StorageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepo extends JpaRepository<CurrBilling, Long> {

    @Query(value = "select * from curr_billing where biz_id = :bizId", nativeQuery = true)
    CurrBilling getBillingOfCurrentPlanByBizId(@Param("bizId") Long bizId);

    @Query(value = """
          with base as
         (select curr_plan_code, curr_expire_date
            from curr_billing
           where biz_id = :bizId
           order by curr_expire_date desc
           limit 1),
         b1 as
         (select case when curr_expire_date >= current_date
                      then curr_plan_code
                      else 'free' end status
            from base)
         select
           r.limit_storage_kb  as limitStorageKb,
           r.limit_storage_txt as limitStorageTxt,
           r.long_name         as longName
         from b1
         left join billing_rule r on r.code = b1.status
        """, nativeQuery = true)
    StorageProjection getStorageResponse(@Param("bizId") Long bizId);


}
