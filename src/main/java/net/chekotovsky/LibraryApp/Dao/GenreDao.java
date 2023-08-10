package net.chekotovsky.LibraryApp.Dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class GenreDao {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    public static final BiFunction<Row, RowMetadata, Genre> MAPPING_FUNCTION = (row, rowMetaData) -> Genre.builder()
            .id(row.get("genre_id", Long.class))
            .name(row.get("name", String.class))
            .description(row.get("description", String.class))
            .build();
    public Flux<Genre> selectAll()
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM genres").map(MAPPING_FUNCTION).all();
    }
    public Mono<Genre> selectById(long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM genres WHERE genre_id = :genre_id;")
                .bind("genre_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("Genre with such name not exists",
                        "LIBRARY_GENRE_NOT_EXISTS")));
    }
    public Mono<Genre> selectByName(String name)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("SELECT * FROM genres WHERE name = :name;")
                .bind("name", name)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("Genre with such name not exists",
                        "LIBRARY_GENRE_NOT_EXISTS")));
    }
    public Mono<Genre> update(Genre genre, long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("UPDATE genres SET name = :name, " +
                        "description = :description WHERE genre_id = :genre_id RETURNING genre_id, name, description;")
                .bind("name", genre.getName())
                .bind("description", genre.getDescription())
                .bind("genre_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class, error -> new DuplicateException(
                        "Genre with this name already exists",
                        "LIBRARY_DUPLICATE_GENRE_NAME"));
    }
    public Mono<Genre> insert(Genre genre)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("INSERT INTO genres(name, description) " +
                        "VALUES (:name, :description) RETURNING genre_id, name, description;")
                .bind("name", genre.getName())
                .bind("description", genre.getDescription())
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class, error -> new DuplicateException(
                        "Genre with this name already exists",
                        "LIBRARY_DUPLICATE_GENRE_NAME"));
    }
    public Mono<Long> delete(long id)
    {
        return this.r2dbcEntityTemplate.getDatabaseClient().sql("DELETE FROM genres WHERE genre_id = :genre_id")
                .bind("genre_id", id)
                .fetch()
                .rowsUpdated();
    }
    public Mono<Long> deleteAll()
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("DELETE FROM genres WHERE genre_id > :genre_id;")
                .bind("genre_id", 0)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.just(Long.valueOf(0)))
                .doOnError(error ->
                        Mono.error(new RuntimeException(error.getMessage()))
                );
    }
}
