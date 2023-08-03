package org.example.service;

import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.InvalidBookException;
import org.example.exceptions.InvalidPersonAgeException;
import org.example.exceptions.InvalidReturnPersonException;
import org.example.exceptions.PersonAlreadyLeasingBookException;
import org.example.model.Book;
import org.example.model.Person;
import org.example.repository.BookRepository;
import org.example.repository.BookRepositoryImpl;
import org.example.service.Library;
import org.example.validation.BookValidator;
import org.example.validation.BookValidatorImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LibraryTest {

    private BookRepository bookRepository = mock(BookRepositoryImpl.class);
    private BookValidator bookValidator = mock(BookValidatorImpl.class);
    private Library library = new Library(bookRepository, bookValidator);

    @Test
    public void shouldTakeBookFromLibrary() {
        Person person = mock(Person.class);
        Book book = mock(Book.class);
        String title = "Some Title";
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));

        Book actual = library.takeBook(person, title);

        assertThat(actual).isEqualTo(book);
    }

    @Test
    public void shouldThrowInvalidPersonAgeExceptionIfPersonIsTooYoungForTheBook() {
        Person person = mock(Person.class);
        Book book = mock(Book.class);
        String title = "Some Title";
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));
        doThrow(InvalidPersonAgeException.class).when(bookValidator).validateTake(book, person);

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(InvalidPersonAgeException.class);
    }

    @Test
    public void shouldThrowBookNotFoundExceptionIfBookWasNotFoundInLibrary() {
        Person person = mock(Person.class);
        String title = "Some Title";
        when(bookRepository.takeBook(title)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    public void shouldThrowAssertionErrorIfPersonIsNull() {
        Person person = null;
        String title = "Some title";
        AssertionError ex = Assert.assertThrows(AssertionError.class, () -> library.takeBook(person, title));

        assertThat(ex).hasMessage("Person is null.");
    }

    @Test
    public void shouldThrowPersonAlreadyLeasingBookExceptionWhenTakingSecondBookWithoutReturningFirst() {
        Person person = mock(Person.class);
        String title = "Some Title";
        Book book = mock(Book.class);
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));

        Book firstTakenBook = library.takeBook(person, title);
        PersonAlreadyLeasingBookException ex = Assert.assertThrows(PersonAlreadyLeasingBookException.class, () -> library.takeBook(person, title));

        assertThat(ex)
                .hasMessage("Person{" + person.getName() + "} already owes library a book{" + firstTakenBook.getTitle() + "}.");
    }

    @Test
    public void shouldCallReturnBookOfBookRepositoryWhenPersonAgeIsNotSufficientToLeaseBook() {
        Person person = mock(Person.class);
        String title = "Some Title";
        Book book = mock(Book.class);
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));
        doThrow(InvalidPersonAgeException.class).when(bookValidator).validateTake(book, person);

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(InvalidPersonAgeException.class);
        verify(bookRepository).returnBook(book);
    }

    @Test
    public void shouldThrowAssertionErrorWhenReturneeIsNull() {
        Person returnee = null;
        Book book = mock(Book.class);

        AssertionError ex = Assert.assertThrows(AssertionError.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Returnee was null.");
    }

    @Test
    public void shouldThrowAssertionErrorWhenReturnBookIsNull() {
        Person returnee = mock(Person.class);
        Book book = null;

        AssertionError ex = Assert.assertThrows(AssertionError.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Given book was null.");
    }

    @Test
    public void shouldThrowInvalidBookExceptionWhenNonExistingBookIsReturned() {
        Person returnee = mock(Person.class);
        Book book = mock(Book.class);
        when(bookRepository.bookExists(book)).thenReturn(false);

        InvalidBookException ex = Assert.assertThrows(InvalidBookException.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
    }

    @Test
    public void shouldThrowInvalidReturnPersonExceptionWhenWrongPersonReturnsBook() {
        Person person = mock(Person.class);
        Book book = mock(Book.class);
        library.donateBook(book);
        when(bookRepository.bookExists(book)).thenReturn(true);

        InvalidReturnPersonException ex = Assert.assertThrows(InvalidReturnPersonException.class, () -> library.returnBook(person, book));

        assertThat(ex).hasMessage("Returnee{" + person.getName() + "} was not found in lease list.");
    }

    @Test
    public void shouldThrowInvalidReturnPersonExceptionWhenValidPersonReturnsWrongBook() {
        Book validBook = mock(Book.class);
        Book invalidBook = mock(Book.class);
        Person person = mock(Person.class);
        library.donateBook(validBook);
        library.donateBook(invalidBook);
        when(bookRepository.bookExists(invalidBook)).thenReturn(true);
        when(bookRepository.takeBook(validBook.getTitle())).thenReturn(Optional.of(validBook));

        library.takeBook(person, validBook.getTitle());
        InvalidReturnPersonException ex = Assert.assertThrows(InvalidReturnPersonException.class, () -> library.returnBook(person, invalidBook));

        assertThat(ex).hasMessage("Returnee{" + person.getName() + "} was not found in lease list.");
    }

    @Test
    public void shouldRemovePersonFromLeaseWhenPersonReturnsBook() {
        Book book = mock(Book.class);
        Person person = mock(Person.class);
        library.donateBook(book);
        when(bookRepository.bookExists(book)).thenReturn(true);
        when(bookRepository.takeBook(book.getTitle())).thenReturn(Optional.of(book));

        library.takeBook(person, book.getTitle());
        boolean afterLeasing = library.hasLeaser(person);
        library.returnBook(person, book);
        boolean actual = library.hasLeaser(person);

        verify(bookValidator).validateReturn();
        verify(bookRepository).returnBook(book);
        assertThat(afterLeasing).isTrue();
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldNotRemovePersonFromLeaseWhenPersonHaveNotReturnedAllBooks() {
        Book book1 = mock(Book.class);
        String book1Title = "Some Title";
        when(book1.getTitle()).thenReturn(book1Title);

        Book book2 = mock(Book.class);
        String book2Title = "Other Title";
        when(book2.getTitle()).thenReturn(book2Title);

        library.donateBook(book1);
        library.donateBook(book2);

        when(bookRepository.bookExists(book1)).thenReturn(true);
        when(bookRepository.bookExists(book2)).thenReturn(true);

        when(bookRepository.takeBook(book1.getTitle())).thenReturn(Optional.of(book1));
        when(bookRepository.takeBook(book2.getTitle())).thenReturn(Optional.of(book2));

        Person person = mock(Person.class);

        library.takeBook(person, book1.getTitle());
        library.takeBook(person, book2.getTitle());
        boolean afterLeasing = library.hasLeaser(person);

        library.returnBook(person, book1);
        boolean afterReturningOne = library.hasLeaser(person);

        assertThat(afterLeasing).isTrue();
        assertThat(afterReturningOne).isTrue();

        verify(bookValidator).validateReturn();
        verify(bookRepository).returnBook(book1);
        verify(bookRepository, never()).returnBook(book2);
    }

    @Test
    public void shouldRemovePersonFromLeaseWhenPersonReturnsAllBooks() {
        Book book1 = mock(Book.class);
        String bookTitle1 = "Some Title";
        when(book1.getTitle()).thenReturn(bookTitle1);

        Book book2 = mock(Book.class);
        String bookTitle2 = "Other Title";
        when(book2.getTitle()).thenReturn(bookTitle2);

        library.donateBook(book1);
        library.donateBook(book2);

        when(bookRepository.bookExists(book1)).thenReturn(true);
        when(bookRepository.bookExists(book2)).thenReturn(true);

        when(bookRepository.takeBook(bookTitle1)).thenReturn(Optional.of(book1));
        when(bookRepository.takeBook(bookTitle2)).thenReturn(Optional.of(book2));

        Person person = mock(Person.class);

        library.takeBook(person, book1.getTitle());
        library.takeBook(person, book2.getTitle());
        boolean afterLeasing = library.hasLeaser(person);

        library.returnBook(person, book1);
        library.returnBook(person, book2);
        boolean afterReturning = library.hasLeaser(person);

        assertThat(afterLeasing).isTrue();
        assertThat(afterReturning).isFalse();

        verify(bookValidator, times(2)).validateReturn();
        verify(bookRepository).returnBook(book1);
        verify(bookRepository).returnBook(book2);
    }
}

