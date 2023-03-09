package com.bloemist.services;

import com.bloemist.dto.Order;
import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface IOrderService {

  @Transactional
  Order createNewOrder(Order order);

  @Transactional
  void updateOrder(Order order);

  @Transactional
  void deleteOrder(String orderCode);

  @Transactional
  void changeOrderStateInfo(Order order);

  @Transactional(readOnly = true)
  List<Order> getPage(Date startTime, Date endTime);


}
