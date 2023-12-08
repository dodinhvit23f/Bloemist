package com.bloemist.services;

import com.bloemist.dto.Order;
import java.io.IOException;

public interface IPrinterService {
  void printA5Order(String printerName, Order order)
      throws IOException;

  void printA5Image(String printerName, Order order);
}
