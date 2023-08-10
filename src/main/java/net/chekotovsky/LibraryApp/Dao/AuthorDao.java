package net.chekotovsky.LibraryApp.Dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class AuthorDao {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    public static final BiFunction<Row, RowMetadata, Author> MAPPING_FUNCTION = (row, rowMetaData) -> Author.builder()
            .id(row.get("author_id", Long.class))
            .fullName(row.get("full_name", String.class))
            .description(row.get("description", String.class))
            .build();
    public Flux<Author> selectAll()
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM authors").map(MAPPING_FUNCTION).all();
    }
    public Mono<Author> selectById(long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM authors WHERE author_id = :author_id;")
                .bind("author_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("Author not exists",
                        "LIBRARY_AUTHOR_NOT_EXISTS")));
    }
    public Mono<Author> selectByFullName(String fullName)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM authors WHERE full_name = :fullName;")
                .bind("fullName", fullName)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("Author not exists",
                        "LIBRARY_AUTHOR_NOT_EXISTS")));
    }
    public Mono<Author> update(Author author, long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("UPDATE authors SET full_name = :full_name, " +
                        "description = :description WHERE author_id = :author_id " +
                        "RETURNING author_id, full_name, description;")
                .bind("full_name", author.getFullName())
                .bind("description", author.getDescription())
                .bind("author_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class, error -> new DuplicateException(
                        "Author with such full name already exists",
                        "LIBRARY_DUPLICATE_AUTHOR"));
    }
    public Mono<Author> insert(Author author)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("INSERT INTO authors(full_name, description) " +
                        "VALUES (:full_name, :description) " +
                        "RETURNING author_id, full_name, description;")
                .bind("full_name", author.getFullName())
                .bind("description", author.getDescription())
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class, error -> new DuplicateException(
                        "Author with such full name already exists",
                        "LIBRARY_DUPLICATE_AUTHOR"));
    }
    public Mono<Long> delete(long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("DELETE FROM authors WHERE author_id = :author_id")
                .bind("author_id", id)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.just(Long.valueOf(0)))
                .doOnError(error ->
                        Mono.error(new RuntimeException(error.getMessage()))
                );
    }
    public Mono<Long> deleteAll()
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("DELETE FROM authors WHERE author_id > :author_id")
                .bind("author_id", 0)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.just(Long.valueOf(0)))
                .doOnError(error ->
                        Mono.error(new RuntimeException(error.getMessage()))
                );
    }
}
