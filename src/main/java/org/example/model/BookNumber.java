package org.example.model;

import org.example.exceptions.NoBooksToRemoveException;

import java.util.Objects;

public class BookNumber {

    private Book book;
    private int availableBooks;

    public BookNumber(Book book, int availableBooks) {
        this.book = book;
        this.availableBooks = availableBooks;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(int availableBooks) {
        this.availableBooks = availableBooks;
    }

    public void removeBook() {
        if (availableBooks == 0) {
            throw new NoBooksToRemoveException("Trying to remove Book{" + book.getTitle() + "}, while availableBooks is 0");
        }
        availableBooks--;
    }

    public void addBook() {
        availableBooks++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookNumber that = (BookNumber) o;
        return availableBooks == that.availableBooks && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, availableBooks);
    }
}
