package pe.com.creditya.api.dtos;

import java.time.Instant;

public record ErrorResponseDto(String code,
                               String message,
                               Instant timestamp, String path) {
}