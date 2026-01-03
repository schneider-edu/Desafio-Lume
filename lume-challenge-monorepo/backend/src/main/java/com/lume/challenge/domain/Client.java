package com.lume.challenge.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_client_name", columnList = "name")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Client {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, length = 11, unique = true)
  private String cpf; // somente dígitos

  @Column(nullable = false, length = 8)
  private String cep; // somente dígitos

  @Column(nullable = false, length = 120)
  private String logradouro;

  @Column(nullable = false, length = 80)
  private String bairro;

  @Column(nullable = false, length = 80)
  private String cidade;

  @Column(nullable = false, length = 2)
  private String uf;

  @Column(nullable = false, length = 20)
  private String numero;

  @Column(length = 80)
  private String complemento;
}
