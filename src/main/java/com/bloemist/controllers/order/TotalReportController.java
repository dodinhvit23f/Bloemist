package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.bloemist.dto.OrderInfo;
import com.constant.ApplicationVariable;
import com.constant.OrderState;
import com.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
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
import org.springframework.util.ObjectUtils;

@Component
public class TotalReportController extends OrderController {

  public static final String ZERO = "0";

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
  private TableColumn<Order, String> categoryFee;
  @FXML
  private TableColumn<Order, String> actualDeliveryFee;
  @FXML
  private TableColumn<Order, String> actualVatFee;
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

  @FXML
  public void updateInfo() {

  }

  @FXML
  public void saveSelectedOrders() {
    List<Order> selectedOrder = orderTable.getItems()
        .filtered(order -> Objects.equals(order.getIsSelected(), Boolean.TRUE))
        .stream()
        .map(order -> {
          order.setTotal(getTotalPrice(Double.valueOf(Utils.currencyToNumber(order.getSalePrice())),
              Double.valueOf(Utils.currencyToNumber(order.getDeliveryFee())),
              Double.valueOf(Utils.currencyToNumber(order.getVatFee()))).toString());
          order.setRemain(String.valueOf(Double.parseDouble(order.getTotal()) - Double.parseDouble(
              Utils.currencyToNumber(order.getDeposit()))));

          order.setActualPrice(Utils.currencyToNumber(order.getActualPrice()));
          order.setActualVatFee(Utils.currencyToNumber(order.getActualVatFee()));
          order.setActualDeliveryFee(Utils.currencyToNumber(order.getActualDeliveryFee()));
          order.setSalePrice(Utils.currencyToNumber(order.getSalePrice()));
          order.setDeliveryFee(Utils.currencyToNumber(order.getDeliveryFee()));
          order.setDeposit(Utils.currencyToNumber(order.getDeposit()));
          order.setDiscount(Utils.currencyToNumber(order.getDiscount()));
          order.setVatFee(Utils.currencyToNumber(order.getVatFee()));
          order.setMaterialsFee(Utils.currencyToNumber(order.getMaterialsFee()));
          //order.setCustomerSource(y);

          return order;
        }).collect(Collectors.toList());

    var failOrder = selectedOrder.stream()
        .filter(order -> {
          if (validateOrderInfo(OrderInfo.builder()
              .customerName(order.getCustomerName())
              .customerPhone(order.getCustomerPhone())
              .customerSocialLink(order.getCustomerSocialLink())
              .deliveryAddress(order.getDeliveryAddress())
              .deliveryTime(order.getDeliveryHour())
              .deliveryFee(Utils.currencyToNumber(order.getDeliveryFee()))
              .vatFee(order.getVatFee())
              .truePrice(order.getActualPrice())
              .salePrice(order.getSalePrice())
              .depositAmount(order.getDeposit())
              .remainAmount(order.getRemain())
              .totalAmount(order.getTotal())
              // .imagePath(order.getImagePath())
              .imagePath("12313")
              .build())) {

            if (ObjectUtils.isEmpty(order.getCustomerSource()) ||
                ObjectUtils.isEmpty(order.getDeliveryDate()) ||
                ObjectUtils.isEmpty(order.getDiscount()) ||
                ObjectUtils.isEmpty(order.getMaterialsFee()) ||
                !Utils.isNumber(order.getMaterialsFee()) ||
                ObjectUtils.isEmpty(order.getActualDeliveryFee()) ||
                !Utils.isNumber(order.getActualDeliveryFee()) ||
                ObjectUtils.isEmpty(order.getActualVatFee()) ||
                !Utils.isNumber(order.getActualVatFee())) {
              return Boolean.TRUE;
            }

            if (ObjectUtils.isEmpty(order.getReceiverName())) {
              order.setReceiverName(order.getCustomerName());
            }

            if (ObjectUtils.isEmpty(order.getReceiverPhone())) {
              order.setReceiverPhone(order.getCustomerPhone());
            }
            return Boolean.FALSE;
          }
          return Boolean.TRUE;
        }).findFirst();

    if (failOrder.isPresent()) {
      return;
    }

    orderService.createNewOrders(selectedOrder);
    ApplicationVariable.sortOrders();
    setDataOrderTable(orderTable);
    orderTable.refresh();
  }

