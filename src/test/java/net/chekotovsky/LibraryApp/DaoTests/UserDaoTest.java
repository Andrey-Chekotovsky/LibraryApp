package net.chekotovsky.LibraryApp.DaoTests;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import net.chekotovsky.LibraryApp.Dao.UserDao;
import net.chekotovsky.LibraryApp.Exceptions.DuplicateUsernameException;
import net.chekotovsky.LibraryApp.Exceptions.NotFoundException;
import net.chekotovsky.LibraryApp.models.Role;
import net.chekotovsky.LibraryApp.models.User;
import org.junit.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;


//@RunWith(MockitoJUnitRunner.class)

@TestPropertySource(properties = {
        "connection.host=localhost:5432",
        "connection.database=library_app",
        "connection.username=postgres",
        "connection.password=Trazyn",
})
public class UserDaoTest {


    private UserDao userDao = new UserDao(
            new R2dbcEntityTemplate(ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                    .option(ConnectionFactoryOptions.HOST, "localhost")
                    .option(ConnectionFactoryOptions.PORT, 5432)
                    .option(ConnectionFactoryOptions.DATABASE, "library_app")
                    .option(ConnectionFactoryOptions.USER, "postgres")
                    .option(ConnectionFactoryOptions.PASSWORD, "Trazyn")
                    .build())));

    private User[] users = {
            new User().toBuilder().firstName("Nick").lastName("Gurov").username("Persi")
                    .password("aA@2aa").role(Role.Customer).enabled(true).build(),
            new User().toBuilder().firstName("Nill").lastName("Kiggers").username("Nill")
                    .password("aA@2aa").role(Role.Customer).enabled(true).build(),
            new User().toBuilder().firstName("Adam").lastName("Uolneur").username("Uo")
                    .password("aA@2aa").role(Role.Admin).enabled(true).build()
    };
//    @Before
//    public void clear() {
//        userDao.deleteAll().subscribe();
//    }
    @Test
    public void insert() {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[1])))
                .expectNext(users[1])
                .verifyComplete();
    }
    @Test
    public void insertWithDuplicateUsername() {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[2]))
                        .then(userDao.insert(users[2])))
                .expectError(DuplicateUsernameException.class)
                .verify();
    }
    @Test
    public void deleteAll()
    {
        StepVerifier.create( userDao.deleteAll()
                        .then(userDao.insert(users[1]))
                        .then(userDao.deleteAll()))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void selectByUsername()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .then(userDao.selectByUsername(users[0].getUsername())))
                .expectNext(users[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongUsername()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .then(userDao.selectByUsername(users[1].getUsername())))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void selectById()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .flatMap(user -> userDao.selectById(user.getId())))
                .expectNext(users[0])
                .verifyComplete();
    }
    @Test
    public void selectByWrongId()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .flatMap(user -> userDao.selectById(user.getId() + 1)))
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    public void delete()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .flatMap(user -> userDao.delete(user.getId())))
                .expectNext(Long.valueOf(1))
                .verifyComplete();
    }
    @Test
    public void update()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[0]))
                        .flatMap(user -> userDao.update(users[1], user.getId())))
                .expectNext(users[1])
                .verifyComplete();
    }
    @Test
    public void updateWithDuplicateUsername()
    {
        StepVerifier
                .create(userDao.deleteAll()
                        .then(userDao.insert(users[1]))
                        .then(userDao.insert(users[0]))
                        .flatMap(user -> userDao.update(users[1], user.getId())))
                .expectError(DuplicateUsernameException.class)
                .verify();
    }
}
