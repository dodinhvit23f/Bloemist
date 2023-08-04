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

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderPrintControllers extends OrderController {

  public static final String LOGO = "logo";
  public static final String CUSTOMER_NAME = "customer_name";
  public static final String CUSTOMER_PHONE = "customer_phone";
  public static final String ORDER_DESCRIPTION = "order_description";
  public static final String BANNER_DESCRIPTION = "banner_description";
  public static final String RECEIVE_NAME = "receive_name";
  public static final String RECEIVE_PHONE = "receive_phone";
  public static final String RECEIVE_TIME = "receive_time";
  public static final String RECEIVE_DATE = "receive_date";
  public static final String SALE_PRICE = "sale_price";
  public static final String DELIVERY_FEE = "delivery_fee";
  public static final String SALE_OFF = "sale_off";
  public static final String TOTAL_PRICE = "total_price";
  public static final String DEPOSIT_AMOUNT = "deposit_amount";
  public static final String REMAIN_AMOUNT = "remain_amount";
  public static final String STAFF_NAME = "staff_name";
  public static final String SRC = "src";
  public static final String PRODUCT = "product";
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
    order = ApplicationVariable.currentOrder;

    if (Objects.isNull(this.order)) {
      return;
    }

    if (a5Bill.isSelected()) {
      printA5(choicePrinter.getValue(), this.order);
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
