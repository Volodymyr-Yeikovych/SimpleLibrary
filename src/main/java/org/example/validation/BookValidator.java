package org.example.validation;

import org.example.model.Book;
import org.example.model.Person;

public interface BookValidator {

    void validateReturn();
    void validateTake(Book book, Person person);
}
