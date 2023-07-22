package com.bloemist.services;

import com.bloemist.dto.Order;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface IOrderService {

  @Transactional
  void createNewOrder(Order order);

  @Transactional
  void createNewOrders(Collection<Order> order);

  @Transactional
  void updateOrder(Order order);

  void updateOrders(List<Order> orders);

  @Transactional
  void deleteOrder(String orderCode);

  @Transactional
  void changeOrderStateInfo(Order order);

  @Transactional(readOnly = true)
  List<Order> getPage(LocalDateTime startTime, LocalDateTime endTime);

  boolean validOrder(Order orderInfo);
}
