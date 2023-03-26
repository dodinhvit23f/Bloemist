package com.bloemist.controllers.order;

import com.bloemist.controllers.BaseController;
import com.bloemist.services.IOrderService;
import com.constant.ApplicationView;
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
import javafx.print.Printer.MarginType;
import javafx.print.PrinterJob;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javax.print.DocPrintJob;
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
      Printer printer = Printer.getAllPrinters().stream()
          .filter(p -> p.getName().equals(choicePrinter.getValue()))
          .findFirst().orElseThrow();

      PrinterJob job = PrinterJob.createPrinterJob(printer);

      PageLayout pageLayout = printer.createPageLayout(Paper.A5, PageOrientation.PORTRAIT, MarginType.HARDWARE_MINIMUM);

      if (job.printPage(pageLayout, stageManager.getPane())) {
        job.endJob();
      }
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

  @Override
  public void cancel() {
    switchScene(ApplicationView.INQUIRY_ORDER);
  }
}
