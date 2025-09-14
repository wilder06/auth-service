package pe.com.creditya.api.dtos;

import lombok.Builder;

@Builder(toBuilder = true)
public record AuthTokenResponse(String token) {
}
