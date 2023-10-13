package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.dto.Order;
import com.bloemist.events.MessageWarning;
import com.bloemist.funcation.MethodParameter;
import com.bloemist.services.IOrderService;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.constant.Constants;
import com.utils.Utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.util.ObjectUtils;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class OrderController extends BaseController {

  public static final AtomicBoolean isEnd = new AtomicBoolean(Boolean.FALSE);
  public static final String DD_MM_YYYY = "dd-MM-yyyy";
  @Autowired
  IOrderService orderService;
  @Autowired
  IPrinterService printerService;

  AtomicBoolean isLoadingPage = new AtomicBoolean(Boolean.FALSE);

  protected OrderController(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  public Double getSalePrice(Double truePrice, Double discount) {
    return truePrice - discount;
  }

  public Double getTotalPrice(Double salePriceValue, Double deliveryFee, Double vatFee) {
    return salePriceValue + deliveryFee + vatFee;
  }

  protected void addEventLostFocus(TextField textField, MethodParameter consumer) {
    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (Boolean.FALSE.equals(newValue)) {
        consumer.apply();
      }
    });
  }

  protected boolean validateOrderInfo(Order orderInfo) {
    return orderService.validOrder(orderInfo);
  }

  protected Date getDeliveryDate(DatePicker datePicker) {
    return Date.from(datePicker.getValue()
        .atTime(BigInteger.ZERO.intValue(), BigInteger.ZERO.intValue())
        .atZone(ZoneId.systemDefault()).toInstant());
  }

  private LocalDateTime getPageEndTime(Boolean isNew, Order oldestOrder) {

    if (Boolean.TRUE.equals(isNew) || Objects.isNull(isNew)) {
      return LocalDateTime.now().plus(1, ChronoUnit.MONTHS);
    }
    return LocalDate.parse(oldestOrder.getOrderDate(),
            DateTimeFormatter.ofPattern(DD_MM_YYYY))
        .atStartOfDay();
  }

  private Order getOrderRecordInApp() {
    return ApplicationVariable.getOrders()
        .stream()
        .min(Comparator.comparing(Order::getOrderDate))
        .orElse(null);
  }

  private LocalDateTime getPageStartTime(Boolean isNew, Order oldestOrder) {
    if (Objects.isNull(isNew)) {
      return LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
    }

    if (Boolean.TRUE.equals(isNew)) {
      return LocalDateTime.now().toLocalDate().atStartOfDay();
    }

    var previousMonth = LocalDate.parse(oldestOrder.getOrderDate(),
            DateTimeFormatter.ofPattern(DD_MM_YYYY))
        .minusMonths(2);

    return previousMonth
        .withMonth(previousMonth.getMonth().getValue())
        .atStartOfDay();
  }


  protected void loadPageAsync(Boolean isNew, TableView<Order> orderTable,
      Function<Pair<LocalDateTime, LocalDateTime>, List<Order>> consumer) {

    if (isLoadingPage.get()) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setContentText("Tải lại đang trong quá trình xử lý ");
      alert.showAndWait();
      return;
    }

    isLoadingPage.set(Boolean.TRUE);

    Order oldestOrder = getOrderRecordInApp();
    LocalDateTime endTime = getPageEndTime(isNew, oldestOrder);
    LocalDateTime startTime = getPageStartTime(isNew, oldestOrder);

    CompletableFuture<List<Order>> orderLoading = CompletableFuture
        .supplyAsync(() -> {
          if (isEnd.get()) {
            return Collections.emptyList();
          }

          return consumer.apply(Pair.of(startTime, endTime));
        });

    try {
      List<Order> orders = orderLoading.get(3, TimeUnit.SECONDS);
      if (Objects.isNull(isNew)) {
        ApplicationVariable.setOrders(orders);
        setDataOrderTable(orderTable);
      } else if (Boolean.TRUE.equals(isNew)) {
        handleLatest(orderTable, orders);
      } else {
        handleOldData(orders, orderTable);
      }

    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }

    setCountDownEvent(() -> {
      isLoadingPage.set(Boolean.FALSE);
      orderTable.refresh();
    }, 2000);

  }

  private void handleOldData(List<Order> orders, TableView<Order> orderTable) {
    if (ObjectUtils.isEmpty(orders)) {
      isEnd.set(Boolean.TRUE);
      isLoadingPage.set(Boolean.FALSE);
      return;
    }

    var existCode = ApplicationVariable.getOrders().stream().map(Order::getCode)
        .collect(Collectors.toSet());

    var ordersNotDuplicate = orders.stream()
        .filter(order -> !existCode.contains(order.getCode()))
        .toList();

    if (ObjectUtils.isEmpty(ordersNotDuplicate)) {
      isEnd.set(Boolean.TRUE);
      isLoadingPage.set(Boolean.FALSE);
      return;
    }

    ApplicationVariable.getOrders().addAll(ordersNotDuplicate);

    updateApplicationData(orderTable);
    reprintOrderStt();
    setDataOrderTable(orderTable);
  }

  private void handleLatest(TableView<Order> orderTable, List<Order> orders) {
    if (ObjectUtils.isEmpty(orders)) {
      return;
    }
    orders = new ArrayList<>(orders);

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
    }).toList();

    ApplicationVariable.setOrders(orders);
  }

  protected void setCountDownEvent(Runnable runnable, int delayMilliseconds) {
    var task = new TimerTask() {
      @Override
      public void run() {
        runnable.run();
      }
    };
    new Timer().schedule(task, delayMilliseconds);
  }

  protected void reprintOrderStt() {
    for (int i = 0; i < ApplicationVariable.getOrders().size(); i = i + 1) {
      ApplicationVariable.getOrders().get(i).setStt(String.valueOf(i + 1));
    }
  }

  public void onScrollFinished(TableView<Order> orderTable,
      Function<Pair<LocalDateTime, LocalDateTime>, List<Order>> consumer) {
    var tvScrollBar = (ScrollBar) orderTable.lookup(".scroll-bar:vertical");
    tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() == BigInteger.ONE.doubleValue()) {
        loadPageAsync(Boolean.FALSE, orderTable, consumer);
      }

      if (newValue.doubleValue() == BigInteger.ZERO.doubleValue()) {
        loadPageAsync(Boolean.TRUE, orderTable, consumer);
      }

    });
  }

  private void setDataOrderTable(TableView<Order> orderTable) {
    orderTable.setItems(FXCollections.observableList(Collections.emptyList()));
    orderTable.setItems(FXCollections.observableList(ApplicationVariable.getOrders()));
  }

  void updateApplicationData(TableView<Order> orderTable) {
    List newOrders = ApplicationVariable.getOrders()
        .stream()
        .sorted(Comparator.comparing(Order::getStatus)
            .thenComparing((o1, o2) -> {
              var date1 = Utils.toDate(o1.getDeliveryDate());
              var date2 = Utils.toDate(o2.getDeliveryDate());

              if (date2.after(date1)) {
                return 1;
              }

              if (date2.before(date1)) {
                return -1;
              }

              return 0;
            })
            .thenComparing(Order::getDeliveryHour))
        .toList();
    ApplicationVariable.setOrders(newOrders);
  }

  @FXML
  public abstract void extractData() throws IOException;

  protected boolean isCurrentOrderEmpty() {
    if (ApplicationVariable.getOrders().stream().filter(Order::getIsSelected).findAny().isEmpty()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_STATUS));
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public void viewImage(Order currentOrder) throws IOException {
    if (Objects.isNull(currentOrder)) {
      return;
    }

    var imagePath = currentOrder.getImagePath();
    if (Objects.nonNull(imagePath)) {
      if (imagePath.contains("http")) {
        Desktop.getDesktop().browse(URI.create(imagePath));
        return;
      }
      Desktop.getDesktop().open(new File(imagePath));
    }
  }

  public abstract void initEvent();
}
