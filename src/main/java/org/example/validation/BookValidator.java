package org.example.validation;

import org.example.model.Book;
import org.example.model.Person;
import org.example.exceptions.*;

import java.util.Optional;

public class BookValidator {

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


}
