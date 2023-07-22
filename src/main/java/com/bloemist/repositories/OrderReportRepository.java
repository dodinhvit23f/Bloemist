package com.bloemist.repositories;

import com.bloemist.entity.OrderReport;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReportRepository extends JpaRepository<OrderReport, Long> {

  Optional<OrderReport> findByOrderCode(String code);

  @Query("SELECT order "
      + "FROM OrderReport order "
      + "WHERE  order.orderStatus <> 6 AND "
      + "order.orderDate >= :startTime AND "
      + "order.orderDate <= :endTime "
      + "ORDER BY order.deliveryDate ASC,"
      + "order.deliveryTime ASC, "
      + "order.orderStatus ASC")
  List<OrderReport> getOrders(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

  List<OrderReport> findOrderReportByOrderCodeIn(List<String> codes);
}
