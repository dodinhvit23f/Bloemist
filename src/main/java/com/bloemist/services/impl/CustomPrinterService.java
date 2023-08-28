package com.bloemist.services.impl;

import static com.bloemist.dto.Order.IMAGE_PATH;

import com.bloemist.controllers.order.OrderPrintControllers;
import com.bloemist.dto.Order;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.utils.Utils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class CustomPrinterService implements IPrinterService {

  private static final String A5_BILL = "bill/a5_bill.jrxml";
  private static final String IMAGE_A5_BILL = "bill/image_a5.jrxml";
  private static final String BLOEMIST_LOGO = "classpath:Img/logo.png";
  private static final String FB_ICON = "classpath:Img/facebook.png";
  private static final String PHONE_ICON = "classpath:Img/telephone.png";
  public static final String PREVIEW_PDF = "preview.pdf";
  public static final String IMAGE_PDF = "image.pdf";
  public static final String REPORT_LOCALE = "REPORT_LOCALE";
  public static final String TODAY = "today";
  public static final String LOGO_URL = "logo_url";
  public static final String FB_URL = "fb_url";
  public static final String TELEPHONE_URL = "telephone_url";

  @Override
  public void printA5Order(String printerName, Order order) {
    Optional<PrintService> printService = Arrays.stream(
            PrintServiceLookup.lookupPrintServices(null, null))
        .filter(p -> p.getName().equals(printerName)).findFirst();

    Map<String, Object> parameters = new HashMap<>();
    try {

      parameters.put(Order.CUSTOMER_NAME, order.getCustomerName());
      parameters.put(Order.CUSTOMER_PHONE, order.getCustomerPhone());
      parameters.put(Order.ORDER_DESCRIPTION, order.getOrderDescription());
      parameters.put(Order.RECEIVER_NAME, order.getReceiverName());
      parameters.put(Order.RECEIVER_PHONE, order.getReceiverPhone());
      parameters.put(Order.DELIVERY_HOUR, order.getDeliveryHour());
      parameters.put(Order.DELIVERY_DATE, order.getDeliveryDate());
      parameters.put(Order.SALE_PRICE, order.getSalePrice());
      parameters.put(Order.DELIVERY_FEE, order.getDeliveryFee());
      parameters.put(Order.DISCOUNT, order.getDiscount());
      parameters.put(Order.TOTAL, order.getTotal());
      parameters.put(Order.DEPOSIT, order.getDeposit());
      parameters.put(Order.REMAIN, order.getRemain());
      parameters.put(Order.VAT_FEE, order.getRemain());
      parameters.put(Order.CODE, order.getCode());
      parameters.put(TODAY, Utils.formatDate(new Date()));
      parameters.put(Order.DELIVERY_ADDRESS, order.getDeliveryAddress());
      parameters.put(OrderPrintControllers.STAFF_NAME,
          Objects.isNull(ApplicationVariable.getUser()) ? ""
              : ApplicationVariable.getUser().getFullName());
      parameters.put(REPORT_LOCALE, new Locale("vi-VN"));
      parameters.put(LOGO_URL, ResourceUtils.getFile(BLOEMIST_LOGO).getAbsolutePath());
      parameters.put(FB_URL, ResourceUtils.getFile(FB_ICON).getAbsolutePath());
      parameters.put(TELEPHONE_URL, ResourceUtils.getFile(PHONE_ICON).getAbsolutePath());

      JasperReport jasperReport = JasperCompileManager
          .compileReport(new ClassPathResource(A5_BILL).getInputStream());

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRMapArrayDataSource(new Object[]{new HashMap<String, Object>()}));

      JasperExportManager.exportReportToPdfFile(jasperPrint, PREVIEW_PDF);

      if (printService.isPresent()) {
        PrintService printer = printService.get();
        printFilePDF(printer, PREVIEW_PDF);
      }

    } catch (JRException | PrintException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void printA5Image(String printerName, Order order) {

    Optional<PrintService> printService = Arrays.stream(
            PrintServiceLookup.lookupPrintServices(null, null))
        .filter(p -> p.getName().equals(printerName)).findFirst();

    Map<String, Object> parameters = new HashMap<>();
    try {
      JasperReport jasperReport = JasperCompileManager
          .compileReport(new ClassPathResource(IMAGE_A5_BILL).getInputStream());

      parameters.put(IMAGE_PATH, order.getImagePath());

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRMapArrayDataSource(new Object[]{new HashMap<String, Object>()}));

      JasperExportManager.exportReportToPdfFile(jasperPrint, IMAGE_PDF);

      if (printService.isPresent()) {
        PrintService printer = printService.get();
        printFilePDF(printer, IMAGE_PDF);
      }
    } catch (JRException | PrintException | IOException e) {
      e.printStackTrace();
    }
  }

  private void printFilePDF(PrintService printService, String filePath)
      throws PrintException, IOException {
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    pras.add(new Copies(1));
    pras.add(new MediaPrintableArea(2, 2, 148 , 152, MediaPrintableArea.MM)); // print stapled
    pras.add(Sides.ONE_SIDED);
    pras.add(OrientationRequested.LANDSCAPE);
    //pras.add(ColorSupported.SUPPORTED);

    DocFlavor docFlavor = DocFlavor.INPUT_STREAM.PDF;

    final BufferedInputStream inputStream =  new BufferedInputStream(new FileInputStream(filePath));


    Doc pdfDoc = new SimpleDoc(inputStream, docFlavor, new HashDocAttributeSet());
    DocPrintJob printJob = printService.createPrintJob();

    printJob.print(pdfDoc, pras);
    inputStream.close();
  }

}
