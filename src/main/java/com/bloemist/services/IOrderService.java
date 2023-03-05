package com.bloemist.services;

import com.bloemist.dto.CustomerOrder;
import com.bloemist.dto.Order;
import com.constant.OrderState;
import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface IOrderService {

  @Transactional
  void createNewOrder(CustomerOrder order);

  @Transactional
  void updateOrder(CustomerOrder order);

  @Transactional
  void deleteOrder(String orderCode);

  @Transactional
  void changeOrderStateInfo(Order order);

  @Transactional(readOnly = true)
  List<Order> getPage(Date startTime, Date endTime);


}
