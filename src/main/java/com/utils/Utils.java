package com.utils;

import com.constant.Constants;
import com.google.common.hash.Hashing;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.util.ObjectUtils;

public final class Utils {

  private Utils() {
  }

  private static final Random RANDOM = new Random();

  public static String generateOTP(int length) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  public static String hashPassword(String password, String salt) {
    return Hashing.sha256()
        .hashString(String.join("", password, salt), StandardCharsets.UTF_8)
        .toString();
  }

  public static String currencyFormat(Double doubleValue) {
    DecimalFormat formatter = new DecimalFormat("#,###");
    return formatter.format(doubleValue);
  }

  public static boolean isNumber(String number) {
    return number.chars().filter(character -> character != '.' && character != ',' && character != 'E')
        .allMatch(Character::isDigit);
  }

  public static String currencyToStringNumber(String currency) {
    if (ObjectUtils.isEmpty(currency)) {
      return BigInteger.ZERO.toString();
    }
    return currency.replace(Constants.COMMA, "").strip();
  }

  public static String formatDate(Date date) {
    SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
    return dt.format(date);
  }

  public static Date toDate(String date) {
    SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
    try {
      return dt.parse(date);
    } catch (ParseException e) {
      return new Date();
    }
  }

  public static File openDialogChoiceImage(Stage stage ) {
    FileChooser fc = new FileChooser();
    fc.setTitle("Choose a image");
    FileChooser.ExtensionFilter imageFilter =
        new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
    fc.getExtensionFilters().add(imageFilter);

    File chooseFile = fc.showOpenDialog(stage);
    return chooseFile;
  }
}
