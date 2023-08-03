package org.example.repository;

import org.example.model.Book;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> takeBook(String title);
    void returnBook(Book book);
    boolean bookExists(Book book);

    void donateBook(Book book);
}
