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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class OrderController extends BaseController {

  public static final AtomicBoolean isEnd = new AtomicBoolean(Boolean.FALSE);
  public static final String DD_MM_YYYY = "dd-MM-yyyy";
  @Autowired
  IOrderService orderService;
  @Autowired
  IPrinterService printerService;

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

    return LocalDate.parse(oldestOrder.getOrderDate(),
            DateTimeFormatter.ofPattern(DD_MM_YYYY))
        .minusMonths(1)
        .atStartOfDay();
  }


  protected void loadPageAsync(Boolean isNew, TableView<Order> orderTable,
      Function<Pair<LocalDateTime, LocalDateTime>, List<Order>> consumer) {
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

    orderLoading.thenAccept(orders -> {
      if (ObjectUtils.isEmpty(orders)) {
        isEnd.set(Boolean.TRUE);
      }

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
    }).toList();

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
    }).toList();

    ApplicationVariable.add(oldOrders);
    setDataOrderTable(orderTable);
  }

  public void onScrollFinished(TableView<Order> orderTable,
      Function<Pair<LocalDateTime, LocalDateTime>, List<Order>> consumer) {
    var tvScrollBar = (ScrollBar) orderTable.lookup(".scroll-bar:vertical");
    tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
      tvScrollBar.setDisable(Boolean.TRUE);
      if (newValue.doubleValue() == BigInteger.ONE.doubleValue()) {
        loadPageAsync(Boolean.FALSE, orderTable, consumer);
      }

      if (newValue.doubleValue() == BigInteger.ZERO.doubleValue()) {
        Alert alert = confirmDialog();
        if (alert.getResult() == ButtonType.YES) {
          loadPageAsync(Boolean.TRUE, orderTable, consumer);
        }
      }
      tvScrollBar.setDisable(Boolean.FALSE);
    });
  }

  protected void setDataOrderTable(TableView<Order> orderTable) {
    orderTable.setItems(FXCollections.observableArrayList(ApplicationVariable.getOrders()));
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
