package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.OrderState;
import com.utils.Utils;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import static com.constant.Constants.CUSTOMER;
import static com.constant.Constants.FACEBOOK;
import static com.constant.Constants.HOTLINE;
import static com.constant.Constants.INSTAGRAM;
import static com.constant.Constants.REGULAR_CUSTOMER;
import static com.constant.Constants.TIKTOK;
import static com.constant.Constants.ZALO;
import static com.utils.Utils.openDialogChoiceImage;

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
  @FXML
  private TableColumn<Order, String> pictureLink;

  private AtomicReference<TableColumn<Order, ?>> editableColumn = new AtomicReference<>();
  private Order currentOrder;
  private int orderRow;

  private Map<Integer, TableColumn> tableColumnMap = new ConcurrentHashMap<>();

  private void setCellValueFactory() {
    setColumnsValues();
    setColumnsFactory();
    setColumnsEditStart();
    setAfterEditEvent();
  }

  @FXML
  public void updateInfo() {
    Alert alert = confirmDialog();

    if (alert.getResult() == ButtonType.YES) {
      List<Order> selectedOrder = orderTable.getItems()
          .filtered(order -> Objects.equals(order.getIsSelected(), Boolean.TRUE))
          .stream().toList();

      orderService.updateOrders(selectedOrder);
    }
  }

  @FXML
  public void saveSelectedOrders() {
    Alert alert = confirmDialog();

    if (alert.getResult() == ButtonType.YES) {
      List<Order> selectedOrder = orderTable.getItems()
          .filtered(order -> Objects.equals(order.getIsSelected(), Boolean.TRUE))
          .stream()
          .map(order -> {
            order.setTotal(
                getTotalPrice(Double.valueOf(Utils.currencyToStringNumber(order.getSalePrice())),
                    Double.valueOf(Utils.currencyToStringNumber(order.getDeliveryFee())),
                    Double.valueOf(Utils.currencyToStringNumber(order.getVatFee()))).toString());
            order.setRemain(
                String.valueOf(Double.parseDouble(order.getTotal()) - Double.parseDouble(
                    Utils.currencyToStringNumber(order.getDeposit()))));

            order.setActualPrice(Utils.currencyToStringNumber(order.getActualPrice()));
            order.setActualVatFee(Utils.currencyToStringNumber(order.getActualVatFee()));
            order.setActualDeliveryFee(Utils.currencyToStringNumber(order.getActualDeliveryFee()));
            order.setSalePrice(Utils.currencyToStringNumber(order.getSalePrice()));
            order.setDeliveryFee(Utils.currencyToStringNumber(order.getDeliveryFee()));
            order.setDeposit(Utils.currencyToStringNumber(order.getDeposit()));
            order.setDiscount(Utils.currencyToStringNumber(order.getDiscount()));
            order.setVatFee(Utils.currencyToStringNumber(order.getVatFee()));
            order.setMaterialsFee(Utils.currencyToStringNumber(order.getMaterialsFee()));

            return order;
          }).toList();

      var failOrder = selectedOrder.stream()
          .filter(order -> {
            if (validateOrderInfo(Order.builder()
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerSocialLink(order.getCustomerSocialLink())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryHour(order.getDeliveryHour())
                .deliveryFee(Utils.currencyToStringNumber(order.getDeliveryFee()))
                .vatFee(order.getVatFee())
                .actualPrice(order.getActualPrice())
                .salePrice(order.getSalePrice())
                .deposit(order.getDeposit())
                .remain(order.getRemain())
                .total(order.getTotal())
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
      setDataOrderTable(orderTable, Boolean.FALSE);
      orderTable.refresh();
    }
  }

  @FXML
  public void printSelectedOrders() {
    if (isCurrentOrderEmpty()) {
      return;
    }
    switchScene(ApplicationView.PRINT_ORDER, ApplicationView.MASTER_ORDER);
  }

  @FXML
  public void refresh() {
    this.orderTable.setItems(FXCollections.observableArrayList());
    loadPageAsync(null, this.orderTable, Boolean.TRUE);
  }

  @FXML
  public void pickImage() {
    if (Objects.nonNull(currentOrder)) {
      File chooseFile = openDialogChoiceImage(
          (Stage) stageManager.getStage().getScene().getWindow());

      if (Objects.nonNull(chooseFile)) {
        currentOrder.setImagePath(chooseFile.getAbsolutePath());
        orderTable.refresh();
      }
    }
  }

  @FXML
  public void seeImage() {
    try {
      viewImage(currentOrder);
    } catch (Exception e) {

    }
  }

  @FXML
  public void deleteSelectedRow() {
    ApplicationVariable.getOrders().removeAll(ApplicationVariable.getOrders()
        .stream()
        .filter(Order::getIsSelected)
        .toList());

    updateOrderTable();
  }

  @FXML
  public void addNewRow() {
    ApplicationVariable.addFirst(Order.builder()
        .orderDate(Utils.formatDate(new Date()))
        .status(OrderState.PENDING_TEXT)
        .isSelected(Boolean.TRUE)
        .actualVatFee(ZERO)
        .actualPrice(ZERO)
        .actualDeliveryFee(ZERO)
        .materialsFee(ZERO)
        .deliveryDate(Utils.formatDate(new Date()))
        .deliveryHour("00:00 - 00:00")
        .priority(BigInteger.ZERO.intValue())
        .statusProperty(new SimpleStringProperty(OrderState.PENDING.getStateText()))
        .customerSourceProperty(new SimpleStringProperty(FACEBOOK))
        .build());

    updateOrderTable();
  }

  @FXML
  public void clearScreen() {
    this.orderTable.setItems(FXCollections.observableArrayList(Collections.emptyList()));
    ApplicationVariable.getOrders().clear();
  }

  @FXML
  @Override
  public void extractData() {
    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      File csvFile = new File(String.join(".",
          String.valueOf(System.currentTimeMillis()),
          "xls"));

      try (HSSFWorkbook workbook = new HSSFWorkbook();) {
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

        Alert confirm = new Alert(AlertType.CONFIRMATION,
            String.join(" ",
                "File",
                csvFile.getName(),
                "đã lưu lại"),
            ButtonType.YES);
        confirm.show();
      } catch (Exception e) {
        new Alert(AlertType.CONFIRMATION, "Xảy ra lỗi khi xuất file",
            ButtonType.OK).show();
      }
    }
  }

  private void setColumnsValues() {
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

    pictureLink.setCellValueFactory(new PropertyValueFactory<>(Order.IMAGE_PATH));
  }

  private void setColumnsFactory() {
    setStatusColumn();
    setCustomerSourceColumn();

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

    orderDate.setCellFactory(TextFieldTableCell.forTableColumn());
    bannerContent.setCellFactory(TextFieldTableCell.forTableColumn());
    orderCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    categoryFee.setCellFactory(TextFieldTableCell.forTableColumn());
    actualDeliveryFee.setCellFactory(TextFieldTableCell.forTableColumn());
    actualVatFee.setCellFactory(TextFieldTableCell.forTableColumn());
    pictureLink.setCellFactory(TextFieldTableCell.forTableColumn());
    checkAll.setCellFactory(CheckBoxTableCell.forTableColumn(checkAll));
  }

  private void setStatusColumn() {
    statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(
        OrderState.DONE.getStateText(),
        OrderState.DONE_DELIVERY.getStateText(),
        OrderState.DONE_PROCESS.getStateText(),
        OrderState.CANCEL.getStateText(),
        OrderState.IN_DELIVERY.getStateText(),
        OrderState.IN_DEBIT.getStateText(),
        OrderState.IN_PROCESS.getStateText(),
        OrderState.PENDING.getStateText()));

    statusCol.setCellValueFactory(orderStringCellDataFeatures -> {
      Order value = orderStringCellDataFeatures.getValue();
      value.setStatus(value.getStatusProperty().getValue());
      return value.getStatusProperty();
    });
  }

  private void setCustomerSourceColumn() {
    customerSource.setCellFactory(ComboBoxTableCell.forTableColumn(
        FACEBOOK,
        INSTAGRAM,
        ZALO,
        CUSTOMER,
        REGULAR_CUSTOMER,
        TIKTOK,
        HOTLINE));

    customerSource.setCellValueFactory(orderStringCellDataFeatures -> {
      Order value = orderStringCellDataFeatures.getValue();
      value.setCustomerSource(value.getCustomerSourceProperty().getValue());
      return value.getCustomerSourceProperty();
    });
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

    setEditEventTableCell(orderDate);
    setEditEventTableCell(customerName);
    setEditEventTableCell(actualDeliveryFee);
    setEditEventTableCell(actualVatFee);
    setEditEventTableCell(categoryFee);
    setEditEventTableCell(pictureLink);

  }

  private void setEditEventTableCell(TableColumn<Order, String> tableColumn) {

    tableColumn.setOnEditStart(event -> {
      orderRow = event.getTablePosition().getRow();
      currentOrder = event.getTableView().getItems().get(orderRow);
      editableColumn.set(tableColumn);

      if (!textArea.getText().equals(event.getOldValue()) &&
          Objects.isNull(event.getNewValue())) {
        if (Objects.isNull(event.getNewValue())) {
          if(Objects.nonNull(event.getOldValue())){
            textArea.setText(event.getOldValue());
          }
          return;
        }
        textArea.setText(event.getNewValue());
      }

    });

    tableColumn.setOnEditCommit(event -> {
      if (Objects.nonNull(event.getNewValue())) {
        orderRow = event.getTablePosition().getRow();
        currentOrder = event.getTableView().getItems().get(orderRow);
        textArea.setText(event.getNewValue());
        editableColumn.set(tableColumn);
        System.out.println(tableColumn.getText());
        setValueToColumn(tableColumn, currentOrder, event.getNewValue());
      }
    });


  }

  private void setAfterEditEvent() {
    textArea.textProperty().addListener((observable, oldValue, newValue) -> {
      if (Objects.nonNull(currentOrder)) {
        setValueToColumn(editableColumn.get(), currentOrder, newValue);
        orderTable.refresh();
      }
    });

    textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (Boolean.FALSE.equals(newValue)) {
        editableColumn.set(null);
        textArea.setText("");
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

    if (orderDate.equals(tableColumn)) {
      order.setOrderDate(value);
    }
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

  private void updateOrderTable() {
    setDataOrderTable(orderTable, Boolean.TRUE);
    ApplicationVariable.setTableSequence();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initEvent();
    addTableViewListener();
    this.stageManager.getStage().setOnShown(event ->
        onScrollFinished(this.orderTable));

    setTabEvent();

    if (CollectionUtils.isEmpty(ApplicationVariable.getOrders())) {
      loadPageAsync(null, this.orderTable, Boolean.TRUE);
      return;
    }
    setDataOrderTable(this.orderTable, Boolean.TRUE);



    //TODO empName.setText(ApplicationVariable.getUser().getFullName());
  }

  private void setTabEvent() {
    AtomicInteger atomicInteger = new AtomicInteger(0);

    for (TableColumn column : orderTable.getColumns()){
      if(column.getColumns().size() > 1){
        for (Object subCol : column.getColumns()){
          tableColumnMap.put(atomicInteger.getAndIncrement(), (TableColumn) subCol);
        }
      }else {
        tableColumnMap.put(atomicInteger.getAndIncrement(), column);
      }
    }

    // Add event handler to move focus to the next column
    orderTable.setOnKeyPressed(event -> {

      if (event.getCode().equals(KeyCode.TAB) ||
          event.getCode().equals(KeyCode.ENTER)) {
        TableView.TableViewSelectionModel<Order> selectionModel = orderTable.getSelectionModel();
        TablePosition tablePosition = selectionModel.getSelectedCells().get(0);
        Optional<Integer> position = tableColumnMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(editableColumn.get()))
            .map(entry ->entry.getKey())
            .findFirst();

        if(position.isEmpty()){
          System.err.println("Not found column");
          return;
        }

        TableColumn column = tableColumnMap.get(position.get() + 1);
        if (Objects.nonNull(column)) {
          orderTable.getSelectionModel().select(tablePosition.getRow(), column);
          orderTable.edit(tablePosition.getRow(), column);
        }
      }
      event.consume();
    });

  }
}
