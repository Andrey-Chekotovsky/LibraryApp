package net.chekotovsky.LibraryApp.DaoTests;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import net.chekotovsky.LibraryApp.Dao.AuthorDao;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Author;
import org.junit.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

public class AuthorDaoTest {
    private AuthorDao authorDao = new AuthorDao(
            new R2dbcEntityTemplate(ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                    .option(ConnectionFactoryOptions.HOST, "localhost")
                    .option(ConnectionFactoryOptions.PORT, 5432)
                    .option(ConnectionFactoryOptions.DATABASE, "library_app")
                    .option(ConnectionFactoryOptions.USER, "postgres")
                    .option(ConnectionFactoryOptions.PASSWORD, "Trazyn")
                    .build())));

    private Author[] authors = {
            new Author().toBuilder().fullName("Alexander Duma").description("Some description").build(),
            new Author().toBuilder().fullName("Stanislav Lem").description("Another description").build(),
            new Author().toBuilder().fullName("Emilio Salgari").description("Absolutely unique description").build()
    };

    @Test
    public void insert() {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[1]))
                        .then(authorDao.selectByFullName(authors[1].getFullName())))
                .expectNext(authors[1])
                .verifyComplete();
    }
    @Test
    public void insertWithDuplicateUsername() {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[2]))
                        .then(authorDao.insert(authors[2])))
                .expectError(DuplicateException.class)
                .verify();
    }
    @Test
    public void deleteAll()
    {
        StepVerifier.create( authorDao.deleteAll()
                        .then(authorDao.insert(authors[1]))
                        .then(authorDao.deleteAll()))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void selectByName()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .then(authorDao.selectByFullName(authors[0].getFullName())))
                .expectNext(authors[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongName()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .then(authorDao.selectByFullName(authors[1].getFullName())))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void selectById()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .flatMap(author -> authorDao.selectById(author.getId())))
                .expectNext(authors[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongId()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .flatMap(author -> authorDao.selectById(author.getId() + 1)))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void delete()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .flatMap(author -> authorDao.delete(author.getId())))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void update()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[0]))
                        .flatMap(author -> authorDao.update(authors[1], author.getId()))
                        .then(authorDao.selectByFullName(authors[1].getFullName())))
                .expectNext(authors[1])
                .verifyComplete();
    }
    @Test
    public void updateWithDuplicateName()
    {
        StepVerifier
                .create(authorDao.deleteAll()
                        .then(authorDao.insert(authors[1]))
                        .then(authorDao.insert(authors[0]))
                        .flatMap(author -> authorDao.update(authors[1], author.getId())))
                .expectError(DuplicateException.class)
                .verify();
    }
}
