package com.bloemist.repositories;

import com.bloemist.entity.OrderReport;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
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
      + "orders.deliveryDate >= :startTime AND "
      + "orders.deliveryDate < :endTime "
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  List<OrderReport> getOrderInDateRange(@Param("startTime") Date startTime,@Param("endTime") Date endTime);


  @Query( value = "SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE  (orders.clientPhone = :condition OR "
      + "orders.clientName ILIKE :condition%) AND "
      + "orders.deliveryDate >= :startTime "
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  List<OrderReport> searchOrderByPhoneOrNameInDateRange(@Param("condition") String condition, @Param("startTime") Date startTime, Pageable pageable);

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE  orders.orderStatus < 3 AND "
      + "orders.deliveryDate = :startTime  AND "
      + "orders.deliveryTime ILIKE %:condition%"
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  Set<OrderReport> getOrderNeedToShipInDateRange(@Param("startTime") Date startTime, @Param("condition") String condition);

  @Query("SELECT orders "
      + "FROM OrderReport orders "
      + "WHERE  orders.orderStatus < 2 AND "
      + "orders.deliveryDate = :startTime  AND "
      + "orders.deliveryTime ILIKE %:condition%"
      + "ORDER BY orders.orderStatus ASC,"
      + "orders.deliveryDate DESC,"
      + "orders.deliveryTime DESC")
  Set<OrderReport> getOrderNeedToDoneInDateRange(@Param("startTime") Date startTime, @Param("condition") String condition);


}
