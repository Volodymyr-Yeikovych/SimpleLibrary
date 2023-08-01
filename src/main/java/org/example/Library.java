package org.example;

import java.util.*;

public class Library {

    private final List<Book> bookList = new ArrayList<>();
    private final Map<Book, Person> takenBooks = new HashMap<>();
    public Library() {
        generateBooks();
    }

    private void generateBooks() {
        bookList.add(new Book("7 Good Questions", new Person("Alex Fooks", 45, true), 6));
        bookList.add(new Book("Im Just Ken", new Person("Ryan Reynolds", 40, true), 12));
        bookList.add(new Book("10 Reasons for Feminism", new Person("Klara Schitter", 39, false), 18));
        bookList.add(new Book("Whispers of Eternity", new Person("Emily Davidson", 32, false), 6));
        bookList.add(new Book("The Enigma Chronicles", new Person("Benjamin Knight", 45, true), 18));
        bookList.add(new Book("Shadows of the Forgotten", new Person("Victoria Greene", 28, false), 21));
    }

    public Optional<Book> takeBook(Person person, String title) {
        Optional<Book> bookToTake = getBookFromShelf(title);
        if (bookToTake.isEmpty()) {
            System.out.println("Book you've specified doesn't exist yet.");
            return bookToTake;
        }
        Book book = bookToTake.get();
        if (book.getAgeLimit() > person.getAge()) {
            System.out.println("This person is to young for this shit.");
            return bookToTake;
        }
        takenBooks.put(bookToTake.get(), person);
        return bookToTake;
    }

    private Optional<Book> getBookFromShelf(String title) {
        return bookList.stream().filter(book -> book.getTitle().equals(title)).findFirst();
    }

    public void returnBook(Person person, Book book) {
        if (!takenBooks.containsKey(book)) {
            System.out.println("No such book to return.");
            return;
        }
        if (!takenBooks.get(book).equals(person)) {
            System.out.println("Book was taken by a different person. Impossible to return.");
            return;
        }
        takenBooks.remove(book);
    }

}