  @FXML
  public void printSelectedOrders() {

  }

  @FXML
  @Override
  public void extractData() throws IOException {
    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      File csvFile = new File(String.join(".",
          String.valueOf(System.currentTimeMillis()),
          "xls"));

      HSSFWorkbook workbook = new HSSFWorkbook();
      HSSFSheet sheet = workbook.createSheet("Hoá Đơn");

      HSSFRow rowHead = sheet.createRow(BigInteger.ZERO.shortValue());

      int statusCodeCell = 0;
      int orderCodeCell = 1;
      int orderDesCell = 2;
      int bannerContentCell = 3;
      int receiverNameCell = 4;
      int receiverPhoneCell = 5;
      int deliveryAddressCell = 6;
      int receiveDateCell = 7;
      int orderNoteCell = 8;
      int customerNameCell = 9;
      int customerPhoneCell = 10;
      int socialLinkCell = 11;
      int sourceCell = 12;
      int orderDateCell = 13;
      int truePriceCell = 14;
      int discountCell = 15;
      int salePriceCell = 16;
      int deliveryFeeCell = 17;
      int vatFeeCell = 18;
      int depositCell = 19;
      int remainCell = 20;
      int categoryFeeCell = 21;
      int actualFeeCell = 22;
      int actualVatCell = 23;
      rowHead.createCell(statusCodeCell).setCellValue("Tình Trạng");
      rowHead.createCell(orderCodeCell).setCellValue("Mã Đơn");
      rowHead.createCell(orderDesCell).setCellValue("Mô tả đơn");
      rowHead.createCell(bannerContentCell).setCellValue("Nội dung banner");
      rowHead.createCell(receiverNameCell).setCellValue("Người nhận");
      rowHead.createCell(receiverPhoneCell).setCellValue("SĐT người nhận");
      rowHead.createCell(deliveryAddressCell).setCellValue("Địa chỉ giao");
      rowHead.createCell(receiveDateCell).setCellValue("Ngày nhận");
      rowHead.createCell(orderNoteCell).setCellValue("Ghi chú");
      rowHead.createCell(customerNameCell).setCellValue("Tên Người đặt");
      rowHead.createCell(customerPhoneCell).setCellValue("SĐT người đặt");
      rowHead.createCell(socialLinkCell).setCellValue("Link mạng xã hội");
      rowHead.createCell(sourceCell).setCellValue("Nguồn khách");
      rowHead.createCell(orderDateCell).setCellValue("Ngày Đặt");
      rowHead.createCell(truePriceCell).setCellValue("Giá niêm yết");
      rowHead.createCell(discountCell).setCellValue("Chiết khấu");
      rowHead.createCell(salePriceCell).setCellValue("Giá bán");
      rowHead.createCell(deliveryFeeCell).setCellValue("Phí giao khách trả");
      rowHead.createCell(vatFeeCell).setCellValue("Phí viết VAT khách trả");
      rowHead.createCell(depositCell).setCellValue("Đặt cọc");
      rowHead.createCell(remainCell).setCellValue("Còn phải trả");
      rowHead.createCell(categoryFeeCell).setCellValue("Chi phí nguyên liệu");
      rowHead.createCell(actualFeeCell).setCellValue("Phí giao hoa thực tế");
      rowHead.createCell(actualVatCell).setCellValue("Phí viết VAT thực tế");

      ApplicationVariable.getOrders()
          .forEach(order -> {
            HSSFRow row = sheet.createRow(Integer.parseInt(order.getStt()));
            row.createCell(statusCodeCell).setCellValue(order.getStatus());
            row.createCell(orderCodeCell).setCellValue(order.getCode());
            row.createCell(orderDesCell).setCellValue(order.getOrderDescription());
            row.createCell(bannerContentCell).setCellValue(order.getBanner());
            row.createCell(receiverNameCell).setCellValue(order.getReceiverName());
            row.createCell(receiverPhoneCell).setCellValue(order.getReceiverPhone());
            row.createCell(deliveryAddressCell).setCellValue(order.getDeliveryAddress());
            row.createCell(orderDateCell).setCellValue(order.getDeliveryDate());
            row.createCell(orderNoteCell).setCellValue(order.getCustomerNote());
            row.createCell(customerNameCell).setCellValue(order.getCustomerName());
            row.createCell(customerPhoneCell).setCellValue(order.getCustomerPhone());
            row.createCell(socialLinkCell).setCellValue(order.getCustomerSocialLink());
            row.createCell(sourceCell).setCellValue(order.getCustomerSource());
            row.createCell(orderDateCell).setCellValue(order.getOrderDate());
            row.createCell(truePriceCell).setCellValue(order.getActualPrice());
            row.createCell(discountCell).setCellValue(order.getDiscount());
            row.createCell(salePriceCell).setCellValue(order.getSalePrice());
            row.createCell(deliveryFeeCell).setCellValue(order.getDeliveryFee());
            row.createCell(vatFeeCell).setCellValue(order.getVatFee());
            row.createCell(depositCell).setCellValue(order.getVatFee());
            row.createCell(remainCell).setCellValue(order.getDeposit());
            row.createCell(categoryFeeCell).setCellValue(ZERO);
            row.createCell(actualFeeCell).setCellValue(order.getActualDeliveryFee());
            row.createCell(actualVatCell).setCellValue(order.getActualVatFee());

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
  public void addOrder() {
    ApplicationVariable.addFirst(Order.builder()
        .orderDate(Utils.formatDate(new Date()))
        .status(OrderState.PENDING_TEXT)
        .isSelected(Boolean.TRUE)
        .actualVatFee(ZERO)
        .actualPrice(ZERO)
        .actualDeliveryFee(ZERO)
        .materialsFee(ZERO)
        .build());

    setDataOrderTable(orderTable);
    ApplicationVariable.setTableSequence();
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
    categoryFee.setCellValueFactory(new PropertyValueFactory<>(Order.MATERIALS_FEE));
    actualDeliveryFee.setCellValueFactory(new PropertyValueFactory<>(Order.ACTUAL_VAT_FEE));
    actualVatFee.setCellValueFactory(new PropertyValueFactory<>(Order.ACTUAL_DELIVERY_FEE));

    checkAll.setCellValueFactory(param -> {
      Order order = param.getValue();
      final SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(order.getIsSelected());
      booleanProp.addListener((observable, oldValue, newValue) -> order.setIsSelected(newValue));
      return booleanProp;
    });
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
    categoryFee.setCellFactory(TextFieldTableCell.forTableColumn());
    actualDeliveryFee.setCellFactory(TextFieldTableCell.forTableColumn());
    actualVatFee.setCellFactory(TextFieldTableCell.forTableColumn());
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
    setEditEventTableCell(actualDeliveryFee);
    setEditEventTableCell(actualVatFee);
    setEditEventTableCell(categoryFee);
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
    addTableViewListener();
    this.stageManager.getStage().setOnShown(event ->
        onScrollFinished(this.orderTable));
    if (CollectionUtils.isEmpty(ApplicationVariable.getOrders())) {
      loadPageAsync(null, this.orderTable);
      return;
    }
    setDataOrderTable(this.orderTable);

    //TODO empName.setText(ApplicationVariable.getUser().getFullName());
  }

  private void addTableViewListener() {
    orderTable.getSelectionModel().selectedItemProperty()
        .addListener((obs, oldSelection, newSelection) -> {
          if (Objects.nonNull(newSelection)) {
            currentOrder = newSelection;
            ApplicationVariable.currentOrder = currentOrder;
          }
        });
  }
}
