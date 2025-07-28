package com.aunghein.SpringTemplate.repository.admin;

import com.aunghein.SpringTemplate.model.admin.AmBillingReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmBillingReceiveRepo extends JpaRepository<AmBillingReceive, Long> {
}
