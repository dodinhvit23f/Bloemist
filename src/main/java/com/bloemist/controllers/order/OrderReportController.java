package com.bloemist.controllers.order;

import com.bloemist.dto.CustomerOrder;
import com.bloemist.dto.Order;
import com.bloemist.dto.OrderInfo;
import com.bloemist.events.MessageWarning;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.utils.Utils;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderReportController extends OrderController {

  @FXML
  private TableView<Order> orderTable;
  @FXML
  private TableColumn<Order, String> statusCol;
  @FXML
  private TableColumn<Order, String> deliveryDateCol;
  @FXML
  private TableColumn<Order, String> deliveryHourCol;
  @FXML
  private TableColumn<Order, String> orderCodeCol;
  @FXML
  private TableColumn<Order, String> customerNameCol;
  @FXML
  private TableColumn<Order, String> customerSocialLink;
  @FXML
  private TableColumn<Order, String> orderDescriptionCol;
  @FXML
  private TableColumn<Order, String> orderRemarkCol;
  @FXML
  private TextField customerName;
  @FXML
  private TextField customerPhone;
  @FXML
  private TextField receiverPhone;
  @FXML
  private TextField receiverName;
  @FXML
  private TextField deliveryHour;
  @FXML
  private TextField actualPrice;
  @FXML
  private TextField discountRate;
  @FXML
  private TextField totalAmount;
  @FXML
  private TextField outstandingBalance;
  @FXML
  private TextField deliveryFee;
  @FXML
  private TextField vatFee;
  @FXML
  private TextField depositAmount;
  @FXML
  private TextField empName;
  @FXML
  private TextArea orderDescription;
  @FXML
  private TextArea bannerContent;
  @FXML
  private TextArea deliveryAddress;
  @FXML
  private DatePicker deliveryDate;
  @FXML
  private DatePicker toDate;
  @FXML
  private DatePicker fromDate;


  private Order currentOrder;

  protected OrderReportController(ApplicationEventPublisher publisher) {
    super(publisher);
    currentOrder = new Order();
  }

  @FXML
  private void createOrder() {
    switchScene(ApplicationView.CREATE_ORDER);
  }

  @FXML
  private void updateOrder() {
    var customerName = this.customerName.getText().strip(); //NOSONAR
    var customerPhone = this.customerPhone.getText().strip(); //NOSONAR
    var deliveryAddress = this.deliveryAddress.getText().strip(); //NOSONAR
    var receiverName = this.receiverName.getText().strip(); //NOSONAR
    var receiverPhone = this.receiverPhone.getText().strip(); //NOSONAR
    var deliveryTime = this.deliveryHour.getText().strip(); //NOSONAR
    var orderDescription = this.orderDescription.getText().strip(); //NOSONAR
    var banner = this.bannerContent.getText().strip(); //NOSONAR
    var discount = this.discountRate.getText(); //NOSONAR
    var deliveryFee = Utils.currencyToNumber(this.deliveryFee.getText().strip()); //NOSONAR
    var vatFee = Utils.currencyToNumber(this.vatFee.getText().strip());//NOSONAR
    var depositAmount = Utils.currencyToNumber(this.depositAmount.getText().strip());//NOSONAR
    var truePrice = Utils.currencyToNumber(this.actualPrice.getText().strip());//NOSONAR
    var remainAmount = Utils.currencyToNumber(this.outstandingBalance.getText());//NOSONAR
    var totalAmount = Utils.currencyToNumber(this.outstandingBalance.getText());//NOSONAR

    if (validateOrderInfo(
        new OrderInfo(customerName, customerPhone, currentOrder.getCustomerSocialLink(),
            deliveryAddress,
            deliveryTime, truePrice, deliveryFee, vatFee,
            Utils.currencyToNumber(currentOrder.getSalePrice()), depositAmount, remainAmount,
            totalAmount, currentOrder.getImagePath()))) {
      return;
    }

    if (!Utils.isNumber(discount)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_008));
      return;
    }

    Date deliveryDateTime = getDeliveryDate(this.deliveryDate);
    if (Objects.isNull(deliveryDateTime)) {
      return;
    }

    if (ObjectUtils.isEmpty(receiverName)) {
      receiverName = customerName;
    }

    if (ObjectUtils.isEmpty(receiverPhone)) {
      receiverPhone = customerPhone;
    }

    var salePrice = getSalePrice(NumberUtils.parseNumber(truePrice, Double.class),
        NumberUtils.parseNumber(discount, Double.class));

    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      orderService.updateOrder(
          CustomerOrder.builder()
              .customerName(customerName)
              .customerPhone(customerPhone)
              .deliveryAddress(deliveryAddress)
              .receiverPhone(receiverPhone)
              .receiverName(receiverName)
              .orderDate(Date.from(Instant.now()))
              .receiveDate(deliveryDateTime)
              .orderDescription(orderDescription)
              .banner(banner)
              .discount(NumberUtils.parseNumber(discount, Integer.class))
              .truePrice(NumberUtils.parseNumber(truePrice, BigDecimal.class))
              .deliveryFee(NumberUtils.parseNumber(deliveryFee, BigDecimal.class))
              .vatFee(NumberUtils.parseNumber(vatFee, BigDecimal.class))
              .salePrice(BigDecimal.valueOf(salePrice))
              .depositAmount(NumberUtils.parseNumber(depositAmount, BigDecimal.class))
              .remainAmount(NumberUtils.parseNumber(remainAmount, BigDecimal.class))
              .totalBill(NumberUtils.parseNumber(totalAmount, BigDecimal.class))
              .code(currentOrder.getCode())
              .build());

      currentOrder.setCustomerName(customerName);
      currentOrder.setCustomerPhone(customerPhone);
      currentOrder.setDeliveryAddress(deliveryAddress);
      currentOrder.setReceiverName(receiverName);
      currentOrder.setReceiverPhone(receiverPhone);
      currentOrder.setOrderDescription(orderDescription);
      currentOrder.setBanner(banner);
      currentOrder.setDiscount(discount);
      currentOrder.setActualPrice(this.actualPrice.getText());
      currentOrder.setDeliveryFee(this.deliveryFee.getText());
      currentOrder.setVatFee(this.vatFee.getText());
      currentOrder.setDeposit(this.depositAmount.getText());
      currentOrder.setRemain(this.outstandingBalance.getText());
      currentOrder.setTotal(this.totalAmount.getText());
      orderTable.refresh();
    }
  }

  @FXML
  private void printOrder() {

  }

  @FXML
  public void extractData() throws IOException {
    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      File csvFile = new File(String.join(".",
          String.valueOf(System.currentTimeMillis()),
          "xls"));

      HSSFWorkbook workbook = new HSSFWorkbook();
      HSSFSheet sheet = workbook.createSheet("Hoá Đơn");

      HSSFRow rowhead = sheet.createRow(BigInteger.ZERO.shortValue());
      rowhead.createCell(0).setCellValue("Tình Trạng");
      rowhead.createCell(1).setCellValue("Mã Đơn");
      rowhead.createCell(2).setCellValue("Giờ Giao");
      rowhead.createCell(3).setCellValue("Ngày Giao");
      rowhead.createCell(4).setCellValue("Tên Khách Hàng");
      rowhead.createCell(5).setCellValue("Link FB");
      rowhead.createCell(6).setCellValue("Mô Tả Đơn");
      rowhead.createCell(7).setCellValue("Ghi Chú");
      rowhead.createCell(8).setCellValue("Số nợ");
      rowhead.createCell(9).setCellValue("Tổng Tiền");

      ApplicationVariable.getOrders().stream()
          .filter(order -> Utils.toDate(order.getOrderDate())
              .compareTo(
                  Date.from(
                      toDate.getValue()
                          .atStartOfDay()
                          .atZone(ZoneId.systemDefault()).toInstant()))
              >= BigInteger.ONE.intValue())
          .forEach(order -> {
            HSSFRow row = sheet.createRow(Integer.parseInt(order.getStt()));
            row.createCell(0).setCellValue(order.getStatus());
            row.createCell(1).setCellValue(order.getCode());
            row.createCell(2).setCellValue(order.getDeliveryHour());
            row.createCell(3).setCellValue(order.getDeliveryDate());
            row.createCell(4).setCellValue(order.getCustomerName());
            row.createCell(5).setCellValue(order.getCustomerSocialLink());
            row.createCell(6).setCellValue(order.getOrderDescription());
            row.createCell(7).setCellValue(order.getCustomerNote());
            row.createCell(8).setCellValue(order.getRemain());
            row.createCell(9).setCellValue(order.getTotal());
          });

      workbook.write(csvFile);
      workbook.close();

      Alert confirm = new Alert(AlertType.CONFIRMATION,
          String.join(" ",
              "File",
              csvFile.getName(),
              "đã lưu lại"),
          ButtonType.YES);
      confirm.show();
    }
  }

  @FXML
  private void changeStatus() {
    this.switchScene(ApplicationView.SUB_ORDER_SCREEN);
  }


  private void addTableViewListener() {
    orderTable.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldSelection, newSelection) -> {
          if (Objects.nonNull(newSelection)) {
            currentOrder = newSelection;
            setOrderData();
            ApplicationVariable.currentOrder = currentOrder;
          }
        });
  }

  private void setCellValueFactory() {
    statusCol.setCellValueFactory(new PropertyValueFactory<>(Order.STATUS));
    deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_DATE));
    customerNameCol.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NAME));
    customerSocialLink.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_SOCIAL_LINK));
    orderDescriptionCol.setCellValueFactory(new PropertyValueFactory<>(Order.ORDER_DESCRIPTION));
    orderRemarkCol.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NOTE));
    orderCodeCol.setCellValueFactory(new PropertyValueFactory<>(Order.CODE));
    deliveryHourCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_HOUR));
  }

  private void setOrderData() {
    this.customerName.setText(currentOrder.getCustomerName());
    this.customerPhone.setText(currentOrder.getCustomerPhone());
    this.receiverPhone.setText(currentOrder.getReceiverPhone());
    this.receiverName.setText(currentOrder.getReceiverName());
    this.deliveryHour.setText(currentOrder.getDeliveryHour());
    this.actualPrice.setText(currentOrder.getActualPrice());
    this.discountRate.setText(currentOrder.getDiscount());
    this.totalAmount.setText(currentOrder.getTotal());
    this.outstandingBalance.setText(currentOrder.getRemain());
    this.deliveryFee.setText(currentOrder.getDeliveryFee());
    this.vatFee.setText(currentOrder.getVatFee());
    this.depositAmount.setText(currentOrder.getDeposit());
    this.orderDescription.setText(currentOrder.getOrderDescription());
    this.bannerContent.setText(currentOrder.getBanner());
    this.deliveryAddress.setText(currentOrder.getDeliveryAddress());
    this.deliveryDate.setValue(
        LocalDate.ofInstant(Utils.toDate(currentOrder.getDeliveryDate()).toInstant(),
            ZoneId.systemDefault()));
  }

  @FXML
  private void seeImage() throws IOException {
    var imagePath = currentOrder.getImagePath();
    if (Objects.nonNull(imagePath) && new File(imagePath).exists()) {
      Desktop.getDesktop().open(new File(imagePath));
    }
  }


  @Override
  public void initEvent() {
    setCellValueFactory();
    addTableViewListener();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initEvent();
    this.stageManager.getStage().setOnShown(event ->
        onScrollFinished(this.orderTable));

    var localDate = LocalDate.now();
    fromDate.setValue(localDate);
    toDate.setValue(localDate.minusDays(SEVEN_DAYS));

    if (CollectionUtils.isEmpty(ApplicationVariable.getOrders())) {
      loadPageAsync(null, this.orderTable);
      return;
    }

    setData(this.orderTable);
    //TODO empName.setText(ApplicationVariable.getUser().getFullName());
  }
}
