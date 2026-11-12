package org.digio.book.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookPageRequestDto {

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 3;

    private String title;
    private String author;
    private String isbn;
}
