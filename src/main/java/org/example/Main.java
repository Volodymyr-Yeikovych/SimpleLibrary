package org.example;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Library lib = new Library();
        Person vasa = new Person("Vasa", 35, true);
        Person alex = new Person("Alex", 45, true);

        System.out.println("Attempted to take.");
        Optional<Book> takenBook = lib.takeBook(vasa, "7 Good Questions"); // book taken test

        lib.returnBook(alex, takenBook.get()); // wrong person cannot return test
        lib.returnBook(vasa, takenBook.get()); // book return test
        System.out.println("Attempted to return.");

        lib.returnBook(vasa, takenBook.get()); // person cannot return twice test

        Optional<Book> anotherOne = lib.takeBook(vasa, "9 Good Questions"); // non-existing book take test
        lib.returnBook(vasa, anotherOne.orElse(new Book("123", alex, 6))); // non-existing book return test

        Person youngVasa = new Person("Vasa", 17, true);
        Optional<Book> youngVasaChoice = lib.takeBook(youngVasa, "10 Reasons for Feminism"); // young person learning feminism test

    }
}