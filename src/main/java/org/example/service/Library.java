package org.example.service;

import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.InvalidBookException;
import org.example.repository.BookRepository;
import org.example.model.Book;
import org.example.model.Person;
import org.example.validation.BookValidator;

import java.util.*;

public class Library {

    private final BookRepository bookRepository;

    private final BookValidator bookValidator;

    public Library(BookRepository bookRepository, BookValidator bookValidator) {
        this.bookRepository = bookRepository;
        this.bookValidator= bookValidator;
    }

    public Book takeBook(Person person, String title) {
        assert person != null : "Person is null.";

        Optional<Book> bookToTake = bookRepository.takeBook(title);

        if (bookToTake.isEmpty()) {
            throw new BookNotFoundException("Book {" + title + "} is not available.");
        }

        Book book = bookToTake.get();
        bookValidator.validateTake(book, person);

        return book;
    }

    public void returnBook(Person returnee, Book book) {
        assert book != null : "Given book was null.";

        if (!bookRepository.hasBook(book)) {
            throw new InvalidBookException("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
        }

//        validation.checkForCorrectReturnPerson(dao.getReturnee(book), returnee);

        bookRepository.returnBook(book);
    }

}
