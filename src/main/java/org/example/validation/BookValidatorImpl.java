package org.example.validation;

import org.example.model.Book;
import org.example.model.Person;
import org.example.exceptions.*;

public class BookValidatorImpl implements BookValidator{

    private void checkForAgeLimit(Book book, Person person) {
        if (person.getAge() < book.getAgeLimit()) {
            throw new InvalidPersonAgeException("Person{" + person.getName() + "}, is too young for Book{" + book.getTitle() + "}");
        }
    }

    public void validateTake(Book book, Person person) {
        checkForAgeLimit(book, person);
    }

    public void validateReturn() {

    }
}
