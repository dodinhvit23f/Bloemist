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
      + "WHERE  order.orderStatus NOT IN (6, 7) AND "
      + "order.orderDate >= :startTime AND "
      + "order.orderDate <= :endTime "
      + "ORDER BY order.orderStatus ASC,"
      + "order.deliveryDate DESC,"
      + "order.deliveryTime DESC")
  List<OrderReport> getOrdersForStaff(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

  @Query("SELECT order "
      + "FROM OrderReport order "
      + "WHERE "
      + "order.orderDate >= :startTime AND "
      + "order.orderDate <= :endTime "
      + "ORDER BY order.orderStatus ASC,"
      + "order.deliveryDate DESC,"
      + "order.deliveryTime DESC")
  List<OrderReport> getOrdersAdmin(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

  List<OrderReport> findOrderReportByOrderCodeIn(List<String> codes);
}
