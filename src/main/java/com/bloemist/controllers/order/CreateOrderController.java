package com.bloemist.controllers.order;

import com.bloemist.dto.Order;
import com.bloemist.dto.OrderInfo;
import com.bloemist.events.MessageWarning;
import com.constant.ApplicationVariable;
import com.constant.ApplicationView;
import com.constant.Constants;
import com.constant.OrderState;
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
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
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
  ComboBox<Integer> discountRate;

  File imageFile;

  public void openImage() throws IOException {

    if (Objects.nonNull(imageFile)) {
      Desktop.getDesktop().open(new File(imageFile.getAbsolutePath()));
    }
  }


  public void chooseImage() {
    Stage stage = (Stage) stageManager.getStage().getScene().getWindow();
    FileChooser fc = new FileChooser();
    fc.setTitle("Choose a image");
    FileChooser.ExtensionFilter imageFilter =
        new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
    fc.getExtensionFilters().add(imageFilter);

    File chooseFile = fc.showOpenDialog(stage);
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

  public void createNew() {

    var customerName = this.customerName.getText().strip(); //NOSONAR
    var customerPhone = this.customerPhone.getText().strip(); //NOSONAR
    var customerSocialLink = this.socialLink.getText().strip(); //NOSONAR
    var customerSource = this.customerSource.getSelectionModel().getSelectedItem()
        .strip(); //NOSONAR
    var deliveryAddress = this.deliveryAddress.getText().strip(); //NOSONAR
    var receiverName = this.receiverName.getText().strip(); //NOSONAR
    var receiverPhone = this.receiverPhone.getText().strip(); //NOSONAR
    var deliveryTime = this.deliveryHour.getText().strip(); //NOSONAR
    var orderDescription = this.orderDescription.getText().strip();//NOSONAR
    var orderNote = this.customerRemark.getText().strip(); //NOSONAR
    var banner = this.bannerContent.getText().strip(); //NOSONAR
    var truePrice = this.actualPrice.getText().strip(); //NOSONAR
    var discount = this.discountRate.getSelectionModel().getSelectedItem();//NOSONAR
    var deliveryFee = this.deliveryFee.getText().strip(); //NOSONAR
    var vatFee = this.vatFee.getText().strip(); //NOSONAR
    var salePrice = Utils.currencyToNumber(this.salePriceValue.getText());//NOSONAR
    var depositAmount = this.depositAmount.getText().strip();//NOSONAR
    var remainAmount = Utils.currencyToNumber(this.outstandingBalanceValue.getText()); //NOSONAR
    var totalAmount = Utils.currencyToNumber(this.totalAmountValue.getText());//NOSONAR

    if (Objects.isNull(imageFile)) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_001));
      return;
    }

    if (!validateOrderInfo(
        OrderInfo.builder()
            .customerName(customerName)
            .customerPhone(customerPhone)
            .customerSocialLink(customerSocialLink)
            .deliveryAddress(deliveryAddress)
            .deliveryTime(deliveryTime)
            .truePrice(truePrice)
            .deliveryFee(deliveryFee)
            .vatFee(vatFee)
            .salePrice(salePrice)
            .depositAmount(depositAmount)
            .remainAmount(remainAmount)
            .totalAmount(totalAmount)
            .imagePath(imageFile.getPath())
            .build())) {
      return;
    }

    Date deliveryDate = getDeliveryDate(this.deliveryDate);

    if (ObjectUtils.isEmpty(receiverName)) {
      receiverName = customerName;
    }

    if (ObjectUtils.isEmpty(receiverPhone)) {
      receiverPhone = customerPhone;
    }

    Alert alert = confirmDialog();
    if (alert.getResult() == ButtonType.YES) {
      var order =  Order.builder()
          .customerName(customerName)
          .customerPhone(customerPhone)
          .customerSocialLink(customerSocialLink)
          .customerSource(customerSource)
          .deliveryAddress(deliveryAddress)
          .receiverPhone(receiverPhone)
          .receiverName(receiverName)
          .orderDate(Utils.formatDate(Date.from(Instant.now())))
          .deliveryHour(deliveryTime)
          .deliveryDate(Utils.formatDate(deliveryDate))
          .imagePath(imageFile.getAbsolutePath())
          .orderDescription(orderDescription)
          .customerNote(orderNote)
          .banner(banner)
          .discount(String.valueOf(discount))
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
       orderService.createNewOrder(order);

      ApplicationVariable.add(order);
      CompletableFuture.runAsync(ApplicationVariable::sortOrders);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set discount rate from 0 to max
    this.discountRate.setItems(FXCollections
        .observableArrayList(IntStream.range(BigInteger.ZERO.intValue(), Constants.MAX_DISCOUNT)
            .boxed().collect(Collectors.toList())));

    this.customerSource.setItems(FXCollections.observableArrayList(Constants.ON_SHOP,
        Constants.FACEBOOK, Constants.INSTAGRAM, Constants.ZALO));
    this.customerSource.setValue(Constants.FACEBOOK);
    this.discountRate.setValue(BigInteger.ZERO.intValue());
    resetScene();
    initEvent();
  }

  @FXML
  public void calculateTotalPrice() {
    if (!Utils.isNumber(this.actualPrice.getText().strip())
        || !Utils.isNumber(this.deliveryFee.getText().strip())
        || !Utils.isNumber(this.vatFee.getText().strip())
        || !Utils.isNumber(this.depositAmount.getText().strip())) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003));
      return;
    }
    var discount = this.discountRate.getSelectionModel().getSelectedItem().doubleValue();

    var truePrice = NumberUtils.parseNumber(this.actualPrice.getText(), Double.class);
    var deliveryFeeAmount = NumberUtils.parseNumber(this.deliveryFee.getText(), Double.class);
    var vatFeeAmount = NumberUtils.parseNumber(this.vatFee.getText(), Double.class);
    var deposit = NumberUtils.parseNumber(this.depositAmount.getText(), Double.class);

    if (vatFeeAmount > Constants.MAX_VAT) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_007));
      return;
    }

    var salePrice = getSalePrice(truePrice, discount);
    var totalSaleAmount = getTotalPrice(salePrice, deliveryFeeAmount, vatFeeAmount);

    this.salePriceValue.setText(Utils.currencyFormat(salePrice));
    this.totalAmountValue.setText(Utils.currencyFormat(totalSaleAmount));
    this.outstandingBalanceValue.setText(Utils.currencyFormat(totalSaleAmount - deposit));
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

    this.deliveryDate.setValue(LocalDate.now());

    this.discountRate.setValue(BigInteger.ZERO.intValue());
    this.customerSource.setValue(Constants.FACEBOOK);
    this.deliveryHour.setText("00:00");

  }

  public void initEvent() {
    // set event when actual price lost focus
    addEventLostFocus(this.actualPrice, this::calculateTotalPrice);
    addEventLostFocus(this.deliveryFee, this::calculateTotalPrice);
    addEventLostFocus(this.vatFee, this::calculateTotalPrice);
    addEventLostFocus(this.depositAmount, this::calculateTotalPrice);
    discountRate.getSelectionModel().selectedItemProperty()
        .addListener((options, oldValue, newValue) -> calculateTotalPrice());
  }

  @Override
  public void cancel() {
    switchScene(ApplicationView.INQUIRY_ORDER);
  }

  @Override
  public void extractData() {
  }
}
