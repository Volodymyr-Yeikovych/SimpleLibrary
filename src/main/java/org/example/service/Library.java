package org.example.service;

import com.google.common.collect.Lists;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.InvalidBookException;
import org.example.exceptions.InvalidPersonAgeException;
import org.example.exceptions.InvalidReturnPersonException;
import org.example.repository.BookRepository;
import org.example.model.Book;
import org.example.model.Person;
import org.example.validation.BookValidator;

import java.util.*;

public class Library {

    private final BookRepository bookRepository;
    private final BookValidator bookValidator;
    private final Map<String, List<Person>> lendLease = new HashMap<>();

    public Library(BookRepository bookRepository, BookValidator bookValidator) {
        this.bookRepository = bookRepository;
        this.bookValidator = bookValidator;
    }

    public Book takeBook(Person person, String title) {
        assert person != null : "Person is null.";

        Optional<Book> bookToTake = bookRepository.takeBook(title);

        if (bookToTake.isEmpty()) {
            throw new BookNotFoundException("Book {" + title + "} is not available.");
        }

        Book book = bookToTake.get();
        try {
            bookValidator.validateTake(book, person);
            if (lendLease.containsKey(book.getTitle())) {
                lendLease.get(book.getTitle()).add(person);
            } else {
                lendLease.put(book.getTitle(), Lists.newArrayList(person));
            }
            return book;
        } catch (InvalidPersonAgeException ex) {
            bookRepository.returnBook(book);
            throw ex;
        }
    }

    public void returnBook(Person returnee, Book book) {
        assert book != null : "Given book was null.";
        assert returnee != null : "Returnee is null.";

        if (!bookRepository.hasBook(book)) {
            throw new InvalidBookException("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
        }


        if (!lendLease.get(book.getTitle()).contains(returnee)) {
            throw new InvalidReturnPersonException("Returnee{" + returnee.getName() + "} was not found in lease list.");
        }

        bookValidator.validateReturn();
        lendLease.get(book.getTitle()).remove(returnee);

        bookRepository.returnBook(book);
    }

    public void donateBook(Book book) {
        bookRepository.donateBook(book);
    }
}
