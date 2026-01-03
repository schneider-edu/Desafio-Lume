package com.lume.challenge.api;

import com.lume.challenge.api.dto.CepDtos;
import com.lume.challenge.integration.ViaCepClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CEP")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cep")
public class CepController {

  private final ViaCepClient viaCepClient;

  public CepController(ViaCepClient viaCepClient) {
    this.viaCepClient = viaCepClient;
  }

  @GetMapping("/{cep}")
  public CepDtos.CepResponse get(@PathVariable @Pattern(regexp = "\\d{8}") String cep) {
    var addr = viaCepClient.lookup(cep);
    return new CepDtos.CepResponse(addr.cep(), addr.logradouro(), addr.bairro(), addr.cidade(), addr.uf());
  }
}
