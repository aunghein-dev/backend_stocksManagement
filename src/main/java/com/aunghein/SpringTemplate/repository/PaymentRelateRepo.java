package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.PaymentRelate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRelateRepo extends JpaRepository<PaymentRelate, Long> {
}
