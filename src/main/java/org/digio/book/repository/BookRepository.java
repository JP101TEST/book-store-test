package org.digio.book.repository;

import jakarta.persistence.QueryHint;
import org.digio.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Query(
            value = "SELECT book FROM Book book " +
                    "WHERE (:title IS NULL OR LOWER(book.title) LIKE LOWER(CONCAT(:title, '%'))) " +
                    "AND (:author IS NULL OR LOWER(book.author) LIKE LOWER(CONCAT(:author, '%'))) " +
                    "AND (:isbn IS NULL OR LOWER(book.isbn) LIKE LOWER(CONCAT(:isbn, '%')))",
            countQuery = "SELECT COUNT(book) FROM Book book " +
                    "WHERE (:title IS NULL OR LOWER(book.title) LIKE LOWER(CONCAT(:title, '%'))) " +
                    "AND (:author IS NULL OR LOWER(book.author) LIKE LOWER(CONCAT(:author, '%'))) " +
                    "AND (:isbn IS NULL OR LOWER(book.isbn) LIKE LOWER(CONCAT(:isbn, '%')))"
    )
    @QueryHints(value = @QueryHint(name = "jakarta.persistence.query.timeout", value = "5000"), forCounting = true)
    Page<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("isbn") String isbn,
            Pageable pageable
    );

    @Override
    @Query(value = "SELECT book FROM Book book", countQuery = "SELECT COUNT(book) FROM Book book")
    @QueryHints(value = @QueryHint(name = "jakarta.persistence.query.timeout", value = "5000"), forCounting = true)
    Page<Book> findAll(Pageable pageable);
}
