package org.digio.book.service;

import org.digio.book.dto.request.BookPageRequestDto;
import org.digio.book.dto.request.BookRequestDto;
import org.digio.book.dto.request.BookUpdateRequestDto;
import org.digio.book.entity.Book;
import org.digio.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooksSkipsEmptySearchFiltersAndKeepsPagination() {
        BookPageRequestDto request = new BookPageRequestDto();
        request.setPage(2);
        request.setSize(5);
        request.setTitle("   ");
        request.setAuthor(" King ");
        request.setIsbn("");
        when(bookRepository.searchBooks(isNull(), eq("King"), isNull(),
                eq(PageRequest.of(2, 5)))).thenReturn(Page.empty(PageRequest.of(2, 5)));

        bookService.getSearch(request);

        verify(bookRepository).searchBooks(isNull(), eq("King"), isNull(), eq(PageRequest.of(2, 5)));
    }

    @Test
    void getSearchRejectsNegativePageBeforeSearching() {
        BookPageRequestDto request = new BookPageRequestDto();
        request.setPage(-1);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getSearch(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(bookRepository);
    }

    @Test
    void getSearchRejectsNonPositiveSizeBeforeSearching() {
        BookPageRequestDto request = new BookPageRequestDto();
        request.setSize(0);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getSearch(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(bookRepository);
    }

    @Test
    void createBookRejectsDuplicateIsbn() {
        BookRequestDto request = new BookRequestDto();
        request.setTitle("Title");
        request.setAuthor("Author");
        request.setIsbn("123");
        when(bookRepository.existsByIsbn("123")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.createBook(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any(Book.class));
    }

    @Test
    void updateBookRejectsIsbnUsedByAnotherBook() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(1L, "Title", "Author", "123");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book("Old title", "Author", "456")));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.updateBook(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any(Book.class));
    }

    @Test
    void updateBookAllowsKeepingItsCurrentIsbn() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(1L, "New title", "Author", "123");
        Book currentBook = new Book("Old title", "Author", "123");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(currentBook));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
        when(bookRepository.save(currentBook)).thenReturn(currentBook);

        bookService.updateBook(request);

        verify(bookRepository).save(currentBook);
    }

    @Test
    void updateBookChangesOnlyNonBlankFields() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(1L, "New title", "   ", null);
        Book currentBook = new Book("Old title", "Author", "123");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(currentBook));
        when(bookRepository.save(currentBook)).thenReturn(currentBook);

        bookService.updateBook(request);

        assertEquals("New title", currentBook.getTitle());
        assertEquals("Author", currentBook.getAuthor());
        assertEquals("123", currentBook.getIsbn());
        verify(bookRepository, never()).existsByIsbnAndIdNot(org.mockito.ArgumentMatchers.anyString(), eq(1L));
    }

    @Test
    void updateBookLeavesFieldsUnchangedWhenValuesAreEmpty() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(1L, null, "", "  ");
        Book currentBook = new Book("Title", "Author", "123");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(currentBook));
        when(bookRepository.save(currentBook)).thenReturn(currentBook);

        bookService.updateBook(request);

        assertEquals("Title", currentBook.getTitle());
        assertEquals("Author", currentBook.getAuthor());
        assertEquals("123", currentBook.getIsbn());
        verify(bookRepository, never()).existsByIsbnAndIdNot(org.mockito.ArgumentMatchers.anyString(), eq(1L));
    }
}
