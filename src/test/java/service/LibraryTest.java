package service;

import org.example.exceptions.*;
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

    @Test
    public void shouldThrowPersonAlreadyLeasingBookExceptionWhenTakingSecondBookWithoutReturningFirst() {
        Person person = new Person("Whatever1", 29, true);
        String title = "Some Title";
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));

        Book firstTakenBook = library.takeBook(person, title);
        PersonAlreadyLeasingBookException ex = Assert.assertThrows(PersonAlreadyLeasingBookException.class, () -> library.takeBook(person, title));

        assertThat(ex)
                .hasMessage("Person{" + person.getName() + "} already owes library a book{" + firstTakenBook.getTitle() + "}.");
    }

    @Test
    public void shouldCallReturnBookOfBookRepositoryWhenPersonAgeIsNotSufficientToLeaseBook() {
        Person person = new Person("Whatever1", 12, true);
        String title = "Some Title";
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        when(bookRepository.takeBook(title)).thenReturn(Optional.of(book));
        doThrow(InvalidPersonAgeException.class).when(bookValidator).validateTake(book, person);

        assertThatThrownBy(() -> library.takeBook(person, title)).isInstanceOf(InvalidPersonAgeException.class);
        verify(bookRepository).returnBook(book);
    }

    @Test
    public void shouldThrowAssertionErrorWhenReturnBookIsNull() {
        Person returnee = new Person("Whatever1", 29, true);
        Book book = null;

        AssertionError ex = Assert.assertThrows(AssertionError.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Given book was null.");
    }

    @Test
    public void shouldThrowAssertionErrorWhenReturneeIsNull() {
        Person returnee = null;
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);

        AssertionError ex = Assert.assertThrows(AssertionError.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Returnee was null.");
    }

    @Test
    public void shouldThrowInvalidBookExceptionWhenNonExistingBookIsReturned() {
        Person returnee = new Person("Whatever1", 29, true);
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        when(bookRepository.hasBook(book)).thenReturn(false);

        InvalidBookException ex = Assert.assertThrows(InvalidBookException.class, () -> library.returnBook(returnee, book));

        assertThat(ex).hasMessage("Book{" + book.getTitle() + "} doesn't exist in a library. Unable to return.");
    }

    @Test
    public void shouldThrowInvalidReturnPersonExceptionWhenWrongPersonReturnsBook() {
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        Person person = new Person("Whatever1", 29, true);
        library.donateBook(book);
        when(bookRepository.hasBook(book)).thenReturn(true);

        InvalidReturnPersonException ex = Assert.assertThrows(InvalidReturnPersonException.class, () -> library.returnBook(person, book));

        assertThat(ex).hasMessage("Returnee{" + person.getName() + "} was not found in lease list.");
    }

    @Test
    public void shouldThrowInvalidReturnPersonExceptionWhenValidPersonReturnsWrongBook() {
        Book validBook = new Book("Some Title", new Person("Whatever", 29, true), 18);
        Book invalidBook = new Book("Whatever Title", new Person("Some", 99, true), 2);
        Person person = new Person("Whatever1", 29, true);
        library.donateBook(validBook);
        library.donateBook(invalidBook);
        when(bookRepository.hasBook(invalidBook)).thenReturn(true);
        when(bookRepository.takeBook(validBook.getTitle())).thenReturn(Optional.of(validBook));

        library.takeBook(person, validBook.getTitle());
        InvalidReturnPersonException ex = Assert.assertThrows(InvalidReturnPersonException.class, () -> library.returnBook(person, invalidBook));

        assertThat(ex).hasMessage("Returnee{" + person.getName() + "} was not found in lease list.");
    }

    @Test
    public void shouldRemovePersonFromLeaseWhenPersonReturnsBook(){
        Book book = new Book("Some Title", new Person("Whatever", 29, true), 18);
        Person person = new Person("Whatever1", 29, true);
        library.donateBook(book);
        when(bookRepository.hasBook(book)).thenReturn(true);
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
        Book book1 = new Book("Some Title1", new Person("Whatever", 29, true), 18);
        Book book2 = new Book("Some Title2", new Person("Whatever", 29, true), 18);
        Person person = new Person("Whatever1", 29, true);
        library.donateBook(book1);
        library.donateBook(book2);
        when(bookRepository.hasBook(book1)).thenReturn(true);
        when(bookRepository.takeBook(book1.getTitle())).thenReturn(Optional.of(book1));
        when(bookRepository.hasBook(book2)).thenReturn(true);
        when(bookRepository.takeBook(book2.getTitle())).thenReturn(Optional.of(book2));

        library.takeBook(person, book1.getTitle());
        library.takeBook(person, book2.getTitle());
        boolean afterLeasing = library.hasLeaser(person);
        library.returnBook(person, book1);
        boolean actual = library.hasLeaser(person);

        verify(bookValidator).validateReturn();
        verify(bookRepository).returnBook(book1);
        verify(bookRepository, never()).returnBook(book2);
        assertThat(afterLeasing).isTrue();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldRemovePersonFromLeaseWhenPersonReturnsAllBooks() {
        Book book1 = new Book("Some Title1", new Person("Whatever", 29, true), 18);
        Book book2 = new Book("Some Title2", new Person("Whatever", 29, true), 18);
        Person person = new Person("Whatever1", 29, true);
        library.donateBook(book1);
        library.donateBook(book2);
        when(bookRepository.hasBook(book1)).thenReturn(true);
        when(bookRepository.takeBook(book1.getTitle())).thenReturn(Optional.of(book1));
        when(bookRepository.hasBook(book2)).thenReturn(true);
        when(bookRepository.takeBook(book2.getTitle())).thenReturn(Optional.of(book2));

        library.takeBook(person, book1.getTitle());
        library.takeBook(person, book2.getTitle());
        boolean afterLeasing = library.hasLeaser(person);
        library.returnBook(person, book1);
        library.returnBook(person, book2);
        boolean actual = library.hasLeaser(person);

        verify(bookValidator, times(2)).validateReturn();
        verify(bookRepository).returnBook(book1);
        verify(bookRepository).returnBook(book2);
        assertThat(afterLeasing).isTrue();
        assertThat(actual).isFalse();
    }
}

