package com.bloemist;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.junit.jupiter.api.Test;

class BloemistSpringApplicationTests {

  @Test
  public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
      byte[] array = new byte[7]; // length is bounded by 7
      new Random().nextBytes(array);
      String generatedString = new String(array, StandardCharsets.UTF_8);

      System.out.println(generatedString);
  }

}
