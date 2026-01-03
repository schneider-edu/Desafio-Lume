package com.lume.challenge.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

  private final Key key;
  private final long accessTokenMinutes;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.access-token-minutes}") long accessTokenMinutes
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenMinutes = accessTokenMinutes;
  }

  public String generateAccessToken(String subject, String role) {
    Instant now = Instant.now();
    Instant exp = now.plus(accessTokenMinutes, ChronoUnit.MINUTES);

    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .claim("role", role)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);
  }
}
