package org.example.validation;

import org.example.model.Book;
import org.example.model.Person;
import org.example.exceptions.*;

import java.util.Optional;

public class BookValidator {

    private void checkForAgeLimit(Book book, Person person) {
        if (person.getAge() < book.getAgeLimit()) {
            throw new InvalidPersonAgeException("Person{" + person.getName() + "}, is too young for Book{" + book.getTitle() + "}");
        }
    }

    public void checkForCorrectReturnPerson(Person expectedReturnee, Person returnee) {
        if (expectedReturnee == null || !expectedReturnee.equals(returnee)) {
            throw new InvalidReturnPersonException("Expected returnee{" + expectedReturnee + "}, Given{" + returnee + "}");
        }
    }


    public void validateTake(Book book, Person person) {
        checkForAgeLimit(book, person);
    }
}
