package com.lume.challenge.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token_token", columnList = "token", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, unique = true, length = 200)
  private String token;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant revokedAt;

  @Column(nullable = false)
  private Instant createdAt;
}
