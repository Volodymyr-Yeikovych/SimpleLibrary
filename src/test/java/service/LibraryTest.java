package service;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.InvalidPersonAgeException;
import org.example.model.Book;
import org.example.model.Person;
import org.example.repository.BookRepository;
import org.example.repository.BookRepositoryImpl;
import org.example.service.Library;
import org.example.validation.BookValidator;
import org.example.validation.BookValidatorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
@RunWith(JUnitParamsRunner.class)
public class LibraryTest {

    private Library library;

    private BookRepository bookRepository;
    private BookValidator bookValidator;

    @Before
    public void setup() {
        this.bookRepository = mock(BookRepositoryImpl.class, "repMock");
        this.bookValidator = mock(BookValidatorImpl.class, "valMock");
        this.library = new Library(bookRepository, bookValidator);
    }

    private Object[] getValidPersonAndBook() {
        return new Object[]{
                new Object[]{new Person("Whatever1", 29, true), "Some Title", new Book("Some Title", new Person("Whatever", 29, true), 18)},
                new Object[]{new Person("Whatever2", 25, true), "What About you", new Book("What About you", new Person("Whatever", 29, true), 12)},
                new Object[]{new Person("Whatever3", 20, true), "The other title", new Book("The other title", new Person("Whatever", 29, true), 6)}
        };
    }

    private Object[] getAdultBooksForMinors() {
        return new Object[]{
                new Object[]{new Person("Minor1", 5, true), "Some Title", new Book("Some Title", new Person("Whatever", 29, true), 18)},
                new Object[]{new Person("Minor2", 6, true), "What About you", new Book("What About you", new Person("Whatever", 29, true), 18)},
                new Object[]{new Person("Minor3", 7, true), "The other title", new Book("The other title", new Person("Whatever", 29, true), 18)}
        };
    }

    private Object[] getNullPersonsAndValidTitles() {
        return new Object[] {
                new Object[]{null, "Some Title"},
                new Object[]{null, "What About you"},
                new Object[]{null, "The other title"}
        };
    }

    @Test
    @Parameters(method = "getValidPersonAndBook")
    public void shouldTakeBookFromLibrary(Person person, String title, Book book) {
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));

        Book takenBook = library.takeBook(person, title);
        boolean actual = takenBook.equals(book);

        assertThat(actual).isTrue();
    }

    @Test
    @Parameters(method = "getAdultBooksForMinors")
    public void shouldThrowInvalidPersonAgeExceptionIfPersonIsTooYoungForTheBook(Person person, String title, Book book) {
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));
        doThrow(InvalidPersonAgeException.class).when(bookValidator).validateTake(book, person);

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(InvalidPersonAgeException.class);
    }

    @Test
    @Parameters(method = "getValidPersonAndBook")
    public void shouldThrowBookNotFoundExceptionIfBookWasNotFoundInLibrary(Person person, String title, Book book) {
        when(bookRepository.takeBook(title)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @Parameters(method = "getNullPersonsAndValidTitles")
    public void shouldThrowAssertionErrorIfPersonIsNull(Person person, String title) {
        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(AssertionError.class);
    }


}
