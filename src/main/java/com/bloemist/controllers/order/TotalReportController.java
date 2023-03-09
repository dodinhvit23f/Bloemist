package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.constant.ApplicationVariable;
import com.constant.OrderState;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class TotalReportController extends OrderController {

  TotalReportController(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  @Override
  public void initEvent() {
    setCellValueFactory();
  }

  @FXML
  TextArea textArea;

  @FXML
  private TableView<Order> orderTable;

  @FXML
  private TableColumn<Order, String> orderCodeCol;
  @FXML
  private TableColumn<Order, String> deliveryHourCol;
  @FXML
  private TableColumn<Order, String> deliveryDateCol;
  @FXML
  private TableColumn<Order, String> orderRemarkCol;
  @FXML
  private TableColumn<Order, String> statusCol;
  @FXML
  private TableColumn<Order, String> orderDescriptionCol;
  @FXML
  private TableColumn<Order, String> customerName;
  @FXML
  private TableColumn<Order, String> customerSocialLink;
  @FXML
  private TableColumn<Order, String> salePrice;
  @FXML
  private TableColumn<Order, String> remainAmount;
  @FXML
  private TableColumn<Order, String> discount;
  @FXML
  private TableColumn<Order, String> bannerContent;
  @FXML
  private TableColumn<Order, String> truePrice;
  @FXML
  private TableColumn<Order, String> customerPhone;
  @FXML
  private TableColumn<Order, String> deliveryFee;
  @FXML
  private TableColumn<Order, String> orderStt;
  @FXML
  private TableColumn<Order, String> receiver;
  @FXML
  private TableColumn<Order, String> deliveryAddress;
  @FXML
  private TableColumn<Order, String> vatFee;
  @FXML
  private TableColumn<Order, String> receiverPhone;
  @FXML
  private TableColumn<Order, String> deposit;
  @FXML
  private TableColumn<Order, String> customerSource;
  @FXML
  private TableColumn<Order, String> orderDate;
  @FXML
  private TableColumn<Order, Boolean> checkAll;

  private TableColumn<Order, String> editableColumn;
  private Order currentOrder;
  private int orderRow;

  private void setCellValueFactory() {
    setColumnsValues();
    setColumnsFactory();
    setColumnsEditStart();
    setAfterEditEvent();
  }

  @Override
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
      rowhead.createCell(2).setCellValue("Mô tả đơn");
      rowhead.createCell(3).setCellValue("Nội dung banner");
      rowhead.createCell(4).setCellValue("Người nhận");
      rowhead.createCell(5).setCellValue("SĐT người nhận");
      rowhead.createCell(6).setCellValue("Địa chỉ giao");
      rowhead.createCell(7).setCellValue("Ngày nhận");
      rowhead.createCell(8).setCellValue("Ghi chú");
      rowhead.createCell(9).setCellValue("Tên Người đặt");
      rowhead.createCell(10).setCellValue("SĐT người đặt");
      rowhead.createCell(11).setCellValue("Link mạng xã hội");
      rowhead.createCell(12).setCellValue("Nguồn khách");
      rowhead.createCell(13).setCellValue("Ngày Đặt");
      rowhead.createCell(14).setCellValue("Giá niêm yết");
      rowhead.createCell(15).setCellValue("Chiết khấu");
      rowhead.createCell(16).setCellValue("Giá bán");
      rowhead.createCell(17).setCellValue("Phí giao khách trả");
      rowhead.createCell(18).setCellValue("Phí viết VAT khách trả");
      rowhead.createCell(19).setCellValue("Đặt cọc");
      rowhead.createCell(20).setCellValue("Còn phải trả");
      rowhead.createCell(21).setCellValue("Chi phí nguyên liệu");
      rowhead.createCell(22).setCellValue("Phí giao hoa thực tế");
      rowhead.createCell(23).setCellValue("Phí viết VAT thực tế");

      ApplicationVariable.getOrders()
          .forEach(order -> {
            HSSFRow row = sheet.createRow(Integer.parseInt(order.getStt()));
            row.createCell(0).setCellValue(order.getStatus());
            row.createCell(1).setCellValue(order.getCode());
            row.createCell(2).setCellValue(order.getOrderDescription());
            row.createCell(3).setCellValue(order.getBanner());
            row.createCell(4).setCellValue(order.getReceiverName());
            row.createCell(5).setCellValue(order.getReceiverPhone());
            row.createCell(6).setCellValue(order.getDeliveryAddress());
            row.createCell(7).setCellValue(order.getDeliveryDate());
            row.createCell(8).setCellValue(order.getCustomerNote());
            row.createCell(9).setCellValue(order.getCustomerName());
            row.createCell(10).setCellValue(order.getCustomerPhone());
            row.createCell(11).setCellValue(order.getCustomerSocialLink());
            row.createCell(12).setCellValue(order.getCustomerSource());
            row.createCell(13).setCellValue(order.getOrderDate());
            row.createCell(14).setCellValue(order.getActualPrice());
            row.createCell(15).setCellValue(order.getDiscount());
            row.createCell(16).setCellValue(order.getSalePrice());
            row.createCell(17).setCellValue(order.getDeliveryFee());
            row.createCell(18).setCellValue(order.getVatFee());
            row.createCell(19).setCellValue(order.getVatFee());
            row.createCell(20).setCellValue(order.getDeposit());
            row.createCell(21).setCellValue("0");
            row.createCell(22).setCellValue(order.getActualDeliveryFee());
            row.createCell(23).setCellValue(order.getActualVatFee());

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

  private void setColumnsValues() {
    statusCol.setCellValueFactory(new PropertyValueFactory<>(Order.STATUS));
    customerName.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NAME));
    deliveryDateCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_DATE));
    orderDescriptionCol.setCellValueFactory(new PropertyValueFactory<>(Order.ORDER_DESCRIPTION));
    orderRemarkCol.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_NOTE));
    orderCodeCol.setCellValueFactory(new PropertyValueFactory<>(Order.CODE));
    deliveryHourCol.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_HOUR));
    customerSocialLink.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_SOCIAL_LINK));

    salePrice.setCellValueFactory(new PropertyValueFactory<>(Order.SALE_PRICE));
    remainAmount.setCellValueFactory(new PropertyValueFactory<>(Order.REMAIN));
    discount.setCellValueFactory(new PropertyValueFactory<>(Order.DISCOUNT));
    bannerContent.setCellValueFactory(new PropertyValueFactory<>(Order.BANNER));
    truePrice.setCellValueFactory(new PropertyValueFactory<>(Order.ACTUAL_PRICE));
    customerPhone.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_PHONE));
    deliveryFee.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_FEE));
    orderStt.setCellValueFactory(new PropertyValueFactory<>(Order.STT));
    receiver.setCellValueFactory(new PropertyValueFactory<>(Order.RECEIVER_NAME));
    deliveryAddress.setCellValueFactory(new PropertyValueFactory<>(Order.DELIVERY_ADDRESS));
    vatFee.setCellValueFactory(new PropertyValueFactory<>(Order.VAT_FEE));
    receiverPhone.setCellValueFactory(new PropertyValueFactory<>(Order.RECEIVER_PHONE));
    deposit.setCellValueFactory(new PropertyValueFactory<>(Order.DEPOSIT));
    customerSource.setCellValueFactory(new PropertyValueFactory<>(Order.CUSTOMER_SOURCE));
    orderDate.setCellValueFactory(new PropertyValueFactory<>(Order.ORDER_DATE));
  }

  private void setColumnsFactory() {
    setStatusColumn();
    deliveryHourCol.setCellFactory(TextFieldTableCell.forTableColumn());
    deliveryDateCol.setCellFactory(TextFieldTableCell.forTableColumn());
    orderRemarkCol.setCellFactory(TextFieldTableCell.forTableColumn());

    orderDescriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
    customerName.setCellFactory(TextFieldTableCell.forTableColumn());
    customerSocialLink.setCellFactory(TextFieldTableCell.forTableColumn());
    salePrice.setCellFactory(TextFieldTableCell.forTableColumn());
    remainAmount.setCellFactory(TextFieldTableCell.forTableColumn());
    discount.setCellFactory(TextFieldTableCell.forTableColumn());

    truePrice.setCellFactory(TextFieldTableCell.forTableColumn());
    customerPhone.setCellFactory(TextFieldTableCell.forTableColumn());
    deliveryFee.setCellFactory(TextFieldTableCell.forTableColumn());
    orderStt.setCellFactory(TextFieldTableCell.forTableColumn());
    receiver.setCellFactory(TextFieldTableCell.forTableColumn());
    deliveryAddress.setCellFactory(TextFieldTableCell.forTableColumn());
    vatFee.setCellFactory(TextFieldTableCell.forTableColumn());
    receiverPhone.setCellFactory(TextFieldTableCell.forTableColumn());
    deposit.setCellFactory(TextFieldTableCell.forTableColumn());
    customerSource.setCellFactory(TextFieldTableCell.forTableColumn());
    orderDate.setCellFactory(TextFieldTableCell.forTableColumn());
    bannerContent.setCellFactory(TextFieldTableCell.forTableColumn());
    orderCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    checkAll.setCellFactory(CheckBoxTableCell.forTableColumn(checkAll));
  }

  private void setStatusColumn() {
    statusCol.setCellFactory(tc -> {
          ComboBox<String> combo = new ComboBox<>();
          combo.getItems().addAll(FXCollections.observableArrayList(
              OrderState.DONE.getStateText(),
              OrderState.DONE_DELIVERY.getStateText(),
              OrderState.DONE_PROCESS.getStateText(),
              OrderState.CANCEL.getStateText(),
              OrderState.IN_DELIVERY.getStateText(),
              OrderState.IN_DEBIT.getStateText(),
              OrderState.IN_PROCESS.getStateText()));

          return new TableCell<>() {
            @Override
            protected void updateItem(String reason, boolean empty) {
              super.updateItem(reason, empty);
              if (empty) {
                setGraphic(null);
              } else {
                combo.setValue(reason);
                setGraphic(combo);
              }
            }
          };
        }
    );
  }

  private void setColumnsEditStart() {
    setEditEventTableCell(deliveryDateCol);
    setEditEventTableCell(orderDescriptionCol);
    setEditEventTableCell(orderRemarkCol);
    setEditEventTableCell(deliveryHourCol);
    setEditEventTableCell(customerSocialLink);
    setEditEventTableCell(salePrice);
    setEditEventTableCell(remainAmount);
    setEditEventTableCell(discount);
    setEditEventTableCell(bannerContent);
    setEditEventTableCell(truePrice);
    setEditEventTableCell(customerPhone);
    setEditEventTableCell(deliveryFee);
    setEditEventTableCell(orderStt);
    setEditEventTableCell(receiver);
    setEditEventTableCell(deliveryAddress);
    setEditEventTableCell(vatFee);
    setEditEventTableCell(receiverPhone);
    setEditEventTableCell(deposit);
    setEditEventTableCell(customerSource);
    setEditEventTableCell(orderDate);
    setEditEventTableCell(customerName);
  }

  private void setEditEventTableCell(TableColumn<Order, String> tableColumn) {
    tableColumn.setOnEditStart(event -> {
      var cellEditStartEvent = ((CellEditEvent<Order, String>) event);
      textArea.setText(cellEditStartEvent.getOldValue());
      orderRow = cellEditStartEvent.getTablePosition().getRow();
      currentOrder = cellEditStartEvent.getTableView().getItems().get(orderRow);
      editableColumn = tableColumn;
    });

    tableColumn.setOnEditCommit(event -> {
      var cellEditCommitEvent = ((CellEditEvent<Order, String>) event);
      if (Objects.nonNull(cellEditCommitEvent.getNewValue())) {
        textArea.setText(cellEditCommitEvent.getNewValue());
        setValueToColumn(editableColumn, currentOrder, cellEditCommitEvent.getNewValue());
      }
    });


  }

  private void setAfterEditEvent() {
    textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (Boolean.FALSE.equals(newValue)) {
        setValueToColumn(editableColumn, currentOrder, textArea.getText());
        orderTable.refresh();
      }
    });
  }

  private void setValueToColumn(TableColumn<?, ?> tableColumn, Order order, String value) {
    if (deliveryHourCol.equals(tableColumn)) {
      order.setDeliveryHour(value);
      return;
    }
    if (deliveryDateCol.equals(tableColumn)) {
      order.setDeliveryDate(value);
      return;
    }
    if (orderRemarkCol.equals(tableColumn)) {
      order.setCustomerNote(value);
      return;
    }
    if (statusCol.equals(tableColumn)) {
      order.setStatus(value);
      return;
    }
    if (orderDescriptionCol.equals(tableColumn)) {
      order.setOrderDescription(value);
      return;
    }
    if (customerName.equals(tableColumn)) {
      order.setCustomerName(value);
      return;
    }
    if (customerSocialLink.equals(tableColumn)) {
      order.setCustomerSocialLink(value);
      return;
    }
    if (salePrice.equals(tableColumn)) {
      order.setSalePrice(value);
      return;
    }
    if (remainAmount.equals(tableColumn)) {
      order.setRemain(value);
      return;
    }
    if (discount.equals(tableColumn)) {
      order.setDiscount(value);
      return;
    }
    if (bannerContent.equals(tableColumn)) {
      order.setBanner(value);
      return;
    }
    if (truePrice.equals(tableColumn)) {
      order.setActualPrice(value);
      return;
    }
    if (customerPhone.equals(tableColumn)) {
      order.setCustomerPhone(value);
      return;
    }
    if (deliveryFee.equals(tableColumn)) {
      order.setDeliveryFee(value);
      return;
    }
    if (receiver.equals(tableColumn)) {
      order.setReceiverName(value);
      return;
    }
    if (deliveryAddress.equals(tableColumn)) {
      order.setDeliveryAddress(value);
      return;
    }
    if (vatFee.equals(tableColumn)) {
      order.setVatFee(value);
      return;
    }
    if (receiverPhone.equals(tableColumn)) {
      order.setReceiverPhone(value);
      return;
    }
    if (deposit.equals(tableColumn)) {
      order.setDeposit(value);
      return;
    }
    if (customerSource.equals(tableColumn)) {
      order.setCustomerSource(value);
      return;
    }
    if (orderDate.equals(tableColumn)) {
      order.setOrderDate(value);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initEvent();
    this.stageManager.getStage().setOnShown(event ->
        onScrollFinished(this.orderTable));
    if (CollectionUtils.isEmpty(ApplicationVariable.getOrders())) {
      loadPageAsync(null, this.orderTable);
      return;
    }
    setData(this.orderTable);
    //TODO empName.setText(ApplicationVariable.getUser().getFullName());
  }
}
