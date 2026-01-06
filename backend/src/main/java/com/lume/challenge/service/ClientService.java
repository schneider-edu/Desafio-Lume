package com.lume.challenge.service;

import com.lume.challenge.api.dto.ClientDtos;
import com.lume.challenge.domain.Client;
import com.lume.challenge.exception.ApiException;
import com.lume.challenge.integration.ViaCepClient;
import com.lume.challenge.repo.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

  private final ClientRepository clientRepository;
  private final ViaCepClient viaCepClient;

  public ClientService(ClientRepository clientRepository, ViaCepClient viaCepClient) {
    this.clientRepository = clientRepository;
    this.viaCepClient = viaCepClient;
  }

  @Transactional
  public ClientDtos.ClientResponse create(ClientDtos.ClientRequest req) {
    String cpf = digitsOnly(req.cpf());
    if (clientRepository.existsByCpf(cpf)) {
      throw new ApiException(HttpStatus.CONFLICT, "CPF já cadastrado");
    }
    Client entity = toEntity(req);
    fillAddressFromCepIfNeeded(entity);
    validateAddressComplete(entity);
    Client saved = clientRepository.save(entity);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public ClientDtos.ClientResponse get(Long id) {
    return clientRepository.findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
  }

  @Transactional(readOnly = true)
  public List<ClientDtos.ClientResponse> list(String cpfQuery, String nameQuery) {
    String cpf = digitsOnly(cpfQuery);
    String name = trim(nameQuery);

    if (isBlank(cpf) && isBlank(name)) {
      return clientRepository.findAll().stream().map(this::toResponse).toList();
    }

    if (!isBlank(cpf) && !isBlank(name)) {
      return clientRepository
          .findByCpfContainingAndNameContainingIgnoreCase(cpf, name)
          .stream()
          .map(this::toResponse)
          .toList();
    }

    if (!isBlank(cpf)) {
      return clientRepository.findByCpfContaining(cpf).stream().map(this::toResponse).toList();
    }

    return clientRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
  }

  @Transactional
  public ClientDtos.ClientResponse update(Long id, ClientDtos.ClientRequest req) {
    Client existing = clientRepository.findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

    String cpf = digitsOnly(req.cpf());
    if (!existing.getCpf().equals(cpf) && clientRepository.existsByCpf(cpf)) {
      throw new ApiException(HttpStatus.CONFLICT, "CPF já cadastrado");
    }

    existing.setName(req.name());
    existing.setCpf(cpf);
    existing.setCep(digitsOnly(req.cep()));
    existing.setNumero(req.numero());
    existing.setComplemento(trim(req.complemento()));
    existing.setLogradouro(trim(req.logradouro()));
    existing.setBairro(trim(req.bairro()));
    existing.setCidade(trim(req.cidade()));
    existing.setUf(trim(req.uf()).toUpperCase());

    fillAddressFromCepIfNeeded(existing);
    validateAddressComplete(existing);

    return toResponse(existing);
  }

  @Transactional
  public void delete(Long id) {
    if (!clientRepository.existsById(id)) {
      throw new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
    }
    clientRepository.deleteById(id);
  }

  private Client toEntity(ClientDtos.ClientRequest req) {
    return Client.builder()
        .name(req.name().trim())
        .cpf(digitsOnly(req.cpf()))
        .cep(digitsOnly(req.cep()))
        .logradouro(trim(req.logradouro()))
        .bairro(trim(req.bairro()))
        .cidade(trim(req.cidade()))
        .uf(trim(req.uf()).toUpperCase())
        .numero(req.numero().trim())
        .complemento(trim(req.complemento()))
        .build();
  }

  private void fillAddressFromCepIfNeeded(Client c) {
    validateCep(c.getCep());

    if (!isBlank(c.getLogradouro()) && !isBlank(c.getBairro()) && !isBlank(c.getCidade()) && !isBlank(c.getUf())) {
      return;
    }

    var addr = viaCepClient.lookup(c.getCep());

    c.setLogradouro(addr.logradouro());
    c.setBairro(addr.bairro());
    c.setCidade(addr.cidade());
    c.setUf(addr.uf().toUpperCase());
  }

  private void validateAddressComplete(Client c) {
    if (isBlank(c.getLogradouro()) || isBlank(c.getBairro()) || isBlank(c.getCidade()) || isBlank(c.getUf())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Endereço incompleto para o CEP informado");
    }
  }

  private ClientDtos.ClientResponse toResponse(Client c) {
    return new ClientDtos.ClientResponse(
        c.getId(),
        c.getName(),
        c.getCpf(),
        c.getCep(),
        c.getLogradouro(),
        c.getBairro(),
        c.getCidade(),
        c.getUf(),
        c.getNumero(),
        c.getComplemento()
    );
  }

  private String digitsOnly(String s) {
    return (s == null) ? "" : s.replaceAll("\\D", "");
  }

  private String trim(String s) {
    return (s == null) ? "" : s.trim();
  }

  private boolean isBlank(String s) {
    return s == null || s.isBlank();
  }

  private void validateCep(String cep) {
    if (cep == null || !cep.matches("\\d{8}")) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "CEP inválido");
    }
  }
}
