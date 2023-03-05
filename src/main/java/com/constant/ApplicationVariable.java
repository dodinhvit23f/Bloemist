package com.constant;

import com.bloemist.dto.AccountDetail;
import com.bloemist.dto.Order;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ApplicationVariable {

  private ApplicationVariable() {
  }

  private static AccountDetail user;
  private static List<Order> ORDERS = new ArrayList<>();

  public static Order currentOrder;

  public static AccountDetail getUser() {
    return user;
  }

  public static void setUser(AccountDetail loginUser) {
    user = loginUser;
  }

  public static List<Order> getOrders(){
    return  ORDERS;
  }
  public static void setOrders(List<Order> orders){
    ORDERS = orders;
  }
  public static void add(Collection<Order> orders){
    ORDERS .addAll(orders);
  }

}
