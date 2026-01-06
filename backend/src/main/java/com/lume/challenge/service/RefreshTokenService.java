package com.lume.challenge.service;

import com.lume.challenge.domain.RefreshToken;
import com.lume.challenge.domain.User;
import com.lume.challenge.exception.ApiException;
import com.lume.challenge.repo.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final long refreshTokenDays;
  private final SecureRandom random = new SecureRandom();

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      @Value("${app.security.refresh-token-days}") long refreshTokenDays
  ) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.refreshTokenDays = refreshTokenDays;
  }

  public IssuedRefreshToken issue(User user) {
    String token = generateToken();
    Instant now = Instant.now();

    RefreshToken rt = RefreshToken.builder()
        .user(user)
        .token(token)
        .createdAt(now)
        .expiresAt(now.plus(refreshTokenDays, ChronoUnit.DAYS))
        .build();

    refreshTokenRepository.save(rt);

    return new IssuedRefreshToken(token);
  }

  public RotatedToken rotate(String currentToken) {
    RefreshToken rt = refreshTokenRepository.findByToken(currentToken)
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"));

    if (rt.getRevokedAt() != null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token revogado");
    }
    if (rt.getExpiresAt().isBefore(Instant.now())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expirado");
    }

    rt.setRevokedAt(Instant.now());
    refreshTokenRepository.save(rt);

    String newToken = generateToken();
    Instant now = Instant.now();

    RefreshToken newRt = RefreshToken.builder()
        .user(rt.getUser())
        .token(newToken)
        .createdAt(now)
        .expiresAt(now.plus(refreshTokenDays, ChronoUnit.DAYS))
        .build();
    refreshTokenRepository.save(newRt);

    return new RotatedToken(
        rt.getUser().getEmail(),
        rt.getUser().getRole().name(),
        newToken
    );
  }

  public void revoke(String token) {
    RefreshToken rt = refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token não encontrado"));

    if (rt.getRevokedAt() == null) {
      rt.setRevokedAt(Instant.now());
      refreshTokenRepository.save(rt);
    }
  }

  private String generateToken() {
    byte[] bytes = new byte[48];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public record IssuedRefreshToken(String token) {}
  public record RotatedToken(String userEmail, String role, String newRefreshToken) {}
}
