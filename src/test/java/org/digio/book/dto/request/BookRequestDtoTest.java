package org.digio.book.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BookRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMissingOrBlankRequiredFields() {
        BookRequestDto request = new BookRequestDto();

        assertFalse(validator.validate(request).isEmpty());
    }
}
