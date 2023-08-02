package org.example.repository;


import org.example.model.Book;
import org.example.model.Person;

import java.util.*;

public class BookRepositoryImpl implements BookRepository{

    private final Map<String, BookNumber> bookMap = new HashMap<>();

    public BookRepositoryImpl() {
        generateBooks();
    }

    @Override
    public Optional<Book> takeBook(String title) {

        var bookAndNumber = bookMap.get(title);

        if (bookAndNumber == null || bookAndNumber.getAvailableBooks() == 0) {
            return Optional.empty();
        }

        bookAndNumber.removeBook();
        return Optional.of(bookAndNumber.getBook());
    }

    @Override
    public void returnBook(Book book) {
        var bookAndNumber = bookMap.get(book.getTitle());

        bookAndNumber.addBook();
    }

    @Override
    public boolean hasBook(Book book) {
        return bookMap.containsKey(book.getTitle());
    }

    @Override
    public void donateBook(Book book) {
        if (bookMap.containsKey(book.getTitle())) {
            bookMap.get(book.getTitle()).addBook();
        } else {
            bookMap.put(book.getTitle(), new BookNumber(book, 1));
        }
    }

    private void generateBooks() {
        bookMap.put("7 Good Questions", new BookNumber(new Book("7 Good Questions", new Person("Alex Fooks", 45, true), 6), 2));
        bookMap.put("Im Just Ken", new BookNumber(new Book("Im Just Ken", new Person("Ryan Reynolds", 40, true), 12), 1));
        bookMap.put("10 Reasons for Feminism", new BookNumber(new Book("10 Reasons for Feminism", new Person("Klara Schitter", 39, false), 18), 3));
        bookMap.put("Whispers of Eternity", new BookNumber(new Book("Whispers of Eternity", new Person("Emily Davidson", 32, false), 6), 5));
        bookMap.put("The Enigma Chronicles", new BookNumber(new Book("The Enigma Chronicles", new Person("Benjamin Knight", 45, true), 18), 0));
        bookMap.put("Shadows of the Forgotten", new BookNumber(new Book("Shadows of the Forgotten", new Person("Victoria Greene", 28, false), 21), 1));
    }
}
