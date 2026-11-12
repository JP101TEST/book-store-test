package org.digio.book.validate;

import org.digio.book.dto.request.BookIdRequestDto;
import org.digio.book.dto.request.BookPageRequestDto;
import org.digio.book.dto.request.BookRequestDto;
import org.digio.book.dto.request.BookUpdateRequestDto;
import org.digio.book.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.digio.book.util.CheckUtil.hasValue;

public class BookValidate {

    public static void validateBookSearch(BookPageRequestDto request) {
        if (request.getPage() < 0 || request.getSize() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
        }
    }

    public static void validateBookDetail(BookIdRequestDto request, BookRepository bookRepository) {
        if (!bookRepository.existsById(request.id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

    }

    public static void validateBookCreate(BookRequestDto request, BookRepository bookRepository) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A book with this ISBN already exists");
        }
    }
    public static void validateBookUpdate(BookUpdateRequestDto request, BookRepository bookRepository) {
        if (hasValue(request.isbn())
                && bookRepository.existsByIsbnAndIdNot(request.isbn(), request.id())) {
            throw duplicateIsbn();
        }
    }

    public static void validateBookDelete(BookIdRequestDto request, BookRepository bookRepository) {
        if (!bookRepository.existsById(request.id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

    }

    private static ResponseStatusException duplicateIsbn() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "A book with this ISBN already exists");
    }
}
