package net.chekotovsky.LibraryApp.Dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Author;
import net.chekotovsky.LibraryApp.models.Book;
import net.chekotovsky.LibraryApp.models.Genre;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import  org.springframework.dao.DataIntegrityViolationException;
import reactor.util.function.Tuple2;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class BookDao {
    private GenreDao genreDao;
    private AuthorDao authorDao;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    public final BiFunction<Row, RowMetadata, Book> MAPPING_FUNCTION = (row, rowMetaData) -> Book.builder()
            .id(row.get("book_id", Long.class))
            .amountInStock(row.get("amount", Integer.class))
            .name(row.get("books.name", String.class))
            .description(row.get("description", String.class))
            .numOfPages(row.get("num_of_pages", Integer.class))
            .yearOfIssue(row.get("year_of_issue", Integer.class))
            .author(new Author().toBuilder()
                    .id(row.get("author_id", Long.class))
                    .fullName(row.get("full_name", String.class))
                    .description(row.get("authors.description", String.class))
                    .build())
            .genre(new Genre().toBuilder()
                    .id(row.get("genre_id", Long.class))
                    .name(row.get("genres.name", String.class))
                    .description(row.get("genres.description", String.class))
                    .build())
            .build();
    public final BiFunction<Row, RowMetadata, Book> MAPPING_FUNCTION_WITH_LAZY_LOAD =
            (row, rowMetaData) -> Book.builder()
            .id(row.get("book_id", Long.class))
            .amountInStock(row.get("amount", Integer.class))
            .name(row.get("name", String.class))
            .description(row.get("description", String.class))
            .numOfPages(row.get("num_of_pages", Integer.class))
            .yearOfIssue(row.get("year_of_issue", Integer.class))
            .author(null)
            .genre(null)
            .build();
    public final BiFunction<Row, RowMetadata, Tuple2<Author, Genre>> MAPPING_FUNCTION_FOR_DETAILS =
            (row, rowMetaData) -> new Tuple2<Author, Genre> (Author.builder().build(),
                    Genre.builder().build());
    private Mono<Boolean> checkBookUnique(Book book)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM books WHERE " +
                "author_id = :author_id AND name = :name")
                .bind("author_id", book.getAuthor().getId())
                .bind("name", book.getName())
                .map(MAPPING_FUNCTION_WITH_LAZY_LOAD)
                .one()
                .map(b -> {
                    if (b == null)
                        return true;
                    else
                        return false;
                });
    }
    public Mono<Book> insert(Book book)
    {
        return checkBookUnique(book).flatMap(check ->
        {
            if (!check)
                return Mono.error(new DuplicateException(
                        "This book is already exists",
                        "LIBRARY_DUPLICATE_BOOK"));
            else return Mono.just(check);
        }).then(
                this.r2dbcEntityTemplate.getDatabaseClient().sql(
                "INSERT INTO books(amount, name, description, num_of_pages, " +
                        "year_of_issue, author_id, genre_id) " +
                        "VALUES (:amount_in_stock, :name, :description, :num_of_pages, " +
                        ":year_of_issue, :author_id, :genre_id) " +
                        "RETURNING book_id, amount, name, description, num_of_pages, " +
                        "year_of_issue;")
                .bind("amount_in_stock", book.getAmountInStock())
                .bind("name", book.getName())
                .bind("description", book.getDescription())
                .bind("num_of_pages", book.getNumOfPages())
                .bind("year_of_issue", book.getYearOfIssue())
                .bind("author_id", book.getAuthor().getId())
                .bind("genre_id", book.getGenre().getId())
                .map(MAPPING_FUNCTION_WITH_LAZY_LOAD)
                .one()
//                .onErrorMap(DuplicateKeyException.class, error -> new DuplicateException(
//                        "",
//                        "LIBRARY_"))
                .onErrorMap(DataIntegrityViolationException.class, error -> new NotFoundException(
                        "Provided author or genre does not exist",
                        "LIBRARY_AUTHOR_OR_GENRE_NOT_EXIST")));
    }public Mono<Book> selectById(long id)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT * FROM books WHERE book_id = :book_id " +
                        "INNER JOIN authors ON books.author_id = authors.author_id" +
                        "INNER JOIN genres ON books.genre_id = genres.genre_id;")
                .bind("book_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("",
                        "LIBRARY_")));
    }
    public Mono<Book> selectByName(String name)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT * FROM books WHERE name = :name;")
                .bind("name", name)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("",
                        "LIBRARY_")));
    }
    public Mono<Book> getBookDetails(Book book)
    {
//        return r2dbcEntityTemplate.getDatabaseClient()
//                .sql("SELECT *" +
//                        "FROM public.books" +
//                        "inner join authors on books.author_id = authors.author_id" +
//                        "inner join genres on books.genre_id = genres.genre_id " +
//                        "WHERE book_id = :book_id;")
//                .bind("book_id", book.getId())
//                .map(MAPPING_FUNCTION);
        return null;
    }
    public Mono<Long> deleteAll()
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("DELETE FROM books WHERE book_id > :book_id;")
                .bind("book_id", 0)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.just(Long.valueOf(0)))
                .doOnError(error ->
                        Mono.error(new RuntimeException(error.getMessage()))
                );
    }

}
