package com.lume.challenge.api.dto;

import com.lume.challenge.validation.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClientDtos {

  public record ClientRequest(
      @NotBlank @Size(max = 120) String name,
      @NotBlank @Cpf String cpf,
      @NotBlank @Pattern(regexp = "\\d{8}") String cep,
      String logradouro,
      String bairro,
      String cidade,
      @Size(min = 2, max = 2) String uf,
      @NotBlank @Size(max = 20) String numero,
      String complemento
  ) {}

  public record ClientResponse(
      Long id,
      String name,
      String cpf,
      String cep,
      String logradouro,
      String bairro,
      String cidade,
      String uf,
      String numero,
      String complemento
  ) {}
}
