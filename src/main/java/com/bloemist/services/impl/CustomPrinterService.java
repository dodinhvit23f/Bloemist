package com.bloemist.services.impl;

import static com.utils.Utils.fileFormat;

import com.bloemist.controllers.order.OrderPrintControllers;
import com.bloemist.dto.Order;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class CustomPrinterService implements IPrinterService {

  private static final String A5_BILL = "classpath:bill/a5_bill.html";
  private static final String CSS_A5_BILL = "classpath:css/index.css";
  private static final String BLOEMIST_LOGO = "classpath:Img/logo.png";
  private static final String PDF_FONT = "classpath:fonts/pdf.ttf";
  public static final String FILE = "file:///";
  public static final String CSS_LINK = "css-link";
  public static final String HREF = "href";
  public static final String PREVIEW_HTML = "preview.html";
  public static final String PREVIEW_PDF = "preview.pdf";

  @Override
  public void printA5Order(String printerName, Order order) {
    Optional<PrintService> printService = Arrays.stream(
            PrintServiceLookup.lookupPrintServices(null, null))
        .filter(p -> p.getName().equals(printerName))
        .findFirst();

    String filename = PREVIEW_PDF;
    try (OutputStream outputStream = new FileOutputStream(filename);) {
      Document document = Jsoup.parse(
          new File(ResourceUtils.getFile(CustomPrinterService.A5_BILL).toURI()),
          StandardCharsets.UTF_8.name());

      document.body().getElementById(OrderPrintControllers.LOGO)
          .attr(
              OrderPrintControllers.SRC, FILE + fileFormat(CustomPrinterService.BLOEMIST_LOGO));
      document.body().getElementById(OrderPrintControllers.CUSTOMER_NAME)
          .val(order.getCustomerName());
      document.body().getElementById(OrderPrintControllers.CUSTOMER_PHONE)
          .val(order.getCustomerPhone());
      document.body().getElementById(OrderPrintControllers.ORDER_DESCRIPTION)
          .text(order.getOrderDescription());
      document.body().getElementById(OrderPrintControllers.RECEIVE_NAME)
          .val(order.getReceiverName());
      document.body().getElementById(OrderPrintControllers.RECEIVE_PHONE)
          .val(order.getReceiverPhone());
      document.body().getElementById(OrderPrintControllers.RECEIVE_TIME)
          .val(order.getDeliveryHour());
      document.body().getElementById(OrderPrintControllers.RECEIVE_DATE)
          .val(order.getDeliveryDate());
      document.body().getElementById(OrderPrintControllers.SALE_PRICE).val(order.getSalePrice());
      document.body().getElementById(OrderPrintControllers.DELIVERY_FEE)
          .val(order.getDeliveryFee());
      document.body().getElementById(OrderPrintControllers.SALE_OFF).val(order.getDiscount());
      document.body().getElementById(OrderPrintControllers.TOTAL_PRICE).val(order.getTotal());
      document.body().getElementById(OrderPrintControllers.DEPOSIT_AMOUNT).val(order.getDeposit());
      document.body().getElementById(OrderPrintControllers.REMAIN_AMOUNT).val(order.getRemain());
      document.body().getElementById(OrderPrintControllers.VAT_FEE).val(order.getRemain());
      document.body().getElementById(OrderPrintControllers.STAFF_NAME).val(
          Objects.isNull(ApplicationVariable.getUser()) ? ""
              : ApplicationVariable.getUser().getFullName());

      document.getElementById(CSS_LINK)
          .attr(HREF,
              FILE + ResourceUtils.getFile(CustomPrinterService.CSS_A5_BILL).getAbsolutePath()
                  .replace("\\", "/"));

      File file = new File(order.getImagePath());

     /* if (file.exists()) {
        document.getElementById(OrderPrintControllers.PRODUCT)
            .attr(OrderPrintControllers.SRC, "file:///" + fileFormat(order.getImagePath()));
      }*/

      OutputStreamWriter fileOutputStream = new OutputStreamWriter(
          new FileOutputStream(PREVIEW_HTML));
      fileOutputStream.write(document.html());
      fileOutputStream.close();
      /*PdfWriter pdfWriter = new PdfWriter(outputStream);
      ConverterProperties converterProperties = new ConverterProperties();
      converterProperties.setBaseUri("/css");
      PdfDocument pdfDocument = new PdfDocument(pdfWriter);

      var docs = HtmlConverter.convertToDocument(new FileInputStream(PREVIEW_HTML), pdfDocument,
          converterProperties);
      docs.close();*/

     /* File ox = new File("x.html");
      */

      PrintService printer = printService.get();
      printFilePDF(printer, PREVIEW_HTML);
    } catch (IOException | PrintException e) {
      e.printStackTrace();
    }
  }

  private void printFilePDF(PrintService printService, String filePath)
      throws PrintException, IOException {
    FileInputStream in = new FileInputStream(filePath);

    PrintRequestAttributeSet asset = new HashPrintRequestAttributeSet();
    asset.add(new Copies(BigInteger.ONE.intValue())); // print one copies
    asset.add(new MediaPrintableArea(0, 0, 148, 210, MediaPrintableArea.MM)); // print stapled



    DocPrintJob docPrintJob = printService.createPrintJob();
    Doc doc = new SimpleDoc(in,  DocFlavor.INPUT_STREAM.AUTOSENSE, null);

    AtomicBoolean docsPrinted = new AtomicBoolean(Boolean.FALSE);
    docPrintJob.addPrintJobListener(new PrintJobAdapter() {
      @Override
      public void printJobCompleted(PrintJobEvent pje) {
        super.printJobCompleted(pje);
        docsPrinted.set(Boolean.TRUE);
      }
    });
    docPrintJob.print(doc, asset);
  }

}
