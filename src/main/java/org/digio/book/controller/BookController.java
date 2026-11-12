package org.digio.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.digio.book.dto.ApiResponseDto;
import org.digio.book.dto.request.BookIdRequestDto;
import org.digio.book.dto.request.BookPageRequestDto;
import org.digio.book.dto.request.BookRequestDto;
import org.digio.book.dto.request.BookUpdateRequestDto;
import org.digio.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponseDto> getSearch(@Valid @RequestBody BookPageRequestDto request) {
        return bookService.getSearch(request);
    }

    @PostMapping("/detail")
    public ResponseEntity<ApiResponseDto> getBookDetail(@Valid @RequestBody BookIdRequestDto request) {
        return bookService.getBookDetail(request);
    }

    @PostMapping("/insert")
    public ResponseEntity<ApiResponseDto> createBook(@Valid @RequestBody BookRequestDto request) {
        return bookService.createBook(request);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDto> updateBook(@Valid @RequestBody BookUpdateRequestDto request) {
        return bookService.updateBook(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseDto> deleteBook(@Valid @RequestBody BookIdRequestDto request) {
        return bookService.deleteBook(request);
    }
}
