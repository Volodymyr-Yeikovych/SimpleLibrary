package service;

import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.InvalidPersonAgeException;
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

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class LibraryTest {

    private BookRepository bookRepository = mock(BookRepositoryImpl.class);
    private BookValidator bookValidator = mock(BookValidatorImpl.class);
    private Library library = new Library(bookRepository, bookValidator);

    @Test
    public void shouldTakeBookFromLibrary() {
        Person person = new Person("Whatever1", 29, true);
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        String title = "Some Title";
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));

        Book actual = library.takeBook(person, title);

        assertThat(actual).isEqualTo(book);
    }

    @Test
    public void shouldThrowInvalidPersonAgeExceptionIfPersonIsTooYoungForTheBook() {
        Person person = new Person("Whatever1", 29, true);
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        String title = "Some Title";
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));
        doThrow(InvalidPersonAgeException.class).when(bookValidator).validateTake(book, person);

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(InvalidPersonAgeException.class);
    }

    @Test
    public void shouldThrowBookNotFoundExceptionIfBookWasNotFoundInLibrary() {
        Person person = new Person("Whatever1", 29, true);
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
}

