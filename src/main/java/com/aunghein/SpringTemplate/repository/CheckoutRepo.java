package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.dto.*;
import com.aunghein.SpringTemplate.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CheckoutRepo extends JpaRepository<Checkout, Long> {

    @Query(value = "SELECT * FROM checkout WHERE biz_id = :businessId", nativeQuery = true)
    List<Checkout> findCheckoutByBizId(@Param("businessId") Long businessId);

    @Query(value = "SELECT * FROM checkout WHERE tranid = :tranId AND biz_id = :bizId LIMIT 1", nativeQuery = true)
    Optional<Checkout> findCheckoutByBizIdTranId(@Param("bizId") Long bizId, @Param("tranId") Long tranId);

    @Query(value = """
    SELECT batch_id as batchId,
           SUM(checkout_qty) as totalQty,
           COUNT(stk_item_id) as stkItemCnt,
           SUM(sub_checkout) as checkoutTotal,
           MAX(tran_date) as tranDate,
           tran_user_email,
           biz_id
    FROM checkout
    WHERE biz_id = :bizId
    GROUP BY batch_id, tran_user_email, biz_id
    """, nativeQuery = true)
    List<BatchReport> findBatchReportByBizId(@Param("bizId") Long bizId);

    @Query(value = "SELECT get_business_storage_usage(:bizId) AS usagePercentage", nativeQuery = true)
    StorageLimitRateProjection getStorageUsage(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_radar_data(:bizId)", nativeQuery = true)
    List<RadarData> getRadarDataByBizId(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_dashboard_summary(:bizId)", nativeQuery = true)
    DashboardMiniCard getDashboardSummary(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_pie_data(:bizId)", nativeQuery = true)
    List<PieData> getPieData(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_linechart_data(:bizId)", nativeQuery = true)
    List<LinechartData> getLineChartData(@Param("bizId") Long bizId);

    @Query(value = "SELECT * FROM get_top_sold_by_month(:bizId)", nativeQuery = true)
    List<BarsetDataDTO> getTopSoldByMonth(@Param("bizId") Long bizId);

    @Query(value = """
    select batch_id as batch,
           max(tran_date) as date,
           stk_group_id as id,
           stk_group_name as name,
           item_unit_price as price,
           sum(checkout_qty) as quantity,
           sum((item_unit_price * checkout_qty)) as subTotal
    from checkout where batch_id  = :batchId
    and biz_id = :bizId
    group by batch, id, name, price
    """, nativeQuery = true)
    List<VouncherGen> getVoucherData (@Param("batchId") String batchId,@Param("bizId") Long bizId);
}
