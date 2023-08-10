package net.chekotovsky.LibraryApp.DaoTests;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import net.chekotovsky.LibraryApp.Dao.GenreDao;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateException;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateUsernameException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Genre;
import net.chekotovsky.LibraryApp.models.Role;
import net.chekotovsky.LibraryApp.models.User;
import org.junit.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

public class GenreDaoTest {
    private GenreDao genreDao = new GenreDao(
            new R2dbcEntityTemplate(ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                    .option(ConnectionFactoryOptions.HOST, "localhost")
                    .option(ConnectionFactoryOptions.PORT, 5432)
                    .option(ConnectionFactoryOptions.DATABASE, "library_app")
                    .option(ConnectionFactoryOptions.USER, "postgres")
                    .option(ConnectionFactoryOptions.PASSWORD, "Trazyn")
                    .build())));

    private Genre[] genres = {
            new Genre().toBuilder().name("Fantasy").description("Some description").build(),
            new Genre().toBuilder().name("Science fiction").description("Another description").build(),
            new Genre().toBuilder().name("Detective").description("Absolutely unique description").build()
    };

    @Test
    public void insert() {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[1]))
                )
                .expectNext(genres[1])
                .verifyComplete();
    }
    @Test
    public void insertWithDuplicateUsername() {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[2]))
                        .then(genreDao.insert(genres[2])))
                .expectError(DuplicateException.class)
                .verify();
    }
    @Test
    public void deleteAll()
    {
        StepVerifier.create( genreDao.deleteAll()
                        .then(genreDao.insert(genres[1]))
                        .then(genreDao.deleteAll()))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void selectByName()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .then(genreDao.selectByName(genres[0].getName())))
                .expectNext(genres[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongName()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .then(genreDao.selectByName(genres[1].getName())))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void selectById()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .flatMap(genre -> genreDao.selectById(genre.getId())))
                .expectNext(genres[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongId()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .flatMap(genre -> genreDao.selectById(genre.getId() + 1)))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void delete()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .flatMap(genre -> genreDao.delete(genre.getId())))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void update()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[0]))
                        .flatMap(genre -> genreDao.update(genres[1], genre.getId()))
                )
                .expectNext(genres[1])
                .verifyComplete();
    }
    @Test
    public void updateWithDuplicateName()
    {
        StepVerifier
                .create(genreDao.deleteAll()
                        .then(genreDao.insert(genres[1]))
                        .then(genreDao.insert(genres[0]))
                        .flatMap(genre -> genreDao.update(genres[1], genre.getId())))
                .expectError(DuplicateException.class)
                .verify();
    }
}
