package com.lume.challenge.api;

import com.lume.challenge.api.dto.AuthDtos;
import com.lume.challenge.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
    authService.register(req.email(), req.password());
  }

  @PostMapping("/login")
  public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    var pair = authService.login(req.email(), req.password());
    return new AuthDtos.TokenResponse(pair.accessToken(), pair.refreshToken());
  }

  @PostMapping("/refresh")
  public AuthDtos.TokenResponse refresh(@Valid @RequestBody AuthDtos.RefreshRequest req) {
    var pair = authService.refresh(req.refreshToken());
    return new AuthDtos.TokenResponse(pair.accessToken(), pair.refreshToken());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody AuthDtos.LogoutRequest req) {
    authService.logout(req.refreshToken());
  }
}
