package org.digio.book.service;

import lombok.RequiredArgsConstructor;
import org.digio.book.dto.ApiResponseDto;
import org.digio.book.dto.request.BookIdRequestDto;
import org.digio.book.dto.request.BookPageRequestDto;
import org.digio.book.dto.request.BookRequestDto;
import org.digio.book.dto.request.BookUpdateRequestDto;
import org.digio.book.dto.response.BookResponseDto;
import org.digio.book.entity.Book;
import org.digio.book.repository.BookRepository;
import org.digio.book.validate.BookValidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

import static org.digio.book.util.CheckUtil.hasValue;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true, timeout = 5)
    public ResponseEntity<ApiResponseDto> getSearch(BookPageRequestDto request) {
        BookValidate.validateBookSearch(request);
        Page<BookResponseDto> books = bookRepository.searchBooks(
                normalizeFilter(request.getTitle()),
                normalizeFilter(request.getAuthor()),
                normalizeFilter(request.getIsbn()),
                PageRequest.of(request.getPage(), request.getSize())
        ).map(this::toResponse);
        return ResponseEntity.ok(new ApiResponseDto(200, books, "Books retrieved successfully"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseDto> getBookDetail(BookIdRequestDto request) {
        BookValidate.validateBookDetail(request, bookRepository);
        Optional<Book> book = bookRepository.findById(request.id());
        BookResponseDto bookResponse = toResponse(book.get());
        return ResponseEntity.ok(new ApiResponseDto(200, bookResponse, "Book retrieved successfully"));
    }

    public ResponseEntity<ApiResponseDto> createBook(BookRequestDto request) {
        BookValidate.validateBookCreate(request, bookRepository);
        Book book = new Book(request.getTitle(), request.getAuthor(), request.getIsbn());
        BookResponseDto created = toResponse(bookRepository.save(book));
        return ResponseEntity.created(URI.create("/api/books/" + created.getId()))
                .body(new ApiResponseDto(201, created, "Book created successfully"));
    }

    public ResponseEntity<ApiResponseDto> updateBook(BookUpdateRequestDto request) {
        BookValidate.validateBookUpdate(request, bookRepository);
        Optional<Book> book = bookRepository.findById(request.id());
        updateBook(request, book.get());
        BookResponseDto updated = toResponse(bookRepository.save(book.get()));
        return ResponseEntity.ok(new ApiResponseDto(200, updated, "Book updated successfully"));
    }


    public ResponseEntity<ApiResponseDto> deleteBook(BookIdRequestDto request) {
        BookValidate.validateBookDelete(request, bookRepository);
        Optional<Book> book = bookRepository.findById(request.id());
        bookRepository.delete(book.get());
        return ResponseEntity.ok(new ApiResponseDto(200, null, "Book deleted successfully"));
    }

    private void updateBook(BookUpdateRequestDto request, Book book) {
        if (hasValue(request.title())) {
            book.setTitle(request.title());
        }
        if (hasValue(request.author())) {
            book.setAuthor(request.author());
        }
        if (hasValue(request.isbn())) {
            book.setIsbn(request.isbn());
        }
    }

    private String normalizeFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }
        return filter.trim();
    }

    private BookResponseDto toResponse(Book book) {
        return new BookResponseDto(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn());
    }
}
