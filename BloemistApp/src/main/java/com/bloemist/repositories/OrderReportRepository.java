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

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE  orders.orderStatus NOT IN (6, 7) AND "
      + "orders.orderDate >= :startTime AND "
      + "orders.orderDate < :endTime "
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  List<OrderReport> getOrdersForStaff(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE orders.orderStatus != 7 AND "
      + "orders.orderDate >= :startTime AND "
      + "orders.orderDate < :endTime "
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  List<OrderReport> getOrdersAdmin(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

  List<OrderReport> findOrderReportByOrderCodeIn(List<String> codes);

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE orders.clientName ILIKE :condition% OR "
      + "orders.clientPhone = :condition OR "
      + "orders.receiver ILIKE :condition% OR "
      + "orders.receiverPhone = :condition OR "
      + "orders.orderCode = :condition")
  List<OrderReport> searchOrderReportByConditionForStaff(@Param("condition")String condition);

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE orders.clientName ILIKE %:condition% OR "
      + "orders.clientPhone = :condition OR "
      + "orders.receiver ILIKE %:condition% OR "
      + "orders.receiverPhone = :condition OR "
      + "orders.orderCode = :condition OR "
      + "orders.clientSocialLink ILIKE %:condition%")
  List<OrderReport> searchOrderReportByConditionForAdmin(@Param("condition")String condition);
}
