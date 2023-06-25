package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.dto.Order;
import com.bloemist.dto.OrderInfo;
import com.bloemist.events.MessageWarning;
import com.bloemist.funcation.MethodParameter;
import com.bloemist.services.IOrderService;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.constant.Constants;
import com.utils.Utils;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class OrderController extends BaseController {

  public static final int INT_100 = 100;
  public static final AtomicBoolean isEnd = new AtomicBoolean(Boolean.TRUE);
  public static final int SEVEN_DAYS = 7;
  @Autowired
  IOrderService orderService;
  @Autowired
  IPrinterService printerService;


  protected OrderController(ApplicationEventPublisher publisher) {
    super(publisher);
  }


  public Double getSalePrice(Double truePrice, Double discount) {
    return truePrice - (truePrice * discount / INT_100);
  }

  public Double getTotalPrice(Double salePriceValue, Double deliveryFee, Double vatFee) {
    return salePriceValue + deliveryFee + (salePriceValue * vatFee / INT_100);
  }

  protected void addEventLostFocus(TextField textField, MethodParameter consumer) {
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (Boolean.FALSE.equals(newValue)) {
        consumer.apply();
      }
    });
  }

  protected boolean validateOrderInfo(OrderInfo orderInfor) {
    if (ObjectUtils.isEmpty(orderInfor.getCustomerName())
        || ObjectUtils.isEmpty(orderInfor.getCustomerPhone())
        || ObjectUtils.isEmpty(orderInfor.getDeliveryAddress())
        || ObjectUtils.isEmpty(orderInfor.getDeliveryTime())
        || ObjectUtils.isEmpty(orderInfor.getImagePath())
        || ObjectUtils.isEmpty(orderInfor.getTruePrice())
        || ObjectUtils.isEmpty(orderInfor.getDeliveryFee())
        || ObjectUtils.isEmpty(orderInfor.getDepositAmount())
        || ObjectUtils.isEmpty(orderInfor.getRemainAmount())
        || ObjectUtils.isEmpty(orderInfor.getSalePrice())
        || ObjectUtils.isEmpty(orderInfor.getTotalAmount())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_001));
      return Boolean.FALSE;
    }

    if (!Utils.isNumber(orderInfor.getTruePrice())
        || !Utils.isNumber(orderInfor.getDeliveryFee())
        || !Utils.isNumber(orderInfor.getVatFee())
        || !Utils.isNumber(orderInfor.getDepositAmount())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.FALSE;
    }

    if (orderInfor.getDeliveryTime().length() != 5) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_006));
      return Boolean.FALSE;
    }

    if (!validateTime(orderInfor.getDeliveryTime().split(":"))) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_005));
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  protected boolean validateTime(String[] deliveryTimeAr) {
    if (deliveryTimeAr.length != BigInteger.TWO.intValue()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003));
      return Boolean.FALSE;
    }

    if (!Utils.isNumber(deliveryTimeAr[BigInteger.ZERO.intValue()]) ||
        !Utils.isNumber(deliveryTimeAr[BigInteger.ONE.intValue()])) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  protected Date getDeliveryDate(DatePicker datePicker) {
    return Date.from(datePicker.getValue()
        .atTime(BigInteger.ZERO.intValue(), BigInteger.ZERO.intValue())
        .atZone(ZoneId.systemDefault()).toInstant());
  }

  protected void loadPageAsync(Boolean isNew, TableView<Order> orderTable) {
    CompletableFuture<List<Order>> orderLoading = CompletableFuture
        .supplyAsync(() -> {
          var now = Instant.now();
          if (Objects.isNull(isNew)) {
            return orderService
                .getPage(Date.from(now.minus(SEVEN_DAYS, ChronoUnit.DAYS)),
                    Date.from(now));
          }
          // load new record
          if (Boolean.TRUE.equals(isNew)) {
            return orderService.getPage(Date.from(
                    LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(now));
          }
          // load old record
          if (isEnd.get()) {
            return Collections.emptyList();
          }

          var min = ApplicationVariable.getOrders().stream().min(
              Comparator.comparing(Order::getDeliveryDate)).orElseThrow();

          return orderService.getPage(
              Date.from(LocalDateTime.of(
                      now.get(ChronoField.YEAR),
                      now.get(ChronoField.MONTH_OF_YEAR),
                      now.get(ChronoField.DAY_OF_MONTH),
                      BigInteger.ZERO.intValue(),
                      BigInteger.ZERO.intValue(),
                      BigInteger.ZERO.intValue())
                  .minus(SEVEN_DAYS, ChronoUnit.DAYS)
                  .atZone(ZoneId.systemDefault()).toInstant()),
              Utils.toDate(min.getDeliveryDate()));
        });

    orderLoading.thenAccept(orders -> {
      if (Objects.isNull(isNew)) {
        ApplicationVariable.setOrders(orders);
        setDataOrderTable(orderTable);
        return;
      }

      if (Boolean.TRUE.equals(isNew)) {
        handleLatest(orderTable, orders);
        return;
      }

      handleOldData(orderTable, orders);
    });
  }

  private void handleLatest(TableView<Order> orderTable, List<Order> orders) {

    if (CollectionUtils.isEmpty(orders)) {
      return;
    }

    Date now = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    var index = Integer.parseInt(ApplicationVariable.getOrders().stream()
        .filter(order -> Utils.toDate(order.getDeliveryDate()).compareTo(now)
            >= BigInteger.ZERO.intValue())
        .min(Comparator.comparing(o -> Utils.toDate(o.getDeliveryDate())))
        .orElseThrow().getStt()) - BigInteger.ONE.intValue();

    ApplicationVariable.getOrders()
        .stream()
        .filter(order -> Integer.parseInt(order.getStt()) > index)
        .forEach(orders::add);
    AtomicInteger integer = new AtomicInteger(BigInteger.ZERO.intValue());

    orders = orders.stream().map(order -> {
      order.setStt(String.valueOf(integer.getAndIncrement()));
      return order;
    }).collect(Collectors.toList());

    ApplicationVariable.setOrders(orders);
    setDataOrderTable(orderTable);
  }

  private void handleOldData(TableView<Order> orderTable, List<Order> orders) {
    if (CollectionUtils.isEmpty(orders)) {
      isEnd.set(Boolean.TRUE);
      return;
    }

    var raise = ApplicationVariable.getOrders().size();
    var oldOrders = orders.stream().map(order -> {
      order.setStt(order.getStt() + raise);
      return order;
    }).collect(Collectors.toList());

    ApplicationVariable.add(oldOrders);
    setDataOrderTable(orderTable);
  }

  public void onScrollFinished(TableView<Order> orderTable) {
    var tvScrollBar = (ScrollBar) orderTable.lookup(".scroll-bar:vertical");
    tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() == BigInteger.ONE.doubleValue()) {
        loadPageAsync(Boolean.FALSE, orderTable);
      }

      if (newValue.doubleValue() == BigInteger.ZERO.doubleValue()) {
        Alert alert = confirmDialog();
        if (alert.getResult() == ButtonType.YES) {
          loadPageAsync(Boolean.TRUE, orderTable);
        }
      }
    });
  }

  protected void setDataOrderTable(TableView<Order> orderTable) {
    orderTable.setItems(FXCollections.observableArrayList(ApplicationVariable.getOrders()
        .stream()
        .sorted(Comparator.comparing(Order::getDeliveryDate)
            .thenComparing(Order::getDeliveryHour)
            .thenComparing(Order::getPriority))
        .collect(Collectors.toList())));
  }

  protected void printA5(String printerName, Order order) throws IOException {
    printerService.printA5Order(printerName, order);
  }

  @FXML
  public abstract void extractData() throws IOException;

  public abstract void initEvent();
}
