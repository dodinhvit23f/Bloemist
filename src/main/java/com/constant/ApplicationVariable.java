package com.constant;

import com.bloemist.dto.AccountDetail;
import com.bloemist.dto.Order;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Setter;

public final class ApplicationVariable {

  private ApplicationVariable() {
  }

  private static AccountDetail user;
  private static List<Order> order = new ArrayList<>();

  @Setter
  public static Order currentOrder;

  public static AccountDetail getUser() {
    return user;
  }

  public static void setUser(AccountDetail loginUser) {
    user = loginUser;
  }

  public static List<Order> getOrders() {
    return order;
  }

  public static void setOrders(List<Order> orders) {
    order = orders;
  }

  public static void add(Collection<Order> orders) {
    order.addAll(orders);
  }

  public static void add(Order order) {
    ApplicationVariable.order.add(order);
  }

  public static void addFirst(Order order) {
    ApplicationVariable.order.add(BigInteger.ZERO.intValue(), order);
  }

  public static void sortOrders() {
    order.sort((previous, next) -> {

      if (!Objects.equals(previous.getPriority(), next.getPriority())) {
        return previous.getPriority().compareTo(next.getPriority());
      }

      if (!previous.getOrderDate().equals(next.getOrderDate())) {
        return previous.getOrderDate().compareTo(next.getOrderDate());
      }

      return previous.getDeliveryHour().compareTo(next.getDeliveryHour());
    });
    setTableSequence();
  }

  public static void setTableSequence() {
    AtomicInteger stt = new AtomicInteger(BigInteger.ONE.intValue());
    order.forEach(order -> order.setStt(String.valueOf(stt.getAndIncrement())));
  }
}
