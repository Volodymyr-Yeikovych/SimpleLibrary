package org.example.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.example.exceptions.*;
import org.example.repository.BookRepository;
import org.example.model.Book;
import org.example.model.Person;
import org.example.validation.BookValidator;

import java.util.*;

public class Library {

    private final BookRepository bookRepository;
    private final BookValidator bookValidator;
    private final Multimap<Person, Book> lendLease = HashMultimap.create();

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

            if (lendLease.containsEntry(person, book)) {
                throw new PersonAlreadyLeasingBookException("Person{" + person.getName()
                        + "} already owes library a book{" + book.getTitle() + "}.");
            }

            lendLease.put(person, book);

            return book;
        } catch (InvalidPersonAgeException ex) {
            bookRepository.returnBook(book);
            throw ex;
        }
    }

    public void returnBook(Person returnee, Book book) {
        assert book != null : "Given book was null.";
        assert returnee != null : "Returnee was null.";

        if (!bookRepository.hasBook(book)) {
            throw new InvalidBookException("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
        }

        if (!lendLease.containsKey(returnee) || !lendLease.get(returnee).contains(book)) {
            throw new InvalidReturnPersonException("Returnee{" + returnee.getName() + "} was not found in lease list.");
        }

        bookValidator.validateReturn();
        lendLease.remove(returnee, book);

        bookRepository.returnBook(book);
    }

    public void donateBook(Book book) {
        bookRepository.donateBook(book);
    }

    public boolean hasLeaser(Person person) {
        return lendLease.containsKey(person);
    }
}
