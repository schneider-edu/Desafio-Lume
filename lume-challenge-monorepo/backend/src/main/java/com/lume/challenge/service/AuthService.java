package com.lume.challenge.service;

import com.lume.challenge.domain.Role;
import com.lume.challenge.domain.User;
import com.lume.challenge.exception.ApiException;
import com.lume.challenge.repo.UserRepository;
import com.lume.challenge.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      RefreshTokenService refreshTokenService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
  }

  public User register(String email, String password) {
    if (userRepository.existsByEmail(email)) {
      throw new ApiException(HttpStatus.CONFLICT, "E-mail já cadastrado");
    }
    User user = User.builder()
        .email(email)
        .passwordHash(passwordEncoder.encode(password))
        .role(Role.USER)
        .build();
    return userRepository.save(user);
  }

  public TokenPair login(String email, String password) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

    String access = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
    String refresh = refreshTokenService.issue(user).token();

    return new TokenPair(access, refresh);
  }

  public TokenPair refresh(String refreshToken) {
    var rotated = refreshTokenService.rotate(refreshToken);
    String access = jwtService.generateAccessToken(rotated.userEmail(), rotated.role());
    return new TokenPair(access, rotated.newRefreshToken());
  }

  public void logout(String refreshToken) {
    refreshTokenService.revoke(refreshToken);
  }

  public record TokenPair(String accessToken, String refreshToken) {}
}
