package org.example;

import org.example.exceptions.*;

import java.util.Optional;

public class BookValidation {
    public void checkForAvailability(Optional<Book> book, String title) {
        if (book.isEmpty()) {
            throw new BookIsNotFoundException("Book {" + title + "} is not available.");
        }
    }

    public void checkForAgeLimit(Book book, Person person) {
        if (person.getAge() < book.getAgeLimit()) {
            throw new InvalidPersonAgeException("Person{" + person.getName() + "}, is too young for Book{" + book.getTitle() + "}");
        }
    }

    public void checkForBookExistence(boolean hasBook, Book book) {
        if (!hasBook) {
            throw new InvalidBookException("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
        }
    }

    public void checkForCorrectReturnPerson(Person expectedReturnee, Person returnee) {
        if (expectedReturnee == null || !expectedReturnee.equals(returnee)) {
            throw new InvalidReturnPersonException("Expected returnee{" + expectedReturnee + "}, Given{" + returnee + "}");
        }
    }

    public void checkForNullBook(Book book) {
        if (book == null) {
            throw new NullParameterException("Given book was null.");
        }
    }

    public void checkForNullPerson(Person person) {
        if (person == null) {
            throw new NullParameterException("Given person was null.");
        }
    }
}
