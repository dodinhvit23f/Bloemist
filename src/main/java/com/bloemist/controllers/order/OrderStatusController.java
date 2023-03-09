package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.dto.Order;
import com.bloemist.events.MessageSuccess;
import com.bloemist.services.IOrderService;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.constant.OrderState;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusController extends BaseController {

  @FXML
  ComboBox<String> changeStatus;
  @FXML
  TextField actualDeliveryfee;
  @FXML
  TextArea customerRemark;

  private final IOrderService orderService;

  Order order;

  protected OrderStatusController(ApplicationEventPublisher publisher, IOrderService orderService) {
    super(publisher);
    this.orderService = orderService;


  }

  public void changeOrderStatus(){

    if(Objects.isNull(this.order)){
      publisher.publishEvent(new MessageSuccess(Constants.ERR_ORDER_STATUS));
      switchScene(ApplicationView.INQUIRY_ORDER);
      return;
    }

    var note = customerRemark.getText();
    var deliveryFee = actualDeliveryfee.getText();

    if(!order.getCustomerNote().equals(note)){
      order.setCustomerNote(note);
    }

    if(!order.getActualDeliveryFee().equals(deliveryFee)){
      order.setActualDeliveryFee(deliveryFee);
    }

    if(!order.getStatus().equals(changeStatus.getValue())){
      order.setActualDeliveryFee(changeStatus.getValue());
    }

    orderService.changeOrderStateInfo(order);

  }

  @Override
  public void cancel(){
    switchScene(ApplicationView.INQUIRY_ORDER);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { //NOSONAR

    order = ApplicationVariable.currentOrder;
    if(Objects.isNull(this.order)){
      return;
    }

    this.changeStatus.setValue(order.getStatus());
    this.actualDeliveryfee.setText(order.getActualDeliveryFee());
    this.customerRemark.setText(order.getCustomerNote());

    changeStatus.setItems(
        FXCollections.observableList(
            List.of(OrderState.CANCEL.getStateText(),
                OrderState.DONE.getStateText(),
                OrderState.IN_PROCESS.getStateText(),
                OrderState.PENDING.getStateText(),
                OrderState.DONE_DELIVERY.getStateText(),
                OrderState.DONE_PROCESS.getStateText(),
                OrderState.IN_DEBIT.getStateText(),
                OrderState.IN_DELIVERY.getStateText())));
  }

}
