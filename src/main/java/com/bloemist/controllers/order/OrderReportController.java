package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.bloemist.events.MessageWarning;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
  private TableColumn<Order, String> orderBanner;
  @FXML
  private TableColumn<Order, Boolean> choice;

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
  private TextArea orderNote;
  @FXML
  private TextArea deliveryAddress;
  @FXML
  private DatePicker deliveryDate;

  @FXML
  private Label orderCode;

  @FXML
  private Label orderDate;

  private Order currentOrder;

  protected OrderReportController(ApplicationEventPublisher publisher) {
    super(publisher);
    currentOrder = new Order();
  }

  @FXML
  private void createOrder() {
    switchScene(ApplicationView.CREATE_ORDER);
    CreateOrderController.setPopup(Boolean.FALSE);
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
    var banner = this.orderBanner.getText().strip(); //NOSONAR
    var discount = Utils.currencyToStringNumber(this.discountRate.getText().strip()); //NOSONAR
    var deliveryFee = Utils.currencyToStringNumber(this.deliveryFee.getText().strip()); //NOSONAR
    var vatFee = Utils.currencyToStringNumber(this.vatFee.getText().strip());//NOSONAR
    var depositAmount = Utils.currencyToStringNumber(this.depositAmount.getText().strip());//NOSONAR
    var truePrice = Utils.currencyToStringNumber(this.actualPrice.getText().strip());//NOSONAR
    var remainAmount = Utils.currencyToStringNumber(this.outstandingBalance.getText());//NOSONAR
    var totalAmount = Utils.currencyToStringNumber(this.totalAmount.getText());//NOSONAR
    var customerNote = this.orderNote.getText().strip();

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

    Double salePrice = getSalePrice(NumberUtils.parseNumber(truePrice, Double.class),
        NumberUtils.parseNumber(discount, Double.class));

    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      orderService.updateOrder(
          Order.builder()
              .customerName(customerName)
              .customerPhone(customerPhone)
              .deliveryAddress(deliveryAddress)
              .receiverPhone(receiverPhone)
              .receiverName(receiverName)
              .orderDate(currentOrder.getOrderDate())
              .deliveryDate(Utils.formatDate(deliveryDateTime))
              .orderDescription(orderDescription)
              .deliveryHour(deliveryTime)
              .customerNote(customerNote)
              .banner(banner)
              .customerSource(currentOrder.getCustomerSource())
              .customerSocialLink(currentOrder.getCustomerSocialLink())
              .discount(discount)
              .actualPrice(truePrice)
              .deliveryFee(deliveryFee)
              .vatFee(vatFee)
              .salePrice(salePrice.toString())
              .deposit(depositAmount)
              .remain(remainAmount)
              .total(totalAmount)
              .code(currentOrder.getCode())
              .imagePath(currentOrder.getImagePath())
              .status(currentOrder.getStatus())
              .build());

      currentOrder.setCustomerName(customerName);
      currentOrder.setCustomerPhone(customerPhone);
      currentOrder.setDeliveryAddress(deliveryAddress);
      currentOrder.setReceiverPhone(receiverPhone);
      currentOrder.setReceiverName(receiverName);
      currentOrder.setDeliveryDate(Utils.formatDate(deliveryDateTime));
      currentOrder.setOrderDescription(orderDescription);
      currentOrder.setDeliveryHour(deliveryTime);
      currentOrder.setCustomerNote(customerNote);
      currentOrder.setBanner(banner);
      currentOrder.setDiscount(discount);
      currentOrder.setActualPrice(truePrice);
      currentOrder.setDeliveryFee(deliveryFee);
      currentOrder.setVatFee(vatFee);
      currentOrder.setSalePrice(salePrice.toString());
      currentOrder.setDeposit(depositAmount);
      currentOrder.setRemain(remainAmount);
      currentOrder.setTotal(totalAmount);

      orderService.updateOrder(currentOrder);
    }
    orderTable.refresh();
  }

  @FXML
  private void printOrder() {
    if (isCurrentOrderEmpty()) {
      return;
    }
    switchScene(ApplicationView.PRINT_ORDER, ApplicationView.INQUIRY_ORDER);
  }

  @FXML
  public void seeImage() {
    try {
      viewImage(currentOrder);
    } catch (IOException e) {

    }
  }

  @FXML
  public void calculateTotalPrice() {
    if (validOrderPrice()) {
      return;
    }

    var discount = NumberUtils
        .parseNumber(Utils.currencyToStringNumber(this.discountRate.getText()), Double.class);
    var truePrice = NumberUtils
        .parseNumber(Utils.currencyToStringNumber(this.actualPrice.getText()), Double.class);
    var deliveryFeeAmount = NumberUtils
        .parseNumber(Utils.currencyToStringNumber(this.deliveryFee.getText()), Double.class);
    var vatFeeAmount = NumberUtils
        .parseNumber(Utils.currencyToStringNumber(this.vatFee.getText()), Double.class);
    var deposit = NumberUtils
        .parseNumber(Utils.currencyToStringNumber(this.depositAmount.getText()), Double.class);

    var salePrice = getSalePrice(truePrice, discount);
    var totalSaleAmount = getTotalPrice(salePrice, deliveryFeeAmount, vatFeeAmount);

    this.totalAmount.setText(Utils.currencyFormat(totalSaleAmount));
    this.outstandingBalance.setText(Utils.currencyFormat(totalSaleAmount - deposit));
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
            ApplicationVariable.setCurrentOrder(currentOrder);
          }
        });
  }

  @FXML
  private void reload() {
    this.orderTable.setItems(FXCollections.observableArrayList());
    loadPageAsync(null, this.orderTable,
        pair -> orderService.getStaffPage(pair.getFirst(), pair.getSecond()));
  }

  private void setCellValueFactory() {
    statusCol.setCellValueFactory(new PropertyValueFactory<>(Order.STATUS));
    deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_DATE));
    customerNameCol.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NAME));
    customerSocialLink.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_SOCIAL_LINK));
    orderDescriptionCol.setCellValueFactory(new PropertyValueFactory<>(Order.ORDER_DESCRIPTION));
    orderRemarkCol.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NOTE));
    orderBanner.setCellValueFactory(new PropertyValueFactory<>(Order.BANNER));
    orderCodeCol.setCellValueFactory(new PropertyValueFactory<>(Order.CODE));
    deliveryHourCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_HOUR));
    choice.setCellFactory(CheckBoxTableCell.forTableColumn(choice));
    choice.setEditable(Boolean.TRUE);
    choice.setCellValueFactory(param -> {
      Order order = param.getValue();
      final SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(order.getIsSelected());
      booleanProp.addListener((observable, oldValue, newValue) -> order.setIsSelected(newValue));
      return booleanProp;
    });
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
    this.deliveryAddress.setText(currentOrder.getDeliveryAddress());
    this.orderNote.setText(currentOrder.getCustomerNote());
    this.deliveryDate.setValue(
        LocalDate.ofInstant(Utils.toDate(currentOrder.getDeliveryDate()).toInstant(),
            ZoneId.systemDefault()));
    this.orderCode.setText(currentOrder.getCode());
    this.orderDate.setText(currentOrder.getOrderDate());

  }

  @Override
  public void initEvent() {
    setCellValueFactory();
    addTableViewListener();
    addEventLostFocus(this.actualPrice, this::calculateTotalPrice);
    addEventLostFocus(this.deliveryFee, this::calculateTotalPrice);
    addEventLostFocus(this.vatFee, this::calculateTotalPrice);
    addEventLostFocus(this.depositAmount, this::calculateTotalPrice);
    addEventLostFocus(this.discountRate, this::calculateTotalPrice);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initEvent();

    ApplicationVariable.getOrders().clear();

    loadPageAsync(null, this.orderTable,
        pair -> orderService.getStaffPage(pair.getFirst(), pair.getSecond()));

    this.stageManager.getStage().setOnShown(event ->
        onScrollFinished(this.orderTable,
            pair -> orderService.getStaffPage(pair.getFirst(), pair.getSecond())));

    empName.setText(Objects.isNull(ApplicationVariable.getUser()) ?
                    "" : ApplicationVariable.getUser().getFullName());
  }

  @Override
  public void extractData() throws IOException {
    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      File csvFile = new File(String.join(".",
          String.valueOf(System.currentTimeMillis()),
          "xls"));

      final int statusCell = 0;
      final int orderCodeCell = 1;
      final int deliveryTimeCell = 2;
      final int deliveryDateCell = 3;
      final int customerNameCell = 4;
      final int socialLinkCell = 5;
      final int orderDesCell = 6;
      final int orderNoteCell = 7;
      final int remainCell = 8;
      final int totalCell = 9;

      try (final HSSFWorkbook workbook = new HSSFWorkbook()) {
        HSSFSheet sheet = workbook.createSheet("Hoá Đơn");

        HSSFRow rowhead = sheet.createRow(BigInteger.ZERO.shortValue());

        rowhead.createCell(statusCell).setCellValue("Tình Trạng");
        rowhead.createCell(orderCodeCell).setCellValue("Mã Đơn");
        rowhead.createCell(deliveryTimeCell).setCellValue("Giờ Giao");
        rowhead.createCell(deliveryDateCell).setCellValue("Ngày Giao");
        rowhead.createCell(customerNameCell).setCellValue("Tên Khách Hàng");
        rowhead.createCell(socialLinkCell).setCellValue("Link FB");
        rowhead.createCell(orderDesCell).setCellValue("Mô Tả Đơn");
        rowhead.createCell(orderNoteCell).setCellValue("Ghi Chú");
        rowhead.createCell(remainCell).setCellValue("Số nợ");
        rowhead.createCell(totalCell).setCellValue("Tổng Tiền");

        ApplicationVariable.getOrders()
            .forEach(order -> {
              HSSFRow row = sheet.createRow(Integer.parseInt(order.getStt()));
              row.createCell(statusCell).setCellValue(order.getStatus());
              row.createCell(orderCodeCell).setCellValue(order.getCode());
              row.createCell(deliveryTimeCell).setCellValue(order.getDeliveryHour());
              row.createCell(deliveryDateCell).setCellValue(order.getDeliveryDate());
              row.createCell(customerNameCell).setCellValue(order.getCustomerName());
              row.createCell(socialLinkCell).setCellValue(order.getCustomerSocialLink());
              row.createCell(orderDesCell).setCellValue(order.getOrderDescription());
              row.createCell(orderNoteCell).setCellValue(order.getCustomerNote());
              row.createCell(remainCell).setCellValue(order.getRemain());
              row.createCell(totalCell).setCellValue(order.getTotal());
            });
        workbook.write(csvFile);

        Alert confirm = new Alert(AlertType.CONFIRMATION,
            String.join(" ",
                "File",
                csvFile.getName(),
                "đã lưu lại"),
            ButtonType.YES);
        confirm.show();
      } catch (Exception e) {
        Alert confirm = new Alert(AlertType.ERROR, "Xuất file xảy ra lỗi", ButtonType.OK);
        confirm.show();
      }
    }
  }

  @Override
  public void cancel() {
    switchScene(ApplicationView.HOME);
  }

  private boolean validOrderPrice() {
    if (!Utils.isNumber(Utils.currencyToStringNumber(this.actualPrice.getText()).strip())
        || !Utils.isNumber(Utils.currencyToStringNumber(this.deliveryFee.getText().strip()))
        || !Utils.isNumber(Utils.currencyToStringNumber(this.vatFee.getText().strip()))
        || !Utils.isNumber(Utils.currencyToStringNumber(this.depositAmount.getText().strip()))) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

}
