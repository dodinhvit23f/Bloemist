package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.services.IOrderService;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderPrintControllers extends BaseController {

  IOrderService orderService;
  @FXML
  private ChoiceBox<String> choicePrinter;
  @FXML
  private RadioButton a5Bill;
  @FXML
  private RadioButton heatBill;
  @FXML
  private RadioButton imageBill;


  protected OrderPrintControllers(ApplicationEventPublisher publisher, IOrderService orderService) {
    super(publisher);
    this.orderService = orderService;
  }

  public void applyPrint(){
    if(imageBill.isSelected()){
      
    }

    if(a5Bill.isSelected()){
      var printerServices = PrintServiceLookup.lookupPrintServices(null, null);

      PrinterJob job = PrinterJob.createPrinterJob();
      Printer printer = Printer.getDefaultPrinter();
      PageLayout pageLayout = printer.createPageLayout(Paper.A5, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
      JobSettings jobSettings = job.getJobSettings();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    var printerServices = PrintServiceLookup.lookupPrintServices(null, null);

    choicePrinter.setItems(
        FXCollections.observableList(
            Arrays.stream(printerServices)
                .map(PrintService::getName)
                .collect(Collectors.toList())));
  }
}
