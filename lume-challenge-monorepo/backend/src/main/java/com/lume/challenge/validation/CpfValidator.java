package com.lume.challenge.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return false;
    String cpf = value.replaceAll("\\D", "");
    if (cpf.length() != 11) return false;
    if (cpf.chars().distinct().count() == 1) return false;

    int d1 = calcDigit(cpf, 9);
    int d2 = calcDigit(cpf, 10);

    return cpf.charAt(9) - '0' == d1 && cpf.charAt(10) - '0' == d2;
  }

  private int calcDigit(String cpf, int length) {
    int sum = 0;
    int weight = length + 1;
    for (int i = 0; i < length; i++) {
      int num = cpf.charAt(i) - '0';
      sum += num * (weight--);
    }
    int mod = sum % 11;
    return (mod < 2) ? 0 : (11 - mod);
  }
}
