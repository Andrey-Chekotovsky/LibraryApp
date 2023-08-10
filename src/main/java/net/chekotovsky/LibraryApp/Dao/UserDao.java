package net.chekotovsky.LibraryApp.Dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateUsernameException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Role;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class UserDao {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    public static final BiFunction<Row, RowMetadata, User> MAPPING_FUNCTION = (row, rowMetaData) -> User.builder()
            .id(row.get("user_id", Long.class))
            .firstName(row.get("first_name", String.class))
            .lastName(row.get("last_name", String.class))
            .username(row.get("username", String.class))
            .password(row.get("password", String.class))
            .role(Role.valueOf(row.get("role", String.class)))
            .enabled(row.get("enabled", Boolean.class))
            .build();

    public Flux<User> selectAll()
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT * FROM users").map(MAPPING_FUNCTION).all();
    }
    public Mono<User> selectById(long id)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT * FROM users WHERE user_id = :user_id;")
                .bind("user_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("User with such id not exists",
                        "LIBRARY_USER_NOT_EXISTS")));
    }
    public Mono<User> selectByUsername(String username)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT * FROM users WHERE username = :username;")
                .bind("username", username)
                .map(MAPPING_FUNCTION)
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("User with such username not exists",
                        "LIBRARY_USER_NOT_EXISTS")));
    }

    public Mono<User> update(User user, long id)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("UPDATE users SET first_name = :first_name, " +
                        "last_name = :last_name, username = :username, password = :password, " +
                        "role = :role, enabled = :enabled WHERE user_id = :user_id " +
                        "RETURNING user_id, first_name, last_name, username, password, enabled, role;")
                .bind("first_name", user.getFirstName())
                .bind("last_name", user.getLastName())
                .bind("username", user.getUsername())
                .bind("password", user.getPassword())
                .bind("role", user.getRole().toString())
                .bind("enabled", user.isEnabled())
                .bind("user_id", id)
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class,
                        error -> new DuplicateUsernameException("User with such username is already in use"));
    }
    public Mono<User> insert(User user)
    {

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("INSERT INTO users(first_name, last_name, username, password, enabled, role) " +
                        "VALUES (:first_name, :last_name, :username, :password, :enabled, :role) " +
                        "RETURNING user_id, first_name, last_name, username, password, enabled, role;")
                .bind("first_name", user.getFirstName())
                .bind("last_name", user.getLastName())
                .bind("username", user.getUsername())
                .bind("password", user.getPassword())
                .bind("role", user.getRole().toString())
                .bind("enabled", user.isEnabled())
                .map(MAPPING_FUNCTION)
                .one()
                .onErrorMap(DuplicateKeyException.class,
                        error -> new DuplicateUsernameException("User with such username is already in use"));
    }
    public Mono<Long> delete(long id)
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("DELETE FROM users WHERE user_id = :user_id;")
                .bind("user_id", id)
                .fetch()
                .rowsUpdated();
    }
    public Mono<Long> deleteAll()
    {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("DELETE FROM users WHERE user_id > :user_id;")
                .bind("user_id", 0)
                .fetch()
                .rowsUpdated()
                .switchIfEmpty(Mono.just(Long.valueOf(0)))
                .doOnError(error ->
                        Mono.error(new RuntimeException(error.getMessage()))
                );
    }

}
