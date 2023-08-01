package org.example.service;

import org.example.dao.BookRepository;
import org.example.model.Book;
import org.example.model.Person;
import org.example.validation.BookValidator;

import java.util.*;

public class Library {

    private final BookRepository dao = new BookRepository();

    private final BookValidator bookValidator = new BookValidator();


    public Book takeBook(Person person, String title) {
        assert person != null : "Person is null.";

        Optional<Book> bookToTake = dao.takeBook(title);

        bookValidator.checkForAvailability(bookToTake, title);
        Book book = bookToTake.get();
        bookValidator.checkForAgeLimit(book, person);

        return book;
    }

    public void returnBook(Person returnee, Book book) {
        bookValidator.checkForNullBook(book);
        bookValidator.checkForBookExistence(dao.hasBook(book), book);
//        validation.checkForCorrectReturnPerson(dao.getReturnee(book), returnee);

        dao.returnBook(book);
    }

}
