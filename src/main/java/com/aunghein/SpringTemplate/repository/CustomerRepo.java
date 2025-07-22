package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.Customer;
import com.aunghein.SpringTemplate.model.dto.CustomerDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT * FROM customer WHERE biz_id = :bizId", nativeQuery = true)
    List<Customer> findAllByBizId(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM customer WHERE biz_id = :bizId AND cid = :cid", nativeQuery = true)
    Optional<Customer> findByCidAndBizId(@Param("cid") String cid, @Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_customer_dashboard(:bizId)", nativeQuery = true)
    CustomerDashboard getCustomerDashboard(@Param("bizId") Long bizId);
}
