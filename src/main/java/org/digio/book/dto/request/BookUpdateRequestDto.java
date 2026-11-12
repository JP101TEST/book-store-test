package org.digio.book.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BookUpdateRequestDto(
        @NotNull @Positive Long id,
        @Size(max = 200) String title,
        @Size(max = 150) String author,
        @Size(max = 20) String isbn
) {
}
