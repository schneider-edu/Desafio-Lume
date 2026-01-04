package com.lume.challenge.repo;

import com.lume.challenge.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
  List<Client> findByNameContainingIgnoreCase(String namePart);

  List<Client> findByCepContaining(String cep);

  List<Client> findByCepContainingAndNameContainingIgnoreCase(String cep, String namePart);

  boolean existsByCpf(String cpf);
}
