package org.example;

import java.util.*;

public class Library {

    private final BookRepository dao = new BookRepository();

    private final BookValidation validation = new BookValidation();


    public Book takeBook(Person person, String title) {
        validation.checkForNullPerson(person);

        Optional<Book> bookToTake = dao.takeBook(title);

        validation.checkForAvailability(bookToTake, title);
        Book book = bookToTake.get();
        validation.checkForAgeLimit(book, person);

        return book;
    }

    public void returnBook(Person returnee, Book book) {
        validation.checkForNullBook(book);
        validation.checkForBookExistence(dao.hasBook(book), book);
//        validation.checkForCorrectReturnPerson(dao.getReturnee(book), returnee);

        dao.returnBook(book);
    }

}
