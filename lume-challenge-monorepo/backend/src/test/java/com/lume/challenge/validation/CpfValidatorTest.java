package com.lume.challenge.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

  private final CpfValidator v = new CpfValidator();

  @Test
  void acceptsValidCpf() {
    assertTrue(v.isValid("529.982.247-25", null));
  }

  @Test
  void rejectsInvalidCpf() {
    assertFalse(v.isValid("111.111.111-11", null));
    assertFalse(v.isValid("529.982.247-26", null));
    assertFalse(v.isValid("123", null));
  }
}
