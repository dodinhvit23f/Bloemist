package com.bloemist.services.impl;

import com.bloemist.controllers.order.OrderPrintControllers;
import com.bloemist.dto.Order;
import com.bloemist.services.IPrinterService;
import com.constant.ApplicationVariable;
import com.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.type.PdfPermissionsEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class CustomPrinterService implements IPrinterService {

  private static final String A5_BILL = "classpath:bill/a5_bill.jrxml";
  private static final String BLOEMIST_LOGO = "classpath:Img/logo.png";
  private static final String FB_ICON = "classpath:Img/facebook.png";
  private static final String PHONE_ICON = "classpath:Img/telephone.png";
  public static final String PREVIEW_PDF = "preview.pdf";
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

      JasperReport jasperReport = JasperCompileManager.compileReport(
          ResourceUtils.getFile(A5_BILL).getAbsolutePath());

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRMapArrayDataSource(new Object[]{new HashMap<String, Object>()}));

      JasperExportManager.exportReportToPdfFile(jasperPrint, PREVIEW_PDF);
//      exporter = new JRPdfExporter();
//
//      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//      exporter.setExporterOutput(
//          new SimpleOutputStreamExporterOutput(PREVIEW_PDF));
//
//      SimplePdfReportConfiguration reportConfig
//          = new SimplePdfReportConfiguration();
//      reportConfig.setSizePageToContent(Boolean.TRUE);
//      reportConfig.setForceLineBreakPolicy(Boolean.FALSE);
//
//      SimplePdfExporterConfiguration exportConfig
//          = new SimplePdfExporterConfiguration();
//      exportConfig.setEncrypted(Boolean.TRUE);
//      exportConfig.setAllowedPermissionsHint(PdfPermissionsEnum.ALL.getName());
//
//      exporter.setConfiguration(exportConfig);
//      exporter.setConfiguration(reportConfig);
//      exporter.exportReport();
      if (printService.isPresent()) {
        PrintService printer = printService.get();
        printFilePDF(printer, PREVIEW_PDF);
      }

    } catch (JRException | PrintException | IOException e) {
      e.printStackTrace();
    }
  }

  private void printFilePDF(PrintService printService, String filePath)
      throws PrintException, IOException {
    FileInputStream fis = new FileInputStream(filePath);
    Doc pdfDoc = new SimpleDoc(fis, INPUT_STREAM.AUTOSENSE, null);
    DocPrintJob printJob = printService.createPrintJob();
    printJob.print(pdfDoc, new HashPrintRequestAttributeSet());
    fis.close();
  }

}
