package com.utils;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import com.constant.Constants;
import com.google.common.hash.Hashing;

public final class Utils {

  private Utils() {}

  private static final Random RANDOM = new Random();

  public static String generateOTP(int length) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'


    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
  
  public static String hashPassword(String password) {
    return Hashing.sha256().hashString(String.join("", password, Constants.SALT), StandardCharsets.UTF_8)
        .toString();
  }
  
  public static String currencyFormat(Double doubleValue) {
    DecimalFormat formatter = new DecimalFormat("#,###");
    return formatter.format(doubleValue);
  }

  public static boolean isNumber(String number){
    return  number.chars().allMatch(Character::isDigit);
  }
  
  public static String currencyToNumber(String currency) {
    return currency.replace(",", "")
        .replace(".", "").strip();
  }

  public static String formatDate(Date date){
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

  public static String formatTime(Date date){
    SimpleDateFormat dt = new SimpleDateFormat("hh:mm");
    return dt.format(date);
  }
}
