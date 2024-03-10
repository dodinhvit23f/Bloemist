package com.bloemist.controllers.order;

import static com.bloemist.utils.Utils.currencyToStringNumber;
import static com.utils.Utils.openDialogChoiceImage;

import com.bloemist.dto.Order;
import com.bloemist.entity.OrderReport;
import com.bloemist.events.MessageWarning;
import com.bloemist.constant.ApplicationVariable;
import com.bloemist.constant.ApplicationView;
import com.bloemist.constant.Constants;
import com.bloemist.constant.OrderState;
import com.utils.Utils;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderController extends OrderController {

  protected CreateOrderController(ApplicationEventPublisher publisher) {
    super(publisher);
  }

  @FXML
  TextField outstandingBalanceValue;
  @FXML
  TextField totalAmountValue;
  @FXML
  TextField salePriceValue;
  @FXML
  TextArea orderDescription;
  @FXML
  TextArea customerRemark;
  @FXML
  TextArea bannerContent;
  @FXML
  TextField actualPrice;
  @FXML
  TextField socialLink;
  @FXML
  TextField customerPhone;
  @FXML
  TextField receiverPhone;
  @FXML
  TextField deliveryAddress;
  @FXML
  TextField deliveryHour;
  @FXML
  TextField depositAmount;
  @FXML
  TextField receiverName;
  @FXML
  TextField customerName;
  @FXML
  TextField deliveryFee;
  @FXML
  TextField vatFee;
  @FXML
  DatePicker deliveryDate;
  @FXML
  ComboBox<String> customerSource;
  @FXML
  TextField discountAmount;

  File imageFile;

  private static final AtomicBoolean isPopup = new AtomicBoolean(Boolean.FALSE);

  @FXML
  public void openImage() throws IOException {
    if (Objects.nonNull(imageFile)) {
      Desktop.getDesktop().open(new File(imageFile.getAbsolutePath()));
    }
  }

  @FXML
  public void chooseImage() {
    File chooseFile = openDialogChoiceImage((Stage) stageManager.getStage().getScene().getWindow());

    if (Objects.nonNull(chooseFile)) {
      imageFile = chooseFile;
    }
  }

  public void nextOrder() {
    this.actualPrice.clear();
    this.orderDescription.clear();
    this.socialLink.clear();
    this.customerPhone.clear();
    this.receiverPhone.clear();
    this.deliveryAddress.clear();
    this.deliveryHour.clear();
    this.depositAmount.clear();
    this.customerRemark.clear();
    this.receiverName.clear();
    this.bannerContent.clear();
    this.customerName.clear();
    this.deliveryFee.clear();
    this.vatFee.clear();

    resetScene();
  }

  public Optional<OrderReport> createNew() {

    var customerName = this.customerName.getText().strip(); //NOSONAR
    var customerPhone = this.customerPhone.getText().strip(); //NOSONAR
    var customerSocialLink = this.socialLink.getText().strip(); //NOSONAR
    var customerSource = this.customerSource.getSelectionModel().getSelectedItem() //NOSONAR
        .strip(); //NOSONAR
    var deliveryAddress = this.deliveryAddress.getText().strip(); //NOSONAR
    var receiverName = this.receiverName.getText().strip(); //NOSONAR
    var receiverPhone = this.receiverPhone.getText().strip(); //NOSONAR
    var deliveryTime = this.deliveryHour.getText().strip(); //NOSONAR
    var orderDescription = this.orderDescription.getText().strip();//NOSONAR
    var orderNote = this.customerRemark.getText().strip(); //NOSONAR
    var banner = this.bannerContent.getText().strip(); //NOSONAR
    var truePrice = currencyToStringNumber(this.actualPrice.getText().strip()); //NOSONAR
    var deliveryFee = currencyToStringNumber(this.deliveryFee.getText().strip()); //NOSONAR
    var vatFee = currencyToStringNumber(this.vatFee.getText().strip()); //NOSONAR
    var salePrice = currencyToStringNumber(Utils.currencyToStringNumber(this.salePriceValue.getText()));//NOSONAR
    var depositAmount = currencyToStringNumber(this.depositAmount.getText().strip());//NOSONAR
    var remainAmount = currencyToStringNumber(this.outstandingBalanceValue.getText()); //NOSONAR
    var totalAmount = currencyToStringNumber(this.totalAmountValue.getText());//NOSONAR
    var discountAmount = currencyToStringNumber(this.discountAmount.getText());//NOSONAR

    if (Objects.isNull(imageFile)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_001));
      return Optional.empty();
    }

    Date deliveryDateTime = getDeliveryDate(this.deliveryDate);

    if (ObjectUtils.isEmpty(receiverName)) {
      receiverName = customerName;
    }

    if (ObjectUtils.isEmpty(receiverPhone)) {
      receiverPhone = customerPhone;
    }

    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      var order = Order.builder()
          .customerName(customerName)
          .customerPhone(customerPhone)
          .customerSocialLink(customerSocialLink)
          .customerSource(customerSource)
          .deliveryAddress(deliveryAddress)
          .receiverPhone(receiverPhone)
          .receiverName(receiverName)
          .orderDate(Utils.formatDate(Date.from(Instant.now())))
          .deliveryHour(deliveryTime)
          .deliveryDate(Utils.formatDate(deliveryDateTime))
          .imagePath(imageFile.getAbsolutePath())
          .orderDescription(orderDescription)
          .customerNote(orderNote)
          .banner(banner)
          .discount(String.valueOf(discountAmount))
          .actualPrice(truePrice)
          .deliveryFee(deliveryFee)
          .vatFee(vatFee)
          .salePrice(salePrice)
          .deposit(depositAmount)
          .remain(remainAmount)
          .total(totalAmount)
          .status(OrderState.PENDING.getStateText())
          .actualVatFee("0")
          .actualDeliveryFee("0")
          .build();

      Optional<Boolean> orderReport = orderService.createNewOrder(order);

      if (orderReport.isPresent()) {
        ApplicationVariable.add(order);
        CompletableFuture.runAsync(ApplicationVariable::sortOrders);
        return Optional.of(new OrderReport());
      }
    }
    return Optional.empty();
  }

  public void resetScene() {
    String zeroCurrency = Utils.currencyFormat(BigInteger.ZERO.doubleValue());

    this.salePriceValue.setText(zeroCurrency);
    this.outstandingBalanceValue.setText(zeroCurrency);
    this.totalAmountValue.setText(zeroCurrency);
    this.actualPrice.setText(String.valueOf(BigInteger.ZERO).intern());

    String zero = String.valueOf(BigInteger.ZERO).intern();
    this.deliveryFee.setText(zero);
    this.vatFee.setText(zero);
    this.depositAmount.setText(zero);
    this.discountAmount.setText(zero);
    this.deliveryDate.setValue(LocalDate.now());

    this.customerSource.setValue(Constants.FACEBOOK);
    this.deliveryHour.setText("00:00 - 00:00");
    this.imageFile = null;
  }

  @FXML
  public void nextInput() {
    createNew().ifPresent(action -> nextOrder());
  }

  @FXML
  public void finishInput() {
    if (createNew().isPresent()) {
      cancel();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.customerSource.setItems(FXCollections.observableArrayList(Constants.REGULAR_CUSTOMER,
        Constants.CUSTOMER, Constants.TIKTOK, Constants.HOTLINE,
        Constants.FACEBOOK, Constants.INSTAGRAM, Constants.ZALO));
    this.customerSource.setValue(Constants.FACEBOOK);

    resetScene();
    initEvent();
  }

  @FXML
  public void calculateTotalPrice() {
    if (!Utils.isNumber(currencyToStringNumber(this.actualPrice.getText()).strip())
        || !Utils.isNumber(currencyToStringNumber(this.deliveryFee.getText().strip()))
        || !Utils.isNumber(currencyToStringNumber(this.vatFee.getText().strip()))
        || !Utils.isNumber(currencyToStringNumber(this.depositAmount.getText().strip()))) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003));
      return;
    }

    var discount = NumberUtils.parseNumber(currencyToStringNumber(this.discountAmount.getText()), Double.class);
    var truePrice = NumberUtils.parseNumber(currencyToStringNumber(this.actualPrice.getText()), Double.class);
    var deliveryFeeAmount = NumberUtils.parseNumber(currencyToStringNumber(this.deliveryFee.getText()), Double.class);
    var vatFeeAmount = NumberUtils.parseNumber(currencyToStringNumber(this.vatFee.getText()), Double.class);
    var deposit = NumberUtils.parseNumber(currencyToStringNumber(this.depositAmount.getText()), Double.class);

    var salePrice = getSalePrice(truePrice, discount);
    var totalSaleAmount = getTotalPrice(salePrice, deliveryFeeAmount, vatFeeAmount);

    this.salePriceValue.setText(Utils.currencyFormat(salePrice));
    this.totalAmountValue.setText(Utils.currencyFormat(totalSaleAmount));
    this.outstandingBalanceValue.setText(Utils.currencyFormat(totalSaleAmount - deposit));
  }


  public void initEvent() {
    // set event when actual price lost focus
    addEventLostFocus(this.actualPrice, this::calculateTotalPrice);
    addEventLostFocus(this.deliveryFee, this::calculateTotalPrice);
    addEventLostFocus(this.vatFee, this::calculateTotalPrice);
    addEventLostFocus(this.depositAmount, this::calculateTotalPrice);
    addEventLostFocus(this.discountAmount, this::calculateTotalPrice);
  }

  @Override
  public void cancel() {
    if (!isPopup()) {
      switchScene(ApplicationView.INQUIRY_ORDER);
      return;
    }
    isPopup.set(Boolean.FALSE);
    ((Stage)discountAmount.getScene().getWindow()).close();
  }

  @Override
  public void extractData() {
    throw new UnsupportedOperationException();
  }

  public static boolean isPopup() {
    return isPopup.get();
  }

  public static void setPopup(Boolean popup) {
    isPopup.set(popup);
  }
}
