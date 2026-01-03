package com.lume.challenge.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

  public record RegisterRequest(
      @Email @NotBlank String email,
      @NotBlank @Size(min = 6, max = 100) String password
  ) {}

  public record LoginRequest(
      @Email @NotBlank String email,
      @NotBlank String password
  ) {}

  public record TokenResponse(
      String accessToken,
      String refreshToken
  ) {}

  public record RefreshRequest(
      @NotBlank String refreshToken
  ) {}

  public record LogoutRequest(
      @NotBlank String refreshToken
  ) {}
}
