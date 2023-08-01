package org.example;


import java.util.*;

public class BookRepository {

    private final Map<Book, Integer> bookMap = new HashMap<>();

    public BookRepository() {
        generateBooks();
    }

    public Optional<Book> takeBook(String title) {
        for (Map.Entry<Book, Integer> entry : bookMap.entrySet()) {
            if (entry.getKey().getTitle().equals(title)) {
                int booksLeft = entry.getValue();
                if (booksLeft == 0) {
                    return Optional.empty();
                }
                booksLeft--;
                entry.setValue(booksLeft);
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
    public void returnBook(Book book) {
        for (Map.Entry<Book, Integer> entry : bookMap.entrySet()) {
            if (book.equals(entry.getKey())) {
                int booksLeft = entry.getValue();
                booksLeft++;
                entry.setValue(booksLeft);
            }
        }
    }

    public Person getReturnee(Book book) {
        return null;
    }

    public boolean hasBook(Book book) {
        return bookMap.containsKey(book);
    }

    private void generateBooks() {
        bookMap.put(new Book("7 Good Questions", new Person("Alex Fooks", 45, true), 6), 2);
        bookMap.put(new Book("Im Just Ken", new Person("Ryan Reynolds", 40, true), 12), 1);
        bookMap.put(new Book("10 Reasons for Feminism", new Person("Klara Schitter", 39, false), 18), 3);
        bookMap.put(new Book("Whispers of Eternity", new Person("Emily Davidson", 32, false), 6), 5);
        bookMap.put(new Book("The Enigma Chronicles", new Person("Benjamin Knight", 45, true), 18), 0);
        bookMap.put(new Book("Shadows of the Forgotten", new Person("Victoria Greene", 28, false), 21), 1);
    }
}
