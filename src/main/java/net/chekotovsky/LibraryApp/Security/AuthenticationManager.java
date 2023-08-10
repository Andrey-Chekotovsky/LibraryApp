package net.chekotovsky.LibraryApp.Security;

import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Dao.UserDao;
import net.chekotovsky.LibraryApp.Exceptions.UnauthorisedException;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final UserDao userDao;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userDao.selectById(principal.getId())
                .filter(User::isEnabled)
                .switchIfEmpty(Mono.error(new UnauthorisedException("User disabled")))
                .map(user -> authentication);
    }
}
