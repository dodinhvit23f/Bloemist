package com.utils;

import java.util.Random;

public final class Utils {

  private Utils() {}

  private static final Random RANDOM = new Random();

  public static String genarateOTP(int length) {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = length;


    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
