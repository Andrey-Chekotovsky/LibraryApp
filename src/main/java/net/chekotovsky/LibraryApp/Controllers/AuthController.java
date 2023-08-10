package net.chekotovsky.LibraryApp.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.chekotovsky.LibraryApp.DTO.LoginRequestDto;
import net.chekotovsky.LibraryApp.DTO.LoginResponseDto;
import net.chekotovsky.LibraryApp.DTO.UserDto;
import net.chekotovsky.LibraryApp.Dao.UserDao;
import net.chekotovsky.LibraryApp.Security.SecurityService;
import net.chekotovsky.LibraryApp.mapper.UserDtoMapper;
import net.chekotovsky.LibraryApp.models.User;
import org.apache.logging.log4j.Level;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final UserDao userDao;
    private final SecurityService securityService;
    private final UserDtoMapper userDtoMapper;
    @GetMapping("/register")
    public String nothing() {
        return "eee";
    }
    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto userDto){
        log.log( Level.INFO,"eee");
        User user = userDtoMapper.map(userDto);
        return userDao.insert(user)
                .map(userDtoMapper::map);
    }
    @PostMapping("/login")
    public Mono<LoginResponseDto> login(@RequestBody LoginRequestDto request)
    {
        return securityService.authenticate(request.getUsername(), request.getPassword())
                .map(tokenDetails -> new LoginResponseDto().builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issueAt(tokenDetails.getIssueDate())
                                .expiresAt(tokenDetails.getExpiresDate())
                                .build()
                );
    }
}
