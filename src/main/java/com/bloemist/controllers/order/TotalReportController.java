package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.constant.ApplicationVariable;
import com.constant.OrderState;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
              OrderState.DONE.getState(),
              OrderState.DONE_DELIVERY.getState(),
              OrderState.DONE_PROCESS.getState(),
              OrderState.CANCEL.getState(),
              OrderState.IN_DELIVERY.getState(),
              OrderState.IN_DEBIT.getState(),
              OrderState.IN_PROCESS.getState()));

          TableCell<Order, String> cell = new TableCell<>() {
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

          return cell;
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

  private void setEditEventTableCell(TableColumn tableColumn) {
    tableColumn.setOnEditStart(event -> {
      var cellEditEvent = ((CellEditEvent<Order, String>) event);
      textArea.setText(cellEditEvent.getOldValue());
      orderRow = cellEditEvent.getTablePosition().getRow();
      currentOrder = cellEditEvent.getTableView().getItems().get(orderRow);
      editableColumn = tableColumn;
    });

    tableColumn.setOnEditCommit(event -> {
      var cellEditEvent = ((CellEditEvent<Order, String>) event);
      if(Objects.nonNull(cellEditEvent.getNewValue())) {
        textArea.setText(cellEditEvent.getNewValue());
        setValueToColumn(editableColumn, currentOrder, cellEditEvent.getNewValue());
      }
    });


  }

  private void setAfterEditEvent() {
    textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if(Boolean.FALSE.equals(newValue)){
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
