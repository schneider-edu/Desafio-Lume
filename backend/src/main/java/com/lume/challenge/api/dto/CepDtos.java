package com.lume.challenge.api.dto;

import jakarta.validation.constraints.Pattern;

public class CepDtos {
  public record CepResponse(
      @Pattern(regexp = "\\d{8}") String cep,
      String logradouro,
      String bairro,
      String cidade,
      String uf
  ) {}
}
