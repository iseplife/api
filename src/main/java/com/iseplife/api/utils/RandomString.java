package com.iseplife.api.utils;

import java.security.SecureRandom;

public class RandomString {
  private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generate(int length) {
    char[] chars = new char[length];
    for (int i = 0; i < length; i++)
      chars[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
    return new String(chars);
  }
}
