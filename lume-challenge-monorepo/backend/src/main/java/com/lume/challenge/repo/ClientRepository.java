package com.lume.challenge.repo;

import com.lume.challenge.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
  List<Client> findByNameContainingIgnoreCaseOrCpfContaining(String namePart, String cpfPart);
  boolean existsByCpf(String cpf);
}
