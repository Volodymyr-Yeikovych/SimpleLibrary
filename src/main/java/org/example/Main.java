package org.example;

public class Main {
    public static void main(String[] args) {
        Library lib = new Library();
        Person vasa = new Person("Vasa", 35, true);
        Person alex = new Person("Alex", 45, true);

        System.out.println("Attempted to take.");
        Book takenBook = lib.takeBook(vasa, "7 Good Questions"); // book taken test +

//        lib.returnBook(alex, takenBook); // wrong person cannot return test -
        lib.returnBook(vasa, takenBook); // book return test +
        System.out.println("Attempted to return.");

//        lib.returnBook(vasa, takenBook); // person cannot return twice test -

//        Book anotherOne = lib.takeBook(vasa, "9 Good Questions"); // non-existing book take test +
//        lib.returnBook(vasa, new Book("123", alex, 6)); // non-existing book return test +

        Person youngVasa = new Person("Vasa", 17, true);
//        Book youngVasaChoice = lib.takeBook(youngVasa, "10 Reasons for Feminism"); // young person learning feminism test +

//        Book newBook = lib.takeBook(null, "7 Good Questions"); // Null person take test +
//        lib.returnBook(null, takenBook); // null person return test -
//        lib.returnBook(vasa, null); // null book return test +
    }
}