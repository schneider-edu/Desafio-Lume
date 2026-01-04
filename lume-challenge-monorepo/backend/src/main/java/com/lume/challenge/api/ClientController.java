package com.lume.challenge.api;

import com.lume.challenge.api.dto.ClientDtos;
import com.lume.challenge.service.ClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Clients")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/clients")
public class ClientController {

  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping
  public List<ClientDtos.ClientResponse> list(
      @RequestParam(required = false) String cep,
      @RequestParam(required = false) String name
  ) {
    return clientService.list(cep, name);
  }

  @GetMapping("/{id}")
  public ClientDtos.ClientResponse get(@PathVariable Long id) {
    return clientService.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientDtos.ClientResponse create(@Valid @RequestBody ClientDtos.ClientRequest req) {
    return clientService.create(req);
  }

  @PutMapping("/{id}")
  public ClientDtos.ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientDtos.ClientRequest req) {
    return clientService.update(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    clientService.delete(id);
  }
}
