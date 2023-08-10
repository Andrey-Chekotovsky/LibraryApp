package net.chekotovsky.LibraryApp.Controllers;

import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Dao.UserDao;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private UserDao userDao;
    @PostMapping("/{id}")
    public Mono<User> getUser(@PathVariable("id") long id)
    {
        return userDao.selectById(id);
    }
}
