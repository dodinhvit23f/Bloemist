package com.bloemist.services.impl;

import com.bloemist.dto.Order;
import com.bloemist.services.IPrinterService;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Arrays;
import java.util.Optional;

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

  /*  String filename = "preview.pdf";
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

      Doc myDoc = new SimpleDoc(bufferedInputStream, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8, null);
      DocPrintJob docPrintJob = printer.createPrintJob();

      AtomicBoolean docsPrinted = new AtomicBoolean(Boolean.FALSE);
      docPrintJob.addPrintJobListener(new PrintJobAdapter() {
        @Override
        public void printJobCompleted(PrintJobEvent pje) {
          super.printJobCompleted(pje);
          docsPrinted.set(Boolean.TRUE);
        }
      });

      docPrintJob.print(myDoc, asset);
      docPrintJob.print(myDoc, asset);
      while (!docsPrinted.get()) {
        Thread.sleep(1000);
      }

      bufferedInputStream.close();
    } catch (IOException | InterruptedException | PrintException e) {
      e.printStackTrace();
    }*/
  }

}
