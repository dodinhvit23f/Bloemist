package com.bloemist.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bloemist.services.QrServiceI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Component
public class QrService implements QrServiceI {

  @Override
  public Optional<File> generateQRCodeImage(String barcodeText) {

    QRCodeWriter barcodeWriter = new QRCodeWriter();
    try {
      BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 15, 15);
      String outputFile = String.format("./%s.png", UUID.randomUUID());
      Path path = FileSystems.getDefault().getPath(outputFile);
      MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
      return  Optional.of(new File(path.toUri()));
    } catch (WriterException | IOException e) {
      return Optional.empty();
    }
  }
}
