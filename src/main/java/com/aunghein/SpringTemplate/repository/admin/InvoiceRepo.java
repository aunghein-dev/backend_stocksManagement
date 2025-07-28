package com.aunghein.SpringTemplate.repository.admin;

import com.aunghein.SpringTemplate.model.billing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

    @Query(value = "select * from invoice i where biz_id = :bizId", nativeQuery = true)
    List<Invoice> getAllInvoicesByBizId(@Param("bizId") Long bizId);

    @Query(value = "select * from invoice where tran_hist_id = :tranId limit 1", nativeQuery = true)
    Invoice getSpecificInvoiceByTranId(@Param("tranId") String tranId);
}
