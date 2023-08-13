package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.bloemist.services.IOrderService;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class OrderPrintControllers extends OrderController {
  public static final String STAFF_NAME = "staff_name";
  IOrderService orderService;
  Order order;
  @FXML
  private ChoiceBox<String> choicePrinter;
  @FXML
  private RadioButton a5Bill;
  @FXML
  private RadioButton heatBill;
  @FXML
  private RadioButton imageBill;

  protected OrderPrintControllers(ApplicationEventPublisher publisher, IOrderService orderService) {
    super(publisher);
    this.orderService = orderService;
  }

  @FXML
  public void applyPrint() throws IOException {
    Set<Order> printOrders = ApplicationVariable.getOrders().stream().filter(o -> o.getIsSelected())
        .collect(Collectors.toSet());

    if (ObjectUtils.isEmpty(printOrders)) {
      return;
    }

    if (a5Bill.isSelected()) {
      if (imageBill.isSelected()) {

      }else {
        printOrders.forEach(
            orderForPrint -> printA5(choicePrinter.getValue(), orderForPrint)
        );
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    var printerServices = PrintServiceLookup.lookupPrintServices(null, null);
    order = ApplicationVariable.currentOrder;
    choicePrinter.setItems(
        FXCollections.observableList(
            Arrays.stream(printerServices)
                .map(PrintService::getName)
                .toList()));
  }

  @Override
  public void cancel() {
    switchScene(stageManager.getPreviousView());
  }

  @Override
  public void extractData() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void initEvent() {
    throw new UnsupportedOperationException();
  }
}
