package com.lume.challenge.integration;

import com.lume.challenge.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepClient {

  private final RestClient restClient;

  public ViaCepClient(RestClient.Builder builder) {
    this.restClient = builder.baseUrl("https://viacep.com.br").build();
  }

  public ViaCepAddress lookup(String cepDigits) {
    ViaCepRaw raw = restClient.get()
        .uri("/ws/{cep}/json/", cepDigits)
        .retrieve()
        .body(ViaCepRaw.class);

    if (raw == null || Boolean.TRUE.equals(raw.erro())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "CEP inválido");
    }

    return new ViaCepAddress(
        digitsOnly(raw.cep()),
        nvl(raw.logradouro()),
        nvl(raw.bairro()),
        nvl(raw.localidade()),
        nvl(raw.uf())
    );
  }

  private String nvl(String s) {
    return (s == null) ? "" : s.trim();
  }

  private String digitsOnly(String s) {
    return (s == null) ? "" : s.replaceAll("\\D", "");
  }

  public record ViaCepAddress(String cep, String logradouro, String bairro, String cidade, String uf) {}
  public record ViaCepRaw(String cep, String logradouro, String bairro, String localidade, String uf, Boolean erro) {}
}
