package com.bloemist.services;

import java.io.File;
import java.util.Optional;

public interface QrServiceI {
  public Optional<File> generateQRCodeImage(String barcodeText);
}
