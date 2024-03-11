package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.dto.Order;
import com.bloemist.events.MessageWarning;
import com.bloemist.function.MethodParameter;
import com.bloemist.services.IOrderService;
import com.bloemist.services.IPrinterService;
import com.bloemist.constant.ApplicationVariable;
import com.bloemist.constant.Constants;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
        .min(Comparator.comparing(Order::getOrderDateTime))
        .orElse(null);
  }

  private LocalDateTime getPageStartTime(Boolean isNew, Order oldestOrder) {
    if (Objects.isNull(isNew)) {
      return LocalDateTime.now().minus(2, ChronoUnit.MONTHS);
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
      Function<Pair<LocalDateTime, LocalDateTime>, List<Order>> consumer, Button btnReload) {

    if (isLoadingPage.get()) {
      try {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("Tải lại đang trong quá trình xử lý ");
        alert.showAndWait();
      } catch (Exception e) {
      }
      return;
    }
    orderTable.setItems(FXCollections.emptyObservableList());

    isLoadingPage.set(Boolean.TRUE);

    Order oldestOrder = getOrderRecordInApp();
    LocalDateTime endTime = getPageEndTime(isNew, oldestOrder);
    LocalDateTime startTime = getPageStartTime(isNew, oldestOrder);

    CompletableFuture<List<Order>> orderLoading = CompletableFuture
        .supplyAsync(() -> {
          if (isEnd.get()) {
            return Collections.emptyList();
          }

          try {
            return consumer.apply(Pair.of(startTime, endTime));
          } catch (Exception ex) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Vui lòng kiểm tra đường truyền");
            alert.showAndWait();
            isLoadingPage.set(Boolean.FALSE);
            return Collections.emptyList();
          }

        });

    List<Order> orders = orderLoading.join();

    if(!ObjectUtils.isEmpty(orders)){
      if (Objects.isNull(isNew) ) {
        ApplicationVariable.setOrders(orders);
      } else{
        var existCode = ApplicationVariable.getOrders().stream().map(Order::getCode)
            .collect(Collectors.toSet());

        var ordersNotDuplicate = orders.stream()
            .filter(order -> !existCode.contains(order.getCode()))
            .toList();

        if(!ObjectUtils.isEmpty(ordersNotDuplicate)){
          ApplicationVariable.getOrders().addAll(ordersNotDuplicate);
          sortOrder();
        }
      }
    }

    orderTable.setItems(FXCollections.observableList(ApplicationVariable.getOrders()));
    orderTable.refresh();

    setCountDownEvent(() -> {
      if (Objects.nonNull(btnReload)) {
        btnReload.setDisable(Boolean.FALSE);
      }
      isLoadingPage.set(Boolean.FALSE);
    }, 3000);

  }

  private static void sortOrder() {

    AtomicInteger integer = new AtomicInteger(BigInteger.ONE.intValue());

    Collections.sort(ApplicationVariable.getOrders(),
        (o1, o2) -> {
          if(!o1.getStatus().equals(o2.getStatus())){
            return o1.getStatus().compareTo(o2.getStatus());
          }

          return o1.getDeliveryStartRange().compareTo(o2.getDeliveryStartRange()) * -1;
        });

    ApplicationVariable.getOrders().forEach(order ->
      order.setStt(String.valueOf(integer.getAndIncrement()))
    );
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
        loadPageAsync(Boolean.FALSE, orderTable, consumer, null);
      }

      if (newValue.doubleValue() == BigInteger.ZERO.doubleValue()) {
        loadPageAsync(Boolean.TRUE, orderTable, consumer, null);
      }

    });
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
