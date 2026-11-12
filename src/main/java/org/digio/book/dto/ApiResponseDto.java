package org.digio.book.dto;

public record ApiResponseDto(int code, Object data, String message) {
}
