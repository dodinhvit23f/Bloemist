package com.bloemist.services.impl;

import static com.utils.Utils.fileFormat;

import com.bloemist.controllers.order.OrderPrintControllers;
import com.bloemist.dto.Order;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.lowagie.text.pdf.BaseFont;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Component
public class CustomPrinterService implements IPrinterService {

  private static final String A5_BILL = "classpath:bill/a5_bill.html";
  private static final String BLOEMIST_LOGO = "classpath:Img/logo.png";
  private static final String PDF_FONT = "classpath:fonts/pdf.ttf";

  @Override
  public void printA5Order(String printerName, Order order) {
    Optional<PrintService> printService = Arrays.stream(
            PrintServiceLookup.lookupPrintServices(null, null))
        .filter(p -> p.getName().equals(printerName))
        .findFirst();

    String filename = "preview.pdf";
    try (OutputStream outputStream = new FileOutputStream(filename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      Document document = Jsoup.parse(
          new File(ResourceUtils.getFile(CustomPrinterService.A5_BILL).toURI()),
          StandardCharsets.UTF_8.name());

      document.body().getElementById(OrderPrintControllers.LOGO)
          .attr(
              OrderPrintControllers.SRC,
              "file:///" + fileFormat(CustomPrinterService.BLOEMIST_LOGO));
      document.body().getElementById(OrderPrintControllers.CUSTOMER_NAME)
          .text(order.getCustomerName());
      document.body().getElementById(OrderPrintControllers.CUSTOMER_PHONE)
          .text(order.getCustomerPhone());
      document.body().getElementById(OrderPrintControllers.ORDER_DESCRIPTION)
          .text(order.getOrderDescription());
      document.body().getElementById(OrderPrintControllers.BANNER_DESCRIPTION)
          .text(order.getBanner());
      document.body().getElementById(OrderPrintControllers.RECEIVE_NAME)
          .text(order.getReceiverName());
      document.body().getElementById(OrderPrintControllers.RECEIVE_PHONE)
          .text(order.getReceiverPhone());
      document.body().getElementById(OrderPrintControllers.RECEIVE_TIME)
          .text(order.getDeliveryHour());
      document.body().getElementById(OrderPrintControllers.RECEIVE_DATE)
          .text(order.getDeliveryDate());
      document.body().getElementById(OrderPrintControllers.SALE_PRICE).text(order.getSalePrice());
      document.body().getElementById(OrderPrintControllers.DELIVERY_FEE)
          .text(order.getDeliveryFee());
      document.body().getElementById(OrderPrintControllers.SALE_OFF).text(order.getDiscount());
      document.body().getElementById(OrderPrintControllers.TOTAL_PRICE).text(order.getTotal());
      document.body().getElementById(OrderPrintControllers.DEPOSIT_AMOUNT).text(order.getDeposit());
      document.body().getElementById(OrderPrintControllers.REMAIN_AMOUNT).text(order.getRemain());
      document.body().getElementById(OrderPrintControllers.STAFF_NAME).text(
          Objects.isNull(ApplicationVariable.getUser()) ? ""
              : ApplicationVariable.getUser().getFullName());

      File file = new File(order.getImagePath());

      if (file.exists()) {
        document.getElementById(OrderPrintControllers.PRODUCT)
            .attr(OrderPrintControllers.SRC, "file:///" + fileFormat(order.getImagePath()));
      }

      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
      ITextRenderer renderer = new ITextRenderer();
      renderer.getFontResolver().addFont(fileFormat(CustomPrinterService.PDF_FONT),
          BaseFont.IDENTITY_H,
          BaseFont.EMBEDDED);
      SharedContext sharedContext = renderer.getSharedContext();
      sharedContext.setPrint(true);
      sharedContext.setInteractive(false);

      renderer.setDocument(new W3CDom().fromJsoup(document), File.separator);
      renderer.layout();

      if (printService.isEmpty()) {
        renderer.createPDF(outputStream);
      }

      PrintService printer = printService.get();

      PrintRequestAttributeSet asset = new HashPrintRequestAttributeSet();
      asset.add(new Copies(BigInteger.ONE.intValue())); // print one copies
      asset.add(new MediaPrintableArea(0, 0, 148, 210, MediaPrintableArea.MM)); // print stapled
      asset.add(Sides.ONE_SIDED);

      renderer.createPDF(byteArrayOutputStream);

      ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(
          byteArrayOutputStream.toByteArray());

      Doc myDoc = new SimpleDoc(bufferedInputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
      DocPrintJob docPrintJob = printer.createPrintJob();
      docPrintJob.print(myDoc, asset);

      AtomicBoolean docsPrinted = new AtomicBoolean(Boolean.FALSE);
      docPrintJob.addPrintJobListener(new PrintJobAdapter() {
        @Override
        public void printJobCompleted(PrintJobEvent pje) {
          super.printJobCompleted(pje);
          docsPrinted.set(Boolean.TRUE);
        }
      });

      docPrintJob.print(myDoc, asset);

      while (!docsPrinted.get()) {
        Thread.sleep(1000);
      }

      bufferedInputStream.close();
    } catch (IOException | InterruptedException | PrintException e) {
      e.printStackTrace();
    }
  }

}
